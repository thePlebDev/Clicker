package com.example.clicker.network.domain

import com.example.clicker.network.clients.ManageAutoModMessage
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow

interface TwitchEventSubscriptions {

    fun createEventSubSubscription(
        oAuthToken:String,
        clientId:String,
        broadcasterId:String,
        moderatorId:String,
        sessionId:String,
    ): Flow<Response<Boolean>>

    fun manageAutoModMessage(
        oAuthToken:String,
        clientId:String,
        manageAutoModMessageData: ManageAutoModMessage
    ) : Flow<Response<Boolean>>
}