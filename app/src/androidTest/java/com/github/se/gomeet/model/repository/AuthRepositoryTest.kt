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
      AuthRepository.signUpWithEmailPassword(email, pwd) { success -> assertTrue(success) }
    }
  }

  @Test
  fun testSignUpFailure() = runTest {
    runBlocking {
      AuthRepository.signUpWithEmailPassword(invEmail, pwd) { success -> assertFalse(success) }
      AuthRepository.signUpWithEmailPassword(email, invPwd) { success -> assertFalse(success) }
      AuthRepository.signUpWithEmailPassword(invEmail, invPwd) { success -> assertFalse(success) }
    }
  }

  @Test
  fun testSignInSuccess() = runTest {
    Firebase.auth.createUserWithEmailAndPassword(email, pwd).await()
    runBlocking {
      AuthRepository.signInWithEmailPassword(email, pwd) { success -> assertTrue(success) }
    }
    AuthRepository.currentUser?.delete()
  }

  @Test
  fun testSignInFailure() = runTest {
    runBlocking {
      AuthRepository.signInWithEmailPassword(email, invPwd) { success -> assertFalse(success) }
      AuthRepository.signInWithEmailPassword(invEmail, pwd) { success -> assertFalse(success) }
      AuthRepository.signInWithEmailPassword(invEmail, invPwd) { success -> assertFalse(success) }
    }
  }
}
