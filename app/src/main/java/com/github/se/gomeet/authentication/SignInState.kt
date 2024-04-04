package com.github.se.gomeet.authentication

data class SignInState(
    val userId: Unit? = null,
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)