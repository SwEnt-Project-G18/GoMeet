package com.github.se.gomeet.model.repository

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AuthTests {

  private lateinit var authRepository: AuthRepository
  private val firebaseAuth: FirebaseAuth = mock()
  private val email = "test@123.com"
  private val pwd = "pass1234"

  @Before
  fun setUp() {
    // Initialize your repository with the mocked FirebaseAuth
    authRepository = AuthRepository()
  }

  @Test
  fun testSignUpSuccess() = runTest {
    val successfulTask: Task<AuthResult> = Tasks.forResult(mock())
    whenever(firebaseAuth.createUserWithEmailAndPassword(anyString(), anyString()))
        .thenReturn(successfulTask)

    launch { authRepository.signUpWithEmailPassword(email, pwd) { success -> assertTrue(success) } }
        .join()
  }

  @Test
  fun testSignUpFailure() = runTest {
    val exception = FirebaseAuthException("auth/error", "Authentication failed")
    val unsuccessfulTask: Task<AuthResult> = Tasks.forException(exception)
    whenever(firebaseAuth.createUserWithEmailAndPassword(anyString(), anyString()))
        .thenReturn(unsuccessfulTask)

    launch {
          authRepository.signUpWithEmailPassword(email, pwd) { success -> assertFalse(success) }
        }
        .join()
  }

  @Test
  fun testSignInSuccess() = runTest {
    val successfulTask: Task<AuthResult> = Tasks.forResult(mock())
    whenever(firebaseAuth.signInWithEmailAndPassword(anyString(), anyString()))
        .thenReturn(successfulTask)

    launch { authRepository.signInWithEmailPassword(email, pwd) { success -> assertTrue(success) } }
        .join()
  }

  @Test
  fun testSignInFailure() = runTest {
    val exception = FirebaseAuthException("auth/error", "Authentication failed")
    val unsuccessfulTask: Task<AuthResult> = Tasks.forException(exception)
    whenever(firebaseAuth.createUserWithEmailAndPassword(anyString(), anyString()))
        .thenReturn(unsuccessfulTask)

    launch {
          authRepository.signInWithEmailPassword(email, pwd) { success -> assertTrue(!success) }
        }
        .join()
  }
}
