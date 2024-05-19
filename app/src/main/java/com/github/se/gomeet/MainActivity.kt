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

/** The main activity of the application. */
class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // debug()
    MapsInitializer.initialize(this)
    initCache()
    val client = initChatClient(applicationContext)

    setContent {
      GoMeetTheme {
        SetStatusBarColor(color = MaterialTheme.colorScheme.background)
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          val nav = rememberNavController()
          InitNavigation(nav = nav, client = client, applicationContext = applicationContext)
        }
      }
    }
  }
}
