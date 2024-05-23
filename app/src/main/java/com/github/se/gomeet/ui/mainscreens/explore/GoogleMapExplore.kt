package com.github.se.gomeet.ui.mainscreens.explore

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.ui.mainscreens.LoadingText
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.EventViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
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
internal val isButtonVisible = mutableStateOf(true)

/**
 * The GoogleMapView composable displays a Google Map with custom pins for events.
 *
 * @param modifier The modifier.
 * @param currentPosition The current position.
 * @param onMapLoaded The callback when the map is loaded.
 * @param content The content.
 * @param events The events.
 * @param query The query for the search bar.
 * @param locationPermitted The location permission.
 * @param eventViewModel The event view model.
 */
@Composable
internal fun GoogleMapView(
    modifier: Modifier = Modifier,
    currentPosition: MutableState<LatLng>,
    onMapLoaded: () -> Unit = {},
    content: @Composable () -> Unit = {},
    events: MutableState<List<Event>>,
    query: MutableState<String>,
    locationPermitted: Boolean,
    eventViewModel: EventViewModel,
    nav: NavigationActions
) {

  val ctx = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  val eventLocations =
      events.value.map { event -> LatLng(event.location.latitude, event.location.longitude) }
  val eventStates = eventLocations.map { location -> rememberMarkerState(position = location) }

  val uiSettings by remember {
    mutableStateOf(
        MapUiSettings(
            compassEnabled = false, zoomControlsEnabled = false, myLocationButtonEnabled = false))
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

  LaunchedEffect(events.value) {
    Log.d("ViewModel", "Loading custom pins for ${events.value.size} events.")
    eventViewModel.loadCustomPins(context, events.value)
  }

  val isLoading by eventViewModel.loading.observeAsState(false)

  LaunchedEffect(isLoading) {
    if (!isLoading) {
      Log.d("ViewModel", "Loading complete, map is now displayed.")
    }
  }

  if (mapVisible) {
    Box(Modifier.fillMaxSize()) {
      if (isLoading) {
        LoadingText()
      } else {
        GoogleMap(
            modifier = modifier,
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = uiSettings,
            onMapLoaded = onMapLoaded,
            onMapClick = { isButtonVisible.value = true },
            onPOIClick = {}) {
              val markerClick: (Marker) -> Boolean = {
                isButtonVisible.value = false
                false
              }

              events.value.forEachIndexed { index, event ->
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

                MarkerInfoWindowContent(
                    state = eventStates[index],
                    title = event.title,
                    icon = customPinBitmapDescriptor,
                    onClick = markerClick,
                    onInfoWindowClick = {
                      nav.navigateToEventInfo(
                          eventId = event.eventID,
                          title = event.title,
                          date = event.getDateString(),
                          time = event.getTimeString(),
                          description = event.description,
                          organizer = event.creator,
                          loc = LatLng(event.location.latitude, event.location.longitude),
                          rating = event.eventRatings[eventViewModel.currentUID!!] ?: 0,
                          // TODO: add image
                      )
                    },
                    visible = event.title.contains(query.value, ignoreCase = true)) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.padding(20.dp)) {
                          Text(
                              event.title,
                              color = MaterialTheme.colorScheme.secondary,
                              style = MaterialTheme.typography.titleMedium)
                          Text(
                              event.getDateString(),
                              color = MaterialTheme.colorScheme.secondary,
                              style = MaterialTheme.typography.bodyMedium)
                        }
                        Icon(
                            imageVector = Icons.Filled.Info,
                            tint = MaterialTheme.colorScheme.secondary,
                            contentDescription = "See More",
                            modifier = Modifier.padding(end = 20.dp))
                      }
                    }
              }
            }
        content()
      }
    }
  }
}
