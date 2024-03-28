package com.github.se.gomeet.ui.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.github.se.gomeet.R

class NavigationActions(private val navController: NavHostController) {

  private var lastDest = ""
  private var currDest = ""

  fun navigateTo(destination: String) {
    navController.navigate(destination) {
      // Pop up to the start destination of the graph to
      // avoid building up a large stack of destinations
      // on the back stack as users select items
      popUpTo(navController.graph.findStartDestination().id) { saveState = true }
      // Avoid multiple copies of the same destination when
      // reselecting the same item
      launchSingleTop = true
      // Restore state when reselecting a previously selected item
      restoreState = true

      lastDest = currDest
      currDest = destination
    }
  }


  fun goBack() {
    if (DESTINATIONS.contains(lastDest)) navigateTo(lastDest)
  }
}

val LOGIN = "Login"
val DESTINATIONS = listOf(LOGIN)

val TOP_LEVEL_DESTINATIONS =
    listOf( TopLevelDestination(LOGIN, null, 0) )

class TopLevelDestination(val route: String, val icon: Int?, val textId: Int) {}
