package com.github.se.gomeet.ui.authscreens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.se.gomeet.R
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
  val screenHeight = LocalConfiguration.current.screenHeightDp.dp
  val signInState = authViewModel.signInState.collectAsState()
  val textFieldColors =
      TextFieldDefaults.colors(
          focusedTextColor = DarkCyan,
          unfocusedTextColor = DarkCyan,
          unfocusedContainerColor = Color.Transparent,
          focusedContainerColor = Color.Transparent,
          cursorColor = DarkCyan,
          focusedLabelColor = MaterialTheme.colorScheme.tertiary,
          focusedIndicatorColor = MaterialTheme.colorScheme.tertiary)

  var state by remember { mutableIntStateOf(1) }

  Column(modifier = Modifier.fillMaxSize()) {
    TopAppBar(
        modifier = Modifier.testTag("TopBar"),
        backgroundColor = MaterialTheme.colorScheme.background,
        elevation = 0.dp,
        title = {},
        navigationIcon = {
          IconButton(onClick = {
              if (state == 0) {
                  nav.goBack()
              }else {
                  state -= 1
              }
          }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground)
          }
        })

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp)
            .verticalScroll(ScrollState(0))) {

        Spacer(modifier = Modifier.height(screenHeight/10))
          Image(
              painter = painterResource(id = R.drawable.gomeet_text),
              contentDescription = "Go Meet",
              modifier = Modifier.padding(top = 0.dp),
              alignment = Alignment.Center,
              colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary))

        Spacer(modifier = Modifier.height(screenHeight/10))

        fun nextState() {
            state += 1 // Simplified state increment
        }

        when (state) {
            1 -> RegisterUsernameEmail(
                callback = { username, email ->
                    nextState()
                    authViewModel.onUsernameRegisterChange(username)
                    authViewModel.onEmailRegisterChange(username)
                },
                userViewModel = UserViewModel(),
                textFieldColors = textFieldColors
            )

            2 -> RegisterPassword(
                callback = { password ->
                    nextState()
                    authViewModel.onPasswordRegisterChange(password)
                },
                textFieldColors = textFieldColors
            )

            3 -> RegisterNamePfpCountry(
                callback = { firstname, lastname, country ->
                    nextState()
                    authViewModel.onFirstNameRegisterChange(firstname)
                    authViewModel.onLastNameChange(lastname)
                    authViewModel.onCountryChange(country)
                },
                textFieldColors = textFieldColors
            )

            4 -> RegisterEmailPhoneNumber(
                callback = { email, phone ->
                    nextState()
                    authViewModel.onEmailRegisterChange(email)
                    authViewModel.onPhoneNumberRegisterChange(phone)
                },
                textFieldColors = textFieldColors
            )
        }

          }
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
