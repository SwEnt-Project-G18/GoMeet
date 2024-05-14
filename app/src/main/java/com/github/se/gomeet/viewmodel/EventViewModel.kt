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
import com.google.firebase.firestore.FirebaseFirestore
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

/**
 * ViewModel for the event. The viewModel is responsible for handling the logic that comes from the
 * UI and the repository.
 *
 * @param creatorId the id of the creator of the event
 */
class EventViewModel(private val creatorId: String? = null, eventRepository: EventRepository) :
    ViewModel() {
  private val repository = eventRepository
  private val _bitmapDescriptors = mutableStateMapOf<String, BitmapDescriptor>()
  val bitmapDescriptors: MutableMap<String, BitmapDescriptor> = _bitmapDescriptors

  private var lastLoadedEvents: List<Event>? = null
  private val _loading = MutableLiveData(false)
  val loading: LiveData<Boolean> = _loading

  /**
   * Load custom pins for the events.
   *
   * @param context the context of the application
   * @param events the list of events to load the custom pins for
   */
  fun loadCustomPins(context: Context, events: List<Event>) =
      viewModelScope.launch {
        // Check if the current events are different from the last loaded events
        if (events != lastLoadedEvents) {
          _loading.value = true
          val loadJobs =
              events.map { event ->
                async {
                  val imagePath = "event_icons/${event.eventID}.png"
                  val storageRef = FirebaseStorage.getInstance().reference.child(imagePath)
                  val uri = storageRef.downloadUrl.await() // Await the download URL
                  try {
                    val bitmapDescriptor =
                        loadBitmapFromUri(context, uri) // Load the bitmap as a BitmapDescriptor
                    _bitmapDescriptors[event.eventID] = bitmapDescriptor
                  } catch (e: Exception) {
                    Log.e("ViewModel", "Error loading bitmap descriptor: ${e.message}")
                    _bitmapDescriptors[event.eventID] =
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

  /**
   * Load a bitmap from a URI.
   *
   * @param context the context of the application
   * @param uri the URI of the image to load
   */
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

  /**
   * Get an event by its UID.
   *
   * @param uid the UID of the event to get
   * @return the event with the given UID, or null if it does not exist
   */
  suspend fun getEvent(uid: String): Event? {
    return try {
      val event = CompletableDeferred<Event?>()
      repository.getEvent(uid) { t -> event.complete(t) }
      event.await()
    } catch (e: Exception) {
      null
    }
  }

  /**
   * Get the image URL of an event's image.
   *
   * @param eventId the ID of the event
   * @return the image URL of the event
   */
  suspend fun getEventImageUrl(eventId: String): String? {
    val db = FirebaseFirestore.getInstance()
    return try {
      val event = CompletableDeferred<String?>()
      db.collection("events")
          .document(eventId)
          .get()
          .addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
              val imagesList = documentSnapshot.get("images") as? List<*>
              if (!imagesList.isNullOrEmpty()) {
                event.complete(imagesList.firstOrNull()?.toString())
              } else {
                event.complete(null)
              }
            } else {
              event.complete(null)
            }
          }
          .addOnFailureListener { event.completeExceptionally(it) }
      event.await()
    } catch (e: Exception) {
      Log.e("Firebase", "Error fetching event image: ${e.localizedMessage}")
      null
    }
  }

  /**
   * Upload an image to Firebase Storage and get the download URL.
   *
   * @param imageUri the URI of the image to upload
   * @return the download URL of the uploaded image
   */
  suspend fun uploadImageAndGetUrl(imageUri: Uri): String {
    val imageRef = Firebase.storage.reference.child("images/${imageUri.lastPathSegment}")
    val uploadTaskSnapshot = imageRef.putFile(imageUri).await()
    return uploadTaskSnapshot.metadata?.reference?.downloadUrl?.await().toString()
  }

  /** Get all events that exist in the database. */
  suspend fun getAllEvents(): List<Event>? {
    return try {
      val event = CompletableDeferred<List<Event>?>()
      repository.getAllEvents { t -> event.complete(t) }
      event.await()
    } catch (e: Exception) {
      null
    }
  }

  /**
   * Create an event.
   *
   * @param title the title of the event
   * @param description the description of the event
   * @param location the location of the event
   * @param date the date of the event
   * @param price the price of the event
   * @param url the URL of the event
   * @param participants the participants of the event
   * @param visibleToIfPrivate the users that the event is visible to if it is private
   * @param maxParticipants the maximum number of participants of the event
   * @param public whether the event is public
   * @param tags the tags of the event
   * @param images the images of the event
   * @param imageUri the URI of the image of the event
   * @param uid the UID of the event
   */
  fun createEvent(
      title: String,
      description: String,
      location: Location,
      date: LocalDate,
      price: Double,
      url: String,
      pendingParticipants: List<String>,
      participants: List<String>,
      visibleToIfPrivate: List<String>,
      maxParticipants: Int,
      public: Boolean,
      tags: List<String>,
      images: List<String>,
      imageUri: Uri?,
      userViewModel: UserViewModel,
      uid: String
  ) {
    Log.d("CreatorID", "Creator ID is $creatorId")
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
                pendingParticipants,
                participants,
                visibleToIfPrivate,
                maxParticipants,
                public,
                tags,
                updatedImages)

        repository.addEvent(event)
        joinEvent(event, creatorId)
        userViewModel.joinEvent(event.eventID, creatorId)
        userViewModel.userCreatesEvent(event.eventID, creatorId)
      } catch (e: Exception) {
        Log.w(TAG, "Error uploading image or adding event", e)
      }
    }
  }

  /**
   * Edit an event.
   *
   * @param event the event to edit
   */
  fun editEvent(event: Event) {
    repository.updateEvent(event)
  }

  /**
   * Remove an event by its UID.
   *
   * @param eventID the ID of the event to remove
   */
  fun removeEvent(eventID: String) {
    repository.removeEvent(eventID)
  }

  fun joinEvent(event: Event, userId: String) {
    if (event.participants.contains(userId)) {
      Log.w(TAG, "User $userId is already in event ${event.eventID}")
      return
    }

    repository.updateEvent(event.copy(participants = event.participants.plus(userId)))
  }

  fun sendInvitation(event: Event, userId: String) {
    if (event.pendingParticipants.contains(userId)) {
      Log.w(TAG, "User $userId is already invited to event ${event.eventID}")
      return
    }

    repository.updateEvent(event.copy(pendingParticipants = event.pendingParticipants.plus(userId)))
  }

  fun acceptInvitation(event: Event, userId: String) {
    assert(event.pendingParticipants.contains(userId))
    repository.updateEvent(
        event.copy(pendingParticipants = event.pendingParticipants.minus(userId)))
    joinEvent(event, userId)
  }

  fun declineInvitation(event: Event, userId: String) {
    assert(event.pendingParticipants.contains(userId))
    repository.updateEvent(
        event.copy(pendingParticipants = event.pendingParticipants.minus(userId)))
  }

  fun kickParticipant(event: Event, userId: String) {
    assert(event.participants.contains(userId))
    repository.updateEvent(event.copy(participants = event.participants.minus(userId)))
  }

  fun cancelInvitation(event: Event, userId: String) {
    if (!event.pendingParticipants.contains(userId)) {
      Log.w(TAG, "Event doesn't have ${userId} as a pendingParticipant")
      return
    }

    repository.updateEvent(
        event.copy(pendingParticipants = event.pendingParticipants.minus(userId)))
  }

  /**
   * Get the location of an event.
   *
   * @param locationName the name of the location
   * @param numberOfResults the number of results to get
   * @param onResult the function to call with the result
   */
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

  /**
   * Helper function to parse the location response.
   *
   * @param responseBody the response body
   * @param numberOfResults the number of results to get
   * @return the list of locations
   */
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
