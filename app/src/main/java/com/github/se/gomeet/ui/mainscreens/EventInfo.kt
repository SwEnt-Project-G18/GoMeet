import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.R
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.SECOND_LEVEL_DESTINATION
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.ui.theme.Grey
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun EventInfo(nav: NavigationActions, title: String = "", date: String = "", time: String ="", organizer: String ="", rating: Double =0.0, image: Painter = painterResource(id = R.drawable.ic_launcher_background), description: String = "", loc: LatLng = LatLng(0.0, 0.0)){
    Scaffold(
        topBar = {
            TopAppBar(
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
                },

                actions = {
                    IconButton( onClick = { /* Handle share action */ }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_chat_bubble_outline_24),
                            contentDescription = "Share",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    IconButton(onClick = { /* Handle more action */ }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More",
                            modifier = Modifier.rotate(90f),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

            )
        },
        bottomBar = {
            // Your bottom bar content
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(start = 15.dp, end = 15.dp, top = 0.dp, bottom = 15.dp)
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
        ) {
            EventHeader(title = title, organizer = organizer, rating = rating, nav = nav, date = date, time = time)
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

@Composable
fun EventHeader(title: String, organizer: String, rating: Double, nav: NavigationActions, date : String , time : String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = title,
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkCyan)
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                modifier = Modifier.clickable { nav.navigateTo(SECOND_LEVEL_DESTINATION.get(0)) },
                text = organizer,
                style = TextStyle(fontSize = 16.sp, color = Color.Gray)
            )
            // Add other details like rating here
        }
        // Icon for settings or more options, assuming using Material Icons
        EventDateTime(day = date, time = time)
    }
}

@Composable
fun EventDateTime(day: String, time: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(end = 15.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = day,
                style = TextStyle(
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            )
            Text(
                text = time,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
fun EventImage(painter: Painter) {
    Column (modifier = Modifier.fillMaxWidth() ){
        Image(
            painter = painter,
            contentScale = ContentScale.Crop,
            contentDescription = "Event Image",
            modifier = Modifier
                .aspectRatio(3f / 1.75f)
                // Specify the height you want for the image

                .clip(RoundedCornerShape(20.dp))
        )
    }
}

@Composable
fun EventDescription(text: String) {
    Text(
        text = text,
        style = TextStyle(fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground)
    )
}

@Composable
fun EventButtons() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(
            onClick = { /* Handle button click */ },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .weight(1f),
            colors = ButtonDefaults.textButtonColors(
                containerColor = Color(0xFFECEFF1),
                contentColor = Color.Black
            )
        ) {
            Text("Get tickets")
        }
        IconButton(onClick = { /* Handle button click */ }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.heart),
                contentDescription = "Email",
                modifier = Modifier.size(30.dp),
                tint = DarkCyan)

        }
    }
}


@Composable
fun MapViewComposable(
    loc: LatLng,
    zoomLevel: Float = 15f // Default zoom level for close-up of location
) {
    // Prepare the states for the camera position and marker
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(loc, zoomLevel)
    }
    val markerState = rememberMarkerState(position = loc)

    // Use the GoogleMap composable to render the map
    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp)),
        cameraPositionState = cameraPositionState
    ) {
        // Add a single marker at the given location
        Marker(
            state = markerState,
            title = "Marker in Location",
            snippet = "This is the selected location"
        )
    }
}


@Composable
fun SingleLocationGoogleMapView(
    modifier: Modifier = Modifier,
    location: LatLng,
    zoomLevel: Float = 15f, // Default zoom level for close-up of location
    onMapLoaded: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, zoomLevel)
    }

    val markerState = rememberMarkerState(position = location)

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        onMapLoaded = {
            onMapLoaded()
            cameraPositionState.position = CameraPosition.fromLatLngZoom(location, zoomLevel)
        },
        onPOIClick = {}
    ) {
        Marker(
            state = markerState,
            title = "Location",
            snippet = "Zoomed in here"
        )
        content()
    }
}


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
    val latitude = arguments?.getDouble("latitude") ?: 0.0
    val longitude = arguments?.getDouble("longitude") ?: 0.0
    val loc = LatLng(latitude, longitude)

    EventInfo(
        nav = NavigationActions(navController),
        title = title,
        date = date,
        time = time,
        organizer = organizer,
        rating = rating,
        image = painterResource(id = R.drawable.ic_launcher_background), // Image handling might need different approach
        description = description,
        loc = loc
    )
}





@Preview(showBackground = true)
@Composable
fun EventScreenPreview() {
    EventInfo(nav = NavigationActions(rememberNavController()), title = "Chess Tournament", organizer = "EPFL Chess Club", date = "TUE", time = "19:10", rating = 4.8, image = painterResource(id = R.drawable.chess_demo), description = "Howdy!\n\nAfter months of planning, La Dame Blanche is finally offering you a rapid tournament!\n\nJoin us on Saturday 23rd of March afternoon for 6 rounds of 12+3” games in the chill and cozy vibe of Satellite. Take your chance to have fun and play, and maybe win one of our many prizes\n\nOnly 50 spots available, with free entry!", loc = LatLng(46.519962, 6.633597))
}
