package com.github.se.gomeet.ui.mainscreens.profile.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.gomeet.R
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS

@Composable
fun SettingsAbout(
    nav: NavigationActions,
) {
  val screenHeight = LocalConfiguration.current.screenHeightDp.dp
  val screenWidth = LocalConfiguration.current.screenWidthDp.dp

  Scaffold(
      modifier = Modifier.testTag("SettingsAbout"),
      topBar = {
        Column {
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
                        contentDescription = "About",
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
              Spacer(modifier = Modifier.padding(10.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.gomeet_logo),
                    contentDescription = "GoMeet Logo",
                    modifier = Modifier.size(60.dp))
                Spacer(modifier = Modifier.padding(10.dp))
                Text(
                    text = "GoMeet",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontStyle = FontStyle.Normal,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Default,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.titleSmall)
                Text(
                    text = " Social",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                    fontStyle = FontStyle.Normal,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Default,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.titleSmall)
              }
              Spacer(modifier = Modifier.padding(10.dp))
              Text(
                  text = "version alpha.m2",
                  color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.70f),
                  fontStyle = FontStyle.Normal,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.SemiBold,
                  fontFamily = FontFamily.Default,
                  textAlign = TextAlign.Start,
                  style = MaterialTheme.typography.titleSmall)
              Spacer(modifier = Modifier.padding(20.dp))
              Text(
                  text =
                      "This app was develloped by the GoMeet Team for the EPFL Software Entreprise Class.",
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
