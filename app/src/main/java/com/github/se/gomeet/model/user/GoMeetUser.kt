package com.github.se.gomeet.model.user

import com.github.se.gomeet.model.event.Invitation

const val UID = "uid"
const val USERNAME = "username"
const val FIRST_NAME = "firstName"
const val LAST_NAME = "lastName"
const val EMAIL = "email"
const val PHONE_NUMBER = "phoneNumber"
const val COUNTRY = "country"
const val FOLLOWING = "following"
const val FOLLOWERS = "followers"
const val PENDING_REQUESTS = "pendingRequests"
const val JOINED_EVENTS = "joinedEvents"
const val MY_EVENTS = "myEvents"
const val MY_FAVORITES = "myFavorites"
const val PROFILE_PICTURE = "profilePicture"
const val TAGS = "tags"
const val RATING = "rating"





/**
 * This data class represents the user of the application. It contains the user's information.
 *
 * @param uid The user id
 * @param username The username
 * @param firstName The first name of the user
 * @param lastName The last name of the user
 * @param email The email of the user
 * @param phoneNumber The phone number of the user
 * @param country The country of the user
 * @param following The list of users that the user is following
 * @param followers The list of users that are following the user
 * @param pendingRequests The list of requests that the user has not accepted or refused yet
 * @param joinedEvents The list of events that the user joined
 * @param myEvents The list of events that were created by the user
 * @param myFavorites The list of events that the user added to favorites
 * @param profilePicture The profile picture of the user
 * @param tags The list of tags that the user is interested in
 * @param rating The average rating of the user (total rating, number of ratings)
 */
data class GoMeetUser(
    val uid: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val country: String,
    val following: List<String>,
    val followers: List<String>,
    val pendingRequests: Set<Invitation>,
    var joinedEvents: List<String>,
    var myEvents: List<String>,
    var myFavorites: List<String>,
    var profilePicture: String = "",
    var tags: List<String>,
    var rating: Pair<Long, Long> = Pair(0, 0),
) {

  /**
   * This function checks if the user matches the search query.
   *
   * @param query The search query
   * @return true if the user matches the search query, false otherwise
   */
  fun doesMatchSearchQuery(query: String): Boolean {
    val matchingCombinations =
        listOf(
            "$firstName$lastName", "$firstName $lastName", username, email
            // "${firstName.first()} ${lastName.first()}",
            )

    return matchingCombinations.any { it.contains(query, ignoreCase = true) }
  }
}
