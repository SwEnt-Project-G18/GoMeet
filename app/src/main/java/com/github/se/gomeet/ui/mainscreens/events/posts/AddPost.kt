package com.github.se.gomeet.ui.mainscreens.events.posts

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Post
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.mainscreens.profile.ProfileImage
import com.github.se.gomeet.ui.theme.White
import java.io.InputStream
import java.time.LocalDate
import java.time.LocalTime

@SuppressLint("SuspiciousIndentation")
@Composable
fun AddPost(user: GoMeetUser, callbackCancel: () -> Unit, callbackPost: (Post) -> Unit) {
  val screenHeight = LocalConfiguration.current.screenHeightDp.dp
  val screenWidth = LocalConfiguration.current.screenWidthDp.dp

  var titleState by remember { mutableStateOf("") }
  var contentState by remember { mutableStateOf("") }
  var url by remember { mutableStateOf("") }

  val context = LocalContext.current

  var imageUri by remember { mutableStateOf<Uri?>(null) }
  var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
  val imagePickerLauncher =
      rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri?
        ->
        imageUri = uri
        uri?.let { uriNonNull ->
          val inputStream: InputStream? =
              try {
                context.contentResolver.openInputStream(uriNonNull)
              } catch (e: Exception) {
                e.printStackTrace()
                null
              }
          inputStream?.let {
            val bitmap = BitmapFactory.decodeStream(it)
            imageBitmap = bitmap.asImageBitmap()
            url = uriNonNull.toString()
          }
        }
      }

  val textFieldColors =
      TextFieldDefaults.colors(
          focusedTextColor = MaterialTheme.colorScheme.onBackground,
          unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
          unfocusedContainerColor = Color.Transparent,
          focusedContainerColor = Color.Transparent,
          cursorColor = MaterialTheme.colorScheme.outlineVariant,
          focusedLabelColor = MaterialTheme.colorScheme.tertiary,
          unfocusedLabelColor = MaterialTheme.colorScheme.tertiary,
          focusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
          unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary)

  Box(
      modifier =
      Modifier
          .border(
              BorderStroke(4.dp, MaterialTheme.colorScheme.primaryContainer),
              RoundedCornerShape(10.dp)
          )
          .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(10.dp))) {
      IconButton(onClick = { callbackCancel() }) {
          Icon(Icons.Filled.Close, contentDescription = "Cancel")
      }
        Column(modifier = Modifier.padding(top = 10.dp)) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "Add a Post",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold))

            Spacer(modifier = Modifier.height(screenHeight / 40))

            Row(
              horizontalArrangement = Arrangement.Start,
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                  .fillMaxWidth()
                  .padding(start = 20.dp)
                  .testTag("UserInfo")) {
                Box(modifier = Modifier.padding(end = 5.dp)) {
                  ProfileImage(
                      userId = user.uid,
                      modifier = Modifier.testTag("Profile Picture"),
                      size = 50.dp)
                }
                Column(horizontalAlignment = Alignment.Start) {
                  Text(
                      (user.firstName + " " + user.lastName),
                      textAlign = TextAlign.Center,
                      style = MaterialTheme.typography.bodyLarge)

                  Text(text = "@" + (user.username), style = MaterialTheme.typography.bodySmall)
                }
              }

            OutlinedTextField(
                value = contentState,
                onValueChange = { newVal -> contentState = newVal },
                placeholder = { Text("Share the latest updates about this event...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.tertiary,
                    focusedLabelColor = MaterialTheme.colorScheme.tertiary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

          if (url.isNotEmpty()) {
            Image(
                painter =
                    if (imageBitmap != null) {
                      BitmapPainter(imageBitmap!!)
                    } else if (url.isNotEmpty()) {
                      rememberAsyncImagePainter(url)
                    } else {
                      painterResource(id = R.drawable.gomeet_logo)
                    },
                contentDescription = "Post Image",
                contentScale = ContentScale.Crop,
                modifier =
                Modifier
                    .padding(horizontal = 20.dp)
                    .aspectRatio(2f)
                    .clickable { imagePickerLauncher.launch("image/*") }
                    .clip(RoundedCornerShape(20.dp)))
          }

          Row(
              modifier = Modifier
                  .fillMaxWidth()
                  .padding(20.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween) {
              if (url.isNotEmpty()) {
                  IconButton(
                      modifier = Modifier.size(30.dp),
                      onClick = { url = "" }) {
                      Icon(
                          ImageVector.vectorResource(R.drawable.image_delete_icon),
                          contentDescription = "Add Image",
                          tint = MaterialTheme.colorScheme.tertiary)
                  }
              }else {
                  IconButton(
                      modifier = Modifier
                          .size(30.dp),
                      onClick = { imagePickerLauncher.launch("image/*") }) {
                      Icon(
                          ImageVector.vectorResource(R.drawable.image_add_icon),
                          contentDescription = "Add Image",
                          tint = MaterialTheme.colorScheme.tertiary)
                  }
              }
                Button(
                    modifier = Modifier.width(screenWidth / 3),
                    shape = RoundedCornerShape(10.dp),
                    onClick = {
                      callbackPost(
                          Post(
                              user.uid,
                              titleState,
                              contentState,
                              LocalDate.now(),
                              LocalTime.now(),
                              url,
                              emptyList(),
                              emptyList()))
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.outlineVariant)) {
                      Text(text = "Post", color = White)
                    }
              }
        }
      }
}
