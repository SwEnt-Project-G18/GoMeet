package com.github.se.gomeet.ui.authscreens.register

import android.annotation.SuppressLint
import android.util.Patterns.PHONE
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.se.gomeet.model.authentication.getCountries

/**
 * A visual transformation for formatting phone numbers in the international format.
 *
 * This class formats phone numbers to insert spaces at appropriate positions. For example, a phone
 * number like `+41227676111` will be transformed to `+41 22 767 6111`.
 *
 * This transformation is designed to handle the following format:
 * - The first 3 characters, including the `+` sign and country code, are grouped together.
 * - A space follows the country code.
 * - The next 2 characters form the area code.
 * - A space follows the area code.
 * - The next 3 characters form the prefix.
 * - A space follows the prefix.
 * - The remaining characters form the subscriber number.
 *
 * Example usage:
 * ```
 * TextField(
 *     value = phoneNumber,
 *     onValueChange = { phoneNumber = it },
 *     label = { Text("Phone Number") },
 *     singleLine = true,
 *     colors = textFieldColors,
 *     keyboardOptions = KeyboardOptions.Default.copy(
 *         imeAction = ImeAction.Done,
 *         keyboardType = KeyboardType.Phone
 *     ),
 *     visualTransformation = PhoneNumberVisualTransformation(),
 *     modifier = Modifier.fillMaxWidth()
 * )
 * ```
 *
 * @see VisualTransformation
 */
class PhoneNumberVisualTransformation : VisualTransformation {
  override fun filter(text: AnnotatedString): TransformedText {
    val trimmed = if (text.text.length >= 15) text.text.substring(0..14) else text.text
    val formatted = buildAnnotatedString {
      append(trimmed.take(3)) // +41
      if (trimmed.length > 3) append(" ")
      if (trimmed.length > 3) append(trimmed.substring(3, minOf(5, trimmed.length))) // 22
      if (trimmed.length > 5) append(" ")
      if (trimmed.length > 5) append(trimmed.substring(5, minOf(8, trimmed.length))) // 767
      if (trimmed.length > 8) append(" ")
      if (trimmed.length > 8) append(trimmed.substring(8, minOf(12, trimmed.length))) // 6111
    }

    val offsetMapping =
        object : OffsetMapping {
          override fun originalToTransformed(offset: Int): Int {
            return when {
              offset <= 3 -> offset
              offset <= 5 -> offset + 1
              offset <= 8 -> offset + 2
              offset <= 12 -> offset + 3
              else -> offset + 3
            }
          }

          override fun transformedToOriginal(offset: Int): Int {
            return when {
              offset <= 4 -> offset
              offset <= 7 -> offset - 1
              offset <= 11 -> offset - 2
              offset <= 15 -> offset - 3
              else -> offset - 3
            }
          }
        }

    return TransformedText(formatted, offsetMapping)
  }
}

/**
 * This composable function collects personal details such as first name, last name, country, and
 * phone number. It validates the inputs and proceeds with the registration process upon successful
 * validation.
 *
 * @param callback Function to be called with the collected data (first name, last name, country,
 *   and phone number).
 * @param textFieldColors Custom colors for the TextField components used in this Composable.
 */
@SuppressLint("MutableCollectionMutableState")
@Composable
fun RegisterNameCountryPhone(
    callback: (String, String, String, String) -> Unit,
    textFieldColors: TextFieldColors
) {

  val screenHeight = LocalConfiguration.current.screenHeightDp.dp

  var firstName by remember { mutableStateOf("") }
  var lastName by remember { mutableStateOf("") }
  var phoneNumber by remember { mutableStateOf("") }
  var country by remember { mutableStateOf("") }
  val countries = remember { mutableStateOf(getCountries()) }

  var firstClick by remember { mutableStateOf(true) }
  var countryValid by remember { mutableStateOf(false) }
  var validPhoneNumber by remember { mutableStateOf(false) }
  var validFirstName by remember { mutableStateOf(false) }
  var validLastName by remember { mutableStateOf(false) }

  Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceAround) {
        Text(
            text = "Tell Us More About Yourself",
            modifier = Modifier.fillMaxWidth().testTag("Text"),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.size(screenHeight / 20))

        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            singleLine = true,
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth())

        if (!validFirstName && !firstClick) {
          Text(text = "First Name is not valid", color = Color.Red)
        }

        Spacer(modifier = Modifier.size(screenHeight / 60))

        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            singleLine = true,
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth())

        if (!validLastName && !firstClick) {
          Text(text = "Last Name is not valid", color = Color.Red)
        }

        Spacer(modifier = Modifier.size(screenHeight / 60))

        TextField(
            value = phoneNumber,
            onValueChange = {
              // fixes phone number length bug
              if (it.length < 13) {
                phoneNumber = it
              }
            },
            label = { Text("Phone Number") },
            singleLine = true,
            colors = textFieldColors,
            keyboardOptions =
                KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PhoneNumberVisualTransformation())

        if (!validPhoneNumber && !firstClick) {
          Text(text = "Phone Number is not valid", color = Color.Red)
        }

        Spacer(modifier = Modifier.size(screenHeight / 60))
        CountrySuggestionTextField(countries.value, textFieldColors, countries.value[0]) {
            selectedCountry ->
          country = selectedCountry
        }

        if (!countryValid && !firstClick) {
          Text(text = "Country is not valid", color = Color.Red)
        }

        Spacer(modifier = Modifier.size(screenHeight / 15))

        Row(
            modifier = Modifier.fillMaxWidth().testTag("BottomRow"),
            horizontalArrangement = Arrangement.End) {
              LinearProgressIndicator(
                  modifier = Modifier.padding(top = 20.dp, end = 25.dp),
                  progress = { 0.6f },
                  color = MaterialTheme.colorScheme.tertiary,
                  trackColor = Color.LightGray,
                  strokeCap = ProgressIndicatorDefaults.CircularIndeterminateStrokeCap)
              IconButton(
                  modifier = Modifier.padding(bottom = 2.5.dp, end = 3.dp).size(screenHeight / 19),
                  colors = IconButtonDefaults.outlinedIconButtonColors(),
                  onClick = {
                    validLastName = lastName.isNotEmpty() && lastName.length <= 20
                    validFirstName = firstName.isNotEmpty() && firstName.length <= 20
                    validPhoneNumber =
                        phoneNumber.isEmpty() ||
                            (PHONE.matcher(phoneNumber).matches() &&
                                (phoneNumber.startsWith('0') || phoneNumber.startsWith('+')) &&
                                phoneNumber.length >= 10 &&
                                phoneNumber.length <= 14)

                    countryValid = country.isEmpty() || countries.value.contains(country)
                    firstClick = false
                    if (validFirstName && validLastName && countryValid && validPhoneNumber) {
                      callback(firstName, lastName, country, phoneNumber)
                    }
                  }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(60.dp))
                  }
            }
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountrySuggestionTextField(
    total: List<String>,
    textFieldColors: TextFieldColors,
    value: String,
    callback: (String) -> Unit
) {
  var expanded by remember { mutableStateOf(false) }
  var selectedOptionText by remember { mutableStateOf(value) }
  val countries = remember { mutableStateOf(total) }

  ExposedDropdownMenuBox(
      modifier = Modifier.fillMaxWidth().testTag("Country"),
      expanded = expanded,
      onExpandedChange = { expanded = !expanded }) {
        TextField(
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            readOnly = true,
            value = selectedOptionText,
            onValueChange = {},
            label = { Text("Country") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors =
                textFieldColors.copy(focusedTrailingIconColor = MaterialTheme.colorScheme.tertiary))
        ExposedDropdownMenu(
            modifier =
                Modifier.fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.primaryContainer)
                    .testTag("CountryDropdownMenu"),
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
              countries.value.forEach { selectionOption ->
                DropdownMenuItem(
                    modifier =
                        Modifier.align(Alignment.CenterHorizontally)
                            .fillMaxWidth()
                            .testTag("CountryItem"),
                    text = { Text(text = selectionOption) },
                    onClick = {
                      selectedOptionText = selectionOption
                      expanded = false
                      callback(selectedOptionText)
                    })
              }
            }
      }
}
