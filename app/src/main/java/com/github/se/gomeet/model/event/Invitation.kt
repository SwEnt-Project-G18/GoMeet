package com.github.se.gomeet.model.event

/**
 * This enum class represents the status of an invitation. It can be ACCEPTED, PENDING or REFUSED.
 */
enum class InviteStatus {
  ACCEPTED,
  PENDING,
  REFUSED,
}

/**  */
data class Invitation(val eventId: String, val status: InviteStatus)
