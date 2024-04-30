package com.github.se.gomeet.endtoend

/**
 * package com.github.se.gomeet.endtoend
 *
 * import androidx.compose.ui.test.isDisplayed import
 * androidx.compose.ui.test.junit4.createAndroidComposeRule import
 * androidx.compose.ui.test.onNodeWithTag import androidx.compose.ui.test.onNodeWithText import
 * androidx.compose.ui.test.performClick import androidx.test.ext.junit.runners.AndroidJUnit4 import
 * com.github.se.gomeet.MainActivity import com.github.se.gomeet.model.event.location.Location
 * import com.github.se.gomeet.screens.LoginScreen import com.github.se.gomeet.screens.TrendsScreen
 * import com.github.se.gomeet.screens.WelcomeScreenScreen import
 * com.github.se.gomeet.viewmodel.EventViewModel import com.github.se.gomeet.viewmodel.UserViewModel
 * import com.google.firebase.auth.ktx.auth import com.google.firebase.ktx.Firebase import
 * com.kaspersky.kaspresso.testcases.api.testcase.TestCase import
 * io.github.kakaocup.compose.node.element.ComposeScreen import java.time.LocalDate import
 * java.util.concurrent.TimeUnit import kotlinx.coroutines.runBlocking import org.junit.After import
 * org.junit.BeforeClass import org.junit.Rule import org.junit.Test import org.junit.runner.RunWith
 *
 * @RunWith(AndroidJUnit4::class) class EndToEndTest2 : TestCase() {
 *
 * @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()
 *
 * @After fun tearDown() { // Clean up the user runBlocking { eventVM.getAllEvents()?.forEach { if
 *   (it.creator == uid1) eventVM.removeEvent(it.uid) } } Firebase.auth.currentUser?.delete()
 *   userVM.deleteUser(uid1) userVM.deleteUser(uid2) }
 * @Test fun test() = run { ComposeScreen.onComposeScreen<WelcomeScreenScreen>(composeTestRule) {
 *   step("Click on log in button") { logInButton { assertIsDisplayed() performClick() } } }
 *
 * ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) { step("Log in with email and
 * password") { logInButton { assertIsDisplayed() assertIsNotEnabled() } emailField {
 * assertIsDisplayed() performTextInput(email2) } passwordField { assertIsDisplayed()
 * performTextInput(pwd2) } logInButton { assertIsEnabled() performClick()
 * composeTestRule.waitForIdle() composeTestRule.waitUntil(timeoutMillis = 5000) {
 * composeTestRule.onNodeWithTag("CreateUI").isDisplayed() } } } }
 *
 * composeTestRule.onNodeWithText("Trends").performClick() composeTestRule.waitForIdle()
 * composeTestRule.onNodeWithText("Trends").performClick()
 *
 * ComposeScreen.onComposeScreen<TrendsScreen>(composeTestRule) {
 * composeTestRule.waitUntil(timeoutMillis = 10000) {
 * composeTestRule.onNodeWithTag("Card").isDisplayed() } } }
 *
 * companion object {
 *
 * private val email1 = "user1@test.com" private val pwd1 = "123456" private val uid1 = "testuid1"
 * private val username1 = "testuser1"
 *
 * private val email2 = "user2@test.com" private val pwd2 = "123456" private val uid2 = "testuid2"
 * private val username2 = "testuser2"
 *
 * private lateinit var userVM: UserViewModel private lateinit var eventVM: EventViewModel
 *
 * @JvmStatic
 * @BeforeClass fun setup() { userVM = UserViewModel()
 *
 * userVM.createUserIfNew(uid1, username1) userVM.createUserIfNew(uid2, username2)
 * Firebase.auth.createUserWithEmailAndPassword(email1, pwd1)
 * Firebase.auth.createUserWithEmailAndPassword(email2, pwd2) TimeUnit.SECONDS.sleep(10)
 *
 * eventVM = EventViewModel(uid1) eventVM.createEvent( "title", "description", Location(0.0, 0.0,
 * "location"), LocalDate.now(), 0.0, "url", emptyList(), emptyList(), 0, true, emptyList(),
 * emptyList(), null, userVM, uid1) TimeUnit.SECONDS.sleep(10) eventVM = EventViewModel(uid2) } } }*
 */
