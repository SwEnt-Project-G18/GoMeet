package com.github.se.gomeet.ui.mainscreens.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.se.gomeet.model.event.EventInviteUsers
import com.github.se.gomeet.model.event.InviteStatus
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.viewmodel.EventInviteViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import kotlinx.coroutines.launch

/**
 * Composable function for the AddParticipants screen.
 *
 * @param nav The navigation actions.
 */
@Composable
fun AddParticipants(nav: NavigationActions, userId: String, userViewModel : UserViewModel, eventId: String, eventInviteViewModel: EventInviteViewModel) {


  /* TODO: Code the UI of the AddParticipants screen when the logic of
  the invites will be done and the UI of this screen discussed with the team */
    val coroutineScope = rememberCoroutineScope()
    val currentUser = remember { mutableStateOf<GoMeetUser?>(null) }
    val followers = remember{ mutableListOf<String>() }
    val invited = remember { mutableListOf<String>()}

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            currentUser.value = userViewModel.getUser(userId)
            while (currentUser.value == null){}
            val fwers = currentUser.value!!.followers

            if (fwers.isNotEmpty()) {
                fwers.forEach {
                    followers.add(it)
                }
            }
    }
    }

  Scaffold(
      modifier = Modifier.testTag("AddParticipantsScreen"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { selectedTab ->
              nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
            },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = Route.CREATE)
      }) { innerPadding ->
      Column(
          verticalArrangement = Arrangement.Top,
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
              .padding(innerPadding)
              .fillMaxSize()) {
          followers.forEach{user ->
              AddWidget(userId = user, invited = false, remove = { invited.remove(user) }, add = { invited.add(user) })
              }
          }
          OutlinedButton(onClick = {
              eventInviteViewModel.addEventInviteUsers(
                  EventInviteUsers(eventId,
                      invited.map { Pair(it, InviteStatus.PENDING) }.toMutableList()
                  ))
              nav.goBack()
          }) {
              Text(text = "Finish")
          }
      }
}


@Composable
fun AddWidget (userId: String, invited: Boolean, remove: () -> Unit, add: ()->Unit){
    val invt = remember {
        mutableStateOf<Boolean>(invited)
    }
    Row{
        Text(text = userId)
        OutlinedButton(onClick = {
            if(invt.value){
                remove()
            }else {
                add()
            }
        }) {
            if (invt.value) {
                Text(text = "Invited")
            } else {
                Text(text = "Send Invite")
            }
        }
    }
}
