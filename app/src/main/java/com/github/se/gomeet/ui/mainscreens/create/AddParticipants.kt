package com.github.se.gomeet.ui.mainscreens.create

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS

@Composable
fun AddParticipants(nav: NavigationActions) {
    /* TODO: Code the UI of the AddParticipants screen when the logic of
    the invites will be done and the UI of this screen discussed with the team */

    Scaffold(
        modifier = Modifier.testTag("AddParticipantsScreen"),
        bottomBar = {
            BottomNavigationMenu(
                onTabSelect = { selectedTab ->
                    nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
                },
                tabList = TOP_LEVEL_DESTINATIONS,
                selectedItem = Route.CREATE)
        }) { innerPadding ->
        Text("Add Participants", Modifier.padding(innerPadding))
    }
}