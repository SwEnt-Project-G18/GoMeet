package com.github.se.gomeet.ui.mainscreens

import EventWidget
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.getEventDateString
import com.github.se.gomeet.model.event.getEventTimeString
import com.github.se.gomeet.model.event.isPastEvent
import com.github.se.gomeet.ui.mainscreens.events.GoMeetSearchBar
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.EventViewModel.SortOption
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// TODO : This class has only been implemented for testing purposes!
//  It is showing ALL EVENTS IN FIREBASE,
//  THIS IS NOT THE IMPLEMENTATION OF TRENDS

/**
 * Trends screen composable. This is where the popular trends are displayed.
 *
 * @param nav Navigation actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Trends(
    currentUserId: String,
    nav: NavigationActions,
    userViewModel: UserViewModel,
    eventViewModel: EventViewModel
) {

  val eventList = remember { mutableStateListOf<Event>() }
  val coroutineScope = rememberCoroutineScope()
  val query = remember { mutableStateOf("") }
  val eventsLoaded = remember { mutableStateOf(false) }
  val screenWidth = LocalConfiguration.current.screenWidthDp.dp
  val screenHeight = LocalConfiguration.current.screenHeightDp.dp

  LaunchedEffect(Unit) {
    coroutineScope.launch {
      val allEvents = eventViewModel.getAllEvents()!!.filter { !isPastEvent(it) }
      if (allEvents.isNotEmpty()) {
        eventList.addAll(allEvents)
      }
      eventsLoaded.value = true
    }
  }

  Scaffold(
      topBar = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = screenWidth / 15, top = screenHeight / 30)) {
              Text(
                  text = "Trends",
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
            selectedItem = Route.TRENDS)
      }) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState())) {
              Spacer(modifier = Modifier.height(5.dp))
              GoMeetSearchBar(
                  nav,
                  query,
                  MaterialTheme.colorScheme.primaryContainer,
                  MaterialTheme.colorScheme.tertiary)
              Spacer(modifier = Modifier.height(5.dp))

              SortButton(eventList)

              if (!eventsLoaded.value) {
                LoadingText()
              } else {
                Spacer(modifier = Modifier.height(5.dp))
                // TODO: Use the top 5 events instead
                EventCarousel(eventList.take(5), nav)

                Column(modifier = Modifier.fillMaxSize()) {
                  // TODO: Remove the top 5 events from the list
                  eventList.forEach { event ->
                    if (event.title.contains(query.value, ignoreCase = true)) {

                      EventWidget(
                          event = event, verified = false, nav = nav, userVM = userViewModel)
                    }
                  }
                }
              }
            }
      }
}

/**
 * Event carousel composable. This is where the events are displayed in a carousel.
 *
 * @param events The list of events to display.
 * @param nav Navigation actions.
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun EventCarousel(events: List<Event>, nav: NavigationActions) {
  val pagerState = rememberPagerState()

  LaunchedEffect(pagerState) {
    launch {
      while (true) {
        delay(10000) // Wait for 10 seconds
        val nextPage = if (events.isNotEmpty()) (pagerState.currentPage + 1) % events.size else 0
        pagerState.animateScrollToPage(nextPage)
      }
    }
  }

  Column(modifier = Modifier.fillMaxWidth().height(250.dp)) {
    HorizontalPager(count = events.size, state = pagerState, modifier = Modifier.weight(1f)) { page
      ->
      val event = events[page]
      val painter =
          if (event.images.isNotEmpty()) {
            rememberAsyncImagePainter(event.images[0])
          } else {
            painterResource(id = R.drawable.gomeet_logo)
          }

        val dayString = getEventDateString(event.date)
        val timeString = getEventTimeString(event.time)

      Box(
          modifier =
              Modifier.padding(8.dp)
                  .background(Color.Gray, shape = RoundedCornerShape(16.dp))
                  .fillMaxSize()
                  .clickable {
                    nav.navigateToEventInfo(
                        eventId = event.eventID,
                        title = event.title,
                        date = dayString,
                        time = timeString,
                        organizer = event.creator,
                        rating = 0.0,
                        description = event.description,
                        loc = LatLng(event.location.latitude, event.location.longitude))
                  }
                  .clip(RoundedCornerShape(16.dp)),
          contentAlignment = Alignment.Center) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop)
            Box(modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp)) {
              Box(
                  modifier =
                      Modifier.clip(RoundedCornerShape(8.dp))
                          .background(Color.Black.copy(alpha = 0.5f))
                          .padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(
                        text = event.title,
                        color = Color.White,
                        fontSize = 17.sp,
                    )
                  }
            }
          }
    }

    HorizontalPagerIndicator(
        pagerState = pagerState,
        modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp),
        inactiveColor = MaterialTheme.colorScheme.inverseOnSurface,
        activeColor = MaterialTheme.colorScheme.onSurface)
  }
}

/**
 * Sort button composable. This is where the user can sort the events.
 *
 * @param eventList The list of events to sort.
 */
@Composable
fun SortButton(eventList: MutableList<Event>) {
  var expanded by remember { mutableStateOf(false) }
  var selectedOption by remember { mutableStateOf(EventViewModel.SortOption.DEFAULT) }

  Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.fillMaxWidth().padding(top = 10.dp, start = 10.dp, end = 10.dp)) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            colors =
                ButtonDefaults.buttonColors(
                    MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.tertiary),
            onClick = { expanded = true }) {
              Text("Sort")
            }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier =
                Modifier.align(Alignment.Center)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)) {
              DropdownMenuItem(
                  text = { Text("Popularity") },
                  onClick = {
                    // TODO: Implement popularity sorting
                    selectedOption = SortOption.DEFAULT
                    expanded = false
                  },
                  modifier =
                      Modifier.background(
                          if (selectedOption == SortOption.DEFAULT)
                              MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                          else Color.Transparent))
              DropdownMenuItem(
                  text = { Text("Name") },
                  onClick = {
                    selectedOption = SortOption.ALPHABETICAL
                    eventList.sortBy { it.title }
                    expanded = false
                  },
                  modifier =
                      Modifier.background(
                          if (selectedOption == SortOption.ALPHABETICAL)
                              MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                          else Color.Transparent))
              DropdownMenuItem(
                  text = { Text("Date") },
                  onClick = {
                    selectedOption = SortOption.DATE
                    eventList.sortBy { it.date }
                    expanded = false
                  },
                  modifier =
                      Modifier.background(
                          if (selectedOption == SortOption.DATE)
                              MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                          else Color.Transparent))
            }
      }
}
