package com.github.se.gomeet

import android.util.Log
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.location.Location
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate

class EventFirebaseConnection(private val db: FirebaseFirestore) {
  companion object {
    private const val TAG = "FirebaseConnection"
    private const val EVENT_COLLECTION = "events"
  }

  fun getNewId(): String {
    return db.collection(EVENT_COLLECTION).document().id
  }

  fun getEvent(uid: String, callback: (Event?) -> Unit) {
    db.collection(EVENT_COLLECTION)
        .document(uid)
        .get()
        .addOnSuccessListener { document ->
          if (document != null && document.exists()) {
            val event = document.data!!.fromMap(uid)
            callback(event)
          } else {
            Log.d(TAG, "No such document")
            callback(null)
          }
        }
        .addOnFailureListener { exception ->
          Log.d(TAG, "get failed with ", exception)
          callback(null)
        }
  }

  fun getAllEvents(callback: (List<Event>?) -> Unit) {
    db.collection(EVENT_COLLECTION)
        .get()
        .addOnSuccessListener { querySnapshot ->
          val eventList = mutableListOf<Event>()
          for (document in querySnapshot.documents) {
            val event = document.data?.fromMap(document.id)
            if (event != null) {
              eventList.add(event)
            }
          }
          callback(eventList)
        }
        .addOnFailureListener { exception ->
          Log.w(TAG, "Error getting documents.", exception)
          callback(null)
        }
  }

  fun addEvent(event: Event) {
    db.collection(EVENT_COLLECTION)
        .document(event.uid)
        .set(event.toMap())
        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
  }

  fun updateEvent(event: Event) {
    val documentRef = db.collection(EVENT_COLLECTION).document(event.uid)
    documentRef
        .update(event.toMap())
        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
  }

  fun removeEvent(uid: String) {
    db.collection(EVENT_COLLECTION)
        .document(uid)
        .delete()
        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
  }

  private fun Event.toMap(): Map<String, Any?> {
    return mapOf(
        "uid" to uid,
        "creator" to creator,
        "title" to title,
        "description" to description,
        "location" to location.toMap(),
        "date" to date.toString(),
        "price" to price,
        "url" to url,
        "participants" to participants,
        "visibleToIfPrivate" to visibleToIfPrivate,
        "maxParticipants" to maxParticipants,
        "public" to public,
        "tags" to tags,
        "images" to images)
  }

  private fun Location.toMap(): Map<String, Any> {
    return mapOf("latitude" to latitude, "longitude" to longitude, "name" to name)
  }

  private fun Map<String, Any>.fromMap(id: String): Event {
    return Event(
        uid = id,
        creator = this["creator"] as String,
        title = this["title"] as String,
        description = this["description"] as String,
        location = (this["location"] as Map<String, Any>).toLocation(),
        date = LocalDate.parse(this["date"] as String),
        price = this["price"] as Double,
        url = this["url"] as String,
        participants = this["participants"] as List<String>,
        visibleToIfPrivate = this["visibleToIfPrivate"] as List<String>,
        maxParticipants = this["maxParticipants"].toString().toInt(),
        public = this["public"] as Boolean,
        tags = this["tags"] as List<String>,
        images = this["images"] as List<String>)
  }

  private fun Map<String, Any>.toLocation(): Location {
    return Location(
        latitude = this["latitude"] as Double,
        longitude = this["longitude"] as Double,
        name = this["name"] as String)
  }
}
