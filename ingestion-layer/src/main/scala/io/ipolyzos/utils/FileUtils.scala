package io.ipolyzos.utils

import io.ipolyzos.models.Event

import java.sql.Timestamp
import scala.io.BufferedSource
import scala.util.{Failure, Success, Try}

trait FileUtils {
  def loadEvents(path: String, withHeader: Boolean = false): List[Event] = {
    val result = Try(scala.io.Source.fromFile(path)) match {
      case Success(source) =>
        Some(sourceHandler(source, withHeader))
      case Failure(exception) =>
        println(s"Failed to read file '$path' - Reason: $exception")
        None
    }
    result.getOrElse(List.empty)
  }

  private def sourceHandler(source: BufferedSource, withHeader: Boolean): List[Event] = {
    val events: List[Event] = source.getLines()
      .map(toEvent)
      .toList
    if (withHeader) events.drop(1) else events
  }

  private def toEvent(line: String): Event = {
    Try {
      val tokens = line.split(",")
      val eventTime = Timestamp.valueOf(tokens(0).replace(" UTC", ""))

      Event(
        tokens(7),
        eventTime.getTime,
        tokens(1),
        tokens(2),
        tokens(3),
        tokens(4),
        tokens(5),
        tokens(6).toDouble,
        tokens(8)
      )
    }.getOrElse(Event.emptyEvent())
  }
}
