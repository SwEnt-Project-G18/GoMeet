package com.github.se.gomeet.ui.mainscreens.events.manageinvites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.InviteStatus
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.navigation.NavigationActions

/**
 * This composable function represents the list of users that can be invited to an event.
 *
 * @param list the list of users that can be invited
 * @param currentEvent the event for which the users are invited
 * @param status the status of the invitation
 * @param callback the callback function to update the list of users to invite
 * @param initialClicked the initial state of the button
 * @param nav the navigation actions
 */
@Composable
fun PageUserInvites(
    list: List<GoMeetUser>,
    currentEvent: Event,
    status: InviteStatus?,
    callback: (GoMeetUser) -> Unit,
    initialClicked: Boolean,
    nav: NavigationActions
) {

  Column(
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize()) {
        list.forEach { follower ->
          UserInviteWidget(follower, currentEvent, status, initialClicked, callback, nav)
        }
      }
}
