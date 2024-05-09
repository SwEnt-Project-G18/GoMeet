package com.github.se.gomeet.model

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.github.se.gomeet.ui.theme.DarkCyan

// TODO: change this to actual tags
enum class Tag {
  tag1,
  tag2,
  tag3,
  tag4,
  tag5,
  tag6,
  tag7,
  tag8,
  tag9,
  tag10,
  tag11,
  tag12,
  tag13,
  tag14,
  tag15,
  tag16,
  tag17,
  tag18,
  tag19,
  tag20,
  tag21,
  tag22,
  tag23,
  tag24,
  tag25,
  tag26,
  tag27,
  tag28,
  tag29,
  tag30
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditTags(
    tags: MutableState<List<String>>,
    showPopup: MutableState<Boolean>,
    onSave: () -> Unit
) {
  Popup(alignment = Alignment.Center, onDismissRequest = { showPopup.value = !showPopup.value }) {
    Box(
        modifier =
            Modifier.background(MaterialTheme.colorScheme.background)
                .width((LocalConfiguration.current.screenWidthDp - 60).dp)
                .height((LocalConfiguration.current.screenHeightDp - 200).dp)
                .padding()
                .shadow(1.dp)) {
          Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier =
                    Modifier.fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 15.dp, start = 15.dp, end = 15.dp, bottom = 15.dp)) {
                  Text(
                      "Edit tags",
                      color = DarkCyan,
                      fontWeight = FontWeight.SemiBold,
                      style = MaterialTheme.typography.titleLarge)
                }
            FlowRow(
                modifier =
                    Modifier.fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .weight(1f, fill = false)
                        .padding(start = 15.dp)) {
                  for (tag in Tag.entries) {
                    if (tags.value.contains(tag.name)) {
                      Button(
                          onClick = { tags.value = tags.value.minus(tag.name) },
                          modifier = Modifier.padding(end = 15.dp, bottom = 5.dp).wrapContentSize(),
                          colors =
                              ButtonDefaults.buttonColors(
                                  containerColor = DarkCyan, contentColor = Color.White),
                          contentPadding = PaddingValues(start = 15.dp, end = 10.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                  Text(tag.name, modifier = Modifier.padding(end = 10.dp))
                                  Icon(
                                      Icons.Default.Check,
                                      contentDescription = null,
                                      modifier = Modifier.size(20.dp))
                                }
                          }
                    } else {
                      OutlinedButton(
                          onClick = { tags.value = tags.value.plus(tag.name) },
                          modifier = Modifier.padding(end = 15.dp, bottom = 5.dp).wrapContentSize(),
                          border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                          contentPadding = PaddingValues(start = 15.dp, end = 10.dp),
                          colors =
                              ButtonColors(
                                  contentColor = MaterialTheme.colorScheme.onBackground,
                                  containerColor = Color.Transparent,
                                  disabledContainerColor = Color.Transparent,
                                  disabledContentColor = MaterialTheme.colorScheme.onBackground)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                  Text(tag.name, modifier = Modifier.padding(end = 10.dp))
                                  Icon(
                                      Icons.Default.Add,
                                      contentDescription = null,
                                      modifier = Modifier.size(20.dp))
                                }
                          }
                    }
                  }
                }
            Row(
                modifier =
                    Modifier.fillMaxWidth().padding(top = 15.dp, end = 15.dp, bottom = 15.dp),
                horizontalArrangement = Arrangement.End) {
                  Text(
                      "Save",
                      fontWeight = FontWeight.SemiBold,
                      color = DarkCyan,
                      modifier = Modifier.clickable { onSave() })
                }
          }
        }
  }
}
