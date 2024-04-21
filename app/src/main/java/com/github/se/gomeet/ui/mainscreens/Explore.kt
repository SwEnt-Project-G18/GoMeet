package com.github.se.gomeet.ui.mainscreens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.core.content.ContextCompat
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.viewmodel.EventViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private var currentPosition: LatLng = LatLng(0.0, 0.0)

@Composable
fun Explore(nav: NavigationActions, eventViewModel: EventViewModel) {
  val coroutineScope = rememberCoroutineScope()
  var isMapLoaded by remember { mutableStateOf(false) }
  var eventList = remember { mutableListOf<Event>() }
  val query = remember { mutableStateOf("") }
  val context = LocalContext.current

  var locationPermitted by remember { mutableStateOf(false) }
  val locationPermissionsAlreadyGranted =
      ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
          PackageManager.PERMISSION_GRANTED
  val locationPermissions =
      arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
  val locationPermissionLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.RequestMultiplePermissions(),
          onResult = { permissions ->
            locationPermitted =
                permissions.values.reduce { acc, isPermissionGranted -> acc && isPermissionGranted }
          })
  val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

  LaunchedEffect(Unit) {
    coroutineScope.launch {
      if (locationPermissionsAlreadyGranted) {
        locationPermitted = true
      } else {
        locationPermissionLauncher.launch(locationPermissions)
      }

      val allEvents = eventViewModel.getAllEvents()
      if (allEvents != null) {
        eventList.addAll(allEvents)
      }

      if (locationPermitted) {
        val priority = PRIORITY_BALANCED_POWER_ACCURACY
        val result =
            locationClient
                .getCurrentLocation(
                    priority,
                    CancellationTokenSource().token,
                )
                .await()
        result?.let { fetchedLocation ->
          currentPosition = LatLng(fetchedLocation.latitude, fetchedLocation.longitude)
        }
      }
      isMapLoaded = true
    }
  }

  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { selectedTab ->
              nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
            },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = Route.EXPLORE)
      }) { innerPadding ->
        if (isMapLoaded) {
          Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            GoogleMapView(
                events = eventList,
                modifier = Modifier.testTag("Map"),
                query = query,
                locationPermitted = locationPermitted)
          }
        } else {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
          }
        }
        SearchBar(query, Color.White)
      }
}

@SuppressLint("MissingPermission")
@Composable
fun GoogleMapView(
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState = rememberCameraPositionState {
      position = CameraPosition.fromLatLngZoom(currentPosition, 16f)
    },
    onMapLoaded: () -> Unit = {},
    content: @Composable () -> Unit = {},
    events: List<Event>,
    query: MutableState<String>,
    locationPermitted: Boolean
) {
  val locations = events.map { event -> LatLng(event.location.latitude, event.location.longitude) }
  val states = locations.map { location -> rememberMarkerState(position = location) }
  val uiSettings by remember {
    mutableStateOf(MapUiSettings(compassEnabled = false, zoomControlsEnabled = false))
  }
  val mapProperties by remember {
    mutableStateOf(MapProperties(mapType = MapType.NORMAL, isMyLocationEnabled = locationPermitted))
  }
  val mapVisible by remember { mutableStateOf(true) }

  if (mapVisible) {
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = mapProperties,
        uiSettings = uiSettings,
        onMapLoaded = onMapLoaded,
        onPOIClick = {}) {
          val markerClick: (Marker) -> Boolean = { false }

          for (i in states.indices) {
            MarkerInfoWindowContent(
                state = states[i],
                title = events[i].title,
                icon =
                    BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_RED), // TODO: change this
                onClick = markerClick,
                visible =
                    events[i]
                        .title
                        .contains(
                            query.value,
                            ignoreCase =
                                true) // maybe it would also make sense to be able to search for
                // creators or tags ?
                ) {
                  Text(it.title!!, color = Color.Black)
                }
          }
          content()
        }
  }
}
