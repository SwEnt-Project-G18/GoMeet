package com.github.se.gomeet.ui.mainscreens.events

import EventWidget
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.isPastEvent
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.mainscreens.LoadingText
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
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
  val screenWidth = LocalConfiguration.current.screenWidthDp.dp
  val screenHeight = LocalConfiguration.current.screenHeightDp.dp
  // State management for event filters and list
  var selectedFilter by remember { mutableStateOf("All") }
  val eventList = remember { mutableListOf<Event>() }
  val coroutineScope = rememberCoroutineScope()
  val query = remember { mutableStateOf("") }
  val user = remember { mutableStateOf<GoMeetUser?>(null) }
  val eventsLoaded = remember { mutableStateOf(false) }

  // Initial data loading using LaunchedEffect
  LaunchedEffect(Unit) {
    coroutineScope.launch {
      user.value = userViewModel.getUser(currentUser)
      val allEvents =
          (eventViewModel.getAllEvents() ?: emptyList()).filter { e ->
            (user.value!!.myEvents.contains(e.eventID) ||
                user.value!!.myFavorites.contains(e.eventID) ||
                user.value!!.joinedEvents.contains(e.eventID)) && !isPastEvent(e)
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
      floatingActionButton = {
        Box(modifier = Modifier.padding(8.dp)) {
          IconButton(
              modifier =
                  Modifier.background(
                      color = MaterialTheme.colorScheme.outlineVariant,
                      shape = RoundedCornerShape(10.dp)),
              onClick = { nav.navigateToScreen(Route.CREATE) }) {
                Icon(Icons.Filled.Add, contentDescription = "Create Event", tint = Color.White)
              }
        }
      },
      topBar = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = screenWidth / 15, top = screenHeight / 30)) {
              Text(
                  text = "Events",
                  style =
                      MaterialTheme.typography.headlineMedium.copy(
                          fontWeight = FontWeight.SemiBold))
              Spacer(Modifier.weight(1f))
            }
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
              GoMeetSearchBar(
                  nav,
                  query,
                  MaterialTheme.colorScheme.primaryContainer,
                  MaterialTheme.colorScheme.tertiary)
              Spacer(modifier = Modifier.height(5.dp))
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceEvenly,
                  modifier = Modifier.heightIn(min = 56.dp).fillMaxWidth()) {
                    Button(
                        modifier = Modifier.testTag("JoinedButton"),
                        onClick = { onFilterButtonClick("Joined") },
                        content = { Text("Joined Events") },
                        shape = RoundedCornerShape(10.dp),
                        colors =
                            if (selectedFilter == "Joined")
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.outlineVariant,
                                    contentColor = Color.White)
                            else
                                ButtonDefaults.buttonColors(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.tertiary))

                    Button(
                        modifier = Modifier.testTag("FavouritesButton"),
                        onClick = { onFilterButtonClick("Favourites") },
                        content = { Text("Favourites") },
                        shape = RoundedCornerShape(10.dp),
                        colors =
                            if (selectedFilter == "Favourites")
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.outlineVariant,
                                    contentColor = Color.White)
                            else
                                ButtonDefaults.buttonColors(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.tertiary))
                    Button(
                        modifier = Modifier.testTag("MyEventsButton"),
                        onClick = { onFilterButtonClick("MyEvents") },
                        content = { Text("My Events") },
                        shape = RoundedCornerShape(10.dp),
                        colors =
                            if (selectedFilter == "MyEvents")
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.outlineVariant,
                                    contentColor = Color.White)
                            else
                                ButtonDefaults.buttonColors(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.tertiary))
                  }

              if (!eventsLoaded.value) {
                LoadingText()
              } else {

                // Display events based on the selected filter
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()).fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                      // Display joined events if 'All' or 'Joined' is selected
                      if (selectedFilter == "All" || selectedFilter == "Joined") {
                        Spacer(modifier = Modifier.height(screenHeight / 40))
                        Text(
                            text = "Joined Events",
                            style = MaterialTheme.typography.titleLarge,
                            modifier =
                                Modifier.padding(horizontal = screenWidth / 15)
                                    .testTag("JoinedTitle"))

                        // Loop through and display events that match the joined events criteria
                        eventList
                            .filter { e -> user.value!!.joinedEvents.contains(e.eventID) }
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
                                    eventId = event.eventID,
                                    eventDescription = event.description,
                                    eventDate = event.date,
                                    eventTime = event.time,
                                    eventPicture = painter,
                                    eventLocation = event.location,
                                    verified = false,
                                    nav = nav) // verification to be done using user details
                              }
                            }
                      }

                      // Display favourite events if 'All' or 'Favourites' is selected
                      if (selectedFilter == "All" || selectedFilter == "Favourites") {
                        Spacer(modifier = Modifier.height(screenHeight / 40))
                        Text(
                            text = "Favourites",
                            style = MaterialTheme.typography.titleLarge,
                            modifier =
                                Modifier.padding(horizontal = screenWidth / 15)
                                    .testTag("FavouritesTitle"))

                        // Loop through and display events are marked favourites by the currentUser
                        eventList
                            .filter { e -> user.value!!.myFavorites.contains(e.eventID) }
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
                                    eventId = event.eventID,
                                    eventName = event.title,
                                    eventDescription = event.description,
                                    eventDate = event.date,
                                    eventTime = event.time,
                                    eventPicture = painter,
                                    eventLocation = event.location,
                                    verified = false,
                                    nav = nav)
                              }
                            }
                      }

                      // Display user's own events if 'All' or 'MyEvents' is selected
                      if (selectedFilter == "All" || selectedFilter == "MyEvents") {
                        Spacer(modifier = Modifier.height(screenHeight / 40))
                        Text(
                            text = "My Events",
                            style = MaterialTheme.typography.titleLarge,
                            modifier =
                                Modifier.padding(horizontal = screenWidth / 15)
                                    .testTag("MyEventsTitle"))

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
                                    eventId = event.eventID,
                                    eventName = event.title,
                                    eventDescription = event.description,
                                    eventDate = event.date,
                                    eventTime = event.time,
                                    eventPicture = painter,
                                    eventLocation = event.location,
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
 * Composable function to display the search bar.
 *
 * @param query MutableState object to store the search query
 * @param backgroundColor Color of the search bar background
 * @param contentColor Color of the search bar content
 */
@ExperimentalMaterial3Api
@Composable
fun GoMeetSearchBar(
    nav: NavigationActions,
    query: MutableState<String>,
    backgroundColor: Color,
    contentColor: Color
) {
  val customTextSelectionColors =
      TextSelectionColors(handleColor = DarkCyan, backgroundColor = DarkCyan.copy(alpha = 0.4f))
  CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
    SearchBar(
        shape = RoundedCornerShape(10.dp),
        query = query.value,
        onQueryChange = { query.value = it },
        active = false,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
        placeholder = { Text("Search", color = contentColor) },
        leadingIcon = {
          IconButton(onClick = { nav.navigateToScreen(Route.MESSAGE_CHANNELS) }) {
            Icon(
                ImageVector.vectorResource(R.drawable.gomeet_icon),
                contentDescription = null,
                tint = contentColor)
          }
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
                        cursorColor = MaterialTheme.colorScheme.outlineVariant,
                    ),
            ),
        onActiveChange = {},
        onSearch = {}) {}
  }
}
