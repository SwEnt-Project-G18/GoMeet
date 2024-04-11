package com.github.se.gomeet.model.repository

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.kotlin.whenever
import org.mockito.kotlin.any


// Choose either one mock() or the other
//import org.mockito.Mockito.mock
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner


//@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class AuthTests {

  private lateinit var authRepository: AuthRepository
  private val firebaseAuth: FirebaseAuth = mock()

  @Before
  fun setUp() {
    // Initialize your repository with the mocked FirebaseAuth
    authRepository = AuthRepository(firebaseAuth)
  }

  @Test
  fun testSignUpWithEmailPassword_Success() = runBlockingTest {
    // Mock FirebaseAuth to simulate a successful sign-up
    val successfulTask: Task<AuthResult> = Tasks.forResult(mock())
    whenever(firebaseAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(successfulTask)

    authRepository.signUpWithEmailPassword("email@example.com", "password123") { success ->
      // Assert that the success callback is invoked with true
      assertTrue("The onComplete callback should be invoked with true.", success)
    }
  }

  @Test
  fun testSignUpWithEmailPassword_Failure() = runBlockingTest {
    // Mock FirebaseAuth to simulate a failed sign-up
    // Create a Task instance that represents a failed operation
    val exception = FirebaseAuthException("errorCode", "Authentication failed")
    val failedTask: Task<AuthResult> = Tasks.forException(exception)
    whenever(firebaseAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(failedTask)

    authRepository.signUpWithEmailPassword("email@example.com", "password") { success ->
      // Assert that the success callback is invoked with false
      assertFalse("The onComplete callback should be invoked with false.", success)
    }
  }

}
