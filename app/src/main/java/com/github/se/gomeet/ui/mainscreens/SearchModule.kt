package com.github.se.gomeet.ui.mainscreens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.viewmodel.SearchViewModel

@Composable
fun SearchModule() {
  val viewModel = viewModel<SearchViewModel>()
  val searchText by viewModel.searchText.collectAsState()
  val persons by viewModel.searchQuery.collectAsState()
  val isSearching by viewModel.isSearching.collectAsState()

    println("### Persons: ${persons.toString()}")

  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    TextField(
        value = searchText,
        onValueChange = viewModel::onSearchTextChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = "Search") })
    Spacer(modifier = Modifier.height(16.dp))
    if (isSearching) {
      Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
      }
    } else {
      LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
        item { persons?.forEach { person -> SearchModuleSnippet(person) } }
      }
    }
  }
}

@Composable
fun SearchModuleSnippet(person: GoMeetUser) {
  Text(
      text = "${person.firstName} ${person.lastName}",
      modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))
}

@Preview
@Composable
fun PreviewSearchModule() {
  SearchModule()
}
