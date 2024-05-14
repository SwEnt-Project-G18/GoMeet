package com.github.se.gomeet.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen

class ManageInvitesScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ManageInvitesScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("ManageInvitesScreen") })
