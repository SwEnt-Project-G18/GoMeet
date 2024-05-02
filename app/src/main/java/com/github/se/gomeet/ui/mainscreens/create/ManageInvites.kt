package com.github.se.gomeet.ui.mainscreens.create

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.InviteStatus
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.DarkCyan

@Composable
fun ManageInvites(

//    currentUser: String,
                 currentEvent: String,
                 nav: NavigationActions,
//                 userViewModel: UserViewModel,
//                 eventViewModel: EventViewModel
) {

    var selectedFilter by remember { mutableStateOf("All") }
    val userList = remember { mutableListOf<GoMeetUser>() }
    val coroutineScope = rememberCoroutineScope()
    val query = remember { mutableStateOf("") }
    val user = remember { mutableStateOf<GoMeetUser?>(null) }
    val event = remember { mutableStateOf<Event?>(null) }

    // Initial data loading using LaunchedEffect
//    LaunchedEffect(Unit) {
//        coroutineScope.launch {
//            user.value = userViewModel.getUser(currentUser)
//            event.value = eventViewModel.getEvent(currentEvent)
//            val friendList = userViewModel.getUserFriends(currentUser)
//            if (friendList.isNotEmpty()) {
//                userList.addAll(friendList)
//            }
//        }
//    }



    Scaffold(
        modifier = Modifier.testTag("ManageInvitesScreen"),
        bottomBar = {
            BottomNavigationMenu(
                onTabSelect = { selectedTab ->
                    nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
                },
                tabList = TOP_LEVEL_DESTINATIONS,
                selectedItem = Route.EVENTS)
        },
        topBar = {
            Row {
                Text(
                    text = "Manage Invites",
                    modifier = Modifier.padding(start = 15.dp, top = 15.dp, end = 15.dp, bottom = 15.dp),
                    color = DarkCyan,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Default,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.headlineLarge)

            }
        }) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState(0))
                .fillMaxSize()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, end = 15.dp, bottom = 15.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top) {
                Button(
                    onClick = { /*TODO*/},
                    modifier = Modifier
                        .height(40.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                    Text(text = "All", color = MaterialTheme.colorScheme.onBackground)
                }

                Button(
                    onClick = { /*TODO*/},
                    modifier = Modifier
                        .height(40.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                    Text(text = "Invited", color = MaterialTheme.colorScheme.onBackground)
                }

                Button(
                    onClick = { /*TODO*/},
                    modifier = Modifier
                        .height(40.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                    Text(text = "Accepted", color = MaterialTheme.colorScheme.onBackground)
                }
            }

            /* TODO: for every followers this user has, retrieve them and stock them in a list and
                display them in the following way using the UserInviteWidget in a for-loop
             */

            UserInviteWidget(username = "Test", status = InviteStatus.PENDING)
            UserInviteWidget(username = "Test", status = InviteStatus.REFUSED)
            UserInviteWidget(username = "Test", status = InviteStatus.ACCEPTED)
            UserInviteWidget(username = "Test", status = null)
        }
    }
}




@Composable
fun UserInviteWidget(username : String, status : InviteStatus?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp)
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Image(
            modifier =
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.background),
            painter = painterResource(id = R.drawable.gomeet_logo),
            contentDescription = "profile picture",
            contentScale = ContentScale.None)

        Text(text = username, color = MaterialTheme.colorScheme.onBackground)

        // Status text
        Text(
            text = when(status) {
                null -> "Invite"
                InviteStatus.PENDING -> "Pending"
                InviteStatus.ACCEPTED -> "Accepted"
                InviteStatus.REFUSED -> "Refused"
            },
            modifier = Modifier.width(70.dp),
            style = TextStyle(fontSize = 12.sp),
            color = when(status) {
                null -> Color.White
                InviteStatus.PENDING -> Color.Gray
                InviteStatus.ACCEPTED -> Color.Green
                InviteStatus.REFUSED -> Color.Red
            }
        )

        // Button
        Button(
            onClick = {/*TODO*/},
            modifier = Modifier
                .height(26.dp)
                .width(82.dp),
            contentPadding = PaddingValues(vertical = 2.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor =when(status) {
                    null -> DarkCyan
                    InviteStatus.PENDING -> Color.LightGray
                    InviteStatus.ACCEPTED -> Color.Red
                    InviteStatus.REFUSED -> Color.LightGray
                }
            ))

        {
            Text(
                text = when(status) {
                    null -> "Invite"
                    InviteStatus.PENDING -> "Cancel"
                    InviteStatus.ACCEPTED -> "Cancel"
                    InviteStatus.REFUSED -> "Invite"
                },
                color = when(status) {
                    null -> Color.White
                    InviteStatus.PENDING -> Color.DarkGray
                    InviteStatus.ACCEPTED -> Color.White
                    InviteStatus.REFUSED -> Color.White
                },
                fontSize = 12.sp)
        }
    }
}


@Preview
@Composable
fun ManageInvitesPreview() {
    ManageInvites("eventId",nav = NavigationActions(rememberNavController()))

}