package com.github.se.gomeet.ui.mainscreens.create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.CREATE_ITEMS
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS

/**
 * Composable function for the Create screen. This composable will navigate to either the
 * PublicCreate or PrivateCreate screen.
 *
 * @param nav The navigation actions.
 */
@Composable
fun Create(nav: NavigationActions) {
  val screenWidth = LocalConfiguration.current.screenWidthDp.dp
  val screenHeight = LocalConfiguration.current.screenHeightDp.dp

  Scaffold(
      modifier = Modifier.testTag("CreateUI"),
      topBar = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = screenWidth / 15, top = screenHeight / 30)) {
              Text(
                  text = "Create",
                  style =
                      MaterialTheme.typography.headlineMedium.copy(
                          fontWeight = FontWeight.SemiBold))
              Spacer(Modifier.weight(1f))
            }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { selectedTab ->
              nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
            },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = Route.CREATE)
      }) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Spacer(modifier = Modifier.height(screenHeight / 6))

              Text(text = "Choose your audience", style = MaterialTheme.typography.headlineSmall)

              Spacer(modifier = Modifier.height(screenHeight / 6))
              OutlinedButton(
                  onClick = {
                    nav.navigateTo(CREATE_ITEMS.first { it.route == Route.PUBLIC_CREATE })
                  },
                  modifier = Modifier.width((screenWidth / 1.5.dp).dp).height(screenHeight / 17),
                  shape = RoundedCornerShape(10.dp),
                  enabled = true,
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = MaterialTheme.colorScheme.outlineVariant)) {
                    Text(
                        text = "Public",
                        style =
                            MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold),
                        color = Color.White)
                  }

              Spacer(modifier = Modifier.size(10.dp))
              OutlinedButton(
                  onClick = {
                    nav.navigateTo(CREATE_ITEMS.first { it.route == Route.PRIVATE_CREATE })
                  },
                  modifier = Modifier.width((screenWidth / 1.5.dp).dp).height(screenHeight / 17),
                  shape = RoundedCornerShape(10.dp),
                  border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                  enabled = true,
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = MaterialTheme.colorScheme.background)) {
                    Text(
                        text = "Private",
                        style =
                            MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.outlineVariant)
                  }
            }
      }
}

@Preview
@Composable
fun CreatePreview() {
  Create(nav = NavigationActions(rememberNavController()))
}
