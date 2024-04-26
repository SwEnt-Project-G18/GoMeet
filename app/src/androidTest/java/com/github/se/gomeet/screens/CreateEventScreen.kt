package com.github.se.gomeet.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasTestTag
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class CreateEventScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CreateEventScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("CreateEvent") }) {

  val title: KNode = onNode { hasTestTag("Title") }
  val description: KNode = onNode { hasTestTag("Description") }
  val location: KNode = onNode { hasTestTag("Location") }
  val date: KNode = onNode { hasTestTag("Date") }
  val price: KNode = onNode { hasTestTag("Price") }
  val link: KNode = onNode { hasTestTag("Link") }
  val postButton: KNode = onNode { hasTestTag("PostButton") }
}
