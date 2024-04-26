package com.github.se.gomeet.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class LoginScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<LoginScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("LoginScreen") }) {

  val emailField: KNode = onNode { hasTestTag("EmailField") }
  val passwordField: KNode = onNode { hasTestTag("LogInField") }
  val logInButton: KNode = onNode { hasTestTag("LogInButton") }
}
