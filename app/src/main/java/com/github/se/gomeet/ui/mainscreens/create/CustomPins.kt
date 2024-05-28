package com.github.se.gomeet.ui.mainscreens.create

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.github.se.gomeet.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/** CustomPins class is responsible for creating custom pins for the map and uploading them. */
class CustomPins {

  /**
   * Create a custom pin with the given date and time.
   *
   * @param context The context of the activity.
   * @param date The date of the event.
   * @param time The time of the event.
   * @param callback The callback function to be called when the pin is created.
   */
  fun createCustomPin(
      context: Context,
      date: LocalDate,
      time: LocalTime,
      width: Int,
      height: Int,
      callback: (BitmapDescriptor, Bitmap) -> Unit
  ) {
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val customPinView = inflater.inflate(R.layout.custom_pin_layout, null, false)

    val eventDate = customPinView.findViewById<TextView>(R.id.eventDay)
    val eventTime = customPinView.findViewById<TextView>(R.id.eventTime)

    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    eventDate.text = dayOfWeek.uppercase(Locale.getDefault())
    eventTime.text = time.format(DateTimeFormatter.ofPattern("HH:mm"))

    // Measure and layout with specified width and height
    val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
    val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
    customPinView.measure(widthMeasureSpec, heightMeasureSpec)
    customPinView.layout(0, 0, width, height)

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    customPinView.draw(canvas)

    callback(BitmapDescriptorFactory.fromBitmap(bitmap), bitmap)
  }

  /**
   * Convert a bitmap to a byte array.
   *
   * @param bitmap The bitmap to convert.
   * @return The byte array of the bitmap.
   */
  fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
  }

  /**
   * Upload the event icon to Firebase Storage.
   *
   * @param byteArray The byte array of the icon.
   * @param eventID The ID of the event.
   */
  fun uploadEventIcon(byteArray: ByteArray, eventID: String) {
    val storageRef = FirebaseStorage.getInstance().reference
    val iconRef = storageRef.child("event_icons/$eventID.png")

    val uploadTask = iconRef.putBytes(byteArray)
    uploadTask
        .addOnSuccessListener {
          // Handle successful upload, e.g., store the download URL or path
          iconRef.downloadUrl.addOnSuccessListener { uri ->
            // Save or use the URI as needed, e.g., store it in your database
            println("Icon uploaded, URI: $uri")
          }
        }
        .addOnFailureListener {
          // Handle failure
          println("Upload failed: ${it.message}")
        }
  }
}
