package com.github.se.gomeet.ui.mainscreens.notifications

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.getEventDateString
import com.github.se.gomeet.model.event.getEventTimeString
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * This composable is used to display the invitations notifications widgets.
 *
 * @param event the event to display
 * @param userViewModel the user view model
 * @param currentUserId the user receiving the notifications
 * @param initialClicked the initial state of the buttons
 * @param callback the callback to update the event
 */
@Composable
fun InvitationsNotificationsWidget(
    event: Event,
    userViewModel: UserViewModel,
    eventViewModel: EventViewModel,
    currentUserId: String,
    initialClicked: Boolean,
    callback: (Event) -> Unit,
    nav: NavigationActions
) {
    var clicked by rememberSaveable { mutableStateOf(initialClicked) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    val screenWidth = LocalConfiguration.current.screenWidthDp

    LaunchedEffect(Unit) {
        imageUrl = eventViewModel.getEventImageUrl(event.eventID)
    }
    Column (modifier = Modifier.clickable {
        nav.navigateToEventInfo(
            event.eventID,
            event.title,
            getEventDateString(event.date),
            getEventTimeString(event.time),
            event.creator,
            0.0,
            event.description,
            LatLng(event.location.latitude, event.location.longitude)
        )
    }
        .padding(horizontal = 10.dp)){
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {

                var username by remember { mutableStateOf<String?>("Loading...") }
                LaunchedEffect(event.creator) {
                    username = userViewModel.getUsername(event.creator)
                }

                username?.let {
                    Text(
                        it + " invited you to join " + event.title,
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .testTag("UserName")
                    )
                }

                Text(
                    getEventDateString(event.date) + ", " + getEventTimeString(event.time),
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.testTag("EventDate")
                )
            }
        }


        // Accept and Decline buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    clicked = true
                    callback(
                        event.copy(
                            pendingParticipants = event.pendingParticipants.minus(currentUserId),
                            participants = event.participants.plus(currentUserId)
                        )
                    )
                },
                shape = RoundedCornerShape(10.dp),
                colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.outlineVariant,
                    contentColor = Color.White
                ),
                enabled = !clicked,
                modifier = Modifier
                    .width((screenWidth / 3).dp)
                    .testTag("AcceptButton")
            ) {
                Text("Accept")
            }


            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = {
                    clicked = true
                    callback(
                        event.copy(
                            pendingParticipants = event.pendingParticipants.minus(currentUserId)
                        )
                    )
                },
                shape = RoundedCornerShape(10.dp),
                colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.tertiary
                ),
                enabled = !clicked,
                modifier = Modifier
                    .width((screenWidth / 3).dp)
                    .testTag("DeclineButton")
            ) {
                Text("Decline")
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.primaryContainer)
    }
}
