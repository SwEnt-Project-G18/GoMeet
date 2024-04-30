package com.github.se.gomeet.model.event

/**
 * This data class represents the invitations of an event containing all the users invited to this
 * event.
 *
 * @param event The event id
 * @param usersInvited The list of users invited to the event with their invitation status
 */
data class EventInviteUsers(
    val event: String,
    val usersInvited: MutableList<Pair<String, InviteStatus>>,
)

/**
 * This data class represents the invitations of a user containing all the events the user has been
 * invited to.
 *
 * @param user The user id
 * @param invitedToEvents The list of events the user has been invited to with their invitation
 *   status
 */
data class UserInvitedToEvents(
    val user: String,
    val invitedToEvents: MutableList<Pair<String, InviteStatus>>
)

/**
 * This enum class represents the status of an invitation. It can be ACCEPTED, PENDING or REFUSED.
 */
enum class InviteStatus {
  ACCEPTED,
  PENDING,
  REFUSED
}

data class Invite(
    val eventId: String,
    val userId: String,
    val status: InviteStatus
)