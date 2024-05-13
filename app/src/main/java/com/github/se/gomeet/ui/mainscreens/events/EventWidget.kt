import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.location.Location
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * A composable function that displays detailed information about an event in a card layout. This
 * widget is designed to present event details including the name, description, date, and an image
 * if available. The card is interactive and can be tapped to navigate to further event details.
 *
 * @param userName The name of the event creator.
 * @param eventId The unique identifier for the event.
 * @param eventName The name of the event.
 * @param eventDescription A short description of the event.
 * @param eventDate The date and time at which the event is scheduled.
 * @param eventPicture A painter object that handles the rendering of the event's image.
 * @param verified A boolean indicating whether the event or the creator is verified. This could
 *   influence the visual representation.
 * @param nav NavigationActions object to handle navigation events such as tapping on the event
 */
@Composable
fun EventWidget(
    userName: String,
    eventId: String,
    eventName: String,
    eventDescription: String,
    eventDate: Date,
    eventPicture: Painter,
    eventLocation: Location,
    verified: Boolean,
    nav: NavigationActions,
) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val density = LocalDensity.current

    val smallTextSize = with(density) { screenWidth.toPx() / 85 }
    val bigTextSize = with(density) { screenWidth.toPx() / 60 }

    val currentDate = Calendar.getInstance()
    val startOfWeek = currentDate.clone() as Calendar
    startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.firstDayOfWeek)
    val endOfWeek = startOfWeek.clone() as Calendar
    endOfWeek.add(Calendar.DAY_OF_WEEK, 6)

    val eventCalendar = Calendar.getInstance().apply { time = eventDate }

    val isThisWeek = eventCalendar.after(currentDate) && eventCalendar.before(endOfWeek)
    val isToday =
        currentDate.get(Calendar.YEAR) == eventCalendar.get(Calendar.YEAR) &&
                currentDate.get(Calendar.DAY_OF_YEAR) == eventCalendar.get(Calendar.DAY_OF_YEAR)

    val dayFormat =
        if (isThisWeek) {
            SimpleDateFormat("EEEE", Locale.getDefault())
        } else {
            SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        }

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val dayString =
        if (isToday) {
            "Today"
        } else {
            dayFormat.format(eventDate)
        }
    val timeString = timeFormat.format(eventDate)

    Card(
        modifier =
        Modifier.fillMaxWidth()
            .testTag("Card")
            .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 5.dp)
            .clickable {
                nav.navigateToEventInfo(
                    eventId = eventId,
                    title = eventName,
                    date = dayString,
                    time = timeString,
                    description = eventDescription,
                    organizer = userName,
                    loc = LatLng(eventLocation.latitude, eventLocation.longitude),
                    rating = 0.0 // TODO: replace with actual rating
                    // TODO: add image
                )
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround) {
            Column(
                modifier = Modifier.weight(4f).padding(15.dp),
                horizontalAlignment = Alignment.Start, // Align text horizontally to center
                verticalArrangement = Arrangement.Center) {
                Text(
                    text = eventName,
                    style =
                    TextStyle(
                        fontSize = bigTextSize.sp,
                        lineHeight = 20.sp,
                        fontFamily = FontFamily(Font(R.font.roboto)),
                        fontWeight = FontWeight(700),
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = 0.25.sp,
                    ),
                    modifier = Modifier.testTag("EventName"))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center) {
                    var username by remember { mutableStateOf<String?>("Loading...") }
                    LaunchedEffect(userName) {
                        username =
                            UserViewModel(UserRepository(Firebase.firestore))
                                .getUsername(userName)
                    }

                    username?.let {
                        Text(
                            it,
                            style =
                            TextStyle(
                                fontSize = smallTextSize.sp,
                                lineHeight = 24.sp,
                                fontFamily = FontFamily(Font(R.font.roboto)),
                                fontWeight = FontWeight(700),
                                color = MaterialTheme.colorScheme.onBackground,
                                letterSpacing = 0.15.sp,
                            ),
                            modifier = Modifier.padding(top = 5.dp).testTag("UserName"))
                    }
                    if (verified) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(5.dp).size(smallTextSize.dp * (1.4f))) {
                            Image(
                                painter = painterResource(id = R.drawable.verified),
                                contentDescription = "Verified",
                            )
                        }
                    }
                }

                Text(
                    dayString + " - " + timeString,
                    style =
                    TextStyle(
                        fontSize = smallTextSize.sp,
                        lineHeight = 20.sp,
                        fontFamily = FontFamily(Font(R.font.roboto)),
                        fontWeight = FontWeight(700),
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = 0.25.sp,
                    ),
                    modifier = Modifier.testTag("EventDate"))
            }
            Image(
                painter = eventPicture,
                contentDescription = "Event Picture",
                modifier =
                Modifier.weight(3f)
                    .fillMaxHeight()
                    .aspectRatio(3f / 1.75f)
                    .clipToBounds()
                    .padding(0.dp) // Clip the image if it overflows its bounds
                    .testTag("EventPicture"),
                contentScale = ContentScale.Crop, // Crop the image to fit the aspect ratio
            )
        }
    }
}
