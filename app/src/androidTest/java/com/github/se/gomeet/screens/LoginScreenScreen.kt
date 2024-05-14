package com.github.se.gomeet.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen

class LoginScreenScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<LoginScreenScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("LoginScreen") })
