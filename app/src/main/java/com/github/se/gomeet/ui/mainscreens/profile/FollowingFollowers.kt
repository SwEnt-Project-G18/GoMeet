package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.github.se.gomeet.R
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.mainscreens.LoadingText
import com.github.se.gomeet.ui.mainscreens.profile.FollowingFollowerPage.*
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.viewmodel.UserViewModel
import kotlinx.coroutines.launch

private var currentUser: GoMeetUser? = null

/**
 * Composable function for a user's "following" list
 *
 * @param nav The navigation actions
 * @param uidOfFollowList the uid of the user whose "following" list is displayed
 * @param currentUid The uid of the current user
 * @param userViewModel The user view model
 * @param followingScreen A boolean indicating whether the "following" list is displayed. If false,
 *   the "followers" list is displayed
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FollowingFollowers(
    nav: NavigationActions,
    uidOfFollowList: String,
    currentUid: String,
    userViewModel: UserViewModel,
    followingScreen: Boolean
) {
  val pagerState =
      rememberPagerState(
          pageCount = { FollowingFollowerPage.entries.size },
          initialPage = if (followingScreen) FOLLOWING.ordinal else FOLLOWERS.ordinal)
  val coroutineScope = rememberCoroutineScope()
  var isLoaded by remember { mutableStateOf(false) }
  var username by remember { mutableStateOf("") }

  val following = remember { mutableListOf<GoMeetUser>() }
  val followers = remember { mutableListOf<GoMeetUser>() }
  val screenHeight = LocalConfiguration.current.screenHeightDp.dp

  LaunchedEffect(Unit) {
    coroutineScope.launch {
      currentUser = userViewModel.getUser(uidOfFollowList)
      username = currentUser!!.username
      currentUser?.following?.forEach { uid ->
        val user = userViewModel.getUser(uid)
        if (user != null) {
          following.add(user)
        }
      }
      currentUser?.followers?.forEach { uid ->
        val user = userViewModel.getUser(uid)
        if (user != null) {
          followers.add(user)
        }
      }
      isLoaded = true
    }
  }

  Scaffold(
      modifier = Modifier.testTag("FollowingFollower"),
      topBar = {
        Column {
          Box(contentAlignment = Alignment.Center) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                  IconButton(
                      modifier = Modifier.testTag("GoBackFollower"), onClick = { nav.goBack() }) {
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
                      text = username,
                      color = MaterialTheme.colorScheme.onBackground,
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
                        Modifier.height(screenHeight / 20).weight(1f).clickable {
                          coroutineScope.launch {
                            pagerState.animateScrollToPage(FOLLOWERS.ordinal)
                          }
                        }) {
                      Text(
                          text = FOLLOWERS.str,
                          color = MaterialTheme.colorScheme.onBackground,
                          style =
                              MaterialTheme.typography.bodyMedium.copy(
                                  fontWeight =
                                      if (pagerState.currentPage == FOLLOWERS.ordinal)
                                          FontWeight.Bold
                                      else FontWeight.Normal))
                    }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier.weight(1f).height(screenHeight / 20).clickable {
                          coroutineScope.launch {
                            pagerState.animateScrollToPage(FOLLOWING.ordinal)
                          }
                        }) {
                      Text(
                          text = FOLLOWING.str,
                          color = MaterialTheme.colorScheme.onBackground,
                          style =
                              MaterialTheme.typography.bodyMedium.copy(
                                  fontWeight =
                                      if (pagerState.currentPage == FOLLOWING.ordinal)
                                          FontWeight.Bold
                                      else FontWeight.Normal))
                    }
              }
          val canvasColor = MaterialTheme.colorScheme.tertiary
          Canvas(
              modifier =
                  Modifier.fillMaxWidth() // Ensures the Canvas takes up full screen width
                      .height(1.dp) // Sets the height of the Canvas to 1 dp
              ) {
                val canvasWidth = size.width

                drawLine(
                    color = canvasColor,
                    start =
                        if (pagerState.currentPage == FOLLOWERS.ordinal) Offset(x = 0f, y = 0f)
                        else Offset(x = canvasWidth / 2, y = 0f),
                    end =
                        if (pagerState.currentPage == FOLLOWERS.ordinal)
                            Offset(x = canvasWidth / 2, y = 0f)
                        else Offset(x = canvasWidth, y = 0f),
                    strokeWidth = 5f)
              }
          Spacer(modifier = Modifier.height(screenHeight / 30))
        }
      },
  ) { innerPadding ->
    if (isLoaded) {
      HorizontalPager(
          state = pagerState, modifier = Modifier.fillMaxSize().padding(innerPadding)) { page ->
            when (page) {
              FOLLOWING.ordinal ->
                  PageUsers(following, nav, userViewModel, uidOfFollowList, currentUid)
              FOLLOWERS.ordinal ->
                  PageUsers(followers, nav, userViewModel, uidOfFollowList, currentUid)
              else -> Text("Page not found")
            }
          }
    } else {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { LoadingText() }
    }
  }
}

/**
 * Composable function for displaying a list of followers or following
 *
 * @param users The list of users to display
 * @param nav The navigation actions
 * @param userViewModel The user view model
 * @param uidOfFollowList The uid of the user whose followers/following are displayed
 * @param currentUid The uid of the current user
 */
@Composable
fun PageUsers(
    users: List<GoMeetUser>,
    nav: NavigationActions,
    userViewModel: UserViewModel,
    uidOfFollowList: String,
    currentUid: String
) {
  Column(modifier = Modifier.fillMaxSize()) {
    users.forEach { user ->
      var isFollowing by remember { mutableStateOf(user.followers.contains(currentUid)) }
      Row(
          modifier =
              Modifier.fillMaxWidth()
                  .padding(start = 15.dp, end = 15.dp, bottom = 10.dp)
                  .clickable {
                    nav.navigateToScreen(Route.OTHERS_PROFILE.replace("{uid}", user.uid))
                  }
                  .testTag("FollowingUser"),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically) {
            val painter: Painter =
                if (user.profilePicture.isNotEmpty()) {
                  rememberAsyncImagePainter(
                      ImageRequest.Builder(LocalContext.current)
                          .data(data = user.profilePicture)
                          .apply {
                            crossfade(true)
                            placeholder(R.drawable.gomeet_logo)
                          }
                          .build())
                } else {
                  painterResource(id = R.drawable.gomeet_logo)
                }
            Image(
                modifier =
                    Modifier.width(60.dp)
                        .height(60.dp)
                        .clip(CircleShape)
                        .background(color = MaterialTheme.colorScheme.background),
                painter = painter,
                contentDescription = "Profile picture",
                contentScale = ContentScale.FillBounds)
            Column(modifier = Modifier.padding(start = 15.dp).weight(1f)) {
              Text(
                  text = "${user.firstName} ${user.lastName}",
                  color = MaterialTheme.colorScheme.onBackground)
              Text(
                  "@${user.username}",
                  color = MaterialTheme.colorScheme.onBackground,
              )
            }
            if (uidOfFollowList == currentUid) {
              if (isFollowing) {
                Button(
                    shape = RoundedCornerShape(10.dp),
                    onClick = {
                      userViewModel.unfollow(user.uid)
                      isFollowing = false
                    },
                    modifier =
                        Modifier.padding(start = 15.dp).width(110.dp).testTag("UnfollowButton"),
                    colors =
                        ButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onBackground,
                            disabledContentColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent)) {
                      Text(text = "Following", style = MaterialTheme.typography.labelLarge)
                    }
              } else {
                Button(
                    shape = RoundedCornerShape(10.dp),
                    onClick = {
                      userViewModel.follow(user.uid)
                      isFollowing = true
                    },
                    modifier = Modifier.padding(start = 15.dp).width(110.dp),
                    colors =
                        ButtonColors(
                            containerColor = MaterialTheme.colorScheme.outlineVariant,
                            contentColor = Color.White,
                            disabledContentColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent)) {
                      Text(text = "Follow", style = MaterialTheme.typography.labelLarge)
                    }
              }
            }
          }
    }
  }
}

@Preview
@Composable
fun FollowingPreview() {
  FollowingFollowers(NavigationActions(rememberNavController()), "", "", UserViewModel(), true)
}

private enum class FollowingFollowerPage(val str: String) {
  FOLLOWERS("Followers"),
  FOLLOWING("Following")
}
