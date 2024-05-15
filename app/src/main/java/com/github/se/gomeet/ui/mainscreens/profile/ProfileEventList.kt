package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Composable function for the ProfileEventsList screen.
 *
 * @param title The title of the event.
 */
@Composable
fun ProfileEventsList(
    title: String,
    listState: LazyListState,
    eventList: MutableList<Event>,
    nav: NavigationActions
) {
  Column(Modifier.fillMaxWidth().padding(start = 15.dp)) {
    Row(Modifier.testTag("EventsListHeader"), verticalAlignment = Alignment.CenterVertically) {
      Text(
          text = title,
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.W400))
      Spacer(modifier = Modifier.width(10.dp))
      ClickableText(
          style =
              MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary),
          onClick = { // TODO: Go to List of Events
          },
          text = AnnotatedString(text = "View All >"))
    }

    Spacer(modifier = Modifier.height(5.dp))

    LazyRow(
        state = listState,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(end = 15.dp),
        modifier = Modifier.heightIn(min = 56.dp)) {
          itemsIndexed(eventList) { _, event ->
            Column(
                modifier =
                    Modifier.width(170.dp)
                        .clickable {
                          val eventDate =
                              Date.from(event.date.atStartOfDay(ZoneId.systemDefault()).toInstant())
                          val currentDate = Calendar.getInstance()
                          val startOfWeek = currentDate.clone() as Calendar
                          startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.firstDayOfWeek)
                          val endOfWeek = startOfWeek.clone() as Calendar
                          endOfWeek.add(Calendar.DAY_OF_WEEK, 6)

                          val eventCalendar = Calendar.getInstance().apply { time = eventDate }

                          val isThisWeek =
                              eventCalendar.after(currentDate) && eventCalendar.before(endOfWeek)
                          val isToday =
                              currentDate.get(Calendar.YEAR) == eventCalendar.get(Calendar.YEAR) &&
                                  currentDate.get(Calendar.DAY_OF_YEAR) ==
                                      eventCalendar.get(Calendar.DAY_OF_YEAR)

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

                          nav.navigateToEventInfo(
                              eventId = event.eventID,
                              title = event.title,
                              date = dayString,
                              time = timeString,
                              description = event.description,
                              organizer = event.creator,
                              loc = LatLng(event.location.latitude, event.location.longitude),
                              rating = 0.0 // TODO: replace with actual rating
                              // TODO: add image
                              )
                        }
                        .testTag("EventListItem")) {
                  Image(
                      painter =
                          if (event.images.isNotEmpty()) {
                            rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current)
                                    .data(data = event.images[0])
                                    .apply(
                                        block =
                                            fun ImageRequest.Builder.() {
                                              crossfade(true)
                                              placeholder(R.drawable.gomeet_logo)
                                            })
                                    .build())
                          } else {
                            painterResource(id = R.drawable.gomeet_logo)
                          },
                      contentDescription = event.description,
                      contentScale = ContentScale.Crop,
                      modifier =
                          Modifier.fillMaxWidth()
                              .aspectRatio(3f / 1.75f)
                              .clip(RoundedCornerShape(size = 10.dp)))
                  Spacer(modifier = Modifier.height(2.dp))

                  Text(text = event.title, style = MaterialTheme.typography.bodyLarge)
                  Text(
                      text = event.date.toString(),
                      style =
                          MaterialTheme.typography.bodyLarge.copy(
                              color = MaterialTheme.colorScheme.primary))
                }
          }
        }
  }
}
