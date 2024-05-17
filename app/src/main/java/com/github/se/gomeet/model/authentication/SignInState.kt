package com.github.se.gomeet.model.authentication

/**
 * This data class represents the state of the sign in authentication.
 *
 * @param email The email of the user.
 * @param password The password of the user.
 * @param emailRegister The email of the user for registration.
 * @param passwordRegister The password of the user for registration.
 * @param confirmPasswordRegister The confirmation password of the user for registration.
 * @param firstNameRegister The first name of the user for registration.
 * @param lastNameRegister The last name of the user for registration.
 * @param phoneNumberRegister The phone number of the user for registration.
 * @param countryRegister The country of the user for registration.
 * @param usernameRegister The username of the user for registration.
 * @param username The username of the user for registration.
 * @param firstName The first name of the user for registration.
 * @param lastName The last name of the user for registration.
 * @param phoneNumber The phone number of the user for registration.
 * @param country The country of the user for registration.
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
    val firstNameRegister: String = "",
    val lastNameRegister: String = "",
    val phoneNumberRegister: String = "",
    val countryRegister: String = "",
    val usernameRegister: String = "",
    val username: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val country: String = "",
    val isLoading: Boolean = false,
    val isSignInSuccessful: Boolean = false,
    val registerError: String? = null,
    val signInError: String? = null
)
