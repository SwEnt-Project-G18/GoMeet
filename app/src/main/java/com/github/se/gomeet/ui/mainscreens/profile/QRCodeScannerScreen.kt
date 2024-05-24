package com.github.se.gomeet.ui.mainscreens.profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
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
import com.github.se.gomeet.model.event.getEventDateString
import com.github.se.gomeet.model.event.getEventTimeString
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.accompanist.permissions.*
import com.google.android.gms.maps.model.LatLng
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QRCodeScannerScreen(nav: NavigationActions) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember { mutableStateOf(false) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val galleryLauncher = rememberLauncherForActivityResult(
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
                        "Event" -> fetchEventAndNavigate(id, nav, EventViewModel())
                        else -> throw IllegalArgumentException("Unknown QR code type")
                    }
                }
            }
        }
    )

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
                                delay(1000)  // Adjust the delay as needed
                                scanCompleted = false
                            }
                        }
                    }
                )
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

fun processGalleryImage(context: Context, uri: Uri, onQRCodeScanned: (String) -> Unit) {
    val scanner = BarcodeScanning.getClient()
    try {
        val image = InputImage.fromFilePath(context, uri)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    barcode.rawValue?.let {
                        Log.d("QRCodeScannerScreen", "QR Code detected: $it")
                        onQRCodeScanned(it)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("QRCodeScannerScreen", "Error processing image", e)
            }
    } catch (e: IOException) {
        Log.e("QRCodeScannerScreen", "Error loading image", e)
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
        modifier = Modifier.fillMaxSize().padding(16.dp)  // Optional padding to create some spacing
            .clip(RoundedCornerShape(16.dp)),  // Apply rounded corners,
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

class QRCodeAnalyzer(private val onQRCodeScanned: (String) -> Unit) : ImageAnalysis.Analyzer {
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        val scanner = BarcodeScanning.getClient()
        scanner
            .process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    barcode.rawValue?.let { onQRCodeScanned(it) }
                }
            }
            .addOnFailureListener {
                // Handle errors
            }
            .addOnCompleteListener { imageProxy.close() }
    }
}

fun parseQRCodeContent(content: String): Pair<String, String> {
    val parts = content.split("/")
    if (parts.size == 2) {
        val type = parts[0]
        val id = parts[1]
        return Pair(type, id)
    }
    throw IllegalArgumentException("Invalid QR code content")
}

fun fetchEventAndNavigate(eventId: String, nav: NavigationActions, eventVM: EventViewModel) {
    CoroutineScope(Dispatchers.Main).launch {
        val navigationPerformed = mutableStateOf(false)
        if (!navigationPerformed.value) {
            navigationPerformed.value = true
            CoroutineScope(Dispatchers.Main).launch {
                val event = eventVM.getEvent(eventId)
                if (event != null) {
                    Log.d("Event", "Navigating to event info screen")
                    nav.navigateToEventInfo(
                        eventId = event.eventID,
                        title = event.title,
                        date = getEventDateString(event.date),
                        time = getEventTimeString(event.time),
                        organizer = event.creator,
                        rating = 0.0,
                        description = event.description,
                        loc = LatLng(event.location.latitude, event.location.longitude)
                    )
                } else {
                    // Handle event not found
                }
            }
        }
    }
}