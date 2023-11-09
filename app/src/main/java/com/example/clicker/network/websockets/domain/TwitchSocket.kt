package com.example.clicker.network.websockets.domain

import com.example.clicker.network.websockets.models.LoggedInUserData
import com.example.clicker.network.websockets.models.RoomState
import com.example.clicker.network.websockets.models.TwitchUserData
import kotlinx.coroutines.flow.StateFlow

interface TwitchSocket {

    val messageToDeleteId: StateFlow<String?>

    val loggedInUserUiState: StateFlow<LoggedInUserData?>

    val state: StateFlow<TwitchUserData>

    val roomState: StateFlow<RoomState?>

    fun run(
        channelName: String?,
        username: String
    ): Unit

     fun sendMessage(
        chatMessage: String
    ): Boolean

    fun close(): Unit
}