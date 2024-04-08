package com.github.se.gomeet.model

import com.github.se.gomeet.model.location.Location
import java.time.LocalDate

data class Event(
    val uid: String,
    val creator: String,
    val title: String,
    val description: String,
    val location: Location,
    val date: LocalDate,
    val price: Double,
    val url: String?,
    val participants: List<String?>,
    val visibleToIfPrivate: List<String>,
    val maxParticipants: Int,
    val public: Boolean,
    val tags: List<String>,
    val images: List<String>
)
