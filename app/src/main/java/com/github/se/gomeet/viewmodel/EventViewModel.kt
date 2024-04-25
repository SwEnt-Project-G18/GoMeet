package com.github.se.gomeet.viewmodel

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.location.Location
import com.github.se.gomeet.model.repository.EventRepository
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.io.IOException
import java.time.LocalDate
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

class EventViewModel(private val creatorId: String? = null) : ViewModel() {
  private val db = EventRepository(Firebase.firestore)
  private val _bitmapDescriptors = mutableStateMapOf<String, BitmapDescriptor>()
  val bitmapDescriptors: MutableMap<String, BitmapDescriptor> = _bitmapDescriptors

  private var lastLoadedEvents: List<Event>? = null
  private val _loading = MutableLiveData(false)
  val loading: LiveData<Boolean> = _loading

  fun loadCustomPins(context: Context, events: List<Event>) =
      viewModelScope.launch {
        // Check if the current events are different from the last loaded events
        if (events != lastLoadedEvents) {
          _loading.value = true
          val loadJobs =
              events.map { event ->
                async {
                  val imagePath = "event_icons/${event.uid}.png"
                  val storageRef = FirebaseStorage.getInstance().reference.child(imagePath)
                  val uri = storageRef.downloadUrl.await() // Await the download URL
                  try {
                    val bitmapDescriptor =
                        loadBitmapFromUri(context, uri) // Load the bitmap as a BitmapDescriptor
                    _bitmapDescriptors[event.uid] = bitmapDescriptor
                  } catch (e: Exception) {
                    Log.e("ViewModel", "Error loading bitmap descriptor: ${e.message}")
                    _bitmapDescriptors[event.uid] =
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                  }
                }
              }

          try {
            loadJobs.awaitAll() // Await all loading jobs
          } finally {
            lastLoadedEvents = events.toList() // Update the last loaded events
            Log.d("ViewModel", "Finished loading custom pins")
            _loading.value = false
          }
        }
      }

  suspend fun loadBitmapFromUri(context: Context, uri: Uri): BitmapDescriptor =
      suspendCancellableCoroutine { continuation ->
        // Create a temporary ImageView to load the image.
        val imageView = ImageView(context)
        imageView.layout(0, 0, 1, 1) // Minimal size

        Picasso.get()
            .load(uri)
            .into(
                imageView,
                object : com.squareup.picasso.Callback {
                  override fun onSuccess() {
                    imageView.drawable?.let { drawable ->
                      val bitmap = (drawable as BitmapDrawable).bitmap
                      continuation.resume(BitmapDescriptorFactory.fromBitmap(bitmap))
                    }
                        ?: run {
                          Log.e("ViewModel", "Drawable is null after loading image.")
                          continuation.resumeWithException(
                              RuntimeException("Drawable is null after loading image"))
                        }
                  }

                  override fun onError(e: Exception?) {
                    Log.e("ViewModel", "Error loading image from Picasso: ${e?.message}")
                    continuation.resumeWithException(
                        e ?: RuntimeException("Unknown error in Picasso"))
                  }
                })

        // Handle cancellation of the coroutine.
        continuation.invokeOnCancellation {
          imageView.setImageDrawable(null) // Clear resources
        }
      }

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
      imageUri: Uri?,
      uid: String
  ) {
    CoroutineScope(Dispatchers.IO).launch {
      try {
        val imageUrl = imageUri?.let { uploadImageAndGetUrl(it) }
        val updatedImages = images.toMutableList().apply { imageUrl?.let { add(it) } }
        val event =
            Event(
                uid,
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
        val resBody = res.body?.string() ?: throw IOException("No response from nominatim")
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
