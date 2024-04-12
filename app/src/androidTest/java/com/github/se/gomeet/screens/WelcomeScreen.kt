package com.github.se.gomeet.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class WelcomeScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<WelcomeScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("WelcomeScreen") }) {

  val logInButton: KNode = child { hasTestTag("LogInButton") }
}
