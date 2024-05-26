import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
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
import coil.compose.rememberAsyncImagePainter
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.android.gms.maps.model.LatLng

private const val TAG = "EventWidget"

/**
 * A composable function that displays detailed information about an event in a card layout. This
 * widget is designed to present event details including the name, description, date, and an image
 * if available. The card is interactive and can be tapped to navigate to further event details.
 *
 * @param event The event to display.
 * @param verified True if the event organiser is verified, false otherwise.
 * @param nav The navigation actions.
 * @param userVM The user view model.
 */
@Composable
fun EventWidget(event: Event, verified: Boolean, nav: NavigationActions, userVM: UserViewModel) {

  val configuration = LocalConfiguration.current
  val screenWidth = configuration.screenWidthDp.dp
  val density = LocalDensity.current

  val smallTextSize = with(density) { screenWidth.toPx() / 85 }
  val bigTextSize = with(density) { screenWidth.toPx() / 60 }

  val dayString = event.getDateString()
  val timeString = event.getTimeString()

  val painter: Painter =
      if (event.images.isNotEmpty()) {
        rememberAsyncImagePainter(event.images[0])
      } else {
        painterResource(id = R.drawable.gomeet_logo)
      }

  Card(
      modifier =
          Modifier.fillMaxWidth()
              .testTag("Card")
              .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 5.dp)
              .clickable {
                nav.navigateToEventInfo(
                    eventId = event.eventID,
                    title = event.title,
                    date = dayString,
                    time = timeString,
                    description = event.description,
                    organizer = event.creator,
                    loc = LatLng(event.location.latitude, event.location.longitude),
                    rating = event.ratings[userVM.currentUID!!] ?: 0,
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
                    maxLines = 1,
                    text = event.title,
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
                      LaunchedEffect(event.creator) { username = userVM.getUsername(event.creator) }

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
                    "$dayString - $timeString",
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
              painter = painter,
              contentDescription = "Event Picture",
              modifier =
                  Modifier.weight(3f)
                      .clip(RectangleShape)
                      .aspectRatio(3f / 1.75f)
                      .testTag("EventPicture"),
              contentScale = ContentScale.Crop, // Crop the image to fit the aspect ratio
          )
        }
  }
}
