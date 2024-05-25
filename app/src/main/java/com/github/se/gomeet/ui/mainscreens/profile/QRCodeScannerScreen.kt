package com.github.se.gomeet.ui.mainscreens.profile

import android.Manifest
import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.google.accompanist.permissions.*
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QRCodeScannerScreen(onQRCodeScanned: (String) -> Unit, nav: NavigationActions) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  var hasCameraPermission by remember { mutableStateOf(false) }
  val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

  val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

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
        }
      }) { padding ->
        if (hasCameraPermission) {
          Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            CameraPreviewView(
                cameraProviderFuture = cameraProviderFuture,
                lifecycleOwner = lifecycleOwner,
                onQRCodeScanned = onQRCodeScanned)
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
  val context = LocalContext.current
  val executor = remember { Executors.newSingleThreadExecutor() }

  AndroidView(
      modifier = Modifier.fillMaxSize(),
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
