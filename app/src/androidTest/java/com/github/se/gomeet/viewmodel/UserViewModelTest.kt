package com.github.se.gomeet.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserViewModelTest {

  @Test
  fun getUserTest() = runTest {
    val userViewModel = UserViewModel()
    val uid = "QpjFlPXRuoYhvtOWcjgR51JYCXs2"
    val user = userViewModel.getUser(uid)

    assert(user != null)
    assert(user!!.uid == uid)
    assert(user.username == "qwe@asd.com")
  }
}
