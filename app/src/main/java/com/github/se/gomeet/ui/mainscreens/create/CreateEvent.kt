package com.github.se.gomeet.ui.mainscreens.create

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.location.Location
import com.github.se.gomeet.model.repository.EventRepository
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.ui.theme.Grey
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.InputStream
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlinx.coroutines.delay

private const val NUMBER_OF_SUGGESTIONS = 3

/**
 * Composable function for the CreateEvent screen.
 *
 * @param nav The navigation actions.
 * @param eventViewModel The event view model.
 * @param isPrivate The boolean value to determine if the event is private or not.
 */
@Composable
fun CreateEvent(nav: NavigationActions, eventViewModel: EventViewModel, isPrivate: Boolean) {

  val db = EventRepository(Firebase.firestore)
  val uid = db.getNewId()
  val titleState = remember { mutableStateOf("") }
  val descriptionState = remember { mutableStateOf("") }
  val locationState = remember { mutableStateOf("") }
  val textDate = remember { mutableStateOf("") }
  var dateState by remember { mutableStateOf<LocalDate?>(null) }
  var dateFormatError by remember { mutableStateOf(false) }
  var price by remember { mutableDoubleStateOf(0.0) }
  var priceText by remember { mutableStateOf("") }
  val url = remember { mutableStateOf("") }
  val isPrivateEvent = remember { mutableStateOf(false) }

  val customPins = remember { CustomPins() }

  val context = LocalContext.current

  var imageUri by remember { mutableStateOf<android.net.Uri?>(null) }
  var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
  val imagePickerLauncher =
      rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
          uri: android.net.Uri? ->
        imageUri = uri
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
            imageBitmap = bitmap.asImageBitmap()
          }
        }
      }

  val selectedLocation: MutableState<Location?> = remember { mutableStateOf(null) }

  Scaffold(
      topBar = {
        Column {
          Text(
              text = "Create",
              modifier = Modifier.padding(top = 15.dp, start = 15.dp, end = 18.dp, bottom = 0.dp),
              color = DarkCyan,
              fontStyle = FontStyle.Normal,
              fontWeight = FontWeight.SemiBold,
              fontFamily = FontFamily.Default,
              textAlign = TextAlign.Start,
              style = MaterialTheme.typography.headlineLarge)

          if (isPrivate) {
            isPrivateEvent.value = true
            Text(
                text = "Private",
                modifier = Modifier.padding(top = 0.dp, start = 18.dp, end = 18.dp, bottom = 15.dp),
                color = Grey,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Default,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleSmall)
          } else {
            isPrivateEvent.value = false
            Text(
                text = "Public",
                modifier = Modifier.padding(top = 0.dp, start = 18.dp, end = 18.dp, bottom = 15.dp),
                color = Grey,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Default,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleSmall)
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              // Spacer(modifier = Modifier.size((LocalConfiguration.current.screenHeightDp /
              // 9).dp))

              OutlinedTextField(
                  value = titleState.value,
                  onValueChange = { newVal -> titleState.value = newVal },
                  label = { Text("Title") },
                  placeholder = { Text("Name the event") },
                  singleLine = true,
                  shape = RoundedCornerShape(10.dp),
                  textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                  colors =
                      TextFieldDefaults.outlinedTextFieldColors(
                          focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                          unfocusedBorderColor = MaterialTheme.colorScheme.onBackground),
                  modifier = Modifier.fillMaxWidth().padding(start = 7.dp, end = 7.dp))

              OutlinedTextField(
                  value = descriptionState.value,
                  onValueChange = { newVal -> descriptionState.value = newVal },
                  label = { Text("Description") },
                  placeholder = { Text("Describe the task") },
                  singleLine = true,
                  shape = RoundedCornerShape(10.dp),
                  textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                  colors =
                      TextFieldDefaults.outlinedTextFieldColors(
                          focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                          unfocusedBorderColor = MaterialTheme.colorScheme.onBackground),
                  modifier = Modifier.fillMaxWidth().padding(start = 7.dp, end = 7.dp))
              LocationField(selectedLocation, locationState, eventViewModel)
              OutlinedTextField(
                  value = textDate.value,
                  onValueChange = { newText ->
                    textDate.value = newText
                    try {
                      dateState = LocalDate.parse(newText, DateTimeFormatter.ISO_LOCAL_DATE)
                      dateFormatError = false
                    } catch (e: DateTimeParseException) {
                      dateFormatError = true
                    }
                  },
                  label = { Text("Date") },
                  placeholder = { Text("Enter a date (yyyy-mm-dd)") },
                  singleLine = true,
                  shape = RoundedCornerShape(10.dp),
                  textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                  colors =
                      TextFieldDefaults.outlinedTextFieldColors(
                          focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                          unfocusedBorderColor = MaterialTheme.colorScheme.onBackground),
                  modifier = Modifier.fillMaxWidth().padding(start = 7.dp, end = 7.dp))

              OutlinedTextField(
                  value = priceText,
                  onValueChange = { newVal ->
                    priceText = newVal
                    newVal.toDoubleOrNull()?.let { price = it }
                  },
                  label = { Text("Price") },
                  placeholder = { Text("Enter a price") },
                  singleLine = true,
                  shape = RoundedCornerShape(10.dp),
                  textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                  colors =
                      TextFieldDefaults.outlinedTextFieldColors(
                          focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                          unfocusedBorderColor = MaterialTheme.colorScheme.onBackground),
                  modifier = Modifier.fillMaxWidth().padding(start = 7.dp, end = 7.dp))

              OutlinedTextField(
                  value = url.value,
                  onValueChange = { newVal -> url.value = newVal },
                  label = { Text("Link") },
                  placeholder = { Text("Enter a link") },
                  singleLine = true,
                  shape = RoundedCornerShape(10.dp),
                  textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                  colors =
                      TextFieldDefaults.outlinedTextFieldColors(
                          focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                          unfocusedBorderColor = MaterialTheme.colorScheme.onBackground),
                  modifier = Modifier.fillMaxWidth().padding(start = 7.dp, end = 7.dp))

              Spacer(modifier = Modifier.height(16.dp))

              if (isPrivate) {
                Button(
                    modifier = Modifier.fillMaxWidth().padding(start = 7.dp, end = 7.dp),
                    onClick = {
                      nav.navigateToScreen(Route.ADD_PARTICIPANTS.replace("{eventId}", uid))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Grey),
                    shape = RoundedCornerShape(10.dp)) {
                      Text(text = "Add Participants", color = Color.White)
                    }
              }

              Button(
                  modifier = Modifier.fillMaxWidth().padding(start = 7.dp, end = 7.dp),
                  onClick = {
                    if (imageUri != null) {
                      imageUri = null
                    } else {
                      imagePickerLauncher.launch("image/*")
                    }
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = Grey),
                  shape = RoundedCornerShape(10.dp)) {
                    Text(
                        text = if (imageUri != null) "Delete Image" else "Add Image",
                        color = Color.White)
                  }

              var showDialog by remember { mutableStateOf(false) }
              imageUri?.let {
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
                  imageBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxWidth())
                  }
                }
              }

              Spacer(modifier = Modifier.height(16.dp))

              OutlinedButton(
                  onClick = {
                    if (titleState.value.isNotEmpty() && !dateFormatError && dateState != null) {
                      if (selectedLocation.value == null) {
                        eventViewModel.location(locationState.value, 1) { locations ->
                          if (locations.isNotEmpty()) {
                            eventViewModel.createEvent(
                                titleState.value,
                                descriptionState.value,
                                locations[0],
                                dateState!!,
                                price,
                                url.value,
                                listOf(),
                                listOf(),
                                0,
                                !isPrivateEvent.value,
                                listOf(),
                                listOf(),
                                imageUri,
                                UserViewModel(),
                                uid)

                            nav.goBack()
                          }
                        }
                      } else {
                        eventViewModel.createEvent(
                            titleState.value,
                            descriptionState.value,
                            selectedLocation.value!!,
                            dateState!!,
                            price,
                            url.value,
                            listOf(),
                            listOf(),
                            0,
                            !isPrivateEvent.value,
                            listOf(),
                            listOf(),
                            imageUri,
                            UserViewModel(),
                            uid)

                        nav.goBack()
                      }
                    }

                    dateState?.let {
                      customPins.createCustomPin(context, it, LocalTime.MIDNIGHT) {
                          bitmapDescriptor,
                          bitmap ->
                        // Handle the bitmap descriptor and bitmap as needed
                        val byteArray = customPins.bitmapToByteArray(bitmap)
                        customPins.uploadEventIcon(context, byteArray, uid)
                      }
                    }
                  },
                  shape = RoundedCornerShape(10.dp),
                  border = BorderStroke(1.dp, Color.Gray),
                  enabled =
                      fieldsAreFull(
                          titleState.value,
                          descriptionState.value,
                          locationState.value,
                          textDate.value,
                          priceText,
                          url.value),
                  colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFECEFF1)),
              ) {
                Text(
                    text = "Post",
                    style =
                        TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight(1000),
                            color = Color(0xFF000000),
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.5.sp,
                        ))
              }

              if (dateFormatError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Error: Date Format Error", color = Color.Red)
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
      modifier = Modifier.padding(start = 7.dp, end = 7.dp).fillMaxSize()) {
        OutlinedTextField(
            value = locationQuery.value,
            onValueChange = {
              expanded = true
              locationQuery.value = it
            },
            label = { Text("Location") },
            placeholder = { Text("Enter an address") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
            colors =
                TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground),
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

private fun fieldsAreFull(
    title: String,
    desc: String,
    loc: String,
    date: String,
    price: String,
    url: String
): Boolean {
  return title.isNotEmpty() &&
      desc.isNotEmpty() &&
      loc.isNotEmpty() &&
      date.isNotEmpty() &&
      price.isNotEmpty() &&
      url.isNotEmpty()
}
