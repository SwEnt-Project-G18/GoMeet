package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.InviteStatus.*
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.mainscreens.LoadingText
import com.github.se.gomeet.ui.mainscreens.profile.NotificationPagerState.*
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * This composable function displays the notifications screen.
 *
 * @param nav The navigation actions.
 * @param userViewModel The user view model.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Notifications(nav: NavigationActions, userViewModel: UserViewModel) {
  val eventViewModel = EventViewModel(null)
  val pagerState = rememberPagerState(pageCount = { NotificationPagerState.entries.size })
  val screenHeight = LocalConfiguration.current.screenHeightDp.dp

  var isLoaded by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()

  val toUpdate = remember { mutableListOf<Event>() }
  val user = remember { mutableStateOf<GoMeetUser?>(null) }
  val eventsList = remember { mutableStateListOf<Event>() }
  val eventToCreatorMap = remember { mutableStateMapOf<Event, String>() }
  val currentUserID = userViewModel.currentUID!!

  LaunchedEffect(Unit) {
    coroutineScope.launch {
      fetchUserAndEvents(
          userViewModel, eventViewModel, currentUserID, user, eventsList, eventToCreatorMap)
      isLoaded = true
    }
  }

  Scaffold(
      modifier = Modifier.testTag("NotificationsScreen"),
      topBar = {
        NotificationsTopBar(
            pagerState,
            coroutineScope,
            screenHeight,
            nav,
            currentUserID,
            eventViewModel,
            userViewModel,
            toUpdate)
      },
      bottomBar = { BottomNavigation(nav) }) { innerPadding ->
        if (isLoaded) {
          HorizontalPager(state = pagerState, modifier = Modifier.padding(innerPadding)) { page ->
            when (page) {
              0 ->
                  PageInvitationsNotifications(
                      listEvent = eventsList,
                      userViewModel = userViewModel,
                      currentUserId = currentUserID,
                      initialClicked = false,
                      callback = { event -> toUpdate.add(event) })
              1 -> {
                // TODO: Implement the page for messages notifications
              }
            }
          }
        } else {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LoadingText()
          }
        }
      }
}

/**
 * This function is used to fetch the user and events needed for the notifications.
 *
 * @param userViewModel the user view model
 * @param eventViewModel the event view model
 * @param currentUserID the userID of the user receiving the notifications
 * @param user the user to update
 * @param eventsList the list of events to update
 * @param eventToCreatorMap the map of events to creators to update
 */
suspend fun fetchUserAndEvents(
    userViewModel: UserViewModel,
    eventViewModel: EventViewModel,
    currentUserID: String,
    user: MutableState<GoMeetUser?>,
    eventsList: SnapshotStateList<Event>,
    eventToCreatorMap: SnapshotStateMap<Event, String>
) {
  val currentUser = userViewModel.getUser(currentUserID)

  currentUser?.let {
    user.value = it
    val events = mutableListOf<Event>()
    val creatorMap = mutableMapOf<Event, String>()

    it.pendingRequests
        .filter { invitation -> invitation.status == PENDING }
        .forEach { request ->
          val invitedEvent = eventViewModel.getEvent(request.eventId)
          invitedEvent?.let { event ->
            events.add(event)
            creatorMap[event] = userViewModel.getUser(event.creator)?.username ?: "GoMeetUser"
          }
        }

    eventsList.clear()
    eventsList.addAll(events)
    eventToCreatorMap.clear()
    eventToCreatorMap.putAll(creatorMap)
  }
}

/**
 * This composable is used to display the top bar of the notifications page.
 *
 * @param pagerState the pager state
 * @param coroutineScope the coroutine scope
 * @param screenHeight the screen height
 * @param nav the navigation actions
 * @param currentUserID the userID of the user receiving the notifications
 * @param eventViewModel the event view model
 * @param userViewModel the user view model
 * @param toUpdate the list of events to update
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotificationsTopBar(
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    screenHeight: Dp,
    nav: NavigationActions,
    currentUserID: String,
    eventViewModel: EventViewModel,
    userViewModel: UserViewModel,
    toUpdate: List<Event>
) {
  Column {
    TopAppBar(nav, currentUserID, eventViewModel, userViewModel, toUpdate)
    TabRow(pagerState, coroutineScope, screenHeight)
  }
}

/**
 * This composable is used to display the top app bar.
 *
 * @param nav the navigation actions
 * @param currentUserID the userID of the user receiving the notifications
 * @param eventViewModel the event view model
 * @param userViewModel the user view model
 * @param toUpdate the list of events to update
 */
@Composable
fun TopAppBar(
    nav: NavigationActions,
    currentUserID: String,
    eventViewModel: EventViewModel,
    userViewModel: UserViewModel,
    toUpdate: List<Event>
) {
  Box(contentAlignment = Alignment.Center) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
      IconButton(
          onClick = {
            toUpdate.forEach { event ->
              eventViewModel.editEvent(event)

              if (event.participants.contains(currentUserID)) {
                userViewModel.userAcceptsInvitation(event.eventID, currentUserID)
              } else {
                userViewModel.userRefusesInvitation(event.eventID, currentUserID)
              }
            }
            nav.goBack()
          }) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Go back",
                tint = MaterialTheme.colorScheme.onBackground)
          }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
          Text(
              text = "Notifications",
              color = MaterialTheme.colorScheme.onBackground,
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
        }
  }
}

/**
 * This composable is used to display the tab row showing "Invitations" and "Messages" texts.
 *
 * @param pagerState the pager state
 * @param coroutineScope the coroutine scope
 * @param screenHeight the screen height
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabRow(pagerState: PagerState, coroutineScope: CoroutineScope, screenHeight: Dp) {
  Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceAround) {
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier.weight(1f).height(screenHeight / 20).clickable {
                  coroutineScope.launch { pagerState.animateScrollToPage(INVITATIONS.ordinal) }
                }) {
              Text(
                  text = INVITATIONS.str,
                  color = MaterialTheme.colorScheme.onBackground,
                  style =
                      MaterialTheme.typography.bodyMedium.copy(
                          fontWeight =
                              when (pagerState.currentPage) {
                                INVITATIONS.ordinal -> FontWeight.Bold
                                else -> FontWeight.Normal
                              }))
            }

        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier.weight(1f).height(screenHeight / 20).clickable {
                  coroutineScope.launch { pagerState.animateScrollToPage(MESSAGES.ordinal) }
                }) {
              Text(
                  text = MESSAGES.str,
                  color = MaterialTheme.colorScheme.onBackground,
                  style =
                      MaterialTheme.typography.bodyMedium.copy(
                          fontWeight =
                              when (pagerState.currentPage) {
                                MESSAGES.ordinal -> FontWeight.Bold
                                else -> FontWeight.Normal
                              }))
            }
      }
  Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
    val canvasWidth = size.width
    drawLine(
        color = Color.Black,
        start =
            when (pagerState.currentPage) {
              INVITATIONS.ordinal -> Offset(x = 0f, y = 0f)
              MESSAGES.ordinal -> Offset(x = canvasWidth / 2, y = 0f)
              else -> Offset(x = 0f, y = 0f)
            },
        end =
            when (pagerState.currentPage) {
              INVITATIONS.ordinal -> Offset(x = canvasWidth / 2, y = 0f)
              MESSAGES.ordinal -> Offset(x = canvasWidth, y = 0f)
              else -> Offset(x = 0f, y = 0f)
            },
        strokeWidth = 5f)
  }
  Spacer(modifier = Modifier.height(screenHeight / 30))
}

/**
 * This composable is used to display the bottom navigation bar.
 *
 * @param nav the navigation actions
 */
@Composable
fun BottomNavigation(nav: NavigationActions) {
  BottomNavigationMenu(
      onTabSelect = { selectedTab ->
        nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
      },
      tabList = TOP_LEVEL_DESTINATIONS,
      selectedItem = Route.NOTIFICATIONS)
}

/**
 * This composable is used to display the invitations notifications page.
 *
 * @param listEvent the list of events to display
 * @param userViewModel the user view model
 * @param currentUserId the user receiving the notifications
 * @param initialClicked the initial state of the buttons
 * @param callback the callback to update the event
 */
@Composable
fun PageInvitationsNotifications(
    listEvent: List<Event>,
    userViewModel: UserViewModel,
    currentUserId: String,
    initialClicked: Boolean,
    callback: (Event) -> Unit
) {
  Column(
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        listEvent.forEach { event ->
          InvitationsNotificationsWidget(
              event = event,
              userViewModel = userViewModel,
              currentUserId = currentUserId,
              initialClicked = initialClicked,
              callback = callback)
        }
      }
}

/**
 * This composable is used to display the invitations notifications widgets.
 *
 * @param event the event to display
 * @param userViewModel the user view model
 * @param currentUserId the user receiving the notifications
 * @param initialClicked the initial state of the buttons
 * @param callback the callback to update the event
 */
@Composable
fun InvitationsNotificationsWidget(
    event: Event,
    userViewModel: UserViewModel,
    currentUserId: String,
    initialClicked: Boolean,
    callback: (Event) -> Unit
) {
  var clicked by rememberSaveable { mutableStateOf(initialClicked) }

  val configuration = LocalConfiguration.current
  val screenWidth = configuration.screenWidthDp.dp
  val density = LocalDensity.current

  val smallTextSize = with(density) { screenWidth.toPx() / 85 }
  val bigTextSize = with(density) { screenWidth.toPx() / 60 }

  val dayString = event.getDateString()
  val timeString = event.getTimeString()

  Card(
      modifier =
          Modifier.fillMaxWidth()
              .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 5.dp)
              .testTag("EventCard"),
      colors =
          CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround) {
              Column(
                  modifier = Modifier.weight(4f).padding(15.dp),
                  horizontalAlignment = Alignment.Start,
                  verticalArrangement = Arrangement.Center) {
                    Text(
                        text = event.title,
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
                          LaunchedEffect(event.creator) {
                            username = userViewModel.getUsername(event.creator)
                          }

                          username?.let {
                            Text(
                                it,
                                color = MaterialTheme.colorScheme.onBackground,
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
                        }

                    Text(
                        "$dayString - $timeString",
                        color = MaterialTheme.colorScheme.onBackground,
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
                  painter =
                      painterResource(
                          id = R.drawable.gomeet_logo), // Use the event picture if available
                  contentDescription = "Event Picture",
                  modifier =
                      Modifier.weight(3f)
                          .fillMaxHeight()
                          .aspectRatio(3f / 1.75f)
                          .clipToBounds()
                          .padding(0.dp)
                          .testTag("EventPicture"),
                  contentScale = ContentScale.Crop,
              )
            }

        // Accept and Decline buttons
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
              Button(
                  onClick = {
                    clicked = true
                    callback(
                        event.copy(
                            pendingParticipants = event.pendingParticipants.minus(currentUserId),
                            participants = event.participants.plus(currentUserId)))
                  },
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = Color.Green, contentColor = Color.White),
                  border = BorderStroke(1.dp, Color.Green),
                  enabled = !clicked,
                  modifier = Modifier.testTag("AcceptButton")) {
                    Text(ACCEPTED.button)
                  }

              Spacer(modifier = Modifier.width(10.dp))

              Button(
                  onClick = {
                    clicked = true
                    callback(
                        event.copy(
                            pendingParticipants = event.pendingParticipants.minus(currentUserId)))
                  },
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = Color.Red, contentColor = Color.White),
                  border = BorderStroke(1.dp, Color.Red),
                  enabled = !clicked,
                  modifier = Modifier.testTag("DeclineButton")) {
                    Text(REFUSED.button)
                  }
            }
      }
}

private enum class NotificationPagerState(val str: String) {
  INVITATIONS("Invitations"),
  MESSAGES("Messages")
}
