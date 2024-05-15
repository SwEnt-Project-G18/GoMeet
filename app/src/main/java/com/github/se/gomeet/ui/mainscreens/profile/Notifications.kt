package com.github.se.gomeet.ui.mainscreens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.gomeet.R
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.repository.EventRepository
import com.github.se.gomeet.model.repository.UserRepository
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.navigation.BottomNavigationMenu
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.viewmodel.EventViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Notifications(nav: NavigationActions, currentUserID: String) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val userViewModel = UserViewModel(UserRepository(Firebase.firestore))
    val eventViewModel = EventViewModel(null, EventRepository(Firebase.firestore))

    var isLoaded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val toUpdate = remember { mutableListOf<Event>() }
    val user = remember { mutableStateOf<GoMeetUser?>(null) }
    val eventsList = remember { mutableStateListOf<Event>() }
    val eventToCreatorMap = remember { mutableStateMapOf<Event, String>() }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            fetchUserAndEvents(userViewModel, eventViewModel, currentUserID, user, eventsList, eventToCreatorMap)
            isLoaded = true
        }
    }

    Scaffold(
        modifier = Modifier.testTag("NotificationsScreen"),
        topBar = { NotificationsTopBar(pagerState, coroutineScope, screenHeight, nav, currentUserID, eventViewModel, userViewModel, toUpdate) },
        bottomBar = { BottomNavigation(nav) }
    ) { innerPadding ->
        if (isLoaded) {
            HorizontalPager(state = pagerState, modifier = Modifier.padding(innerPadding)) { page ->
                when (page) {
                    0 -> PageInvitationsNotifications(
                        listEvent = eventsList,
                        userViewModel = userViewModel,
                        currentUserId = currentUserID,
                        initialClicked = false,
                        callback = { event -> toUpdate.add(event) }
                    )
                    1 -> {
                        // TODO: Implement the page for messages notifications
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

suspend fun fetchUserAndEvents(
    userViewModel: UserViewModel,
    eventViewModel: EventViewModel,
    currentUserID: String,
    user: MutableState<GoMeetUser?>,
    eventsList: SnapshotStateList<Event>,
    eventToCreatorMap: SnapshotStateMap<Event, String>
) {
    val currentUser = userViewModel.getUser(currentUserID)

    currentUser?.let {
        user.value = it
        val events = mutableListOf<Event>()
        val creatorMap = mutableMapOf<Event, String>()

        it.pendingRequests.forEach { request ->
            val invitedEvent = eventViewModel.getEvent(request.eventId)
            invitedEvent?.let { event ->
                events.add(event)
                val creatorName = userViewModel.getUser(event.creator)?.username ?: "GoMeetUser"
                creatorMap[event] = creatorName
            }
        }

        eventsList.clear()
        eventsList.addAll(events)
        eventToCreatorMap.clear()
        eventToCreatorMap.putAll(creatorMap)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotificationsTopBar(
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    screenHeight: Dp,
    nav: NavigationActions,
    currentUserID: String,
    eventViewModel: EventViewModel,
    userViewModel: UserViewModel,
    toUpdate: List<Event>
) {
    Column {
        TopAppBar(nav, currentUserID, eventViewModel, userViewModel, toUpdate)
        TabRow(pagerState, coroutineScope, screenHeight)
    }
}

@Composable
fun TopAppBar(nav: NavigationActions, currentUserID: String, eventViewModel: EventViewModel, userViewModel: UserViewModel, toUpdate: List<Event>) {
    Box(contentAlignment = Alignment.Center) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    toUpdate.forEach {
                        event ->
                        eventViewModel.editEvent(event)

                        if (event.participants.contains(currentUserID)) {
                            userViewModel.userAcceptsInvitation(event.eventID, currentUserID)
                        } else {
                            userViewModel.userRefusesInvitation(event.eventID, currentUserID)
                        }
                    }
                    nav.goBack()
                }
            ) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Go back")
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabRow(pagerState: PagerState, coroutineScope: CoroutineScope, screenHeight: Dp) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .height(screenHeight / 20)
                .clickable { coroutineScope.launch { pagerState.animateScrollToPage(0) } }
        ) {
            Text(
                text = "Invitations",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (pagerState.currentPage == 0) FontWeight.Bold else FontWeight.Normal
                )
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .height(screenHeight / 20)
                .clickable { coroutineScope.launch { pagerState.animateScrollToPage(1) } }
        ) {
            Text(
                text = "Messages",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (pagerState.currentPage == 1) FontWeight.Bold else FontWeight.Normal
                )
            )
        }
    }
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        val canvasWidth = size.width
        drawLine(
            color = Color.Black,
            start = if (pagerState.currentPage == 0) Offset(x = 0f, y = 0f) else Offset(x = canvasWidth / 2, y = 0f),
            end = if (pagerState.currentPage == 0) Offset(x = canvasWidth / 2, y = 0f) else Offset(x = canvasWidth, y = 0f),
            strokeWidth = 5f
        )
    }
    Spacer(modifier = Modifier.height(screenHeight / 30))
}

@Composable
fun BottomNavigation(nav: NavigationActions) {
    BottomNavigationMenu(
        onTabSelect = { selectedTab ->
            nav.navigateTo(TOP_LEVEL_DESTINATIONS.first { it.route == selectedTab })
        },
        tabList = TOP_LEVEL_DESTINATIONS,
        selectedItem = Route.NOTIFICATIONS
    )
}

@Composable
fun PageInvitationsNotifications(
    listEvent: List<Event>,
    userViewModel: UserViewModel,
    currentUserId: String,
    initialClicked: Boolean,
    callback: (Event) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        listEvent.forEach { event ->
            InvitationsNotificationsWidget(
                event = event,
                userViewModel = userViewModel,
                currentUserId = currentUserId,
                initialClicked = initialClicked,
                callback = callback
            )
        }
    }
}


@Composable
fun InvitationsNotificationsWidget(
    event: Event,
    userViewModel: UserViewModel,
    currentUserId: String,
    initialClicked: Boolean,
    callback: (Event) -> Unit
) {
    var clicked by rememberSaveable { mutableStateOf(initialClicked) }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val density = LocalDensity.current

    // Example logic to calculate text size based on screen width
    val smallTextSize = with(density) { screenWidth.toPx() / 85 }
    val bigTextSize = with(density) { screenWidth.toPx() / 60 }

    val creatorName by produceState<String?>(initialValue = null) {
        value = userViewModel.getUsername(event.creator)
    }

    Row(Modifier.padding(start = 10.dp)) {
        Text(
            text = creatorName ?: "Loading...",
            style = TextStyle(
                fontSize = 11.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily(Font(R.font.roboto)),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF202124),
                letterSpacing = 0.5.sp,
            ),
            modifier = Modifier.testTag("InviterUserName")
        )
        Spacer(modifier = Modifier.width(2.5.dp))
        Text(
            text = "invited you to attend ",
            style = TextStyle(
                fontSize = 11.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily(Font(R.font.roboto)),
                fontWeight = FontWeight(700),
                color = Color(0xFF202124),
                letterSpacing = 0.5.sp,
            )
        )
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 5.dp)
            .testTag("EventCard"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(2.dp, DarkCyan)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = event.title,
                    style = TextStyle(
                        fontSize = bigTextSize.sp,
                        lineHeight = 20.sp,
                        fontFamily = FontFamily(Font(R.font.roboto)),
                        fontWeight = FontWeight(700),
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = 0.25.sp,
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        creatorName ?: "DefaultUsername",
                        style = TextStyle(
                            fontSize = smallTextSize.sp,
                            lineHeight = 24.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight(700),
                            color = MaterialTheme.colorScheme.onBackground,
                            letterSpacing = 0.15.sp,
                        ),
                        modifier = Modifier.padding(top = 5.dp)
                    )
                }

                val currentDate = LocalDate.now()
                val startOfWeek = currentDate.with(java.time.DayOfWeek.MONDAY)
                val endOfWeek = startOfWeek.plusDays(6)
                val eventDateTime = event.date.atStartOfDay()
                val isThisWeek = eventDateTime.toLocalDate() in startOfWeek..endOfWeek
                val dateFormat =
                    if (isThisWeek) {
                        SimpleDateFormat("EEEE - HH:mm", Locale.getDefault())
                    } else {
                        SimpleDateFormat("dd/MM/yy - HH:mm", Locale.getDefault())
                    }
                val dateString =
                    dateFormat.format(Date.from(event.date.atStartOfDay(ZoneId.systemDefault()).toInstant()))

                Column {
                    Text(
                        dateString,
                        style = TextStyle(
                            fontSize = smallTextSize.sp,
                            lineHeight = 20.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight(700),
                            color = MaterialTheme.colorScheme.onBackground,
                            letterSpacing = 0.25.sp,
                        ),
                        modifier = Modifier.testTag("EventDate")
                    )
                    Row {
                        Button(
                            onClick = {
                                clicked = !clicked
                                callback(event.copy(pendingParticipants = event.pendingParticipants.minus(currentUserId), participants = event.participants.plus(currentUserId)))
                            },
                            content = {
                                Text("Accept")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Green,
                                contentColor = Color.White
                            ),
                            border = BorderStroke(1.dp, DarkCyan),
                            enabled = !clicked,
                            modifier = Modifier.testTag("AcceptButton")
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = {
                                clicked = !clicked
                                callback(event.copy(pendingParticipants = event.pendingParticipants.minus(currentUserId)))
                            },
                            content = {
                                Text("Decline")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red, contentColor = Color.White
                            ),
                            border = BorderStroke(1.dp, DarkCyan),
                            enabled = !clicked,
                            modifier = Modifier.testTag("DeclineButton")
                        )
                    }
                }
            }
            Image(
                painter = painterResource(id = R.drawable.gomeet_logo),
                contentDescription = "Event Picture",
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(3f / 1.75f)
                    .clipToBounds()
                    .padding(end = 5.dp)
                    .testTag("EventImage"),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

