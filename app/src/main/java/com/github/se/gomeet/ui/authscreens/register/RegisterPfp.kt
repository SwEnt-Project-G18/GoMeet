package com.github.se.gomeet.ui.authscreens.register

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.se.gomeet.R

/**
 * This composable function handles the upload and display of the user's profile picture. It allows
 * the user to select an image from their device, displays the selected image, and proceeds with the
 * registration.
 *
 * @param callback Function to be called with the URI of the selected profile picture.
 * @param name The first name of the user, used to personalize the greeting message.
 */
@Composable
fun RegisterPfp(callback: (String) -> Unit, name: String) {
  val screenHeight = LocalConfiguration.current.screenHeightDp.dp
  var pfpUri by remember { mutableStateOf<Uri?>(null) }
  var pfp by remember { mutableStateOf("") }

  val imagePickerLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.GetContent(),
          onResult = { uri: Uri? ->
            pfpUri = uri
            pfp = uri.toString()
          })

  Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
        text = if (pfp.isEmpty()) "Hi $name!\nWhat about a Profile Picture ?" else "You look good!",
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center)

    Spacer(modifier = Modifier.size(screenHeight / 20))

    Image(
        painter =
            if (pfpUri != null) {
              val imageStream = LocalContext.current.contentResolver.openInputStream(pfpUri!!)
              val drawable = BitmapFactory.decodeStream(imageStream).asImageBitmap()
              remember { BitmapPainter(drawable) }
            } else {
              painterResource(id = R.drawable.gomeet_logo)
            },
        contentDescription = "Profile Picture",
        modifier =
            Modifier.size(screenHeight / 5).padding(8.dp).clickable {
              imagePickerLauncher.launch("image/*")
            },
        contentScale = ContentScale.Crop)

    Spacer(modifier = Modifier.size(screenHeight / 15))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
      LinearProgressIndicator(
          modifier = Modifier.padding(top = 20.dp, end = 25.dp),
          progress = { 0.8f },
          color = MaterialTheme.colorScheme.tertiary,
          trackColor = Color.LightGray,
          strokeCap = ProgressIndicatorDefaults.CircularIndeterminateStrokeCap)
      IconButton(
          modifier = Modifier.padding(bottom = 2.5.dp, end = 3.dp).size(screenHeight / 19),
          colors = IconButtonDefaults.outlinedIconButtonColors(),
          onClick = { callback(pfp) }) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(60.dp))
          }
    }
  }
}
