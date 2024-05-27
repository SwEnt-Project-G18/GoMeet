package com.github.se.gomeet.viewmodel

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.google.android.gms.maps.model.LatLng
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun processGalleryImage(context: Context, uri: Uri, onQRCodeScanned: (String) -> Unit) {
  val scanner = BarcodeScanning.getClient()
  try {
    val image = InputImage.fromFilePath(context, uri)
    scanner
        .process(image)
        .addOnSuccessListener { barcodes ->
          for (barcode in barcodes) {
            barcode.rawValue?.let {
              Log.d("QRCodeScannerScreen", "QR Code detected: $it")
              onQRCodeScanned(it)
            }
          }
        }
        .addOnFailureListener { e -> Log.e("QRCodeScannerScreen", "Error processing image", e) }
  } catch (e: IOException) {
    Log.e("QRCodeScannerScreen", "Error loading image", e)
  }
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
              date = event.getDateString(),
              time = event.getTimeString(),
              organizer = event.creator,
              rating = event.ratings[eventVM.currentUID!!] ?: 0L,
              description = event.description,
              url = event.url,
              loc = LatLng(event.location.latitude, event.location.longitude))
        } else {
          Log.e("Event", "Event not found")
        }
      }
    }
  }
}

fun generateQRCode(type: String, id: String): Bitmap {
  val content = "$type/$id"
  val writer = MultiFormatWriter()
  val bitMatrix: BitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 1024, 1024)
  val encoder = BarcodeEncoder()
  return encoder.createBitmap(bitMatrix)
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
