package com.github.se.gomeet

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.github.se.gomeet.ui.create.Create
import com.github.se.gomeet.ui.events.Events
import com.github.se.gomeet.ui.explore.Explore
import com.github.se.gomeet.ui.navigation.NavigationActions
import com.github.se.gomeet.ui.navigation.Route
import com.github.se.gomeet.ui.profile.Profile
import com.github.se.gomeet.ui.theme.GoMeetTheme
import com.github.se.gomeet.ui.trending.Trending
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      GoMeetTheme {
          Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            val userIdState = remember { mutableStateOf<String?>(null) }
            if (userIdState.value == null) {
                WelcomeScreen(onSignInSuccess = { userId -> userIdState.value = userId })
            } else {
                val nav = rememberNavController()
                Log.d("SignIn", "Signed in state changed: ${userIdState.value}")
                NavHost(navController = nav, startDestination = Route.EXPLORE) {
                    composable(Route.EXPLORE) {
                        Explore(NavigationActions(nav))
                    }
                    composable(Route.EVENTS) {
                        Events(NavigationActions(nav))
                    }
                    composable(Route.TRENDING) {
                        Trending(NavigationActions(nav))
                    }
                    composable(Route.CREATE) {
                        Create(NavigationActions(nav))
                    }
                    composable(Route.PROFILE){
                        Profile(NavigationActions(nav))
                    }
                }
            }
        }
      }
    }
  }
    @Composable
    fun WelcomeScreen(onSignInSuccess: (String) -> Unit) {
        val launcher =
            rememberLauncherForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
                if (res.resultCode == RESULT_OK) {
                    val user = FirebaseAuth.getInstance().currentUser
                    user?.uid?.let { userId -> onSignInSuccess(userId) }
                } else {
                    // Sign-in error
                }
            }

        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val signInIntent =
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build()

        Column(
            modifier = Modifier.padding(15.dp).width(258.dp).height(510.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                modifier = Modifier.width(258.dp).height(65.dp),
                text = "Welcome",

                // M3/display/large
                style =
                TextStyle(
                    fontSize = 57.sp,
                    lineHeight = 64.sp,
                    fontFamily = FontFamily(Font(R.font.roboto)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFF191C1E),
                    textAlign = TextAlign.Center,
                )
            )

            Spacer(modifier = Modifier.height(150.dp))

            OutlinedButton(
                onClick = { launcher.launch(signInIntent) },
                shape = RoundedCornerShape(size = 20.dp),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp).wrapContentSize(),
                colors = ButtonDefaults.outlinedButtonColors()) {
                Image(
                    modifier = Modifier.width(24.dp).height(24.dp),
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = "Google logo")

                Text(modifier = Modifier.padding(6.dp), text = "Sign in with Google")
            }
        }
    }
}
