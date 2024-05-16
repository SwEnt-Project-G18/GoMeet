package com.github.se.gomeet.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen

class CreateScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CreateScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("CreateUI") })
