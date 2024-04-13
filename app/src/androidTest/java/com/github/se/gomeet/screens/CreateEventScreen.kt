package com.github.se.gomeet.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class CreateEventScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CreateEventScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("CreateEvent") }) {

  val title: KNode = child { hasTestTag("Title") }
  val description: KNode = child { hasTestTag("Description") }
  val location: KNode = child { hasTestTag("Location") }
  val date: KNode = child { hasTestTag("Date") }
  val price: KNode = child { hasTestTag("Price") }
  val link: KNode = child { hasTestTag("Link") }
  val postButton: KNode = child { hasTestTag("PostButton") }
}
