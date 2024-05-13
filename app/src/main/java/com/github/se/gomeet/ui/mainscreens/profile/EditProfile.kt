package com.github.se.gomeet.ui.mainscreens.profile

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.layout.ContentScale
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
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.mainscreens.LoadingText
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.InputStream
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditProfile(
    nav: NavigationActions,
    userViewModel: UserViewModel = UserViewModel(UserRepository(Firebase.firestore))
) {
  val currentUser = remember { mutableStateOf<GoMeetUser?>(null) }
  val firstName = remember { mutableStateOf("") }
  val lastName = remember { mutableStateOf("") }
  val email = remember { mutableStateOf("") }
  val username = remember { mutableStateOf("") }
  val phoneNumber = remember { mutableStateOf("") }
  val country = remember { mutableStateOf("") }
  val tags = remember { mutableStateOf(emptyList<String>()) }
  var isLoaded by remember { mutableStateOf(false) }
  val showPopup = remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    currentUser.value = userViewModel.getUser(com.google.firebase.Firebase.auth.currentUser!!.uid)
    firstName.value = currentUser.value!!.firstName
    lastName.value = currentUser.value!!.lastName
    email.value = currentUser.value!!.email
    username.value = currentUser.value!!.username
    phoneNumber.value = currentUser.value!!.phoneNumber
    country.value = currentUser.value!!.country
    tags.value = (currentUser.value!!.tags)
    isLoaded = true
  }

  val textFieldColors =
      TextFieldDefaults.colors(
          focusedTextColor = MaterialTheme.colorScheme.onBackground,
          unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
          unfocusedContainerColor = Color.Transparent,
          focusedContainerColor = Color.Transparent,
          cursorColor = DarkCyan,
          focusedLabelColor = MaterialTheme.colorScheme.tertiary,
          focusedIndicatorColor = MaterialTheme.colorScheme.tertiary)

  var imageUri by remember { mutableStateOf<android.net.Uri?>(null) }
  var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
  var profilePictureUrl by remember { mutableStateOf<String?>(null) }

  val context = LocalContext.current

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
            profilePictureUrl = uriNonNull.toString()
          }
        }
      }

  LaunchedEffect(currentUser.value?.uid) {
    val db = FirebaseFirestore.getInstance()
    val userDocRef = currentUser.value?.uid?.let { db.collection("users").document(it) }
    try {
      val snapshot = userDocRef?.get()?.await()
      if (snapshot != null) {
        profilePictureUrl = snapshot.getString("profilePicture")
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  Scaffold(
      modifier = Modifier.padding(horizontal = 15.dp),
      topBar = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = { nav.goBack() }) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Go back")
          }
          Spacer(modifier = Modifier.weight(1f))
          Text(
              text = "Done",
              style = MaterialTheme.typography.bodyLarge,
              modifier =
                  Modifier.padding(2.dp).clickable {
                    if (imageUri != null) {
                      userViewModel.uploadImageAndGetUrl(
                          userId = currentUser.value!!.uid,
                          imageUri = imageUri!!,
                          onSuccess = { imageUrl ->
                            val updatedUser =
                                currentUser.value!!.copy(
                                    firstName = firstName.value,
                                    lastName = lastName.value,
                                    email = email.value,
                                    username = username.value,
                                    phoneNumber = phoneNumber.value,
                                    country = country.value,
                                    profilePicture = imageUrl)
                            userViewModel.editUser(updatedUser)
                            nav.goBack()
                          },
                          onError = { exception ->
                            Log.e(
                                "ProfileUpdate", "Failed to upload new image: ${exception.message}")
                          })
                    } else {
                      val updatedUser =
                          currentUser.value!!.copy(
                              firstName = firstName.value,
                              lastName = lastName.value,
                              email = email.value,
                              username = username.value,
                              phoneNumber = phoneNumber.value,
                              country = country.value,
                              profilePicture = profilePictureUrl ?: "")
                      userViewModel.editUser(updatedUser)
                      nav.goBack()
                      Log.e("ProfileUpdate", "No image selected")
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
            selectedItem = Route.PROFILE)
      },
      content = { innerPadding ->
        if (isLoaded) {
          Column(
              modifier =
                  Modifier.padding(innerPadding)
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
                    contentDescription = "Profile picture",
                    modifier =
                        Modifier.padding(start = 15.dp, end = 15.dp, top = 30.dp, bottom = 15.dp)
                            .width(101.dp)
                            .height(101.dp)
                            .clickable { imagePickerLauncher.launch("image/*") }
                            .clip(CircleShape)
                            .background(color = MaterialTheme.colorScheme.background)
                            .align(Alignment.CenterHorizontally)
                            .testTag("Profile Picture"),
                    contentScale = ContentScale.Crop)

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

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 15.dp, bottom = 10.dp),
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
                          Icons.Default.KeyboardArrowRight,
                          null,
                          modifier = Modifier.clickable { showPopup.value = true })
                    }
              }
        } else {
          LoadingText()
        }
        if (showPopup.value) {
          Popup(
              alignment = Alignment.Center,
              onDismissRequest = { showPopup.value = !showPopup.value }) {
                TagsSelector("Edit Tags", tags) {
                  currentUser.value!!.tags = tags.value
                  userViewModel.editUser(currentUser.value!!)
                  showPopup.value = false
                }
              }
        }
      })
}
