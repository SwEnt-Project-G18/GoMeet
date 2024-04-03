package com.github.se.gomeet.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.se.gomeet.R
import com.github.se.gomeet.ui.theme.DarkCyan

@Composable
fun LoginScreen(onLoginClicked: (String, String) -> Unit) {
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }

  Column(
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize().background(Color.White).padding(25.dp)) {
        Image(
            painter = painterResource(id = R.drawable.gomeet_text),
            contentDescription = "GoMeet",
            modifier = Modifier.padding(top = 40.dp),
            alignment = Alignment.Center
        )

        Spacer(modifier = Modifier.size(40.dp))

        Text(
            text = "Login",
            modifier = Modifier.padding(bottom = 16.dp),
            color = DarkCyan,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.size(110.dp))

        TextField(
            value = email,
            onValueChange = { newValue -> email = newValue },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.size(16.dp))

        TextField(
            value = password,
            onValueChange = { newValue -> password = newValue },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.size(50.dp))

        Button(onClick = { onLoginClicked(email, password) }, modifier = Modifier.fillMaxWidth()) {
          Text("Log in")
        }
      }
}

@Preview
@Composable
fun PreviewLoginScreen() {
  LoginScreen { email, password -> println("Email: $email, Password: $password") }
}
