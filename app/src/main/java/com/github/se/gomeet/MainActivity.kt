package com.github.se.gomeet

import android.app.Activity.RESULT_OK
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.github.se.gomeet.ui.theme.GoMeetTheme

private val providers =
    arrayListOf(
        AuthUI.IdpConfig.GoogleBuilder().build(),
    )

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      GoMeetTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.background) {
          Greeting()
        }
      }
    }
  }
}

private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult): Boolean {
  return result.resultCode == RESULT_OK
}

@Composable
fun Greeting() {
  val signedIn = remember { mutableStateOf(false) }
  val signInLauncher =
      rememberLauncherForActivityResult(
          FirebaseAuthUIActivityResultContract(),
      ) { res ->
        signedIn.value = onSignInResult(res)
      }
  OutlinedButton(
      modifier = Modifier.padding(top = 300.dp),
      onClick = {
        val signInIntent =
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build()
        signInLauncher.launch(signInIntent)
      },
  ) {
    if (!signedIn.value) {
      Text("Hello")
    } else {
      Text("Login successful")
    }
  }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  GoMeetTheme { Greeting() }
}
