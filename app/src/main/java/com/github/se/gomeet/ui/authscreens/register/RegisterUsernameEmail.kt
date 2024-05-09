package com.github.se.gomeet.ui.authscreens.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.se.gomeet.model.user.GoMeetUser
import com.github.se.gomeet.ui.theme.Cyan
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun RegisterUsernameEmail(callback: (String, String) -> Unit,
                          userViewModel: UserViewModel,
                          textFieldColors: TextFieldColors) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isValidEmail by remember { mutableStateOf(false) }
    var isValidUsername by remember { mutableStateOf(false) }
    var firstClick by remember { mutableStateOf(true) }
    var charactersExceeded by remember { mutableStateOf(false) }
    var allUsers by remember { mutableStateOf<List<GoMeetUser>?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            allUsers = userViewModel.getAllUser()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround) {
        Text(
            text = "Welcome to GoMeet !\nPlease enter a username and an email.",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge
        )

        OutlinedTextField(
            value = username,
            onValueChange = {
                if (it.length < 26) {
                    charactersExceeded = false
                    username = it
                } else{
                    charactersExceeded = true
                }
            },
            colors = textFieldColors,
            label = { Text("Username") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth()
        )

        if (charactersExceeded){
            Text(text = "Your Username Should Not Exceed 26 characters", color = Color.Red)
        }

        if (!firstClick && !isValidUsername){
            Text(text = "The Username is not valid or already taken", color = Color.Red)
        }

        OutlinedTextField(
            value = email,
            onValueChange = {
                    email = it
                    isValidEmail = validateEmail(email)
                },
            colors = textFieldColors,
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth()
        )

        if (!isValidEmail && !firstClick) {
            Text("The Email is not valid or already taken", color = Color.Red)
        }

        Spacer(Modifier.height(screenHeight/10))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
            CircularProgressIndicator(
                progress = { 0.2f },
                color = DarkCyan,
                trackColor = Color.LightGray,
            )

            Spacer(modifier = Modifier.width (10.dp))

            IconButton(
                onClick = {
                    firstClick = false
                    isValidUsername = !(allUsers!!.any { u -> u.username == username }) && username.isNotBlank()
                    isValidEmail = !(allUsers!!.any { u -> u.email == username })
                    if (isValidUsername && isValidEmail) {
                        callback(username, email)
                    }
                },
                modifier = Modifier.size(50.dp)) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next",
                    tint = Cyan
                )
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewRegisterUsername() {
    RegisterUsernameEmail (callback = { username, email ->
        println("Preview Username: $username, Preview Email: $email")
    }, userViewModel = UserViewModel(), TextFieldDefaults.colors(
        focusedTextColor = DarkCyan,
        unfocusedTextColor = DarkCyan,
        unfocusedContainerColor = Color.Transparent,
        focusedContainerColor = Color.Transparent,
        cursorColor = DarkCyan,
        focusedLabelColor = MaterialTheme.colorScheme.tertiary,
        focusedIndicatorColor = MaterialTheme.colorScheme.tertiary))
}

fun validateEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    return email.matches(emailRegex)
}