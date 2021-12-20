package io.ipolyzos

import io.ipolyzos.config.AppConfig
import io.ipolyzos.models.{AnalysisEvent, Event}
import io.ipolyzos.wrappers.SparkSessionWrapper
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.OutputMode

import java.sql.Timestamp

object AbandonedCart extends SparkSessionWrapper {

  def main(args: Array[String]): Unit = {
    val configs = List(
      ("spark.sql.shuffle.partitions", "4")
    )

    implicit val spark: SparkSession = initSparkSession("Abandoned Cart Analysis", configs)

    spark.sparkContext.setLogLevel("WARN")

    val clickEventsDF = spark
      .readStream
      .format("pulsar")
      .option("service.url", AppConfig.serviceUrl)
      .option("admin.url", AppConfig.adminUrl)
      .option("topic", AppConfig.topicName)
   // .option("pulsar.client.authPluginClassName","org.apache.pulsar.client.impl.auth.AuthenticationToken")
   // .option("pulsar.client.authParams","token:")
      .load()

    import spark.implicits._
    import io.circe.generic.auto._
    import org.apache.spark.sql.functions._

    val parsedEvents = clickEventsDF
      .selectExpr("CAST(value AS STRING)").as[String]
      .map { line =>
        val event = io.circe.parser.decode[Event](line).getOrElse(Event.emptyEvent())
        AnalysisEvent(event.userSession, event.userid, new Timestamp(event.eventTime), event.eventType)
      }.as[AnalysisEvent]

      val checkoutEvents = parsedEvents
        .withWatermark("eventTime", "30 minutes")
        .groupBy(col("userSession"), col("userId"),  session_window(col("eventTime"), "80 minutes"))
        .agg(collect_list("eventType").`as`("eventTypes"))
        .filter(array_contains(col("eventTypes"),"cart"))
        .select(
          col("session_window.start").`as`("sessionWindowStart"),
          col("session_window.end").`as`("sessionWindowEnd"),
          col("userId"),
          col("userSession"),
          col("eventTypes")
        )

    checkoutEvents
      .writeStream
      .outputMode(OutputMode.Complete())
      .option("truncate", value = false)
      .format("console")
      .option("numRows", 10)
      .start()
      .awaitTermination()
  }
}
