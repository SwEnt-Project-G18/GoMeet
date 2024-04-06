package com.github.se.gomeet.authentication

data class SignInState(
    val email: String = "",
    val password: String = "",
    val emailRegister: String = "",
    val passwordRegister: String = "",
    val confirmPasswordRegister: String = "",
    val isLoading: Boolean = false,
    val isSignInSuccessful: Boolean = false,
    val registerError: String? = null,
    val signInError: String? = null
)