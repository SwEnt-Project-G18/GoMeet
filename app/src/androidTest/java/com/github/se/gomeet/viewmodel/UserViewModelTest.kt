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
  private val uid = "testuid"
  private val username = "testuser"

  @Before
  fun createNewUser() {
    userViewModel.createUserIfNew(uid, username)
  }

  @Test
  fun test() = runTest {
    // wait for user to be created
    TimeUnit.SECONDS.sleep(2)

    // test getUser and createUser
    var user = userViewModel.getUser(uid)

    assert(user != null)
    assert(user!!.uid == uid)
    assert(user.username == username)

    assert(userViewModel.getUser("this_user_does_not_exist") == null)

    // test editUser
    val newUsername = "newtestuser"
    val newUser = GoMeetUser(user.uid, newUsername, emptyList(), emptyList(), emptyList())

    userViewModel.editUser(newUser)
    user = userViewModel.getUser(uid)

    assert(user != null)
    assert(user!!.uid == uid)
    assert(user.username == newUsername)

    // test deleteUser
    userViewModel.deleteUser(user)
    user = userViewModel.getUser(uid)

    assert(user == null)
  }
}
