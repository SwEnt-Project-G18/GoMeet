package com.github.se.gomeet.ui.mainscreens.events

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.github.se.gomeet.viewmodel.EventInviteViewModel

/**
 * Composable function for the EventInvites screen.
 * 
 * @param eventInviteViewModel The view model for the EventInvites screen.
 
 */
@Composable fun EventInvites(eventInviteViewModel: EventInviteViewModel) {}

@Preview
@Composable
fun PreviewEventInvites() {
  EventInvites(EventInviteViewModel())
}
