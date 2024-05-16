package com.github.se.gomeet.model.event

/**
 * This enum class represents the status of an invitation. It can be ACCEPTED, PENDING or REFUSED.
 */
enum class InviteStatus {
  ACCEPTED,
  PENDING,
  REFUSED,
}

/**
 * This data class represents the invitation to an event. It contains the event's uid and the status
 * of the invitation.
 *
 * @param eventId Event's id
 * @param status Status of the invitation
 */
data class Invitation(val eventId: String, val status: InviteStatus)
