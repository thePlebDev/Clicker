package com.example.clicker.network.domain

import com.example.clicker.network.models.websockets.LoggedInUserData
import com.example.clicker.network.models.websockets.RoomState
import com.example.clicker.network.models.websockets.TwitchUserData
import kotlinx.coroutines.flow.StateFlow

/**
 * TwitchSocket is an interface that will act as the API for access all the information inside of the [TwitchWebSocket][com.example.clicker.network.websockets.TwitchWebSocket]
 *
 * @property messageToDeleteId a [StateFlow] String representing the id of the message that was just deleted
 * @property loggedInUserUiState a [StateFlow] [LoggedInUserData]  object used to represent the state of user's logged in status
 * @property state a [StateFlow] [TwitchUserData] object that is used to represent the most recent user message sent from the Twitch IRC server
 * @property latestBannedUserId a [StateFlow] string representing the id of the latest person to be banned or timed out.
 * @property roomState a [StateFlow] [RoomState] object that is used to represent state of the current chat room
 * @property hasWebSocketFailed a [StateFlow] Boolean object that is used to represent if the websocket has failed or not
 * @property run a function used to start the websocket. It should first check if one is already running. If it is, shut it down and then create a new one
 * @property sendMessage a function used to send a message to a chat session
 * @property close a function used to close the current websocket
 *
 * */
interface TwitchSocket {

    val messageToDeleteId: StateFlow<String?>
    val loggedInUserUiState: StateFlow<LoggedInUserData?>
    val state: StateFlow<TwitchUserData>
    val latestBannedUserId: StateFlow<String?>
    val roomState: StateFlow<RoomState?>
    val hasWebSocketFailed: StateFlow<Boolean?>

    /**
     * - run() is a function meant to be called whenever a new websocket needs to be created
     *
     * @param channelName a string representing the channel we want to establish a websocket connection to
     * @param username a String representing the username of the logged in user
     * @param oAuthToken a String representing a token that uniquely identifies the user's logged in session
     * */
    fun run(
        channelName: String?,
        username: String,
        oAuthToken:String,
    ): Unit

    /**
     * - sendMessage() is a function meant to be called whenever a user wants to send a message to a user's chat session
     *
     * @param chatMessage a string representing the message a user is sending to the chat session
     * */
     fun sendMessage(
        chatMessage: String
    ): Boolean

    /**
     *  - close() is a function meant to be called whenever a user wants to end the currently established websocket
     * */
    fun close(): Unit
}