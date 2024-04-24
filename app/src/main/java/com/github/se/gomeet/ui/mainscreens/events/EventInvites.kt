package com.github.se.gomeet.ui.mainscreens.events

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.github.se.gomeet.viewmodel.EventInviteViewModel

@Composable fun EventInvites(viewModel: EventInviteViewModel) {}

@Preview
@Composable
fun PreviewEventInvites() {
  EventInvites(EventInviteViewModel())
}
