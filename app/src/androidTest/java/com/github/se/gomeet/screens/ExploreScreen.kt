package com.github.se.gomeet.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class ExploreScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ExploreScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("CreateUI") }) {
  val map: KNode = onNode { hasTestTag("Map") }
  val currentLocationButton = onNode { hasTestTag("CurrentLocationButton") }
  val searchBar = onNode { hasText("Search") }
}
