package com.example.clicker.network.domain

import kotlinx.coroutines.flow.Flow

interface TwitchEventSubscriptions {

    fun createEventSubSubscription(
        oAuthToken:String,
        clientId:String,
        broadcasterId:String,
        moderatorId:String,
        sessionId:String,
    ): Flow<String>
}