package com.github.se.gomeet.ui.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.gomeet.ui.theme.TranslucentCyan

/**
 * Bottom navigation menu for the app.
 *
 * @param onTabSelect callback for when a tab is selected
 * @param tabList list of top level destinations
 * @param selectedItem the currently selected item
 */
@Composable
fun BottomNavigationMenu(
    onTabSelect: (String) -> Unit,
    tabList: List<TopLevelDestination>,
    selectedItem: String
) {
  NavigationBar(
      modifier = Modifier.navigationBarsPadding().height(80.dp),
      containerColor = MaterialTheme.colorScheme.background,
      tonalElevation = 0.dp,
  ) {
    tabList.forEach { destination ->
      val selected = selectedItem == destination.route
      NavigationBarItem(
          modifier = Modifier.testTag(destination.route),
          icon = {
            Icon(
                imageVector =
                    if (selected) getIconForSelectedRoute(destination.route)
                    else getIconForRoute(destination.route),
                contentDescription = destination.textId,
                modifier = Modifier.size(24.dp))
          },
          label = {
            Text(
                text = destination.textId,
                maxLines = 1,
                style =
                    if (selected)
                        MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    else MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp))
          },
          selected = selected,
          onClick = { onTabSelect(destination.route) },
          colors =
              NavigationBarItemDefaults.colors(
                  unselectedIconColor = Gray,
                  unselectedTextColor = Gray,
                  selectedTextColor = MaterialTheme.colorScheme.tertiary,
                  selectedIconColor = MaterialTheme.colorScheme.tertiary,
                  indicatorColor = TranslucentCyan))
    }
  }
}

@Preview
@Composable
fun PreviewBottomNavigationMenu() {
  BottomNavigationMenu(
      onTabSelect = {},
      tabList = TOP_LEVEL_DESTINATIONS,
      selectedItem = Route.NOTIFICATIONS,
  )
}
