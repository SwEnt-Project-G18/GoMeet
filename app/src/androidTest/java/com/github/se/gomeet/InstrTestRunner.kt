package com.github.se.gomeet

import android.os.Bundle
import androidx.test.runner.AndroidJUnitRunner
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class InstrTestRunner : AndroidJUnitRunner() {
  override fun onCreate(arguments: Bundle) {
    super.onCreate(arguments)
    // Code to execute before all tests
    setupGlobalTestEnvironment()
  }

  override fun finish(resultCode: Int, results: Bundle) {
    // Code to execute after all tests
    tearDownGlobalTestEnvironment()
    super.finish(resultCode, results)
  }

  private fun setupGlobalTestEnvironment() {
    Firebase.auth.useEmulator("10.0.2.2", 9099)
    Firebase.storage.useEmulator("10.0.2.2.", 9199)
    Firebase.firestore.useEmulator("10.0.2.2", 8080)
  }

  private fun tearDownGlobalTestEnvironment() {}
}
