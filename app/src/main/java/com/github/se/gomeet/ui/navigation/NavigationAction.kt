package com.github.se.gomeet.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

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
  const val TRENDING = "Trending"
  const val EXPLORE = "Explore"
  const val CREATE = "Create"
  const val PROFILE = "Profile"
}

val TOP_LEVEL_DESTINATIONS =
    listOf(
        TopLevelDestination(
            route = Route.WELCOME, icon = Icons.Default.AccountCircle, textId = "Welcome"),
        TopLevelDestination(
            route = Route.LOGIN, icon = Icons.Default.AccountCircle, textId = "Login"),
        TopLevelDestination(
            route = Route.REGISTER, icon = Icons.Default.AccountCircle, textId = "Register"),
        TopLevelDestination(
            route = Route.EVENTS, icon = Icons.Default.DateRange, textId = "Events"),
        TopLevelDestination(
            route = Route.TRENDING, icon = Icons.Default.KeyboardArrowUp, textId = "Trending"),
        TopLevelDestination(route = Route.EXPLORE, icon = Icons.Default.Home, textId = "Explore"),
        TopLevelDestination(route = Route.CREATE, icon = Icons.Default.Add, textId = "Create"),
        TopLevelDestination(route = Route.PROFILE, icon = Icons.Default.Person, textId = "Profile"))

class NavigationActions(private val navController: NavHostController) {
  fun navigateTo(destination: TopLevelDestination) {
    navController.navigate(destination.route) {
      popUpTo(navController.graph.findStartDestination().id) { saveState = true }
      launchSingleTop = true
      restoreState = true
    }
  }

  fun goBack() {
    navController.popBackStack()
  }
}
