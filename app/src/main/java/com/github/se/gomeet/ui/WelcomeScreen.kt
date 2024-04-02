package com.github.se.gomeet.ui

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.github.se.gomeet.R
import com.github.se.gomeet.ui.theme.DarkCyan
import com.google.firebase.auth.FirebaseAuth



@Composable
fun WelcomeScreen(onSignInSuccess: (String) -> Unit) {
    /*
    val launcher =
        rememberLauncherForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
            if (res.resultCode == Activity.RESULT_OK) {
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

     */


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(25.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.gomeet_logo),
            contentDescription = "GoMeet Logo"
        )

        Spacer(modifier = Modifier.size(40.dp))

        Text(
            text = "See what's happening around you right now.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            color = DarkCyan,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.size(80.dp))

        OutlinedButton(
            onClick = { /* TODO: launcher.launch(signInIntent) */ },
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .width(250.dp)
                .height(50.dp),
            shape = RoundedCornerShape(size = 20.dp),
            enabled = true,
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.LightGray),
        ) {
            Image(
                painter = painterResource(id = R.drawable.google_logo),
                contentDescription = "Google logo",
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
            )

            Spacer(modifier = Modifier.size(6.dp))

            Text(
                text = "Continue with Google",
                modifier = Modifier.padding(6.dp),
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.size(70.dp))

        OutlinedButton(
            onClick = { /* TODO: email-password logic */ },
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .width(250.dp)
                .height(50.dp),
            shape = RoundedCornerShape(size = 20.dp),
            enabled = true,
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.LightGray),
        ) {
            Text(
                text = "Log in",
                modifier = Modifier.padding(6.dp),
                color = Color.Black
            )
        }

        Text(
            text = "Don't have an account? Create account",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 32.dp)
        )
    }
}

@Preview
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen {}
}
