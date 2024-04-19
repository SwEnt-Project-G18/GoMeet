package com.github.se.gomeet.model.event

import com.github.se.gomeet.model.event.location.Location
import com.github.se.gomeet.model.event.location.parseLocation
import java.time.LocalDate

data class Event(
    val uid: String, // Event's uid
    val creator: String, // Creator of the event
    val title: String, // title of the event
    val description: String, // Description of the event
    val location: Location, // Location of the event
    val date: LocalDate, // Date of the event
    val price: Double, // price of the Event
    val url: String, // Website of the event (can be ticketLink)
    val participants: List<String>, // People participating to the event
    val visibleToIfPrivate: List<String>, // People that can enter the event if it's private
    val maxParticipants: Int, // Maximum number of Participants of the event
    val public: Boolean, // True if the event is public, false if it's private
    val tags: List<String>, // Tags of the event
    val images: List<String>,
    val verified: Boolean// TODO : Is it the right type for images ?
)

fun parseEvent(document: Map<String, Any>): Event? {
  return try {
    Event(
        creator = document["creator"] as? String ?: return null,
        date = LocalDate.parse(document["date"] as? String ?: return null),
        description = document["description"] as? String ?: return null,
        images = document["images"] as? List<String> ?: emptyList(),
        location = parseLocation(document["location"] as? Map<String, Any> ?: return null),
        maxParticipants = (document["maxParticipants"] as? Number)?.toInt() ?: return null,
        participants = document["participants"] as? List<String> ?: emptyList(),
        price = (document["price"] as? Number)?.toDouble() ?: return null,
        public = document["public"] as? Boolean ?: return null,
        tags = document["tags"] as? List<String> ?: emptyList(),
        title = document["title"] as? String ?: return null,
        uid = document["uid"] as? String ?: return null,
        url = document["url"] as? String ?: return null,
        visibleToIfPrivate = document["visibleToIfPrivate"] as? List<String> ?: emptyList(),
    )
  } catch (e: Exception) {
    e.printStackTrace()
    null // or log the error, depending on your error handling strategy
  }
}
