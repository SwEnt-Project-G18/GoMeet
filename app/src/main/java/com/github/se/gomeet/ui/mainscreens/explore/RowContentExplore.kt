package com.github.se.gomeet.ui.mainscreens.explore

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.google.android.gms.maps.model.LatLng

@SuppressLint("SuspiciousIndentation")
@Composable
fun ContentInRow(
    listState: LazyListState,
    eventList: MutableState<List<Event>>,
    nav: NavigationActions,
) {

  val events = eventList.value
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp
    LazyRow(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.padding(bottom = 10.dp),
        state = listState) {
          items(events) { event ->
            val columnShape =
                RoundedCornerShape(
                    topStart = 24.dp, topEnd = 24.dp, bottomStart = 10.dp, bottomEnd = 10.dp)
            Column(
                modifier =
                    Modifier.padding(start = 10.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, columnShape)
                        .fillMaxWidth()) {
                  Card(
                      shape = RoundedCornerShape(16.dp),
                      modifier =
                          Modifier.size(width = (screenWidth / 1.35).dp, height = screenHeight / 6)
                              .padding(10.dp)
                              .clickable {
                                nav.navigateToEventInfo(
                                    eventId = event.eventID,
                                    title = event.title,
                                    date = event.getDateString(),
                                    time = event.getTimeString(),
                                    description = event.description,
                                    organizer = event.creator,
                                    loc = LatLng(event.location.latitude, event.location.longitude),
                                    rating = 0L // TODO: replace with actual rating
                                    // TODO: add image
                                    )
                              }) {
                        val painter: Painter =
                            if (event.images.isNotEmpty()) {
                              rememberAsyncImagePainter(
                                  ImageRequest.Builder(LocalContext.current)
                                      .data(data = event.images[0])
                                      .apply {
                                        crossfade(true)
                                        placeholder(R.drawable.gomeet_logo)
                                      }
                                      .build())
                            } else {
                              painterResource(id = R.drawable.gomeet_logo)
                            }
                        Image(
                            painter = painter,
                            contentDescription = "Event Image",
                            alignment = Alignment.Center,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().aspectRatio(3f / 1.75f))
                      }

                  Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp)) {
                    Text(
                        text =
                            if (event.title.length > 37) event.title.take(33) + "...."
                            else event.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground)
                    Text(
                        text = event.momentToString(),
                        style = MaterialTheme.typography.bodyMedium, // Smaller text style
                        color = MaterialTheme.colorScheme.onBackground)
                  }
                }
          }
        }
  }
}
