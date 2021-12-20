package io.ipolyzos.models

case class Event(userid: String,
                 eventTime: Long,
                 eventType: String,
                 productId: String,
                 categoryId: String,
                 categoryCode: String,
                 brand: String,
                 price: Double,
                 userSession: String)

object Event {
  def emptyEvent(): Event = Event("", 0L, "" , "", "", "", "", 0.0, "")
}
