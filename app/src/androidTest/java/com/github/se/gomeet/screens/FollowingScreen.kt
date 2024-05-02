package com.github.se.gomeet.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen

class FollowingScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<FollowingScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("Following") }) {}
