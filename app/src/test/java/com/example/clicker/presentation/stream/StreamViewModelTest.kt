package com.example.clicker.presentation.stream

import com.example.clicker.domain.TwitchDataStore
import com.example.clicker.network.domain.TwitchStream
import com.example.clicker.network.domain.TwitchSocket
import com.example.clicker.network.models.websockets.RoomState
import com.example.clicker.network.websockets.models.MessageType
import com.example.clicker.util.objectMothers.TwitchUserDataObjectMother
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

class StreamViewModelTestDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@RunWith(MockitoJUnitRunner::class)
class StreamViewModelTest {


    @get:Rule
    val mainDispatcherRule = StreamViewModelTestDispatcherRule()



    @Test
    fun testingTHingers()= runTest{
        val dispatcher = mainDispatcherRule.testDispatcher


        /**THEN*/

        /**THEN*/

        Assert.assertEquals(1, 1)
    }



}