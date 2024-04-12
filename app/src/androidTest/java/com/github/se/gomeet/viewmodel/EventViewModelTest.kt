package com.github.se.gomeet.viewmodel/*
import android.net.Uri
import com.github.se.gomeet.EventFirebaseConnection
import com.github.se.gomeet.model.event.Event
import com.github.se.gomeet.model.event.location.Location
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.gomeet.viewmodel.EventViewModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotSame
import kotlinx.coroutines.coroutineScope
import org.junit.Rule
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext

@RunWith(AndroidJUnit4::class)
class EventFirebaseConnectionTest {

    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var eventFirebaseConnection: EventFirebaseConnection

    @Before
    fun setUp() {
        // Mock the FirebaseFirestore instance
        mockFirestore = Firebase.firestore
        // Initialize the EventFirebaseConnection with the mocked Firestore
        eventFirebaseConnection = EventFirebaseConnection(mockFirestore)
    }

    @Test
    fun testGetNewId() {
        // Arrange
        val expectedId = "newId123"
        every { mockFirestore.collection("events").document().id } returns expectedId

        // Act
        val newId = eventFirebaseConnection.getNewId()

        // Assert
        assertNotSame(expectedId, newId, "The new ID should match the expected value.")
    }

    @Test
    suspend fun testGetEventSuccess() { coroutineScope {
        EventViewModel().createEvent(
            "androidtest",
            "androidtest",
            Location(0.0, 0.0, "test"),
            LocalDate.of(2003, 2, 26),
            0.0,
            "androidtest",
            listOf(),
            listOf(),
            0,
            false,
            listOf(),
            listOf(),
            Uri.EMPTY
        )
        val e = EventViewModel().getAllEvents()
        Thread.sleep(3000)
        if (e != null) {
            assert(!e.first{it.title == "androidTest"}.public)
        }
    }
    }
}
*/
