package com.github.se.gomeet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gomeet.EventFirebaseConnection
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.location.Location
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.IOException
import java.time.LocalDate
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

class EventViewModel(private val creatorId: String? = null) : ViewModel() {
  private val db = EventFirebaseConnection(Firebase.firestore)

  suspend fun getEvent(uid: String): Event? {
    return try {
      val event = CompletableDeferred<Event?>()
      db.getEvent(uid) { t -> event.complete(t) }
      event.await()
    } catch (e: Exception) {
      null
    }
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
      images: List<String>
  ) {
    db.addEvent(
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
            images))
  }

  fun editEvent(event: Event) {
    db.updateEvent(event)
  }

  fun removeEvent(uid: String) {
    db.removeEvent(uid)
  }

  fun location(locationName: String, onResult: (Location?) -> Unit) {
    viewModelScope.launch {
      try {
        val client = OkHttpClient()
        val url =
            "https://nominatim.openstreetmap.org/search?q=${
                        locationName.replace(
                            " ",
                            "+"
                        )
                    }&format=json&limit=1"
        val req = Request.Builder().url(url).build()
        val res = withContext(Dispatchers.IO) { client.newCall(req).execute() }
        if (!res.isSuccessful) throw IOException("IOException")
        val resBody = res.body()?.string() ?: throw IOException("No response from nominatim")
        val location = locHelper(resBody)
        withContext(Dispatchers.Main) { onResult(location) }
      } catch (e: Exception) {
        onResult(null)
      }
    }
  }

  private fun locHelper(responseBody: String): Location {
    val jar = JSONArray(responseBody)
    return if (jar.length() > 0) {
      val jObj = jar.getJSONObject(0)
      val displayName = jObj.optString("display_name", "Unknown Location")
      Location(jObj.getString("lat").toDouble(), jObj.getString("lon").toDouble(), displayName)
    } else {
      Location(0.0, 0.0, "Null Island")
    }
  }
}
