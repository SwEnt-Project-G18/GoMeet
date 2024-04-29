package com.github.se.gomeet.model.user

/**
 * This data class represents the user of the application. It contains the user's information.
 *
 * @param uid The user id
 * @param username The username
 * @param following The list of users that the user is following
 * @param followers The list of users that are following the user
 * @param pendingRequests The list of requests that the user has not accepted or refused yet
 */
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
