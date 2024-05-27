package com.github.se.gomeet.model.authentication

import android.net.Uri
import java.util.Locale

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
 * @param pfp The profile picture of the user for registration.
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
    val pfp: Uri? = null,
    val lastName: String = "",
    val phoneNumber: String = "",
    val country: String = "",
    val isLoading: Boolean = false,
    val isSignInSuccessful: Boolean = false,
    val registerError: String? = null,
    val signInError: String? = null
)

/**
 * This function validates the email address.
 *
 * @param email The email address to be validated
 * @return True if the email address is valid, false otherwise
 */
fun validateEmail(email: String): Boolean {
  return email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

/**
 * This function returns all countries with their respective flags.
 *
 * @return A list of countries with their respective flags
 */
fun getCountries(): ArrayList<String> {
  val isoCountryCodes: Array<String> = Locale.getISOCountries()
  val countriesWithEmojis: ArrayList<String> = arrayListOf()
  for (countryCode in isoCountryCodes) {
    val locale = Locale("", countryCode)
    val countryName: String = locale.displayCountry
    val flagOffset = 0x1F1E6
    val asciiOffset = 0x41
    val firstChar = Character.codePointAt(countryCode, 0) - asciiOffset + flagOffset
    val secondChar = Character.codePointAt(countryCode, 1) - asciiOffset + flagOffset
    val flag = (String(Character.toChars(firstChar)) + String(Character.toChars(secondChar)))
    countriesWithEmojis.add("$countryName $flag")
  }
  countriesWithEmojis.sortBy { it.lowercase() }
  return countriesWithEmojis
}
