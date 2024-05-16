package com.github.se.gomeet.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen

class WelcomeScreenScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<WelcomeScreenScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("WelcomeScreenCol") })
