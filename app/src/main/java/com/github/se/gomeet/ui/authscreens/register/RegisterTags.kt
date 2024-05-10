package com.github.se.gomeet.ui.authscreens.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.github.se.gomeet.ui.theme.DarkGrey

@Composable
fun RegisterTags(callback: () -> Unit) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        LinearProgressIndicator(
            modifier = Modifier.padding(top = 20.dp, end = 25.dp),
            progress = { 0.6f },
            color = DarkGrey,
            trackColor = Color.LightGray,
            strokeCap = ProgressIndicatorDefaults.CircularIndeterminateStrokeCap
        )
        IconButton(
            modifier = Modifier
                .padding(bottom = 2.5.dp, end = 3.dp)
                .size(screenHeight / 19),
            colors = IconButtonDefaults.outlinedIconButtonColors(),
            onClick = {
                callback()
            }) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next",
                tint = DarkGrey,
                modifier = Modifier.size(60.dp)
            )
        }
    }
}