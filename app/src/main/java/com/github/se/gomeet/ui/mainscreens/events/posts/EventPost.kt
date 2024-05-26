package com.github.se.gomeet.ui.mainscreens.events.posts

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.Post
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.mainscreens.profile.ProfileImage
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.theme.Grey
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import kotlinx.coroutines.launch

/**
 * Composable function to display an event post.
 *
 * @param nav NavigationActions object to handle navigation
 * @param event Event object representing the event associated with the post
 * @param post Post object representing the post details
 * @param userViewModel UserViewModel object to interact with user data
 * @param eventViewModel EventViewModel object to interact with event data
 * @param currentUser The ID of the current user
 * @param onPostDeleted Callback function to be called when the post is deleted
 */
@Composable
fun EventPost(
    nav: NavigationActions,
    event: Event,
    post: Post,
    userViewModel: UserViewModel,
    eventViewModel: EventViewModel,
    currentUser: String,
    onPostDeleted: (Post) -> Unit
) {
  var poster by remember { mutableStateOf<GoMeetUser?>(null) }
  var liked by remember { mutableStateOf(false) }
  var likes by remember { mutableIntStateOf(0) }
  val coroutineScope = rememberCoroutineScope()
  val screenHeight = LocalConfiguration.current.screenHeightDp.dp

  var showDeletePostDialog by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    coroutineScope.launch {
      poster = userViewModel.getUser(post.userId)
      liked = post.likes.contains(currentUser)
      likes = post.likes.size
    }
  }

  Column {
    if (poster != null) {
      Row(
          horizontalArrangement = Arrangement.Start,
          verticalAlignment = Alignment.CenterVertically,
          modifier =
              Modifier.clickable {
                    if (currentUser == post.userId) {
                      nav.navigateToScreen(Route.PROFILE)
                    } else {
                      nav.navigateToScreen(Route.OTHERS_PROFILE.replace("{uid}", post.userId))
                    }
                  }
                  .testTag("EventPostUserInfo")) {
            ProfileImage(
                userId = poster!!.uid,
                modifier = Modifier.testTag("Event Post Profile Picture"),
                size = 50.dp)

            Column(
                horizontalAlignment = Alignment.Start, modifier = Modifier.padding(start = 10.dp)) {
                  Text(
                      (poster!!.firstName + " " + poster!!.lastName),
                      textAlign = TextAlign.Center,
                      style = MaterialTheme.typography.bodyLarge,
                      color = MaterialTheme.colorScheme.tertiary)

                  Text(
                      text = "@" + (poster!!.username),
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.secondary)
                }
          }
      if (post.content.isNotEmpty()) {

        Spacer(modifier = Modifier.height(screenHeight / 60))
        Text(
            modifier = Modifier.padding(horizontal = 10.dp),
            text = post.content,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.tertiary)
      }

      if (post.image.isNotEmpty()) {
        Spacer(modifier = Modifier.height(screenHeight / 80))
        Image(
            painter = rememberAsyncImagePainter(post.image),
            contentDescription = "Post Image",
            contentScale = ContentScale.Crop,
            modifier =
                Modifier.padding(horizontal = 10.dp)
                    .aspectRatio(2f)
                    .clip(RoundedCornerShape(20.dp)))
      }
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = {
              val oldPost = post.copy()
              if (liked) {
                likes--
                post.likes = post.likes.minus(currentUser)
                eventViewModel.editPost(event, oldPost, post)
              } else {
                likes++
                post.likes = post.likes.plus(currentUser)
                eventViewModel.editPost(event, oldPost, post)
              }
              liked = !liked
            }) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                if (!liked) {
                  Icon(
                      Icons.Outlined.ThumbUp,
                      contentDescription = "Like",
                      tint = MaterialTheme.colorScheme.tertiary)
                  Spacer(modifier = Modifier.width(5.dp))
                  Text(
                      text = likes.toString(),
                      color = MaterialTheme.colorScheme.tertiary,
                      style = MaterialTheme.typography.bodyMedium)
                } else {
                  Icon(
                      Icons.Filled.ThumbUp,
                      contentDescription = "Like",
                      tint = MaterialTheme.colorScheme.outlineVariant)
                  Spacer(modifier = Modifier.width(5.dp))
                  Text(
                      text = likes.toString(),
                      color = MaterialTheme.colorScheme.outlineVariant,
                      style = MaterialTheme.typography.bodyMedium)
                }
              }
            }

        if (currentUser == post.userId) {
          IconButton(onClick = { showDeletePostDialog = true }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.onBackground)
          }
        }

        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "${event.date}, ${event.time}",
            style = MaterialTheme.typography.bodyMedium,
            color = Grey)
      }
    }
  }

  if (showDeletePostDialog) {
    DeletePostDialog(
        onConfirm = {
          coroutineScope.launch {
            eventViewModel.deletePost(event, post)
            onPostDeleted(post)
            showDeletePostDialog = false
          }
        },
        onDismiss = { showDeletePostDialog = false })
  }
}

@Composable
private fun DeletePostDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
  AlertDialog(
      onDismissRequest = onDismiss,
      confirmButton = {
        Button(
            onClick = onConfirm,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.outlineVariant)) {
              Text("Confirm")
            }
      },
      dismissButton = {
        Button(
            onClick = onDismiss,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onErrorContainer)) {
              Text("Cancel")
            }
      },
      title = { Text("Delete Post") },
      text = { Text("Are you sure you want to delete this post?") },
      containerColor = MaterialTheme.colorScheme.background,
  )
}
