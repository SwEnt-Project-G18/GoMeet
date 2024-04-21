package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.ui.theme.NavBarUnselected

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OthersProfile(nav: NavigationActions) { // TODO Add parameters to the function
  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { selectedTab ->
              nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
            },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = "")
      },
      topBar = {
        TopAppBar(
            title = {},
            backgroundColor = MaterialTheme.colorScheme.background,
            elevation = 0.dp,
            modifier = Modifier.height(50.dp),
            actions = {
              // Settings Icon
              IconButton(onClick = { /* Handle settings icon click */}) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    modifier = Modifier.size(24.dp),
                    tint = DarkCyan)
              }

              MoreActionsButton()
            })
      }) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(innerPadding).verticalScroll(rememberScrollState(0))) {
              Row(
                  horizontalArrangement = Arrangement.Start,
                  verticalAlignment = Alignment.CenterVertically,
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(start = 15.dp, end = 0.dp, top = 0.dp, bottom = 30.dp)) {
                    Image(
                        modifier =
                            Modifier.padding(start = 0.dp, end = 0.dp, top = 0.dp, bottom = 0.dp)
                                .width(101.dp)
                                .height(101.dp)
                                .clip(CircleShape)
                                .background(color = MaterialTheme.colorScheme.background),
                        painter = painterResource(id = R.drawable.gomeet_logo),
                        contentDescription = "image description",
                        contentScale = ContentScale.None)
                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally, // Center horizontally within this column
                        modifier = Modifier.padding(0.dp)) {
                          Row(
                              horizontalArrangement = Arrangement.Start,
                              verticalAlignment = Alignment.CenterVertically,
                              modifier = Modifier.padding(start = 30.dp)) {
                                Text(
                                    "Username",
                                    textAlign = TextAlign.Center,
                                    style =
                                        TextStyle(
                                            fontSize = 20.sp,
                                            lineHeight = 16.sp,
                                            fontFamily = FontFamily(Font(R.font.roboto)),
                                            fontWeight = FontWeight(1000),
                                            color = MaterialTheme.colorScheme.onBackground,
                                            textAlign = TextAlign.Center,
                                            letterSpacing = 0.5.sp,
                                        ))
                              }
                          Text(
                              text = "@usertag",
                              style =
                                  TextStyle(
                                      fontSize = 15.sp,
                                      lineHeight = 16.sp,
                                      fontFamily = FontFamily(Font(R.font.roboto)),
                                      fontWeight = FontWeight(600),
                                      color = MaterialTheme.colorScheme.onBackground,
                                      textAlign = TextAlign.Center,
                                      letterSpacing = 0.5.sp,
                                  ))
                        }
                  }

              Row(
                  horizontalArrangement = Arrangement.spacedBy(5.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(horizontal = 15.dp, vertical = 15.dp)) {
                    // Edit Profile button
                    Button(
                        onClick = { /*TODO*/},
                        modifier = Modifier.height(40.dp).width(180.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECEFF1))) {
                          Text(text = "Follow", color = Color.Black)
                        }

                    Spacer(Modifier.width(5.dp))

                    Button(
                        onClick = { /*TODO*/},
                        modifier = Modifier.height(40.dp).width(180.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECEFF1))) {
                          Text(text = "Message", color = Color.Black)
                        }

                    Spacer(Modifier.width(5.dp))
                  }

              Spacer(modifier = Modifier.height(30.dp))
              Row(
                  horizontalArrangement = Arrangement.SpaceEvenly,
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.fillMaxWidth()) {
                    Column {
                      Text(
                          text = "10",
                          style =
                              TextStyle(
                                  fontSize = 20.sp,
                                  lineHeight = 16.sp,
                                  fontFamily = FontFamily(Font(R.font.roboto)),
                                  fontWeight = FontWeight(1000),
                                  color = Color(0xFF2F6673),
                                  textAlign = TextAlign.Center,
                                  letterSpacing = 0.5.sp,
                              ),
                          modifier = Modifier.align(Alignment.CenterHorizontally))
                      Text(
                          text = "Events",
                          style =
                              TextStyle(
                                  fontSize = 13.sp,
                                  lineHeight = 16.sp,
                                  fontFamily = FontFamily(Font(R.font.roboto)),
                                  fontWeight = FontWeight(1000),
                                  color = Color(0xFF2F6673),
                                  textAlign = TextAlign.Center,
                                  letterSpacing = 0.5.sp,
                              ),
                          modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    Divider(
                        modifier =
                            Modifier
                                // .fillMaxHeight()
                                .height(40.dp)
                                .width(2.dp))
                    Column(modifier = Modifier.clickable {}) {
                      Text(
                          text = "10",
                          style =
                              TextStyle(
                                  fontSize = 20.sp,
                                  lineHeight = 16.sp,
                                  fontFamily = FontFamily(Font(R.font.roboto)),
                                  fontWeight = FontWeight(1000),
                                  color = Color(0xFF2F6673),
                                  textAlign = TextAlign.Center,
                                  letterSpacing = 0.5.sp,
                              ),
                          modifier = Modifier.align(Alignment.CenterHorizontally))
                      Text(
                          text = "Followers",
                          style =
                              TextStyle(
                                  fontSize = 13.sp,
                                  lineHeight = 16.sp,
                                  fontFamily = FontFamily(Font(R.font.roboto)),
                                  fontWeight = FontWeight(1000),
                                  color = Color(0xFF2F6673),
                                  textAlign = TextAlign.Center,
                                  letterSpacing = 0.5.sp,
                              ),
                          modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    Divider(
                        modifier =
                            Modifier
                                // .fillMaxHeight()
                                .height(40.dp)
                                .width(2.dp))
                    Column {
                      Text(
                          text = "10",
                          style =
                              TextStyle(
                                  fontSize = 20.sp,
                                  lineHeight = 16.sp,
                                  fontFamily = FontFamily(Font(R.font.roboto)),
                                  fontWeight = FontWeight(1000),
                                  color = Color(0xFF2F6673),
                                  textAlign = TextAlign.Center,
                                  letterSpacing = 0.5.sp,
                              ),
                          modifier = Modifier.align(Alignment.CenterHorizontally))
                      Text(
                          text = "Following",
                          style =
                              TextStyle(
                                  fontSize = 13.sp,
                                  lineHeight = 16.sp,
                                  fontFamily = FontFamily(Font(R.font.roboto)),
                                  fontWeight = FontWeight(1000),
                                  color = Color(0xFF2F6673),
                                  textAlign = TextAlign.Center,
                                  letterSpacing = 0.5.sp,
                              ),
                          modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                  }
              Spacer(modifier = Modifier.height(30.dp))
              Text(
                  text = "Tags",
                  style =
                      TextStyle(
                          fontSize = 18.sp,
                          lineHeight = 16.sp,
                          fontFamily = FontFamily(Font(R.font.roboto)),
                          fontWeight = FontWeight(1000),
                          color = DarkCyan,
                          textAlign = TextAlign.Start,
                          letterSpacing = 0.5.sp,
                      ),
                  modifier =
                      Modifier.width(74.dp)
                          .height(21.dp)
                          .align(Alignment.Start)
                          .padding(start = 15.dp))
              Column(modifier = Modifier.padding(start = 0.dp, end = 0.dp).fillMaxWidth()) {
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(start = 15.dp, end = 15.dp),
                    modifier = Modifier.heightIn(min = 56.dp)) {
                      items(10) {
                        Button(
                            onClick = {},
                            content = { Text("Tag") },
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = NavBarUnselected, contentColor = DarkCyan),
                            border = BorderStroke(1.dp, DarkCyan),
                        )
                      }
                    }
              }
              Spacer(modifier = Modifier.height(10.dp))
              ProfileEventsList("My Events")
              Spacer(modifier = Modifier.height(10.dp))
              ProfileEventsList("History")
            }
      }
}

@Composable
fun MoreActionsButton() {
  var showMenu by remember { mutableStateOf(false) }

  IconButton(onClick = { showMenu = true }) {
    Icon(
        imageVector = Icons.Default.MoreVert,
        contentDescription = "More",
        modifier = Modifier.size(24.dp).rotate(90f), // Rotates the icon by 90 degrees
        tint = DarkCyan)
  }

  DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }, modifier = Modifier) {
    DropdownMenuItem(
        text = { Text("Share Profile") },
        onClick = {
          // Handle Share Profile logic here
          showMenu = false
        })
    DropdownMenuItem(
        text = { Text("Block") },
        onClick = {
          // Handle Block logic here
          showMenu = false
        })
  }
}

@Preview
@Composable
fun OthersProfilePreview() {
  OthersProfile(nav = NavigationActions(rememberNavController()))
}
