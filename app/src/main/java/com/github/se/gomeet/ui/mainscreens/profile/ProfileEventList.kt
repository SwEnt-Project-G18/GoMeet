package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.ui.mainscreens.EventDateToString
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.ui.theme.Grey
import java.time.ZoneId
import java.util.Date

/**
 * Composable function for the ProfileEventsList screen.
 *
 * @param title The title of the event.
 */
@Composable
fun ProfileEventsList(title: String, listState : LazyListState, eventList: MutableList<Event>) {
  Spacer(modifier = Modifier.height(10.dp))
    Column {
        Row(
            Modifier.padding(start = 15.dp, end = 0.dp, top = 0.dp, bottom = 0.dp)
                .fillMaxWidth()
                .testTag("EventsListHeader")
        ) {
            Text(
                text = title,
                style =
                TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(R.font.roboto)),
                    fontWeight = FontWeight(1000),
                    color = DarkCyan,
                    textAlign = TextAlign.Start,
                    letterSpacing = 0.5.sp,
                ),
                modifier = Modifier.width(104.dp).height(21.dp).align(Alignment.Bottom)
            )
            Text(text = "View all", color = Grey, modifier = Modifier.align(Alignment.Bottom))
        }
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(
            state = listState,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(start = 15.dp, end = 15.dp),
            modifier = Modifier.heightIn(min = 56.dp).testTag("EventsListItems")
        ) {
            itemsIndexed(eventList) { _, event ->
                Column(modifier = Modifier.width(170.dp)) {
                    Image(
                        painter = if (event.images.isNotEmpty()) {
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
                            .clip(RoundedCornerShape(size = 10.dp))
                    )
                    Text(text = event.title, color = DarkCyan)
                    Text(text = event.date.toString(), color = Grey)
                }
            }
        }
    }
}
