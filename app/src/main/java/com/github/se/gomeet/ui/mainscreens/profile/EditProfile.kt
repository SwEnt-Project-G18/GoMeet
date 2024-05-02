package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.R
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.auth

@Composable
fun EditProfile(nav: NavigationActions, userViewModel: UserViewModel = UserViewModel()) {
  val currentUser = remember { mutableStateOf<GoMeetUser?>(null) }
  val firstName = remember { mutableStateOf("") }
  val lastName = remember { mutableStateOf("") }
  val email = remember { mutableStateOf("") }
  val username = remember { mutableStateOf("") }
  val phoneNumber = remember { mutableStateOf("") }
  val country = remember { mutableStateOf("") }

  LaunchedEffect(Unit) {
    currentUser.value = userViewModel.getUser(com.google.firebase.Firebase.auth.currentUser!!.uid)
      firstName.value = currentUser.value!!.firstName
        lastName.value = currentUser.value!!.lastName
        email.value = currentUser.value!!.email
        username.value = currentUser.value!!.username
        phoneNumber.value = currentUser.value!!.phoneNumber
        country.value = currentUser.value!!.country
  }

  val textFieldColors =
      TextFieldDefaults.colors(
          focusedTextColor = DarkCyan,
          unfocusedTextColor = DarkCyan,
          unfocusedContainerColor = Color.Transparent,
          focusedContainerColor = Color.Transparent,
          cursorColor = DarkCyan,
          focusedLabelColor = MaterialTheme.colorScheme.tertiary,
          focusedIndicatorColor = MaterialTheme.colorScheme.tertiary)

  Scaffold(
      topBar = {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically) {
              Text(
                  text = "My Profile",
                  modifier =
                      Modifier.padding(start = 15.dp, top = 15.dp, end = 15.dp, bottom = 0.dp),
                  color = DarkCyan,
                  fontStyle = FontStyle.Normal,
                  fontWeight = FontWeight.SemiBold,
                  fontFamily = FontFamily.Default,
                  textAlign = TextAlign.Start,
                  style = MaterialTheme.typography.headlineLarge)

              Spacer(modifier = Modifier.weight(1f))

              Text(
                  text = "Done",
                  modifier =
                      Modifier.padding(start = 15.dp, top = 15.dp, end = 15.dp, bottom = 0.dp)
                          .clickable {
                            userViewModel.editUser(
                                currentUser.value!!.copy(
                                    firstName = firstName.value,
                                    lastName = lastName.value,
                                    email = email.value,
                                    username = username.value,
                                    phoneNumber = phoneNumber.value,
                                    country = country.value))
                            nav.navigateToScreen(Route.PROFILE)
                          },
                  color = MaterialTheme.colorScheme.onBackground,
                  fontStyle = FontStyle.Normal,
                  fontWeight = FontWeight.Normal,
                  fontFamily = FontFamily.Default,
                  textAlign = TextAlign.Start,
                  style = MaterialTheme.typography.bodyLarge)
            }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { selectedTab ->
              nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
            },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = Route.PROFILE)
      },
      content = { innerPadding ->
        Column(
            modifier =
                Modifier.padding(innerPadding).verticalScroll(rememberScrollState(0)).fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Image(
                  modifier =
                      Modifier.padding(start = 15.dp, end = 15.dp, top = 30.dp, bottom = 15.dp)
                          .width(101.dp)
                          .height(101.dp)
                          .clip(CircleShape)
                          .background(color = MaterialTheme.colorScheme.background)
                          .align(Alignment.CenterHorizontally),
                  painter = painterResource(id = R.drawable.gomeet_logo),
                  contentDescription = "image description",
                  contentScale = ContentScale.None)

              Spacer(modifier = Modifier.size(16.dp))

              TextField(
                  value = firstName.value,
                  onValueChange = { newValue ->
                      if (newValue.isNotBlank() && newValue.isNotEmpty()) {
                          firstName.value = newValue
                      }
                                  },
                  label = { Text("First Name") },
                  singleLine = true,
                  modifier = Modifier.fillMaxWidth(),
                  colors = textFieldColors)

              Spacer(modifier = Modifier.size(16.dp))

              TextField(
                  value = lastName.value,
                  onValueChange = { newValue ->
                      if (newValue.isNotBlank() && newValue.isNotEmpty()) {
                          lastName.value = newValue
                      }
                                  },
                  label = { Text("Last Name") },
                  singleLine = true,
                  modifier = Modifier.fillMaxWidth(),
                  colors = textFieldColors)

              Spacer(modifier = Modifier.size(16.dp))

              TextField(
                  value = email.value,
                  onValueChange = { newValue ->
                      if (newValue.isNotBlank() && newValue.isNotEmpty()) {
                          email.value = newValue
                      }
                                  },
                  label = { Text("Email Address") },
                  singleLine = true,
                  modifier = Modifier.fillMaxWidth(),
                  colors = textFieldColors)

              Spacer(modifier = Modifier.size(16.dp))

              TextField(
                  value = username.value,
                  onValueChange = { newValue ->
                      if (newValue.isNotBlank() && newValue.isNotEmpty()) {
                          username.value = newValue
                      }
                                  },
                  label = { Text("Username") },
                  singleLine = true,
                  modifier = Modifier.fillMaxWidth(),
                  colors = textFieldColors)

              Spacer(modifier = Modifier.size(16.dp))

              TextField(
                  value = phoneNumber.value,
                  onValueChange = { newValue ->
                      if (newValue.isNotBlank() && newValue.isNotEmpty()) {
                          phoneNumber.value = newValue
                      }
                                  },
                  label = { Text("Phone Number") },
                  singleLine = true,
                  modifier = Modifier.fillMaxWidth(),
                  colors = textFieldColors)

              Spacer(modifier = Modifier.size(16.dp))

              TextField(
                  value = country.value,
                  onValueChange = { newValue ->
                      if (newValue.isNotBlank() && newValue.isNotEmpty()) {
                          country.value = newValue
                      }
                                  },
                  label = { Text("Country") },
                  singleLine = true,
                  modifier = Modifier.fillMaxWidth(),
                  colors = textFieldColors)

              Spacer(modifier = Modifier.size(16.dp))

              Text(
                  text = "Edit Tags",
                  modifier =
                      Modifier.padding(start = 15.dp, top = 15.dp, end = 15.dp, bottom = 15.dp)
                          .align(Alignment.Start)
                          .clickable { /* TODO: Edit the tags */},
                  color = MaterialTheme.colorScheme.onBackground,
                  fontStyle = FontStyle.Normal,
                  fontWeight = FontWeight.Normal,
                  fontFamily = FontFamily.Default,
                  textAlign = TextAlign.Start,
                  style = MaterialTheme.typography.bodyMedium)
            }
      })
}

@Preview
@Composable
fun EditProfilePreview() {
  EditProfile(nav = NavigationActions(rememberNavController()))
}
