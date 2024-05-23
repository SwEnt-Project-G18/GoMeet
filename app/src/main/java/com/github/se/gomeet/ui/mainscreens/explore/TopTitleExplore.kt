package com.github.se.gomeet.ui.mainscreens.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
internal fun TopTitle(forColumn: Boolean, alpha: Float) {
  Column(
      modifier =
          Modifier.padding(
                  top = if (forColumn) 34.dp else 12.dp,
                  start = 10.dp) // status bar 24dp in material guidance
              .alpha(alpha = alpha)
              .fillMaxWidth()) {
        Box(
            modifier =
                Modifier.size(width = 48.dp, height = 3.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .background(color = Color.LightGray)
                    .align(alignment = Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(LocalConfiguration.current.screenHeightDp.dp / 80))
        Text(
            text = "Trending Around You",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(LocalConfiguration.current.screenHeightDp.dp / 80))
      }
}
