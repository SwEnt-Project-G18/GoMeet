package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.gomeet.R
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.ui.theme.Grey

@Composable
fun ProfileEventsList(title: String) {
  Spacer(modifier = Modifier.height(10.dp))
  Column {
    Row(Modifier.padding(start = 15.dp, end = 0.dp, top = 0.dp, bottom = 0.dp).fillMaxWidth()) {
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
          modifier = Modifier.width(104.dp).height(21.dp).align(Alignment.Bottom))
      Text(text = "View all", color = Grey, modifier = Modifier.align(Alignment.Bottom))
    }
    Spacer(modifier = Modifier.height(10.dp))
    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(start = 15.dp, end = 15.dp),
        modifier = Modifier.heightIn(min = 56.dp)) {
          items(10) {
            Column(modifier = Modifier.width(170.dp)) {
              Image(
                  painter = painterResource(id = R.drawable.chess_demo),
                  contentDescription = "Event 1",
                  contentScale = ContentScale.Crop,
                  modifier =
                      Modifier.fillMaxWidth()
                          .aspectRatio(3f / 1.75f)
                          .clip(RoundedCornerShape(size = 10.dp)))
              Text(text = "Chess Tournament", color = DarkCyan)
              Text(text = "11.04.2024 - 21:00", color = Grey)
            }
          }
        }
  }
}
