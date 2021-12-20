package io.ipolyzos.wrappers

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

trait SparkSessionWrapper {
  def initSparkSession(appName: String,
                       configs: List[(String, String)] = List.empty,
                       master: String = "local[*]",
                       sparkConf: SparkConf = new SparkConf()): SparkSession = {
    val conf = if (configs.isEmpty) sparkConf else sparkConf.setAll(configs)

    SparkSession
      .builder()
      .appName(appName)
      .master(master)
      .config(conf)
      .getOrCreate()
  }
}