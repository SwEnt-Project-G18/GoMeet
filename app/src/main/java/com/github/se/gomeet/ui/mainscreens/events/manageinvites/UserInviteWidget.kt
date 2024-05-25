package com.github.se.gomeet.ui.mainscreens.events.manageinvites

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.InviteStatus
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.theme.DarkerGreen

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
 */
@Composable
fun UserInviteWidget(
    user: GoMeetUser,
    event: Event,
    status: InviteStatus?,
    initialClicked: Boolean,
    callback: (GoMeetUser) -> Unit
) {

  var clicked by rememberSaveable { mutableStateOf(initialClicked) }
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .padding(start = 15.dp, end = 15.dp)
              .height(50.dp)
              .testTag("UserInviteWidget"),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically) {
        val painter: Painter =
            if (event.images.isNotEmpty()) {
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
        // Profile picture
        Image(
            modifier =
                Modifier.size(40.dp)
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colorScheme.background),
            painter = painter,
            contentDescription = "profile picture",
            contentScale = ContentScale.FillBounds)

        // Username text
        Text(text = user.username, color = MaterialTheme.colorScheme.onBackground)

        // Status text
        Text(
            text =
                when (status) {
                  InviteStatus.PENDING -> InviteStatus.PENDING.formattedName
                  InviteStatus.ACCEPTED -> InviteStatus.ACCEPTED.formattedName
                  InviteStatus.REFUSED -> InviteStatus.REFUSED.formattedName
                  else -> ""
                },
            modifier = Modifier.width(80.dp).testTag("InviteStatus"),
            color =
                when (status) {
                  InviteStatus.PENDING ->
                      if (clicked) Gray else MaterialTheme.colorScheme.onBackground
                  InviteStatus.ACCEPTED -> DarkerGreen
                  InviteStatus.REFUSED -> Color.Red
                  else -> MaterialTheme.colorScheme.onBackground
                })

        // Button to invite or cancel invitation
        Button(
            onClick = {
              clicked = !clicked
              val toAdd =
                  (clicked && (status == null || status == InviteStatus.REFUSED)) ||
                      (!clicked &&
                          (status == InviteStatus.PENDING || status == InviteStatus.ACCEPTED))
              Log.d(
                  TAG,
                  "Clicked invite/cancel button. toAdd: $toAdd, clicked: $clicked, status: $status")
              callback(user.copy(pendingRequests = pendingRequests(user, event, status, toAdd)))
            },
            modifier = Modifier.height(26.dp).width(82.dp),
            contentPadding = PaddingValues(vertical = 2.dp),
            shape = RoundedCornerShape(10.dp),
            colors = manageInvitesButtonColour(status, clicked)) {
              Text(
                  text =
                      when (status) {
                        InviteStatus.PENDING,
                        InviteStatus.ACCEPTED -> if (clicked) "Invite" else "Cancel"
                        else -> if (!clicked) "Invite" else "Cancel"
                      },
                  color =
                      when (status) {
                        InviteStatus.PENDING,
                        InviteStatus.ACCEPTED -> if (clicked) Color.White else Color.DarkGray
                        else -> if (!clicked) Color.White else Color.DarkGray
                      },
                  fontSize = 12.sp)
            }
      }
}
