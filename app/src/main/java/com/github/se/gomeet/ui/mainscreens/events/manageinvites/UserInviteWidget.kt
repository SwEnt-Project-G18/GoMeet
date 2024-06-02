package com.github.se.gomeet.ui.mainscreens.events.manageinvites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.InviteStatus
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.mainscreens.profile.ProfileImage
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route

private const val TAG = "UserInviteWidget"

/**
 * This composable function represents the user invite widget when the user can be invited to an
 * event.
 *
 * @param user the user that can be invited
 * @param event the event for which the user is invited
 * @param status the status of the invitation
 * @param initialClicked the initial state of the button
 * @param callback the callback function to update the list of users to invite
 * @param nav the navigation actions
 */
@Composable
fun UserInviteWidget(
    user: GoMeetUser,
    event: Event,
    status: InviteStatus?,
    initialClicked: Boolean,
    callback: (GoMeetUser) -> Unit,
    nav: NavigationActions
) {

  var clicked by rememberSaveable { mutableStateOf(initialClicked) }
  Column {
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp, bottom = 10.dp)
                .clickable { nav.navigateToScreen(Route.OTHERS_PROFILE.replace("{uid}", user.uid)) }
                .testTag(TAG),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
          ProfileImage(
              userId = user.uid, modifier = Modifier.testTag("UserInvitePfp"), size = 50.dp)
          Column(modifier = Modifier.padding(start = 15.dp).weight(1f)) {
            Text(
                text = "${user.firstName} ${user.lastName}",
                color = MaterialTheme.colorScheme.onBackground)
            Text(
                "@${user.username}",
                color = MaterialTheme.colorScheme.onBackground,
            )
          }

          if (status == InviteStatus.PENDING ||
              status == InviteStatus.TO_INVITE ||
              status == null) {
            Button(
                onClick = {
                  clicked = !clicked
                  val toAdd = (!clicked && status == InviteStatus.PENDING || status == null)
                  callback(user.copy(pendingRequests = pendingRequests(user, event, status, toAdd)))
                },
                modifier = Modifier.padding(start = 15.dp).width(110.dp),
                shape = RoundedCornerShape(10.dp),
                colors = manageInvitesButtonColour(status, clicked)) {
                  Text(
                      text =
                          when (status) {
                            InviteStatus.PENDING -> if (clicked) "Invite" else "Cancel"
                            else -> if (!clicked) "Invite" else "Cancel"
                          },
                      color =
                          when (status) {
                            InviteStatus.PENDING -> if (clicked) Color.White else Color.DarkGray
                            else -> if (!clicked) Color.White else Color.DarkGray
                          },
                      style = MaterialTheme.typography.labelLarge)
                }
          }
        }
    HorizontalDivider(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.padding(bottom = 10.dp))
  }
}
