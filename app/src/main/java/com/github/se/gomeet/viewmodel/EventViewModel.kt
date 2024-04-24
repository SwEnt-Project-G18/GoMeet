package com.github.se.gomeet.viewmodel

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.location.Location
import com.github.se.gomeet.model.repository.EventRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.IOException
import java.time.LocalDate
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

class EventViewModel(private val creatorId: String? = null) : ViewModel() {
  private val db = EventRepository(Firebase.firestore)

  suspend fun getEvent(uid: String): Event? {
    return try {
      val event = CompletableDeferred<Event?>()
      db.getEvent(uid) { t -> event.complete(t) }
      event.await()
    } catch (e: Exception) {
      null
    }
  }

  suspend fun uploadImageAndGetUrl(imageUri: Uri): String {
    val imageRef = Firebase.storage.reference.child("images/${imageUri.lastPathSegment}")
    val uploadTaskSnapshot = imageRef.putFile(imageUri).await()
    return uploadTaskSnapshot.metadata?.reference?.downloadUrl?.await().toString()
  }

  suspend fun getAllEvents(): List<Event>? {
    return try {
      val event = CompletableDeferred<List<Event>?>()
      db.getAllEvents { t -> event.complete(t) }
      event.await()
    } catch (e: Exception) {
      null
    }
  }

  fun createEvent(
      title: String,
      description: String,
      location: Location,
      date: LocalDate,
      price: Double,
      url: String,
      participants: List<String>,
      visibleToIfPrivate: List<String>,
      maxParticipants: Int,
      public: Boolean,
      tags: List<String>,
      images: List<String>,
      imageUri: Uri?
  ) {
    CoroutineScope(Dispatchers.IO).launch {
      try {
        val imageUrl = imageUri?.let { uploadImageAndGetUrl(it) }
        val updatedImages = images.toMutableList().apply { imageUrl?.let { add(it) } }
        val event =
            Event(
                db.getNewId(),
                creatorId!!,
                title,
                description,
                location,
                date,
                price,
                url,
                participants,
                visibleToIfPrivate,
                maxParticipants,
                public,
                tags,
                updatedImages)
        db.addEvent(event)
      } catch (e: Exception) {
        Log.w(TAG, "Error uploading image or adding event", e)
      }
    }
  }

  fun editEvent(event: Event) {
    db.updateEvent(event)
  }

  fun removeEvent(uid: String) {
    db.removeEvent(uid)
  }

  fun location(locationName: String, numberOfResults: Int, onResult: (List<Location>) -> Unit) {
    viewModelScope.launch {
      try {
        val client = OkHttpClient()
        val url =
            "https://nominatim.openstreetmap.org/search?q=${
                        locationName.replace(
                            " ",
                            "+"
                        )
                    }&format=json&limit=$numberOfResults"
        val req = Request.Builder().url(url).build()
        val res = withContext(Dispatchers.IO) { client.newCall(req).execute() }
        if (!res.isSuccessful) throw IOException("IOException")
        val resBody = res.body?.string() ?: throw IOException("No response from nominatim")
        val locations = locHelper(resBody, numberOfResults)
        withContext(Dispatchers.Main) { onResult(locations) }
      } catch (e: Exception) {
        onResult(emptyList())
      }
    }
  }

  private fun locHelper(responseBody: String, numberOfResults: Int): List<Location> {
    val locations: MutableList<Location> = mutableListOf()
    val jar = JSONArray(responseBody)
    if (jar.length() > 0) {
      for (i in 0 until numberOfResults) {
        try {
          val jObj = jar.getJSONObject(i)
          val displayName = jObj.optString("display_name", "Unknown Location")
          locations.add(
              Location(
                  jObj.getString("lat").toDouble(), jObj.getString("lon").toDouble(), displayName))
        } catch (e: Exception) {
          return locations
        }
      }
    }
    return locations
  }
}
