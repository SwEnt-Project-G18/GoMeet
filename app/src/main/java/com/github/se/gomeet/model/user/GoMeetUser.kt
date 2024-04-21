package com.github.se.gomeet.model.user

data class GoMeetUser(
    val uid: String,
    val username: String,
    val following: List<String>,
    val followers: List<String>,
    val pendingRequests: List<String>,
    // can add more things later
)
