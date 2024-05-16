package com.github.se.gomeet.ui.mainscreens.profile.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.se.gomeet.R
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.DarkCyan

/**
 * Composable function for the profile Settings screen.
 *
 * @param nav The navigation actions for the screen.
 */
@Composable
fun SettingsScreen(
    nav: NavigationActions, /*userViewModel: UserViewModel*/
) {
  Scaffold(
      modifier = Modifier.testTag("SettingsScreen"),
      topBar = {
        Column {
          Text(
              text = "Settings",
              modifier = Modifier.padding(top = 15.dp, start = 15.dp, end = 18.dp, bottom = 0.dp),
              color = DarkCyan,
              fontStyle = FontStyle.Normal,
              fontWeight = FontWeight.SemiBold,
              fontFamily = FontFamily.Default,
              textAlign = TextAlign.Start,
              style = MaterialTheme.typography.headlineLarge)

          Row(
              horizontalArrangement = Arrangement.Start,
              verticalAlignment = Alignment.CenterVertically,
          ) {
            Surface(onClick = { nav.goBack() }, shape = CircleShape, color = Color.Transparent) {
              Icon(
                  painter = painterResource(id = R.drawable.arrow_back),
                  contentDescription = "Back button",
                  modifier = Modifier.padding(15.dp),
                  tint = MaterialTheme.colorScheme.onBackground)
            }

            Text(
                text = "Back",
                color = MaterialTheme.colorScheme.onBackground,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Default,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleSmall)
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
                    .testTag("Settings"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              SettingsSubtitle(
                  "Who can see your content", Modifier.padding(15.dp).align(Alignment.Start))

              SettingsComposable(R.drawable.indeterminate_check_box, "Account privacy")
              SettingsComposable(R.drawable.star, "Close friends")
              SettingsComposable(R.drawable.location_icon, "Blocked")
              SettingsComposable(R.drawable.mail, "Messages")

              SettingsSubtitle("Your app and media", Modifier.padding(15.dp).align(Alignment.Start))

              SettingsComposable(R.drawable.folder, "Suggested content")
              SettingsComposable(
                  R.drawable.mobile_friendly,
                  "Device permissions",
                  true,
                  { nav.navigateToScreen(Route.PERMISSIONS) })
              SettingsComposable(R.drawable.check_icon, "Accessibility")
              SettingsComposable(R.drawable.language, "Language")

              SettingsSubtitle(
                  "More info and support", Modifier.padding(15.dp).align(Alignment.Start))

              SettingsComposable(
                  R.drawable.baseline_chat_bubble_outline_24,
                  "Help",
                  true,
                  { nav.navigateToScreen(Route.HELP) })
              SettingsComposable(
                  R.drawable.gomeet_icon, "About", true, { nav.navigateToScreen(Route.ABOUT) })

              Text(
                  text = "Log out",
                  modifier = Modifier.padding(start = 15.dp),
                  color = Color.Red,
                  fontStyle = FontStyle.Normal,
                  fontWeight = FontWeight.SemiBold,
                  fontFamily = FontFamily.Default,
                  textAlign = TextAlign.Start,
                  style = MaterialTheme.typography.bodySmall)

              Text(
                  text = "Delete account",
                  modifier = Modifier.padding(start = 15.dp),
                  color = Color.Red,
                  fontStyle = FontStyle.Normal,
                  fontWeight = FontWeight.SemiBold,
                  fontFamily = FontFamily.Default,
                  textAlign = TextAlign.Start,
                  style = MaterialTheme.typography.bodySmall)
            }
      }
}

/**
 * Composable function for the settings items.
 *
 * @param icon The icon for the item.
 * @param text The text for the item.
 */
@Composable
fun SettingsComposable(
    icon: Int,
    text: String,
    clickable: Boolean = false,
    onClick: () -> Unit = {}
) {
  Row(
      modifier =
          Modifier.padding(start = 15.dp).let {
            if (clickable) it.clickable(onClick = onClick) else it
          }, // Add the clickable modifier here if clickable is true
      horizontalArrangement = Arrangement.Start,
      verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = text + "icon",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(24.dp))

        Text(
            text = text,
            modifier = Modifier.padding(start = 15.dp),
            color = MaterialTheme.colorScheme.onBackground,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Default,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.weight(1f))

        NavigateNextComposable()
      }
}

/** Composable function for the navigate next icon. */
@Composable
fun NavigateNextComposable() {
  Surface(
      onClick = { /* Go to the settings more in depth */},
      modifier = Modifier.padding(end = 15.dp),
      shape = CircleShape,
      color = Color.Transparent) {
        Icon(
            painter = painterResource(id = R.drawable.navigate_next),
            contentDescription = "Navigate next icon",
            modifier = Modifier.padding(15.dp),
            tint = MaterialTheme.colorScheme.onBackground)
      }
}

/**
 * Composable function for the settings subtitle.
 *
 * @param text The text for the subtitle.
 * @param modifier The modifier for the subtitle.
 */
@Composable
fun SettingsSubtitle(text: String, modifier: Modifier) {
  Text(
      text = text,
      modifier = modifier,
      color = Color.Gray,
      fontStyle = FontStyle.Normal,
      fontWeight = FontWeight.Light,
      fontFamily = FontFamily.Default,
      textAlign = TextAlign.Start,
      style = MaterialTheme.typography.labelSmall)
}
