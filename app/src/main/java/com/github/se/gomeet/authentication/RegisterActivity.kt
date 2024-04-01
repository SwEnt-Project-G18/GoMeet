package com.github.se.gomeet.authentication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.se.gomeet.ui.RegisterScreen
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : ComponentActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        setContent {
            RegisterScreen { email, password ->
                registerWithEmailAndPassword(email, password)
            }
        }
    }

    private fun registerWithEmailAndPassword(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registration successful, update UI accordingly
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()

                    //TODO: Navigate to the MapActivity with the current user

                } else {
                    // Registration failed, display a message to the user
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}