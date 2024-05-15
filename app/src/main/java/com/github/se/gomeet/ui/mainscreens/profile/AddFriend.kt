package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.se.gomeet.R
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.mainscreens.events.GoMeetSearchBar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import coil.compose.rememberAsyncImagePainter
import com.github.se.gomeet.ui.navigation.Route
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFriend(nav: NavigationActions, userViewModel: UserViewModel) {

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
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
                users = fetchedUsers.filter { it.uid != currentUser }  // Exclude the current user
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
    val filteredUsers by remember(query.value, users) {
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
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    })

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = screenWidth / 15, top = 0.dp)) {
                    Text(
                        text = "Find User",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.SemiBold))
                }
            }
        },
        bottomBar = {
            // Your bottom bar content
        }
    ) { innerPadding ->

        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column {
                    GoMeetSearchBar(nav, query, MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.height(5.dp))
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredUsers) { user ->
                            UserItem(user, followedUsers, nav) { uid, isFollowing ->
                                coroutineScope.launch {
                                    if (isFollowing) {
                                        userViewModel.unfollow(uid)
                                    } else {
                                        userViewModel.follow(uid)
                                    }
                                    // Directly update the state for immediate reflection
                                    followedUsers = if (isFollowing) {
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
        modifier =
        modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color = Color.Gray),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun UserItem(
    user: GoMeetUser,
    followedUsers: Set<String>,
    nav: NavigationActions,
    onFollowButtonClick: (String, Boolean) -> Unit
) {
    val isFollowing = followedUsers.contains(user.uid)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable { nav.navigateToScreen(Route.OTHERS_PROFILE.replace("{uid}", user.uid)) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProfileImageUser(userId = user.uid)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "${user.firstName} ${user.lastName}", style = MaterialTheme.typography.bodyMedium)
                Text(text = user.username, style = MaterialTheme.typography.bodySmall)
            }
        }
        Button(
            colors = ButtonDefaults.buttonColors(
                 if (isFollowing) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primaryContainer,
                 if (isFollowing) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.tertiary
            ),
            onClick = { onFollowButtonClick(user.uid, isFollowing) }
        ) {
            Text(text = if (isFollowing) "Unfollow" else "Follow")
        }
    }
}
