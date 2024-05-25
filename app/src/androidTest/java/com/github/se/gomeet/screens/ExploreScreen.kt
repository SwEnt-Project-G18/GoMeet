package com.github.se.gomeet.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen

class ExploreScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ExploreScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("ExploreUI") })
