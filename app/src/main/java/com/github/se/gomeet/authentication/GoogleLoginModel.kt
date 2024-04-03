package com.github.se.gomeet.authentication

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult

class GoogleLoginActivity {
  fun onSignInResult(result: FirebaseAuthUIAuthenticationResult): Boolean {
    return result.resultCode == Activity.RESULT_OK
  }

  fun createSignInIntent(signInLauncher: ActivityResultLauncher<Intent>) {
    val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

    val signInIntent =
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .build()
    signInLauncher.launch(signInIntent)
  }
}
