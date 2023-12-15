package com.example.clicker.network.websockets.domain

import com.example.clicker.network.websockets.models.LoggedInUserData
import com.example.clicker.network.websockets.models.RoomState
import com.example.clicker.network.websockets.models.TwitchUserData
import kotlinx.coroutines.flow.StateFlow

/**
 * TwitchSocket is an interface that will act as the API for access all the information inside of the [TwitchWebSocket][com.example.clicker.network.websockets.TwitchWebSocket]
 *
 * */
interface TwitchSocket {

    /**
     *  a String representing the id of the message that was just deleted
     * */
    val messageToDeleteId: StateFlow<String?>

    /**
     *  a [LoggedInUserData] used to represent the state of user's logged in status
     * */
    val loggedInUserUiState: StateFlow<LoggedInUserData?>

    /**
     *  a [TwitchUserData] object that is used to represent the most recent user message sent from the Twitch IRC server
     * */
    val state: StateFlow<TwitchUserData>

    /**
     *  A string representing the id of the latest person to be banned or timed out.
     * */
    val latestBannedUserId: StateFlow<String?>

    /**
     *  a [RoomState] object that is used to represent state of the current chat room
     * */
    val roomState: StateFlow<RoomState?>

    /**
     *  a function used to start the websocket. It should first check if one is already running. If it is, shut it down
     *  and then create a new one
     * */
    fun run(
        channelName: String?,
        username: String
    ): Unit

    /**
     *  a function used to send the message to the Twitch IRC server. This is how the messages get send from our device and
     *  are seen by other people on their devices
     * */
     fun sendMessage(
        chatMessage: String
    ): Boolean

    /**
     *  a function used to close the websocket that is connected to the Twitch IRC server
     * */
    fun close(): Unit
}