package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.isPastEvent
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.mainscreens.LoadingText
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.SECOND_LEVEL_DESTINATION
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Profile screen composable
 *
 * @param nav NavigationActions
 * @param userViewModel UserViewModel
 */
@Composable
fun Profile(
    nav: NavigationActions,
    userId: String,
    userViewModel: UserViewModel,
    eventViewModel: EventViewModel
) {
  val screenWidth = LocalConfiguration.current.screenWidthDp.dp

  val screenHeight = LocalConfiguration.current.screenHeightDp.dp
  val coroutineScope = rememberCoroutineScope()
  var isProfileLoaded by remember { mutableStateOf(false) }
  var currentUser by remember { mutableStateOf<GoMeetUser?>(null) }
  val joinedEventsList = remember { mutableListOf<Event>() }
  val myHistoryList = remember { mutableListOf<Event>() }

  LaunchedEffect(Unit) {
    coroutineScope.launch {
      currentUser = userViewModel.getUser(userId)
      val allEvents =
          (eventViewModel.getAllEvents() ?: emptyList()).filter { e ->
            currentUser!!.joinedEvents.contains(e.eventID)
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
      modifier = Modifier.testTag("Profile"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { selectedTab ->
              nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
            },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = Route.PROFILE)
      },
      topBar = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier.padding(start = screenWidth / 15, top = screenHeight / 30)
                    .testTag("TopBar")) {
              Text(
                  text = "My Profile",
                  style =
                      MaterialTheme.typography.headlineMedium.copy(
                          fontWeight = FontWeight.SemiBold))
              Spacer(Modifier.weight(1f))
              IconButton(
                  modifier = Modifier.align(Alignment.CenterVertically),
                  onClick = { nav.navigateToScreen(Route.NOTIFICATIONS) }) {
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        modifier =
                            Modifier.size(screenHeight / 28).align(Alignment.CenterVertically),
                        tint = MaterialTheme.colorScheme.onBackground)
                  }

              IconButton(
                  modifier = Modifier.align(Alignment.CenterVertically).padding(end = 15.dp),
                  onClick = {
                    nav.navigateTo(SECOND_LEVEL_DESTINATION.first { it.route == Route.SETTINGS })
                  }) {
                    Icon(
                        Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        modifier =
                            Modifier.size(screenHeight / 28).align(Alignment.CenterVertically),
                        tint = MaterialTheme.colorScheme.onBackground)
                  }
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
                      ProfileImage(userId = userId, modifier = Modifier.testTag("Profile Picture"))
                      Column(modifier = Modifier.padding(start = screenWidth / 20)) {
                        Text(
                            (currentUser?.firstName + " " + currentUser?.lastName),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge)

                        Text(
                            text = ("@" + currentUser?.username),
                            style = MaterialTheme.typography.bodyLarge)
                      }
                    }
                Spacer(modifier = Modifier.height(screenHeight / 40))
                Row(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(screenWidth / 50),
                    verticalAlignment = Alignment.CenterVertically) {
                      // Edit Profile button
                      Button(
                          onClick = { nav.navigateToScreen(Route.EDIT_PROFILE) },
                          modifier = Modifier.height(37.dp).width(screenWidth * 4 / 11),
                          shape = RoundedCornerShape(10.dp),
                          colors =
                              ButtonDefaults.buttonColors(
                                  containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                            Text(text = "Edit Profile", color = MaterialTheme.colorScheme.tertiary)
                          }

                      Button(
                          onClick = { /*TODO*/},
                          modifier = Modifier.height(37.dp).width(screenWidth * 4 / 11),
                          shape = RoundedCornerShape(10.dp),
                          colors =
                              ButtonDefaults.buttonColors(
                                  containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                            Text(text = "Share Profile", color = MaterialTheme.colorScheme.tertiary)
                          }

                      Button(
                          onClick = { nav.navigateToScreen(Route.ADD_FRIEND) },
                          modifier = Modifier.height(37.dp),
                          shape = RoundedCornerShape(10.dp),
                          colors =
                              ButtonDefaults.buttonColors(
                                  containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.add_friend),
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.tertiary)
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
                                text = currentUser?.myEvents?.size.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                            Text(
                                text = "Events",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                          }
                      Column(
                          modifier =
                              Modifier.clickable {
                                nav.navigateToScreen(Route.FOLLOWERS.replace("{uid}", userId))
                              }) {
                            Text(
                                text = currentUser?.followers?.size.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                            Text(
                                text = "Followers",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                          }
                      Column(
                          modifier =
                              Modifier.clickable {
                                nav.navigateToScreen(Route.FOLLOWING.replace("{uid}", userId))
                              }) {
                            Text(
                                text = currentUser?.following?.size.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                            Text(
                                text = "Following",
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
                      items(currentUser!!.tags.size) { index ->
                        Button(
                            onClick = {},
                            content = {
                              Text(
                                  text = currentUser!!.tags[index],
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

                ProfileEventsList("Joined Events", rememberLazyListState(), joinedEventsList, nav)
                Spacer(modifier = Modifier.height(screenHeight / 30))
                ProfileEventsList("My History", rememberLazyListState(), myHistoryList, nav)
              }
        } else {
          LoadingText()
        }
      }
}

@Composable
fun ProfileImage(
    userId: String,
    modifier: Modifier = Modifier,
    defaultImageResId: Int = R.drawable.gomeet_logo
) {
  var profilePictureUrl by remember { mutableStateOf<String?>(null) }

  LaunchedEffect(userId) {
    val db = FirebaseFirestore.getInstance()
    val userDocRef = db.collection("users").document(userId)
    try {
      val snapshot = userDocRef.get().await()
      profilePictureUrl = snapshot.getString("profilePicture")
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  Image(
      painter =
          if (!profilePictureUrl.isNullOrEmpty()) {
            rememberAsyncImagePainter(profilePictureUrl)
          } else {
            painterResource(id = defaultImageResId)
          },
      contentDescription = "Profile picture",
      modifier =
          modifier
              .size(101.dp)
              .clip(CircleShape)
              .background(color = MaterialTheme.colorScheme.background),
      contentScale = ContentScale.Crop)
}

@Preview
@Composable
fun ProfilePreview() {
  Profile(
      nav = NavigationActions(rememberNavController()),
      "John",
      UserViewModel(),
      EventViewModel("John"))
}
