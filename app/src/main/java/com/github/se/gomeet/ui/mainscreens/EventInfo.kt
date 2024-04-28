package com.github.se.gomeet.ui.mainscreens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.R
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.SECOND_LEVEL_DESTINATION
import com.github.se.gomeet.ui.theme.DarkCyan
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

/**
 * EventInfo is a composable that displays the details of an event.
 *
 * @param nav NavigationActions object to handle navigation
 * @param title Title of the event
 * @param date Date of the event
 * @param time Time of the event
 * @param organizer Organizer of the event
 * @param rating Rating of the event
 * @param image Image of the event
 * @param description Description of the event
 * @param loc Location of the event
 */
@Composable
fun EventInfo(
    nav: NavigationActions,
    title: String = "",
    date: String = "",
    time: String = "",
    organizer: String = "",
    rating: Double = 0.0,
    image: Painter = painterResource(id = R.drawable.chess_demo),
    description: String = "",
    loc: LatLng = LatLng(0.0, 0.0)
) {
  Log.d("EventInfo", "Organizer is $organizer")
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
              IconButton(
                  onClick = {
                    nav.navigateToScreen(Route.MESSAGE.replace("{id}", Uri.encode(organizer)))
                  }) {
                    Icon(
                        imageVector =
                            ImageVector.vectorResource(
                                id = R.drawable.baseline_chat_bubble_outline_24),
                        contentDescription = "Share",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onBackground)
                  }

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
        Column(
            modifier =
                Modifier.padding(innerPadding)
                    .padding(start = 15.dp, end = 15.dp, top = 0.dp, bottom = 15.dp)
                    .fillMaxSize()
                    .verticalScroll(state = rememberScrollState())) {
              EventHeader(
                  title = title,
                  organizer = organizer,
                  rating = rating,
                  nav = nav,
                  date = date,
                  time = time)
              Spacer(modifier = Modifier.height(20.dp))
              EventButtons()
              Spacer(modifier = Modifier.height(20.dp))
              EventImage(painter = image)
              Spacer(modifier = Modifier.height(20.dp))
              EventDescription(text = description)
              Spacer(modifier = Modifier.height(20.dp))
              MapViewComposable(loc = loc)
            }
      }
}

/**
 * EventHeader is a composable that displays the header of an event.
 *
 * @param title Title of the event
 * @param organizer Organizer of the event
 * @param rating Rating of the event
 * @param nav NavigationActions object to handle navigation
 * @param date Date of the event
 * @param time Time of the event
 */
@Composable
fun EventHeader(
    title: String,
    organizer: String,
    rating: Double,
    nav: NavigationActions,
    date: String,
    time: String
) {
  Row(
      modifier = Modifier.fillMaxWidth().testTag("EventHeader"),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
          Text(
              text = title,
              style =
                  TextStyle(
                      fontSize = 24.sp,
                      fontWeight = FontWeight.Bold,
                      color = DarkCyan,
                      letterSpacing = 0.5.sp))
          Spacer(modifier = Modifier.height(5.dp))
          Text(
              modifier = Modifier.clickable { nav.navigateTo(SECOND_LEVEL_DESTINATION[0]) },
              text = organizer,
              style =
                  TextStyle(
                      fontSize = 16.sp,
                      fontWeight = FontWeight.ExtraBold,
                      color = Color.Gray,
                      fontFamily = FontFamily(Font(R.font.roboto)),
                      letterSpacing = 0.5.sp))
          // Add other details like rating here
        }
        // Icon for settings or more options, assuming using Material Icons
        EventDateTime(day = date, time = time)
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
                  color = MaterialTheme.colorScheme.onBackground,
                  fontWeight = FontWeight.SemiBold,
                  textAlign = TextAlign.Center))
      Text(
          text = time,
          style =
              TextStyle(
                  fontSize = 16.sp,
                  color = MaterialTheme.colorScheme.onBackground,
                  textAlign = TextAlign.Center))
    }
  }
}

/**
 * EventImage is a composable that displays the image of an event.
 *
 * @param painter Painter object for the image
 */
@Composable
fun EventImage(painter: Painter) {
  Column(modifier = Modifier.fillMaxWidth().testTag("EventImage")) {
    Image(
        painter = painter,
        contentScale = ContentScale.Crop,
        contentDescription = "Event Image",
        modifier =
            Modifier.aspectRatio(3f / 1.75f)
                // Specify the height you want for the image

                .clip(RoundedCornerShape(20.dp)))
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
      style =
          TextStyle(
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onBackground,
              fontFamily = FontFamily(Font(R.font.roboto)),
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 0.5.sp),
      modifier = Modifier.testTag("EventDescription"))
}

/**
 * EventButtons is a composable that displays the buttons for an event.
 */
@Composable
fun EventButtons() {
  Row(
      modifier = Modifier.fillMaxWidth().testTag("EventButton"),
      horizontalArrangement = Arrangement.SpaceBetween) {
        TextButton(
            onClick = { /* Handle button click */},
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.weight(1f),
            colors =
                ButtonDefaults.textButtonColors(
                    containerColor = Color(0xFFECEFF1), contentColor = Color.Black)) {
              Text("Get tickets")
            }
        IconButton(onClick = { /* Handle button click */}) {
          Icon(
              imageVector = ImageVector.vectorResource(id = R.drawable.heart),
              contentDescription = "Email",
              modifier = Modifier.size(30.dp),
              tint = DarkCyan)
        }
      }
}

/**
 * MapViewComposable is a composable that displays the map view of an event.
 *
 * @param loc Location of the event
 * @param zoomLevel Zoom level of the map
 */
@Composable
fun MapViewComposable(
    loc: LatLng,
    zoomLevel: Float = 15f // Default zoom level for close-up of location
) {
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(loc, zoomLevel)
  }

  val markerState = rememberMarkerState(position = loc)

  // Set up the GoogleMap composable
  GoogleMap(
      modifier =
          Modifier.testTag("MapView").fillMaxWidth().height(200.dp).clip(RoundedCornerShape(20.dp)),
      cameraPositionState = cameraPositionState) {
        Marker(
            state = markerState,
            title = "Marker in Location",
            snippet = "This is the selected location")
      }

  // Initialize the map position once and avoid resetting on recomposition
  DisposableEffect(loc) {
    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(loc, zoomLevel))
    onDispose {}
  }

  DisposableEffect(loc) {
    markerState.position = loc
    onDispose {}
  }
}

/**
 * EventInfoScreen is a composable that displays the details of an event.
 *
 * @param navController NavHostController object to handle navigation
 */
@Composable
fun EventInfoScreen(navController: NavHostController) {
  val backStackEntry = navController.currentBackStackEntryAsState().value
  val arguments = backStackEntry?.arguments

  val title = arguments?.getString("title") ?: ""
  val date = arguments?.getString("date") ?: ""
  val time = arguments?.getString("time") ?: ""
  val organizer = arguments?.getString("organizer") ?: ""
  val rating = arguments?.getDouble("rating") ?: 0.0
  val description = arguments?.getString("description") ?: ""
  val latitude = arguments?.getFloat("latitude") ?: 0.0
  val longitude = arguments?.getFloat("longitude") ?: 0.0
  val loc = LatLng(latitude.toDouble(), longitude.toDouble())

  Log.d("EventInfoScreen", "Loc is $loc")

  EventInfo(
      nav = NavigationActions(navController),
      title = title,
      date = date,
      time = time,
      organizer = organizer,
      rating = rating,
      image =
          painterResource(
              id = R.drawable.chess_demo), // Image handling might need different approach
      description = description,
      loc = loc)
}

@Preview(showBackground = true)
@Composable
fun EventScreenPreview() {
  EventInfo(
      nav = NavigationActions(rememberNavController()),
      title = "Chess Tournament",
      organizer = "EPFL Chess Club",
      date = "TUE",
      time = "19:10",
      rating = 4.8,
      image = painterResource(id = R.drawable.chess_demo),
      description =
          "Howdy!\n\nAfter months of planning, La Dame Blanche is finally offering you a rapid tournament!\n\nJoin us on Saturday 23rd of March afternoon for 6 rounds of 12+3” games in the chill and cozy vibe of Satellite. Take your chance to have fun and play, and maybe win one of our many prizes\n\nOnly 50 spots available, with free entry!",
      loc = LatLng(46.519962, 6.633597))
}
