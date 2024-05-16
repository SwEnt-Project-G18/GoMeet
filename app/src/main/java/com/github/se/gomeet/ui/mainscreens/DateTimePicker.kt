package com.github.se.gomeet.ui.mainscreens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.se.gomeet.ui.theme.GoMeetTheme
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
  GoMeetTheme {
    val datePickerColours =
        com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults.colors(
            headerBackgroundColor = MaterialTheme.colorScheme.primary,
            headerTextColor = MaterialTheme.colorScheme.tertiary,
            calendarHeaderTextColor = MaterialTheme.colorScheme.tertiary,
            dateActiveBackgroundColor = MaterialTheme.colorScheme.outlineVariant,
            dateInactiveBackgroundColor = MaterialTheme.colorScheme.primary,
            dateActiveTextColor = MaterialTheme.colorScheme.tertiary,
            dateInactiveTextColor = MaterialTheme.colorScheme.secondary)

    val timePickerColours =
        com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults.colors(
            activeBackgroundColor = MaterialTheme.colorScheme.primary,
            inactiveBackgroundColor = MaterialTheme.colorScheme.primary,
            activeTextColor = MaterialTheme.colorScheme.tertiary,
            inactiveTextColor = MaterialTheme.colorScheme.tertiary,
            inactivePeriodBackground = MaterialTheme.colorScheme.secondary,
            selectorColor = MaterialTheme.colorScheme.secondary,
            selectorTextColor = MaterialTheme.colorScheme.tertiary,
            headerTextColor = MaterialTheme.colorScheme.tertiary,
            borderColor = MaterialTheme.colorScheme.primary)

    val buttonColors =
        ButtonColors(
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            containerColor = MaterialTheme.colorScheme.outlineVariant,
            disabledContentColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.tertiary)

    val buttonShape = RoundedCornerShape(10.dp)

    val textStyle =
        TextStyle(color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)

    val bgColour = MaterialTheme.colorScheme.surface

    val context = LocalContext.current

    val formattedDate by remember {
      derivedStateOf { DateTimeFormatter.ofPattern("dd MMM yyyy").format(pickedDate.value) }
    }
    val formattedTime by remember {
      derivedStateOf { DateTimeFormatter.ofPattern("hh:mm").format(pickedTime.value) }
    }

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Spacer(modifier = Modifier.width(80.dp))

      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center) {
            Button(
                onClick = { dateDialogState.show() }, colors = buttonColors, shape = buttonShape) {
                  Text(text = "Pick date")
                }
            Text(text = formattedDate)
          }
      Column(
          modifier = Modifier.fillMaxSize(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center) {
            Button(
                onClick = { timeDialogState.show() }, colors = buttonColors, shape = buttonShape) {
                  Text(text = "Pick time")
                }
            Text(text = formattedTime)
          }

      MaterialDialog(
          dialogState = dateDialogState,
          buttons = {
            positiveButton(text = "Ok", textStyle = textStyle) {
              Toast.makeText(context, "Clicked ok", Toast.LENGTH_LONG).show()
            }
            negativeButton(text = "Cancel", textStyle = textStyle)
          },
          backgroundColor = bgColour) {
            datepicker(
                initialDate = pickedDate.value,
                title = "Pick a date",
                allowedDateValidator = { it.isAfter(LocalDate.now()) },
                colors = datePickerColours) {
                  pickedDate.value = it
                }
          }
      MaterialDialog(
          dialogState = timeDialogState,
          buttons = {
            positiveButton(text = "Ok", textStyle = textStyle) {
              Toast.makeText(context, "Clicked ok", Toast.LENGTH_LONG).show()
            }
            negativeButton(text = "Cancel", textStyle = textStyle)
          },
          backgroundColor = bgColour) {
            timepicker(
                initialTime = pickedTime.value,
                title = "Pick a time",
                colors = timePickerColours,
                is24HourClock = true) {
                  pickedTime.value = it
                }
          }
    }
  }
}
