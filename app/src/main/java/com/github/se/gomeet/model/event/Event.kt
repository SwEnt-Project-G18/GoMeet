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
    val images: List<String>, // Is it the right type?
    val eventRatings: Map<String, Int> // Ratings of the event by each user (i.e. userID -> rating)
) {

  /**
   * This function checks if the event matches the search query.
   *
   * @param query The search query.
   * @return true if the event matches the search query, false otherwise.
   */
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
  /**
   * This function decides whether an event is in the past or not.
   *
   * @return true if the event is today or in the future, false otherwise.
   */
  fun isPastEvent(): Boolean {
    return this.date.isBefore(LocalDate.now()) && this.date != LocalDate.now()
  }

  /**
   * This function returns whether a user has joined an event or not.
   *
   * @param userId The user's id.
   * @return true if the user has joined the event, false otherwise.
   */
  fun hasUserJoined(userId: String): Boolean {
    return this.participants.contains(userId)
  }

  /**
   * Converts the time of an event to a string.
   *
   * @return The string representation of the time.
   */
  fun getTimeString(): String {
    val minutes = if (this.time.minute < 9) "0${this.time.minute}" else this.time.minute
    val hours = if (this.time.hour < 9) "0${this.time.hour}" else this.time.hour
    return "${hours}:${minutes}"
  }

  /**
   * Converts the date of an event to a string.
   *
   * @return The string representation of the date.
   */
  fun getDateString(): String {
    val date =
        if (this.date == LocalDate.now()) "Today"
        else "${this.date.dayOfMonth}/${this.date.monthValue}/${this.date.year}"
    return date
  }

  /**
   * Converts an event's date and time to a string.
   *
   * @return The string representation of the date and time.
   */
  fun momentToString(): String {
    return "${this.getDateString()} at ${this.getTimeString()}"
  }
}
