package com.github.se.gomeet.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class CreateScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CreateScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("CreateUI") }) {

  val createPublicEventButton: KNode = child { hasTestTag("CreatePublic") }
}
