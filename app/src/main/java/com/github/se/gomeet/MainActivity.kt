package com.github.se.gomeet

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.se.gomeet.ui.authscreens.LoginScreen
import com.github.se.gomeet.ui.authscreens.RegisterScreen
import com.github.se.gomeet.ui.authscreens.WelcomeScreen
import com.github.se.gomeet.ui.mainscreens.EventInfo
import com.github.se.gomeet.ui.mainscreens.Events
import com.github.se.gomeet.ui.mainscreens.Explore
import com.github.se.gomeet.ui.mainscreens.Profile
import com.github.se.gomeet.ui.mainscreens.Trends
import com.github.se.gomeet.ui.mainscreens.create.AddParticipants
import com.github.se.gomeet.ui.mainscreens.create.Create
import com.github.se.gomeet.ui.mainscreens.create.CreateEvent
import com.github.se.gomeet.ui.mainscreens.profile.Notifications
import com.github.se.gomeet.ui.mainscreens.profile.OthersProfile
import com.github.se.gomeet.ui.mainscreens.profile.SettingsScreen
import com.github.se.gomeet.ui.navigation.LOGIN_ITEMS
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.GoMeetTheme
import com.github.se.gomeet.ui.theme.SetStatusBarColor
import com.github.se.gomeet.viewmodel.AuthViewModel
import com.github.se.gomeet.viewmodel.EventInviteViewModel
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.compose.ui.messages.MessagesScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory
import io.getstream.chat.android.models.InitializationState
import io.getstream.chat.android.models.User
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    MapsInitializer.initialize(this)

    // 1 - Set up the OfflinePlugin for offline storage
    val offlinePluginFactory = StreamOfflinePluginFactory(appContext = applicationContext)
    val statePluginFactory =
        StreamStatePluginFactory(config = StatePluginConfig(), appContext = this)

    // 2 - Set up the client for API calls and with the plugin for offline storage
    // TODO Secure API KEY
    val client =
        ChatClient.Builder(getString(R.string.chat_api_key), applicationContext)
            .withPlugins(offlinePluginFactory, statePluginFactory)
            .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
            .build()

    setContent {
      val clientInitialisationState by client.clientState.initializationState.collectAsState()

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
                        Firebase.auth.currentUser!!.uid, Firebase.auth.currentUser!!.email!!)
                    val user =
                        User(
                            id = Firebase.auth.currentUser!!.uid,
                            name =
                                Firebase.auth.currentUser!!
                                    .email!!) // TODO: Add Profile Picture to User

                    client.connectUser(user = user, token = client.devToken(userId)).enqueue {
                        result ->
                      if (result.isSuccess) {
                        NavigationActions(nav)
                            .navigateTo(
                                TOP_LEVEL_DESTINATIONS.first { it.route == Route.CREATE },
                                clearBackStack = true)
                      } else {
                        // Handle connection failure
                        Log.e("ChatClient", "Failed to connect user: $userId")
                      }
                    }
                  })
            }
            composable(Route.LOGIN) {
              LoginScreen(authViewModel) {
                NavigationActions(nav)
                    .navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == Route.CREATE })
              }
            }
            composable(Route.REGISTER) {
              RegisterScreen(client, authViewModel, userViewModel) {
                NavigationActions(nav)
                    .navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == Route.CREATE })
              }
            }
            composable(Route.EXPLORE) { Explore(navAction, EventViewModel()) }
            composable(Route.EVENTS) { Events(navAction, EventViewModel()) }
            composable(Route.TRENDS) { Trends(navAction) }
            composable(Route.CREATE) { Create(navAction) }

            composable(Route.NOTIFICATIONS) { Notifications(navAction)}

            composable(Route.PROFILE) { Profile(navAction) }
            composable(
                route = Route.OTHERS_PROFILE,
                arguments = listOf(navArgument("uid") { type = NavType.StringType })) {
                  OthersProfile(navAction, it.arguments?.getString("uid") ?: "")
                }

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
                  val rating = entry.arguments?.getDouble("rating") ?: 0.0
                  val description = entry.arguments?.getString("description") ?: ""
                  val latitude = entry.arguments?.getFloat("latitude") ?: 0.0
                  val longitude = entry.arguments?.getFloat("longitude") ?: 0.0
                  val loc = LatLng(latitude.toDouble(), longitude.toDouble())

                  EventInfo(
                      NavigationActions(nav),
                      title,
                      date,
                      time,
                      organizer,
                      rating,
                      painterResource(id = R.drawable.chess_demo),
                      description,
                      loc)
                }
            composable(
                route = Route.MESSAGE,
                arguments = listOf(navArgument("id") { type = NavType.StringType })) {
                  val id = it.arguments?.getString("id") ?: ""
                  val success = remember { mutableStateOf(false) }
                  val channelId = remember { mutableStateOf("") }

                  when (clientInitialisationState) {
                    InitializationState.COMPLETE -> {
                      Log.d(
                          "Sign in",
                          "Sign in to chat works, $id, and ${Firebase.auth.currentUser!!.uid}")
                      client
                          .createChannel(
                              channelType = "messaging",
                              channelId = "", // Let the API generate an ID
                              memberIds = listOf(id, Firebase.auth.currentUser!!.uid),
                              extraData = emptyMap())
                          .enqueue { res ->
                            res.onError { error ->
                              Log.d("Creating channel", "Failed, Error: $error")
                            }
                            res.onSuccess { result ->
                              Log.d("Creating channel", "Success !")
                              success.value = true
                              channelId.value =
                                  "messaging:${result.id}" // Correct format "channelType:channelId"
                            }
                          }

                      if (success.value) {
                        ChatTheme {
                          MessagesScreen(
                              viewModelFactory =
                                  MessagesViewModelFactory(
                                      context = applicationContext,
                                      channelId = channelId.value, // Make sure this is in
                                      // "channelType:channelId" format
                                      messageLimit = 30),
                              onBackPressed = { NavigationActions(nav).goBack() })
                        }
                      }
                    }
                    InitializationState.INITIALIZING -> {
                      Log.d("Initializing", "Sign in to Chat is initializing")
                      Text(text = "Initializing...")
                    }
                    InitializationState.NOT_INITIALIZED -> {
                      Log.d("Not initialized", "Sign in to Chat doesn't work, not initialized")
                      Text(text = "Not initialized...")
                    }
                  }
                }

            composable(Route.SETTINGS) { SettingsScreen(navAction) }
          }
        }
      }
    }
  }
}
