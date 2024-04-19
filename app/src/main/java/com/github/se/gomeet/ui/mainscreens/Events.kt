package com.github.se.gomeet.ui.mainscreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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

@Composable
fun Events(nav: NavigationActions) {
  Scaffold(
      topBar = {
        Text(
            text = "Events",
            style =
                TextStyle(
                    fontSize = 24.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(R.font.roboto)),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = DarkCyan,
                    letterSpacing = 0.5.sp,
                ),
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 15.dp).testTag("EventsTitle"))
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
                  horizontalArrangement = Arrangement.Center,
                  modifier = Modifier.heightIn(min = 56.dp).fillMaxWidth()) {
                    Button(
                        onClick = {},
                        content = { Text("My tickets") },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = NavBarUnselected, contentColor = DarkCyan),
                        border = BorderStroke(1.dp, DarkCyan),
                        modifier = Modifier.testTag("MyTicketsButton"),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {},
                        content = { Text("Favourites") },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = NavBarUnselected, contentColor = DarkCyan),
                        border = BorderStroke(1.dp, DarkCyan),
                        modifier = Modifier.testTag("FavouritesButton")
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {},
                        content = { Text("My events") },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = NavBarUnselected, contentColor = DarkCyan),
                        border = BorderStroke(1.dp, DarkCyan),
                        modifier = Modifier.testTag("MyEventsButton")
                    )
                  }
              Text(
                  text = "My Tickets",
                  style =
                      TextStyle(
                          fontSize = 16.sp,
                          lineHeight = 16.sp,
                          fontFamily = FontFamily(Font(R.font.roboto)),
                          fontWeight = FontWeight(700),
                          color = DarkCyan,
                          textAlign = TextAlign.Center,
                          letterSpacing = 0.5.sp,
                      ),
                  modifier = Modifier.padding(10.dp).align(Alignment.Start).testTag("MyTicketsText"))
              EventWidget(
                  UserName = "EPFL Chess Club",
                  EventName = "Chess Tournament",
                  EventDate = "WEDNESDAY - 14:00",
                  ProfilePicture = R.drawable.gomeet_logo,
                  EventPicture = R.drawable.intbee_logo,
                  Verified = true)
              Spacer(modifier = Modifier.height(10.dp))
              EventWidget(
                  UserName = "EPFL Chess Club",
                  EventName = "Chess Tournament",
                  EventDate = "WEDNESDAY - 14:00",
                  ProfilePicture = R.drawable.gomeet_logo,
                  EventPicture = R.drawable.intbee_logo,
                  Verified = true)
              Spacer(modifier = Modifier.height(10.dp))
              EventWidget(
                  UserName = "EPFL Chess Club",
                  EventName = "Chess Tournament",
                  EventDate = "WEDNESDAY - 14:00",
                  ProfilePicture = R.drawable.gomeet_logo,
                  EventPicture = R.drawable.intbee_logo,
                  Verified = true)

              Text(
                  text = "Favourites",
                  style =
                      TextStyle(
                          fontSize = 16.sp,
                          lineHeight = 16.sp,
                          fontFamily = FontFamily(Font(R.font.roboto)),
                          fontWeight = FontWeight(700),
                          color = DarkCyan,
                          textAlign = TextAlign.Center,
                          letterSpacing = 0.5.sp,
                      ),
                  modifier = Modifier.padding(10.dp).align(Alignment.Start).testTag("FavouritesText"))
              EventWidget(
                  UserName = "EPFL Chess Club",
                  EventName = "Chess Tournament",
                  EventDate = "WEDNESDAY - 14:00",
                  ProfilePicture = R.drawable.gomeet_logo,
                  EventPicture = R.drawable.intbee_logo,
                  Verified = true)
              Text(
                  text = "My Events",
                  style =
                      TextStyle(
                          fontSize = 16.sp,
                          lineHeight = 16.sp,
                          fontFamily = FontFamily(Font(R.font.roboto)),
                          fontWeight = FontWeight(700),
                          color = DarkCyan,
                          textAlign = TextAlign.Center,
                          letterSpacing = 0.5.sp,
                      ),
                  modifier = Modifier.padding(10.dp).align(Alignment.Start).testTag("MyEventsText"))
              EventWidget(
                  UserName = "EPFL Chess Club",
                  EventName = "Chess Tournament",
                  EventDate = "WEDNESDAY - 14:00",
                  ProfilePicture = R.drawable.gomeet_logo,
                  EventPicture = R.drawable.intbee_logo,
                  Verified = true)
            }
      }
}

@Composable
fun EventWidget(
    UserName: String,
    EventName: String,
    EventDate: String,
    ProfilePicture: Int,
    EventPicture: Int,
    Verified: Boolean
) {
  Card(
      modifier = Modifier.width(377.dp).height(77.dp).testTag("Card"),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = BorderStroke(1.dp, DarkCyan)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
              Image(
                  painterResource(id = ProfilePicture),
                  "Event Picture",
                  modifier = Modifier.padding(15.dp).width(40.dp).height(40.dp).testTag("ProfilePicture"))
              Column {
                Row {
                  Text(
                      UserName,
                      style =
                          TextStyle(
                              fontSize = 16.sp,
                              lineHeight = 24.sp,
                              fontFamily = FontFamily(Font(R.font.roboto)),
                              fontWeight = FontWeight(700),
                              color = Color(0xFF1D1B20),
                              letterSpacing = 0.15.sp,
                          ),
                      modifier = Modifier.padding(top = 5.dp).testTag("UserName"))
                  if (Verified) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        modifier = Modifier.padding(5.dp))
                  }
                }
                Text(
                    text = EventName,
                    style =
                        TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight(700),
                            color = Color(0xFF1D1B20),
                            letterSpacing = 0.25.sp,
                        ),
                    modifier = Modifier.testTag("EventName"))
                Text(
                    EventDate,
                    style =
                        TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight(700),
                            color = Color(0xFF1D1B20),
                            letterSpacing = 0.25.sp,
                        ),
                    modifier = Modifier.testTag("EventDate"))
              }
              Image(
                  painterResource(id = EventPicture),
                  "Event Picture",
                  modifier = Modifier.width(80.dp).height(77.dp).padding(10.dp).testTag("EventPicture"),
                  contentScale = ContentScale.FillBounds)
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
  "WEDNESDAY - 14:00",
  R.drawable.gomeet_logo,
  R.drawable.intbee_logo,
  true)*/
}
