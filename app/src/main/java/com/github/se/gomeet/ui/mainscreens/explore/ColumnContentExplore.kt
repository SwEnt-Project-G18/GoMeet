package com.github.se.gomeet.ui.mainscreens.explore

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BackdropScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ContentInColumn(
    backdropState: BackdropScaffoldState,
    halfHeightPx: Float,
    listState: LazyListState,
    eventList: MutableState<List<Event>>,
    nav: NavigationActions,
    currentUID: String
) {
  val offset by backdropState.offset

  val columnAlpha = ((halfHeightPx - offset) / halfHeightPx).coerceIn(0f..1f)
  val events = eventList.value
  if (columnAlpha > 0) {
    Column {
      TopTitle(forColumn = true, alpha = columnAlpha)

      LazyColumn(
          modifier = Modifier.alpha(columnAlpha),
          state = listState,
          horizontalAlignment = Alignment.CenterHorizontally) {
            itemsIndexed(events) { _, event ->
              Column {
                val configuration = LocalConfiguration.current
                val screenWidth = configuration.screenWidthDp.dp

                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier =
                        Modifier.width(screenWidth / 0.6f).aspectRatio(3f / 1.75f).clickable {
                          nav.navigateToEventInfo(
                              eventId = event.eventID,
                              title = event.title,
                              date = event.getDateString(),
                              time = event.getTimeString(),
                              description = event.description,
                              organizer = event.creator,
                              loc = LatLng(event.location.latitude, event.location.longitude),
                              rating = event.ratings[currentUID] ?: 0,
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
                Spacer(Modifier.height(8.dp))
                Column(modifier = Modifier.padding(8.dp)) {
                  Text(
                      text = event.title,
                      style = MaterialTheme.typography.bodyLarge,
                      color = MaterialTheme.colorScheme.tertiary)
                  Text(
                      text = event.momentToString(),
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.tertiary)
                }
              }
              HorizontalDivider(
                  color = Color.Gray, modifier = Modifier.padding(top = 0.dp, bottom = 16.dp))
            }
          }
    }
  }
}
