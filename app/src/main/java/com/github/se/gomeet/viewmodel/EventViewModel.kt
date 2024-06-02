package com.github.se.gomeet.viewmodel

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.Post
import com.github.se.gomeet.model.event.location.Location
import com.github.se.gomeet.model.repository.EventRepository
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.model.user.GoMeetUser
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
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

private const val TAG = "EventViewModel"

/**
 * ViewModel for the event. The viewModel is responsible for handling the logic that comes from the
 * UI and the repository.
 *
 * @param currentUID the id of the current user (and therefore the creator of any events)
 */
class EventViewModel(val currentUID: String? = null) : ViewModel() {
  private val _bitmapDescriptors = mutableStateMapOf<String, BitmapDescriptor>()
  val bitmapDescriptors: MutableMap<String, BitmapDescriptor> = _bitmapDescriptors

  private var lastLoadedEvents: List<Event> = emptyList()
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
                    Log.e(TAG, "Error loading bitmap descriptor", e)
                    _bitmapDescriptors[event.eventID] =
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                  }
                }
              }

          try {
            loadJobs.awaitAll() // Await all loading jobs
          } finally {
            lastLoadedEvents = events.toList() // Update the last loaded events
            Log.d(TAG, "Finished loading custom pins")
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
                          Log.w(TAG, "Drawable is null after loading image.")
                          continuation.resumeWithException(
                              RuntimeException("Drawable is null after loading image"))
                        }
                  }

                  override fun onError(e: Exception?) {
                    Log.e(TAG, "Error loading image from Picasso", e)
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
   * @param eid the UID of the event to get
   * @return the event with the given UID, or null if it does not exist
   */
  suspend fun getEvent(eid: String): Event? {
    return try {
      val event = CompletableDeferred<Event?>()
      EventRepository.getEvent(eid) { t -> event.complete(t) }
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
      Log.e(TAG, "Error fetching event image for event $eventId", e)
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
    // Generate a unique filename using a timestamp and UUID
    val uniqueFilename = "images/${System.currentTimeMillis()}-${UUID.randomUUID()}.jpg"
    val imageRef = Firebase.storage.reference.child(uniqueFilename)

    // Upload the file and await the completion
    val uploadTaskSnapshot = imageRef.putFile(imageUri).await()

    // Retrieve and return the download URL
    return uploadTaskSnapshot.metadata?.reference?.downloadUrl?.await()?.toString()
        ?: throw Exception("Failed to upload image and retrieve URL")
  }

  /**
   * Get all events that exist in the database. Also removes phantom favourite events from the
   * current user's favourites list. Caution: will throw null pointer exception if currentUID is
   * null (this function shouldn't get called as long as no user has signed in).
   *
   * @param callback the function to call with the list of events.
   */
  fun getAllEvents(callback: (List<Event>?) -> Unit) {
    viewModelScope.launch {
      val event = CompletableDeferred<List<Event>?>()
      EventRepository.getAllEvents { t -> event.complete(t) }
      val events = event.await()
      callback(events)

      // Prevent favourite events from being displayed if they are not in the database
      if (events != null) {
        val illegalEvents = mutableSetOf<String>()
        UserRepository.getUser(currentUID!!) { user ->
          if (user == null) return@getUser
          illegalEvents.addAll(user.myFavorites.toSet().minus(events.map { it.eventID }.toSet()))
        }
        UserRepository.removeFavouriteEvents(currentUID, eventIDs = illegalEvents.toList())
      }
    }
  }

  /**
   * Create an event.
   *
   * @param title the title of the event
   * @param description the description of the event
   * @param location the location of the event
   * @param date the date of the event
   * @param time the time at which the event takes place
   * @param price the price of the event
   * @param url the URL of the event
   * @param pendingParticipants the list of users with a pending invitation to the event
   * @param participants the participants of the event
   * @param visibleToIfPrivate the users that the event is visible to if it is private
   * @param maxParticipants the maximum number of participants of the event
   * @param public whether the event is public
   * @param tags the tags of the event
   * @param images the images of the event
   * @param imageUri the URI of the image of the event
   * @param userViewModel the user view model
   * @param eventId the UID of the event
   */
  fun createEvent(
      title: String,
      description: String,
      location: Location,
      date: LocalDate,
      time: LocalTime,
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
      eventId: String
  ) {
    Log.d(TAG, "User $currentUID is creating event $eventId")
    CoroutineScope(Dispatchers.IO).launch {
      try {
        val participantsWithCreator =
            if (participants.contains(currentUID)) participants else participants.plus(currentUID!!)
        val imageUrl = imageUri?.let { uploadImageAndGetUrl(it) }
        val updatedImages = images.toMutableList().apply { imageUrl?.let { add(it) } }
        val event =
            Event(
                eventId,
                currentUID!!,
                title,
                description,
                location,
                date,
                time,
                price,
                url,
                pendingParticipants,
                participantsWithCreator,
                visibleToIfPrivate,
                maxParticipants,
                public,
                tags,
                updatedImages)

        EventRepository.addEvent(event)
        lastLoadedEvents = lastLoadedEvents.plus(event)
        userViewModel.joinEvent(event.eventID, currentUID)
        userViewModel.userCreatesEvent(event.eventID)
      } catch (e: Exception) {
        Log.e(TAG, "Error uploading image or adding event", e)
      }
    }
  }

  /**
   * Edit an event.
   *
   * @param event the event to edit
   */
  fun editEvent(event: Event) {
    lastLoadedEvents = lastLoadedEvents.filter { it.eventID != event.eventID }
    lastLoadedEvents = lastLoadedEvents.plus(event)
    EventRepository.updateEvent(event)
  }

  /**
   * Edits a post in the specified event.
   *
   * @param event The event containing the post to be edited
   * @param oldPost The original post to be edited
   * @param newPost The new post data
   */
  fun editPost(event: Event, oldPost: Post, newPost: Post) {
    val updatedPosts =
        event.posts.map { currentPost ->
          if (currentPost == oldPost) {
            newPost
          } else {
            currentPost
          }
        }
    editEvent(event.copy(posts = updatedPosts))
  }

  /**
   * Update the rating of an event by one particular user.
   *
   * @param eventID the ID of the event to update
   * @param newRating the new rating of the event
   * @param oldRating the old rating of the event
   * @param organiserID the id of the organiser of the event
   */
  fun updateRating(eventID: String, newRating: Long, oldRating: Long, organiserID: String) {
    viewModelScope.launch {
      EventRepository.updateRating(eventID, newRating, currentUID!!, oldRating, organiserID)
    }
  }

  /**
   * Remove an event by its UID.
   *
   * @param eventID the ID of the event to remove
   * @param callback the function to call after the event has been removed (optional)
   */
  fun removeEvent(eventID: String, callback: () -> Unit = {}) {
    lastLoadedEvents = lastLoadedEvents.filter { it.eventID != eventID }
    viewModelScope.launch {
      val event = getEvent(eventID)
      if (event == null) {
        Log.e(TAG, "Event with ID $eventID couldn't be found to delete")
        return@launch
      }
      event.participants.forEach { participant -> EventRepository.leaveEvent(eventID, participant) }
      event.pendingParticipants.forEach { pendingParticipant ->
        cancelInvitation(event, pendingParticipant)
      }
      EventRepository.removeEvent(eventID)
      callback()
    }
  }

  /**
   * Update the event participants field by adding the given user to the list. Note that this
   * function should be called at the same time as the equivalent function in the UserViewModel.
   *
   * @param event the event to update
   * @param userId the ID of the user to add to the event
   */
  fun joinEvent(event: Event, userId: String) {
    if (event.participants.contains(userId)) {
      Log.w(TAG, "User $userId is already in event ${event.eventID}")
      return
    }
    editEvent(event.copy(participants = event.participants.plus(userId)))
  }

  /**
   * Update the event participants field by removing the given user to the list. Note that this
   * function should be called at the same time as the equivalent function in the UserViewModel.
   *
   * @param eventID the ID of the event to update
   * @param userId the ID of the user to remove from the event
   * @param callback the function to call after the user has left the event (optional)
   */
  fun leaveEvent(eventID: String, userId: String, callback: () -> Unit = {}) {
    viewModelScope.launch {
      EventRepository.leaveEvent(eventID, userId)
      callback()
    }
  }

  /**
   * Update the event pendingParticipants field by adding the given user to the list. Note that this
   * function should be called at the same time as the equivalent function in the UserViewModel.
   *
   * @param event the event to update
   * @param userId the ID of the user to add to the event
   */
  fun sendInvitation(event: Event, userId: String) {
    if (event.pendingParticipants.contains(userId)) {
      Log.w(TAG, "User $userId is already invited to event ${event.eventID}")
      return
    }

    EventRepository.updateEvent(
        event.copy(pendingParticipants = event.pendingParticipants.plus(userId)))
  }

  /**
   * Update the event pendingParticipants field by removing the given user to the list and adds the
   * given user to the participants list of the event. Note that this function should be called at
   * the same time as the equivalent function in the UserViewModel.
   *
   * @param event the event to update
   * @param userId the ID of the user to add to the event
   */
  fun acceptInvitation(event: Event, userId: String): Boolean {
    if (!event.pendingParticipants.contains(userId)) return false
    EventRepository.updateEvent(
        event.copy(pendingParticipants = event.pendingParticipants.minus(userId)))
    joinEvent(event, userId)
    return true
  }

  /**
   * Update the event pendingParticipants field by removing the given user from the list. Note that
   * this function should be called at the same time as the equivalent function in the
   * UserViewModel.
   *
   * @param event the event to update
   * @param userId the ID of the user to remove from the event
   */
  fun declineInvitation(event: Event, userId: String) {
    assert(event.pendingParticipants.contains(userId))
    EventRepository.updateEvent(
        event.copy(pendingParticipants = event.pendingParticipants.minus(userId)))
  }

  /**
   * Update the event participants field by removing the given user from the list. Note that this
   * function should be called at the same time as the equivalent function in the UserViewModel.
   *
   * @param event the event to update
   * @param userId the ID of the user to remove from the event
   */
  fun kickParticipant(event: Event, userId: String) {
    assert(event.participants.contains(userId))
    EventRepository.updateEvent(event.copy(participants = event.participants.minus(userId)))
  }

  /**
   * Update the event pendingParticipants field by removing the given user from the list. Note that
   * this function should be called at the same time as the equivalent function in the
   * UserViewModel.
   *
   * @param event the event to update
   * @param userId the ID of the user to remove from the event
   */
  fun cancelInvitation(event: Event, userId: String) {
    if (!event.pendingParticipants.contains(userId)) {
      Log.w(TAG, "Event ${event.eventID} doesn't have $userId as a pendingParticipant")
      return
    }

    EventRepository.updateEvent(
        event.copy(pendingParticipants = event.pendingParticipants.minus(userId)))
  }

  /**
   * Update the posts field of the event by removing the given post to the list.
   *
   * @param event the event to update
   * @param post the post to remove from the event
   */
  fun deletePost(event: Event, post: Post) {
    if (!event.posts.contains(post)) {
      Log.w(TAG, "Event ${event.eventID} doesn't have post ${post.title}")
      return
    }

    val updatedPosts = event.posts.minus(post)
    editEvent(event.copy(posts = updatedPosts))
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

  /** Companion object containing static methods. */
  companion object {
    /**
     * Sort the events depending on the user's preferences according to their tags.
     *
     * @param userTags the user's tags
     * @param eventsList the list of events to sort
     */
    fun sortEvents(
        userTags: List<String>,
        eventsList: MutableList<Event>,
    ) {

      // Only attempt to sort if user has tags
      if (userTags.isNotEmpty()) {
        val eventScoreList: MutableMap<String, Int> = mutableMapOf()
        eventsList.forEach { event ->
          val tagsInCommon = event.tags.intersect(userTags.toSet()).size
          eventScoreList[event.eventID] = tagsInCommon
        }
        eventsList.sortByDescending { eventScoreList[it.eventID] }

        Log.d(TAG, "Sort success")
      } else {
        Log.d(TAG, "User has no tags, no sorting done")
      }
    }
  }

  /** Events sorting enum, placed here because this is also where the sorting algorithm goes. */
  enum class SortOption {
    DEFAULT,
    ALPHABETICAL,
    DATE
  }
}

/**
 * ViewModel for the creation of an event. This viewModel is useful to store the participants that
 * are invited to an event that is in creation (i.e. not created yet).
 */
class EventCreationViewModel : ViewModel() {
  val title: MutableState<String> = mutableStateOf("")
  val description: MutableState<String> = mutableStateOf("")
  val location: MutableState<String> = mutableStateOf("")
  val price: MutableState<Double> = mutableDoubleStateOf(0.0)
  val url: MutableState<String> = mutableStateOf("")
  val pickedTime: MutableState<LocalTime> = mutableStateOf(LocalTime.now())
  val pickedDate: MutableState<LocalDate> = mutableStateOf(LocalDate.now())
  val invitedParticipants = mutableStateListOf<GoMeetUser>()
  val tags: MutableState<List<String>> = mutableStateOf(emptyList())
  val imageUri: MutableState<Uri?> = mutableStateOf(null)
  val imageBitmap: MutableState<ImageBitmap?> = mutableStateOf(null)
}
