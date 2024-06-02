package com.github.se.gomeet.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasTestTag
import io.github.kakaocup.compose.node.element.ComposeScreen

class AddParticipantsScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<AddParticipantsScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("AddParticipants") })
