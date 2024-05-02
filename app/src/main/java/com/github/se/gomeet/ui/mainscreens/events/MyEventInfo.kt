package com.github.se.gomeet.ui.mainscreens.events

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.R
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@Composable
fun MyEventInfo(
    nav: NavigationActions,
    title: String = "",
    eventId: String = "",
    date: String = "",
    time: String = "",
    organizerId: String,
    rating: Double = 0.0,
    image: Painter = painterResource(id = R.drawable.chess_demo),
    description: String = "",
    loc: LatLng = LatLng(0.0, 0.0),
    userViewModel: UserViewModel
) {

  val organizer = remember { mutableStateOf<GoMeetUser?>(null) }
  val currentUser = remember { mutableStateOf<GoMeetUser?>(null) }

  val coroutineScope = rememberCoroutineScope()

  LaunchedEffect(Unit) {
    coroutineScope.launch { organizer.value = userViewModel.getUser(organizerId) }
    currentUser.value = userViewModel.getUser(Firebase.auth.currentUser!!.uid)
  }

  Log.d("EventInfo", "Organizer is $organizerId")
  Scaffold(
      topBar = {
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
                    tint = MaterialTheme.colorScheme.onBackground)
              }
            },
            actions = {
              IconButton(onClick = { /* Handle more action */}) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "More",
                    modifier = Modifier.rotate(90f),
                    tint = MaterialTheme.colorScheme.onBackground)
              }
            })
      },
      bottomBar = {
        // Your bottom bar content
      }) { innerPadding ->
        if (organizer.value == null || currentUser.value == null) {
          Text(text = "Loading....", Modifier.padding(innerPadding))
        } else {
          Column(
              modifier =
                  Modifier.padding(innerPadding)
                      .padding(start = 15.dp, end = 15.dp, top = 0.dp, bottom = 15.dp)
                      .fillMaxSize()
                      .verticalScroll(state = rememberScrollState())) {
                EventHeader(
                    title = title,
                    organizer = organizer.value!!,
                    rating = rating,
                    nav = nav,
                    date = date,
                    time = time)
                Spacer(modifier = Modifier.height(20.dp))
                EventButtons(currentUser.value!!, organizer.value!!, eventId, userViewModel, nav)
                Spacer(modifier = Modifier.height(20.dp))
                EventImage(painter = image)
                Spacer(modifier = Modifier.height(20.dp))
                EventDescription(text = description)
                Spacer(modifier = Modifier.height(20.dp))
                MapViewComposable(loc = loc)
              }
        }
      }
}

@Preview
@Composable
fun PreviewEventInfo() {
  MyEventInfo(
      nav = NavigationActions(rememberNavController()),
      title = "Event Title",
      eventId = "eventid",
      date = "2024-05-01",
      organizerId = "organiserid",
      time = "00:00",
      description = "Event Description",
      loc = LatLng(0.0, 0.0),
      userViewModel = UserViewModel())
}
