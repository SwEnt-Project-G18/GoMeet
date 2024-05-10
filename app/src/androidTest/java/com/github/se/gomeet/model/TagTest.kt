package com.github.se.gomeet.model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class TagTest {

  @get:Rule val composeTestRule = createComposeRule()
  var success = mutableStateOf(false)
  var tagList = mutableStateOf(emptyList<String>())

  @Test
  fun testTagSelector() {
    composeTestRule.setContent { TagsSelector("Edit Tags", tagList) { success.value = true } }

    composeTestRule.onNodeWithText("Edit Tags").assertIsDisplayed()
    composeTestRule.onNodeWithTag("TagList").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("Save").assertIsDisplayed().performClick()
    assert(success.value)
    assert(tagList.value.isNotEmpty())
  }
}
