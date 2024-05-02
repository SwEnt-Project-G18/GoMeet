package com.github.se.gomeet.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class EventInfoScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<EventInfoScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("EventInfoScreen") }) {
  val eventHeader: KNode = onNode { hasTestTag("EventHeader") }
}
