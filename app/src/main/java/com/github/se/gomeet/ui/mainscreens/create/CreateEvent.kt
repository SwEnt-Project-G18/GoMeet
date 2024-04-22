package com.github.se.gomeet.ui.mainscreens.create

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalConfiguration
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
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.SECOND_LEVEL_DESTINATION
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.ui.theme.Grey
import com.github.se.gomeet.viewmodel.EventViewModel
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun CreateEvent(nav: NavigationActions, eventViewModel: EventViewModel, isPrivate: Boolean) {

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

  Scaffold(
      modifier = Modifier.testTag("CreateEvent"),
      topBar = {
          Column {
              Text(
                  text = "Create",
                  modifier = Modifier.padding(horizontal = 15.dp, vertical = 15.dp),
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
                      modifier = Modifier.padding(horizontal = 18.dp),
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
                      modifier = Modifier.padding(horizontal = 18.dp),
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
            Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              Spacer(modifier = Modifier.size((LocalConfiguration.current.screenHeightDp / 9).dp))

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
                  modifier =
                  Modifier
                      .fillMaxWidth()
                      .padding(start = 7.dp, end = 7.dp)
                      .testTag("Title"))

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
                  modifier =
                  Modifier
                      .fillMaxWidth()
                      .padding(start = 7.dp, end = 7.dp)
                      .testTag("Description"))

              OutlinedTextField(
                  value = locationState.value,
                  onValueChange = { newVal -> locationState.value = newVal },
                  label = { Text("Location") },
                  placeholder = { Text("Enter an address") },
                  singleLine = true,
                  shape = RoundedCornerShape(10.dp),
                  textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                  colors =
                      TextFieldDefaults.outlinedTextFieldColors(
                          focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                          unfocusedBorderColor = MaterialTheme.colorScheme.onBackground),
                  modifier =
                  Modifier
                      .fillMaxWidth()
                      .padding(start = 7.dp, end = 7.dp)
                      .testTag("Location"))

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
                  modifier =
                  Modifier
                      .fillMaxWidth()
                      .padding(start = 7.dp, end = 7.dp)
                      .testTag("Date"))

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
                  modifier =
                  Modifier
                      .fillMaxWidth()
                      .padding(start = 7.dp, end = 7.dp)
                      .testTag("Price"))

              OutlinedTextField(
                  value = url.value,
                  onValueChange = { newVal -> url.value = newVal },
                  label = { Text("Link") },
                  placeholder = { Text("Enter a link") },
                  singleLine = true,
                  shape = RoundedCornerShape(10.dp),
                  colors =
                      TextFieldDefaults.outlinedTextFieldColors(
                          focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                          unfocusedBorderColor = MaterialTheme.colorScheme.onBackground),
                  modifier =
                  Modifier
                      .fillMaxWidth()
                      .padding(start = 7.dp, end = 7.dp)
                      .testTag("Link"))

              if (isPrivate) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(7.dp),
                    onClick = {
                      nav.navigateTo(
                          SECOND_LEVEL_DESTINATION.first { it.route == Route.ADD_PARTICIPANTS })
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Grey),
                    shape = RoundedCornerShape(10.dp)) {
                      Text(text = "Add Participants", color = Color.White)
                    }
              }

              Button(
                  modifier = Modifier
                      .fillMaxWidth()
                      .padding(7.dp),
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
                            color = Color(0xFF000000),
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
                    if (!dateFormatError && dateState != null && titleState.value.isNotEmpty()) {
                      eventViewModel.location(locationState.value) { location ->
                        eventViewModel.createEvent(
                            titleState.value,
                            descriptionState.value,
                            location!!,
                            dateState!!,
                            price,
                            url.value,
                            listOf(),
                            listOf(),
                            0,
                            !isPrivateEvent.value,
                            listOf(),
                            listOf(),
                            imageUri)
                        nav.goBack()
                      }
                    }
                  },
                  modifier = Modifier.testTag("PostButton"),
                  shape = RoundedCornerShape(10.dp),
                  border = BorderStroke(1.dp, Color.Gray),
                  enabled = true,
                  colors =
                      ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFECEFF1))) {
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
