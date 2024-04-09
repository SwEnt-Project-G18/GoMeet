package com.github.se.gomeet.ui.mainscreens.create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.github.se.gomeet.R
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.CREATE_ITEMS
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS

@Composable
fun Create(nav: NavigationActions) {
  val screen = remember { mutableStateOf<String>("") }
  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { selectedTab ->
              nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
            },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = Route.EXPLORE)
      }) { innerPadding ->
        Text(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 30.dp),
            text = "Create",
            style =
                TextStyle(
                    fontSize = 24.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(R.font.roboto)),
                    fontWeight = FontWeight(1000),
                    color = Color(0xFF073F57),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp,
                ))

        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Spacer(modifier = Modifier.height(220.dp))

              Text(
                  text = "Choose your audience",
                  style =
                      TextStyle(
                          fontSize = 15.sp,
                          lineHeight = 16.sp,
                          fontFamily = FontFamily(Font(R.font.roboto)),
                          fontWeight = FontWeight(500),
                          color = Color(0xFF000000),
                          textAlign = TextAlign.Center,
                          letterSpacing = 0.5.sp,
                      ))

              Spacer(modifier = Modifier.height(120.dp))

              Row {
                OutlinedButton(
                    modifier = Modifier.width(128.dp).height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color.Gray), // Set border here if needed
                    enabled = true,
                    colors =
                        ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFECEFF1)),
                    onClick = {
                      nav.navigateTo(CREATE_ITEMS.first { it.route == Route.PUBLIC_CREATE })
                    }) {
                      Text(
                          text = "Public",
                          style =
                              TextStyle(
                                  fontSize = 12.sp,
                                  lineHeight = 16.sp,
                                  fontFamily = FontFamily(Font(R.font.roboto)),
                                  fontWeight = FontWeight(1000),
                                  color = Color(0xFF000000),
                                  textAlign = TextAlign.Center,
                                  letterSpacing = 0.5.sp,
                              ))
                    }
                Spacer(modifier = Modifier.width(20.dp))

                OutlinedButton(
                    modifier = Modifier.width(128.dp).height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color.Gray), // Set border here if needed
                    enabled = true,
                    colors =
                        ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFECEFF1)),
                    onClick = {
                      nav.navigateTo(CREATE_ITEMS.first { it.route == Route.PRIVATE_CREATE })
                    }) {
                      Text(
                          text = "Private",
                          style =
                              TextStyle(
                                  fontSize = 12.sp,
                                  lineHeight = 16.sp,
                                  fontFamily = FontFamily(Font(R.font.roboto)),
                                  fontWeight = FontWeight(500),
                                  color = Color(0xFF000000),
                                  textAlign = TextAlign.Center,
                                  letterSpacing = 0.5.sp,
                              ))
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
