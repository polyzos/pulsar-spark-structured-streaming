package io.ipolyzos.config

object AppConfig {
  lazy val topicName      = "events"
  lazy val eventsFilePath = "data/events.csv"
  lazy val serviceUrl     = "pulsar://localhost:6650"
  lazy val adminUrl       = "http://localhost:8080"
  lazy val producerName   = "event-producer"
  lazy val threadPoolSize = 4
}
