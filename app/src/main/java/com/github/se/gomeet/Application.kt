package com.github.se.gomeet

import android.app.Application
import com.google.android.gms.maps.MapsInitializer

class MyApp : Application() {
  override fun onCreate() {
    super.onCreate()
    // Initialize Google Maps SDK
    MapsInitializer.initialize(this)
  }
}
