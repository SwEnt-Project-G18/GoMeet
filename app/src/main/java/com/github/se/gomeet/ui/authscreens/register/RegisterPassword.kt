package com.github.se.gomeet.ui.authscreens.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.ui.theme.DarkGrey

@Composable
fun RegisterPassword(callback: (String) -> Unit, textFieldColors: TextFieldColors) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var firstClick by remember { mutableStateOf(true) }
    var passwordsMatch by remember { mutableStateOf(false) }
    var isEmpty by remember { mutableStateOf(true) }
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround) {

        Spacer(modifier = Modifier.size(screenHeight/40))

        Text(
            text = "Please enter your password.",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.size(screenHeight/40))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(16.dp))

        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (isEmpty && !firstClick){
            Text(text = "Your Password cannot be empty", color = Color.Red)
        }

        if (!passwordsMatch && !firstClick){
            Text(text = "Your Passwords should match", color = Color.Red)
        }

        Spacer(modifier = Modifier.size(screenHeight / 15))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            LinearProgressIndicator(
                modifier = Modifier.padding(top = 20.dp, end = 25.dp),
                progress = { 0.4f },
                color = DarkGrey,
                trackColor = Color.LightGray,
                strokeCap = ProgressIndicatorDefaults.CircularIndeterminateStrokeCap
            )
            IconButton(
                modifier = Modifier.padding(bottom = 2.5.dp, end = 3.dp).size(screenHeight / 19),
                colors = IconButtonDefaults.outlinedIconButtonColors(),
                onClick = {
                    firstClick = false
                    passwordsMatch = password.equals(confirmPassword)
                    isEmpty = password.isEmpty()
                    if (!isEmpty && passwordsMatch) {
                        callback(password)
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