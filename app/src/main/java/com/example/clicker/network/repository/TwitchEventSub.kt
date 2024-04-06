package com.example.clicker.network.repository

import com.example.clicker.network.clients.Condition
import com.example.clicker.network.clients.EvenSubSubscription
import com.example.clicker.network.clients.Transport
import com.example.clicker.network.clients.TwitchHomeClient
import javax.inject.Inject

class TwitchEventSub @Inject constructor(
    private val twitchClient: TwitchHomeClient,
) {

    fun createEventSubSubscription(
        oAuthToken:String,
        clientId:String,
        broadcasterId:String,
        moderatorId:String,
        sessionId:String,
    ){
        val body = EvenSubSubscription(
            type = "automod.message.hold",
            version="1",
            condition = Condition(broadcaster_user_id =broadcasterId,moderator_user_id = moderatorId ),
            transport = Transport(session_id = sessionId)
        )



    }


}