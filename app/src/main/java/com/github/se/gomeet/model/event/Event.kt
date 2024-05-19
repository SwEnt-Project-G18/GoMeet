package com.github.se.gomeet.model.event

import com.github.se.gomeet.model.event.location.Location
import java.time.LocalDate
import java.time.LocalTime

/**
 * This data class represents an event. An event is the main entity of the application.
 *
 * @param eventID Event's id
 * @param creator Creator of the event
 * @param title Title of the event
 * @param description Description of the event
 * @param location Location of the event
 * @param date Date of the event
 * @param time Time of the event
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
    val time: LocalTime, // Time of the event
    val price: Double, // price of the Event
    val url: String, // Website of the event (can be ticketLink)
    val pendingParticipants: List<String>, // Pending users invitations to the event
    var participants: List<String>, // People participating to the event
    val visibleToIfPrivate: List<String>, // People that can enter the event if it's private
    val maxParticipants: Int, // Maximum number of Participants of the event
    val public: Boolean, // True if the event is public, false if it's private
    val tags: List<String>, // Tags of the event
    val images: List<String> // Is it the right type?
) {
  fun doesMatchSearchQuery(query: String): Boolean {
    val matchingCombinations =
        listOf(
            "$title$description",
            "$title $description",
            "${title.first()} ${description.first()}",
            creator,
            location.toString())

    return matchingCombinations.any { it.contains(query, ignoreCase = true) }
  }
}

/**
 * This function decides whether an event is in the past or not.
 *
 * @param event The event to check.
 * @return true if the event is today or in the future, false otherwise.
 */
fun isPastEvent(event: Event): Boolean {
  return event.date.isBefore(LocalDate.now()) && event.date != LocalDate.now()
}

fun isJoinedEvent(event: Event, userId: String): Boolean {
  return event.participants.contains(userId)
}

/**
 * Converts the time of an event to a string.
 *
 * @param eventTime The event's time.
 * @return The string representation of the time.
 */
fun getEventTimeString(eventTime: LocalTime): String {
  val minutes = if (eventTime.minute < 9) "0${eventTime.minute}" else eventTime.minute
  val hours = if (eventTime.hour < 9) "0${eventTime.hour}" else eventTime.hour
  return "${hours}:${minutes}"
}

/**
 * Converts the date of an event to a string.
 *
 * @param eventDate The event's date.
 * @return The string representation of the date.
 */
fun getEventDateString(eventDate: LocalDate): String {
  val date =
      if (eventDate == LocalDate.now()) "Today"
      else "${eventDate.dayOfMonth}/${eventDate.monthValue}/${eventDate.year}"
  return date
}

/**
 * Converts an event's date and time to a string.
 *
 * @param eventDate The event's date.
 * @param eventTime The event's time.
 * @return The string representation of the date and time.
 */
fun eventMomentToString(eventDate: LocalDate, eventTime: LocalTime): String {
  return "${getEventDateString(eventDate)} at ${getEventTimeString(eventTime)}"
}
