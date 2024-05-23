package com.github.se.gomeet.ui.mainscreens.profile.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS

@Composable
fun SettingsHelp(
    nav: NavigationActions,
) {
  val screenHeight = LocalConfiguration.current.screenHeightDp.dp
  val screenWidth = LocalConfiguration.current.screenWidthDp.dp

  Scaffold(
      modifier = Modifier.testTag("SettingsHelp"),
      topBar = {
        Column(modifier = Modifier.padding(bottom = screenHeight / 90)) {
          TopAppBar(
              modifier = Modifier.testTag("TopBar"),
              backgroundColor = MaterialTheme.colorScheme.background,
              elevation = 0.dp,
              title = {
                // Empty title since we're placing our own components
              },
              navigationIcon = {
                IconButton(onClick = { nav.goBack() }) {
                  Icon(
                      imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                      contentDescription = "Help",
                      tint = MaterialTheme.colorScheme.onBackground)
                }
              })

          Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(start = 18.dp)) {
                Text(
                    text = "Permissions",
                    color = MaterialTheme.colorScheme.onBackground,
                    style =
                        MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold))
              }
        }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { selectedTab ->
              nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
            },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = Route.PROFILE)
      }) { innerPadding ->
        Column(
            modifier =
                Modifier.padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .testTag("Settings")
                    .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              Spacer(modifier = Modifier.padding(40.dp))

              Text(
                  // TODO: Add contact address
                  text =
                      "This app was develloped by the GoMeet Team. If you have any questions or need help, please contact us at : ",
                  color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                  fontStyle = FontStyle.Normal,
                  fontWeight = FontWeight.SemiBold,
                  fontFamily = FontFamily.Default,
                  textAlign = TextAlign.Start,
                  style = MaterialTheme.typography.titleSmall,
                  fontSize = 14.sp,
                  modifier = Modifier.padding(25.dp))
            }
      }
}
