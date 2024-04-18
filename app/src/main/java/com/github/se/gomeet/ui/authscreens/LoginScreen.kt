package com.github.se.gomeet.ui.authscreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.se.gomeet.R
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.viewmodel.AuthViewModel

@Composable
fun LoginScreen(authViewModel: AuthViewModel, onNavToExplore: () -> Unit) {
  val signInState = authViewModel.signInState.collectAsState()
  val isError = signInState.value.signInError != null
  val context = LocalContext.current

  Column(
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize().padding(25.dp).testTag("LoginScreen")) {
        Image(
            painter = painterResource(id = R.drawable.gomeet_text),
            contentDescription = "GoMeet",
            modifier = Modifier.padding(top = 40.dp),
            alignment = Alignment.Center)

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

        if (isError) {
          Text(
              text = signInState.value.signInError!!,
              modifier = Modifier.padding(bottom = 16.dp),
              color = Color.Red,
              textAlign = TextAlign.Center)
        }

        TextField(
            value = signInState.value.email,
            singleLine = true,
            onValueChange = { newValue -> authViewModel.onEmailChange(newValue) },
            modifier = Modifier.fillMaxWidth().testTag("EmailField"),
            label = { Text("Email") },
            isError = isError)

        Spacer(modifier = Modifier.size(16.dp))

        TextField(
            value = signInState.value.password,
            singleLine = true,
            onValueChange = { newValue -> authViewModel.onPasswordChange(newValue) },
            modifier = Modifier.fillMaxWidth().testTag("LogInField"),
            label = { Text("Password") },
            isError = isError,
            visualTransformation = PasswordVisualTransformation())

        Spacer(modifier = Modifier.size(50.dp))

        Button(
            onClick = { authViewModel.signInWithEmailPassword(context) },
            modifier = Modifier.fillMaxWidth().testTag("LogInButton"),
            enabled =
                signInState.value.email.isNotEmpty() && signInState.value.password.isNotEmpty()) {
              Text("Log in")
            }

        if (signInState.value.isLoading) {
          CircularProgressIndicator()
        }

        if (signInState.value.isSignInSuccessful) {
          onNavToExplore()
        }
      }
}

@Preview
@Composable
fun PreviewLoginScreen() {
  LoginScreen(AuthViewModel()) {}
}
