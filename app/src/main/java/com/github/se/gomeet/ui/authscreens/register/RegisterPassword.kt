package com.github.se.gomeet.ui.authscreens.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.se.gomeet.ui.theme.DarkCyan

@Composable
fun RegisterPassword(callback: (String) -> Unit, textFieldColors: TextFieldColors) {
    var password by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()  // Optionally clear focus or handle other actions
                    if (password.isNotBlank()) {
                        callback(password)
                    }
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (password.isNotBlank()) {
                    callback(password)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(12.dp)
        ) {
            Text("Submit")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewRegisterPassword() {
    RegisterPassword(
        callback = { password -> println("Preview Password: $password") },
        textFieldColors = TextFieldDefaults.colors(
            focusedTextColor = DarkCyan,
            unfocusedTextColor = DarkCyan,
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            cursorColor = DarkCyan,
            focusedLabelColor = MaterialTheme.colorScheme.tertiary,
            focusedIndicatorColor = MaterialTheme.colorScheme.tertiary))
}