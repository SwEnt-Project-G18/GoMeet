package com.github.se.gomeet.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserViewModelTest {
  private val userViewModel = UserViewModel()
  private val randomNumber = (0..Int.MAX_VALUE).random().toString()
  private val randomUid = "deletethis$randomNumber"
  private val randomUsername = "$randomNumber@test.com"

  @Before
  fun createNewUser() {
    userViewModel.createUserIfNew(randomUid, randomUsername)
  }

  @Test
  fun getUserTestExisting() = runTest {
    val uid = "QpjFlPXRuoYhvtOWcjgR51JYCXs2"
    val user = userViewModel.getUser(uid)

    assert(user != null)
    assert(user!!.uid == uid)
    assert(user.username == "qwe@asd.com")
  }

  @Test
  fun getUserTestNew() = runTest {
    TimeUnit.SECONDS.sleep(1)

    val user = userViewModel.getUser(randomUid)

    assert(user != null)
    assert(user!!.uid == randomUid)
    assert(user.username == randomUsername)
  }
}
