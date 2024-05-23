package com.github.se.gomeet.ui.mainscreens.explore

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
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
@OptIn(ExperimentalMaterialApi::class)
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
  Scaffold(bottomBar = { BottomNavigationFun(nav) }, modifier = Modifier.testTag("ExploreUI")) {
      innerPadding ->
    val backdropState = rememberBackdropScaffoldState(BackdropValue.Concealed)
    LaunchedEffect(backdropState) { backdropState.reveal() }
    val offset by backdropState.offset
    val halfHeight = (LocalConfiguration.current.screenHeightDp - 80) / 4
    val halfHeightPx = with(LocalDensity.current) { halfHeight.dp.toPx() }
    val rowAlpha = (offset / halfHeightPx).coerceIn(0f..1f)

    Box(
        modifier =
            Modifier.fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding() * rowAlpha)) {
          BackdropScaffold(
              frontLayerBackgroundColor = MaterialTheme.colorScheme.background,
              backLayerBackgroundColor = MaterialTheme.colorScheme.background,
              backLayerContentColor = MaterialTheme.colorScheme.background,
              scaffoldState = backdropState,
              frontLayerScrimColor = Color.Unspecified,
              headerHeight = halfHeight.dp,
              peekHeight = 0.dp,
              modifier = Modifier.testTag("MapSlider").padding(innerPadding),
              appBar = {},
              frontLayerContent = {
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                  val listState = rememberLazyListState()
                  Spacer(modifier = Modifier.height(8.dp))
                  Box(modifier = Modifier.fillMaxSize()) {
                    ContentInRow(
                        backdropState = backdropState,
                        halfHeightPx = halfHeightPx,
                        listState = listState,
                        eventList = eventList,
                        nav = nav,
                        eventViewModel.currentUID!!)

                    ContentInColumn(
                        backdropState = backdropState,
                        halfHeightPx = halfHeightPx,
                        listState = listState,
                        eventList = eventList,
                        nav = nav,
                        eventViewModel.currentUID)
                  }
                }
              },
              backLayerContent = {
                Scaffold(
                    modifier = Modifier.fillMaxSize().alpha(offset / halfHeightPx),
                    floatingActionButton = {
                      if (locationPermitted.value == true && isButtonVisible.value) {
                        FloatingActionButton(
                            onClick = { moveToCurrentLocation.value = CameraAction.ANIMATE },
                            modifier = Modifier.size(45.dp).testTag("CurrentLocationButton"),
                            containerColor = MaterialTheme.colorScheme.outlineVariant) {
                              Icon(
                                  imageVector =
                                      ImageVector.vectorResource(R.drawable.location_icon),
                                  contentDescription = null,
                                  tint = Color.White)
                            }
                      }
                    },
                    bottomBar = {}) { innerPadding ->
                      if (isMapLoaded) {
                        moveToCurrentLocation.value = CameraAction.MOVE

                        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                          GoogleMapView(
                              currentPosition = currentPosition,
                              events = eventList,
                              modifier = Modifier.testTag("Map"),
                              query = query,
                              locationPermitted = locationPermitted.value!!,
                              eventViewModel = eventViewModel,
                              nav = nav)
                        }
                      } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center) {
                              CircularProgressIndicator()
                            }
                      }

                      val isDarkTheme = isSystemInDarkTheme()
                      val backgroundColor =
                          if (isDarkTheme) {
                            MaterialTheme.colorScheme.primaryContainer
                          } else {
                            MaterialTheme.colorScheme.background
                          }

                      SearchModule(
                          nav = nav,
                          backgroundColor = backgroundColor,
                          contentColor = MaterialTheme.colorScheme.tertiary,
                          currentUID = eventViewModel.currentUID!!)
                    }
              }) {}
        }
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
