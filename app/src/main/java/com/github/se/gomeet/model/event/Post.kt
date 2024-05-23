package com.github.se.gomeet.model.event

import java.time.LocalDate
import java.time.LocalTime

/**
 * This data class represents an Event Post.
 *
 * @property userId the ID of the user who created the post
 * @property title the title of the post
 * @property content the content of the post
 * @property date the date of the post
 * @property time the time of the post
 * @property image the URL or path to the image associated with the post
 * @property likes a list of user IDs who liked the post
 * @property comments a list of pairs representing comments on the post, where each pair contains a
 *   user ID and the comment text
 */
data class Post(
    val userId: String,
    val title: String,
    val content: String,
    val date: LocalDate,
    val time: LocalTime,
    var image: String,
    var likes: List<String>,
    val comments: List<Pair<String, String>>
)
