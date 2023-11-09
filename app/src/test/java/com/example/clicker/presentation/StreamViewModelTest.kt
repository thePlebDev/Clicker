package com.example.clicker.presentation

import com.example.clicker.domain.TwitchDataStore
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.domain.TwitchStream
import com.example.clicker.network.websockets.MessageType
import com.example.clicker.network.websockets.domain.TwitchSocket
import com.example.clicker.network.websockets.models.RoomState
import com.example.clicker.network.websockets.models.TwitchUserData
import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.util.Response
import com.example.clicker.util.objectMothers.TwitchUserDataObjectMother
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
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
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
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

    var twitchRepoImpl: TwitchStream = Mockito.mock(TwitchStream::class.java)
    var webSocket: TwitchSocket = Mockito.mock(TwitchSocket::class.java)
    var tokenDataStore: TwitchDataStore = Mockito.mock(TwitchDataStore::class.java)

    fun mockWebSocketStartingState(webSocket: TwitchSocket){
        val twitchUserData = TwitchUserDataObjectMother
            .addMessageType(MessageType.JOIN)
            .addUserType("Chat cleared by moderator")
            .addColor("#000000")
            .build()
        val initialRoomState = RoomState(false,
            false,
            false,
            false,
            1,
            1
        )
        Mockito.`when`(webSocket.messageToDeleteId).thenReturn(MutableStateFlow(null).asStateFlow())
        Mockito.`when`(webSocket.loggedInUserUiState).thenReturn(MutableStateFlow(null).asStateFlow())
        Mockito.`when`(webSocket.loggedInUserUiState).thenReturn(MutableStateFlow(null).asStateFlow())
        Mockito.`when`(webSocket.state).thenReturn(MutableStateFlow(twitchUserData).asStateFlow())
        Mockito.`when`(webSocket.roomState).thenReturn(MutableStateFlow(initialRoomState).asStateFlow())
    }

    //1) set the dispatcher
    //2) mock the dependencies for streamViewModel
    //3) run the tests
    @Test
    fun testing_delete_chat_message_failed() = runTest{
        /**GIVEN*/

        val messageId = "44"
        Mockito.`when`(twitchRepoImpl.deleteChatMessage("", "", "","",messageId)).thenReturn(
            flow { emit(Response.Failure(Exception("Error thrown"))) }
        )
        mockWebSocketStartingState(webSocket)

        val streamViewModel = StreamViewModel(webSocket,tokenDataStore,twitchRepoImpl,mainDispatcherRule.testDispatcher)

        /**WHEN*/
        streamViewModel.deleteChatMessage(messageId)

        val expectedResult = true

        /**THEN*/

        Assert.assertEquals(expectedResult, streamViewModel.state.value.showStickyHeader)
    }



}