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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Tag that users can add to their profile and events */
enum class Tag(val tagName: String) {
    //Quick set of tag for a start
    //TODO: Add more tags once a search feature is available.
    Tag0("Networking"),
    Tag1("Conference"),
    Tag2("Workshop"),
    Tag3("Presentation"),
    Tag4("Meeting"),
    Tag5("Webinar"),
    Tag6("Seminar"),
    Tag7("Business Event"),
    Tag8("Hackathon"),
    Tag9("Team Building"),
    Tag10("Concert"),
    Tag11("Festival"),
    Tag12("Live Music"),
    Tag13("Performance"),
    Tag14("Art Exhibition"),
    Tag15("Theater"),
    Tag16("Movie Night"),
    Tag17("Game Night"),
    Tag18("Board Games"),
    Tag19("Sports Event"),
    Tag20("Fitness Class"),
    Tag21("Dance Class"),
    Tag22("Cooking Class"),
    Tag23("Date Night"),
    Tag24("Social Gathering"),
    Tag25("Happy Hour"),
    Tag26("Dinner Party"),
    Tag27("Picnic"),
    Tag28("Student Association"),
    Tag29("Charity Event");

    companion object {
        fun fromTagName(tagName: String): Tag? {
            return entries.find { it.tagName == tagName }
        }
    }
}
/**
 * Composable function to display a menu that allows the user to choose tags.
 *
 * @param title: The title of the menu.
 * @param tags: The list of selected tags, this list will be updated by this function when the user
 *   (un)selects a tag.
 * @param onSave: the code to execute when the Save button is pressed.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagsSelector(title: String, tags: MutableState<List<String>>, onSave: () -> Unit) {
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
                    title,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.SemiBold,
                    style =
                        MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold))
              }
          FlowRow(
              modifier =
                  Modifier.fillMaxSize()
                      .verticalScroll(rememberScrollState())
                      .weight(1f, fill = false)
                      .padding(start = 15.dp)
                      .testTag("TagList")) {
                for (tag in Tag.entries) {
                  if (tags.value.contains(tag.tagName)) {
                    Button(
                        onClick = { tags.value = tags.value.minus(tag.tagName) },
                        modifier =
                            Modifier.padding(end = 15.dp, bottom = 5.dp)
                                .wrapContentSize()
                                .testTag("Tag"),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.outlineVariant,
                                contentColor = Color.White),
                        contentPadding = PaddingValues(start = 15.dp, end = 10.dp)) {
                          Row(
                              verticalAlignment = Alignment.CenterVertically,
                              horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(tag.tagName, modifier = Modifier.padding(end = 10.dp))
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp))
                              }
                        }
                  } else {
                    OutlinedButton(
                        onClick = { tags.value = tags.value.plus(tag.tagName) },
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
                                Text(tag.tagName, modifier = Modifier.padding(end = 10.dp))
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
              modifier = Modifier.fillMaxWidth().padding(top = 15.dp, end = 15.dp, bottom = 15.dp),
              horizontalArrangement = Arrangement.End) {
                Text(
                    "Save",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.clickable { onSave() })
              }
        }
      }
}
