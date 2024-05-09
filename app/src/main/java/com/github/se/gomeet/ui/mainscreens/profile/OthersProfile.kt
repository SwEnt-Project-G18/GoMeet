package com.github.se.gomeet.ui.mainscreens.profile

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.R
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.mainscreens.LoadingText
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.ui.theme.NavBarUnselected
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
    userViewModel: UserViewModel
) { // TODO Add parameters to the function
  var isFollowing by remember { mutableStateOf(false) }
  var followerCount by remember { mutableIntStateOf(0) }
  val coroutineScope = rememberCoroutineScope()
  var isProfileLoaded by remember { mutableStateOf(false) }
  LaunchedEffect(Unit) {
    coroutineScope.launch {
      user = userViewModel.getUser(uid)
      isProfileLoaded = true
      isFollowing = user?.followers?.contains(currentUser) ?: false
      followerCount = user?.followers?.size ?: 0
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
        TopAppBar(
            title = {},
            backgroundColor = MaterialTheme.colorScheme.background,
            elevation = 0.dp,
            modifier = Modifier.height(50.dp).testTag("TopBar"),
            actions = {
              // Settings Icon
              IconButton(onClick = { /* Handle settings icon click */}) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    modifier = Modifier.size(24.dp),
                    tint = DarkCyan)
              }

              MoreActionsButton()
            })
      }) { innerPadding ->
        if (isProfileLoaded) {
          Column(
              verticalArrangement = Arrangement.SpaceEvenly,
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState(0))) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(start = 15.dp, end = 0.dp, top = 0.dp, bottom = 30.dp)
                            .testTag("UserInfo")) {
                      ProfileImage(userId = uid)
                      Column(
                          horizontalAlignment =
                              Alignment
                                  .CenterHorizontally, // Center horizontally within this column
                          modifier = Modifier.padding(0.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 30.dp)) {
                                  Text(
                                      text = user?.username ?: "username",
                                      textAlign = TextAlign.Center,
                                      style =
                                          TextStyle(
                                              fontSize = 20.sp,
                                              lineHeight = 16.sp,
                                              fontFamily = FontFamily(Font(R.font.roboto)),
                                              fontWeight = FontWeight(1000),
                                              color = MaterialTheme.colorScheme.onBackground,
                                              textAlign = TextAlign.Center,
                                              letterSpacing = 0.5.sp,
                                          ))
                                }
                            Text(
                                text = "@usertag",
                                style =
                                    TextStyle(
                                        fontSize = 15.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = FontFamily(Font(R.font.roboto)),
                                        fontWeight = FontWeight(600),
                                        color = MaterialTheme.colorScheme.onBackground,
                                        textAlign = TextAlign.Center,
                                        letterSpacing = 0.5.sp,
                                    ),
                                modifier = Modifier)
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

                      Spacer(Modifier.width(5.dp))
                    }

                Spacer(modifier = Modifier.height(30.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().testTag("MoreUserInfo")) {
                      Column(
                          modifier =
                              Modifier.clickable {
                                // TODO
                              }) {
                            Text(
                                text = user?.myEvents?.size.toString(),
                                style =
                                    TextStyle(
                                        fontSize = 20.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = FontFamily(Font(R.font.roboto)),
                                        fontWeight = FontWeight(1000),
                                        color = Color(0xFF2F6673),
                                        textAlign = TextAlign.Center,
                                        letterSpacing = 0.5.sp,
                                    ),
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                            Text(
                                text = "Events",
                                style =
                                    TextStyle(
                                        fontSize = 13.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = FontFamily(Font(R.font.roboto)),
                                        fontWeight = FontWeight(1000),
                                        color = Color(0xFF2F6673),
                                        textAlign = TextAlign.Center,
                                        letterSpacing = 0.5.sp,
                                    ),
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                          }
                      HorizontalDivider(
                          modifier =
                              Modifier
                                  // .fillMaxHeight()
                                  .height(40.dp)
                                  .width(2.dp))
                      Column(
                          modifier =
                              Modifier.clickable {
                                nav.navigateToScreen(Route.FOLLOWERS.replace("{uid}", uid))
                              }) {
                            Text(
                                text = followerCount.toString(),
                                style =
                                    TextStyle(
                                        fontSize = 20.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = FontFamily(Font(R.font.roboto)),
                                        fontWeight = FontWeight(1000),
                                        color = Color(0xFF2F6673),
                                        textAlign = TextAlign.Center,
                                        letterSpacing = 0.5.sp,
                                    ),
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                            Text(
                                text = "Followers",
                                style =
                                    TextStyle(
                                        fontSize = 13.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = FontFamily(Font(R.font.roboto)),
                                        fontWeight = FontWeight(1000),
                                        color = Color(0xFF2F6673),
                                        textAlign = TextAlign.Center,
                                        letterSpacing = 0.5.sp,
                                    ),
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                          }
                      HorizontalDivider(
                          modifier =
                              Modifier
                                  // .fillMaxHeight()
                                  .height(40.dp)
                                  .width(2.dp))
                      Column(
                          modifier =
                              Modifier.clickable {
                                nav.navigateToScreen(
                                    Route.FOLLOWING.replace("{uid}", uid)
                                        .replace("{isOwnList}", "false"))
                              }) {
                            Text(
                                text = user?.following?.size.toString(),
                                style =
                                    TextStyle(
                                        fontSize = 20.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = FontFamily(Font(R.font.roboto)),
                                        fontWeight = FontWeight(1000),
                                        color = Color(0xFF2F6673),
                                        textAlign = TextAlign.Center,
                                        letterSpacing = 0.5.sp,
                                    ),
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                            Text(
                                text = "Following",
                                style =
                                    TextStyle(
                                        fontSize = 13.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = FontFamily(Font(R.font.roboto)),
                                        fontWeight = FontWeight(1000),
                                        color = Color(0xFF2F6673),
                                        textAlign = TextAlign.Center,
                                        letterSpacing = 0.5.sp,
                                    ),
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                          }
                    }
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "Tags",
                    style =
                        TextStyle(
                            fontSize = 18.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight(1000),
                            color = DarkCyan,
                            textAlign = TextAlign.Start,
                            letterSpacing = 0.5.sp,
                        ),
                    modifier =
                        Modifier.width(74.dp)
                            .height(21.dp)
                            .align(Alignment.Start)
                            .padding(start = 15.dp))
                Column(
                    modifier =
                        Modifier.padding(start = 0.dp, end = 0.dp)
                            .fillMaxWidth()
                            .testTag("TagList")) {
                      Spacer(modifier = Modifier.height(10.dp))
                      LazyRow(
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.spacedBy(8.dp),
                          contentPadding = PaddingValues(start = 15.dp, end = 15.dp),
                          modifier = Modifier.heightIn(min = 56.dp)) {
                            items(10) {
                              Button(
                                  onClick = {},
                                  content = { Text("Tag") },
                                  colors =
                                      ButtonDefaults.buttonColors(
                                          containerColor = NavBarUnselected,
                                          contentColor = DarkCyan),
                                  border = BorderStroke(1.dp, DarkCyan))
                            }
                          }
                    }
                Spacer(modifier = Modifier.height(10.dp))
                ProfileEventsList("My Events")
                Spacer(modifier = Modifier.height(10.dp))
                ProfileEventsList("History")
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
        modifier = Modifier.size(24.dp).rotate(90f), // Rotates the icon by 90 degrees
        tint = DarkCyan)
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
  OthersProfile(nav = NavigationActions(rememberNavController()), "", UserViewModel())
}
