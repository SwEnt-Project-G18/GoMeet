package com.github.se.gomeet.model.event.location

data class Location(val latitude: Double, val longitude: Double, val name: String)

fun parseLocation(document: Map<String, Any>): Location {
    return Location(
        latitude = document["latitude"] as Double,
        longitude = document["longitude"] as Double,
        name = document["name"] as String
    )
}