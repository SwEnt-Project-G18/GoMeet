package com.github.se.gomeet.viewmodel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gomeet.model.authentication.SignInState
import com.github.se.gomeet.model.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the authentication. The viewModel is responsible for handling the logic that comes
 * from the UI and the repository.
 */
class AuthViewModel : ViewModel() {

  private val _signInState = MutableStateFlow(SignInState())
  val signInState: StateFlow<SignInState> = _signInState

  val currentUser = AuthRepository.currentUser

  /** Check if the user is signed in. */
  val hasUser: Boolean
    get() = AuthRepository.hasUserSignedIn()

  /**
   * Update the email field in the signInState.
   *
   * @param email the email to update the field with
   */
  fun onEmailChange(email: String) {
    _signInState.value = _signInState.value.copy(email = email)
  }

  /**
   * Update the password field in the signInState.
   *
   * @param password the password to update the field with
   */
  fun onPasswordChange(password: String) {
    _signInState.value = _signInState.value.copy(password = password)
  }

  /**
   * Update the emailRegister field in the signInState.
   *
   * @param emailRegister the email to update the field with
   */
  fun onEmailRegisterChange(emailRegister: String) {
    _signInState.value = _signInState.value.copy(emailRegister = emailRegister)
  }

  /**
   * Update the passwordRegister field in the signInState.
   *
   * @param passwordRegister the password to update the field with
   */
  fun onPasswordRegisterChange(passwordRegister: String) {
    _signInState.value = _signInState.value.copy(passwordRegister = passwordRegister)
  }

  /**
   * Update the confirmPasswordRegister field in the signInState.
   *
   * @param confirmPasswordRegister the password to update the field with
   */
  fun onConfirmPasswordRegisterChange(confirmPasswordRegister: String) {
    _signInState.value = _signInState.value.copy(confirmPasswordRegister = confirmPasswordRegister)
  }

  /**
   * Update the pfp field in the signInState.
   *
   * @param pfpRegister the password to update the field with
   */
  fun onPfpRegisterChange(pfpRegister: Uri?) {
    _signInState.value = _signInState.value.copy(pfp = pfpRegister)
  }

  /**
   * Update the firstNameRegister field in the signInState.
   *
   * @param firstName the first name to update the field with
   */
  fun onFirstNameRegisterChange(firstNameRegister: String) {
    _signInState.value = _signInState.value.copy(firstNameRegister = firstNameRegister)
  }

  /**
   * Update the lastNameRegister field in the signInState.
   *
   * @param lastName the last name to update the field with
   */
  fun onLastNameRegisterChange(lastName: String) {
    _signInState.value = _signInState.value.copy(lastNameRegister = lastName)
  }

  /**
   * Update the phoneNumberRegister field in the signInState.
   *
   * @param phoneNumber the phone number to update the field with
   */
  fun onPhoneNumberRegisterChange(phoneNumber: String) {
    _signInState.value = _signInState.value.copy(phoneNumberRegister = phoneNumber)
  }

  /**
   * Update the countryRegister field in the signInState.
   *
   * @param country the country to update the field with
   */
  fun onCountryRegisterChange(country: String) {
    _signInState.value = _signInState.value.copy(countryRegister = country)
  }

  /**
   * Update the usernameReigsiter field in the signInState.
   *
   * @param username the username to update the field with
   */
  fun onUsernameRegisterChange(username: String) {
    _signInState.value = _signInState.value.copy(usernameRegister = username)
  }

  /**
   * Update the username field in the signInState.
   *
   * @param username the username to update the field with
   */
  fun onUsernameChange(username: String) {
    _signInState.value = _signInState.value.copy(username = username)
  }

  /**
   * Update the firstName field in the signInState.
   *
   * @param firstName the first name to update the field with
   */
  fun onFirstNameChange(firstName: String) {
    _signInState.value = _signInState.value.copy(firstName = firstName)
  }

  /**
   * Update the lastName field in the signInState.
   *
   * @param lastName the last name to update the field with
   */
  fun onLastNameChange(lastName: String) {
    _signInState.value = _signInState.value.copy(lastName = lastName)
  }

  /**
   * Update the phoneNumber field in the signInState.
   *
   * @param phoneNumber the phone number to update the field with
   */
  fun onPhoneNumberChange(phoneNumber: String) {
    _signInState.value = _signInState.value.copy(phoneNumber = phoneNumber)
  }

  /**
   * Update the country field in the signInState.
   *
   * @param country the country to update the field with
   */
  fun onCountryChange(country: String) {
    _signInState.value = _signInState.value.copy(country = country)
  }

  /** Validate the sign in form. */
  private fun validateSignInForm(): Boolean {
    return _signInState.value.email.isNotBlank() && _signInState.value.password.isNotBlank()
  }

  /** Validate the register form. */
  private fun validateRegisterForm(): Boolean {
    return _signInState.value.emailRegister.isNotBlank() &&
        _signInState.value.passwordRegister.isNotBlank() &&
        _signInState.value.confirmPasswordRegister.isNotBlank()
  }

  /**
   * Sign up the user with email-password authentication.
   *
   * @param context the context to display the toast
   */
  fun signUpWithEmailPassword(context: Context) {
    viewModelScope.launch {
      try {

        _signInState.value = _signInState.value.copy(isLoading = true)
        _signInState.value = _signInState.value.copy(registerError = null)

        AuthRepository.signUpWithEmailPassword(
            _signInState.value.emailRegister, _signInState.value.passwordRegister) { isSuccessful ->
              if (isSuccessful) {
                Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                _signInState.value = _signInState.value.copy(isSignInSuccessful = true)
              } else {
                Toast.makeText(context, "Failed to register", Toast.LENGTH_SHORT).show()
                _signInState.value = _signInState.value.copy(isSignInSuccessful = false)
              }
            }
      } catch (e: Exception) {
        _signInState.value = _signInState.value.copy(registerError = e.message)
        e.printStackTrace()
      } finally {
        _signInState.value = _signInState.value.copy(isLoading = false)
      }
    }
  }

  /**
   * Sign in the user with email-password authentication.
   *
   * @param context the context to display the toast
   */
  fun signInWithEmailPassword(context: Context) {
    viewModelScope.launch {
      try {
        _signInState.value = _signInState.value.copy(isLoading = true)
        _signInState.value = _signInState.value.copy(signInError = null)

        AuthRepository.signInWithEmailPassword(
            _signInState.value.email, _signInState.value.password) { isSuccessful ->
              if (isSuccessful) {
                Toast.makeText(context, "Sign in successful", Toast.LENGTH_SHORT).show()
                _signInState.value = _signInState.value.copy(isSignInSuccessful = true)
              } else {
                Toast.makeText(context, "Failed to sign in", Toast.LENGTH_SHORT).show()
                _signInState.value = _signInState.value.copy(isSignInSuccessful = false)
              }
            }
      } catch (e: Exception) {
        _signInState.value = _signInState.value.copy(signInError = e.message)
        e.printStackTrace()
      } finally {
        _signInState.value = _signInState.value.copy(isLoading = false)
      }
    }
  }

  /** Sign out the user. */
  fun signOut() {
    AuthRepository.signOut()
  }

  /** Delete the current user. */
  /* TODO: merge Auth and User VM and repositories, deleteing a user
  should also delete it on Firebase (not just on auth) */

  //  fun deleteCurrentUser() {
  //    if(currentUser == null) return
  //    UserRepository.removeUser(currentUser.uid)
  //  }

}
