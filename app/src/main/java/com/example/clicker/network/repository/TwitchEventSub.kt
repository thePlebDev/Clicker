package com.example.clicker.network.repository

import android.util.Log
import com.example.clicker.network.clients.Condition
import com.example.clicker.network.clients.EvenSubSubscription
import com.example.clicker.network.clients.Transport
import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.clients.TwitchHomeClient
import com.example.clicker.network.repository.util.handleNetworkNewUserExceptions
import com.example.clicker.util.NetworkNewUserResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
class TwitchEventSub @Inject constructor(
    private val twitchClient: TwitchClient,
) {

    fun createEventSubSubscription(
        oAuthToken:String,
        clientId:String,
        broadcasterId:String,
        moderatorId:String,
        sessionId:String,
    ):Flow<String> = flow {

        val body = EvenSubSubscription(
            type = "automod.message.hold",
            version="1",
            condition = Condition(broadcaster_user_id =broadcasterId,moderator_user_id = moderatorId ),
            transport = Transport(session_id = sessionId)
        )

        val response = twitchClient.createEventSubSubscription(
            authorizationToken = "Bearer $oAuthToken",
            clientId = clientId,
            broadcasterId = broadcasterId,
            evenSubSubscription =body

        )

        if (response.isSuccessful) {
            Log.d("createEventSubSubscriptionRepo","SUCCESS")
            Log.d("createEventSubSubscriptionRepo","response --> ${response.code()}")
            Log.d("createEventSubSubscriptionRepo","body --> ${response.body()}")
            Log.d("createEventSubSubscriptionRepo","message --> ${response.message()}")
            emit("NetworkNewUserResponse.Success(body.data)")
        } else {
            Log.d("createEventSubSubscriptionRepo","FAILED")
            Log.d("createEventSubSubscriptionRepo","response --> ${response.code()}")
            Log.d("createEventSubSubscriptionRepo","body --> ${response.body()}")
            Log.d("createEventSubSubscriptionRepo","message --> ${response.message()}")
            emit("FAILED")
        }

    }.catch { cause ->
        Log.d("createEventSubSubscriptionRepo","cause.message --> ${cause.message}")
        Log.d("createEventSubSubscriptionRepo","cause --> ${cause}")
        emit("catchError")

    }


}