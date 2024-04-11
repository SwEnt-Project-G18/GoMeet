package com.github.se.gomeet.model.repository

// Choose either one mock() or the other
// import org.mockito.Mockito.mock
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AdditionalUserInfo
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.Dispatchers
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
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

// @ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class AuthTests {

  private lateinit var authRepository: AuthRepository
  private val firebaseAuth: FirebaseAuth = mock()
  private val vEmail = "test@123.com"
  private val vPwd = "pass1234"

  @Before
  fun setUp() {
    // Initialize your repository with the mocked FirebaseAuth
    authRepository = AuthRepository(firebaseAuth)
  }

  @Test
  fun testSignUpWithEmailPassword_Success() = runBlockingTest {
    // Mock FirebaseAuth to simulate a successful sign-up
    val successfulTask: Task<AuthResult> = Tasks.forResult(mock())
    whenever(firebaseAuth.createUserWithEmailAndPassword(anyString(), anyString()))
        .thenReturn(successfulTask)

    authRepository.signUpWithEmailPassword("email@example.com", "password123") { success ->
      println("\n\n SUCCESS: " + success + "\n\n")
      // Assert that the success callback is invoked with true
      assertTrue("The onComplete callback should be invoked with true.", !success)
    }
  }



  @Test
  fun testSignUpSuccess() = runTest {

    val successfulTask: Task<AuthResult> = Tasks.forResult(mock())
    whenever(firebaseAuth.createUserWithEmailAndPassword(anyString(), anyString()))
      .thenReturn(successfulTask)

    println("Launching testSignUpSuccess")

    launch{
      authRepository.signUpWithEmailPassword(vEmail, vPwd) { success ->
      println("RUNNING2")
      assertTrue(success)
      }
    }.join()

    println("Finished testSignUpSuccess")
  }

  @Test
  fun testSignUpWithEmailPassword_Failure() = runBlockingTest {
    // Mock FirebaseAuth to simulate a failed sign-up
    // Create a Task instance that represents a failed operation
    val exception = FirebaseAuthException("errorCode", "Authentication failed")
    val failedTask: Task<AuthResult> = Tasks.forException(exception)
    whenever(firebaseAuth.createUserWithEmailAndPassword(anyString(), anyString()))
        .thenReturn(failedTask)

    authRepository.signUpWithEmailPassword("email@example.com", "password") { success ->
      // Assert that the success callback is invoked with false
      assertFalse("The onComplete callback should be invoked with false.", success)
    }
  }




  private fun mockTask(exception: Exception? = null): Task<AuthResult> {
    val task: Task<AuthResult> = mock()
    `when` { task.isComplete }.thenReturn { true }
    `when` { task.exception }.thenReturn { exception }
    `when` { task.isCanceled }.thenReturn { false }
    val res: AuthResult = mock()
    `when` { res.user }.thenReturn { firebaseAuth.currentUser }
    `when` { res.credential }.thenReturn { mock() }
    `when` { res.additionalUserInfo }.thenReturn { mock() }
    `when` { task.result }.thenReturn { res }
    return task
  }

}
