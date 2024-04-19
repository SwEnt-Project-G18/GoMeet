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
  private val randomUid = "fakeuser"
  private val randomUsername = "fakeuser"

  @Before
  fun createNewUser() {
    userViewModel.createUserIfNew(randomUid, randomUsername)
  }

  @Test
  fun getExistingUser() = runTest {
    val uid = "QpjFlPXRuoYhvtOWcjgR51JYCXs2"
    val user = userViewModel.getUser(uid)

    assert(user != null)
    assert(user!!.uid == uid)
    assert(user.username == "qwe@asd.com")
  }

  @Test
  fun getAndDeleteNewUser() = runTest {
    TimeUnit.SECONDS.sleep(1)

    var user = userViewModel.getUser(randomUid)

    assert(user != null)
    assert(user!!.uid == randomUid)
    assert(user.username == randomUsername)

    userViewModel.deleteUser(randomUid)
    TimeUnit.SECONDS.sleep(1)

    user = userViewModel.getUser(randomUid)

    assert(user == null)
  }
}
