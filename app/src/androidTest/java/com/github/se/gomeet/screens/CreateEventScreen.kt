package com.github.se.gomeet.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasTestTag
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class CreateEventScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CreateEventScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("CreateEvent") }) {
  val title: KNode = onNode { hasText("Title") }
  val description: KNode = onNode { hasText("Description") }
  val location: KNode = onNode { hasText("Location") }
  val dropDownMenu: KNode = onNode { hasTestTag("DropdownMenu") }
  val date: KNode = onNode { hasText("Date") }
  val price: KNode = onNode { hasText("Price") }
  val link: KNode = onNode { hasText("Link") }
  val postButton: KNode = onNode { hasText("Post") }
  val dropdownMenuItem: KNode = onNode { hasTestTag("DropdownMenuItem") }
}
