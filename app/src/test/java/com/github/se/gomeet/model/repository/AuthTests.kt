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
  private val invEmail = "invalid.email"
  private val invPwd = "123"

  @Before
  fun setUp() {
    // Initialize your repository with the mocked FirebaseAuth
    authRepository = AuthRepository(firebaseAuth)
  }

  @Test
  fun testSignUpSuccess() = runTest {

    val successfulTask: Task<AuthResult> = Tasks.forResult(mock())
    whenever(firebaseAuth.createUserWithEmailAndPassword(anyString(), anyString()))
      .thenReturn(successfulTask)

    launch{
      authRepository.signUpWithEmailPassword(vEmail, vPwd) { success ->
      assertTrue(success)
      }
    }.join()

  }

  @Test
  fun testSignUpFailure() = runTest {

    val exception = FirebaseAuthException("auth/error", "Authentication failed")
    val unsuccessfulTask: Task<AuthResult> = Tasks.forException(exception)
    whenever(firebaseAuth.createUserWithEmailAndPassword(anyString(), anyString()))
      .thenReturn(unsuccessfulTask)

    launch{
      authRepository.signUpWithEmailPassword(invEmail, vPwd) { success ->
        assertFalse(success)
      }
    }.join()

    launch{
      authRepository.signUpWithEmailPassword(vEmail, invPwd) { success ->
        assertFalse(success)
      }
    }.join()

    launch{
      authRepository.signUpWithEmailPassword(invEmail, invPwd) { success ->
        assertFalse(success)
      }
    }.join()
  }

}
