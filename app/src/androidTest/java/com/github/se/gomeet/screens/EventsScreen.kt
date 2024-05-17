package com.github.se.gomeet.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen

class EventsScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<EventsScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("CreateUI") }) {}
