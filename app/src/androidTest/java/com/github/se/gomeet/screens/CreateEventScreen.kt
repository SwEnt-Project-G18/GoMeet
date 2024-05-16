package com.github.se.gomeet.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasTestTag
import io.github.kakaocup.compose.node.element.ComposeScreen

class CreateEventScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CreateEventScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("CreateEvent") })
