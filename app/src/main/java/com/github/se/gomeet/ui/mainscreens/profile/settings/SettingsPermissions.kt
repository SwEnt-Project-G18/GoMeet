package com.github.se.gomeet.ui.mainscreens.profile.settings

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.INTERNET
import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.provider.SettingsSlicesContract
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.R
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.DarkCyan
import com.google.accompanist.permissions.rememberMultiplePermissionsState


@SuppressLint("UnrememberedMutableState")
@Composable
fun SettingsPermissions(

    nav: NavigationActions, /*userViewModel: UserViewModel*/
) {
    Log.d("MyComposable", "Recomposed")
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

            //MAIN BODY

            //Center and Align
            modifier =
            Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .testTag("Settings")
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center)

        //Composable Content
        {

            val context = LocalContext.current

            // get the permissions status
            val permissionsState =  mutableStateOf<Map<String, Int>>(mapOf())
            updatePermissions(context, permissionsState)

            // set the default state of the dialogues
            var showPermissionDialog by remember {mutableStateOf(false)}
            var showConfirmation by remember {mutableStateOf(false)}

            // create the permission launcher
            val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission(),
                //after attempting to acquire a permission
                onResult = { isGranted ->
                    //whatever the result, update the permissions status
                    updatePermissions(context, permissionsState)
                    //if the permission was not granted, show the appropriate dialogue
                    if (!isGranted) { showPermissionDialog = true } }
            )

            // create the settings launcher
            val settingsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()
            ) { result ->
                //after returning from the settings, update the permissions status to catch an changes
                updatePermissions(context, permissionsState)
            }


            // This helper will be used to create the onClick function for each permission
            fun permissionCompagnon(perm : String) : ()->Unit {
                return {
                    //if the permission is already granted, launch a dialogue explaining the consequences of disabling it
                    if (permissionsState.value.get(perm) == 0) {
                        showConfirmation = true
                    } else {
                        //otherwise, request the permission
                        permissionLauncher.launch(perm)
                    }
                }
            }

            //Main button to acces settings directly
            SettingsComposable(R.drawable.settings_icon, "Open permissions",true, {openPermissionSettings(context,settingsLauncher)})

            //Permissions Buttons
            PermissionComposable(R.drawable.location_icon, "Location", permissionsState, ACCESS_FINE_LOCATION,
                permissionCompagnon(ACCESS_FINE_LOCATION))
            PermissionComposable(R.drawable.language, "Internet", permissionsState, INTERNET,
                permissionCompagnon(INTERNET))
            PermissionComposable(R.drawable.notifications_icon,"Notifications", permissionsState, POST_NOTIFICATIONS,
                permissionCompagnon(POST_NOTIFICATIONS))



            //Declare dialogues
            if(showPermissionDialog){
                SettingsDialogue(
                    onClose = { showPermissionDialog = false },
                    context, settingsLauncher, permissionsState,
                    title = "Permission Request",
                    text = "The app requires some permissions from you device. To enable the full functionality of the app, please go to settings and enable the permission manually",
                )
            }
            if(showConfirmation){
                SettingsDialogue(
                    onClose = { showConfirmation = false },
                    context, settingsLauncher, permissionsState,
                    title = "Are You Sure ?",
                    text = "Are you sure you want to disable this permission ? Doing so might prevent some functionalities of the app from working properly",
                )
            }

        }
    }
}









/**
 * Function to update the permissions state map with current values
 *
 * @param context The context of the app
 * @param permissionsState The state map of the permissions
 * @return The updated state of the permissions
 */
fun updatePermissions(context: Context, permissionsState: MutableState<Map<String, Int>>){
    permissionsState.value = mapOf(
        ACCESS_FINE_LOCATION to ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION),
        ACCESS_COARSE_LOCATION to ContextCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION),
        INTERNET to ContextCompat.checkSelfPermission(context, INTERNET),
        POST_NOTIFICATIONS to ContextCompat.checkSelfPermission(context, POST_NOTIFICATIONS)
    )
}



/**
 * Function to get the state of a permission as a string
 * Will also check coarse location when passed FINE_LOCATION
 *
 * @param permissionsState The state of the permissions
 * @param permissionString The string representing the permission
 * @return A string representing the state of the permission
 */
fun getPermissionState(permissionsState: MutableState<Map<String, Int>>, permissionString : String) : String{
    if (permissionsState.value.get(permissionString) == 0) {
        return "Enabled"
    }

    //Account for the fact that the location permission can be either fine or coarse
    else if(permissionString == ACCESS_COARSE_LOCATION && permissionsState.value.get(ACCESS_COARSE_LOCATION) == 0){
        return "Only Coarse Location"
    }

    return "Disabled"
}

/**
 * Function to open the permission settings of the app
 *
 * @param context The context of the app
 * @param launcher The launcher to open the settings
 */
fun openPermissionSettings(context: Context, launcher: ManagedActivityResultLauncher<Intent,ActivityResult>){

    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri: Uri = Uri.fromParts("package", context.packageName, null)
    intent.data = uri
    launcher.launch(intent)
}

/**
 * Composable function for the settings items.
 *
 * @param icon The icon for the item.
 * @param text The text for the item.
 * @param status Short text snippet to display the status of the permission
 * @param onClick The function to be called when the item is clicked
 */
@Composable
fun PermissionComposable(icon: Int,
                         text: String,
                         permissionsState: MutableState<Map<String, Int>>,
                         permission: String,
                         onClick : ()->Unit ){

    Row(
        modifier = Modifier
            .padding(start = 15.dp)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    )


    {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = text + "icon",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(24.dp))

            //Set icon size

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
            text = getPermissionState(permissionsState, permission),
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


/**
 * Composable representing a dialogue screen to be opened when the user needs to access the permission settings
 * Show a title, a text, a button toward setting and a cancel button
 *
 * @param onClose The function to be called to close the dialogue
 * @param context The context of the app
 * @param settingsLauncher The launcher to open the settings
 * @param permissionsState The state of the permissions
 * @param title The title of the dialogue
 * @param text The text of the dialogue
 */
@Composable
fun SettingsDialogue(onClose: ()->Unit,
                     context: Context,
                     settingsLauncher: ManagedActivityResultLauncher<Intent,ActivityResult>,
                     permissionsState: MutableState<Map<String, Int>>,
                     title: String,
                     text: String){
    AlertDialog(

        onDismissRequest = { onClose() },
        title = {Text(title)},
        text = {Text(text)},
        confirmButton = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .width(90.dp),
                onClick = {
                    openPermissionSettings(context, settingsLauncher)
                    onClose()
                    updatePermissions(context,permissionsState)
                }) {
                Text("Continue to Settings")
            }
        },

        dismissButton = {
            Button(

                modifier = Modifier
                    .fillMaxWidth()
                    .width(90.dp),
                onClick = {
                    onClose()
                    updatePermissions(context, permissionsState)
                }) {
                Text("Cancel")
            }
        }
    )
}


@Preview
@Composable
fun SettingsPermissionsPreview() {
    SettingsPermissions(nav = NavigationActions(rememberNavController()))
}