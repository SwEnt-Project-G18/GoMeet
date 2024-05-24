package com.github.se.gomeet.ui.mainscreens.profile

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.isPastEvent
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.mainscreens.LoadingText
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.SECOND_LEVEL_DESTINATION
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Profile screen composable
 *
 * @param nav NavigationActions
 * @param userViewModel UserViewModel
 */
@Composable
fun Profile(
    nav: NavigationActions,
    userId: String,
    userViewModel: UserViewModel,
    eventViewModel: EventViewModel
) {
  val screenWidth = LocalConfiguration.current.screenWidthDp.dp

  val screenHeight = LocalConfiguration.current.screenHeightDp.dp
  val coroutineScope = rememberCoroutineScope()
  var isProfileLoaded by remember { mutableStateOf(false) }
  var currentUser by remember { mutableStateOf<GoMeetUser?>(null) }
  val joinedEventsList = remember { mutableListOf<Event>() }
  val myHistoryList = remember { mutableListOf<Event>() }
  var showShareProfileDialog by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    coroutineScope.launch {
      currentUser = userViewModel.getUser(userId)
      val allEvents =
          (eventViewModel.getAllEvents() ?: emptyList()).filter { e ->
            currentUser!!.joinedEvents.contains(e.eventID)
          }
      allEvents.forEach {
        if (!isPastEvent(it)) {
          joinedEventsList.add(it)
        } else {
          myHistoryList.add(it)
        }
      }
      isProfileLoaded = true
    }
  }
  Scaffold(
      floatingActionButton = {
        Box(modifier = Modifier.padding(8.dp)) {
          IconButton(
              modifier =
                  Modifier.background(
                      color = MaterialTheme.colorScheme.outlineVariant,
                      shape = RoundedCornerShape(10.dp)),
              onClick = { nav.navigateToScreen(Route.SCAN) }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.scan_icon),
                    contentDescription = "Create Event",
                    tint = Color.White)
              }
        }
      },
      modifier = Modifier.testTag("Profile"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { selectedTab ->
              nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
            },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = Route.PROFILE)
      },
      topBar = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier.padding(start = screenWidth / 15, top = screenHeight / 30)
                    .testTag("TopBar")) {
              Text(
                  text = "My Profile",
                  color = MaterialTheme.colorScheme.onBackground,
                  style =
                      MaterialTheme.typography.headlineMedium.copy(
                          fontWeight = FontWeight.SemiBold))
              Spacer(Modifier.weight(1f))
              IconButton(
                  modifier = Modifier.align(Alignment.CenterVertically),
                  onClick = { nav.navigateToScreen(Route.NOTIFICATIONS) }) {
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        modifier =
                            Modifier.size(screenHeight / 28).align(Alignment.CenterVertically),
                        tint = MaterialTheme.colorScheme.onBackground)
                  }

              IconButton(
                  modifier = Modifier.align(Alignment.CenterVertically).padding(end = 15.dp),
                  onClick = {
                    nav.navigateTo(SECOND_LEVEL_DESTINATION.first { it.route == Route.SETTINGS })
                  }) {
                    Icon(
                        Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        modifier =
                            Modifier.size(screenHeight / 28).align(Alignment.CenterVertically),
                        tint = MaterialTheme.colorScheme.onBackground)
                  }
            }
      }) { innerPadding ->
        if (isProfileLoaded) {
          Column(
              verticalArrangement = Arrangement.SpaceEvenly,
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState(0))) {
                Spacer(modifier = Modifier.height(screenHeight / 60))
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(start = screenWidth / 20)
                            .testTag("UserInfo")) {
                      ProfileImage(
                          userId = userId,
                          modifier = Modifier.testTag("Profile Picture"),
                          size = 101.dp)
                      Column(modifier = Modifier.padding(start = screenWidth / 20)) {
                        Text(
                            (currentUser?.firstName + " " + currentUser?.lastName),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleLarge)

                        Text(
                            text = ("@" + currentUser?.username),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyLarge)
                      }
                    }
                Spacer(modifier = Modifier.height(screenHeight / 40))
                Row(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(screenWidth / 50),
                    verticalAlignment = Alignment.CenterVertically) {
                      // Edit Profile button
                      Button(
                          onClick = { nav.navigateToScreen(Route.EDIT_PROFILE) },
                          modifier = Modifier.height(37.dp).width(screenWidth * 4 / 11),
                          shape = RoundedCornerShape(10.dp),
                          colors =
                              ButtonDefaults.buttonColors(
                                  containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                            Text(text = "Edit Profile", color = MaterialTheme.colorScheme.tertiary)
                          }

                      Button(
                          onClick = { showShareProfileDialog = true },
                          modifier = Modifier.height(37.dp).width(screenWidth * 4 / 11),
                          shape = RoundedCornerShape(10.dp),
                          colors =
                              ButtonDefaults.buttonColors(
                                  containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                            Text(text = "Share Profile", color = MaterialTheme.colorScheme.tertiary)
                          }

                      Button(
                          onClick = { nav.navigateToScreen(Route.ADD_FRIEND) },
                          modifier = Modifier.height(37.dp),
                          shape = RoundedCornerShape(10.dp),
                          colors =
                              ButtonDefaults.buttonColors(
                                  containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.add_friend),
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.tertiary)
                          }
                    }

                Spacer(modifier = Modifier.height(screenHeight / 40))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()) {
                      Column(
                          modifier =
                              Modifier.clickable {
                                // TODO
                              }) {
                            Text(
                                text = currentUser?.myEvents?.size.toString(),
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                            Text(
                                text = "Events",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                          }
                      Column(
                          modifier =
                              Modifier.clickable {
                                nav.navigateToScreen(Route.FOLLOWERS.replace("{uid}", userId))
                              }) {
                            Text(
                                text = currentUser?.followers?.size.toString(),
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                            Text(
                                text = "Followers",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                          }
                      Column(
                          modifier =
                              Modifier.clickable {
                                nav.navigateToScreen(Route.FOLLOWING.replace("{uid}", userId))
                              }) {
                            Text(
                                text = currentUser?.following?.size.toString(),
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                            Text(
                                text = "Following",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.CenterHorizontally))
                          }
                    }

                Spacer(modifier = Modifier.fillMaxWidth().height(screenHeight / 50))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    contentPadding = PaddingValues(start = 15.dp, end = 15.dp)) {
                      items(currentUser!!.tags.size) { index ->
                        Button(
                            onClick = {},
                            content = {
                              Text(
                                  text = currentUser!!.tags[index],
                                  style = MaterialTheme.typography.labelLarge)
                            },
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.outlineVariant),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.padding(end = 8.dp))
                      }
                    }

                Spacer(modifier = Modifier.height(screenHeight / 40))

                ProfileEventsList("Joined Events", rememberLazyListState(), joinedEventsList, nav)
                Spacer(modifier = Modifier.height(screenHeight / 30))
                ProfileEventsList("My History", rememberLazyListState(), myHistoryList, nav)
              }
        } else {
          LoadingText()
        }
      }

  // Show the QR code dialog if the state is true
  if (showShareProfileDialog) {
    ShareDialog("Profile", currentUser?.uid ?: "", onDismiss = { showShareProfileDialog = false })
  }
}

@Composable
fun ProfileImage(
    userId: String,
    modifier: Modifier = Modifier,
    defaultImageResId: Int = R.drawable.gomeet_logo,
    size: Dp
) {
  var profilePictureUrl by remember { mutableStateOf<String?>(null) }

  LaunchedEffect(userId) {
    val db = FirebaseFirestore.getInstance()
    val userDocRef = db.collection("users").document(userId)
    try {
      val snapshot = userDocRef.get().await()
      profilePictureUrl = snapshot.getString("profilePicture")
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  Image(
      painter =
          if (!profilePictureUrl.isNullOrEmpty()) {
            rememberAsyncImagePainter(profilePictureUrl)
          } else {
            painterResource(id = defaultImageResId)
          },
      contentDescription = "Profile picture",
      modifier =
          modifier
              .size(size)
              .clip(CircleShape)
              .background(color = MaterialTheme.colorScheme.background),
      contentScale = ContentScale.Crop)
}

fun generateQRCode(type: String, id: String): Bitmap {
  val content = "$type/$id"
  val writer = MultiFormatWriter()
  val bitMatrix: BitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 1024, 1024)
  val encoder = BarcodeEncoder()
  return encoder.createBitmap(bitMatrix)
}

@Composable
fun ShareDialog(type: String, uid: String, onDismiss: () -> Unit) {
  val context = LocalContext.current
  val qrCodeBitmap by remember { mutableStateOf(generateQRCode(type, uid)) }

  AlertDialog(
      containerColor = MaterialTheme.colorScheme.background,
      onDismissRequest = onDismiss,
      icon = {
        Column {
          Row {
            IconButton(onClick = { saveImageToGallery(context, qrCodeBitmap) }) {
              Icon(
                  imageVector = ImageVector.vectorResource(R.drawable.download_icon),
                  contentDescription = "Save",
                  tint = MaterialTheme.colorScheme.tertiary,
                  modifier = Modifier.size(30.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { onDismiss() }) {
              Icon(
                  Icons.Filled.Close,
                  contentDescription = "Close",
                  tint = MaterialTheme.colorScheme.tertiary,
                  modifier = Modifier.size(36.dp))
            }
          }
          Image(
              bitmap = qrCodeBitmap.asImageBitmap(),
              contentDescription = "QR Code",
              modifier = Modifier.fillMaxWidth().background(Color.White),
              contentScale = ContentScale.Fit)
        }
      },
      confirmButton = {
        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.outlineVariant),
            onClick = { shareImage(context, qrCodeBitmap) }) {
              Text("Share", color = Color.White)
            }
      })
}

fun saveImageToGallery(context: Context, bitmap: Bitmap) {
  val values =
      ContentValues().apply {
        put(MediaStore.Images.Media.TITLE, "QR Code")
        put(MediaStore.Images.Media.DISPLAY_NAME, "QR Code")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/QR Codes")
      }

  val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

  uri?.let {
    val outputStream: OutputStream? = context.contentResolver.openOutputStream(it)
    outputStream?.use { stream ->
      if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
        Toast.makeText(context, "QR code saved to gallery", Toast.LENGTH_SHORT).show()
      } else {
        Toast.makeText(context, "Failed to save QR code", Toast.LENGTH_SHORT).show()
      }
    }
  }
}

fun shareImage(context: Context, bitmap: Bitmap) {
  val cachePath = File(context.cacheDir, "images")
  cachePath.mkdirs() // Create the directory if it doesn't exist
  val file = File(cachePath, "qr_code.png")
  val fileOutputStream = FileOutputStream(file)
  bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
  fileOutputStream.close()

  val fileUri: Uri =
      FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

  val shareIntent =
      Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, fileUri)
        type = "image/png"
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
  context.startActivity(Intent.createChooser(shareIntent, "Share QR Code"))
}

@Preview
@Composable
fun ProfilePreview() {
  Profile(
      nav = NavigationActions(rememberNavController()),
      "John",
      UserViewModel(),
      EventViewModel("John"))
}
