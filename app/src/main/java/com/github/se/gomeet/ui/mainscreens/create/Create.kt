package com.github.se.gomeet.ui.mainscreens.create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.CREATE_ITEMS
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.ui.theme.DarkerCyan

@Composable
fun Create(nav: NavigationActions) {
  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { selectedTab ->
              nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
            },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = Route.CREATE)
      }) { innerPadding ->
        Text(
            text = "Create",
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 15.dp),
            color = DarkCyan,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Default,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.headlineLarge)

        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Spacer(modifier = Modifier.height(150.dp))

              Text(
                  text = "Choose your audience",
                  fontStyle = FontStyle.Normal,
                  fontWeight = FontWeight.Normal,
                  fontFamily = FontFamily.Default,
                  textAlign = TextAlign.Start,
                  style = MaterialTheme.typography.headlineSmall)

              Spacer(modifier = Modifier.height(150.dp))

              Row {
                OutlinedButton(
                    modifier = Modifier.width(128.dp).height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color.Gray),
                    enabled = true,
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.LightGray),
                    onClick = {
                      nav.navigateTo(CREATE_ITEMS.first { it.route == Route.PUBLIC_CREATE })
                    }) {
                      Text(
                          text = "Public",
                          color = Color.Black,
                          fontStyle = FontStyle.Normal,
                          fontWeight = FontWeight.Normal,
                          fontFamily = FontFamily.Default,
                          textAlign = TextAlign.Start,
                          style = MaterialTheme.typography.bodyMedium)
                    }
                Spacer(modifier = Modifier.width(20.dp))

                OutlinedButton(
                    modifier = Modifier.width(128.dp).height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color.Gray), // Set border here if needed
                    enabled = true,
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.LightGray),
                    onClick = {
                      nav.navigateTo(CREATE_ITEMS.first { it.route == Route.PRIVATE_CREATE })
                    }) {
                      Text(
                          text = "Private",
                          color = Color.Black,
                          fontStyle = FontStyle.Normal,
                          fontWeight = FontWeight.Normal,
                          fontFamily = FontFamily.Default,
                          textAlign = TextAlign.Start,
                          style = MaterialTheme.typography.bodyMedium)
                    }
              }
            }
      }
}


@Preview
@Composable
fun CreatePreview() {
  Create(nav = NavigationActions(rememberNavController()))
}
