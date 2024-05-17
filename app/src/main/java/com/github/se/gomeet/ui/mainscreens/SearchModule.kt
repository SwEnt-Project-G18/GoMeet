package com.github.se.gomeet.ui.mainscreens

import androidx.compose.foundation.Image
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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.github.se.gomeet.R
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
                item { persons.forEach { person -> SearchModuleSnippet(person) } }
            }
        }
    }
}

@Composable
fun SearchModuleSnippet(item: SearchViewModel.SearchableItem) {
    when (item) {
        is SearchViewModel.SearchableItem.User -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 10.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.gomeet_logo),
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
                                    placeholder(R.drawable.gomeet_logo)
                                })
                            .build())
                } else {
                    painterResource(id = R.drawable.gomeet_logo)
                }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 10.dp)) {
                Image(
                    painter = painter,
                    contentDescription = "User Icon",
                    modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "${item.event.title} - ${item.event.date.toString()}",
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
    SearchModule()
}
