package com.github.se.gomeet.ui.mainscreens.profile

import android.Manifest
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.github.se.gomeet.R
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.QRCodeAnalyzer
import com.github.se.gomeet.viewmodel.fetchEventAndNavigate
import com.github.se.gomeet.viewmodel.parseQRCodeContent
import com.github.se.gomeet.viewmodel.processGalleryImage
import com.google.accompanist.permissions.*
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRCodeScannerScreen(nav: NavigationActions, eventViewModel: EventViewModel) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  var hasCameraPermission by remember { mutableStateOf(false) }
  val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

  val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
  val galleryLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.GetContent(),
          onResult = { uri: Uri? ->
            uri?.let {
              Log.d("QRCodeScannerScreen", "Selected image URI: $uri")
              processGalleryImage(context, it) { content ->
                Log.d("QRCodeScannerScreen", "Scanned content from gallery: $content")
                val (type, id) = parseQRCodeContent(content)
                val currentUserId = Firebase.auth.currentUser!!.uid
                when (type) {
                  "Profile" -> {
                    if (id == currentUserId) {
                      nav.navigateToScreen(Route.PROFILE)
                    } else {
                      nav.navigateToScreen(Route.OTHERS_PROFILE.replace("{uid}", id))
                    }
                  }
                  "Event" -> fetchEventAndNavigate(id, nav, eventViewModel)
                  else -> throw IllegalArgumentException("Unknown QR code type")
                }
              }
            }
          })

  LaunchedEffect(key1 = cameraPermissionState) { cameraPermissionState.launchPermissionRequest() }

  hasCameraPermission = cameraPermissionState.status.isGranted

  Scaffold(
      topBar = {
        Row(modifier = Modifier.testTag("TopBar"), verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = { nav.goBack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Go back",
                tint = MaterialTheme.colorScheme.onBackground)
          }
          Spacer(modifier = Modifier.weight(1f))
          IconButton(onClick = { galleryLauncher.launch("image/*") }) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.upload_icon),
                contentDescription = "Upload QR code from gallery",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(end = 16.dp))
          }
        }
      }) { padding ->
        if (hasCameraPermission) {
          Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            var scanCompleted by remember { mutableStateOf(false) }

            CameraPreviewView(
                cameraProviderFuture = cameraProviderFuture,
                lifecycleOwner = lifecycleOwner,
                onQRCodeScanned = { content ->
                  if (!scanCompleted) {
                    scanCompleted = true
                    val (type, id) = parseQRCodeContent(content)
                    val currentUserId = Firebase.auth.currentUser!!.uid
                    when (type) {
                      "Profile" -> {
                        if (id == currentUserId) {
                          nav.navigateToScreen(Route.PROFILE)
                        } else {
                          nav.navigateToScreen(Route.OTHERS_PROFILE.replace("{uid}", id))
                        }
                      }
                      "Event" -> fetchEventAndNavigate(id, nav, EventViewModel())
                      else -> throw IllegalArgumentException("Unknown QR code type")
                    }
                    // Reset the scanCompleted flag after a delay if necessary
                    CoroutineScope(Dispatchers.Main).launch {
                      delay(1000) // Adjust the delay as needed
                      scanCompleted = false
                    }
                  }
                })
          }
        } else {
          Column(
              modifier = Modifier.fillMaxSize().padding(16.dp),
              verticalArrangement = Arrangement.Center,
              horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Camera permission is required to scan QR codes.")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                  Text("Grant Camera Permission")
                }
              }
        }
      }
}

@Composable
fun CameraPreviewView(
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    lifecycleOwner: LifecycleOwner,
    onQRCodeScanned: (String) -> Unit
) {
  val executor = remember { Executors.newSingleThreadExecutor() }

  AndroidView(
      modifier =
          Modifier.fillMaxSize()
              .padding(16.dp) // Optional padding to create some spacing
              .clip(RoundedCornerShape(16.dp)), // Apply rounded corners,
      factory = { ctx ->
        val previewView =
            PreviewView(ctx).apply {
              layoutParams =
                  ViewGroup.LayoutParams(
                      ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        val cameraProvider = cameraProviderFuture.get()

        val preview =
            Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        val imageAnalysis =
            ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

        imageAnalysis.setAnalyzer(executor, QRCodeAnalyzer(onQRCodeScanned))

        try {
          cameraProvider.unbindAll()
          cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
        } catch (exc: Exception) {
          // Handle exceptions
        }

        previewView
      })
}
