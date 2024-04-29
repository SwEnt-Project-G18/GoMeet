package com.github.se.gomeet.model.user

data class GoMeetUser(
    val uid: String,
    val username: String,
    val following: List<String>,
    val followers: List<String>,
    val pendingRequests: List<String>,
    val joinedEvents: List<String>,
    var myEvents: List<String>,
    var myFavorites: List<String>
    // can add more things later
)
