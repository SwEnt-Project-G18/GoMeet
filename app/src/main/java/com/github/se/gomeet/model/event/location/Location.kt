package com.github.se.gomeet.model.event.location

// Constants for the keys in the Firestore database
const val LATITUDE = "latitude"
const val LONGITUDE = "longitude"
const val NAME = "name"

/**
 * This data class represents the location of an event.
 *
 * @param latitude The latitude of the location.
 * @param longitude The longitude of the location.
 * @param name The name of the location.
 */
data class Location(val latitude: Double, val longitude: Double, val name: String)
