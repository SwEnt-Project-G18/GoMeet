package com.github.se.gomeet.ui.authscreens.register

import android.util.Patterns.PHONE
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.github.se.gomeet.ui.theme.DarkGrey
import java.util.Locale


@Composable
fun RegisterNameCountryPhone (callback: (String, String, String, String) -> Unit,
                              textFieldColors: TextFieldColors
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var country by remember { mutableStateOf("") }
    val countries = remember { mutableStateOf(getCountries()) }
    var filteredCountries by remember { mutableStateOf(listOf<String>()) }

    var firstClick by  remember { mutableStateOf(true) }
    var countryValid by  remember { mutableStateOf(false) }
    var validPhoneNumber by  remember { mutableStateOf(false) }
    var validFirstName by  remember { mutableStateOf(false) }
    var validLastName by  remember { mutableStateOf(false) }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround) {

        Text(
            text = "Tell Us More About Yourself",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Centergit
        )

        Spacer(modifier = Modifier.size(screenHeight/20))

        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            singleLine = true,
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(16.dp))

        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            singleLine = true,
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(16.dp))

        Column {
            TextField(
                value = country,
                onValueChange = {
                    country = it
                    filteredCountries =
                        if (it.isEmpty()) {
                            countries.value
                        } else {
                            countries.value.filter { c ->
                                c.lowercase(Locale.getDefault()).startsWith(it.lowercase(Locale.getDefault()))
                            }
                        }
                    expanded = true
                },
                label = { Text("Select Country") },
                singleLine = true,
                colors = textFieldColors,
                readOnly = false,
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = 20.dp, y = 20.dp),  // Adjusts the position directly below the TextField
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)  // Limits the height to display around 5 items
            ) {
                for (c in filteredCountries) {
                    DropdownMenuItem(
                        text = { Text(c) },
                        onClick = {
                            country = c
                            expanded = false
                        }
                    )
                }
            }
        }

        if (!countryValid && !firstClick){
            Text(text = "Country is not valid", color = Color.Red)
        }

        Spacer(modifier = Modifier.size(16.dp))

        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            singleLine = true,
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (!validPhoneNumber && !firstClick){
            Text(text = "Phone Number is not valid", color = Color.Red)
        }

        Spacer(modifier = Modifier.size(screenHeight / 15))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            LinearProgressIndicator(
                modifier = Modifier.padding(top = 20.dp, end = 25.dp),
                progress = { 0.6f },
                color = DarkGrey,
                trackColor = Color.LightGray,
                strokeCap = ProgressIndicatorDefaults.CircularIndeterminateStrokeCap
            )
            IconButton(
                modifier = Modifier
                    .padding(bottom = 2.5.dp, end = 3.dp)
                    .size(screenHeight / 19),
                colors = IconButtonDefaults.outlinedIconButtonColors(),
                onClick = {
                    validLastName = lastName.isNotEmpty() && lastName.length <= 20
                    validFirstName = firstName.isNotEmpty() && firstName.length <= 20
                    validPhoneNumber = phoneNumber.isEmpty() || (PHONE.matcher(phoneNumber).matches()
                            && (phoneNumber.startsWith('0') || phoneNumber.startsWith('+'))
                            && phoneNumber.length >= 10 && phoneNumber.length <= 14)

                    countryValid = country.isEmpty() || countries.value.contains(country)
                    firstClick = false
                    if (validFirstName && validLastName && countryValid && validPhoneNumber){
                        callback(firstName, lastName, country, phoneNumber)
                    }
                }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next",
                    tint = DarkGrey,
                    modifier = Modifier.size(60.dp)
                )
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
        val flag =
            (String(Character.toChars(firstChar)) + String(Character.toChars(secondChar)))
        countriesWithEmojis.add("$countryName $flag")
    }
    return countriesWithEmojis
}