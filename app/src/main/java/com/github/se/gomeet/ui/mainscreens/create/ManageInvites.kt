package com.github.se.gomeet.ui.mainscreens.create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.EventInviteUsers
import com.github.se.gomeet.model.event.InviteStatus
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.launch

/**
 * This composable function represents the screen where the user can manage the invitations for one
 * of his event.
 */
@Composable
fun ManageInvites(
    currentUser: String,
    currentEvent: String,
    nav: NavigationActions,
    userViewModel: UserViewModel,
    eventViewModel: EventViewModel,
    eventInviteViewModel: EventInviteViewModel
) {

  var selectedFilter by remember { mutableStateOf("All") }
  val followersList = remember { mutableListOf<GoMeetUser>() }
  val coroutineScope = rememberCoroutineScope()
  val user = remember { mutableStateOf<GoMeetUser?>(null) }
  val event = remember { mutableStateOf<Event?>(null) }
  val eventInviteUsers = remember { mutableStateOf<EventInviteUsers?>(null) }

  LaunchedEffect(Unit) {
    coroutineScope.launch {
      user.value = userViewModel.getUser(currentUser)
      event.value = eventViewModel.getEvent(currentEvent)
      eventInviteUsers.value = eventInviteViewModel.getUsersInvitedToEvent(event.value!!.uid)

      while (user.value == null || event.value == null) {
        TimeUnit.SECONDS.sleep(1)
      }
      val followers = user.value!!.followers
      if (followers.isNotEmpty()) {
        followers.forEach {
          val followerUser = userViewModel.getUser(it)
          followersList.add(followerUser!!)
        }
      }
    }
  }

  fun onFilterButtonClick(filterType: String) {
    selectedFilter = if (selectedFilter == filterType) "All" else filterType
  }

  Scaffold(
      modifier = Modifier.testTag("ManageInvitesScreen"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { selectedTab ->
              nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
            },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = Route.EVENTS)
      },
      topBar = {
        Row {
          Text(
              text = "Manage Invites",
              modifier = Modifier.padding(start = 15.dp, top = 15.dp, end = 15.dp, bottom = 15.dp),
              color = DarkCyan,
              fontStyle = FontStyle.Normal,
              fontWeight = FontWeight.SemiBold,
              fontFamily = FontFamily.Default,
              textAlign = TextAlign.Start,
              style = MaterialTheme.typography.headlineLarge)
        }
      }) { innerPadding ->
        if (isLoaded) {
          HorizontalPager(state = pagerState, modifier = Modifier.padding(innerPadding)) { page ->
            when (page) {
              0 -> {
                PageUserInvites(
                    followersFollowingList.filter { u ->
                      !u.pendingRequests.any { invitation ->
                        invitation.eventId == event.value!!.eventID
                      } && !u.joinedEvents.contains(event.value!!.eventID)
                    },
                    event.value!!,
                    null,
                    callback = { toUpdate.add(it) },
                    initialClicked = false)
              }
              1 -> {
                PageUserInvites(
                    followersFollowingList.filter { u ->
                      u.pendingRequests.any { invitation ->
                        (invitation.eventId == event.value!!.eventID) &&
                            (invitation.status == InviteStatus.PENDING)
                      }
                    },
                    event.value!!,
                    InviteStatus.PENDING,
                    callback = { toUpdate.add(it) },
                    initialClicked = false)
              }
              2 -> {
                PageUserInvites(
                    followersFollowingList.filter { u ->
                      u.joinedEvents.contains(event.value!!.eventID)
                    },
                    event.value!!,
                    InviteStatus.ACCEPTED,
                    callback = { toUpdate.add(it) },
                    initialClicked = false)
              }
              3 -> {
                PageUserInvites(
                    followersFollowingList.filter { u ->
                      u.pendingRequests.any { invitation ->
                        (invitation.eventId == event.value!!.eventID) &&
                            (invitation.status == InviteStatus.REFUSED)
                      }
                    },
                    event.value!!,
                    InviteStatus.REFUSED,
                    callback = { toUpdate.add(it) },
                    initialClicked = false)
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

/**
 * This composable function represents the list of users that can be invited to an event.
 *
 * @param list the list of users that can be invited
 * @param currentEvent the event for which the users are invited
 * @param status the status of the invitation
 * @param callback the callback function to update the list of users to invite
 * @param initialClicked the initial state of the button
 */
@Composable
fun PageUserInvites(
    list: List<GoMeetUser>,
    currentEvent: Event,
    status: InviteStatus?,
    callback: (GoMeetUser) -> Unit,
    initialClicked: Boolean
) {

  Column(
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize()) {
        list.forEach { follower ->
          UserInviteWidget(follower, currentEvent, status, initialClicked, callback)
        }
      }
}

/**
 * This composable function represents the user invite widget when the user can be invited to an
 * event.
 *
 * @param user the user that can be invited
 * @param event the event for which the user is invited
 * @param status the status of the invitation
 * @param initialClicked the initial state of the button
 * @param callback the callback function to update the list of users to invite
 */
@Composable
fun UserInviteWidget(
    username: String,
    userUid: String,
    eventUid: String,
    status: InviteStatus?,
    initialClicked: Boolean,
    callback: (GoMeetUser) -> Unit
) {

  var clicked by rememberSaveable { mutableStateOf(initialClicked) }
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .padding(start = 15.dp, end = 15.dp)
              .height(50.dp)
              .testTag("UserInviteWidget"),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically) {
        // Profile picture
        Image(
            modifier =
                Modifier.size(40.dp)
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colorScheme.background),
            painter = painterResource(id = R.drawable.gomeet_logo),
            contentDescription = "profile picture",
            contentScale = ContentScale.None)

        // Username text
        Text(text = username, color = MaterialTheme.colorScheme.onBackground)

        // Status text
        Text(
            text =
                when (status) {
                  null -> ""
                  InviteStatus.PENDING -> "Pending"
                  InviteStatus.ACCEPTED -> "Accepted"
                  InviteStatus.REFUSED -> "Refused"
                },
            modifier = Modifier.width(80.dp).testTag("InviteStatus"),
            color =
                when (status) {
                  null -> MaterialTheme.colorScheme.onBackground
                  InviteStatus.PENDING -> MaterialTheme.colorScheme.onBackground
                  InviteStatus.ACCEPTED -> Color.Green
                  InviteStatus.REFUSED -> Color.Red
                })

        // Button to invite or cancel invitation
        Button(
            onClick = {
              clicked = !clicked
              val toAdd =
                  (clicked && (status == null || status == InviteStatus.REFUSED)) ||
                      (!clicked &&
                          (status == InviteStatus.PENDING || status == InviteStatus.ACCEPTED))
              Log.d("ManageInvites", "toAdd: $toAdd, clicked: $clicked, status: $status")
              callback(
                  user.copy(
                      pendingRequests =
                          if (toAdd) {
                            val possiblePreviousInvitationRefused =
                                user.pendingRequests.find {
                                  it.eventId == event.eventID && it.status == InviteStatus.REFUSED
                                }

                            if (user.pendingRequests.contains(possiblePreviousInvitationRefused)) {
                              user.pendingRequests
                                  .map {
                                    if (it == possiblePreviousInvitationRefused) {
                                      it.copy(status = InviteStatus.PENDING)
                                    } else {
                                      it
                                    }
                                  }
                                  .toSet()
                            } else {
                              user.pendingRequests.plus(
                                  Invitation(event.eventID, status ?: InviteStatus.PENDING))
                            }
                          } else {
                            user.pendingRequests.minus(
                                Invitation(event.eventID, status ?: InviteStatus.PENDING))
                          }))
            },
            modifier = Modifier.height(26.dp).width(82.dp),
            contentPadding = PaddingValues(vertical = 2.dp),
            shape = RoundedCornerShape(10.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        when (status) {
                          null ->
                              if (!clicked) MaterialTheme.colorScheme.outlineVariant
                              else Color.LightGray
                          InviteStatus.PENDING ->
                              if (clicked) MaterialTheme.colorScheme.outlineVariant
                              else Color.LightGray
                          InviteStatus.ACCEPTED ->
                              if (clicked) MaterialTheme.colorScheme.outlineVariant
                              else Color.LightGray
                          InviteStatus.REFUSED ->
                              if (!clicked) MaterialTheme.colorScheme.outlineVariant
                              else Color.LightGray
                        })) {
              Text(
                  text =
                      when (status) {
                        null -> "Invite"
                        InviteStatus.PENDING -> "Cancel"
                        InviteStatus.ACCEPTED -> "Cancel"
                        InviteStatus.REFUSED -> "Invite"
                      },
                  color =
                      when (status) {
                        null -> Color.White
                        InviteStatus.PENDING -> Color.DarkGray
                        InviteStatus.ACCEPTED -> Color.White
                        InviteStatus.REFUSED -> Color.DarkGray
                      },
                  fontSize = 12.sp)
            }
      }
}

/*
@Preview
@Composable
fun ManageInvitesPreview() {
    ManageInvites("eventId",nav = NavigationActions(rememberNavController()))

}

 */
