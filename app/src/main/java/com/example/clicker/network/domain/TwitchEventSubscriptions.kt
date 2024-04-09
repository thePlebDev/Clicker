package com.example.clicker.network.domain

import com.example.clicker.network.clients.ManageAutoModMessage
import kotlinx.coroutines.flow.Flow

interface TwitchEventSubscriptions {

    fun createEventSubSubscription(
        oAuthToken:String,
        clientId:String,
        broadcasterId:String,
        moderatorId:String,
        sessionId:String,
    ): Flow<String>

    fun manageAutoModMessage(
        oAuthToken:String,
        clientId:String,
        manageAutoModMessageData: ManageAutoModMessage
    ) : Flow<String>
}