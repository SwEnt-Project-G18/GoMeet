package com.github.se.gomeet.model.event

/**
 * This enum class represents the status of an invitation. It can be ACCEPTED, PENDING or REFUSED.
 * The TO_INVITE status is used to represent an invitable user status.
 *
 * @param formattedName The formatted name of the status.
 */
enum class InviteStatus(val formattedName: String, val button: String = "") {
  TO_INVITE("To Invite"),
  PENDING("Pending"),
  ACCEPTED("Accepted", "Accept"),
  REFUSED("Refused", "Refuse");
}

/**
 * This data class represents the invitation to an event. It contains the event's uid and the status
 * of the invitation.
 *
 * @param eventId Event's id
 * @param status Status of the invitation
 */
data class Invitation(val eventId: String, val status: InviteStatus)
