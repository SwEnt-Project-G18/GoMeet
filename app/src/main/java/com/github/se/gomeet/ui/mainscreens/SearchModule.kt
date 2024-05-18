package com.github.se.gomeet.ui.mainscreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.github.se.gomeet.R
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.viewmodel.SearchViewModel
import com.google.android.gms.maps.model.LatLng
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

@Composable
fun SearchModule(nav: NavigationActions, backgroundColor: Color, contentColor: Color) {
  val viewModel = viewModel<SearchViewModel>()
  val searchText by viewModel.searchText.collectAsState()
  val persons by viewModel.searchQuery.collectAsState()
  val isSearching by viewModel.isSearching.collectAsState()
  val coroutineScope = rememberCoroutineScope()
  val focusRequester = remember { FocusRequester() }
  val keyboardController = LocalSoftwareKeyboardController.current

  Column(modifier = Modifier.padding(16.dp)) {
    TextField(
        value = searchText,
        leadingIcon = {
          IconButton(onClick = { nav.navigateToScreen(Route.MESSAGE_CHANNELS) }) {
            Icon(
                ImageVector.vectorResource(R.drawable.gomeet_icon),
                contentDescription = null,
                tint = contentColor)
          }
        },
        onValueChange = viewModel::onSearchTextChange,
        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
        placeholder = { Text(text = "Search") },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions =
            KeyboardActions(
                onSearch = {
                  keyboardController?.hide()
                  coroutineScope.launch { viewModel.performSearch(searchText) }
                }),
        colors =
            TextFieldDefaults.colors(
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                disabledContainerColor = backgroundColor,
            ))

    Spacer(modifier = Modifier.height(16.dp))
    if (isSearching) {
      Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = contentColor)
      }
    } else if (persons.isNotEmpty() && searchText.isNotEmpty()) {
      LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
        items(persons) { item ->
          SearchModuleSnippet(item, nav = nav, backgroundColor = backgroundColor)
          Spacer(modifier = Modifier.height(8.dp))
        }
      }
    }
  }
}

@Composable
fun SearchModuleSnippet(
    item: SearchViewModel.SearchableItem,
    nav: NavigationActions,
    backgroundColor: Color
) {
  when (item) {
    is SearchViewModel.SearchableItem.User -> {
      val painter: Painter =
          if (item.user.profilePicture.isNotEmpty()) {
            rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(data = item.user.profilePicture)
                    .apply(
                        block =
                            fun ImageRequest.Builder.() {
                              crossfade(true)
                              placeholder(R.drawable.gomeet_icon)
                            })
                    .build())
          } else {
            painterResource(id = R.drawable.gomeet_icon)
          }
      Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier =
              Modifier.padding(vertical = 8.dp)
                  .clickable {
                    nav.navigateToScreen(Route.OTHERS_PROFILE.replace("{uid}", item.user.uid))
                  }
                  .background(color = backgroundColor, shape = RoundedCornerShape(10.dp))) {
            Image(
                painter = painter,
                contentDescription = "User Icon",
                modifier = Modifier.size(24.dp).padding(4.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
              Text(
                  text = "${item.user.firstName} ${item.user.lastName}",
                  modifier = Modifier.fillMaxWidth().padding(8.dp),
                  color = MaterialTheme.colorScheme.onBackground)
              Text("@${item.user.username}", color = Color.Gray)
            }
          }
    }
    is SearchViewModel.SearchableItem.Event -> {
      val painter: Painter =
          if (item.event.images.isNotEmpty()) {
            rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(data = item.event.images[0])
                    .apply(
                        block =
                            fun ImageRequest.Builder.() {
                              crossfade(true)
                              placeholder(R.drawable.gomeet_icon)
                            })
                    .build())
          } else {
            painterResource(id = R.drawable.gomeet_icon)
          }
      Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier =
              Modifier.padding(vertical = 8.dp)
                  .background(color = backgroundColor, shape = RoundedCornerShape(10.dp))
                  .clickable {
                    nav.navigateToEventInfo(
                        eventId = item.event.eventID,
                        title = item.event.title,
                        date = item.event.date.toString(),
                        time = item.event.time.toString(),
                        description = item.event.description,
                        organizer = item.event.creator,
                        loc = LatLng(item.event.location.latitude, item.event.location.longitude),
                        rating = 0.0)
                  }) {
            Image(
                painter = painter,
                contentDescription = "Event Icon",
                modifier = Modifier.size(64.dp).padding(8.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
              Text(
                  text = "${item.event.title}",
                  modifier = Modifier.fillMaxWidth().padding(8.dp),
                  color = MaterialTheme.colorScheme.onBackground)
              Text(
                  text =
                      "${item.event.date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))} - ${item.event.time.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                  modifier = Modifier.fillMaxWidth())
              val croppedDescription =
                  if (item.event.description.length > 150) item.event.description.take(150) + "..."
                  else item.event.description
              Text(croppedDescription, color = Color.Gray)
            }
          }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewSearchModule() {
  SearchModule(
      nav = NavigationActions(rememberNavController()),
      backgroundColor = MaterialTheme.colorScheme.background,
      contentColor = MaterialTheme.colorScheme.tertiary)
}
