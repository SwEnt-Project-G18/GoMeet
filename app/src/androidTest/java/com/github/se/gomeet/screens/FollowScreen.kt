package com.github.se.gomeet.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen

class FollowScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<FollowScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("FollowingFollower") }) {}
