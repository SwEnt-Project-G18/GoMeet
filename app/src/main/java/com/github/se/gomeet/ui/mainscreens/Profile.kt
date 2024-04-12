package com.github.se.gomeet.ui.mainscreens

import android.graphics.Paint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.ui.theme.Grey
import com.github.se.gomeet.ui.theme.NavBarUnselected

@Composable
fun Profile(nav: NavigationActions) { // TODO Add parameters to the function
    Scaffold(
        bottomBar = {
            BottomNavigationMenu(
                onTabSelect = { selectedTab ->
                    nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
                },
                tabList = TOP_LEVEL_DESTINATIONS,
                selectedItem = Route.EXPLORE
            )
        },
        modifier = Modifier.verticalScroll(rememberScrollState())) { innerPadding ->
        print(innerPadding)
        Text(
            text = "My Profile",
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 15.dp),
            color = DarkCyan,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Default,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.headlineLarge
        )
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(top = 100.dp)
                .fillMaxSize()
        )
        {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                Image(
                    modifier = Modifier
                        .padding(30.dp)
                        .width(101.dp)
                        .height(101.dp)
                        .clip(CircleShape)
                        .background(color = Color(0xFFD9D9D9)),
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "image description",
                    contentScale = ContentScale.None
                )
                Column {
                    Row {
                        Text(
                            "Name" + " " + "Surname", textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontSize = 20.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily(Font(R.font.roboto)),
                                fontWeight = FontWeight(500),
                                color = Color(0xFF000000),

                                textAlign = TextAlign.Center,
                                letterSpacing = 0.5.sp,
                            )

                        )
                    }
                    Text(
                        text = "@username",
                        style = TextStyle(
                            fontSize = 15.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight(500),
                            color = Color(0xFF000000),

                            textAlign = TextAlign.Center,
                            letterSpacing = 0.5.sp,
                        )

                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .padding(0.dp)
                        .width(177.dp)
                        .height(40.dp)
                        .background(
                            color = Color(0xFFECEFF1),
                            shape = RoundedCornerShape(size = 10.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFECEFF1)
                    )
                ) {
                    Text(text = "Edit Profile", color = Color(0xFF000000))
                }
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .padding(0.dp)
                        .width(177.dp)
                        .height(40.dp)
                        .background(
                            color = Color(0xFFECEFF1),
                            shape = RoundedCornerShape(size = 10.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFECEFF1)
                    )
                ) {
                    Text(text = "Share Profile", color = Color(0xFF000000))
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "10",
                        style = TextStyle(
                            fontSize = 20.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight(500),
                            color = Color(0xFF2F6673),
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.5.sp,
                        ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "Events",
                        style = TextStyle(
                            fontSize = 13.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight(500),
                            color = Color(0xFF2F6673),
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.5.sp,
                        ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                Divider(
                    modifier = Modifier
                        //.fillMaxHeight()
                        .height(40.dp)
                        .width(1.dp)
                )
                Column {
                    Text(
                        text = "10",
                        style = TextStyle(
                            fontSize = 20.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight(500),
                            color = Color(0xFF2F6673),
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.5.sp,
                        ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "Followers",
                        style = TextStyle(
                            fontSize = 13.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight(500),
                            color = Color(0xFF2F6673),
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.5.sp,
                        ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                }
                Divider(
                    modifier = Modifier
                        //.fillMaxHeight()
                        .height(40.dp)
                        .width(1.dp)
                )
                Column {
                    Text(
                        text = "10",
                        style = TextStyle(
                            fontSize = 20.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight(500),
                            color = Color(0xFF2F6673),
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.5.sp,
                        ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "Following",
                        style = TextStyle(
                            fontSize = 13.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight(500),
                            color = Color(0xFF2F6673),
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.5.sp,
                        ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column {
                Text(
                    text = "Tags",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontFamily = FontFamily(Font(R.font.roboto)),
                        fontWeight = FontWeight(700),
                        color = DarkCyan,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.5.sp,
                    ),
                    modifier = Modifier
                        .width(74.dp)
                        .height(17.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(start = 12.dp, end = 8.dp),
                    modifier = Modifier.heightIn(min = 56.dp)

                ) {
                    items(10) {
                        Button(
                            onClick = {},
                            content = {
                                Text("Tag")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NavBarUnselected,
                                contentColor = DarkCyan
                            ),
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

@Composable
fun ProfileEventsList(title: String) {
    Spacer(modifier = Modifier.height(10.dp))
    Column {
        Row {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(R.font.roboto)),
                    fontWeight = FontWeight(700),
                    color = DarkCyan,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp,
                ),
                modifier = Modifier
                    .width(104.dp)
                    .height(17.dp)
            )
            Text(text = "View all", color = Grey)
        }
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(start = 12.dp, end = 8.dp),
            modifier = Modifier.heightIn(min = 56.dp)

        ) {
            items(3) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.chess_demo),
                        contentDescription = "Event 1",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(150.dp)
                            .height(60.dp)
                            .clip(RoundedCornerShape(size = 10.dp))
                    )
                    Text(text = "Chess Tournament", color = DarkCyan)
                    Text(text = "11.04.2024 - 21:00", color = Grey)
                }

            }
        }
    }
}

@Preview
@Composable
fun ProfilePreview() {
    Profile(nav = NavigationActions(rememberNavController()))
}