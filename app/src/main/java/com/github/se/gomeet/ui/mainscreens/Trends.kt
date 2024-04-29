package com.github.se.gomeet.ui.mainscreens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS

/**
 * Trends screen composable. This is where the popular trends are displayed.
 *
 * @param nav Navigation actions.
 */
@Composable
fun Trends(nav: NavigationActions) {
  Scaffold(
      modifier = Modifier.testTag("TrendsScreen"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { selectedTab ->
              nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
            },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = Route.TRENDS)
      }) { innerPadding ->
        Text("Trends", Modifier.padding(innerPadding))
      }
}
