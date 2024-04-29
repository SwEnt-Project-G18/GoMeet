package com.github.se.gomeet.ui.mainscreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.ui.mainscreens.events.EventWidget
import com.github.se.gomeet.ui.mainscreens.events.GoMeetSearchBar
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.ui.theme.NavBarUnselected
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import java.time.ZoneId
import java.util.Date
import kotlinx.coroutines.launch

// TODO : This class has only been implemented for testing purposes!
//  It is showing ALL EVENTS IN FIREBASE,
//  THIS IS NOT THE IMPLEMENTATION OF TRENDS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Trends(
    currentUser: String,
    nav: NavigationActions,
    userViewModel: UserViewModel,
    eventViewModel: EventViewModel
) {

  val eventList = remember { mutableListOf<Event>() }
  val coroutineScope = rememberCoroutineScope()
  val query = remember { mutableStateOf("") }

  LaunchedEffect(Unit) {
    coroutineScope.launch {
      val allEvents = eventViewModel.getAllEvents()!!
      if (allEvents.isNotEmpty()) {
        eventList.addAll(allEvents)
      }
    }
  }

  Scaffold(
      topBar = {
        Text(
            text = "Trends",
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
            selectedItem = Route.TRENDS)
      }) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(innerPadding)) {
              Spacer(modifier = Modifier.height(5.dp))
              GoMeetSearchBar(query, NavBarUnselected, Color.DarkGray)
              Spacer(modifier = Modifier.height(5.dp))
              Column(modifier = Modifier.verticalScroll(rememberScrollState()).fillMaxSize()) {
                Text(
                    text = "All Events",
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

                eventList.forEach { event ->
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
                        eventName = event.title,
                        eventId = event.uid,
                        eventDescription = event.description,
                        eventDate =
                            Date.from(event.date.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        eventPicture = painter,
                        verified = false,
                        nav = nav) // verification to be done using user details
                  }
                }
              }
            }
      }
}
