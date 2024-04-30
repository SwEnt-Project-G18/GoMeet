package com.github.se.gomeet.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen

class OtherProfileScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<OtherProfileScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("OtherProfile") }) {}
