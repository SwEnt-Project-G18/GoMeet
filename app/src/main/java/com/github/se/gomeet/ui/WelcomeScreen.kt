package com.github.se.gomeet.ui

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.github.se.gomeet.R
import com.github.se.gomeet.ui.theme.DarkCyan
import com.google.firebase.auth.FirebaseAuth

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

  val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
  val signInIntent =
      AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build()

  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Top,
      modifier = Modifier.fillMaxSize().background(Color.White).padding(25.dp)) {
        Spacer(modifier = Modifier.size(70.dp))

        Image(
            painter = painterResource(id = R.drawable.gomeet_logo),
            contentDescription = "GoMeet Logo")

        Spacer(modifier = Modifier.size(30.dp))

        Text(
            text = "See what's happening around you right now.",
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            color = DarkCyan,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.size(80.dp))

        OutlinedButton(
            onClick = { launcher.launch(signInIntent) },
            modifier = Modifier.width(250.dp).height(40.dp),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color.Gray), // Set border here if needed
            enabled = true,
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFECEFF1))) {
              Image(
                  painter = painterResource(id = R.drawable.google_logo),
                  contentDescription = "Google logo",
                  modifier = Modifier.width(24.dp).height(24.dp))

              Spacer(modifier = Modifier.size(15.dp)) // Adjust the size of the spacer as needed

              Text(
                  text = "Continue with Google",
                  color = Color.Black,
                  modifier =
                      Modifier.padding(
                          start = 8.dp,
                          end = 8.dp) // This will add padding around the text inside the button
                  )
            }

        Spacer(modifier = Modifier.size(1.dp))

        DividerWithText()

        Spacer(modifier = Modifier.size(1.dp))

        OutlinedButton(
            onClick = { /* TODO: email-password logic */},
            modifier = Modifier.width(250.dp).height(40.dp),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color.Gray), // Set border here if needed
            enabled = true,
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFECEFF1))) {
              Text(
                  text = "Log in",
                  modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                  color = Color.Black)
            }

        Spacer(modifier = Modifier.size(10.dp))

        Row() {
          Text(
              text = "Don’t have an account?",
              style =
                  TextStyle(
                      fontSize = 11.sp,
                      lineHeight = 17.sp,
                      fontFamily = FontFamily(Font(R.font.roboto)),
                      fontWeight = FontWeight(500),
                      color = Color(0xFF3C4043),
                      letterSpacing = 0.25.sp,
                  ))

          // TODO: Create account should go to RegisterScreen
          Text(
              text = "Create account",
              style =
                  TextStyle(
                      fontSize = 11.sp,
                      lineHeight = 17.sp,
                      fontFamily = FontFamily(Font(R.font.roboto)),
                      fontWeight = FontWeight(500),
                      color = Color(0xFF2F6673),
                      textAlign = TextAlign.Center,
                      letterSpacing = 0.25.sp,
                  ))

        }
      }
    }

@Preview
@Composable
fun WelcomeScreenPreview() {
  WelcomeScreen {}
}

@Composable
fun DividerWithText() {
  val paint =
      Paint().apply {
        color = Color.Black
        strokeWidth = 5f
      }

  Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
    Canvas(modifier = Modifier.matchParentSize()) {
      val canvasWidth = size.width
      val canvasHalfHeight = size.height / 2

      drawLine(
          color = Color.Black,
          strokeWidth = 2f,
          start = Offset(x = 120f, y = canvasHalfHeight),
          end = Offset(x = (canvasWidth / 2) - 50f, y = canvasHalfHeight),
      )
      drawLine(
          color = Color.Black,
          strokeWidth = 2f,
          start = Offset(x = (canvasWidth / 2) + 50f, y = canvasHalfHeight),
          end = Offset(x = canvasWidth - 120, y = canvasHalfHeight),
      )
    }
    Text(
        text = "or",
        modifier = Modifier.align(Alignment.Center),
        style =
            TextStyle(
                fontSize = 15.sp,
                fontFamily = FontFamily(Font(R.font.roboto)),
                fontWeight = FontWeight(700),
                color = Color.Black,
            ))
  }
}
