package com.github.se.gomeet.viewmodel

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.gomeet.authentication.SignInState
import com.github.se.gomeet.model.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(): ViewModel() {

    private val authRepository by lazy { AuthRepository() }
    private val _signInState = MutableStateFlow(SignInState())
    val signInState: StateFlow<SignInState> = _signInState

    fun signInWithGoogle() {
        //TODO: What to put here ???

    }

    fun signInWithEmailPassword(email: String, password: String) {
        viewModelScope.launch {
            _signInState.value = SignInState()
            try {
                val userId = authRepository.signInWithEmailPassword(email, password)
                userId.let {
                    _signInState.value = SignInState(userId = userId, isSignInSuccessful = true)
                }
            } catch (e: Exception) {
                _signInState.value = SignInState(signInError = "Sign-in with email and password failed: ${e.message}")
            }
        }
    }

    fun signUpWithEmailPassword(email: String, password: String) {
        viewModelScope.launch {
            _signInState.value = SignInState()
            try {
                val userId = authRepository.signUpWithEmailPassword(email, password)
                userId.let {
                    _signInState.value = SignInState(userId = userId, isSignInSuccessful = true)
                }
            } catch (e: Exception) {
                _signInState.value = SignInState(signInError = "Sign-up with email and password failed: ${e.message}")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _signInState.value = SignInState(userId = null, isSignInSuccessful = false, signInError = null)
        }
    }
}