package com.github.se.gomeet.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen

class FollowersScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<FollowersScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("Followers") }) {}
