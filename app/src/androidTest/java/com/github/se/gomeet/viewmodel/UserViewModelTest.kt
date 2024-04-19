package com.github.se.gomeet.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.model.user.GoMeetUser
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserViewModelTest {
  private val userViewModel = UserViewModel()
  private val testUid = "testuser"
  private val testUsername = "testuser"

  @Before
  fun createNewUser() {
    userViewModel.createUserIfNew(testUid, testUsername)
  }

  @Test
  fun getUserTest() = runTest {
    var uid = "QpjFlPXRuoYhvtOWcjgR51JYCXs2"
    var user = userViewModel.getUser(uid)

    assert(user != null)
    assert(user!!.uid == uid)

    uid = "this_user_does_not_exist"
    user = userViewModel.getUser(uid)

    assert(user == null)
  }

  @Test
  fun getAndDeleteNewUserTest() = runTest {
    var user = userViewModel.getUser(testUid)

    assert(user != null)
    assert(user!!.uid == testUid)
    assert(user.username == testUsername)

    userViewModel.deleteUser(testUid)
    TimeUnit.SECONDS.sleep(1)
    user = userViewModel.getUser(testUid)

    assert(user == null)
  }

  @Test
  fun editUserTest() = runTest {
    val uid = "QpjFlPXRuoYhvtOWcjgR51JYCXs2"
    var user = userViewModel.getUser(uid)
    val randomNumber = (0..Int.MAX_VALUE).random().toString()
    val newUsername = "test_user_dont_delete_$randomNumber"
    val newUser = GoMeetUser(user!!.uid, newUsername, emptyList(), emptyList(), emptyList())

    userViewModel.editUser(newUser)
    user = userViewModel.getUser(uid)

    assert(user != null)
    assert(user!!.username == newUsername)
  }
}
