package com.github.se.gomeet.authentication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.se.gomeet.ui.LoginScreen
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : ComponentActivity() {
  private lateinit var firebaseAuth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    firebaseAuth = FirebaseAuth.getInstance()
    setContent {
      LoginScreen(
          onLoginClicked = { email, password -> loginWithEmailAndPassword(email, password) })
    }
  }

  private fun loginWithEmailAndPassword(email: String, password: String) {
    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
      if (task.isSuccessful) {
        // Login successful, update UI accordingly
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
        // You can navigate to another screen or perform any desired action here
      } else {
        // Login failed, display a message to the user
        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
      }
    }
  }
}
