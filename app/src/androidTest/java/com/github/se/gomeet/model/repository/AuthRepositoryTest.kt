package com.github.se.gomeet.model.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthRepositoryTest {

  private val email = "test@123.com"
  private val pwd = "pass1234"
  private val invEmail = "invalid.email.com"
  private val invPwd = "123"

  @After
  fun tearDown() {
    Firebase.auth.currentUser?.delete()
  }

  @Test
  fun testSignUpSuccess() = runTest {
    runBlocking {
      authRepository.signUpWithEmailPassword(email, pwd) { success -> assertTrue(success) }
    }
  }

  @Test
  fun testSignUpFailure() = runTest {
    runBlocking {
      authRepository.signUpWithEmailPassword(invEmail, pwd) { success -> assertFalse(success) }
      authRepository.signUpWithEmailPassword(email, invPwd) { success -> assertFalse(success) }
      authRepository.signUpWithEmailPassword(invEmail, invPwd) { success -> assertFalse(success) }
    }
  }

  @Test
  fun testSignInSuccess() = runTest {
    Firebase.auth.createUserWithEmailAndPassword(email, pwd).await()
    runBlocking {
      authRepository.signInWithEmailPassword(email, pwd) { success -> assertTrue(success) }
    }
    authRepository.currentUser?.delete()
  }

  @Test
  fun testSignInFailure() = runTest {
    runBlocking {
      authRepository.signInWithEmailPassword(email, invPwd) { success -> assertFalse(success) }
      authRepository.signInWithEmailPassword(invEmail, pwd) { success -> assertFalse(success) }
      authRepository.signInWithEmailPassword(invEmail, invPwd) { success -> assertFalse(success) }
    }
  }

  companion object {

    private lateinit var authRepository: AuthRepository

    @BeforeClass
    @JvmStatic
    fun setUp() {
      authRepository = AuthRepository()
    }
  }
}
