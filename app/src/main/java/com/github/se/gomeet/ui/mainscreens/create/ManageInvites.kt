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
import com.github.se.gomeet.model.event.InviteStatus
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.ui.theme.NavBarUnselected
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    eventViewModel: EventViewModel
) {

  var selectedFilter by remember { mutableStateOf("All") }
  val followersList = remember { mutableListOf<GoMeetUser>() }
  val coroutineScope = rememberCoroutineScope()
  val user = remember { mutableStateOf<GoMeetUser?>(null) }
  val event = remember { mutableStateOf<Event?>(null) }
  val usersInvitedToEvent = remember { mutableListOf<GoMeetUser?>() }

  LaunchedEffect(Unit) {
    coroutineScope.launch {
      user.value = userViewModel.getUser(currentUser)
      event.value = eventViewModel.getEvent(currentEvent)

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

      val pendingInvitations =
          eventViewModel.getEvent(currentEvent)?.pendingParticipants?.toMutableList()

      val participantsOfEvent = eventViewModel.getEvent(currentEvent)?.participants?.toMutableList()

      if (pendingInvitations != null) {
        if (pendingInvitations.isNotEmpty()) {
          pendingInvitations.forEach { invitedUser ->
            val userInvited = userViewModel.getUser(invitedUser)
            usersInvitedToEvent.add(userInvited)
          }
        }
      }

      if (participantsOfEvent != null) {
        if (participantsOfEvent.isNotEmpty()) {
          participantsOfEvent.forEach { invitedUser ->
            val userInvited = userViewModel.getUser(invitedUser)
            usersInvitedToEvent.add(userInvited)
          }
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
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(innerPadding).fillMaxSize()) {
              Row(
                  modifier =
                      Modifier.fillMaxWidth().padding(start = 15.dp, end = 15.dp, bottom = 15.dp),
                  horizontalArrangement = Arrangement.spacedBy(10.dp),
                  verticalAlignment = Alignment.Top) {
                    Button(
                        onClick = { onFilterButtonClick("Uninvited") },
                        modifier = Modifier.height(40.dp).weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    if (selectedFilter == "Uninvited") DarkCyan
                                    else NavBarUnselected,
                                contentColor =
                                    if (selectedFilter == "Uninvited") Color.White else DarkCyan),
                        border = BorderStroke(1.dp, DarkCyan)) {
                          Text(text = "Uninvited", color = Color.Black)
                        }

                    Button(
                        onClick = { onFilterButtonClick("Invited") },
                        modifier = Modifier.height(40.dp).weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    if (selectedFilter == "Invited") DarkCyan else NavBarUnselected,
                                contentColor =
                                    if (selectedFilter == "Invited") Color.White else DarkCyan),
                        border = BorderStroke(1.dp, DarkCyan)) {
                          Text(text = "Invited", color = Color.Black)
                        }

                    Button(
                        onClick = { onFilterButtonClick("Accepted") },
                        modifier = Modifier.height(40.dp).weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    if (selectedFilter == "Accepted") DarkCyan
                                    else NavBarUnselected,
                                contentColor =
                                    if (selectedFilter == "Accepted") Color.White else DarkCyan),
                        border = BorderStroke(1.dp, DarkCyan)) {
                          Text(text = "Accepted", color = Color.Black)
                        }
                  }

              // Display the list of followers to manage our invitations
              Column(modifier = Modifier.verticalScroll(rememberScrollState()).fillMaxSize()) {
                followersList.forEach { follower ->
                  //                  val invitationStatus =
                  //                      follower.pendingRequests
                  //                          .find { it.userId == follower.uid && it.eventId ==
                  // currentEvent }
                  //                          ?.status
                  UserInviteWidget(
                      follower.uid,
                      follower.username,
                      currentEvent,
                      // invitationStatus,
                      userViewModel,
                      eventViewModel)
                }

                //                usersInvitedToEvent.forEach { userInvited ->
                //                  if (userInvited != user.value) {
                //                    UserInviteWidget(
                //                        userInvited!!.uid,
                //                        userInvited.username,
                //                        currentEvent,
                //                        //                        status =
                //                        //                            userInvited.pendingRequests
                //                        //                                .find {
                //                        //                                  it.userId ==
                // user.value!!.uid &&
                //                        // it.eventId == currentEvent
                //                        //                                }
                //                        //                                ?.status,
                //                        userViewModel = userViewModel,
                //                        eventViewModel = eventViewModel)
                //                  }
                //                }
              }
            }
      }
}

@Composable
fun UserInviteWidget(
    userID: String,
    username: String,
    eventID: String,
    // status: InviteStatus?,
    userViewModel: UserViewModel,
    eventViewModel: EventViewModel
) {
  var status by remember { mutableStateOf<InviteStatus?>(null) }
  var event by remember { mutableStateOf<Event?>(null) }

  LaunchedEffect(Unit) {
    status =
        userViewModel
            .getUser(userID)
            ?.pendingRequests
            ?.find { it.userId == userID && it.eventId == eventID }
            ?.status

    event = eventViewModel.getEvent(eventID)
  }

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
            modifier = Modifier.width(70.dp),
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
              CoroutineScope(Dispatchers.Main).launch {
                when (status) {
                  null -> {
                    userViewModel.gotInvitation(eventID, userID)
                    if (event != null) {
                      eventViewModel.sendInvitation(event!!, userID)
                    }
                  }
                  InviteStatus.PENDING -> {
                    userViewModel.invitationCanceled(eventID, userID)
                    if (event != null) {
                      eventViewModel.cancelInvitation(event!!, userID)
                    }
                  }
                  InviteStatus.ACCEPTED -> {
                    userViewModel.gotKickedFromEvent(eventID, userID)
                    if (event != null) {
                      eventViewModel.kickParticipant(event!!, userID)
                    }
                  }
                  InviteStatus.REFUSED -> {
                    userViewModel.gotInvitation(eventID, userID)
                    if (event != null) {
                      eventViewModel.sendInvitation(event!!, userID)
                    }
                  }
                }
              }
            },
            modifier = Modifier.height(26.dp).width(82.dp),
            contentPadding = PaddingValues(vertical = 2.dp),
            shape = RoundedCornerShape(10.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        when (status) {
                          null -> DarkCyan
                          InviteStatus.PENDING -> Color.LightGray
                          InviteStatus.ACCEPTED -> Color.Red
                          InviteStatus.REFUSED -> Color.LightGray
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
