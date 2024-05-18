package com.github.se.gomeet.ui.mainscreens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
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

  Column(modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)) {
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
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        placeholder = { Text(text = "Search") },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions =
            KeyboardActions(
                onSearch = {
                  println("Search for $searchText triggered")
                  keyboardController?.hide()
                  coroutineScope.launch { viewModel.performSearch(searchText) }
                })
    )

    Spacer(modifier = Modifier.height(16.dp))
    if (isSearching) {
      Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
      }
    } else if (persons.isNotEmpty()) {
      LazyColumn(modifier = Modifier
          .fillMaxWidth()
          .weight(1f)) {
        items(persons) { item ->
          SearchModuleSnippet(item, nav = nav)
          Divider(
              color = Color.LightGray,
              thickness = 1.dp,
              modifier = Modifier.padding(vertical = 8.dp))
        }
      }
    }
  }
}

@Composable
fun SearchModuleSnippet(item: SearchViewModel.SearchableItem, nav: NavigationActions) {
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
          Modifier
              .padding(vertical = 10.dp)
              .clickable {
                  nav.navigateToScreen(Route.OTHERS_PROFILE.replace("{uid}", item.user.uid))
              }) {
            Image(
                painter = painter,
                contentDescription = "User Icon",
                modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
              Text(
                  text = "${item.user.firstName} ${item.user.lastName}",
                  modifier = Modifier.fillMaxWidth())
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
          modifier = Modifier.padding(vertical = 10.dp)) {
            Image(
                painter = painter,
                contentDescription = "Event Icon",
                modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
              Text(text = "${item.event.title}", modifier = Modifier.fillMaxWidth())
              Text(
                  text =
                      "${item.event.date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))} - ${item.event.time.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                  modifier = Modifier.fillMaxWidth())
              Text("${item.event.description}", color = Color.Gray)
            }
          }
    }
  }
}

@Preview
@Composable
fun PreviewSearchModule() {
  SearchModule(nav = NavigationActions(rememberNavController()), backgroundColor = MaterialTheme.colorScheme.background, contentColor = MaterialTheme.colorScheme.tertiary)
}
