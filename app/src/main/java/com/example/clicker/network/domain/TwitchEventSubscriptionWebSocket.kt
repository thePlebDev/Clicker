package com.example.clicker.network.domain

import kotlinx.coroutines.flow.StateFlow

interface TwitchEventSubscriptionWebSocket {

    val parsedSessionId: StateFlow<String?>

    fun newWebSocket():Unit

    fun closeWebSocket():Unit
}