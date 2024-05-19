package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.github.se.gomeet.R
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Composable to display a user's profile picture.
 *
 * @param userId The user's ID.
 * @param modifier The modifier for the image.
 * @param defaultImageResId The default image resource ID.
 */
@Composable
fun ProfileImageUser(
    userId: String,
    modifier: Modifier = Modifier,
    defaultImageResId: Int = R.drawable.gomeet_logo
) {
    var profilePictureUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        val db = FirebaseFirestore.getInstance()
        val userDocRef = db.collection("users").document(userId)
        try {
            val snapshot = userDocRef.get().await()
            profilePictureUrl = snapshot.getString("profilePicture")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Image(
        painter =
        if (!profilePictureUrl.isNullOrEmpty()) {
            rememberAsyncImagePainter(profilePictureUrl)
        } else {
            painterResource(id = defaultImageResId)
        },
        contentDescription = "Profile picture",
        modifier = modifier.size(55.dp).clip(CircleShape).background(color = Color.Gray),
        contentScale = ContentScale.Crop)
}

/**
 * Composable to display a user.
 *
 * @param user The user to display.
 * @param followedUsers The set of followed users.
 * @param nav The navigation actions.
 * @param onFollowButtonClick The callback to handle follow button clicks
 */
@Composable
fun UserItem(
    user: GoMeetUser,
    followedUsers: Set<String>,
    nav: NavigationActions,
    onFollowButtonClick: (String, Boolean) -> Unit
) {
    val isFollowing = followedUsers.contains(user.uid)
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Row(
        modifier =
        Modifier.fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable { nav.navigateToScreen(Route.OTHERS_PROFILE.replace("{uid}", user.uid)) }
            .testTag("UserItem"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProfileImageUser(userId = user.uid)
            Spacer(modifier = Modifier.width(screenWidth / 20))
            Column {
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6F))
            }
        }
        Button(
            shape = RoundedCornerShape(10.dp),
            onClick = { onFollowButtonClick(user.uid, isFollowing) },
            modifier =
            Modifier.padding(start = 15.dp)
                .width(110.dp)
                .testTag(if (isFollowing) "UnfollowButton" else "FollowButton"),
            colors =
            ButtonDefaults.buttonColors(
                containerColor =
                if (isFollowing) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.outlineVariant,
                contentColor = if (isFollowing) Color.Black else Color.White,
                disabledContentColor = Color.Transparent,
                disabledContainerColor = Color.Transparent)) {
            Text(
                text = if (isFollowing) "Following" else "Follow",
                style = MaterialTheme.typography.labelLarge,
                color = if (isFollowing) MaterialTheme.colorScheme.onBackground else Color.White)
        }
    }
}
