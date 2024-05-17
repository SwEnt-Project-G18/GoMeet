package com.github.se.gomeet.ui.mainscreens.events

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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import com.github.se.gomeet.model.event.InviteStatus.*
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
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
      user.value = userViewModel.getUser(currentUser)
      event.value = eventViewModel.getEvent(currentEvent)

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
                        inviteAction(toUpdate, event, nav, userViewModel, eventViewModel)
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
                          coroutineScope.launch {
                            pagerState.animateScrollToPage(TO_INVITE.ordinal)
                          }
                        }) {
                      Text(
                          text = TO_INVITE.formattedName,
                          style =
                              MaterialTheme.typography.bodyMedium.copy(
                                  fontWeight = pagerWeight(pagerState, TO_INVITE.formattedName)))
                    }

                // Pending invitations
                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier.height(screenHeight / 20).weight(1f).clickable {
                          coroutineScope.launch { pagerState.animateScrollToPage(PENDING.ordinal) }
                        }) {
                      Text(
                          text = PENDING.formattedName,
                          style =
                              MaterialTheme.typography.bodyMedium.copy(
                                  fontWeight = pagerWeight(pagerState, PENDING.formattedName)))
                    }

                // Accepted invitations
                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier.height(screenHeight / 20).weight(1f).clickable {
                          coroutineScope.launch { pagerState.animateScrollToPage(ACCEPTED.ordinal) }
                        }) {
                      Text(
                          text = ACCEPTED.formattedName,
                          style =
                              MaterialTheme.typography.bodyMedium.copy(
                                  fontWeight = pagerWeight(pagerState, ACCEPTED.formattedName)))
                    }

                // Refused invitations
                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier.height(screenHeight / 20).weight(1f).clickable {
                          coroutineScope.launch { pagerState.animateScrollToPage(REFUSED.ordinal) }
                        }) {
                      Text(
                          text = REFUSED.formattedName,
                          style =
                              MaterialTheme.typography.bodyMedium.copy(
                                  fontWeight = pagerWeight(pagerState, REFUSED.formattedName)))
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
              TO_INVITE.ordinal -> {
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
              PENDING.ordinal -> {
                PageUserInvites(
                    followersFollowingList.filter { u ->
                      u.pendingRequests.any { invitation ->
                        (invitation.eventId == event.value!!.eventID) &&
                            (invitation.status == PENDING)
                      }
                    },
                    event.value!!,
                    PENDING,
                    callback = { toUpdate.add(it) },
                    initialClicked = false)
              }
              ACCEPTED.ordinal -> {
                PageUserInvites(
                    followersFollowingList.filter { u ->
                      u.joinedEvents.contains(event.value!!.eventID)
                    },
                    event.value!!,
                    ACCEPTED,
                    callback = { toUpdate.add(it) },
                    initialClicked = false)
              }
              REFUSED.ordinal -> {
                PageUserInvites(
                    followersFollowingList.filter { u ->
                      u.pendingRequests.any { invitation ->
                        (invitation.eventId == event.value!!.eventID) &&
                            (invitation.status == REFUSED)
                      }
                    },
                    event.value!!,
                    REFUSED,
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
    user: GoMeetUser,
    event: Event,
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
        Text(text = user.username, color = MaterialTheme.colorScheme.onBackground)

        // Status text
        Text(
            text =
                when (status) {
                  PENDING -> PENDING.formattedName
                  ACCEPTED -> ACCEPTED.formattedName
                  REFUSED -> REFUSED.formattedName
                  else -> ""
                },
            modifier = Modifier.width(80.dp).testTag("InviteStatus"),
            color =
                when (status) {
                  PENDING ->
                      if (clicked) MaterialTheme.colorScheme.onBackground
                      else MaterialTheme.colorScheme.primary
                  ACCEPTED -> Color.Green
                  REFUSED -> Color.Red
                  else -> MaterialTheme.colorScheme.onBackground
                })

        // Button to invite or cancel invitation
        Button(
            onClick = {
              clicked = !clicked
              val toAdd =
                  (clicked && (status == null || status == REFUSED)) ||
                      (!clicked && (status == PENDING || status == ACCEPTED))
              Log.d("ManageInvites", "toAdd: $toAdd, clicked: $clicked, status: $status")
              callback(user.copy(pendingRequests = pendingRequests(user, event, status, toAdd)))
            },
            modifier = Modifier.height(26.dp).width(82.dp),
            contentPadding = PaddingValues(vertical = 2.dp),
            shape = RoundedCornerShape(10.dp),
            colors = manageInvitesButtonColour(status, clicked)) {
              Text(
                  text =
                      when (status) {
                        PENDING,
                        ACCEPTED -> if (clicked) "Invite" else "Cancel"
                        else -> if (!clicked) "Invite" else "Cancel"
                      },
                  color =
                      when (status) {
                        PENDING,
                        ACCEPTED -> if (clicked) Color.White else Color.DarkGray
                        else -> if (!clicked) Color.White else Color.DarkGray
                      },
                  fontSize = 12.sp)
            }
      }
}

/**
 * This function updates the list of users to invite to an event.
 *
 * @param toUpdate The list of users to update.
 * @param event The event to which the users are invited.
 * @param nav The navigation actions.
 * @param userViewModel The user view model.
 * @param eventViewModel The event view model.
 */
private fun inviteAction(
    toUpdate: List<GoMeetUser>,
    event: MutableState<Event?>,
    nav: NavigationActions,
    userViewModel: UserViewModel,
    eventViewModel: EventViewModel
) {
  toUpdate.forEach { user ->
    userViewModel.editUser(user)
    if (user.pendingRequests.any { invitation ->
      invitation.eventId == event.value!!.eventID && invitation.status == PENDING
    }) {
      eventViewModel.sendInvitation(event.value!!, user.uid)
    } else {
      eventViewModel.cancelInvitation(event.value!!, user.uid)
    }
  }
  nav.goBack()
}

/**
 * This function returns the font weight of the text of the pager buttons.
 *
 * @param pagerState The state of the pager.
 * @param button The name of the button.
 */
@OptIn(ExperimentalFoundationApi::class)
private fun pagerWeight(pagerState: PagerState, button: String): FontWeight {
  if ((pagerState.currentPage == TO_INVITE.ordinal && button == TO_INVITE.formattedName) ||
      (pagerState.currentPage == PENDING.ordinal && button == PENDING.formattedName) ||
      (pagerState.currentPage == ACCEPTED.ordinal && button == ACCEPTED.formattedName) ||
      (pagerState.currentPage == REFUSED.ordinal && button == REFUSED.formattedName))
      return FontWeight.Bold
  else return FontWeight.Normal
}

/**
 * This function updates the list of pending requests of a user.
 *
 * @param user The user to update.
 * @param event The event to which the user gets invited to.
 * @param status The status of the invitation.
 * @param toAdd True if the invitation is to be added, false if it is to be removed.
 */
fun pendingRequests(
    user: GoMeetUser,
    event: Event,
    status: InviteStatus?,
    toAdd: Boolean
): Set<Invitation> {
  if (toAdd) {
    val possiblePreviousInvitationRefused =
        user.pendingRequests.find { it.eventId == event.eventID && it.status == REFUSED }

    if (user.pendingRequests.contains(possiblePreviousInvitationRefused)) {
      return user.pendingRequests
          .map {
            if (it == possiblePreviousInvitationRefused) {
              it.copy(status = PENDING)
            } else {
              it
            }
          }
          .toSet()
    } else {
      return user.pendingRequests.plus(Invitation(event.eventID, status ?: PENDING))
    }
  } else {
    return user.pendingRequests.minus(Invitation(event.eventID, status ?: PENDING))
  }
}

/**
 * This function returns the button colour for the user invite widget.
 *
 * @param status The status of the invitation.
 * @param clicked True if the button is clicked, false otherwise.
 * @return The button colours.
 */
@Composable
private fun manageInvitesButtonColour(status: InviteStatus?, clicked: Boolean): ButtonColors {

  val c1 = MaterialTheme.colorScheme.outlineVariant
  val c2 = Color.LightGray

  return ButtonDefaults.buttonColors(
      containerColor =
          when (status) {
            PENDING,
            ACCEPTED -> if (clicked) c1 else c2
            else -> if (!clicked) c1 else c2
          })
}
