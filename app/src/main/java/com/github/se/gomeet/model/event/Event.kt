package com.github.se.gomeet.model.event

import com.github.se.gomeet.model.event.location.Location
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
    val images: List<String> // Is it the right type?
)
