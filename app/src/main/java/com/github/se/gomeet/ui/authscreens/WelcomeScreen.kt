package com.github.se.gomeet.ui.authscreens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.github.se.gomeet.R
import com.google.firebase.auth.FirebaseAuth

/**
 * Composable function for the Welcome Screen.
 *
 * @param onNavToLogin The navigation function to navigate to the Login Screen.
 * @param onNavToRegister The navigation function to navigate to the Register Screen.
 * @param onSignInSuccess The function to call when the user successfully signs in.
 * @param chatClientDisconnected True if the chat client is disconnected, false otherwise. It's
 *   important for the chat client to have no active connection when the user attempts to sign in.
 */
@SuppressLint("RestrictedApi")
@Composable
fun WelcomeScreen(
    onNavToLogin: () -> Unit,
    onNavToRegister: () -> Unit,
    onSignInSuccess: (String, String, String, String, String, String) -> Unit,
    chatClientDisconnected: MutableState<Boolean>
) {
  val screenHeight = LocalConfiguration.current.screenHeightDp.dp
  val screenWidth = LocalConfiguration.current.screenWidthDp.dp
  val context = LocalContext.current

  val launcher =
      rememberLauncherForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
          val userFirebase = FirebaseAuth.getInstance().currentUser
          val email = res.idpResponse!!.email ?: ""
          val phoneNumber = res.idpResponse!!.phoneNumber ?: ""

          val name = res.idpResponse!!.user.name ?: ""
          val index = name.indexOf(" ")
          val lastName = res.idpResponse!!.user.name?.take(index) ?: ""
          val firstName = res.idpResponse!!.user.name?.drop(index) ?: ""
          userFirebase?.uid?.let { userId ->
            onSignInSuccess(
                userId, "user" + userId.take(6), email, firstName, lastName, phoneNumber)
          }
        } else {
          // Sign-in error
        }
      }

  val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
  val signInIntent =
      AuthUI.getInstance()
          .createSignInIntentBuilder()
          .setAvailableProviders(providers)
          .setIsSmartLockEnabled(false)
          .build()

  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Top,
      modifier = Modifier.fillMaxSize().padding(25.dp).testTag("WelcomeScreenCol")) {
        Spacer(modifier = Modifier.size(screenHeight / 30))

        Image(
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary),
            painter = painterResource(id = R.drawable.gomeet_text),
            contentDescription = "GoMeet Logo",
            modifier = Modifier.size(width = 200.dp, height = 100.dp))

        Image(
            painter = painterResource(id = R.drawable.welcome2),
            contentDescription = "Welcome Image")

        Text(
            text = "See what's happening around you right now.",
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.SemiBold))

        Spacer(modifier = Modifier.size(screenHeight / 70))

        Button(
            onClick = {
              if (chatClientDisconnected.value) onNavToLogin() else cantLogInToast(context)
            },
            modifier =
                Modifier.width((screenWidth / 1.5.dp).dp)
                    .height(screenHeight / 17)
                    .testTag("LogInButton"),
            shape = RoundedCornerShape(10.dp),
            enabled = true,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.outlineVariant)) {
              Text(
                  text = "Log In",
                  style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                  color = Color.White)
            }

        Spacer(modifier = Modifier.size(10.dp))
        OutlinedButton(
            onClick = {
              if (chatClientDisconnected.value) onNavToRegister() else cantLogInToast(context)
            },
            modifier = Modifier.width((screenWidth / 1.5.dp).dp).height(screenHeight / 17),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            enabled = true,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background)) {
              Text(
                  text = "Sign Up",
                  style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                  color = MaterialTheme.colorScheme.outlineVariant)
            }

        Spacer(modifier = Modifier.size(screenHeight / 30))

        OutlinedIconButton(
            modifier = Modifier.size(screenHeight / 18),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer),
            onClick = {
              if (chatClientDisconnected.value) launcher.launch(signInIntent)
              else cantLogInToast(context)
            },
            enabled = true) {
              Image(
                  painter = painterResource(id = R.drawable.multicolor_google_logo),
                  contentDescription = "Google logo")
            }
      }
}

private fun cantLogInToast(context: Context) {
  Toast.makeText(context, "Please wait for log out to complete", Toast.LENGTH_SHORT).show()
}
