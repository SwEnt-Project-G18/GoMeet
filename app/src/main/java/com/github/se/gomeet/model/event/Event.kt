package com.github.se.gomeet.model.event

import com.github.se.gomeet.model.event.location.Location
import java.time.LocalDate

/**
 * This data class represents an event. An event is the main entity of the application.
 *
 * @param eventID Event's uid
 * @param creator Creator of the event
 * @param title Title of the event
 * @param description Description of the event
 * @param location Location of the event
 * @param date Date of the event
 * @param price Price of the Event
 * @param url Website of the event (can be ticketLink)
 * @param participants People participating to the event
 * @param visibleToIfPrivate People that can enter the event if it's private
 * @param maxParticipants Maximum number of Participants of the event
 * @param public True if the event is public, false if it's private
 * @param tags Tags of the event
 * @param images Is it the right type?
 */
data class Event(
    val eventID: String, // Event's uid
    val creator: String, // Creator of the event
    val title: String, // title of the event
    val description: String, // Description of the event
    val location: Location, // Location of the event
    val date: LocalDate, // Date of the event
    val price: Double, // price of the Event
    val url: String, // Website of the event (can be ticketLink)
    val pendingParticipants: List<String>, // Pending users invitations to the event
    val participants: List<String>, // People participating to the event
    val visibleToIfPrivate: List<String>, // People that can enter the event if it's private
    val maxParticipants: Int, // Maximum number of Participants of the event
    val public: Boolean, // True if the event is public, false if it's private
    val tags: List<String>, // Tags of the event
    val images: List<String> // Is it the right type?
)
