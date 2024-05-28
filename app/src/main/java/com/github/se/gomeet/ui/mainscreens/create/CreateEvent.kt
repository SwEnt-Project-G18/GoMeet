package com.github.se.gomeet.ui.mainscreens.create

import android.graphics.BitmapFactory
import android.webkit.URLUtil
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewModelScope
import com.github.se.gomeet.R
import com.github.se.gomeet.model.TagsSelector
import com.github.se.gomeet.model.event.location.Location
import com.github.se.gomeet.model.repository.EventRepository
import com.github.se.gomeet.ui.mainscreens.DateTimePicker
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.viewmodel.EventCreationViewModel
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import java.io.InputStream
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val NUMBER_OF_SUGGESTIONS = 3

/**
 * Composable function for the CreateEvent screen.
 *
 * @param nav The navigation actions.
 * @param eventViewModel The event view model.
 * @param isPrivate The boolean value to determine if the event is private or not.
 * @param userViewModel The user view model.
 * @param eventCreationViewModel The event creation view model.
 */
@Composable
fun CreateEvent(
    nav: NavigationActions,
    eventViewModel: EventViewModel,
    isPrivate: Boolean,
    userViewModel: UserViewModel,
    eventCreationViewModel: EventCreationViewModel
) {
  val screenHeight = LocalConfiguration.current.screenHeightDp.dp
  val screenWidth = LocalConfiguration.current.screenWidthDp.dp

  val uid = EventRepository.getNewId()

  val titleState = eventCreationViewModel.title
  val descriptionState = eventCreationViewModel.description
  val locationState = eventCreationViewModel.location
  val price by remember { eventCreationViewModel.price }
  var priceText by remember { mutableStateOf(price.toString()) }
  val urlState = eventCreationViewModel.url
  val pickedTime = eventCreationViewModel.pickedTime
  val pickedDate = eventCreationViewModel.pickedDate
  var invitedParticipants = eventCreationViewModel.invitedParticipants
  val tagsState = eventCreationViewModel.tags
  val imageUriState = eventCreationViewModel.imageUri
  val imageBitmapState = eventCreationViewModel.imageBitmap

  var urlValid by remember { mutableStateOf(true) }

  val customPins = remember { CustomPins() }

  val context = LocalContext.current

  val imagePickerLauncher =
      rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
          uri: android.net.Uri? ->
        imageUriState.value = uri
        uri?.let { uriNonNull ->
          val inputStream: InputStream? =
              try {
                nav.navController.context.contentResolver.openInputStream(uriNonNull)
              } catch (e: Exception) {
                e.printStackTrace()
                null
              }
          inputStream?.let {
            val bitmap = BitmapFactory.decodeStream(it)
            imageBitmapState.value = bitmap.asImageBitmap()
          }
        }
      }

  val selectedLocation: MutableState<Location?> = remember { mutableStateOf(null) }
  val showPopup = remember { mutableStateOf(false) }
  var tagsButtonText by remember { mutableStateOf("Add Tags") }

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
        Column(modifier = Modifier.padding(bottom = screenHeight / 90)) {
          TopAppBar(
              modifier = Modifier.testTag("TopBar"),
              backgroundColor = MaterialTheme.colorScheme.background,
              elevation = 0.dp,
              title = {
                // Empty title since we're placing our own components
              },
              navigationIcon = {
                IconButton(
                    onClick = {
                      invitedParticipants = mutableStateListOf()
                      nav.goBack()
                    }) {
                      Icon(
                          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                          contentDescription = "Back",
                          tint = MaterialTheme.colorScheme.onBackground)
                    }
              })

          Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(start = 18.dp)) {
                Text(
                    text = "Create",
                    color = MaterialTheme.colorScheme.onBackground,
                    style =
                        MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold))
              }
        }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { selectedTab ->
              nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
            },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = Route.CREATE)
      }) { innerPadding ->
        Column(
            Modifier.padding(innerPadding).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally) {
              TextField(
                  value = titleState.value,
                  singleLine = true,
                  onValueChange = { newVal ->
                    if ((titleState.value.isNotEmpty() || newVal != " ") &&
                        titleState.value.length < 58) {
                      titleState.value = newVal
                    }
                  },
                  label = { Text("Title") },
                  placeholder = { Text("Name the event") },
                  colors = textFieldColors,
                  modifier = Modifier.fillMaxWidth().padding(start = 15.dp, end = 15.dp))

              TextField(
                  value = descriptionState.value,
                  onValueChange = { newVal ->
                    if ((descriptionState.value.isNotEmpty() || newVal != " ")) {
                      descriptionState.value = newVal
                    }
                  },
                  label = { Text("Description") },
                  placeholder = { Text("Describe the task") },
                  colors = textFieldColors,
                  modifier = Modifier.fillMaxWidth().padding(start = 15.dp, end = 15.dp))

              LocationField(selectedLocation, locationState, eventViewModel)

              Spacer(modifier = Modifier.height(screenHeight / 30))

              DateTimePicker(pickedTime = pickedTime, pickedDate = pickedDate)

              TextField(
                  value = priceText,
                  onValueChange = { newVal ->
                    if (newVal.isEmpty() || isValidPrice(newVal)) {
                      priceText = newVal
                      newVal.toDoubleOrNull()?.let { eventCreationViewModel.price.value = it }
                    }
                  },
                  label = { Text("Price") },
                  placeholder = { Text("Enter a price") },
                  singleLine = true,
                  keyboardOptions =
                      KeyboardOptions.Default.copy(
                          imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
                  colors = textFieldColors,
                  modifier = Modifier.fillMaxWidth().padding(start = 15.dp, end = 15.dp))

              TextField(
                  value = urlState.value,
                  onValueChange = { newVal -> urlState.value = newVal },
                  label = { Text("Link") },
                  placeholder = { Text("Enter a link") },
                  singleLine = true,
                  colors = textFieldColors,
                  modifier = Modifier.fillMaxWidth().padding(start = 15.dp, end = 15.dp))

              if (!urlValid) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text =
                        "Url Not Valid, should be of the form :\n\"" +
                            "http://example.com, https://example.com\"",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Red)
              }

              Spacer(modifier = Modifier.height(screenHeight / 80))

              Row(
                  modifier = Modifier.fillMaxWidth().padding(start = 15.dp, top = 10.dp),
                  verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tagsButtonText,
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
                            Modifier.clickable { showPopup.value = true }.testTag("TagsButton"))
                  }

              Spacer(modifier = Modifier.height(screenHeight / 80))

              if (isPrivate) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 15.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                      Text(
                          text = "Add Participants",
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
                              Modifier.clickable {
                                nav.navigateToScreen(
                                    Route.ADD_PARTICIPANTS.replace("{eventId}", uid))
                              })
                    }
                Spacer(modifier = Modifier.height(screenHeight / 80))
              }

              Row(
                  modifier = Modifier.fillMaxWidth().padding(start = 15.dp),
                  verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (imageUriState.value != null) "Delete Image" else "Add Image",
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
                            Modifier.clickable {
                                  if (imageUriState.value != null) {
                                    imageUriState.value = null
                                    imageBitmapState.value = null
                                  } else {
                                    imagePickerLauncher.launch("image/*")
                                  }
                                }
                                .testTag("AddImageButton"))
                  }

              Spacer(modifier = Modifier.height(screenHeight / 80))

              var showDialog by remember { mutableStateOf(false) }
              imageUriState.value?.let {
                Text(
                    text = "View Selected Image",
                    modifier = Modifier.clickable { showDialog = true },
                    style =
                        TextStyle(
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight(1000),
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.5.sp,
                        ))
              }

              if (showDialog) {
                Dialog(onDismissRequest = { showDialog = false }) {
                  imageBitmapState.value?.let { bitmap ->
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxWidth())
                  }
                }
              }

              Spacer(modifier = Modifier.height(screenHeight / 80))

              Button(
                  modifier = Modifier.width((screenWidth / 1.5.dp).dp).height(screenHeight / 17),
                  onClick = {
                    urlValid = URLUtil.isValidUrl(urlState.value) || urlState.value.isEmpty()

                    titleState.value = titleState.value.trimEnd()
                    descriptionState.value = descriptionState.value.trimEnd()
                    urlState.value = urlState.value.trimEnd()

                    if (titleState.value.isNotEmpty() && urlValid) {
                      when (selectedLocation.value == null) {
                        true ->
                            eventViewModel.location(locationState.value, 1) { locations ->
                              if (locations.isNotEmpty()) {
                                eventViewModel.createEvent(
                                    titleState.value,
                                    descriptionState.value,
                                    locations[0],
                                    pickedDate.value,
                                    pickedTime.value,
                                    price,
                                    urlState.value,
                                    invitedParticipants.map { it.uid },
                                    listOf(),
                                    listOf(),
                                    0,
                                    !isPrivate,
                                    listOf(),
                                    listOf(),
                                    imageUriState.value,
                                    userViewModel,
                                    uid)

                                nav.goBack()
                              }
                            }
                        false -> {
                          eventViewModel.createEvent(
                              titleState.value,
                              descriptionState.value,
                              selectedLocation.value!!,
                              pickedDate.value,
                              pickedTime.value,
                              price,
                              urlState.value,
                              invitedParticipants.map { it.uid },
                              listOf(),
                              listOf(),
                              0,
                              !isPrivate,
                              tagsState.value,
                              listOf(),
                              imageUriState.value,
                              userViewModel,
                              uid)
                          nav.goBack()
                        }
                      }

                      // Update pending requests for invited participants
                      invitedParticipants.forEach { participant ->
                        userViewModel.viewModelScope.launch {
                          userViewModel.gotInvitation(uid, participant.uid)
                        }
                      }
                    }

                    customPins.createCustomPin(
                        context, pickedDate.value, pickedTime.value, 144, 213) { _, bitmap ->
                          // Handle the bitmap descriptor and bitmap as needed
                          val byteArray = customPins.bitmapToByteArray(bitmap)
                          customPins.uploadEventIcon(byteArray, uid)
                        }
                  },
                  shape = RoundedCornerShape(10.dp),
                  enabled =
                      fieldsAreFull(titleState.value, descriptionState.value, locationState.value),
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = MaterialTheme.colorScheme.outlineVariant,
                          contentColor = Color.White,
                          disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                          disabledContentColor = MaterialTheme.colorScheme.onBackground),
              ) {
                Text(text = "Post")
              }
            }
        if (showPopup.value) {
          Popup(
              alignment = Alignment.Center,
              onDismissRequest = { showPopup.value = !showPopup.value }) {
                TagsSelector(tagsButtonText, tagsState) {
                  showPopup.value = false
                  if (tagsState.value.isNotEmpty()) {
                    tagsButtonText = "Edit Tags"
                  }
                }
              }
        }
      }
}

/**
 * Composable function for the LocationField.
 *
 * @param selectedLocation The selected location.
 * @param locationQuery The location query.
 * @param eventViewModel The event view model.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationField(
    selectedLocation: MutableState<Location?>,
    locationQuery: MutableState<String>,
    eventViewModel: EventViewModel
) {
  var expanded by remember { mutableStateOf(false) }
  val locationSuggestions = remember { mutableStateOf(emptyList<Location>()) }

  LaunchedEffect(locationQuery.value) {
    if (locationQuery.value == "") return@LaunchedEffect

    // fetch locations when user finished typing
    delay(1000)

    eventViewModel.location(locationQuery.value, NUMBER_OF_SUGGESTIONS) { locations ->
      locationSuggestions.value = locations
    }
  }

  ExposedDropdownMenuBox(
      expanded = expanded,
      onExpandedChange = { expanded = !expanded },
      modifier = Modifier.fillMaxSize().padding(start = 15.dp, end = 15.dp)) {
        TextField(
            value = locationQuery.value,
            onValueChange = {
              expanded = true
              locationQuery.value = it
            },
            label = { Text("Location") },
            placeholder = { Text("Enter an address") },
            singleLine = true,
            colors =
                TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.tertiary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary),
            modifier = Modifier.fillMaxWidth().menuAnchor())
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier =
                Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .wrapContentHeight()
                    .testTag("DropdownMenu"),
        ) {
          locationSuggestions.value.forEachIndexed { i, location ->
            DropdownMenuItem(
                modifier = Modifier.wrapContentSize(),
                text = { Text(location.name) },
                onClick = {
                  locationQuery.value = location.name
                  selectedLocation.value = location
                  expanded = false
                },
                colors =
                    MenuItemColors(
                        textColor = MaterialTheme.colorScheme.onBackground,
                        leadingIconColor = Color.Transparent,
                        trailingIconColor = Color.Transparent,
                        disabledTextColor = MaterialTheme.colorScheme.onBackground,
                        disabledLeadingIconColor = Color.Transparent,
                        disabledTrailingIconColor = Color.Transparent),
            )
            if (i != locationSuggestions.value.size - 1) {
              HorizontalDivider(
                  modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                  color = MaterialTheme.colorScheme.tertiary)
            }
          }
        }
      }
}

/**
 * Function to determine whether all fields have been filled.
 *
 * @param title The title of the event.
 * @param desc The description of the event.
 * @param loc The location of the event.
 * @param price The price of the event.
 * @param url The url of the event.
 * @return true if all fields are filled, false otherwise.
 */
private fun fieldsAreFull(
    title: String,
    desc: String,
    loc: String,
): Boolean {
  return title.isNotEmpty() && desc.isNotEmpty() && loc.isNotEmpty()
}

fun isValidPrice(price: String): Boolean {
  val regex = """^(\d{1,6})(\.\d{0,2})?$""".toRegex()
  if (!regex.matches(price)) return false

  val value = price.toDoubleOrNull() ?: return false
  return value <= 1_000_000
}
