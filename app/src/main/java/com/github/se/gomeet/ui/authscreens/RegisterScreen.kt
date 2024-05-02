package com.github.se.gomeet.ui.authscreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
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
import com.github.se.gomeet.ui.theme.DarkCyan
import com.github.se.gomeet.viewmodel.AuthViewModel
import com.github.se.gomeet.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.models.User

/**
 * Composable function for the Login Screen.
 *
 * @param client The ChatClient instance.
 * @param authViewModel The ViewModel for the authentication.
 * @param userViewModel The ViewModel for the user.
 * @param onNavToExplore The navigation function to navigate to the Explore Screen.
 */
@Composable
fun RegisterScreen(
    client: ChatClient,
    nav: NavigationActions,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    onNavToExplore: () -> Unit
) {
  val signInState = authViewModel.signInState.collectAsState()
  val isError = signInState.value.registerError != null
  val context = LocalContext.current
  val textFieldColors =
      TextFieldDefaults.colors(
          focusedTextColor = DarkCyan,
          unfocusedTextColor = DarkCyan,
          unfocusedContainerColor = Color.Transparent,
          focusedContainerColor = Color.Transparent,
          cursorColor = DarkCyan,
          focusedLabelColor = MaterialTheme.colorScheme.tertiary,
          focusedIndicatorColor = MaterialTheme.colorScheme.tertiary)

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
        modifier = Modifier.fillMaxSize().padding(25.dp).verticalScroll(ScrollState(0))) {
          Image(
              painter = painterResource(id = R.drawable.gomeet_text),
              contentDescription = "Go Meet",
              modifier = Modifier.padding(top = 0.dp),
              alignment = Alignment.Center,
              colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary))

          Spacer(modifier = Modifier.size(15.dp))

          if (signInState.value.isLoading || signInState.value.isSignInSuccessful) {
            LoadingText()
          } else {

            Text(
                text = "Create account",
                modifier = Modifier.padding(bottom = 16.dp).testTag("register_title"),
                color = DarkCyan,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.size(15.dp))

            if (isError) {
              Text(
                  text = signInState.value.registerError!!,
                  modifier = Modifier.padding(bottom = 16.dp),
                  color = Color.Red,
                  textAlign = TextAlign.Center)
            }

            TextField(
                value = signInState.value.firstNameRegister,
                onValueChange = { newValue -> authViewModel.onFirstNameRegisterChange(newValue) },
                label = { Text("First Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors)

            Spacer(modifier = Modifier.size(16.dp))

            TextField(
                value = signInState.value.lastNameRegister,
                onValueChange = { newValue -> authViewModel.onLastNameRegisterChange(newValue) },
                label = { Text("Last Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors)

            Spacer(modifier = Modifier.size(16.dp))

            TextField(
                value = signInState.value.usernameRegister,
                onValueChange = { newValue -> authViewModel.onUsernameRegisterChange(newValue) },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors)

            Spacer(modifier = Modifier.size(16.dp))

            TextField(
                value = signInState.value.phoneNumberRegister,
                onValueChange = { newValue -> authViewModel.onPhoneNumberRegisterChange(newValue) },
                label = { Text("Phone Number") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors)

            Spacer(modifier = Modifier.size(16.dp))

            TextField(
                value = signInState.value.emailRegister,
                onValueChange = { newValue -> authViewModel.onEmailRegisterChange(newValue) },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors)

            Spacer(modifier = Modifier.size(16.dp))

            TextField(
                value = signInState.value.countryRegister,
                onValueChange = { newValue -> authViewModel.onCountryRegisterChange(newValue) },
                label = { Text("Country") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors)

            Spacer(modifier = Modifier.size(16.dp))

            TextField(
                value = signInState.value.passwordRegister,
                onValueChange = { newValue -> authViewModel.onPasswordRegisterChange(newValue) },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors)

            Spacer(modifier = Modifier.size(16.dp))

            TextField(
                value = signInState.value.confirmPasswordRegister,
                onValueChange = { newValue ->
                  authViewModel.onConfirmPasswordRegisterChange(newValue)
                },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors)

            Spacer(modifier = Modifier.size(50.dp))

            Button(
                onClick = { authViewModel.signUpWithEmailPassword(context) },
                modifier = Modifier.fillMaxWidth().testTag("register_button"),
                colors =
                    ButtonColors(
                        disabledContainerColor = MaterialTheme.colorScheme.primary,
                        containerColor = DarkCyan,
                        disabledContentColor = Color.White,
                        contentColor = Color.White),
                enabled =
                    signInState.value.emailRegister.isNotEmpty() &&
                        signInState.value.passwordRegister.isNotEmpty() &&
                        signInState.value.confirmPasswordRegister.isNotEmpty()) {
                  Text("Create account")
                }
          } add
              if (signInState.value.isLoading) {
                CircularProgressIndicator()
              }

          if (signInState.value.isSignInSuccessful) {
            val currentUser = Firebase.auth.currentUser
            if (currentUser != null) {
              val uid = currentUser.uid
              val email = currentUser.email ?: ""
              val firstName = signInState.value.firstNameRegister
              val lastName = signInState.value.lastNameRegister
              val phoneNumber = signInState.value.phoneNumberRegister
              val country = signInState.value.countryRegister
              val username = signInState.value.usernameRegister

              userViewModel.createUserIfNew(
                  uid, username, firstName, lastName, email, phoneNumber, country)
            }

            val user =
                User(
                    id = Firebase.auth.currentUser!!.uid,
                    name = Firebase.auth.currentUser!!.email!!) // TODO: currently username = email
            client
                .connectUser(
                    user = user,
                    // TODO: Generate Token, see https://getstream.io/tutorials/android-chat/
                    token = client.devToken(user.id))
                .enqueue()
            onNavToExplore()
          }
        }
  }
}
