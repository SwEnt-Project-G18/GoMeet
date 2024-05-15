package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.repository.EventRepository
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Notifications(nav: NavigationActions, currentUserID: String) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val userViewModel = UserViewModel(UserRepository(Firebase.firestore))
    val eventViewModel = EventViewModel(null, EventRepository(Firebase.firestore))

    var isLoaded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val toUpdate = remember { mutableListOf<Event>() }
    val user = remember { mutableStateOf<GoMeetUser?>(null) }
    val eventsList =
        remember { mutableListOf<Event>() } // list of events for which the user has received an invitation

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val currentUser = userViewModel.getUser(currentUserID)

            if (currentUser != null) {
                user.value = currentUser

                currentUser.pendingRequests.forEach { request ->
                    val invitedEvent = eventViewModel.getEvent(request.eventId)
                    if (invitedEvent != null) {
                        eventsList.add(invitedEvent)
                    }
                }
            }

            isLoaded = true
        }
    }

    Scaffold(
        modifier = Modifier.testTag("NotificationsScreen"),
        topBar = {
            Column {
                Box(contentAlignment = Alignment.Center) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                toUpdate.forEach {
                                    // TODO: Update the user in the database userViewModel.updateUser(it)
                                }
                                nav.goBack()
                            }) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Go back"
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Notifications",
                            style =
                            MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // Invitations notifications
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier =
                        Modifier
                            .weight(1f)
                            .height(screenHeight / 20)
                            .clickable {
                                coroutineScope.launch { pagerState.animateScrollToPage(0) }
                            }) {
                        Text(
                            text = "Invitations",
                            style =
                            MaterialTheme.typography.bodyMedium.copy(
                                fontWeight =
                                if (pagerState.currentPage == 0) FontWeight.Bold
                                else FontWeight.Normal
                            )
                        )
                    }

                    // Messages notifications
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier =
                        Modifier
                            .height(screenHeight / 20)
                            .weight(1f)
                            .clickable {
                                coroutineScope.launch { pagerState.animateScrollToPage(1) }
                            }) {
                        Text(
                            text = "Messages",
                            style =
                            MaterialTheme.typography.bodyMedium.copy(
                                fontWeight =
                                if (pagerState.currentPage == 1) FontWeight.Bold
                                else FontWeight.Normal
                            )
                        )
                    }
                }
                Canvas(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                ) {
                    val canvasWidth = size.width
                    drawLine(
                        color = Color.Black,
                        start =
                        when (pagerState.currentPage) {
                            0 -> Offset(x = 0f, y = 0f)
                            else -> Offset(x = canvasWidth / 2, y = 0f)
                        },
                        end =
                        when (pagerState.currentPage) {
                            0 -> Offset(x = canvasWidth / 2, y = 0f)
                            else -> Offset(x = canvasWidth, y = 0f)
                        },
                        strokeWidth = 5f
                    )
                }
                Spacer(modifier = Modifier.height(screenHeight / 30))
            }
        },
        bottomBar = {
            BottomNavigationMenu(
                onTabSelect = { selectedTab ->
                    nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
                },
                tabList = TOP_LEVEL_DESTINATIONS,
                selectedItem = Route.NOTIFICATIONS
            )
        }) { innerPadding ->

        if (isLoaded) {
            HorizontalPager(state = pagerState, modifier = Modifier.padding(innerPadding)) { page ->
                when (page) {
                    0 -> {
                        PageInvitationsNotifications(
                            listEvent = eventsList,
                            userViewModel = userViewModel,
                            coroutineScope = coroutineScope,
                            initialClicked = false,
                            callback = { user ->
                                toUpdate.add(user)
                            }
                        )
                    }

                    1 -> {
                        // TODO: Implement the page for messages notifications
                    }
                }
            }

        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun PageInvitationsNotifications(
    listEvent: List<Event>,
    userViewModel: UserViewModel,
    coroutineScope: CoroutineScope,
    initialClicked: Boolean,
    callback: (Event) -> Unit
) {
    val eventToCreatorMap = mutableMapOf<Event, String>()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            listEvent.forEach { event ->
                eventToCreatorMap[event] = userViewModel.getUser(event.creator)?.username ?: "GoMeetUser"
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
        listEvent.forEach { event ->
            eventToCreatorMap[event]?.let {
                InvitationsNotificationsWidget(creatorID = it,
                    eventTitle = event.title,
                    eventDate = event.date,
                    eventPicture = painterResource(id = R.drawable.gomeet_logo), // TODO: Later, need to use the event's image
                    verified = false,
                    initialClicked = initialClicked,
                    callback = callback)
            }
        }
    }
}

@Composable
fun InvitationsNotificationsWidget(
    creatorID: String,
    eventTitle: String,
    eventDate: LocalDate,
    eventPicture: Painter,
    verified: Boolean,
    initialClicked: Boolean,
    callback: (Event) -> Unit
) {
    var clicked by rememberSaveable { mutableStateOf(initialClicked) }

  val screenWidth = LocalConfiguration.current.screenWidthDp.dp
  val density = LocalDensity.current

  // Example logic to calculate text size based on screen width
  val smallTextSize = with(density) { screenWidth.toPx() / 85 }
  val bigTextSize = with(density) { screenWidth.toPx() / 60 }

  Row(Modifier.padding(start = 10.dp)) {
    Text(
        text = creatorID,
        style =
            TextStyle(
                fontSize = 11.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily(Font(R.font.roboto)),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF202124),
                letterSpacing = 0.5.sp,
            ),
        modifier = Modifier.testTag("InviterUserName"))
    Spacer(modifier = Modifier.width(2.5.dp))
    Text(
        text = "invited you to attend ",
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
      Modifier
          .fillMaxWidth()
          .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 5.dp)
          .testTag("EventCard"),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      border = BorderStroke(2.dp, DarkCyan)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround) {
              Column(
                  modifier = Modifier
                      .weight(4f)
                      .padding(10.dp),
                  horizontalAlignment = Alignment.Start, // Align text horizontally to center
                  verticalArrangement = Arrangement.Center) {
                    Text(
                        text = eventTitle,
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
                              creatorID,
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
                                modifier = Modifier
                                    .padding(5.dp)
                                    .size(smallTextSize.dp * (1.4f))) {
                                  Image(
                                      painter = painterResource(id = R.drawable.verified),
                                      contentDescription = "Verified",
                                  )
                                }
                          }
                        }

                    // Get the current date and time
                    val currentDate = LocalDate.now()

                    // Get the start of the week
                    val startOfWeek = currentDate.with(java.time.DayOfWeek.MONDAY)

                    // Get the end of the week
                    val endOfWeek = startOfWeek.plusDays(6)

                    // Convert eventDate to LocalDateTime (assuming eventDate has both date and
                    // time)
                    val eventDateTime = eventDate.atStartOfDay()

                    // Determine if the event date is within this week
                    val isThisWeek = eventDateTime.toLocalDate() in startOfWeek..endOfWeek

                    // Format based on whether the date is in the current week
                    val dateFormat =
                        if (isThisWeek) {
                          SimpleDateFormat("EEEE - HH:mm", Locale.getDefault()) // "Tuesday"
                        } else {
                          SimpleDateFormat("dd/MM/yy - HH:mm", Locale.getDefault()) // "05/12/24"
                        }

                    // Convert the Date object to a formatted String
                    val dateString =
                        dateFormat.format(
                            Date.from(eventDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))

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
                              ),
                          modifier = Modifier.testTag("EventDate"))
                      Row {
                        Button(
                            onClick = {},
                            content = {
                              Row {
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
                            border = BorderStroke(1.dp, DarkCyan),
                            modifier = Modifier.testTag("AcceptButton"))
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
                            border = BorderStroke(1.dp, DarkCyan),
                            modifier = Modifier.testTag("RejectButton"))
                      }
                    }
                  }
              Image(
                  painter = eventPicture,
                  contentDescription = "Event Picture",
                  modifier =
                  Modifier
                      .weight(
                          2f
                      ) // Take 1/3 of the card space because of the total weight of 4
                      // (3
                      // for the column and 1 for this image)
                      .fillMaxHeight() // Fill the height of the Row
                      .aspectRatio(
                          3f / 1.75f
                      ) // Maintain an aspect ratio of 3:2, change it as needed
                      .clipToBounds()
                      .padding(0.dp)
                      .testTag("EventImage"), // Clip the image if it overflows its bounds
                  contentScale = ContentScale.Crop, // Crop the image to fit the aspect ratio
              )
            }
      }
}

@Preview
@Composable
fun NotificationsPreview() {
  Notifications(nav = NavigationActions(rememberNavController()), currentUserID = "1234")
}
