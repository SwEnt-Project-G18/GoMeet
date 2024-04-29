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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
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
import com.github.se.gomeet.ui.navigation.SECOND_LEVEL_DESTINATION
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.ui.theme.Grey
import com.github.se.gomeet.ui.theme.NavBarUnselected

/**
 * Profile screen composable
 *
 * @param nav NavigationActions
 */
@Composable
fun Profile(nav: NavigationActions) { // TODO Add parameters to the function
  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { selectedTab ->
              nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
            },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = Route.PROFILE)
      },
      topBar = {
        Row {
          Text(
              text = "My Profile",
              modifier = Modifier.padding(horizontal = 15.dp, vertical = 15.dp),
              color = DarkCyan,
              fontStyle = FontStyle.Normal,
              fontWeight = FontWeight.SemiBold,
              fontFamily = FontFamily.Default,
              textAlign = TextAlign.Start,
              style = MaterialTheme.typography.headlineLarge)

          IconButton(
              modifier = Modifier.align(Alignment.CenterVertically).padding(end = 15.dp),
              onClick = { /* Handles notification icon click */}) {
                Icon(
                    ImageVector.vectorResource(R.drawable.notifications_icon),
                    contentDescription = "Notifications",
                    modifier = Modifier.size(40.dp).align(Alignment.CenterVertically),
                    tint = Grey)
              }

          // settings icon
          // This will push the icon to the right
          Spacer(Modifier.weight(1f))
          IconButton(
              modifier = Modifier.align(Alignment.CenterVertically).padding(end = 15.dp),
              onClick = {
                nav.navigateTo(SECOND_LEVEL_DESTINATION.first { it.route == Route.SETTINGS })
              }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.settings_icon),
                    contentDescription = "Settings",
                    modifier = Modifier.size(30.dp).align(Alignment.CenterVertically),
                    tint = Grey)
              }
        }
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
                        modifier = Modifier.height(40.dp).width(135.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECEFF1))) {
                          Text(text = "Edit Profile", color = Color.Black)
                        }

                    Spacer(Modifier.width(5.dp))

                    Button(
                        onClick = { /*TODO*/},
                        modifier = Modifier.height(40.dp).width(135.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECEFF1))) {
                          Text(text = "Share Profile", color = Color.Black)
                        }

                    Spacer(Modifier.width(5.dp))

                    // Settings (Add) button
                    Button(
                        onClick = { /*TODO*/},
                        modifier = Modifier.height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECEFF1))) {
                          Icon(
                              imageVector = ImageVector.vectorResource(R.drawable.add_friend),
                              contentDescription = "Settings",
                              modifier = Modifier.size(15.dp),
                              tint = Grey)
                        }
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
                    HorizontalDivider(
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
                    HorizontalDivider(
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
                          .height(20.dp)
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

@Preview
@Composable
fun ProfilePreview() {
  Profile(nav = NavigationActions(rememberNavController()))
}
