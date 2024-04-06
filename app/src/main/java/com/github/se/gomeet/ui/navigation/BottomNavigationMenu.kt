package com.github.se.gomeet.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.common.collect.Iterables.size

@Composable
fun BottomNavigationMenu(
    onTabSelect: (String) -> Unit,
    tabList: List<TopLevelDestination>,
    selectedItem: String
) {
  BottomNavigation(
      modifier = Modifier.fillMaxWidth().height(80.dp),
      backgroundColor = MaterialTheme.colors.background) {
        for (i in 3..< size(tabList)) {
          val destination = tabList[i]
          BottomNavigationItem(
              icon = {
                Icon(imageVector = destination.icon, contentDescription = destination.textId)
              },
              label = { Text(destination.textId) },
              selected = destination.route == selectedItem,
              onClick = { onTabSelect(destination.route) })
        }
      }
}
