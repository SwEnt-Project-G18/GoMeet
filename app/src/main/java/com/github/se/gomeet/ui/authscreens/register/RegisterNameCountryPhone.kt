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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.Locale

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
  val country by remember { mutableStateOf("") }
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
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            singleLine = true,
            colors = textFieldColors,
            keyboardOptions =
                KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done, keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth())

        if (!validPhoneNumber && !firstClick) {
          Text(text = "Phone Number is not valid", color = Color.Red)
        }

        Spacer(modifier = Modifier.size(screenHeight / 60))
        CountrySuggestionTextField(countries.value, textFieldColors, countries.value[0]) {}

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

fun getCountries(): ArrayList<String> {
  val isoCountryCodes: Array<String> = Locale.getISOCountries()
  val countriesWithEmojis: ArrayList<String> = arrayListOf()
  for (countryCode in isoCountryCodes) {
    val locale = Locale("", countryCode)
    val countryName: String = locale.displayCountry
    val flagOffset = 0x1F1E6
    val asciiOffset = 0x41
    val firstChar = Character.codePointAt(countryCode, 0) - asciiOffset + flagOffset
    val secondChar = Character.codePointAt(countryCode, 1) - asciiOffset + flagOffset
    val flag = (String(Character.toChars(firstChar)) + String(Character.toChars(secondChar)))
    countriesWithEmojis.add("$countryName $flag")
  }
  return countriesWithEmojis
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
                    modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth().testTag("CountryItem"),
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
