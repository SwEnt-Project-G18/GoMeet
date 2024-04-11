package com.github.se.gomeet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.ui.authscreens.LoginScreen
import com.github.se.gomeet.ui.authscreens.RegisterScreen
import com.github.se.gomeet.ui.authscreens.WelcomeScreen
import com.github.se.gomeet.ui.mainscreens.Events
import com.github.se.gomeet.ui.mainscreens.Explore
import com.github.se.gomeet.ui.mainscreens.Profile
import com.github.se.gomeet.ui.mainscreens.Trends
import com.github.se.gomeet.ui.mainscreens.create.Create
import com.github.se.gomeet.ui.mainscreens.create.CreateEvent
import com.github.se.gomeet.ui.navigation.LOGIN_ITEMS
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.GoMeetTheme
import com.github.se.gomeet.ui.theme.SetStatusBarColor
import com.github.se.gomeet.viewmodel.AuthViewModel
import com.github.se.gomeet.viewmodel.EventViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      GoMeetTheme {
        SetStatusBarColor(color = MaterialTheme.colorScheme.background)
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          val userIdState = remember { mutableStateOf<String?>(null) }
          val nav = rememberNavController()
          val viewModel = AuthViewModel()
          val navAction = NavigationActions(nav)
          NavHost(navController = nav, startDestination = Route.WELCOME) {
            composable(Route.WELCOME) {
              WelcomeScreen(
                  onNavToLogin = { NavigationActions(nav).navigateTo(LOGIN_ITEMS[1]) },
                  onNavToRegister = { NavigationActions(nav).navigateTo(LOGIN_ITEMS[2]) },
                  onSignInSuccess = { userId ->
                    userIdState.value = userId
                    NavigationActions(nav)
                        .navigateTo(TOP_LEVEL_DESTINATIONS[1], clearBackStack = true)
                  })
            }
            composable(Route.LOGIN) {
              LoginScreen(viewModel) {
                NavigationActions(nav).navigateTo(TOP_LEVEL_DESTINATIONS[1])
              }
            }
            composable(Route.REGISTER) {
              RegisterScreen(viewModel) {
                NavigationActions(nav).navigateTo(TOP_LEVEL_DESTINATIONS[1])
              }
            }
            composable(Route.EXPLORE) { Explore(navAction) }
            composable(Route.EVENTS) { Events(navAction, EventViewModel()) }
            composable(Route.TRENDS) { Trends(navAction) }
            composable(Route.CREATE) { Create(navAction) }
            composable(Route.PROFILE) { Profile(navAction) }
            composable(Route.PRIVATE_CREATE) {
              CreateEvent(navAction, EventViewModel(userIdState.value), true)
            }
            composable(Route.PUBLIC_CREATE) {
              CreateEvent(navAction, EventViewModel(userIdState.value), false)
            }
          }
        }
      }
    }
  }
}
