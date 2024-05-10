package com.github.se.gomeet.ui.mainscreens.profile.settings

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.provider.SettingsSlicesContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
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
            val context = LocalContext.current
            SettingsComposable(R.drawable.mobile_friendly, "Open permissions",true, {
                //open android settings app
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                startActivity(context, intent, null)
                //how can i access the context in composable function ?
            })

            var location = "Disabled"
            // get the location permission status
            val precise = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            val internet = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET)
            val postNotification = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)



            location = "Disabled"
            if (precise == 0) {
                location = "Enabled"
            }else if(coarse == 0){
                location = "Only Coarse Location"
            }

            PermissionComposable(R.drawable.mobile_friendly, "Location", locationToString(coarse, precise))
            PermissionComposable(R.drawable.mobile_friendly, "Internet", if (internet == 0) "Enabled" else "Disabled" )
            PermissionComposable(R.drawable.mobile_friendly, "Notifications", if (postNotification == 0) "Enabled" else "Disabled")



        }
    }
}

fun locationToString(coarse : Int, precise : Int) : String{
    if (precise == 0) {
        return "Enabled"
    }else if(coarse == 0){
        return "Only Coarse Location"
    }
    return "Disabled"
}


@Composable
fun PermissionComposable(icon: Int, text: String, permission: String){
    Row(
        modifier = Modifier.padding(start = 15.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = text + "icon",
            tint = MaterialTheme.colorScheme.onBackground)

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

        Text(
            text = permission,
            modifier = Modifier.padding(start = 15.dp),
            color = MaterialTheme.colorScheme.onBackground,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Default,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.bodySmall)



        NavigateNextComposable()
    }
}





@Preview
@Composable
fun SettingsPermissionsPreview() {
    SettingsPermissions(nav = NavigationActions(rememberNavController()))
}