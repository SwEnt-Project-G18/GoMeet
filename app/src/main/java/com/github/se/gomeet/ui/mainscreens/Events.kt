package com.github.se.gomeet.ui.mainscreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
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
import com.github.se.gomeet.R
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.ui.theme.NavBarUnselected
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun Events(nav: NavigationActions) {

  var selectedFilter by remember { mutableStateOf("All") }

  // Define a function to handle button clicks
  fun onFilterButtonClick(filterType: String) {
    selectedFilter = if (selectedFilter == filterType) "All" else filterType
  }

  val eventDate =
      Calendar.getInstance()
          .apply {
            set(Calendar.YEAR, 2024)
            set(Calendar.MONTH, Calendar.APRIL)
            set(Calendar.DAY_OF_MONTH, 16)
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 0)
          }
          .time

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
              Spacer(modifier = Modifier.height(10.dp))
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceEvenly,
                  modifier = Modifier.heightIn(min = 56.dp).fillMaxWidth()) {
                    Button(
                        onClick = { onFilterButtonClick("MyTickets") },
                        content = { Text("My tickets") },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    if (selectedFilter == "MyTickets") DarkCyan
                                    else NavBarUnselected,
                                contentColor =
                                    if (selectedFilter == "MyTickets") Color.White else DarkCyan),
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

              Column(modifier = Modifier.verticalScroll(rememberScrollState()).fillMaxSize()) {
                if (selectedFilter == "All" || selectedFilter == "MyTickets") {
                  Text(
                      text = "My Tickets",
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
                  EventWidget(
                      UserName = "EPFL Chess Club",
                      EventName = "Chess Tournament",
                      EventDate = eventDate,
                      ProfilePicture = R.drawable.gomeet_logo,
                      EventPicture = R.drawable.intbee_logo,
                      Verified = true)
                  EventWidget(
                      UserName = "EPFL Chess Club",
                      EventName = "Chess Tournament",
                      EventDate = eventDate,
                      ProfilePicture = R.drawable.gomeet_logo,
                      EventPicture = R.drawable.intbee_logo,
                      Verified = true)
                  EventWidget(
                      UserName = "EPFL Chess Club",
                      EventName = "Chess Tournament",
                      EventDate = eventDate,
                      ProfilePicture = R.drawable.gomeet_logo,
                      EventPicture = R.drawable.intbee_logo,
                      Verified = true)
                  EventWidget(
                      UserName = "EPFL Chess Club",
                      EventName = "Chess Tournament",
                      EventDate = eventDate,
                      ProfilePicture = R.drawable.gomeet_logo,
                      EventPicture = R.drawable.intbee_logo,
                      Verified = true)
                  EventWidget(
                      UserName = "EPFL Chess Club",
                      EventName = "Chess Tournament",
                      EventDate = eventDate,
                      ProfilePicture = R.drawable.gomeet_logo,
                      EventPicture = R.drawable.intbee_logo,
                      Verified = true)
                  EventWidget(
                      UserName = "EPFL Chess Club",
                      EventName = "Chess Tournament",
                      EventDate = eventDate,
                      ProfilePicture = R.drawable.gomeet_logo,
                      EventPicture = R.drawable.intbee_logo,
                      Verified = true)
                }

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
                  EventWidget(
                      UserName = "EPFL Chess Club",
                      EventName = "Chess Tournament",
                      EventDate = eventDate,
                      ProfilePicture = R.drawable.gomeet_logo,
                      EventPicture = R.drawable.intbee_logo,
                      Verified = true)
                }

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
                  EventWidget(
                      UserName = "EPFL Chess Club",
                      EventName = "Chess Tournament",
                      EventDate = eventDate,
                      ProfilePicture = R.drawable.gomeet_logo,
                      EventPicture = R.drawable.intbee_logo,
                      Verified = true)
                }
              }
            }
      }
}

@Composable
fun EventWidget(
    UserName: String,
    EventName: String,
    EventDate: Date,
    ProfilePicture: Int,
    EventPicture: Int,
    Verified: Boolean
) {

  val configuration = LocalConfiguration.current
  val screenWidth = configuration.screenWidthDp.dp
  val density = LocalDensity.current

  // Example logic to calculate text size based on screen width
  val smallTextSize = with(density) { screenWidth.toPx() / 85 }
  val bigTextSize = with(density) { screenWidth.toPx() / 60 }

  Card(
      modifier =
          Modifier.fillMaxWidth().padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 5.dp),
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
                        text = EventName,
                        style =
                            TextStyle(
                                fontSize = bigTextSize.sp,
                                lineHeight = 20.sp,
                                fontFamily = FontFamily(Font(R.font.roboto)),
                                fontWeight = FontWeight(700),
                                color = MaterialTheme.colorScheme.onBackground,
                                letterSpacing = 0.25.sp,
                            ))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center) {
                          Text(
                              UserName,
                              style =
                                  TextStyle(
                                      fontSize = smallTextSize.sp,
                                      lineHeight = 24.sp,
                                      fontFamily = FontFamily(Font(R.font.roboto)),
                                      fontWeight = FontWeight(700),
                                      color = MaterialTheme.colorScheme.onBackground,
                                      letterSpacing = 0.15.sp,
                                  ),
                              modifier = Modifier.padding(top = 5.dp))
                          if (Verified) {
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

                    val currentDate = Calendar.getInstance()
                    val startOfWeek = currentDate.clone() as Calendar
                    startOfWeek[Calendar.DAY_OF_WEEK] = startOfWeek.firstDayOfWeek
                    val endOfWeek = startOfWeek.clone() as Calendar
                    endOfWeek.add(Calendar.DAY_OF_WEEK, 6)

                    val eventCalendar = Calendar.getInstance().apply { time = EventDate }

                    // Determine if the event date is within this week
                    val isThisWeek = eventCalendar >= startOfWeek && eventCalendar <= endOfWeek

                    // Format based on whether the date is in the current week
                    val dateFormat =
                        if (isThisWeek) {
                          SimpleDateFormat("EEEE - HH:mm", Locale.getDefault()) // "Tuesday"
                        } else {
                          SimpleDateFormat("dd/MM/yy - HH:mm", Locale.getDefault()) // "05/12/24"
                        }

                    // Convert the Date object to a formatted String
                    val dateString = dateFormat.format(EventDate)

                    Text(
                        dateString,
                        style =
                            TextStyle(
                                fontSize = smallTextSize.sp,
                                lineHeight = 20.sp,
                                fontFamily = FontFamily(Font(R.font.roboto)),
                                fontWeight = FontWeight(700),
                                color = MaterialTheme.colorScheme.onBackground,
                                letterSpacing = 0.25.sp,
                            ))
                  }
              Image(
                  painter = painterResource(id = EventPicture),
                  contentDescription = "Event Picture",
                  modifier =
                      Modifier.weight(
                              3f) // Take 1/3 of the card space because of the total weight of 4 (3
                                  // for the column and 1 for this image)
                          .fillMaxHeight() // Fill the height of the Row
                          .aspectRatio(
                              3f / 1.75f) // Maintain an aspect ratio of 3:2, change it as needed
                          .clipToBounds()
                          .padding(0.dp), // Clip the image if it overflows its bounds
                  contentScale = ContentScale.Crop // Crop the image to fit the aspect ratio
                  )
            }
      }
}

@Composable
@Preview
fun EventPreview() {
  Events(nav = NavigationActions(rememberNavController()))
  /*EventWidget(
  "EPFL Chess Club",
  "Chess Tournament",
  eventDate,
  R.drawable.gomeet_logo,
  R.drawable.intbee_logo,
  true)*/
}
