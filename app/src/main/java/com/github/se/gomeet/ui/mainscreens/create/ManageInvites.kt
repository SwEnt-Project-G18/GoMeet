package com.github.se.gomeet.ui.mainscreens.create

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.Invitation
import com.github.se.gomeet.model.event.InviteStatus
import com.github.se.gomeet.model.event.NULL_EVENT
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.model.user.NULL_USER
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import kotlinx.coroutines.launch

/**
 * This composable function represents the screen where the user can manage the invitations for one
 * of his event.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ManageInvites(
    currentUser: String,
    currentEvent: String,
    nav: NavigationActions,
    userViewModel: UserViewModel,
    eventViewModel: EventViewModel
) {

  val pagerState = rememberPagerState(pageCount = { 4 })
  val screenHeight = LocalConfiguration.current.screenHeightDp.dp
  val coroutineScope = rememberCoroutineScope()
  val user = remember { mutableStateOf<GoMeetUser?>(null) }
  var isLoaded by remember { mutableStateOf(false) }
  val event = remember { mutableStateOf<Event?>(null) }
  val followersFollowingList = remember { mutableListOf<GoMeetUser>() }
  val toUpdate = remember { mutableListOf<GoMeetUser>() }

  LaunchedEffect(Unit) {
    coroutineScope.launch {
      user.value = userViewModel.getUser(currentUser) ?: NULL_USER
      event.value = eventViewModel.getEvent(currentEvent) ?: NULL_EVENT

      val followers = user.value!!.followers
      if (followers.isNotEmpty()) {
        followers.forEach {
          val followerUser = userViewModel.getUser(it)
          followersFollowingList.add(followerUser!!)
        }
      }
      val following = user.value!!.following
      if (following.isNotEmpty()) {
        following.forEach {
          val followingUser = userViewModel.getUser(it)
          followersFollowingList.add(followingUser!!)
        }
      }
      isLoaded = true
    }
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
        Column {
          Box(contentAlignment = Alignment.Center) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                  IconButton(
                      onClick = {
                        toUpdate.forEach { user ->
                          userViewModel.editUser(user)
                          if (user.pendingRequests.any { invitation ->
                            invitation.eventId == event.value!!.eventID &&
                                invitation.status == InviteStatus.PENDING
                          }) {
                            eventViewModel.sendInvitation(event.value!!, user.uid)
                          } else {
                            eventViewModel.cancelInvitation(event.value!!, user.uid)
                          }
                        }
                        nav.goBack()
                      }) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Go back")
                      }
                }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {
                  Text(
                      text = "Manage Invites",
                      style =
                          MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                }
          }

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
                          text = "To Invite",
                          style =
                              MaterialTheme.typography.bodyMedium.copy(
                                  fontWeight =
                                      if (pagerState.currentPage == 0) FontWeight.Bold
                                      else FontWeight.Normal))
                    }

                // Pending invitations
                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier.height(screenHeight / 20).weight(1f).clickable {
                          coroutineScope.launch { pagerState.animateScrollToPage(1) }
                        }) {
                      Text(
                          text = "Pending",
                          style =
                              MaterialTheme.typography.bodyMedium.copy(
                                  fontWeight =
                                      if (pagerState.currentPage == 1) FontWeight.Bold
                                      else FontWeight.Normal))
                    }

                // Accepted invitations
                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier.height(screenHeight / 20).weight(1f).clickable {
                          coroutineScope.launch { pagerState.animateScrollToPage(2) }
                        }) {
                      Text(
                          text = "Accepted",
                          style =
                              MaterialTheme.typography.bodyMedium.copy(
                                  fontWeight =
                                      if (pagerState.currentPage == 2) FontWeight.Bold
                                      else FontWeight.Normal))
                    }

                // Refused invitations
                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier.height(screenHeight / 20).weight(1f).clickable {
                          coroutineScope.launch { pagerState.animateScrollToPage(3) }
                        }) {
                      Text(
                          text = "Refused",
                          style =
                              MaterialTheme.typography.bodyMedium.copy(
                                  fontWeight =
                                      if (pagerState.currentPage == 3) FontWeight.Bold
                                      else FontWeight.Normal))
                    }
              }
          Canvas(
              modifier =
                  Modifier.fillMaxWidth() // Ensures the Canvas takes up full screen width
                      .height(1.dp) // Sets the height of the Canvas to 1 dp
              ) {
                val canvasWidth = size.width
                drawLine(
                    color = Color.Black,
                    start =
                        when (pagerState.currentPage) {
                          0 -> Offset(x = 0f, y = 0f)
                          1 -> Offset(x = canvasWidth / 4, y = 0f)
                          2 -> Offset(x = canvasWidth / 2, y = 0f)
                          else -> Offset(x = canvasWidth * 3 / 4, y = 0f)
                        },
                    end =
                        when (pagerState.currentPage) {
                          0 -> Offset(x = canvasWidth / 4, y = 0f)
                          1 -> Offset(x = canvasWidth / 2, y = 0f)
                          2 -> Offset(x = canvasWidth * 3 / 4, y = 0f)
                          else -> Offset(x = canvasWidth, y = 0f)
                        },
                    strokeWidth = 5f)
              }
          Spacer(modifier = Modifier.height(screenHeight / 30))
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
                      }
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
                      u.pendingRequests.any { invitation ->
                        (invitation.eventId == event.value!!.eventID) &&
                            (invitation.status == InviteStatus.ACCEPTED)
                      }
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

@Composable
fun UserInviteWidget(
    user: GoMeetUser,
    event: Event,
    status: InviteStatus?,
    initialClicked: Boolean,
    callback: (GoMeetUser) -> Unit
) {

  var clicked by rememberSaveable { mutableStateOf(initialClicked) }
  Row(
      modifier = Modifier.fillMaxWidth().padding(start = 15.dp, end = 15.dp).height(50.dp),
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
        Text(text = user.username, color = MaterialTheme.colorScheme.onBackground)

        // Status text
        Text(
            text =
                when (status) {
                  null -> ""
                  InviteStatus.PENDING -> "Pending"
                  InviteStatus.ACCEPTED -> "Accepted"
                  InviteStatus.REFUSED -> "Refused"
                },
            modifier = Modifier.width(70.dp),
            color =
                when (status) {
                  null -> MaterialTheme.colorScheme.onBackground
                  InviteStatus.PENDING ->
                      if (clicked) MaterialTheme.colorScheme.onBackground
                      else MaterialTheme.colorScheme.primary
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
                          if (toAdd)
                              user.pendingRequests.plus(
                                  Invitation(event.eventID, status ?: InviteStatus.PENDING))
                          else
                              user.pendingRequests.minus(
                                  Invitation(event.eventID, status ?: InviteStatus.PENDING))))
            },
            modifier = Modifier.height(26.dp).width(82.dp),
            contentPadding = PaddingValues(vertical = 2.dp),
            shape = RoundedCornerShape(10.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        when (status) {
                          null -> if (!clicked) DarkCyan else Color.LightGray
                          InviteStatus.PENDING -> if (clicked) DarkCyan else Color.LightGray
                          InviteStatus.ACCEPTED -> if (clicked) DarkCyan else Color.LightGray
                          InviteStatus.REFUSED -> if (!clicked) DarkCyan else Color.LightGray
                        })) {
              Text(
                  text =
                      when (status) {
                        null -> if (!clicked) "Invite" else "Cancel"
                        InviteStatus.PENDING -> if (clicked) "Invite" else "Cancel"
                        InviteStatus.ACCEPTED -> if (clicked) "Invite" else "Cancel"
                        InviteStatus.REFUSED -> if (!clicked) "Invite" else "Cancel"
                      },
                  color =
                      when (status) {
                        null -> if (!clicked) Color.White else Color.DarkGray
                        InviteStatus.PENDING -> if (clicked) Color.White else Color.DarkGray
                        InviteStatus.ACCEPTED -> if (clicked) Color.White else Color.DarkGray
                        InviteStatus.REFUSED -> if (!clicked) Color.White else Color.DarkGray
                      },
                  fontSize = 12.sp)
            }
      }
}
