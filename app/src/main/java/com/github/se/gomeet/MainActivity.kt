package com.github.se.gomeet

import EventInfoScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.se.gomeet.ui.authscreens.LoginScreen
import com.github.se.gomeet.ui.authscreens.RegisterScreen
import com.github.se.gomeet.ui.authscreens.WelcomeScreen
import com.github.se.gomeet.ui.mainscreens.Events
import com.github.se.gomeet.ui.mainscreens.Explore
import com.github.se.gomeet.ui.mainscreens.Profile
import com.github.se.gomeet.ui.mainscreens.Trends
import com.github.se.gomeet.ui.mainscreens.create.AddParticipants
import com.github.se.gomeet.ui.mainscreens.create.Create
import com.github.se.gomeet.ui.mainscreens.create.CreateEvent
import com.github.se.gomeet.ui.mainscreens.profile.OthersProfile
import com.github.se.gomeet.ui.navigation.LOGIN_ITEMS
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.GoMeetTheme
import com.github.se.gomeet.ui.theme.SetStatusBarColor
import com.github.se.gomeet.viewmodel.AuthViewModel
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    MapsInitializer.initialize(this)

    setContent {
      GoMeetTheme {
        SetStatusBarColor(color = MaterialTheme.colorScheme.background)
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          val userIdState = remember { mutableStateOf<String?>(null) }
          val nav = rememberNavController()
          val authViewModel = AuthViewModel()
          val userViewModel = UserViewModel()
          val navAction = NavigationActions(nav)
          NavHost(navController = nav, startDestination = Route.WELCOME) {
            composable(Route.WELCOME) {
              WelcomeScreen(
                  onNavToLogin = { NavigationActions(nav).navigateTo(LOGIN_ITEMS[1]) },
                  onNavToRegister = { NavigationActions(nav).navigateTo(LOGIN_ITEMS[2]) },
                  onSignInSuccess = { userId ->
                    userIdState.value = userId
                    userViewModel.createUserIfNew(
                        Firebase.auth.currentUser!!.uid,
                        Firebase.auth.currentUser!!.email!!) // TODO: currently username = email
                    NavigationActions(nav)
                        .navigateTo(
                            TOP_LEVEL_DESTINATIONS.first { it.route == Route.CREATE },
                            clearBackStack = true)
                  })
            }
            composable(Route.LOGIN) {
              LoginScreen(authViewModel) {
                NavigationActions(nav)
                    .navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == Route.CREATE })
              }
            }
            composable(Route.REGISTER) {
              RegisterScreen(authViewModel, userViewModel) {
                NavigationActions(nav)
                    .navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == Route.CREATE })
              }
            }
            composable(Route.EXPLORE) { Explore(navAction, EventViewModel()) }
            composable(Route.EVENTS) { Events(navAction, EventViewModel()) }
            composable(Route.TRENDS) { Trends(navAction) }
            composable(Route.CREATE) { Create(navAction) }
            composable(Route.PROFILE) { Profile(navAction) }
            composable(Route.OTHERS_PROFILE) { OthersProfile(navAction) }
            composable(Route.PRIVATE_CREATE) {
              CreateEvent(navAction, EventViewModel(userIdState.value), true)
            }
            composable(Route.PUBLIC_CREATE) {
              CreateEvent(navAction, EventViewModel(userIdState.value), false)
            }
            composable(Route.ADD_PARTICIPANTS) { AddParticipants(navAction) }
            composable(
                route = Route.EVENT_INFO,
                arguments =
                    listOf(
                        navArgument("title") { type = NavType.StringType },
                        navArgument("date") { type = NavType.StringType },
                        navArgument("time") { type = NavType.StringType },
                        navArgument("organizer") { type = NavType.StringType },
                        navArgument("rating") { type = NavType.FloatType },
                        navArgument("description") { type = NavType.StringType },
                        navArgument("latitude") {
                          type = NavType.FloatType
                        }, // Change to DoubleType
                        navArgument("longitude") {
                          type = NavType.FloatType
                        } // Change to DoubleType
                        )) { entry ->
                  val title = entry.arguments?.getString("title") ?: ""
                  val date = entry.arguments?.getString("date") ?: ""
                  val time = entry.arguments?.getString("time") ?: ""
                  val organizer = entry.arguments?.getString("organizer") ?: ""
                  val rating = entry.arguments?.getFloat("rating") ?: 0f
                  val description = entry.arguments?.getString("description") ?: ""
                  val latitude = entry.arguments?.getFloat("latitude") ?: 0.0
                  val longitude = entry.arguments?.getFloat("longitude") ?: 0.0
                  val loc = LatLng(latitude.toDouble(), longitude.toDouble())

                  EventInfoScreen(nav)
                }
          }
        }
      }
    }
  }
}
