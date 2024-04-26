package com.github.se.gomeet.model.event
data class EventInviteUsers(
    val e: String,
    val usersInvited: MutableList<Pair<String, InviteStatus>>,
    // List of users uid invited to the event e
)

data class UserInvitedToEvents(
    val u: String,
    val invitedToEvents:
        MutableList<Pair<String, InviteStatus>> // List of events id the user u has been invited to
)

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