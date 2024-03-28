package com.github.se.gomeet.ui.navigation

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.navigation.TopLevelDestination

@Composable
fun BottomNavigationMenu(
    onTabSelect: (String) -> Unit,
    tabList: List<TopLevelDestination>,
    selectedItem: String
) {
  BottomNavigation {
    tabList.forEach { tab ->
      BottomNavigationItem(
          icon = {
            Icon(
                painter = painterResource(id = tab.icon!!),
                contentDescription = "",
                modifier = Modifier.scale(1.5f))
          },
          //                label = { Text(tab.route) },
          selected = tab.route == selectedItem,
          onClick = { onTabSelect(tab.route) })
    }
  }
}

@Preview
@Composable
fun PreviewBottomNavigationMenu() {
  BottomNavigationMenu(
      onTabSelect = {},
      tabList = TOP_LEVEL_DESTINATIONS,
      //        selectedItem = OVERVIEW
      selectedItem = LOGIN
      //        selectedItem = ""
      )
}
