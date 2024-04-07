package com.example.clicker.network.domain

import com.example.clicker.network.websockets.AutoModQueueMessage
import kotlinx.coroutines.flow.StateFlow

interface TwitchEventSubscriptionWebSocket {

    val parsedSessionId: StateFlow<String?>
    val autoModMessageQueue: StateFlow<AutoModQueueMessage?>

    fun newWebSocket():Unit

    fun closeWebSocket():Unit
}