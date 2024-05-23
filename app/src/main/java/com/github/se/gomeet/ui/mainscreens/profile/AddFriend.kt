package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.mainscreens.LoadingText
import com.github.se.gomeet.ui.mainscreens.events.GoMeetSearchBar
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

/**
 * Composable for the add friend screen.
 *
 * @param nav The navigation actions.
 * @param userViewModel The user view model.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFriend(nav: NavigationActions, userViewModel: UserViewModel) {

  val screenHeight = LocalConfiguration.current.screenHeightDp.dp
  val query = remember { mutableStateOf("") }
  var isLoading by remember { mutableStateOf(true) }
  var users by remember { mutableStateOf(listOf<GoMeetUser>()) }
  var followedUsers by remember { mutableStateOf(setOf<String>()) }

  val coroutineScope = rememberCoroutineScope()

  LaunchedEffect(Unit) {
    coroutineScope.launch {
      val fetchedUsers = userViewModel.getAllUsers()
      val currentUser = Firebase.auth.currentUser!!.uid
      if (fetchedUsers != null) {
        users = fetchedUsers.filter { it.uid != currentUser } // Exclude the current user
        // Initialize the followed users
        val currentUserData = userViewModel.getUser(currentUser)
        if (currentUserData != null) {
          followedUsers = currentUserData.following.toSet()
        }
      }
      isLoading = false
    }
  }

  // Filtered users based on search query
  val filteredUsers by
      remember(query.value, users) {
        derivedStateOf {
          if (query.value.isEmpty()) {
            users
          } else {
            users.filter { it.username.contains(query.value, ignoreCase = true) }
          }
        }
      }

  Scaffold(
      topBar = {
        Column {
          TopAppBar(
              modifier = Modifier.testTag("TopBar"),
              backgroundColor = MaterialTheme.colorScheme.background,
              elevation = 0.dp,
              title = {
                // Empty title since we're placing our own components
              },
              navigationIcon = {
                IconButton(onClick = { nav.goBack() }) {
                  Icon(
                      imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                      contentDescription = "Back",
                      tint = MaterialTheme.colorScheme.onBackground)
                }
              })

          Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(start = 18.dp)) {
                Text(
                    text = "Find User",
                    color = MaterialTheme.colorScheme.onBackground,
                    style =
                        MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold))
              }
        }
      },
      bottomBar = {
        // Your bottom bar content
      }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
          if (isLoading) {
            LoadingText()
          } else {
            Column {
              GoMeetSearchBar(
                  nav,
                  query,
                  MaterialTheme.colorScheme.primaryContainer,
                  MaterialTheme.colorScheme.tertiary)
              Spacer(modifier = Modifier.height(screenHeight / 40))
              LazyColumn(
                  contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                  verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredUsers) { user ->
                      UserItem(user, followedUsers, nav) { uid, isFollowing ->
                        coroutineScope.launch {
                          if (isFollowing) {
                            userViewModel.unfollow(uid)
                          } else {
                            userViewModel.follow(uid)
                          }
                          // Directly update the state for immediate reflection
                          followedUsers =
                              if (isFollowing) {
                                followedUsers - uid
                              } else {
                                followedUsers + uid
                              }
                        }
                      }
                    }
                  }
            }
          }
        }
      }
}
