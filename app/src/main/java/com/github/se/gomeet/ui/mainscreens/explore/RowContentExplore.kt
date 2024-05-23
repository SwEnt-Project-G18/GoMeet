package com.github.se.gomeet.ui.mainscreens.explore

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.github.se.gomeet.model.event.eventMomentToString
import com.github.se.gomeet.model.event.getEventDateString
import com.github.se.gomeet.model.event.getEventTimeString
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.google.android.gms.maps.model.LatLng

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContentInRow(
    listState: LazyListState,
    eventList: MutableState<List<Event>>,
    nav: NavigationActions
) {

    val events = eventList.value
        Column (horizontalAlignment = Alignment.CenterHorizontally) {
            val configuration = LocalConfiguration.current
            val screenHeight = configuration.screenHeightDp.dp
            val screenWidth = configuration.screenWidthDp
            LazyRow(horizontalArrangement = Arrangement.Start, modifier = Modifier.padding(start = 10.dp, bottom = 10.dp), state = listState) {
                items(events) { event ->
                    Column(modifier = Modifier
                        .padding(end = 10.dp)
                        .shadow(elevation = 10.dp, shape = RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(10.dp))
                        .fillMaxWidth()) {
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            modifier =
                            Modifier
                                .size(width = (screenWidth/1.35).dp, height = screenHeight / 6)
                                .padding(10.dp)
                                .clickable {
                                nav.navigateToEventInfo(
                                    eventId = event.eventID,
                                    title = event.title,
                                    date = getEventDateString(event.date),
                                    time = getEventTimeString(event.time),
                                    description = event.description,
                                    organizer = event.creator,
                                    loc = LatLng(event.location.latitude, event.location.longitude),
                                    rating = 0.0 // TODO: replace with actual rating
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
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)))
                        }
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.tertiary)
                            Text(
                                text = eventMomentToString(event.date, event.time),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.tertiary)
                        }
                    }
                }
            }
        }
}