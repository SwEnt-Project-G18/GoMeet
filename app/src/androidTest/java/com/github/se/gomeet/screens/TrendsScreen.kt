package com.github.se.gomeet.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.github.se.gomeet.ui.navigation.Route
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class TrendsScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<TrendsScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("TrendsScreen") }) {

  val createButton: KNode = child { hasTestTag(Route.CREATE) }
}
