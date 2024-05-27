package com.github.se.gomeet.ui.mainscreens.events

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.mainscreens.LoadingText
import com.github.se.gomeet.ui.mainscreens.profile.ProfileImageUser
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.viewmodel.EventCreationViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

/**
 * Composable function for the AddParticipants screen.
 *
 * @param nav The navigation actions.
 * @param userViewModel The user view model.
 * @param eventCreationViewModel The event creation view model.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddParticipants(
    nav: NavigationActions,
    userViewModel: UserViewModel,
    eventCreationViewModel: EventCreationViewModel
) {
  val screenHeight = LocalConfiguration.current.screenHeightDp.dp
  val query = remember { mutableStateOf("") }
  var isLoading by remember { mutableStateOf(true) }
  val coroutineScope = rememberCoroutineScope()
  val user = remember { mutableStateOf<GoMeetUser?>(null) }
  val potentialParticipants = remember { mutableStateListOf<GoMeetUser>() }

  LaunchedEffect(Unit) {
    coroutineScope.launch {
      val currentUser = Firebase.auth.currentUser!!.uid

      user.value = userViewModel.getUser(currentUser)

      if (user.value != null) {
        val followers = user.value!!.followers
        if (followers.isNotEmpty()) {
          followers.forEach {
            val followerUser = userViewModel.getUser(it)
            potentialParticipants.add(followerUser!!)
          }
        }
        val following = user.value!!.following
        if (following.isNotEmpty()) {
          following.forEach {
            val followingUser = userViewModel.getUser(it)
            if (!followers.contains(followingUser!!.uid)) {
              potentialParticipants.add(followingUser)
            }
          }
        }
      }

      isLoading = false
    }
  }

  // Filtered users based on search query
  val filteredUsers by
      remember(query.value, potentialParticipants) {
        derivedStateOf {
          if (query.value.isEmpty()) {
            potentialParticipants
          } else {
            potentialParticipants.filter { it.username.contains(query.value, ignoreCase = true) }
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
                    text = "Add Participants",
                    color = MaterialTheme.colorScheme.onBackground,
                    style =
                        MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold))
              }
        }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { selectedTab ->
              nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
            },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = Route.CREATE)
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
                      InviteUserWidget(
                          nav = nav,
                          user = user,
                          pendingParticipants = eventCreationViewModel.invitedParticipants,
                          onInviteButtonClick = { _, isInvited ->
                            coroutineScope.launch {
                              if (isInvited) {
                                if (eventCreationViewModel.invitedParticipants.contains(user)) {
                                  eventCreationViewModel.invitedParticipants.remove(user)
                                }
                              } else {
                                if (!eventCreationViewModel.invitedParticipants.contains(user)) {
                                  eventCreationViewModel.invitedParticipants.add(user)
                                }
                              }
                            }
                          })
                    }
                  }
            }
          }
        }
      }
}

/**
 * Composable function for the InviteUserWidget.
 *
 * @param nav The navigation actions.
 * @param user The user to invite.
 * @param pendingParticipants The list of pending participants.
 * @param onInviteButtonClick The callback for the invite button click.
 */
@Composable
fun InviteUserWidget(
    nav: NavigationActions,
    user: GoMeetUser,
    pendingParticipants: List<GoMeetUser>,
    onInviteButtonClick: (String, Boolean) -> Unit
) {
  val screenWidth = LocalConfiguration.current.screenWidthDp.dp

  val isInvited = pendingParticipants.contains(user)

  Row(
      modifier =
          Modifier.fillMaxWidth()
              .padding(vertical = 3.dp)
              .clickable { nav.navigateToScreen(Route.OTHERS_PROFILE.replace("{uid}", user.uid)) }
              .testTag("InviteUserWidget"),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          ProfileImageUser(userId = user.uid)

          Spacer(modifier = Modifier.width(screenWidth / 20))

          Column {
            Text(
                text = "${user.firstName} ${user.lastName}",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge)
            Text(
                text = user.username,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6F))
          }
        }

        Button(
            shape = RoundedCornerShape(10.dp),
            onClick = { onInviteButtonClick(user.uid, isInvited) },
            modifier =
                Modifier.padding(start = 15.dp)
                    .width(110.dp)
                    .testTag(if (isInvited) "CancelButton" else "InviteButton"),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        if (isInvited) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.outlineVariant,
                    contentColor = if (isInvited) Color.Black else Color.White,
                    disabledContentColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent)) {
              Text(
                  text = if (isInvited) "Cancel" else "Invite",
                  style = MaterialTheme.typography.labelLarge,
                  color = if (isInvited) MaterialTheme.colorScheme.onBackground else Color.White)
            }
      }
}
