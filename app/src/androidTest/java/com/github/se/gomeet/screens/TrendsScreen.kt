package com.github.se.gomeet.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen

class TrendsScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<TrendsScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("TrendsScreen") })
