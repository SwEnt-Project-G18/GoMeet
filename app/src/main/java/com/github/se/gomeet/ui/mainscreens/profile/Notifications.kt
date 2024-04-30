package com.github.se.gomeet.ui.mainscreens.profile

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
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.InviteStatus
import com.github.se.gomeet.model.event.UserInvitedToEvents
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.ui.theme.NavBarUnselected
import com.github.se.gomeet.viewmodel.EventInviteViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun Notifications(nav: NavigationActions) {

  val initialState =
      UserInvitedToEvents(user = "", invitedToEvents = mutableListOf("" to InviteStatus.PENDING))
  var selectedFilter by remember { mutableStateOf("All") }
  val inviteList = remember { mutableStateOf(initialState) }
  val coroutineScope = rememberCoroutineScope()

    /*
  LaunchedEffect(Unit) {
      coroutineScope.launch {
          val allInvitedEvents = inviteViewModel.getUsersInvitedToEvent(userId)
          if (allInvitedEvents != null) {
              inviteList.value = allInvitedEvents
          }
      }
  }
  */

  // Define a function to handle button clicks
  fun onFilterButtonClick(filterType: String) {
    selectedFilter = if (selectedFilter == filterType) "All" else filterType
  }

  Scaffold(
      topBar = {
        Text(
            text = "Notifications",
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
            selectedItem = Route.NOTIFICATIONS)
      }) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(innerPadding)) {
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.Start) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back_24px),
                        contentDescription = "image description",
                        modifier = Modifier.padding(10.dp))
                    Text(
                        text = "Back",
                        style =
                            TextStyle(
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily(Font(R.font.roboto)),
                                fontWeight = FontWeight(600),
                                color = Color(0xFF1D1B20),
                                letterSpacing = 0.5.sp,
                            ))
                  }
              Spacer(modifier = Modifier.height(10.dp))
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceEvenly,
                  modifier = Modifier.heightIn(min = 56.dp).fillMaxWidth()) {
                    Button(
                        onClick = { onFilterButtonClick("All") },
                        content = { Text("All") },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    if (selectedFilter == "All") DarkCyan else NavBarUnselected,
                                contentColor =
                                    if (selectedFilter == "All") Color.White else DarkCyan),
                        border = BorderStroke(1.dp, DarkCyan))
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = { onFilterButtonClick("Invitations") },
                        content = { Text("Invitations") },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    if (selectedFilter == "My events") DarkCyan
                                    else NavBarUnselected,
                                contentColor =
                                    if (selectedFilter == "My events") Color.White else DarkCyan),
                        border = BorderStroke(1.dp, DarkCyan))
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = { onFilterButtonClick("My events") },
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
                val painter: Painter =
                    rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(data = R.drawable.gomeet_logo)
                            .apply(
                                block =
                                    fun ImageRequest.Builder.() {
                                      crossfade(true)
                                      placeholder(R.drawable.gomeet_logo)
                                    })
                            .build())
                NotificationsWidget(
                    userName = "Bill Clinton",
                    eventName = "Chess night",
                    eventDate = Date(),
                    eventPicture = painter,
                    organizerName = "EPFL Chess Club",
                    verified = true)
                  Spacer(Modifier.height(10.dp))

              }
            }
      }
}

@Composable
fun NotificationsWidget(
    userName: String,
    organizerName: String,
    eventName: String,
    eventDate: Date,
    eventPicture: Painter,
    verified: Boolean
) {

  val configuration = LocalConfiguration.current
  val screenWidth = configuration.screenWidthDp.dp
  val density = LocalDensity.current

  // Example logic to calculate text size based on screen width
  val smallTextSize = with(density) { screenWidth.toPx() / 85 }
  val bigTextSize = with(density) { screenWidth.toPx() / 60 }
  Row(Modifier.padding(start = 10.dp)) {
    Text(
        text = userName,
        style =
            TextStyle(
                fontSize = 11.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily(Font(R.font.roboto)),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF202124),
                letterSpacing = 0.5.sp,
            ))
    Spacer(modifier = Modifier.width(2.5.dp))
    Text(
        text = "invited you to attend",
        style =
            TextStyle(
                fontSize = 11.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily(Font(R.font.roboto)),
                fontWeight = FontWeight(700),
                color = Color(0xFF202124),
                letterSpacing = 0.5.sp,
            ))
  }
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
                  modifier = Modifier.weight(4f).padding(10.dp),
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
                            ))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center) {
                          Text(
                              organizerName,
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

                    val currentDate = Calendar.getInstance()
                    val startOfWeek = currentDate.clone() as Calendar
                    startOfWeek[Calendar.DAY_OF_WEEK] = startOfWeek.firstDayOfWeek
                    val endOfWeek = startOfWeek.clone() as Calendar
                    endOfWeek.add(Calendar.DAY_OF_WEEK, 6)

                    val eventCalendar = Calendar.getInstance().apply { time = eventDate }

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
                    val dateString = dateFormat.format(eventDate)
                    Column {
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
                      Row {
                        Button(
                            onClick = {},
                            content = {
                              Row (){
                                /*Icon(
                                    painter = painterResource(id = R.drawable.check_circle),
                                    contentDescription = "Accept",
                                    modifier = Modifier.size(20.dp))*/
                                Text("Accept")
                              }
                            },
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = DarkCyan, contentColor = Color.White),
                            border = BorderStroke(1.dp, DarkCyan))
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = {},
                            content = {
                              Row {
                                /*Icon(
                                    painter = painterResource(id = R.drawable.close),
                                    contentDescription = "Reject",
                                    modifier = Modifier.size(20.dp))*/
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("Reject")
                              }
                            },
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = DarkCyan, contentColor = Color.White),
                            border = BorderStroke(1.dp, DarkCyan))
                      }
                    }
                  }
              Image(
                  painter = eventPicture,
                  contentDescription = "Event Picture",
                  modifier =
                      Modifier.weight(
                              2f) // Take 1/3 of the card space because of the total weight of 4
                          // (3
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

@Preview
@Composable
fun NotificationsPreview() {
  Notifications(nav = NavigationActions(rememberNavController()))
}
