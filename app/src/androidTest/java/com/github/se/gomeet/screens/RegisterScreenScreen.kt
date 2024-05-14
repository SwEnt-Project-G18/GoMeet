package com.github.se.gomeet.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen

class RegisterScreenScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<RegisterScreenScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("RegisterScreen") }) {}
