package com.example.clicker.network.repository

import android.util.Log
import com.example.clicker.network.clients.Condition
import com.example.clicker.network.clients.EvenSubSubscription
import com.example.clicker.network.clients.ManageAutoModMessage
import com.example.clicker.network.clients.Transport
import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.clients.TwitchHomeClient
import com.example.clicker.network.domain.TwitchEventSubscriptions
import com.example.clicker.network.repository.util.handleNetworkNewUserExceptions
import com.example.clicker.util.NetworkNewUserResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
class TwitchEventSub @Inject constructor(
    private val twitchClient: TwitchClient,
): TwitchEventSubscriptions {

    override fun createEventSubSubscription(
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

            emit("NetworkNewUserResponse.Success(body.data)")
        } else {

            emit("FAILED")
        }

    }.catch { cause ->
        Log.d("createEventSubSubscriptionRepo","cause.message --> ${cause.message}")
        Log.d("createEventSubSubscriptionRepo","cause --> ${cause}")
        emit("catchError")

    }
    override fun manageAutoModMessage(
        oAuthToken: String,
        clientId: String,
        manageAutoModMessageData: ManageAutoModMessage
    ):Flow<String> = flow {
        emit("LOADING")
        val response = twitchClient.manageAutoModMessage(
            authorizationToken = "Bearer $oAuthToken",
            clientId = clientId,
            manageAutoModMessageData = manageAutoModMessageData
        )

        if (response.isSuccessful) {
            Log.d("manageAutoModMessage","SUCCESS")
            Log.d("manageAutoModMessage","code--> ${response.code()}")
            Log.d("manageAutoModMessage","message--> ${response.message()}")
            Log.d("manageAutoModMessage","body--> ${response.body()}")

            emit("NetworkNewUserResponse.Success(body.data)")
        } else {

            emit("FAILED")
            Log.d("manageAutoModMessage","FAILED")
            Log.d("manageAutoModMessage","code--> ${response.code()}")
            Log.d("manageAutoModMessage","message--> ${response.message()}")
            Log.d("manageAutoModMessage","body--> ${response.body()}")
        }

    }.catch { cause ->
        Log.d("manageAutoModMessage","ERROR CAUGHT")
        Log.d("manageAutoModMessage","cause.message --> ${cause.message}")
        Log.d("manageAutoModMessage","cause --> ${cause}")
        emit("catchError")

    }


}