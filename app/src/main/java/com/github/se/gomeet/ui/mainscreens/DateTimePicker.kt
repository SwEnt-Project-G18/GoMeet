package com.github.se.gomeet.ui.mainscreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.se.gomeet.ui.theme.GoMeetTheme
import com.github.se.gomeet.ui.theme.White
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Composable function for the DateTimePicker. This date picker doesn't allow for past dates to be
 * picked.
 *
 * @param pickedTime The picked time.
 * @param pickedDate The picked date.
 */
@Composable
fun DateTimePicker(pickedTime: MutableState<LocalTime>, pickedDate: MutableState<LocalDate>) {
  val screenWidth = LocalConfiguration.current.screenWidthDp.dp

  GoMeetTheme {
    val datePickerColours =
        com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults.colors(
            headerBackgroundColor = MaterialTheme.colorScheme.background,
            headerTextColor = MaterialTheme.colorScheme.tertiary,
            calendarHeaderTextColor = MaterialTheme.colorScheme.tertiary,
            dateActiveBackgroundColor = MaterialTheme.colorScheme.outlineVariant,
            dateInactiveBackgroundColor = Color.Transparent,
            dateActiveTextColor = MaterialTheme.colorScheme.background,
            dateInactiveTextColor = MaterialTheme.colorScheme.tertiary)

    val timePickerColours =
        com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults.colors(
            activeBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
            inactiveBackgroundColor = MaterialTheme.colorScheme.background,
            activeTextColor = MaterialTheme.colorScheme.tertiary,
            inactiveTextColor = MaterialTheme.colorScheme.tertiary,
            inactivePeriodBackground = MaterialTheme.colorScheme.outlineVariant,
            selectorColor = MaterialTheme.colorScheme.outlineVariant,
            selectorTextColor = MaterialTheme.colorScheme.background,
            headerTextColor = MaterialTheme.colorScheme.tertiary,
            borderColor = MaterialTheme.colorScheme.outlineVariant)

    val buttonColors =
        ButtonColors(
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            containerColor = MaterialTheme.colorScheme.outlineVariant,
            disabledContentColor = MaterialTheme.colorScheme.tertiary,
            contentColor = White)

    val buttonShape = RoundedCornerShape(10.dp)

    val textStyle =
        TextStyle(color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)

    val bgColour = MaterialTheme.colorScheme.background

    val context = LocalContext.current

    val formattedDate by remember {
      derivedStateOf { DateTimeFormatter.ofPattern("dd MMM yyyy").format(pickedDate.value) }
    }
    val formattedTime by remember {
      derivedStateOf { DateTimeFormatter.ofPattern("HH:mm").format(pickedTime.value) }
    }

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center) {
            Button(
                onClick = { dateDialogState.show() }, colors = buttonColors, shape = buttonShape) {
                  Text(text = "Pick date", style = MaterialTheme.typography.titleMedium)
                }
            Text(
                text = formattedDate,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge)
          }
      Spacer(modifier = Modifier.width(screenWidth / 7))
      Column(
          modifier = Modifier.wrapContentSize(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center) {
            Button(
                onClick = { timeDialogState.show() }, colors = buttonColors, shape = buttonShape) {
                  Text(text = "Pick time", style = MaterialTheme.typography.titleMedium)
                }
            Text(
                text = formattedTime,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge)
          }

      MaterialDialog(
          dialogState = dateDialogState,
          buttons = {
            positiveButton(text = "Ok", textStyle = textStyle) {}
            negativeButton(text = "Cancel", textStyle = textStyle)
          },
          backgroundColor = bgColour) {
            datepicker(
                initialDate = pickedDate.value,
                title = "",
                allowedDateValidator = {
                  it.isAfter(LocalDate.now()) || it.isEqual(LocalDate.now())
                },
                colors = datePickerColours) {
                  pickedDate.value = it
                }
          }
      MaterialDialog(
          dialogState = timeDialogState,
          buttons = {
            positiveButton(text = "Ok", textStyle = textStyle) {}
            negativeButton(text = "Cancel", textStyle = textStyle)
          },
          backgroundColor = bgColour) {
            timepicker(
                initialTime = pickedTime.value,
                title = " ",
                colors = timePickerColours,
                is24HourClock = true) {
                  pickedTime.value = it
                }
          }
    }
  }
}
