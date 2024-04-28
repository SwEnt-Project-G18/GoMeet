package com.github.se.gomeet.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gomeet.authentication.SignInState
import com.github.se.gomeet.model.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the authentication. The viewModel is responsible for handling
 * the logic that comes from the UI and the repository.
 */
class AuthViewModel : ViewModel() {

  private val authRepository by lazy { AuthRepository() }
  private val _signInState = MutableStateFlow(SignInState())
  val signInState: StateFlow<SignInState> = _signInState

  val currentUser = authRepository.currentUser

  /**
   * Check if the user is signed in.
   */
  val hasUser: Boolean
    get() = authRepository.hasUserSignedIn()

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
   * Validate the sign in form.
   */
  private fun validateSignInForm(): Boolean {
    return _signInState.value.email.isNotBlank() && _signInState.value.password.isNotBlank()
  }

  /**
   * Validate the register form.
   */
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
        require(validateRegisterForm()) { "Please fill in all the fields" }
        require(_signInState.value.passwordRegister == _signInState.value.confirmPasswordRegister) {
          "Passwords do not match"
        }

        _signInState.value = _signInState.value.copy(isLoading = true)
        _signInState.value = _signInState.value.copy(registerError = null)

        authRepository.signUpWithEmailPassword(
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
        require(validateSignInForm()) { "Please fill in all the fields" }

        _signInState.value = _signInState.value.copy(isLoading = true)
        _signInState.value = _signInState.value.copy(signInError = null)

        authRepository.signInWithEmailPassword(
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

  /**
   * Sign out the user.
   */
  fun signOut() {
    authRepository.signOut()
  }
}
