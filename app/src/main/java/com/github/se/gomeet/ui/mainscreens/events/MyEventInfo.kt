package com.github.se.gomeet.ui.mainscreens.events

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.mainscreens.LoadingText
import com.github.se.gomeet.ui.mainscreens.events.posts.AddPost
import com.github.se.gomeet.ui.mainscreens.events.posts.EventPost
import com.github.se.gomeet.ui.mainscreens.profile.shareImage
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.theme.White
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch

/**
 * Composable function to display the details of an event.
 *
 * @param nav NavigationActions object to handle navigation
 * @param title Title of the event
 * @param eventId ID of the event
 * @param date Date of the event
 * @param time Time of the event
 * @param organizerId ID of the organizer of the event
 * @param rating Rating of the event
 * @param description Description of the event
 * @param loc Location of the event
 * @param userViewModel UserViewModel object to interact with user data
 * @param eventViewModel EventViewModel object to interact with event data
 */
@Composable
fun MyEventInfo(
    nav: NavigationActions,
    title: String = "",
    eventId: String = "",
    date: String = "",
    time: String = "",
    organizerId: String,
    rating: Double = 0.0, // TODO: Implement rating system
    description: String = "",
    loc: LatLng = LatLng(0.0, 0.0),
    userViewModel: UserViewModel,
    eventViewModel: EventViewModel
) {
  var addPost by remember { mutableStateOf(false) }
  val organizer = remember { mutableStateOf<GoMeetUser?>(null) }
  val currentUser = remember { mutableStateOf<GoMeetUser?>(null) }
  val myEvent = remember { mutableStateOf<Event?>(null) }
  val coroutineScope = rememberCoroutineScope()
  val screenHeight = LocalConfiguration.current.screenHeightDp.dp
  var expanded by remember { mutableStateOf(false) }
  var showShareEventDialog by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    coroutineScope.launch {
      organizer.value = userViewModel.getUser(organizerId)
      currentUser.value = userViewModel.getUser(Firebase.auth.currentUser!!.uid)
      myEvent.value = eventViewModel.getEvent(eventId)
    }
  }

  Log.d("EventInfo", "Organizer is $organizerId")
  Scaffold(
      topBar = {
        TopAppBar(
            modifier = Modifier.testTag("TopBar"),
            backgroundColor = MaterialTheme.colorScheme.background,
            elevation = 0.dp,
            title = {
              // Empty title since we're placing our own components
            },
            navigationIcon = {
              IconButton(onClick = { nav.goBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground)
              }
            },
            actions = {
              IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "More",
                    modifier = Modifier.rotate(90f),
                    tint = MaterialTheme.colorScheme.onBackground)
              }
              DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text("Share Event", modifier = Modifier.padding(10.dp)) },
                    onClick = {
                      expanded = false
                      showShareEventDialog = true
                    })
              }
            })
      },
      bottomBar = {
        // Your bottom bar content
      }) { innerPadding ->
        if (organizer.value == null || currentUser.value == null || myEvent.value == null) {
          LoadingText()
        } else {
          Column(
              modifier =
                  Modifier.padding(innerPadding)
                      .padding(horizontal = 10.dp)
                      .fillMaxSize()
                      .verticalScroll(state = rememberScrollState())) {
                EventHeader(
                    title = title,
                    currentUser = currentUser.value!!,
                    organizer = organizer.value!!,
                    rating = rating,
                    nav = nav,
                    date = date,
                    time = time)

                Spacer(modifier = Modifier.height(20.dp))
                EventButtons(
                    currentUser.value!!,
                    organizer.value!!,
                    eventId,
                    userViewModel,
                    eventViewModel,
                    nav)

                var imageUrl by remember { mutableStateOf<String?>(null) }
                LaunchedEffect(eventId) { imageUrl = eventViewModel.getEventImageUrl(eventId) }
                EventImage(imageUrl = imageUrl)
                Spacer(modifier = Modifier.height(20.dp))

                EventDescription(text = description)
                Spacer(modifier = Modifier.height(20.dp))
                MapViewComposable(loc = loc)
                if (addPost) {
                  Spacer(modifier = Modifier.height(screenHeight / 80))
                  HorizontalDivider(
                      thickness = 5.dp, color = MaterialTheme.colorScheme.primaryContainer)
                  Spacer(modifier = Modifier.height(screenHeight / 80))
                  AddPost(
                      callbackCancel = { addPost = false },
                      callbackPost = { post ->
                        addPost = false
                        coroutineScope.launch {
                          eventViewModel.editEvent(
                              myEvent.value!!.copy(
                                  posts = myEvent.value!!.posts.reversed().plus(post).reversed()))
                          myEvent.value = eventViewModel.getEvent(eventId)
                        }
                      },
                      user = currentUser.value!!,
                      userViewModel = userViewModel)
                }
                Spacer(Modifier.height(screenHeight / 80))
                HorizontalDivider(
                    thickness = 5.dp, color = MaterialTheme.colorScheme.primaryContainer)
                Spacer(Modifier.height(screenHeight / 80))
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Top) {
                      Text(
                          text = "Posts",
                          color = MaterialTheme.colorScheme.tertiary,
                          style =
                              MaterialTheme.typography.headlineSmall.copy(
                                  fontWeight = FontWeight.SemiBold))

                      Spacer(modifier = Modifier.weight(1f))
                      if (!addPost && organizer.value!!.uid == currentUser.value!!.uid) {
                        Button(
                            onClick = { addPost = true },
                            shape = RoundedCornerShape(10.dp),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.outlineVariant)) {
                              Icon(Icons.Filled.Add, contentDescription = "Add Post", tint = White)
                            }
                      }
                    }

                if (myEvent.value!!.posts.isEmpty()) {
                  Spacer(Modifier.height(10.dp))
                  Text(
                      text = "No updates about this event at the moment.",
                      modifier = Modifier.align(Alignment.CenterHorizontally),
                      style = MaterialTheme.typography.bodyLarge)
                  Spacer(Modifier.height(screenHeight / 50))
                } else {
                  Spacer(Modifier.height(10.dp))
                  myEvent.value!!.posts.forEach { post ->
                    key(post.date.toString() + post.time.toString()) {
                      EventPost(
                          nav = nav,
                          event = myEvent.value!!,
                          post = post,
                          userViewModel = userViewModel,
                          eventViewModel = eventViewModel,
                          currentUser = currentUser.value!!.uid)
                      HorizontalDivider(color = MaterialTheme.colorScheme.primaryContainer)
                      Spacer(Modifier.height(10.dp))
                    }
                  }
                }
                Spacer(Modifier.height(screenHeight / 10))
              }
        }
      }

  if (showShareEventDialog) {
    ShareEventDialog(
        uid =
            "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d0/QR_code_for_mobile_English_Wikipedia.svg/1200px-QR_code_for_mobile_English_Wikipedia.svg.png", // Replace with actual QR code URL
        onDismiss = { showShareEventDialog = false })
  }
}

@Composable
fun ShareEventDialog(uid: String, onDismiss: () -> Unit) {
  val painter = rememberAsyncImagePainter(uid)
  val context = LocalContext.current
  AlertDialog(
      containerColor = MaterialTheme.colorScheme.background,
      onDismissRequest = onDismiss,
      icon = {
        Column {
          Row {
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { onDismiss() }) {
              Icon(
                  Icons.Filled.Close,
                  contentDescription = "Close",
                  tint = MaterialTheme.colorScheme.tertiary,
                  modifier = Modifier.size(30.dp))
            }
          }
          Image(
              painter = painter,
              contentDescription = "QR Code",
              modifier = Modifier.fillMaxWidth().background(Color.White),
              contentScale = ContentScale.Fit)
        }
      },
      confirmButton = {
        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.outlineVariant),
            onClick = { shareImage(context, painter) }) {
              Text("Share", color = White)
            }
      })
}

/**
 * Helper function to display the map view of an event.
 *
 * @param loc Location of the event
 * @param zoomLevel Zoom level of the map
 */
@Composable
private fun MapViewComposable(
    loc: LatLng,
    zoomLevel: Float = 15f // Default zoom level for close-up of location
) {
  val ctx = LocalContext.current
  val isDarkTheme = isSystemInDarkTheme()
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(loc, zoomLevel)
  }
  val markerState = rememberMarkerState(position = loc)

  val uiSettings by remember {
    mutableStateOf(
        MapUiSettings(
            compassEnabled = false, zoomControlsEnabled = false, myLocationButtonEnabled = false))
  }
  val mapProperties by remember {
    mutableStateOf(
        MapProperties(
            mapType = MapType.NORMAL,
            mapStyleOptions =
                MapStyleOptions.loadRawResourceStyle(
                    ctx, if (isDarkTheme) R.raw.map_style_dark else R.raw.map_style_light)))
  }

  // Load custom pin bitmap
  val originalBitmap = BitmapFactory.decodeResource(ctx.resources, R.drawable.default_pin)
  val desiredWidth = 94
  val desiredHeight = 140
  val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, desiredWidth, desiredHeight, true)
  val customPin = BitmapDescriptorFactory.fromBitmap(scaledBitmap)

  // Set up the GoogleMap composable
  GoogleMap(
      modifier =
          Modifier.testTag("MapView").fillMaxWidth().height(200.dp).clip(RoundedCornerShape(20.dp)),
      cameraPositionState = cameraPositionState,
      properties = mapProperties,
      uiSettings = uiSettings) {
        Marker(state = markerState, icon = customPin)
      }

  // Initialize the map position once and avoid resetting on recomposition
  DisposableEffect(loc) {
    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(loc, zoomLevel))
    onDispose {}
  }

  DisposableEffect(loc) {
    markerState.position = loc
    onDispose {}
  }
}
