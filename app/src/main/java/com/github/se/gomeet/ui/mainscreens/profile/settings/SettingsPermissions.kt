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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.R
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.DarkCyan

@Composable
fun SettingsPermissions(
    nav: NavigationActions, /*userViewModel: UserViewModel*/
) {
    Scaffold(
        modifier = Modifier.testTag("SettingsPermissions"),
        topBar = {
            Column {
                Text(
                    text = "Permissions",
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
            Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .testTag("Settings")
                .fillMaxWidth(),

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.Center) {
            Spacer(modifier = Modifier.padding(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
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
                style = MaterialTheme.typography.titleSmall
            )






        }
    }
}

@Preview
@Composable
fun SettingsPermissionsPreview() {
    SettingsPermissions(nav = NavigationActions(rememberNavController()))
}