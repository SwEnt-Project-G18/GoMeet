package com.github.se.gomeet.ui

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.github.se.gomeet.R
import com.google.firebase.auth.FirebaseAuth


/*
@Composable
fun WelcomeScreen(onSignInSuccess: (String) -> Unit) {
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

    Column(
        modifier = Modifier.padding(15.dp).width(258.dp).height(510.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        androidx.compose.material3.Text(
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

            androidx.compose.material3.Text(modifier = Modifier.padding(6.dp), text = "Sign in with Google")
        }
    }
}*/
