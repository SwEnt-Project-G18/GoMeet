package com.github.se.gomeet.ui.navigation

import android.net.Uri
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.github.se.gomeet.R
import com.google.android.gms.maps.model.LatLng

data class TopLevelDestination(
    val route: String,
    val icon: ImageVector,
    val textId: String,
)

object Route {
  const val WELCOME = "Welcome"
  const val LOGIN = "Login"
  const val REGISTER = "Register"
  const val EVENTS = "Events"
  const val TRENDS = "Trends"
  const val EXPLORE = "Explore"
  const val CREATE = "Create"
  const val PROFILE = "Profile"
  const val PUBLIC_CREATE = "Public Create"
  const val PRIVATE_CREATE = "Private Create"
  const val OTHERS_PROFILE = "OthersProfile/{uid}"
  const val ADD_PARTICIPANTS = "Add Participants"
  const val EVENT_INFO =
      "eventInfo/{eventId}/{title}/{date}/{time}/{organizer}/{rating}/{description}/{latitude}/{longitude}"
  const val OTHERS_EVENT_INFO =
      "othersEventInfo/{eventId}/{title}/{date}/{time}/{organizer}/{rating}/{description}/{latitude}/{longitude}"
  const val MESSAGE = "Message/{id}"
}

val CREATE_ITEMS =
    listOf(
        TopLevelDestination(
            route = Route.PUBLIC_CREATE,
            icon = Icons.Default.AccountCircle,
            textId = Route.PUBLIC_CREATE),
        TopLevelDestination(
            route = Route.PRIVATE_CREATE,
            icon = Icons.Default.AccountCircle,
            textId = Route.PRIVATE_CREATE),
    )

val LOGIN_ITEMS =
    listOf(
        TopLevelDestination(
            route = Route.WELCOME, icon = Icons.Default.AccountCircle, textId = Route.WELCOME),
        TopLevelDestination(
            route = Route.LOGIN, icon = Icons.Default.AccountCircle, textId = Route.LOGIN),
        TopLevelDestination(
            route = Route.REGISTER, icon = Icons.Default.AccountCircle, textId = Route.REGISTER),
    )

val TOP_LEVEL_DESTINATIONS =
    listOf(
        TopLevelDestination(
            route = Route.EVENTS, icon = Icons.Default.DateRange, textId = Route.EVENTS),
        TopLevelDestination(route = Route.TRENDS, icon = Icons.Default.Home, textId = Route.TRENDS),
        TopLevelDestination(
            route = Route.EXPLORE, icon = Icons.Default.Home, textId = Route.EXPLORE),
        TopLevelDestination(route = Route.CREATE, icon = Icons.Default.Add, textId = Route.CREATE),
        TopLevelDestination(
            route = Route.PROFILE, icon = Icons.Default.Person, textId = Route.PROFILE))

val SECOND_LEVEL_DESTINATION =
    listOf(
        TopLevelDestination(
            route = Route.OTHERS_PROFILE,
            icon = Icons.Default.Person,
            textId = Route.OTHERS_PROFILE),
        TopLevelDestination(
            route = Route.ADD_PARTICIPANTS,
            icon = Icons.Default.Person,
            textId = Route.ADD_PARTICIPANTS),
        TopLevelDestination(
            route = Route.EVENT_INFO, icon = Icons.Default.Person, textId = Route.EVENT_INFO))

class NavigationActions(val navController: NavHostController) {
  fun navigateTo(destination: TopLevelDestination, clearBackStack: Boolean = false) {
    Log.d("Navigation", "Navigating to ${destination.route}, clear back stack: $clearBackStack")
    navController.navigate(destination.route) {
      if (clearBackStack) {
        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
      }
      launchSingleTop = true
      restoreState = true
    }
  }

  fun navigateToScreen(route: String) {
    navController.navigate(route)
  }

  fun navigateToEventInfo(
      eventId: String,
      title: String,
      date: String,
      time: String,
      organizer: String,
      rating: Double,
      description: String,
      loc: LatLng
  ) {
    val route =
        Route.EVENT_INFO.replace("{eventId}", Uri.encode(eventId))
            .replace("{title}", Uri.encode(title))
            .replace("{date}", Uri.encode(date))
            .replace("{time}", Uri.encode(time))
            .replace("{organizer}", Uri.encode(organizer))
            .replace("{rating}", rating.toString())
            .replace("{description}", Uri.encode(description))
            .replace("{latitude}", loc.latitude.toString())
            .replace("{longitude}", loc.longitude.toString())
    navController.navigate(route)
  }

  fun goBack() {
    navController.popBackStack()
  }
}

@Composable
fun getIconForRoute(route: String): ImageVector {
  return when (route) {
    Route.EVENTS -> Icons.Default.DateRange
    Route.TRENDS -> ImageVector.vectorResource(R.drawable.arrow_trending)
    Route.EXPLORE -> Icons.Default.Home
    Route.CREATE -> Icons.Default.Add
    Route.PROFILE -> Icons.Default.Person
    else -> Icons.Default.AccountCircle
  }
}
