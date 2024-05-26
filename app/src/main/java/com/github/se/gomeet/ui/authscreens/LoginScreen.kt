package com.github.se.gomeet.ui.authscreens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.se.gomeet.R
import com.github.se.gomeet.ui.mainscreens.LoadingText
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.viewmodel.AuthViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.models.User

private const val TAG = "LoginScreen"

/**
 * Composable function for the Login Screen.
 *
 * @param authViewModel The ViewModel for the authentication.
 * @param onNavToExplore The navigation function to navigate to the Explore Screen.
 */
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    nav: NavigationActions,
    onNavToExplore: () -> Unit,
) {
  val screenHeight = LocalConfiguration.current.screenHeightDp.dp
  val screenWidth = LocalConfiguration.current.screenWidthDp.dp

  val signInState = authViewModel.signInState.collectAsState()
  val isError = signInState.value.signInError != null
  val context = LocalContext.current
  val textFieldColors =
      TextFieldDefaults.colors(
          focusedTextColor = MaterialTheme.colorScheme.onBackground,
          unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
          unfocusedContainerColor = Color.Transparent,
          focusedContainerColor = Color.Transparent,
          cursorColor = MaterialTheme.colorScheme.outlineVariant,
          focusedLabelColor = MaterialTheme.colorScheme.tertiary,
          unfocusedLabelColor = MaterialTheme.colorScheme.tertiary,
          focusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
          unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary)

  Column(modifier = Modifier.fillMaxSize()) {
    TopAppBar(
        modifier = Modifier.testTag("TopBar"),
        backgroundColor = MaterialTheme.colorScheme.background,
        elevation = 0.dp,
        title = {},
        navigationIcon = {
          IconButton(onClick = { nav.goBack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground)
          }
        })

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(25.dp).testTag("LoginScreen")) {
          Image(
              painter = painterResource(id = R.drawable.gomeet_text),
              contentDescription = "GoMeet",
              modifier = Modifier.padding(top = 40.dp).width(200.dp),
              alignment = Alignment.Center,
              colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary))

          Spacer(modifier = Modifier.size(40.dp))

          Text(
              text = "Login",
              modifier = Modifier.padding(bottom = 16.dp),
              color = MaterialTheme.colorScheme.tertiary,
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
              isError = isError,
              colors = textFieldColors)

          Spacer(modifier = Modifier.size(16.dp))

          TextField(
              value = signInState.value.password,
              singleLine = true,
              onValueChange = { newValue -> authViewModel.onPasswordChange(newValue) },
              modifier = Modifier.fillMaxWidth().testTag("LogInField"),
              label = { Text("Password") },
              isError = isError,
              colors = textFieldColors,
              visualTransformation = PasswordVisualTransformation())

          Spacer(modifier = Modifier.size(50.dp))

          Button(
              onClick = { authViewModel.signInWithEmailPassword(context) },
              modifier =
                  Modifier.width((screenWidth / 1.5.dp).dp)
                      .height(screenHeight / 17)
                      .testTag("LogInButton"),
              shape = RoundedCornerShape(10.dp),
              colors =
                  ButtonDefaults.buttonColors(
                      containerColor = MaterialTheme.colorScheme.outlineVariant,
                      contentColor = Color.White,
                      disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                      disabledContentColor = MaterialTheme.colorScheme.onBackground),
              enabled =
                  signInState.value.email.isNotEmpty() && signInState.value.password.isNotEmpty()) {
                Text("Log In")
              }

          if (signInState.value.isLoading) {
            LoadingText()
          }

          if (signInState.value.isSignInSuccessful) {
            val user =
                User(
                    id = Firebase.auth.currentUser!!.uid,
                    name = Firebase.auth.currentUser!!.email!!) // TODO: Add Profile Picture to User
            val client = ChatClient.instance()
            client.connectUser(user = user, token = client.devToken(user.id)).enqueue { result ->
              if (result.isSuccess) {
                onNavToExplore()
              } else {
                // Handle connection failure
                Log.e(TAG, "ChatClient failed to connect user: ${user.id}")
              }
            }
          }
        }
  }
}
