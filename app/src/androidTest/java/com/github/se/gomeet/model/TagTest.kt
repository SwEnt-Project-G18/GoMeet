package com.github.se.gomeet.model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.R.*
import io.github.kakaocup.kakao.common.utilities.getResourceString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TagTest {

  @get:Rule val composeTestRule = createComposeRule()
  var success = mutableStateOf(false)
  var tagList = mutableStateOf(emptyList<String>())

  @Test
  fun testTagSelector() {
    composeTestRule.setContent { TagsSelector("Edit Tags", tagList) { success.value = true } }

    composeTestRule.onNodeWithText(getResourceString(string.edit_tags)).assertIsDisplayed()
    composeTestRule.onNodeWithTag("TagList").assertIsDisplayed().performClick()
    composeTestRule
        .onNodeWithText(getResourceString(string.save))
        .assertIsDisplayed()
        .performClick()
    assert(success.value)
    assert(tagList.value.isNotEmpty())
  }
}
