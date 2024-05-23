package com.github.se.gomeet

import android.content.Context
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.github.se.gomeet.model.event.getEventDateString
import com.github.se.gomeet.model.event.getEventTimeString
import com.github.se.gomeet.ui.authscreens.LoginScreen
import com.github.se.gomeet.ui.authscreens.WelcomeScreen
import com.github.se.gomeet.ui.authscreens.register.RegisterScreen
import com.github.se.gomeet.ui.mainscreens.Trends
import com.github.se.gomeet.ui.mainscreens.create.Create
import com.github.se.gomeet.ui.mainscreens.create.CreateEvent
import com.github.se.gomeet.ui.mainscreens.events.AddParticipants
import com.github.se.gomeet.ui.mainscreens.events.EditEvent
import com.github.se.gomeet.ui.mainscreens.events.Events
import com.github.se.gomeet.ui.mainscreens.events.MyEventInfo
import com.github.se.gomeet.ui.mainscreens.events.manageinvites.ManageInvites
import com.github.se.gomeet.ui.mainscreens.explore.Explore
import com.github.se.gomeet.ui.mainscreens.profile.AddFriend
import com.github.se.gomeet.ui.mainscreens.profile.EditProfile
import com.github.se.gomeet.ui.mainscreens.profile.FollowingFollowers
import com.github.se.gomeet.ui.mainscreens.profile.Notifications
import com.github.se.gomeet.ui.mainscreens.profile.OthersProfile
import com.github.se.gomeet.ui.mainscreens.profile.Profile
import com.github.se.gomeet.ui.mainscreens.profile.QRCodeScannerScreen
import com.github.se.gomeet.ui.mainscreens.profile.settings.SettingsAbout
import com.github.se.gomeet.ui.mainscreens.profile.settings.SettingsHelp
import com.github.se.gomeet.ui.mainscreens.profile.settings.SettingsPermissions
import com.github.se.gomeet.ui.mainscreens.profile.settings.SettingsScreen
import com.github.se.gomeet.ui.navigation.LOGIN_ITEMS
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.navigation.TopLevelDestination
import com.github.se.gomeet.viewmodel.AuthViewModel
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.firestore.persistentCacheSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.compose.ui.channels.ChannelsScreen
import io.getstream.chat.android.compose.ui.messages.MessagesScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory
import io.getstream.chat.android.models.InitializationState
import io.getstream.chat.android.models.User
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory
import io.getstream.result.call.doOnResult
import io.getstream.result.call.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val chatClientTag = "ChatClient"

/** Initialize the cache. */
fun initCache() {
  val cacheSize = 1024L * 1024L * 100L

  // Initialize Firestore settings
  val firestoreSettings =
      FirebaseFirestoreSettings.Builder()
          .setLocalCacheSettings(memoryCacheSettings {})
          .setLocalCacheSettings(
              persistentCacheSettings {
                // Set size to 100 MB
                setSizeBytes(cacheSize)
              })
          .build()
  Firebase.firestore.firestoreSettings = firestoreSettings

  // Enable indexing for persistent cache
  Firebase.firestore.persistentCacheIndexManager?.apply {
    // Indexing is disabled by default
    enableIndexAutoCreation()
  } ?: println("indexManager is null")
}

/**
 * Initialize the chat client.
 *
 * @param applicationContext The context of the application.
 */
fun initChatClient(applicationContext: Context): ChatClient {
  // 1 - Set up the OfflinePlugin for offline storage
  val offlinePluginFactory = StreamOfflinePluginFactory(appContext = applicationContext)
  val statePluginFactory =
      StreamStatePluginFactory(config = StatePluginConfig(), appContext = applicationContext)

  // 2 - Set up the client for API calls and with the plugin for offline storage
  // TODO Secure API KEY
  val client =
      ChatClient.Builder(getString(applicationContext, R.string.chat_api_key), applicationContext)
          .withPlugins(offlinePluginFactory, statePluginFactory)
          .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
          .build()

  return client
}

/**
 * Set up the navigation for the app.
 *
 * @param nav The NavHostController.
 * @param client The chat client.
 * @param applicationContext The application text.
 */
@Composable
fun InitNavigation(nav: NavHostController, client: ChatClient, applicationContext: Context) {
  val navAction = NavigationActions(nav)
  val clientInitialisationState by client.clientState.initializationState.collectAsState()
  val authViewModel = AuthViewModel()
  val userViewModel = UserViewModel()
  val startScreen = Route.WELCOME // The screen that gets navigated to when the app starts
  val postLoginScreen =
      Route.EXPLORE // The screen that gets navigated to after logging in/signing up
  val applicationScope = CoroutineScope(Job() + Dispatchers.Default)

  val userIdState = remember { mutableStateOf("") }
  val eventViewModel = remember { mutableStateOf(EventViewModel(null)) }
  val startDestination = remember { mutableStateOf(startScreen) }
  val chatDisconnected = remember { mutableStateOf(true) }

  if (Firebase.auth.currentUser != null) {
    userIdState.value = Firebase.auth.currentUser!!.uid
    eventViewModel.value = EventViewModel(userIdState.value)
    // If the user is logged in already, the app should start at the post-login screen
    startDestination.value = postLoginScreen
  }

  return NavHost(navController = nav, startDestination = startDestination.value) {
    composable(Route.WELCOME) {
      WelcomeScreen(
          onNavToLogin = { navAction.navigateTo(LOGIN_ITEMS[1]) },
          onNavToRegister = { navAction.navigateTo(LOGIN_ITEMS[2]) },
          onSignInSuccess = { userId: String, _: String, _: String, _: String, _: String, _: String
            ->
            val currentUser = Firebase.auth.currentUser
            if (currentUser != null) {
              val uid = currentUser.uid
              val email = currentUser.email ?: ""
              val firstName = authViewModel.signInState.value.firstNameRegister
              val lastName = authViewModel.signInState.value.lastNameRegister
              val phoneNumber = authViewModel.signInState.value.phoneNumberRegister
              val country = authViewModel.signInState.value.countryRegister
              val username = authViewModel.signInState.value.usernameRegister
              userViewModel.createUserIfNew(
                  uid, username, firstName, lastName, email, phoneNumber, country)
            }
            val user =
                User(
                    id = Firebase.auth.currentUser!!.uid,
                    name = Firebase.auth.currentUser!!.email!!)

            client.connectUser(user = user, token = client.devToken(userId)).enqueue { result ->
              if (result.isSuccess) {
                onNavToPostLogin(
                    eventViewModel,
                    userIdState,
                    TOP_LEVEL_DESTINATIONS.first { it.route == postLoginScreen },
                    navAction)
              } else {
                // Handle connection failure
                Log.e(chatClientTag, "Failed to connect user: $userId")
              }
            }
          },
          chatClientDisconnected = chatDisconnected)
    }
    composable(Route.LOGIN) {
      LoginScreen(authViewModel = authViewModel, nav = navAction) {
        onNavToPostLogin(
            eventViewModel,
            userIdState,
            TOP_LEVEL_DESTINATIONS.first { it.route == postLoginScreen },
            navAction)
      }
    }
    composable(Route.REGISTER) {
      RegisterScreen(client, navAction, authViewModel, userViewModel) {
        onNavToPostLogin(
            eventViewModel,
            userIdState,
            TOP_LEVEL_DESTINATIONS.first { it.route == postLoginScreen },
            navAction)
      }
    }
    composable(Route.EXPLORE) { Explore(navAction, eventViewModel.value) }
    composable(Route.EVENTS) {
      Events(userIdState.value, navAction, userViewModel, eventViewModel.value)
    }
    composable(Route.TRENDS) {
      Trends(userIdState.value, navAction, userViewModel, eventViewModel.value)
    }
    composable(Route.CREATE) {
      userIdState.value = Firebase.auth.currentUser!!.uid
      Create(navAction)
    }
    composable(Route.NOTIFICATIONS) {
      Notifications(navAction, currentUserID = userIdState.value, userViewModel)
    }

    composable(Route.PROFILE) {
      Profile(navAction, userId = userIdState.value, userViewModel, eventViewModel.value)
    }
    composable(
        route = Route.OTHERS_PROFILE,
        arguments = listOf(navArgument("uid") { type = NavType.StringType })) {
          OthersProfile(
              navAction, it.arguments?.getString("uid") ?: "", userViewModel, eventViewModel.value)
        }
    composable(Route.PRIVATE_CREATE) {
      CreateEvent(navAction, eventViewModel.value, true, userViewModel)
    }
    composable(Route.PUBLIC_CREATE) {
      CreateEvent(navAction, eventViewModel.value, false, userViewModel)
    }

    composable(
        route = Route.ADD_PARTICIPANTS,
        arguments = listOf(navArgument("eventId") { type = NavType.StringType })) { entry ->
          val eventId = entry.arguments?.getString("eventId") ?: ""
          AddParticipants(
              nav = navAction,
              userId = userIdState.value,
              userViewModel = userViewModel,
              eventId = eventId)
        }

    composable(
        route = Route.MANAGE_INVITES,
        arguments = listOf(navArgument("eventId") { type = NavType.StringType })) { entry ->
          val eventId = entry.arguments?.getString("eventId") ?: ""

          ManageInvites(userIdState.value, eventId, navAction, userViewModel, eventViewModel.value)
        }

    composable(
        route = Route.EVENT_INFO,
        arguments =
            listOf(
                navArgument("eventId") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType },
                navArgument("time") { type = NavType.StringType },
                navArgument("organizer") { type = NavType.StringType },
                navArgument("rating") { type = NavType.FloatType },
                navArgument("description") { type = NavType.StringType },
                navArgument("latitude") { type = NavType.FloatType }, // Change to DoubleType
                navArgument("longitude") { type = NavType.FloatType } // Change to DoubleType
                )) { entry ->
          val eventId = entry.arguments?.getString("eventId") ?: ""
          val title = entry.arguments?.getString("title") ?: ""
          val date = entry.arguments?.getString("date") ?: ""
          val time = entry.arguments?.getString("time") ?: ""
          val organizer = entry.arguments?.getString("organizer") ?: ""
          val rating = entry.arguments?.getFloat("rating") ?: 0.0
          val description = entry.arguments?.getString("description") ?: ""
          val latitude = entry.arguments?.getFloat("latitude") ?: 0.0
          val longitude = entry.arguments?.getFloat("longitude") ?: 0.0
          val loc = LatLng(latitude.toDouble(), longitude.toDouble())

          MyEventInfo(
              navAction,
              title,
              eventId,
              date,
              time,
              organizer,
              rating.toDouble(),
              description,
              loc,
              userViewModel,
              eventViewModel.value)
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
                  chatClientTag,
                  "Sign in to chat works, $id, and ${Firebase.auth.currentUser!!.uid}")
              client
                  .createChannel(
                      channelType = "messaging",
                      channelId = "", // Let the API generate an ID
                      memberIds = listOf(id, Firebase.auth.currentUser!!.uid),
                      extraData = emptyMap())
                  .enqueue { res ->
                    res.onError { error ->
                      Log.e(chatClientTag, "Create channel failed, Error: $error")
                    }
                    res.onSuccess { result ->
                      Log.d(chatClientTag, "Create channel success")
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
                      onBackPressed = { navAction.goBack() })
                }
              }
            }
            InitializationState.INITIALIZING -> {
              Log.d(chatClientTag, "Sign in to Chat is initializing")
              Text(text = "Initializing...")
            }
            InitializationState.NOT_INITIALIZED -> {
              Log.e(chatClientTag, "Sign in to Chat doesn't work, not initialized")
              Text(text = "Not initialized...")
            }
          }
        }

    composable(Route.SETTINGS) {
      SettingsScreen(navAction) {
        logOut(
            navAction,
            LOGIN_ITEMS.first { it.route == startScreen },
            eventViewModel,
            userIdState,
            authViewModel,
            client,
            chatDisconnected,
            applicationScope)
      }
    }
    composable(Route.ABOUT) { SettingsAbout(navAction) }
    composable(Route.HELP) { SettingsHelp(navAction) }
    composable(Route.PERMISSIONS) { SettingsPermissions(navAction) }
    composable(Route.EDIT_PROFILE) { EditProfile(nav = navAction) }
    composable(
        route = Route.FOLLOWERS,
        arguments = listOf(navArgument("uid") { type = NavType.StringType })) {
          FollowingFollowers(
              navAction,
              it.arguments?.getString("uid") ?: "",
              userIdState.value,
              userViewModel,
              false)
        }
    composable(
        route = Route.FOLLOWING,
        arguments = listOf(navArgument("uid") { type = NavType.StringType })) {
          FollowingFollowers(
              navAction,
              it.arguments?.getString("uid") ?: "",
              userIdState.value,
              userViewModel,
              true)
        }
    composable(Route.MESSAGE_CHANNELS) {
      ChatTheme {
        ChannelsScreen(
            onBackPressed = { navAction.goBack() },
            onHeaderAvatarClick = { navAction.navigateToScreen(Route.PROFILE) },
            onHeaderActionClick = {
              navAction.navigateToScreen(Route.FOLLOWING.replace("{uid}", userIdState.value))
            },
            onItemClick = { c -> navAction.navigateToScreen(Route.CHANNEL.replace("{id}", c.id)) })
      }
    }
    composable(
        route = Route.CHANNEL,
        arguments = listOf(navArgument("id") { type = NavType.StringType })) {
          val id = it.arguments?.getString("id") ?: ""
          ChatTheme {
            Log.d(chatClientTag, "ID is: $id")
            MessagesScreen(
                viewModelFactory =
                    MessagesViewModelFactory(
                        context = applicationContext,
                        channelId = "messaging:${id}",
                        messageLimit = 30),
                onBackPressed = { navAction.goBack() })
          }
        }
    composable(route = Route.ADD_FRIEND) { AddFriend(navAction, userViewModel) }
    composable(route = Route.SCAN) { backStackEntry ->
      QRCodeScannerScreen(
          onQRCodeScanned = { uid ->
            navAction.navigateToScreen(Route.OTHERS_PROFILE.replace("{uid}", uid))
          },
          nav = navAction)
    }

    composable(
        route = Route.EDIT_EVENT,
        arguments = listOf(navArgument("eventId") { type = NavType.StringType })) { entry ->
          val eventId = entry.arguments?.getString("eventId") ?: ""

          EditEvent(nav = navAction, eventViewModel = eventViewModel.value, eventId = eventId) {
              updatedEvent ->
            // Navigate to the updated event info
            navAction.navigateToEventInfo(
                eventId = updatedEvent.eventID,
                title = updatedEvent.title,
                date = getEventDateString(updatedEvent.date),
                time = getEventTimeString(updatedEvent.time),
                organizer = updatedEvent.creator,
                rating = 0.0,
                description = updatedEvent.description,
                loc = LatLng(updatedEvent.location.latitude, updatedEvent.location.longitude))
          }
        }
  }
}

/**
 * Function to be called upon a successful sign in/register
 *
 * @param eventViewModel The event view model state.
 * @param userIdState The user ID state.
 * @param postLogin The screen that will be navigated to upon logging in/signing up.
 * @param navigationActions The navigation actions.
 */
private fun onNavToPostLogin(
    eventViewModel: MutableState<EventViewModel>,
    userIdState: MutableState<String>,
    postLogin: TopLevelDestination,
    navigationActions: NavigationActions
) {
  userIdState.value = Firebase.auth.currentUser!!.uid
  eventViewModel.value = EventViewModel(userIdState.value)
  navigationActions.navigateTo(postLogin)
}

/**
 * Log out the user.
 *
 * @param navigationActions The navigation actions.
 * @param startScreen The screen that will be navigated to after logging out.
 * @param eventViewModel The event view model state.
 * @param userIdState The user ID state.
 * @param authViewModel The authentication view model.
 * @param client The chat client.
 * @param chatDisconnected Whether the chat is disconnected fully or not.
 * @param scope The coroutine scope.
 */
private fun logOut(
    navigationActions: NavigationActions,
    startScreen: TopLevelDestination,
    eventViewModel: MutableState<EventViewModel>,
    userIdState: MutableState<String>,
    authViewModel: AuthViewModel,
    client: ChatClient,
    chatDisconnected: MutableState<Boolean>,
    scope: CoroutineScope
) {
  navigationActions.navigateTo(startScreen)
  userIdState.value = ""
  eventViewModel.value = EventViewModel()
  authViewModel.signOut()
  chatDisconnected.value = false
  Log.d(chatClientTag, "Starting full disconnect")
  scope.launch {
    try {
      client
          .disconnect(false)
          .doOnResult(scope) {
            Log.d(chatClientTag, "Full disconnect complete")
            chatDisconnected.value = true
          }
          .await()
    } catch (e: Exception) {
      Log.e(chatClientTag, "Error during disconnect: ${e.message}")
    }
  }
}

/**
 * Call this function in main (as the first thing after onCreate()) to use the Firebase Emulator
 * Suite for purely local testing.
 */
fun debug() {
  val androidLocalhost = "10.0.2.2"
  Firebase.firestore.useEmulator(androidLocalhost, 8080)
  Firebase.auth.useEmulator(androidLocalhost, 9099)
  Firebase.storage.useEmulator(androidLocalhost, 9199)
}
