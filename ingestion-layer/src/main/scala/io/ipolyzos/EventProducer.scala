package io.ipolyzos

import com.sksamuel.pulsar4s._
import io.ipolyzos.config.AppConfig
import io.ipolyzos.models.Event
import io.ipolyzos.utils.FileUtils

import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object EventProducer extends FileUtils {
  private val executorsService: ExecutorService = Executors.newFixedThreadPool(AppConfig.threadPoolSize)
  implicit private val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorsService)

  def main(args: Array[String]): Unit = {
    val events: List[Event] = loadEvents(AppConfig.eventsFilePath, withHeader = true)

    val pulsarClient = PulsarClient(AppConfig.serviceUrl)

    import io.circe.generic.auto._
    import com.sksamuel.pulsar4s.circe._

    val topic = Topic(AppConfig.topicName)
    val eventProducer = pulsarClient.producer[Event](
      ProducerConfig(
        topic,
        producerName = Some(AppConfig.producerName),
        enableBatching = Some(true),
        blockIfQueueFull = Some(true)
      )
    )

    val messageIdFutures: Seq[Future[MessageId]] = events.map { event =>
      Thread.sleep(10)
      val message: DefaultProducerMessage[Event] = DefaultProducerMessage[Event](
        Some(event.userid),
        event,
        eventTime = Some(EventTime(event.eventTime)))
      val messageIdFuture = eventProducer.sendAsync(message)
      messageIdFuture.onComplete {
        case Success(id) =>
          println(s"Ack received for message with id '$id'.")
        case Failure(exception) =>
          println(s"Failed to sent message - Reason: $exception")
      }
      messageIdFuture
    }
    Future.sequence(messageIdFutures)
    println("Producer finished sending event records.")

    eventProducer.close()
  }

}
