package com.github.se.gomeet.authentication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : ComponentActivity() {
  private lateinit var firebaseAuth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    firebaseAuth = FirebaseAuth.getInstance()
  }

  private fun registerWithEmailAndPassword(
      email: String,
      password: String,
      onNavigateToExplore: () -> Unit
  ) {
    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task
      ->
      if (task.isSuccessful) {
        // Registration successful, update UI accordingly
        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()

        // TODO: Navigate to the MapActivity with the current user
        onNavigateToExplore()
      } else {
        // Registration failed, display a message to the user
        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT)
            .show()
      }
    }
  }
}
