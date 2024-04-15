package com.example.clicker.network.domain

import com.example.clicker.network.models.twitchStream.ChatSettingsData
import com.example.clicker.network.websockets.AutoModMessageUpdate
import com.example.clicker.network.websockets.AutoModQueueMessage
import kotlinx.coroutines.flow.StateFlow

interface TwitchEventSubscriptionWebSocket {

    val parsedSessionId: StateFlow<String?>
    val autoModMessageQueue: StateFlow<AutoModQueueMessage?>
    val messageIdForAutoModQueue: StateFlow<AutoModMessageUpdate?>
    val updatedChatSettingsData: StateFlow<ChatSettingsData?>

    fun newWebSocket():Unit

    fun closeWebSocket():Unit
}