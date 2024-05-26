package com.github.se.gomeet.ui.mainscreens.explore

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.core.content.ContextCompat
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.ui.mainscreens.LoadingText
import com.github.se.gomeet.ui.mainscreens.SearchModule
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.viewmodel.EventViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * The Explore screen displays a map with events and a search bar.
 *
 * @param nav The navigation actions.
 * @param eventViewModel The event view model.
 */
@Composable
fun Explore(nav: NavigationActions, eventViewModel: EventViewModel) {
  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current

  val locationPermitted: MutableState<Boolean?> = remember { mutableStateOf(null) }
  val locationPermissionsAlreadyGranted =
      (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
          PackageManager.PERMISSION_GRANTED) ||
          (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
              PackageManager.PERMISSION_GRANTED)
  val locationPermissions =
      arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
  val locationPermissionLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.RequestMultiplePermissions(),
          onResult = { permissions ->
            when {
              permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                locationPermitted.value = true
              }
              permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                locationPermitted.value = true
              }
              else -> {
                locationPermitted.value = false
              }
            }
          })
  val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
  val nonFilteredEvents = remember { mutableStateOf<List<Event>>(emptyList()) }
  val eventList = remember { mutableStateOf<List<Event>>(emptyList()) }
  val query = remember { mutableStateOf("") }
  val currentPosition = remember { mutableStateOf(defaultPosition) }
  var isMapLoaded by remember { mutableStateOf(false) }
  LaunchedEffect(Unit) {
    if (locationPermissionsAlreadyGranted) {
      locationPermitted.value = true
    } else {
      locationPermissionLauncher.launch(locationPermissions)
    }

    val allEvents = eventViewModel.getAllEvents()
    if (allEvents != null) {
      eventList.value = allEvents.filter { e -> !e.isPastEvent() }
      nonFilteredEvents.value = allEvents.filter { e -> !e.isPastEvent() }
    }

    // wait for user input
    while (locationPermitted.value == null) {
      delay(100)
    }

    while (true) {
      coroutineScope.launch {
        if (locationPermitted.value == true) {
          val priority = PRIORITY_BALANCED_POWER_ACCURACY
          val result =
              locationClient
                  .getCurrentLocation(
                      priority,
                      CancellationTokenSource().token,
                  )
                  .await()
          result?.let { fetchedLocation ->
            currentPosition.value = LatLng(fetchedLocation.latitude, fetchedLocation.longitude)
            isMapLoaded = true
          }
        } else if (locationPermitted.value == false) {
          isMapLoaded = true
        }
      }
      delay(5000) // map is updated every 5s
    }
  }
  Scaffold(
      bottomBar = { BottomNavigationFun(nav) },
      modifier = Modifier.fillMaxSize().testTag("ExploreUI"),
  ) { innerPadding ->
    if (isMapLoaded) {
      moveToCurrentLocation.value = CameraAction.MOVE
      Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
        val moveToCurrentLocation = remember { mutableStateOf(CameraAction.NO_ACTION) }
        GoogleMapView(
            currentPosition = currentPosition,
            allEvents = nonFilteredEvents,
            moveToCurrentLocation = moveToCurrentLocation,
            events = eventList,
            modifier = Modifier.testTag("Map"),
            query = query,
            locationPermitted = locationPermitted.value!!,
            eventViewModel = eventViewModel,
            nav = nav)
        Column(
            modifier = Modifier.fillMaxHeight().testTag("MapSlider"),
            verticalArrangement = Arrangement.Bottom) {
              ContentInRow(
                  listState = rememberLazyListState(),
                  eventList = eventList,
                  nav = nav,
              )
            }
      }
    } else {
      LoadingText()
    }

    SearchModule(
        nav = nav,
        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.tertiary,
        currentUID = eventViewModel.currentUID!!,
    )
  }
}

@Composable
private fun BottomNavigationFun(nav: NavigationActions) {

  BottomNavigationMenu(
      onTabSelect = { selectedTab ->
        if (selectedTab != "Explore") {
          nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
        }
      },
      tabList = TOP_LEVEL_DESTINATIONS,
      selectedItem = Route.EXPLORE)
}
