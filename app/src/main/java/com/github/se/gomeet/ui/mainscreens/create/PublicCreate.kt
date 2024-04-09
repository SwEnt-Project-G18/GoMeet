package com.github.se.gomeet.ui.mainscreens.create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.R
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.viewmodel.EventViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun PublicCreate(nav: NavigationActions, eventViewModel: EventViewModel) {

    val titleState = remember { mutableStateOf("") }
    val descriptionState = remember { mutableStateOf("") }
    val locationState = remember { mutableStateOf("") }
    val textDate = remember { mutableStateOf("") }
    var dateState by remember { mutableStateOf<LocalDate?>(null) }
    var dateFormatError by remember { mutableStateOf(false) }
    val price by remember { mutableDoubleStateOf(0.0) }
    val url = remember { mutableStateOf("") }


  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { selectedTab ->
              nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
            },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = Route.EXPLORE)
      }) { innerPadding ->
      Text(
          modifier = Modifier.padding(horizontal = 15.dp, vertical = 30.dp),
          text = "Create",
          style =
          TextStyle(
              fontSize = 24.sp,
              lineHeight = 16.sp,
              fontFamily = FontFamily(Font(R.font.roboto)),
              fontWeight = FontWeight(1000),
              color = Color(0xFF073F57),
              textAlign = TextAlign.Center,
              letterSpacing = 0.5.sp,
          )
      )
      Column(Modifier.padding(innerPadding),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center){

          Spacer(modifier = Modifier.height(100.dp))

          OutlinedTextField(
              value = titleState.value,
              onValueChange = { newVal -> titleState.value = newVal },
              label = { Text("Title") },
              placeholder = { Text("Name the event") },
              modifier = Modifier.fillMaxWidth().padding(7.dp))

          OutlinedTextField(
              value = descriptionState.value,
              onValueChange = { newVal -> descriptionState.value = newVal },
              label = { Text("Description") },
              placeholder = { Text("Describe the task") },
              modifier = Modifier.fillMaxWidth().padding(7.dp))


          OutlinedTextField(
              value = locationState.value,
              onValueChange = { newVal -> locationState.value = newVal },
              label = { Text("Location") },
              placeholder = { Text("Enter an address") },
              modifier = Modifier.fillMaxWidth().padding(7.dp))

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
              modifier = Modifier.fillMaxWidth().padding(7.dp))

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
                              true,
                              listOf(),
                              listOf()
                          )
                          nav.goBack()
                      }
                  }
              },
              modifier = Modifier.width(128.dp).height(40.dp),
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(1.dp, Color.Gray), // Set border here if needed
              enabled = true,
              colors =
              ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFECEFF1))) {
              Text(
                  text = "Post",
                  style = TextStyle(
                      fontSize = 12.sp,
                      lineHeight = 16.sp,
                      fontFamily = FontFamily(Font(R.font.roboto)),
                      fontWeight = FontWeight(1000),
                      color = Color(0xFF000000),
                      textAlign = TextAlign.Center,
                      letterSpacing = 0.5.sp,
                  )
              )
          }

          if (dateFormatError) {
              Text("Error: Date Format Error", color = Color.Red)
          }
      }
  }
}

@Preview
@Composable
fun PublicCreatePreview() {
  PublicCreate(NavigationActions(rememberNavController()), EventViewModel())
}
