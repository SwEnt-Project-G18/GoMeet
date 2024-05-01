package com.github.se.gomeet.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class EventsScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
        ComposeScreen<EventsScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("CreateUI") }) {
        }

