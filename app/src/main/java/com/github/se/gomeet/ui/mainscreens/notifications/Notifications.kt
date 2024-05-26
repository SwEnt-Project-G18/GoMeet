package com.github.se.gomeet.ui.mainscreens.notifications

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.InviteStatus
import com.github.se.gomeet.model.user.GoMeetUser
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
  val pagerState = rememberPagerState(pageCount = { 2 })
  val screenHeight = LocalConfiguration.current.screenHeightDp.dp

  var isLoaded by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()

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
  if (isLoaded) {

    Scaffold(
        modifier = Modifier.testTag("NotificationsScreen"),
        topBar = {
          NotificationsTopBar(
              pagerState,
              coroutineScope,
              screenHeight)
        },
        bottomBar = { BottomNavigation(nav) }) { innerPadding ->
          HorizontalPager(state = pagerState, modifier = Modifier.padding(innerPadding)) { page ->
            when (page) {
              0 ->
                  PageInvitationsNotifications(
                      listEvent = eventsList,
                      userViewModel = userViewModel,
                      user = user.value!!,
                      initialClicked = false,
                      eventViewModel = eventViewModel,
                      nav = nav)
              1 -> {
                // TODO: Implement the page for messages notifications
              }
            }
          }
        }
  } else {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      CircularProgressIndicator()
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
        .filter { invitation -> invitation.status == InviteStatus.PENDING }
        .forEach { request ->
          val invitedEvent = eventViewModel.getEvent(request.eventId)
          invitedEvent?.let { event ->
            events.add(event)
            val creatorName = userViewModel.getUser(event.creator)?.username ?: "GoMeetUser"
            creatorMap[event] = creatorName
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
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotificationsTopBar(
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    screenHeight: Dp,
) {
  Column {
    TopAppBar()
    TabRow(pagerState, coroutineScope, screenHeight)
  }
}

/**
 * This composable is used to display the top app bar.
 *
 */
@Composable
fun TopAppBar() {
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
                  coroutineScope.launch { pagerState.animateScrollToPage(0) }
                }) {
              Text(
                  text = "Invitations",
                  color = MaterialTheme.colorScheme.onBackground,
                  style =
                      MaterialTheme.typography.bodyMedium.copy(
                          fontWeight =
                              if (pagerState.currentPage == 0) FontWeight.Bold
                              else FontWeight.Normal))
            }

        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier.weight(1f).height(screenHeight / 20).clickable {
                  coroutineScope.launch { pagerState.animateScrollToPage(1) }
                }) {
              Text(
                  text = "Messages",
                  color = MaterialTheme.colorScheme.onBackground,
                  style =
                      MaterialTheme.typography.bodyMedium.copy(
                          fontWeight =
                              if (pagerState.currentPage == 1) FontWeight.Bold
                              else FontWeight.Normal))
            }
      }
  Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
    val canvasWidth = size.width
    drawLine(
        color = Color.Black,
        start =
            if (pagerState.currentPage == 0) Offset(x = 0f, y = 0f)
            else Offset(x = canvasWidth / 2, y = 0f),
        end =
            if (pagerState.currentPage == 0) Offset(x = canvasWidth / 2, y = 0f)
            else Offset(x = canvasWidth, y = 0f),
        strokeWidth = 5f)
  }
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
 * @param eventViewModel the event view model
 * @param user the user receiving the notifications
 * @param initialClicked the initial state of the buttons
 * @param nav the navigation controller
 */
@Composable
fun PageInvitationsNotifications(
    listEvent: List<Event>,
    userViewModel: UserViewModel,
    eventViewModel: EventViewModel,
    user: GoMeetUser,
    initialClicked: Boolean,
    nav: NavigationActions
) {
  Column(
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        listEvent.forEach { event ->
          InvitationsNotificationsWidget(
              user,
              event = event,
              userViewModel = userViewModel,
              initialClicked = initialClicked,
              eventViewModel = eventViewModel,
              nav = nav)
        }
      }
}
