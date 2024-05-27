package com.github.se.gomeet

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import com.github.se.gomeet.ui.mainscreens.notifications.Notifications
import com.github.se.gomeet.ui.mainscreens.profile.AddFriend
import com.github.se.gomeet.ui.mainscreens.profile.EditProfile
import com.github.se.gomeet.ui.mainscreens.profile.FollowingFollowers
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "InitFunctions"

/** Initialize the cache. */
fun initCache() {
  val cacheSize = 1024L * 1024L * 100L

  // Initialise Firestore settings
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
  val startScreen = Route.WELCOME // The screen that gets navigated to when the app starts
  val postLoginScreen =
      Route.EXPLORE // The screen that gets navigated to after logging in/signing up
  val applicationScope = CoroutineScope(Job() + Dispatchers.Default)

  val eventViewModel = remember { mutableStateOf(EventViewModel(null)) }
  val userViewModel = remember { mutableStateOf(UserViewModel(null)) }
  val startDestination = remember { mutableStateOf(startScreen) }
  val chatDisconnected = remember { mutableStateOf(true) }

  if (Firebase.auth.currentUser != null) {
    val uid = Firebase.auth.currentUser!!.uid
    eventViewModel.value = EventViewModel(uid)
    userViewModel.value = UserViewModel(uid)
    // If the user is logged in already, the app should start at the post-login screen and we need
    // to initialise the chat client
    startDestination.value = postLoginScreen
    connectChatClient(
        client,
        false,
        navAction,
        postLoginScreen,
        eventViewModel,
        userViewModel,
        applicationContext)
  }

  return NavHost(navController = nav, startDestination = startDestination.value) {
    composable(Route.WELCOME) {
      WelcomeScreen(
          onNavToLogin = { navAction.navigateTo(LOGIN_ITEMS[1]) },
          onNavToRegister = { navAction.navigateTo(LOGIN_ITEMS[2]) },
          onSignInSuccess = {
              userId: String,
              email: String,
              firstName: String,
              lastName: String,
              phone: String,
              username: String ->
            val currentUser = Firebase.auth.currentUser
            if (currentUser != null) {
              val country = authViewModel.signInState.value.countryRegister
              userViewModel.value.createUserIfNew(
                  userId, username, firstName, lastName, email, phone, country)
            }
            connectChatClient(
                client,
                true,
                navAction,
                postLoginScreen,
                eventViewModel,
                userViewModel,
                applicationContext)
          },
          chatClientDisconnected = chatDisconnected)
    }
    composable(Route.LOGIN) {
      LoginScreen(authViewModel = authViewModel, nav = navAction) {
        connectChatClient(
            client,
            true,
            navAction,
            postLoginScreen,
            eventViewModel,
            userViewModel,
            applicationContext)
        //        onNavToPostLogin(
        //            eventViewModel,
        //            userViewModel,
        //            TOP_LEVEL_DESTINATIONS.first { it.route == postLoginScreen },
        //            navAction)
      }
    }
    composable(Route.REGISTER) {
      RegisterScreen(navAction, authViewModel, userViewModel.value) {
        connectChatClient(
            client,
            true,
            navAction,
            postLoginScreen,
            eventViewModel,
            userViewModel,
            applicationContext)
      }
    }
    composable(Route.EXPLORE) { Explore(navAction, eventViewModel.value) }
    composable(Route.EVENTS) { Events(navAction, userViewModel.value, eventViewModel.value) }
    composable(Route.TRENDS) { Trends(navAction, userViewModel.value, eventViewModel.value) }
    composable(Route.CREATE) { Create(navAction) }
    composable(Route.NOTIFICATIONS) { Notifications(navAction, userViewModel.value) }

    composable(Route.PROFILE) { Profile(navAction, userViewModel.value, eventViewModel.value) }
    composable(
        route = Route.OTHERS_PROFILE,
        arguments = listOf(navArgument("uid") { type = NavType.StringType })) {
          OthersProfile(
              navAction,
              it.arguments?.getString("uid") ?: "",
              userViewModel.value,
              eventViewModel.value)
        }
    composable(Route.PRIVATE_CREATE) {
      CreateEvent(navAction, eventViewModel.value, true, userViewModel.value)
    }
    composable(Route.PUBLIC_CREATE) {
      CreateEvent(navAction, eventViewModel.value, false, userViewModel.value)
    }

    composable(
        route = Route.ADD_PARTICIPANTS,
        arguments = listOf(navArgument("eventId") { type = NavType.StringType })) { entry ->
          val eventId = entry.arguments?.getString("eventId") ?: ""
          AddParticipants(nav = navAction, userViewModel = userViewModel.value, eventId = eventId)
        }

    composable(
        route = Route.MANAGE_INVITES,
        arguments = listOf(navArgument("eventId") { type = NavType.StringType })) { entry ->
          val eventId = entry.arguments?.getString("eventId") ?: ""

          ManageInvites(eventId, navAction, userViewModel.value, eventViewModel.value)
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
                navArgument("rating") { type = NavType.LongType },
                navArgument("description") { type = NavType.StringType },
                navArgument("latitude") { type = NavType.FloatType }, // Change to DoubleType
                navArgument("longitude") { type = NavType.FloatType } // Change to DoubleType
                )) { entry ->
          val eventId = entry.arguments?.getString("eventId") ?: ""
          val title = entry.arguments?.getString("title") ?: ""
          val date = entry.arguments?.getString("date") ?: ""
          val time = entry.arguments?.getString("time") ?: ""
          val organizer = entry.arguments?.getString("organizer") ?: ""
          val rating: Long = entry.arguments?.getLong("rating") ?: 0
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
              rating,
              description,
              loc,
              userViewModel.value,
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
                  TAG,
                  "ChatClient: Sign in to chat works, $id, and ${userViewModel.value.currentUID}")
              client
                  .createChannel(
                      channelType = "messaging",
                      channelId = "", // Let the API generate an ID
                      memberIds = listOf(id, Firebase.auth.currentUser!!.uid),
                      extraData = emptyMap())
                  .enqueue { res ->
                    res.onError { error ->
                      Log.e(TAG, "ChatClient: create channel failed, Error: $error")
                    }
                    res.onSuccess { result ->
                      Log.d(TAG, "ChatClient: Create channel success")
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
              Log.d(TAG, "ChatClient: Sign in to Chat is initialising")
              Text(text = "Initialising...")
            }
            InitializationState.NOT_INITIALIZED -> {
              Log.e(TAG, "ChatClient: Sign in to Chat doesn't work, not initialised")
              Text(text = "Not initialised...")
            }
          }
        }

    composable(Route.SETTINGS) {
      SettingsScreen(navAction) {
        logOut(
            navAction,
            LOGIN_ITEMS.first { it.route == startScreen },
            eventViewModel,
            userViewModel,
            authViewModel,
            client,
            chatDisconnected,
            applicationScope)
      }
    }
    composable(Route.ABOUT) { SettingsAbout(navAction) }
    composable(Route.HELP) { SettingsHelp(navAction) }
    composable(Route.PERMISSIONS) { SettingsPermissions(navAction) }
    composable(Route.EDIT_PROFILE) { EditProfile(nav = navAction, userViewModel.value) }
    composable(
        route = Route.FOLLOWERS,
        arguments = listOf(navArgument("uid") { type = NavType.StringType })) {
          FollowingFollowers(
              navAction, it.arguments?.getString("uid") ?: "", userViewModel.value, false)
        }
    composable(
        route = Route.FOLLOWING,
        arguments = listOf(navArgument("uid") { type = NavType.StringType })) {
          FollowingFollowers(
              navAction, it.arguments?.getString("uid") ?: "", userViewModel.value, true)
        }
    composable(Route.MESSAGE_CHANNELS) {
      ChatTheme {
        ChannelsScreen(
            onBackPressed = { navAction.goBack() },
            onHeaderAvatarClick = { navAction.navigateToScreen(Route.PROFILE) },
            onHeaderActionClick = {
              navAction.navigateToScreen(
                  Route.FOLLOWING.replace("{uid}", userViewModel.value.currentUID ?: ""))
            },
            onItemClick = { c -> navAction.navigateToScreen(Route.CHANNEL.replace("{id}", c.id)) })
      }
    }
    composable(
        route = Route.CHANNEL,
        arguments = listOf(navArgument("id") { type = NavType.StringType })) {
          val id = it.arguments?.getString("id") ?: ""
          ChatTheme {
            Log.d(TAG, "ChatClient: ID is $id")
            MessagesScreen(
                viewModelFactory =
                    MessagesViewModelFactory(
                        context = applicationContext,
                        channelId = "messaging:${id}",
                        messageLimit = 30),
                onBackPressed = { navAction.goBack() })
          }
        }
    composable(route = Route.ADD_FRIEND) { AddFriend(navAction, userViewModel.value) }
    composable(route = Route.SCAN) { backStackEntry -> QRCodeScannerScreen(nav = navAction, eventViewModel.value) }

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
                date = updatedEvent.getDateString(),
                time = updatedEvent.getTimeString(),
                organizer = updatedEvent.creator,
                rating = updatedEvent.ratings[eventViewModel.value.currentUID ?: ""] ?: 0,
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
 * @param userViewModel The user view model state.
 * @param postLogin The screen that will be navigated to upon logging in/signing up.
 * @param navigationActions The navigation actions.
 */
private fun onNavToPostLogin(
    eventViewModel: MutableState<EventViewModel>,
    userViewModel: MutableState<UserViewModel>,
    postLogin: TopLevelDestination,
    navigationActions: NavigationActions
) {
  val uid = Firebase.auth.currentUser!!.uid
  eventViewModel.value = EventViewModel(uid)
  userViewModel.value = UserViewModel(uid)
  navigationActions.navigateTo(postLogin)
}

/**
 * Log out the user.
 *
 * @param navigationActions The navigation actions.
 * @param startScreen The screen that will be navigated to after logging out.
 * @param eventViewModel The event view model state.
 * @param userViewModel The user view model state.
 * @param authViewModel The authentication view model.
 * @param client The chat client.
 * @param chatDisconnected Whether the chat is disconnected fully or not.
 * @param scope The coroutine scope.
 */
private fun logOut(
    navigationActions: NavigationActions,
    startScreen: TopLevelDestination,
    eventViewModel: MutableState<EventViewModel>,
    userViewModel: MutableState<UserViewModel>,
    authViewModel: AuthViewModel,
    client: ChatClient,
    chatDisconnected: MutableState<Boolean>,
    scope: CoroutineScope
) {
  navigationActions.navigateTo(startScreen)
  eventViewModel.value = EventViewModel()
  userViewModel.value = UserViewModel()
  authViewModel.signOut()
  chatDisconnected.value = false
  Log.d(TAG, "ChatClient: Starting full disconnect")
  scope.launch {
    try {
      client
          .disconnect(false)
          .doOnResult(scope) {
            Log.d(TAG, "ChatClient: Full disconnect complete")
            chatDisconnected.value = true
          }
          .await()
    } catch (e: Exception) {
      Log.e(TAG, "ChatClient: Error during disconnect", e)
    }
  }
}

/**
 * Connect the chat client to the user.
 *
 * @param client The chat client.
 * @param navToPostLogin Whether to navigate to the post-login screen.
 * @param navAction The navigation actions.
 * @param postLoginScreen The post-login screen.
 * @param eventViewModel The event view model state.
 * @param userViewModel The user view model state.
 * @param applicationContext The application context.
 */
private fun connectChatClient(
    client: ChatClient,
    navToPostLogin: Boolean,
    navAction: NavigationActions,
    postLoginScreen: String,
    eventViewModel: MutableState<EventViewModel>,
    userViewModel: MutableState<UserViewModel>,
    applicationContext: Context
) {

  val uid = Firebase.auth.currentUser!!.uid
  val user = User(id = uid, name = Firebase.auth.currentUser!!.email!!)
  // TODO: currently username = email

  client.connectUser(user = user, token = client.devToken(uid)).enqueue { result ->
    // TODO: Generate Token, see https://getstream.io/tutorials/android-chat/
    if (navToPostLogin) {
      if (result.isSuccess) {
        Log.d(TAG, "ChatClient: Successfully connected user ${userViewModel.value.currentUID}")
      } else {
        Toast.makeText(applicationContext, "Failed to connect to chat client", Toast.LENGTH_SHORT)
            .show()
        Log.e(TAG, "ChatClient: Failed to connect user ${userViewModel.value.currentUID}")
      }
      onNavToPostLogin(
          eventViewModel,
          userViewModel,
          TOP_LEVEL_DESTINATIONS.first { it.route == postLoginScreen },
          navAction)
    }
  }
}

/**
 * Call this function in main (as the first thing after onCreate()) to use the Firebase Emulator
 * Suite for purely local testing.
 */
fun debug() {
  val androidLocalhost = "10.0.2.2"
  //  Firebase.auth.signOut() // Uncomment this if the app crashes when you're already signed in
  Firebase.firestore.clearPersistence()
  // It's best to sign out and clear cache at each run if you're testing. Avoids
  // unexpected Firebase issues.
  Firebase.firestore.useEmulator(androidLocalhost, 8080)
  Firebase.auth.useEmulator(androidLocalhost, 9099)
  Firebase.storage.useEmulator(androidLocalhost, 9199)
}
