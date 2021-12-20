package io.ipolyzos.config

object AppConfig {
  lazy val topicName      = "events"
  lazy val serviceUrl     = "pulsar://localhost:6650"
  lazy val adminUrl       = "http://localhost:8080"
}
