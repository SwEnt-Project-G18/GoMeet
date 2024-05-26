package com.github.se.gomeet.ui.mainscreens.explore

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.time.LocalDate
import kotlinx.coroutines.launch

internal val defaultPosition = LatLng(46.51912357457158, 6.568023741881372)
internal const val defaultZoom = 16f

/**
 * Enum class to represent the different actions that can be taken by the camera:
 * - NO_ACTION: No action is taken.
 * - MOVE: The camera moves to a new location.
 * - ANIMATE: The camera animates to a new location.
 */
internal enum class CameraAction {
  NO_ACTION,
  MOVE,
  ANIMATE
}

internal val moveToCurrentLocation = mutableStateOf(CameraAction.NO_ACTION)

/**
 * The GoogleMapView composable displays a Google Map with custom pins for events.
 *
 * @param modifier The modifier of the map
 * @param currentPosition The current position of the user
 * @param content The content of the map
 * @param allEvents all Events of the App
 * @param events The events showed on the map
 * @param query The query for the search bar.
 * @param locationPermitted The location permission.
 * @param eventViewModel The event view model.
 * @param nav the nav controller
 */
@Composable
internal fun GoogleMapView(
    modifier: Modifier = Modifier,
    currentPosition: MutableState<LatLng>,
    moveToCurrentLocation: MutableState<CameraAction>,
    content: @Composable () -> Unit = {},
    allEvents: MutableState<List<Event>>,
    events: MutableState<List<Event>>,
    query: MutableState<String>,
    locationPermitted: Boolean,
    eventViewModel: EventViewModel,
    nav: NavigationActions
) {

  val ctx = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  val uiSettings by remember {
    mutableStateOf(
        MapUiSettings(
            compassEnabled = false,
            zoomControlsEnabled = false,
            myLocationButtonEnabled = true, // Enable default location button
            mapToolbarEnabled =
                false // Disable default map toolbar (might include the Google Maps button)
            ))
  }

  val isDarkTheme = isSystemInDarkTheme()

  val mapProperties by remember {
    mutableStateOf(
        MapProperties(
            mapType = MapType.NORMAL,
            isMyLocationEnabled = locationPermitted,
            mapStyleOptions =
                MapStyleOptions.loadRawResourceStyle(
                    ctx, if (isDarkTheme) R.raw.map_style_dark else R.raw.map_style_light)))
  }

  val mapVisible by remember { mutableStateOf(true) }

  val cameraPositionState = rememberCameraPositionState()

  val selectedLocation = remember { mutableStateOf<LatLng?>(null) }

  LaunchedEffect(moveToCurrentLocation.value, Unit) {
    if (moveToCurrentLocation.value == CameraAction.MOVE) {
      coroutineScope.launch {
        cameraPositionState.move(
            update =
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(currentPosition.value, defaultZoom)))
        moveToCurrentLocation.value = CameraAction.NO_ACTION
      }
    } else if (moveToCurrentLocation.value == CameraAction.ANIMATE) {
      coroutineScope.launch {
        cameraPositionState.animate(
            update =
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(currentPosition.value, defaultZoom)),
            durationMs = 1000)
        moveToCurrentLocation.value = CameraAction.NO_ACTION
      }
    }
  }

  val context = LocalContext.current

  LaunchedEffect(allEvents.value) {
    Log.d("ViewModel", "Loading custom pins for ${events.value.size} events.")
    eventViewModel.loadCustomPins(context, events.value)
  }

  val isLoading by eventViewModel.loading.observeAsState(false)

  LaunchedEffect(isLoading) {
    if (!isLoading) {
      Log.d("ViewModel", "Loading complete, map is now displayed.")
    }
  }
  LaunchedEffect(cameraPositionState.isMoving) {
    val visibleRegion = cameraPositionState.projection?.visibleRegion
    if (visibleRegion != null) {
      val bounds = LatLngBounds(visibleRegion.nearLeft, visibleRegion.farRight)
      events.value =
          allEvents.value.filter { event ->
            bounds.contains(LatLng(event.location.latitude, event.location.longitude))
          }
    }
  }

  if (mapVisible) {
    Box(Modifier.fillMaxSize()) {
      if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
      } else {
        GoogleMap(
            modifier = modifier,
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = uiSettings,
            onMapLoaded = {
              val visibleRegion = cameraPositionState.projection?.visibleRegion
              if (visibleRegion != null) {
                val bounds = LatLngBounds(visibleRegion.nearLeft, visibleRegion.farRight)
                events.value =
                    allEvents.value.filter { e ->
                      bounds.contains(LatLng(e.location.latitude, e.location.longitude))
                    }
              }
            },
            onMapClick = { selectedLocation.value = null },
            onPOIClick = {}) {
              val markerClick: (LatLng) -> Boolean = { location ->
                selectedLocation.value = location
                false
              }
              allEvents.value.forEach { event ->
                val today = LocalDate.now()
                val oneWeekLater = today.plusWeeks(1)

                val isEventThisWeek =
                    event.date.isAfter(today.minusDays(1)) &&
                        event.date.isBefore(oneWeekLater.plusDays(1))

                val stablePins = remember { eventViewModel.bitmapDescriptors }
                val originalBitmap =
                    BitmapFactory.decodeResource(context.resources, R.drawable.default_pin)

                val desiredWidth = 94
                val desiredHeight = 140

                val scaledBitmap =
                    Bitmap.createScaledBitmap(originalBitmap, desiredWidth, desiredHeight, true)

                val scaledPin = BitmapDescriptorFactory.fromBitmap(scaledBitmap)

                val customPinBitmapDescriptor =
                    if (isEventThisWeek) stablePins[event.eventID] else scaledPin
                val markerState =
                    MarkerState(
                        position = LatLng(event.location.latitude, event.location.longitude))

                MarkerInfoWindow(
                    state = markerState,
                    title = event.title,
                    icon =
                        customPinBitmapDescriptor
                            ?: BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_RED),
                    onClick = { markerClick(markerState.position) },
                    onInfoWindowClick = {
                      nav.navigateToEventInfo(
                          eventId = event.eventID,
                          title = event.title,
                          date = event.getDateString(),
                          time = event.getTimeString(),
                          description = event.description,
                          organizer = event.creator,
                          loc = LatLng(event.location.latitude, event.location.longitude),
                          rating = 0L // TODO: replace with actual rating
                          // TODO: add image
                          )
                    },
                    visible = event.title.contains(query.value, ignoreCase = true)) {
                      Box(
                          modifier =
                              Modifier.width((LocalConfiguration.current.screenWidthDp * 0.5).dp)
                                  .padding(10.dp)
                                  .clickable {
                                    nav.navigateToEventInfo(
                                        eventId = event.eventID,
                                        title = event.title,
                                        date = event.getDateString(),
                                        time = event.getTimeString(),
                                        description = event.description,
                                        organizer = event.creator,
                                        loc =
                                            LatLng(
                                                event.location.latitude, event.location.longitude),
                                        rating = 0L // TODO: replace with actual rating
                                        // TODO: add image
                                        )
                                  }
                                  .border(
                                      3.dp,
                                      MaterialTheme.colorScheme.outlineVariant,
                                      RoundedCornerShape(10.dp))
                                  .background(
                                      MaterialTheme.colorScheme.primaryContainer,
                                      RoundedCornerShape(10.dp))) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(15.dp),
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                  Column(horizontalAlignment = Alignment.Start) {
                                    Text(
                                        text =
                                            if (event.title.length > 37)
                                                event.title.take(33) + "...."
                                            else event.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onBackground)
                                    Text(
                                        text = event.momentToString(),
                                        style =
                                            MaterialTheme.typography
                                                .bodyMedium, // Smaller text style
                                        color = MaterialTheme.colorScheme.onBackground)
                                  }
                                  Icon(
                                      Icons.Outlined.Info,
                                      contentDescription = "",
                                      tint = MaterialTheme.colorScheme.outlineVariant,
                                      modifier = Modifier.size(25.dp))
                                }
                          }
                    }
              }
            }
        content()
      }

      // Custom button to open Google Maps, visible only when a pin is selected
      // Custom button to open Google Maps, visible only when a pin is selected
      selectedLocation.value?.let { location ->
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.BottomEnd) {
              FloatingActionButton(
                  onClick = {
                    val gmmIntentUri =
                        Uri.parse("geo:0,0?q=${location.latitude},${location.longitude}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    ctx.startActivity(mapIntent)
                  },
                  containerColor = MaterialTheme.colorScheme.outlineVariant,
                  shape = RoundedCornerShape(10.dp),
                  modifier =
                      Modifier.offset(
                          y = (-210).dp) // Adjust this value to position the button higher
                  ) {
                    Icon(
                        ImageVector.vectorResource(R.drawable.direction_icon),
                        contentDescription = "Open in Google Maps",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp))
                  }
            }
      }

      if (selectedLocation.value == null) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.BottomEnd) {
              FloatingActionButton(
                  onClick = { moveToCurrentLocation.value = CameraAction.ANIMATE },
                  containerColor = MaterialTheme.colorScheme.outlineVariant,
                  shape = RoundedCornerShape(10.dp),
                  modifier =
                      Modifier.offset(
                          y = (-210).dp) // Adjust this value to position the button higher
                  ) {
                    Icon(
                        ImageVector.vectorResource(R.drawable.location_icon),
                        contentDescription = "Center on Current Location",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp))
                  }
            }
      }
    }
  }
}
