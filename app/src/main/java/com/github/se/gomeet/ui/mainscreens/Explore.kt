package com.github.se.gomeet.ui.mainscreens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropScaffoldState
import androidx.compose.material.BackdropValue
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.ui.mainscreens.events.GoMeetSearchBar
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.viewmodel.EventViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private val defaultPosition = LatLng(46.51912357457158, 6.568023741881372)
private const val defaultZoom = 16f

/**
 * Enum class to represent the different actions that can be taken by the camera:
 * - NO_ACTION: No action is taken.
 * - MOVE: The camera moves to a new location.
 * - ANIMATE: The camera animates to a new location.
 */
private enum class CameraAction {
  NO_ACTION,
  MOVE,
  ANIMATE
}

private val moveToCurrentLocation = mutableStateOf(CameraAction.NO_ACTION)
private val isButtonVisible = mutableStateOf(true)

/**
 * The Explore screen displays a map with events and a search bar.
 *
 * @param nav The navigation actions.
 * @param eventViewModel The event view model.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
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
      eventList.value = allEvents
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
  Scaffold(bottomBar = { BottomNavigationFun(nav) }) { innerPadding ->
    val backdropState = rememberBackdropScaffoldState(BackdropValue.Concealed)
    LaunchedEffect(backdropState) { backdropState.reveal() }
    val offset by backdropState.offset
    val halfHeight = (LocalConfiguration.current.screenHeightDp - 80) / 3
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
                        eventList = eventList)

                    ContentInColumn(
                        backdropState = backdropState,
                        halfHeightPx = halfHeightPx,
                        listState = listState,
                        eventList = eventList)
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
                            containerColor = DarkCyan) {
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
                              eventViewModel = eventViewModel)
                        }
                      } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center) {
                              CircularProgressIndicator()
                            }
                      }
                      GoMeetSearchBar(
                          nav,
                          query,
                          MaterialTheme.colorScheme.background,
                          MaterialTheme.colorScheme.tertiary)
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ContentInColumn(
    backdropState: BackdropScaffoldState,
    halfHeightPx: Float,
    listState: LazyListState,
    eventList: MutableState<List<Event>>
) {
  val offset by backdropState.offset

  val columnAlpha = ((halfHeightPx - offset) / halfHeightPx).coerceIn(0f..1f)
  val events = eventList.value
  if (columnAlpha > 0) {
    Column {
      TopTitle(forColumn = true, alpha = columnAlpha)

      LazyColumn(modifier = Modifier.alpha(columnAlpha), state = listState) {
        itemsIndexed(events) { _, event ->
          Column {
            Card(
                elevation = 4.dp,
                modifier =
                    Modifier.size(width = 360.dp, height = 200.dp).padding(8.dp).clickable {}) {
                  val painter: Painter =
                      if (event.images.isNotEmpty()) {
                        rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(data = event.images[0])
                                .apply(
                                    block =
                                        fun ImageRequest.Builder.() {
                                          crossfade(true)
                                          placeholder(R.drawable.gomeet_logo)
                                        })
                                .build())
                      } else {
                        painterResource(id = R.drawable.gomeet_logo)
                      }
                  Image(
                      painter = painter,
                      contentDescription = "",
                      modifier = Modifier.fillMaxSize(),
                      alignment = Alignment.Center,
                      contentScale = ContentScale.Crop)
                }
            Spacer(Modifier.height(8.dp))
            Column(modifier = Modifier.padding(8.dp)) {
              Text(
                  text = event.title,
                  style = MaterialTheme.typography.bodyLarge,
                  color = MaterialTheme.colorScheme.tertiary)
              Text(
                  text =
                      eventDateToString(
                          Date.from(event.date.atStartOfDay(ZoneId.systemDefault()).toInstant())),
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.tertiary)
            }
          }
          Divider(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp))
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContentInRow(
    backdropState: BackdropScaffoldState,
    halfHeightPx: Float,
    listState: LazyListState,
    eventList: MutableState<List<Event>>
) {

  val screenHeight = LocalConfiguration.current.screenHeightDp.dp
  val offset by backdropState.offset
  val rowAlpha = (offset / halfHeightPx).coerceIn(0f..1f)
  val events = eventList.value
  if (rowAlpha > 0) {
    Column {
      TopTitle(forColumn = false, alpha = rowAlpha)
      LazyRow(modifier = Modifier.alpha(rowAlpha), state = listState) {
        itemsIndexed(events) { _, event ->
          Column(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
            Card(
                elevation = 4.dp,
                modifier = Modifier.size(width = 280.dp, height = screenHeight / 4).clickable {}) {
                  val painter: Painter =
                      if (event.images.isNotEmpty()) {
                        rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(data = event.images[0])
                                .apply(
                                    block =
                                        fun ImageRequest.Builder.() {
                                          crossfade(true)
                                          placeholder(R.drawable.gomeet_logo)
                                        })
                                .build())
                      } else {
                        painterResource(id = R.drawable.gomeet_logo)
                      }
                  Image(
                      painter = painter,
                      contentDescription = "",
                      alignment = Alignment.Center,
                      contentScale = ContentScale.Crop)
                }
            Column(modifier = Modifier.padding(8.dp)) {
              Text(
                  text = event.title,
                  style = MaterialTheme.typography.bodyLarge,
                  color = MaterialTheme.colorScheme.tertiary)
              Text(
                  text =
                      eventDateToString(
                          Date.from(event.date.atStartOfDay(ZoneId.systemDefault()).toInstant())),
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.tertiary)
            }
          }
        }
      }
    }
  }
}

fun eventDateToString(eventDate: Date): String {

  val currentDate = Calendar.getInstance()
  val startOfWeek = currentDate.clone() as Calendar
  startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.firstDayOfWeek)
  val endOfWeek = startOfWeek.clone() as Calendar
  endOfWeek.add(Calendar.DAY_OF_WEEK, 6)

  val eventCalendar = Calendar.getInstance().apply { time = eventDate }

  val isThisWeek = eventCalendar.after(currentDate) && eventCalendar.before(endOfWeek)
  val isToday =
      currentDate.get(Calendar.YEAR) == eventCalendar.get(Calendar.YEAR) &&
          currentDate.get(Calendar.DAY_OF_YEAR) == eventCalendar.get(Calendar.DAY_OF_YEAR)

  val dayFormat =
      if (isThisWeek) {
        SimpleDateFormat("EEEE", Locale.getDefault())
      } else {
        SimpleDateFormat("dd/MM/yy", Locale.getDefault())
      }

  val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

  val dayString =
      if (isToday) {
        "Today"
      } else {
        dayFormat.format(eventDate)
      }
  val timeString = timeFormat.format(eventDate)
  return "$dayString at $timeString"
}

@Composable
private fun TopTitle(forColumn: Boolean, alpha: Float) {
  Column(
      modifier =
          Modifier.padding(
                  top = if (forColumn) 34.dp else 12.dp,
                  start = 10.dp) // status bar 24dp in material guidance
              .alpha(alpha = alpha)
              .fillMaxWidth()) {
        Box(
            modifier =
                Modifier.size(width = 48.dp, height = 3.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .background(color = Color.LightGray)
                    .align(alignment = Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(LocalConfiguration.current.screenHeightDp.dp / 80))
        Text(text = "Trending Around You", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(LocalConfiguration.current.screenHeightDp.dp / 80))
      }
}

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
fun GoogleMapView(
    modifier: Modifier = Modifier,
    currentPosition: MutableState<LatLng>,
    onMapLoaded: () -> Unit = {},
    content: @Composable () -> Unit = {},
    events: MutableState<List<Event>>,
    query: MutableState<String>,
    locationPermitted: Boolean,
    eventViewModel: EventViewModel
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
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
                    icon =
                        customPinBitmapDescriptor
                            ?: BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_RED),
                    onClick = markerClick,
                    visible = event.title.contains(query.value, ignoreCase = true)) {
                      Text(it.title!!, color = Color.Black)
                    }
              }
            }
        content()
      }
    }
  }
}
