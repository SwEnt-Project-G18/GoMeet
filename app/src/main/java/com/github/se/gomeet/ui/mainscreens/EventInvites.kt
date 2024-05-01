package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.InviteStatus
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.ui.theme.Grey
import com.github.se.gomeet.ui.theme.NavBarUnselected
import com.github.se.gomeet.ui.theme.White
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun EventInvites(
//    currentUser: String,
//                 currentEvent: String,
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
        bottomBar = {
            BottomNavigationMenu(
                onTabSelect = { selectedTab ->
                    nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
                },
                tabList = TOP_LEVEL_DESTINATIONS,
                selectedItem = Route.PROFILE)
        },
        topBar = {
            Row {
                Text(
                    text = "Manage Invites",
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 15.dp),
                    color = DarkCyan,
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Default,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.headlineLarge)

            }
        }) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState(0))) {


            Row(

                //Set row width to fill the screen
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top) {
                // Edit Profile button
                Button(
                    onClick = { /*TODO*/},
                    modifier = Modifier
                        .height(40.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECEFF1))) {
                    //center text in button
                    Text(text = "All", color = Color.Black)
                }

                Button(
                    onClick = { /*TODO*/},
                    modifier = Modifier
                        .height(40.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEFF1))) {
                    Text(text = "Pending", color = Color.Black)
                }

                Button(
                    onClick = { /*TODO*/},
                    modifier = Modifier
                        .height(40.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECEFF1))) {
                    Text(text = "Accepted", color = Color.Black)
                }
            }
            UserInviteWidget(username = "Test", status = InviteStatus.PENDING)
            UserInviteWidget(username = "Test", status = InviteStatus.REFUSED)
            UserInviteWidget(username = "Test", status = InviteStatus.ACCEPTED)
            UserInviteWidget(username = "Test", status = null)




        }
    }
}




@Composable
fun UserInviteWidget(username : String, usertag : String = "usertag" , status : InviteStatus?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        // Profile picture and name
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Gray) // Change the color as needed
        )
        Column {
            Text(text = username, style = TextStyle(fontSize = 16.sp))
            Text(text = "@$usertag", style = TextStyle(fontSize = 12.sp), color = Grey)
        }

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
fun EventInvitesPreview() {
    EventInvites(nav = NavigationActions(rememberNavController()))

}