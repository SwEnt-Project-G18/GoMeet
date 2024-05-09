package com.github.se.gomeet.ui.mainscreens.events

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.mainscreens.LoadingText
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.ui.theme.NavBarUnselected
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

/**
 * Composable function to display the Events screen.
 *
 * @param currentUser String object representing CurrentUserId
 * @param nav NavigationActions object to handle navigation
 * @param userViewModel UserViewModel object to handle users
 * @param eventViewModel EventViewModel object to handle events
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Events(
    currentUser: String,
    nav: NavigationActions,
    userViewModel: UserViewModel,
    eventViewModel: EventViewModel
) {

  // State management for event filters and list
  var selectedFilter by remember { mutableStateOf("All") }
  val eventList = remember { mutableListOf<Event>() }
  val coroutineScope = rememberCoroutineScope()
  val query = remember { mutableStateOf("") }
  val user = remember { mutableStateOf<GoMeetUser?>(null) }
  var eventsLoaded = remember { mutableStateOf(false) }

  // Initial data loading using LaunchedEffect
  LaunchedEffect(Unit) {
    coroutineScope.launch {
      user.value = userViewModel.getUser(currentUser)
      val allEvents =
          eventViewModel.getAllEvents()!!.filter { e ->
            (user.value!!.myEvents.contains(e.uid) ||
                user.value!!.myFavorites.contains(e.uid) ||
                user.value!!.joinedEvents.contains(e.uid)) && e.date.isAfter(LocalDate.now())
          }
      if (allEvents.isNotEmpty()) {
        eventList.addAll(allEvents)
      }
      eventsLoaded.value = true
    }
  }

  // Event filtering functionality

  fun onFilterButtonClick(filterType: String) {
    selectedFilter = if (selectedFilter == filterType) "All" else filterType
  }

  // Scaffold is a structure that supports top bar, content area, and bottom navigation
  Scaffold(
      topBar = {
        Text(
            text = "Events",
            modifier = Modifier.padding(start = 15.dp, top = 15.dp, end = 15.dp, bottom = 0.dp),
            color = DarkCyan,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Default,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.headlineLarge)
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { selectedTab ->
              nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
            },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = Route.EVENTS)
      }) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(innerPadding)) {
              Spacer(modifier = Modifier.height(5.dp))
              GoMeetSearchBar(query, NavBarUnselected, Color.DarkGray)
              Spacer(modifier = Modifier.height(5.dp))
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceEvenly,
                  modifier = Modifier.heightIn(min = 56.dp).fillMaxWidth()) {
                    Button(
                        onClick = { onFilterButtonClick("Joined") },
                        content = { Text("JoinedEvents") },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    if (selectedFilter == "Joined") DarkCyan else NavBarUnselected,
                                contentColor =
                                    if (selectedFilter == "Joined") Color.White else DarkCyan),
                        border = BorderStroke(1.dp, DarkCyan))
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = { onFilterButtonClick("Favourites") },
                        content = { Text("Favourites") },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    if (selectedFilter == "Favourites") DarkCyan
                                    else NavBarUnselected,
                                contentColor =
                                    if (selectedFilter == "Favourites") Color.White else DarkCyan),
                        border = BorderStroke(1.dp, DarkCyan))
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = { onFilterButtonClick("MyEvents") },
                        content = { Text("My events") },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    if (selectedFilter == "MyEvents") DarkCyan
                                    else NavBarUnselected,
                                contentColor =
                                    if (selectedFilter == "MyEvents") Color.White else DarkCyan),
                        border = BorderStroke(1.dp, DarkCyan))
                  }

              if (!eventsLoaded.value) {
                LoadingText()
              } else {

                // Display events based on the selected filter
                Column(modifier = Modifier.verticalScroll(rememberScrollState()).fillMaxSize()) {
                  // Display joined events if 'All' or 'Joined' is selected
                  if (selectedFilter == "All" || selectedFilter == "Joined") {
                    Text(
                        text = "Joined Events",
                        style =
                            TextStyle(
                                fontSize = 20.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily(Font(R.font.roboto)),
                                fontWeight = FontWeight(1000),
                                color = DarkCyan,
                                textAlign = TextAlign.Center,
                                letterSpacing = 0.5.sp,
                            ),
                        modifier = Modifier.padding(10.dp).align(Alignment.Start))

                    // Loop through and display events that match the joined events criteria
                    eventList
                        .filter { e -> user.value!!.myEvents.contains(e.uid) }
                        .forEach { event ->
                          if (event.title.contains(query.value, ignoreCase = true)) {
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
                            // Reusable widget for displaying event details
                            EventWidget(
                                userName = event.creator,
                                eventName = event.title,
                                eventId = event.uid,
                                eventDescription = event.description,
                                eventDate =
                                    Date.from(
                                        event.date
                                            .atStartOfDay(ZoneId.systemDefault())
                                            .toInstant()),
                                eventPicture = painter,
                                verified = false,
                                nav = nav) // verification to be done using user details
                          }
                        }
                  }

                  // Display favourite events if 'All' or 'Favourites' is selected
                  if (selectedFilter == "All" || selectedFilter == "Favourites") {

                    Text(
                        text = "Favourites",
                        style =
                            TextStyle(
                                fontSize = 20.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily(Font(R.font.roboto)),
                                fontWeight = FontWeight(1000),
                                color = DarkCyan,
                                textAlign = TextAlign.Center,
                                letterSpacing = 0.5.sp,
                            ),
                        modifier = Modifier.padding(10.dp).align(Alignment.Start))

                    // Loop through and display events are marked favourites by the currentUser
                    eventList
                        .filter { e -> user.value!!.myFavorites.contains(e.uid) }
                        .forEach { event ->
                          if (event.title.contains(query.value, ignoreCase = true)) {
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
                            EventWidget(
                                userName = event.creator,
                                eventId = event.uid,
                                eventName = event.title,
                                eventDescription = event.description,
                                eventDate =
                                    Date.from(
                                        event.date
                                            .atStartOfDay(ZoneId.systemDefault())
                                            .toInstant()),
                                eventPicture = painter,
                                verified = false,
                                nav = nav)
                          }
                        }
                  }

                  // Display user's own events if 'All' or 'MyEvents' is selected
                  if (selectedFilter == "All" || selectedFilter == "MyEvents") {
                    Text(
                        text = "My Events",
                        style =
                            TextStyle(
                                fontSize = 20.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily(Font(R.font.roboto)),
                                fontWeight = FontWeight(1000),
                                color = DarkCyan,
                                textAlign = TextAlign.Center,
                                letterSpacing = 0.5.sp,
                            ),
                        modifier = Modifier.padding(10.dp).align(Alignment.Start))

                    // Loop through and display events created by currentUser
                    eventList
                        .filter { e -> e.creator == user.value!!.uid }
                        .forEach { event ->
                          if (event.title.contains(query.value, ignoreCase = true)) {
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
                            EventWidget(
                                userName = event.creator,
                                eventId = event.uid,
                                eventName = event.title,
                                eventDescription = event.description,
                                eventDate =
                                    Date.from(
                                        event.date
                                            .atStartOfDay(ZoneId.systemDefault())
                                            .toInstant()),
                                eventPicture = painter,
                                verified = false,
                                nav = nav)
                          }
                        }
                  }
                }
              }
            }
      }
}

/**
 * A composable function that displays detailed information about an event in a card layout. This
 * widget is designed to present event details including the name, description, date, and an image
 * if available. The card is interactive and can be tapped to navigate to further event details.
 *
 * @param userName The name of the event creator.
 * @param eventId The unique identifier for the event.
 * @param eventName The name of the event.
 * @param eventDescription A short description of the event.
 * @param eventDate The date and time at which the event is scheduled.
 * @param eventPicture A painter object that handles the rendering of the event's image.
 * @param verified A boolean indicating whether the event or the creator is verified. This could
 *   influence the visual representation.
 * @param nav NavigationActions object to handle navigation events such as tapping on the event
 */
@Composable
fun EventWidget(
    userName: String,
    eventId: String,
    eventName: String,
    eventDescription: String,
    eventDate: Date,
    eventPicture: Painter,
    verified: Boolean,
    nav: NavigationActions,
) {

  val configuration = LocalConfiguration.current
  val screenWidth = configuration.screenWidthDp.dp
  val density = LocalDensity.current

  val smallTextSize = with(density) { screenWidth.toPx() / 85 }
  val bigTextSize = with(density) { screenWidth.toPx() / 60 }

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

  Card(
      modifier =
          Modifier.fillMaxWidth()
              .testTag("Card")
              .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 5.dp)
              .clickable {
                nav.navigateToEventInfo(
                    eventId = eventId,
                    title = eventName,
                    date = dayString,
                    time = timeString,
                    description = eventDescription,
                    organizer = userName,
                    loc = LatLng(46.5191, 6.5668), // TODO: replace with actual location
                    rating = 0.0 // TODO: replace with actual rating
                    // TODO: add image
                    )
              },
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = BorderStroke(2.dp, DarkCyan)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround) {
              Column(
                  modifier = Modifier.weight(4f).padding(15.dp),
                  horizontalAlignment = Alignment.Start, // Align text horizontally to center
                  verticalArrangement = Arrangement.Center) {
                    Text(
                        text = eventName,
                        style =
                            TextStyle(
                                fontSize = bigTextSize.sp,
                                lineHeight = 20.sp,
                                fontFamily = FontFamily(Font(R.font.roboto)),
                                fontWeight = FontWeight(700),
                                color = MaterialTheme.colorScheme.onBackground,
                                letterSpacing = 0.25.sp,
                            ),
                        modifier = Modifier.testTag("EventName"))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center) {
                          var username by remember { mutableStateOf<String?>("Loading...") }
                          LaunchedEffect(userName) {
                            username = UserViewModel().getUsername(userName)
                          }

                          username?.let {
                            Text(
                                it,
                                style =
                                    TextStyle(
                                        fontSize = smallTextSize.sp,
                                        lineHeight = 24.sp,
                                        fontFamily = FontFamily(Font(R.font.roboto)),
                                        fontWeight = FontWeight(700),
                                        color = MaterialTheme.colorScheme.onBackground,
                                        letterSpacing = 0.15.sp,
                                    ),
                                modifier = Modifier.padding(top = 5.dp).testTag("UserName"))
                          }
                          if (verified) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(5.dp).size(smallTextSize.dp * (1.4f))) {
                                  Image(
                                      painter = painterResource(id = R.drawable.verified),
                                      contentDescription = "Verified",
                                  )
                                }
                          }
                        }

                    Text(
                        dayString + " - " + timeString,
                        style =
                            TextStyle(
                                fontSize = smallTextSize.sp,
                                lineHeight = 20.sp,
                                fontFamily = FontFamily(Font(R.font.roboto)),
                                fontWeight = FontWeight(700),
                                color = MaterialTheme.colorScheme.onBackground,
                                letterSpacing = 0.25.sp,
                            ),
                        modifier = Modifier.testTag("EventDate"))
                  }
              Image(
                  painter = eventPicture,
                  contentDescription = "Event Picture",
                  modifier =
                      Modifier.weight(3f)
                          .fillMaxHeight()
                          .aspectRatio(3f / 1.75f)
                          .clipToBounds()
                          .padding(0.dp) // Clip the image if it overflows its bounds
                          .testTag("EventPicture"),
                  contentScale = ContentScale.Crop, // Crop the image to fit the aspect ratio
              )
            }
      }
}

/**
 * Composable function to display the search bar.
 *
 * @param query MutableState object to store the search query
 * @param backgroundColor Color of the search bar background
 * @param contentColor Color of the search bar content
 */
@ExperimentalMaterial3Api
@Composable
fun GoMeetSearchBar(query: MutableState<String>, backgroundColor: Color, contentColor: Color) {
  val customTextSelectionColors =
      TextSelectionColors(handleColor = DarkCyan, backgroundColor = DarkCyan.copy(alpha = 0.4f))
  CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
    androidx.compose.material3.SearchBar(
        query = query.value,
        onQueryChange = { query.value = it },
        active = false,
        modifier = Modifier.fillMaxWidth().padding(start = 5.dp, end = 5.dp),
        placeholder = { Text("Search", color = contentColor) },
        leadingIcon = {
          Icon(
              ImageVector.vectorResource(R.drawable.gomeet_icon),
              contentDescription = null,
              tint = contentColor)
        },
        trailingIcon = {
          Icon(
              ImageVector.vectorResource(R.drawable.mic_icon),
              contentDescription = null,
              tint = contentColor,
              modifier =
                  Modifier.clickable {
                    // TODO: handle voice search
                  })
        },
        colors =
            SearchBarDefaults.colors(
                containerColor = backgroundColor,
                inputFieldColors =
                    TextFieldDefaults.colors(
                        focusedTextColor = contentColor,
                        unfocusedTextColor = contentColor,
                        cursorColor = DarkCyan,
                    ),
            ),
        onActiveChange = {},
        onSearch = {}) {}
  }
}

/**
 * A custom search bar composable function that provides a user interface for inputting search
 * queries. This search bar includes visual customizations and functionality adjustments tailored to
 * the GoMeet application's theme. It features a leading icon that represents the app, a trailing
 * icon for voice search, and custom color schemes for different states of the search input field.
 *
 * @param query A mutable state holding the current query string, allowing the text to be updated
 *   dynamically.
 * @param backgroundColor The color of the search bar's background.
 * @param contentColor The color of the text and icons within the search bar.
 */
@Composable
@Preview
fun EventPreview() {
  Events("", nav = NavigationActions(rememberNavController()), UserViewModel(), EventViewModel())
  /*EventWidget(
  "EPFL Chess Club",
  "Chess Tournament",
  eventDate,
  R.drawable.gomeet_logo,
  R.drawable.intbee_logo,
  true)*/
}
