package com.github.se.gomeet.ui.mainscreens.events

import android.graphics.BitmapFactory
import android.net.Uri
import android.webkit.URLUtil
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import coil.compose.rememberAsyncImagePainter
import com.github.se.gomeet.R
import com.github.se.gomeet.model.TagsSelector
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.location.Location
import com.github.se.gomeet.ui.mainscreens.DateTimePicker
import com.github.se.gomeet.ui.mainscreens.LoadingText
import com.github.se.gomeet.ui.mainscreens.create.LocationField
import com.github.se.gomeet.ui.mainscreens.create.isValidPrice
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.viewmodel.EventViewModel
import java.io.InputStream
import kotlinx.coroutines.launch

@Composable
fun EditEvent(
    nav: NavigationActions,
    eventViewModel: EventViewModel,
    eventId: String,
    refreshEvent: (Event) -> Unit
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val screenHeight = LocalConfiguration.current.screenHeightDp.dp

  var event by remember { mutableStateOf<Event?>(null) }
  var profilePictureUrl by remember { mutableStateOf<String?>(null) }

  LaunchedEffect(eventId) {
    eventViewModel.getEvent(eventId)?.let {
      event = it
      profilePictureUrl = it.images.firstOrNull()
    }
  }

  if (event != null) {
    val titleState = remember { mutableStateOf(event!!.title) }
    val descriptionState = remember { mutableStateOf(event!!.description) }
    val locationState = remember { mutableStateOf(event!!.location.name) }
    var price by remember { mutableDoubleStateOf(event!!.price) }
    var priceText by remember { mutableStateOf(event!!.price.toString()) }
    val url = remember { mutableStateOf(event!!.url) }
    var urlValid by remember { mutableStateOf(true) }

    val pickedTime = remember { mutableStateOf(event!!.time) }
    val pickedDate = remember { mutableStateOf(event!!.date) }

    val selectedLocation: MutableState<Location?> = remember { mutableStateOf(event!!.location) }
    val tags = remember { mutableStateOf(event!!.tags) }
    val showPopup = remember { mutableStateOf(false) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            uri: Uri? ->
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

    Scaffold(
        topBar = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { nav.goBack() }) {
              Icon(
                  Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = "Go back",
                  tint = MaterialTheme.colorScheme.onBackground)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Done",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                modifier =
                    Modifier.padding(end = 15.dp).clickable {
                      urlValid = URLUtil.isValidUrl(url.value)
                      if (urlValid &&
                          titleState.value.isNotEmpty() &&
                          descriptionState.value.isNotEmpty() &&
                          locationState.value.isNotEmpty()) {
                        titleState.value = titleState.value.trimEnd()
                        descriptionState.value = descriptionState.value.trimEnd()
                        url.value = url.value.trimEnd()

                        val updatedEvent =
                            event!!.copy(
                                title = titleState.value,
                                description = descriptionState.value,
                                location = selectedLocation.value!!,
                                date = pickedDate.value,
                                time = pickedTime.value,
                                price = price,
                                url = url.value,
                                tags = tags.value)

                        coroutineScope.launch {
                          // Upload image and update event
                          val finalEvent =
                              if (imageUri != null) {
                                val imageUrl = eventViewModel.uploadImageAndGetUrl(imageUri!!)
                                val updatedImages = event!!.images.toMutableList()
                                if (updatedImages.isNotEmpty()) {
                                  updatedImages[0] = imageUrl
                                } else {
                                  updatedImages.add(imageUrl)
                                }
                                updatedEvent.copy(images = updatedImages)
                              } else {
                                updatedEvent
                              }
                          eventViewModel.editEvent(finalEvent)
                          refreshEvent(finalEvent)
                        }
                      }
                    })
          }
        },
        bottomBar = {
          BottomNavigationMenu(
              onTabSelect = { selectedTab ->
                nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
              },
              tabList = TOP_LEVEL_DESTINATIONS,
              selectedItem = Route.EVENTS)
        },
        content = { innerPadding ->
          Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier =
                    Modifier.padding(start = 15.dp, end = 15.dp)
                        .verticalScroll(rememberScrollState(0))
                        .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally) {
                  Image(
                      painter =
                          if (imageBitmap != null) {
                            androidx.compose.ui.graphics.painter.BitmapPainter(imageBitmap!!)
                          } else if (!profilePictureUrl.isNullOrEmpty()) {
                            rememberAsyncImagePainter(profilePictureUrl)
                          } else {
                            painterResource(id = R.drawable.gomeet_logo)
                          },
                      contentDescription = "Event picture",
                      modifier =
                          Modifier.padding(start = 15.dp, end = 15.dp, top = 30.dp, bottom = 15.dp)
                              .fillMaxWidth()
                              .height(200.dp)
                              .clickable { imagePickerLauncher.launch("image/*") }
                              .clip(RoundedCornerShape(12.dp))
                              .background(color = MaterialTheme.colorScheme.background)
                              .align(Alignment.CenterHorizontally)
                              .testTag("Event Picture"),
                      contentScale = ContentScale.Crop)

                  Spacer(modifier = Modifier.height(screenHeight / 80))

                  TextField(
                      value = titleState.value,
                      onValueChange = { newValue ->
                        if ((titleState.value.isNotEmpty() || newValue != " ") &&
                            titleState.value.length < 58) {
                          titleState.value = newValue
                        }
                      },
                      label = { Text("Title") },
                      singleLine = true,
                      modifier = Modifier.fillMaxWidth().padding(start = 15.dp, end = 15.dp),
                      colors = textFieldColors)

                  Spacer(modifier = Modifier.height(screenHeight / 80))

                  TextField(
                      value = descriptionState.value,
                      onValueChange = { newValue ->
                        if ((descriptionState.value.isNotEmpty() || newValue != " ")) {
                          descriptionState.value = newValue
                        }
                      },
                      label = { Text("Description") },
                      modifier = Modifier.fillMaxWidth().padding(start = 15.dp, end = 15.dp),
                      colors = textFieldColors)

                  Spacer(modifier = Modifier.height(screenHeight / 80))

                  LocationField(selectedLocation, locationState, eventViewModel)

                  Spacer(modifier = Modifier.height(screenHeight / 30))

                  DateTimePicker(pickedTime = pickedTime, pickedDate = pickedDate)

                  Spacer(modifier = Modifier.height(screenHeight / 80))

                  TextField(
                      value = priceText,
                      onValueChange = { newVal ->
                        if (newVal.isEmpty() || isValidPrice(newVal)) {
                          priceText = newVal
                          newVal.toDoubleOrNull()?.let { price = it }
                        }
                      },
                      label = { Text("Price") },
                      singleLine = true,
                      modifier = Modifier.fillMaxWidth().padding(start = 15.dp, end = 15.dp),
                      colors = textFieldColors)

                  Spacer(modifier = Modifier.height(screenHeight / 80))

                  TextField(
                      value = url.value,
                      onValueChange = { newVal -> url.value = newVal },
                      label = { Text("Link") },
                      singleLine = true,
                      modifier = Modifier.fillMaxWidth().padding(start = 15.dp, end = 15.dp),
                      colors = textFieldColors)

                  Spacer(modifier = Modifier.height(screenHeight / 80))

                  Row(
                      modifier = Modifier.fillMaxWidth().padding(start = 15.dp, top = 10.dp),
                      verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Edit Tags",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontStyle = FontStyle.Normal,
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily.Default,
                            textAlign = TextAlign.Start,
                            style = MaterialTheme.typography.bodyMedium)
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            null,
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier =
                                Modifier.clickable { showPopup.value = true }
                                    .testTag("EditTagsButton"))
                      }
                }
          }

          if (showPopup.value) {
            Popup(
                alignment = Alignment.Center,
                onDismissRequest = { showPopup.value = !showPopup.value }) {
                  TagsSelector("Edit Tags", tags) { showPopup.value = false }
                }
          }
        })
  } else {
    LoadingText()
  }
}
