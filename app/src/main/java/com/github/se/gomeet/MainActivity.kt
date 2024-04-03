package com.github.se.gomeet

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.ui.WelcomeScreen
import com.github.se.gomeet.ui.create.Create
import com.github.se.gomeet.ui.events.Events
import com.github.se.gomeet.ui.explore.Explore
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.profile.Profile
import com.github.se.gomeet.ui.theme.GoMeetTheme
import com.github.se.gomeet.ui.trending.Trending

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      GoMeetTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          val userIdState = remember { mutableStateOf<String?>(null) }
          if (userIdState.value == null) {
            WelcomeScreen(onSignInSuccess = { userId -> userIdState.value = userId })
          } else {
            val nav = rememberNavController()
            Log.d("SignIn", "Signed in state changed: ${userIdState.value}")
            NavHost(navController = nav, startDestination = Route.EXPLORE) {
              composable(Route.EXPLORE) { Explore(NavigationActions(nav)) }
              composable(Route.EVENTS) { Events(NavigationActions(nav)) }
              composable(Route.TRENDING) { Trending(NavigationActions(nav)) }
              composable(Route.CREATE) { Create(NavigationActions(nav)) }
              composable(Route.PROFILE) { Profile(NavigationActions(nav)) }
            }
          }
        }
      }
    }
  }
}
