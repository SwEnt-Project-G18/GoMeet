package com.github.se.gomeet.authentication

/**
 * This data class represents the state of the sign in authentication.
 *
 * @param email The email of the user.
 * @param password The password of the user.
 * @param emailRegister The email of the user for registration.
 * @param passwordRegister The password of the user for registration.
 * @param confirmPasswordRegister The confirmation password of the user for registration.
 * @param isLoading A boolean indicating if the sign in process is loading.
 * @param isSignInSuccessful A boolean indicating if the sign in process is successful.
 * @param registerError The error message of the registration process.
 * @param signInError The error message of the sign in process.
 */
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
