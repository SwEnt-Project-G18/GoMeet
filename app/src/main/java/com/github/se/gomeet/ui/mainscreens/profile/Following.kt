package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.R
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.ui.theme.LightGray
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

private var currentUser: GoMeetUser? = null

/**
 * Composable function for a user's "following" list
 *
 * @param nav The navigation actions
 * @param uid the uid of the user whose "following" list is displayed
 * @param userViewModel The user view model
 */
@Composable
fun Following(
    nav: NavigationActions,
    uid: String,
    userViewModel: UserViewModel,
) {
  val coroutineScope = rememberCoroutineScope()
  var isLoaded by remember { mutableStateOf(false) }
  val following = remember { mutableListOf<GoMeetUser>() }
  LaunchedEffect(Unit) {
    coroutineScope.launch {
      currentUser = userViewModel.getUser(uid)
      currentUser?.following?.forEach { uid ->
        val user = userViewModel.getUser(uid)
        if (user != null) {
          following.add(user)
        }
      }
      isLoaded = true
    }
  }

  Scaffold(
      modifier = Modifier.testTag("Following"),
      topBar = {
        Column {
          Text(
              text = "Following",
              modifier = Modifier.padding(top = 15.dp, start = 15.dp, end = 18.dp, bottom = 0.dp),
              color = DarkCyan,
              fontStyle = FontStyle.Normal,
              fontWeight = FontWeight.SemiBold,
              fontFamily = FontFamily.Default,
              textAlign = TextAlign.Start,
              style = MaterialTheme.typography.headlineLarge)

          Row(
              horizontalArrangement = Arrangement.Start,
              verticalAlignment = Alignment.CenterVertically,
          ) {
            Surface(onClick = { nav.goBack() }, shape = CircleShape, color = Color.Transparent) {
              Icon(
                  painter = painterResource(id = R.drawable.arrow_back),
                  contentDescription = "Back button",
                  modifier = Modifier.padding(15.dp),
                  tint = MaterialTheme.colorScheme.onBackground)
            }

            Text(
                text = "Back",
                color = MaterialTheme.colorScheme.onBackground,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Default,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleSmall)
          }
        }
      },
  ) { innerPadding ->
    if (isLoaded) {
      Column(modifier = Modifier.padding(innerPadding)) {
        following.forEach { user ->
          var isFollowing by remember { mutableStateOf(true) }
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
                Image(
                    modifier =
                        Modifier.width(60.dp)
                            .height(60.dp)
                            .clip(CircleShape)
                            .background(color = MaterialTheme.colorScheme.background),
                    painter = painterResource(id = R.drawable.gomeet_logo),
                    contentDescription = "image description",
                    contentScale = ContentScale.None)
                Column(modifier = Modifier.padding(start = 15.dp).weight(1f)) {
                  Text(text = user.username)
                  Text("@usertag")
                }
                if (uid == Firebase.auth.currentUser!!.uid) {
                  if (isFollowing) {
                    Button(
                        onClick = {
                          userViewModel.unfollow(user.uid)
                          isFollowing = false
                        },
                        modifier = Modifier.padding(start = 15.dp).width(110.dp),
                        colors =
                            ButtonColors(
                                containerColor = LightGray,
                                contentColor = Color.Black,
                                disabledContentColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent)) {
                          Text(text = "Unfollow")
                        }
                  } else {
                    Button(
                        onClick = {
                          userViewModel.follow(user.uid)
                          isFollowing = true
                        },
                        modifier = Modifier.padding(start = 15.dp).width(110.dp),
                        colors =
                            ButtonColors(
                                containerColor = LightGray,
                                contentColor = Color.Black,
                                disabledContentColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent)) {
                          Text(text = "Follow")
                        }
                  }
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
}

@Preview
@Composable
fun FollowingPreview() {
  Following(
      NavigationActions(rememberNavController()),
      "",
      UserViewModel(UserRepository(Firebase.firestore)))
}
