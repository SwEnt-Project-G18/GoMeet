package com.github.se.gomeet.ui.mainscreens.events

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.theme.White
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import kotlinx.coroutines.launch

private const val TAG = "EventInfo"

/** EventHeader is a composable that displays the header of an event. */
@Composable
fun EventHeader(
    eventViewModel: EventViewModel,
    event: Event,
    nav: NavigationActions,
    rating: MutableState<Long>,
    organiser: GoMeetUser
) {
  val titleBuilder = StringBuilder()
  var index = 0

  while (index < event.title.length) {
    if (index + 20 < event.title.length) {
      titleBuilder.append(event.title.substring(index, index + 20)).append("\n")
    } else {
      titleBuilder.append(event.title.substring(index))
    }
    index += 20
  }
  Row(
      modifier = Modifier.fillMaxWidth().testTag("EventHeader").padding(10.dp),
      verticalAlignment = Alignment.Top,
      horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
          Text(
              text = titleBuilder.toString(),
              style =
                  TextStyle(
                      fontSize = 24.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.tertiary,
                      letterSpacing = 0.5.sp))
          Spacer(modifier = Modifier.height(5.dp))
          Text(
              modifier =
                  Modifier.clickable {
                        if (event.creator == eventViewModel.currentUID) {
                          nav.navigateToScreen(Route.PROFILE)
                        } else {
                          nav.navigateToScreen(Route.OTHERS_PROFILE.replace("{uid}", event.creator))
                        }
                      }
                      .testTag("Username"),
              text = organiser.username,
              style =
                  TextStyle(
                      fontSize = 16.sp,
                      fontWeight = FontWeight.ExtraBold,
                      color = Color.Gray,
                      fontFamily = FontFamily(Font(R.font.roboto)),
                      letterSpacing = 0.5.sp))

          RateEvent(rating, eventViewModel, event)
        }
        // Icon for settings or more options, assuming using Material Icons
        EventDateTime(day = event.getDateString(), time = event.getTimeString())
      }
}

/**
 * RateEvent is a composable that enables a user other than an event's creator to rate a past event.
 *
 * @param rating Rating of the event by the current user (0 if unrated, 1-5 otherwise)
 * @param eventViewModel EventViewModel object to handle event operations
 * @param event Event object
 */
@Composable
fun RateEvent(rating: MutableState<Long>, eventViewModel: EventViewModel, event: Event) {
  if (eventViewModel.currentUID!! != event.creator &&
      event.isPastEvent() &&
      event.hasUserJoined(eventViewModel.currentUID)) {
    Row {
      for (i in 1..5) {
        val star = if (i <= rating.value) Icons.Filled.Star else Icons.TwoTone.Star
        Icon(
            imageVector = star,
            contentDescription = "Rating stars",
            tint = MaterialTheme.colorScheme.outlineVariant,
            modifier =
                Modifier.clickable {
                      val oldRating = rating.value
                      if (rating.value == i.toLong()) {
                        rating.value = 0
                      } else {
                        rating.value = i.toLong()
                      }
                      eventViewModel.updateRating(
                          event.eventID, rating.value, oldRating, event.creator)
                    }
                    .padding(4.dp))
      }
    }
  }
}

/**
 * EventDateTime is a composable that displays the date and time of an event.
 *
 * @param day Day of the event
 * @param time Time of the event
 */
@Composable
fun EventDateTime(day: String, time: String) {
  Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(end = 15.dp)) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
          text = day,
          style =
              TextStyle(
                  fontSize = 24.sp,
                  color = MaterialTheme.colorScheme.tertiary,
                  fontWeight = FontWeight.SemiBold,
                  textAlign = TextAlign.Center))
      Text(
          text = time,
          style =
              TextStyle(
                  fontSize = 16.sp,
                  color = MaterialTheme.colorScheme.tertiary,
                  textAlign = TextAlign.Center))
    }
  }
}

/**
 * EventImage is a composable that displays the image of an event.
 *
 * @param imageUrl URL of the image
 */
@Composable
fun EventImage(imageUrl: String?) {
  if (imageUrl != null) {
    val imagePainter =
        rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .data(data = imageUrl)
                .apply(
                    block =
                        fun ImageRequest.Builder.() {
                          placeholder(R.drawable.gomeet_logo)
                        })
                .build())
    Column(modifier = Modifier.fillMaxWidth().testTag("EventImage").padding(top = 10.dp)) {
      Image(
          painter = imagePainter,
          contentDescription = "Event Image",
          contentScale = ContentScale.Crop,
          modifier = Modifier.aspectRatio(3f / 1.75f).clip(RoundedCornerShape(20.dp)))
    }
  }
}

/**
 * EventDescription is a composable that displays the description of an event.
 *
 * @param text Description of the event
 */
@Composable
fun EventDescription(text: String) {
  Text(
      text = text,
      color = MaterialTheme.colorScheme.tertiary,
      style = MaterialTheme.typography.bodyLarge,
      modifier = Modifier.testTag("EventDescription").padding(horizontal = 10.dp))
}

/**
 * Renders actionable buttons for an event. This includes the ability to join or leave an event,
 * edit an event (if the current user is the organizer), and navigate to messaging with the
 * organizer. Favorite toggling is also managed here.
 *
 * @param currentUser The currently logged-in user, as a GoMeetUser object.
 * @param organiser The organizer of the event, also a GoMeetUser object.
 * @param event The event.
 * @param userViewModel An instance of UserViewModel for performing operations like editing user
 *   details.
 * @param nav An instance of NavigationActions for handling navigation events.
 */
@Composable
fun EventButtons(
    currentUser: GoMeetUser,
    organiser: GoMeetUser,
    event: Event,
    userViewModel: UserViewModel,
    eventViewModel: EventViewModel,
    nav: NavigationActions
) {
  val coroutineScope = rememberCoroutineScope()
  val isFavourite = remember { mutableStateOf(currentUser.myFavorites.contains(event.eventID)) }
  val currentEvent = remember { mutableStateOf<Event?>(null) }
  val isJoined = remember { mutableStateOf(false) }

  /*
  if buttons is 0 then the user is the organizer,
  if buttons is 1 the user has joined the event,
  if buttons is 2 the user is invited to the event,
  if buttons is 3 the user hasn't joined the event and is not invited
  */
  var buttons by remember { mutableIntStateOf(10) }

  LaunchedEffect(Unit) {
    coroutineScope.launch {
      currentEvent.value = eventViewModel.getEvent(event.eventID)
      isJoined.value =
          currentUser.joinedEvents.contains(event.eventID) &&
              currentEvent.value!!.participants.contains(currentUser.uid)
      buttons =
          if (organiser.uid == currentUser.uid) 0
          else if (isJoined.value) 1
          else if (currentUser.pendingRequests.any { invitation ->
            invitation.eventId == event.eventID
          })
              2
          else 3
    }
  }
  if (buttons < 10) {

    Row(
        modifier = Modifier.fillMaxWidth().testTag("EventButton"),
        horizontalArrangement = Arrangement.SpaceBetween) {
          TextButton(
              onClick = {
                eventAction(
                    nav,
                    currentUser,
                    event,
                    userViewModel,
                    eventViewModel,
                    currentEvent.value!!,
                    buttons) { i ->
                      buttons = i
                    }
              },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f),
              colors =
                  when (buttons) {
                    0 ->
                        ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.tertiary)
                    1 ->
                        ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.tertiary)
                    else ->
                        ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.outlineVariant,
                            contentColor = White)
                  },
              content = {
                when (buttons) {
                  0 -> Text("Edit My Event")
                  1 -> Text("Leave Event")
                  2 -> Text("Accept Invite")
                  3 -> Text("Join Event")
                }
              })

          if (buttons == 0 || buttons == 2) {
            Spacer(modifier = Modifier.width(5.dp))
            TextButton(
                onClick = {
                  if (buttons == 0) {
                    nav.navigateToScreen(Route.MANAGE_INVITES.replace("{eventId}", event.eventID))
                  } else {
                    buttons = 3
                    userViewModel.userRefusesInvitation(event, currentUser, eventViewModel)
                    isJoined.value = false
                  }
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f),
                colors =
                    ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.tertiary)) {
                  if (buttons == 0) {
                    Text("Add Participants")
                  } else {
                    Text("Decline Invite")
                  }
                }
          }
          if (buttons != 0) {
            IconButton(
                onClick = {
                  nav.navigateToScreen(Route.MESSAGE.replace("{id}", Uri.encode(organiser.uid)))
                }) {
                  Icon(
                      imageVector =
                          ImageVector.vectorResource(
                              id = R.drawable.baseline_chat_bubble_outline_24),
                      contentDescription = "Chat",
                      modifier = Modifier.size(24.dp),
                      tint = MaterialTheme.colorScheme.onBackground)
                }
          }
          IconButton(
              onClick = {
                favouriteAction(currentUser, event.eventID, userViewModel, isFavourite)
              }) {
                FavouriteButton(isFavourite.value)
              }
        }
  }
}

/**
 * Helper function to perform the appropriate action when clicking the favourite button.
 *
 * @param currentUser Current user
 * @param eventId Event ID
 * @param userViewModel UserViewModel
 * @param isFavourite Whether the event is a favourite
 */
private fun favouriteAction(
    currentUser: GoMeetUser,
    eventId: String,
    userViewModel: UserViewModel,
    isFavourite: MutableState<Boolean>
) {
  if (!isFavourite.value) {
    currentUser.myFavorites = currentUser.myFavorites.plus(eventId)
  } else {
    currentUser.myFavorites = currentUser.myFavorites.minus(eventId)
  }
  userViewModel.editUser(currentUser)
  isFavourite.value = !isFavourite.value
}

/**
 * Helper function to show the favourite button composable.
 *
 * @param isFavourite Whether the event is a favourite.
 */
@Composable
private fun FavouriteButton(isFavourite: Boolean) {
  val tint = MaterialTheme.colorScheme.outlineVariant
  val iconSize = 30.dp
  if (!isFavourite) {
    Icon(
        imageVector = ImageVector.vectorResource(id = R.drawable.heart),
        contentDescription = "Add to Favorites",
        modifier = Modifier.size(iconSize),
        tint = tint)
  } else {
    Icon(
        imageVector = ImageVector.vectorResource(id = R.drawable.redheart),
        contentDescription = "Remove from favorites",
        modifier = Modifier.size(iconSize),
        tint = tint)
  }
}

/**
 * Helper function to perform the appropriate action when clicking the event button.
 *
 * @param currentUser Current user
 * @param eventId Event ID
 * @param userViewModel UserViewModel
 * @param currentEvent The current event
 */
private fun eventAction(
    nav: NavigationActions,
    currentUser: GoMeetUser,
    event: Event,
    userViewModel: UserViewModel,
    eventViewModel: EventViewModel,
    currentEvent: Event,
    buttons: Int,
    callback: (Int) -> Unit
) {
  when (buttons) {
    0 -> nav.navigateToScreen(Route.EDIT_EVENT.replace("{eventId}", event.eventID))
    1 -> {
      currentUser.joinedEvents = currentUser.joinedEvents.minus(event.eventID)
      currentEvent.participants = currentEvent.participants.minus(currentUser.uid)
      userViewModel.editUser(currentUser)
      eventViewModel.editEvent(currentEvent)
      callback(3)
    }
    2 -> {
      userViewModel.userAcceptsInvitation(event, currentUser, eventViewModel)
      callback(1)
    }
    3 -> {
      currentUser.joinedEvents = currentUser.joinedEvents.plus(event.eventID)
      currentEvent.participants = currentEvent.participants.plus(currentUser.uid)
      userViewModel.editUser(currentUser)
      eventViewModel.editEvent(currentEvent)
      callback(1)
    }
  }
}
