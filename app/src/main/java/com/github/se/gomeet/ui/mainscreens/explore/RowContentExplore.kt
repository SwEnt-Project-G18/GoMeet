package com.github.se.gomeet.ui.mainscreens.explore

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun ContentInRow(
    event: MutableState<Event?>,
    nav: NavigationActions
) {
     val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        val screenWidth = configuration.screenWidthDp.dp
    if (event.value != null) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(10.dp))
                .fillMaxWidth()
        ) {
            Card(
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .size(width = screenWidth, height = screenHeight / 4)
                    .padding(10.dp)
                    .clickable {
                        nav.navigateToEventInfo(
                            eventId = event.value!!.eventID,
                            title = event.value!!.title,
                            date = getEventDateString(event.value!!.date),
                            time = getEventTimeString(event.value!!.time),
                            description = event.value!!.description,
                            organizer = event.value!!.creator,
                            loc = LatLng(event.value!!.location.latitude, event.value!!.location.longitude),
                            rating = 0.0 // TODO: replace with actual rating
                            // TODO: add image
                        )
                    }
            ) {
                val painter: Painter =
                    if (event.value!!.images.isNotEmpty()) {
                        rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(data = event.value!!.images[0])
                                .apply {
                                    crossfade(true)
                                    placeholder(R.drawable.gomeet_logo)
                                }
                                .build()
                        )
                    } else {
                        painterResource(id = R.drawable.gomeet_logo)
                    }
                Image(
                    painter = painter,
                    contentDescription = "Event Image",
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                )
            }
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = event.value!!.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = eventMomentToString(event.value!!.date, event.value!!.time),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}