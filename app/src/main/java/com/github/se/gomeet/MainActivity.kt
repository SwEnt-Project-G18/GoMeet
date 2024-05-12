package com.github.se.gomeet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.ui.theme.GoMeetTheme
import com.github.se.gomeet.ui.theme.SetStatusBarColor
import com.google.android.gms.maps.MapsInitializer
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/** The main activity of the application. */
class MainActivity : ComponentActivity() {

  private lateinit var db: FirebaseFirestore

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    db = Firebase.firestore

    MapsInitializer.initialize(this)
    initCache(db)
    val client = initChatClient(applicationContext)

    setContent {
      GoMeetTheme {
        SetStatusBarColor(color = MaterialTheme.colorScheme.background)
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          val nav = rememberNavController()
          InitNavigation(
              nav = nav, db = db, client = client, applicationContext = applicationContext)
        }
      }
    }
  }
}
