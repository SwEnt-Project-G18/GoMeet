package com.github.se.gomeet.ui.mainscreens.profile

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.isPastEvent
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.mainscreens.LoadingText
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

private var user: GoMeetUser? = null
private var currentUser = Firebase.auth.currentUser?.uid ?: ""

/**
 * Composable function for the OthersProfile screen.
 *
 * @param nav The navigation actions for the OthersProfile screen.
 * @param uid The user id of the user whose profile is being viewed.
 * @param userViewModel userViewModel
 */
@Composable
fun OthersProfile(
    nav: NavigationActions,
    uid: String,
    userViewModel: UserViewModel,
    eventViewModel: EventViewModel
) { // TODO Add parameters to the function
  var isFollowing by remember { mutableStateOf(false) }
  var followerCount by remember { mutableIntStateOf(0) }
  val coroutineScope = rememberCoroutineScope()
  var isProfileLoaded by remember { mutableStateOf(false) }
  val joinedEventsList = remember { mutableListOf<Event>() }
  val myHistoryList = remember { mutableListOf<Event>() }
  val screenWidth = LocalConfiguration.current.screenWidthDp.dp
  val screenHeight = LocalConfiguration.current.screenHeightDp.dp

  LaunchedEffect(Unit) {
    coroutineScope.launch {
      user = userViewModel.getUser(uid)
      isFollowing = user?.followers?.contains(currentUser) ?: false
      followerCount = user?.followers?.size ?: 0

      val allEvents =
          (eventViewModel.getAllEvents() ?: emptyList()).filter { e ->
            user!!.joinedEvents.contains(e.eventID) && e.public
          }
      allEvents.forEach {
        if (!isPastEvent(it)) {
          joinedEventsList.add(it)
        } else {
          myHistoryList.add(it)
        }
      }
      isProfileLoaded = true
    }
  }

  Scaffold(
      modifier = Modifier.testTag("OtherProfile"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { selectedTab ->
              nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
            },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = "")
      },
      topBar = {
        Row(modifier = Modifier.testTag("TopBar"), verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = { nav.goBack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Go back",
                tint = MaterialTheme.colorScheme.onBackground)
          }
          Spacer(modifier = Modifier.weight(1F))
          MoreActionsButton()
        }
      }) { innerPadding ->
        if (isProfileLoaded) {
          Column(
              verticalArrangement = Arrangement.SpaceEvenly,
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState(0))) {
                Spacer(modifier = Modifier.height(screenHeight / 60))
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(start = screenWidth / 20)
                            .testTag("UserInfo")) {
                      ProfileImage(
                          userId = uid,
                          modifier = Modifier.testTag("Profile Picture"),
                          size = 101.dp)
                      Column(modifier = Modifier.padding(start = screenWidth / 20)) {
                        Text(
                            (user?.firstName ?: "First") + " " + (user?.lastName ?: " Last"),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleLarge)

                        Text(
                            text = "@" + (user?.username ?: "username"),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyLarge)
                      }
                    }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 15.dp)) {
                      // Edit Profile button
                      if (isFollowing) {
                        Button(
                            onClick = {
                              isFollowing = false
                              followerCount -= 1
                              userViewModel.unfollow(uid)
                            },
                            modifier = Modifier.height(40.dp).width(180.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors =
                                ButtonDefaults.buttonColors(containerColor = Color(0xFFECEFF1))) {
                              Text(text = "Unfollow", color = Color.Black)
                            }
                      } else {
                        Button(
                            onClick = {
                              isFollowing = true
                              followerCount += 1
                              userViewModel.follow(uid)
                            },
                            modifier = Modifier.height(40.dp).width(180.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors =
                                ButtonDefaults.buttonColors(containerColor = Color(0xFFECEFF1))) {
                              Text(text = "Follow", color = Color.Black)
                            }
                      }

                      Spacer(Modifier.width(5.dp))

                      Button(
                          onClick = {
                            nav.navigateToScreen(Route.MESSAGE.replace("{id}", Uri.encode(uid)))
                          },
                          modifier = Modifier.height(40.dp).width(180.dp),
                          shape = RoundedCornerShape(10.dp),
                          colors =
                              ButtonDefaults.buttonColors(containerColor = Color(0xFFECEFF1))) {
                            Text(text = "Message", color = Color.Black)
                          }
                    }

                Spacer(modifier = Modifier.height(screenHeight / 40))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()) {
                      Column(
                          modifier =
                              Modifier.clickable {
                                // TODO
                              }) {
                            Text(
                                text = user?.myEvents?.size.toString(),
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                            Text(
                                text = "Events",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                          }
                      Column(
                          modifier =
                              Modifier.clickable {
                                nav.navigateToScreen(Route.FOLLOWERS.replace("{uid}", user!!.uid))
                              }) {
                            Text(
                                text = user?.followers?.size.toString(),
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                            Text(
                                text = "Followers",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                          }
                      Column(
                          modifier =
                              Modifier.clickable {
                                nav.navigateToScreen(Route.FOLLOWING.replace("{uid}", user!!.uid))
                              }) {
                            Text(
                                text = user?.following?.size.toString(),
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                            Text(
                                text = "Following",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                          }
                    }

                Spacer(modifier = Modifier.fillMaxWidth().height(screenHeight / 50))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    contentPadding = PaddingValues(start = 15.dp, end = 15.dp)) {
                      items(user!!.tags.size) { index ->
                        Button(
                            onClick = {},
                            content = {
                              Text(
                                  text = user!!.tags[index],
                                  color = MaterialTheme.colorScheme.onBackground,
                                  style = MaterialTheme.typography.labelLarge)
                            },
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.outlineVariant),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.padding(end = 8.dp))
                      }
                    }
                Spacer(modifier = Modifier.height(screenHeight / 40))
                ProfileEventsList(
                    (user!!.firstName) + "'s joined Events",
                    rememberLazyListState(),
                    joinedEventsList,
                    nav)
                Spacer(modifier = Modifier.height(screenHeight / 40))
                ProfileEventsList(
                    (user!!.firstName) + "'s History", rememberLazyListState(), myHistoryList, nav)
              }
        } else {
          LoadingText()
        }
      }
}

/** Composable function for the MoreActionsButton. */
@Composable
fun MoreActionsButton() {
  var showMenu by remember { mutableStateOf(false) }

  IconButton(onClick = { showMenu = true }) {
    Icon(
        imageVector = Icons.Default.MoreVert,
        contentDescription = "More",
        modifier = Modifier.rotate(90f), // Rotates the icon by 90 degrees
        tint = MaterialTheme.colorScheme.tertiary)
  }

  DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
    DropdownMenuItem(
        text = { Text("Share Profile") },
        onClick = {
          // Handle Share Profile logic here
          showMenu = false
        })
    DropdownMenuItem(
        text = { Text("Block") },
        onClick = {
          // Handle Block logic here
          showMenu = false
        })
  }
}

@Preview
@Composable
fun OthersProfilePreview() {
  OthersProfile(
      nav = NavigationActions(rememberNavController()), "", UserViewModel(), EventViewModel(null))
}
