package com.github.se.gomeet.ui.authscreens

import android.app.Activity
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
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
 */
@Composable
fun WelcomeScreen(
    onNavToLogin: () -> Unit,
    onNavToRegister: () -> Unit,
    onSignInSuccess: (String) -> Unit
) {
  val screenHeight = LocalConfiguration.current.screenHeightDp.dp
  val screenWidth = LocalConfiguration.current.screenWidthDp.dp

  val launcher =
      rememberLauncherForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
          val user = FirebaseAuth.getInstance().currentUser
          user?.uid?.let { userId -> onSignInSuccess(userId) }
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
            contentDescription = "GoMeet Logo")

        Image(
            painter = painterResource(id = R.drawable.welcomeimage),
            contentDescription = "Welcome Image")

        Text(
            text = "See what's happening around you right now.",
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.SemiBold))

        Spacer(modifier = Modifier.size(screenHeight / 70))

        Button(
            onClick = { onNavToLogin() },
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
            onClick = { onNavToRegister() },
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
            onClick = { launcher.launch(signInIntent) },
            enabled = true) {
              Image(
                  painter = painterResource(id = R.drawable.multicolor_google_logo),
                  contentDescription = "Google logo")
            }
      }
}
