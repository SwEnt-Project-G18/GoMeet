package com.github.se.gomeet.model.event

data class EventInvite(
    val e: String,
    val usersInvited: List<Pair<String, InviteStatus>>,
    //List of users uid invited to the event e
)

data class UsersInvited(
    val u: String,
    val invitedToEvents: List<Pair<String, InviteStatus>> // List of events id the user u has been invited to
)


enum class InviteStatus {
    ACCEPTED,
    PENDING,
    REFUSED
}
