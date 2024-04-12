package com.example.clicker.network.repository

import android.util.Log
import com.example.clicker.network.clients.BlockedTerm
import com.example.clicker.network.clients.Condition
import com.example.clicker.network.clients.EvenSubSubscription
import com.example.clicker.network.clients.ManageAutoModMessage
import com.example.clicker.network.clients.Transport
import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.clients.TwitchHomeClient
import com.example.clicker.network.domain.TwitchEventSubscriptions
import com.example.clicker.network.repository.util.handleNetworkNewUserExceptions
import com.example.clicker.util.NetworkNewUserResponse
import com.example.clicker.util.Response
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
        type:String,
    ):Flow<Response<Boolean>> = flow {
        emit(Response.Loading)

        val body = EvenSubSubscription(
            type = type,
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
            emit(Response.Success(true))
        } else {
            emit(Response.Failure(Exception("failed request")))
        }

    }.catch { cause ->
        emit(Response.Failure(Exception("Error caught")))
    }
    override fun manageAutoModMessage(
        oAuthToken: String,
        clientId: String,
        manageAutoModMessageData: ManageAutoModMessage
    ):Flow<Response<Boolean>> = flow {
        emit(Response.Loading)
        val response = twitchClient.manageAutoModMessage(
            authorizationToken = "Bearer $oAuthToken",
            clientId = clientId,
            manageAutoModMessageData = manageAutoModMessageData
        )

        if (response.isSuccessful) {
            emit(Response.Success(true))
        } else {

            emit(Response.Failure(Exception("Failed action")))
        }

    }.catch { cause ->
        Log.d("manageAutoModMessage","ERROR CAUGHT")
        Log.d("manageAutoModMessage","cause.message --> ${cause.message}")
        Log.d("manageAutoModMessage","cause --> ${cause}")
        emit(Response.Failure(Exception("Error")))

    }

    override fun getBlockedTerms(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String,
        moderatorId: String
    ): Flow<Response<List<BlockedTerm>>> = flow{
        emit(Response.Loading)
        val response = twitchClient.getBlockedTerms(
            authorizationToken = "Bearer $oAuthToken",
            clientId = clientId,
            broadcasterId=broadcasterId,
            moderatorId =moderatorId
        )

        if (response.isSuccessful) {
            Log.d("getBlockedTerms","SUCCESS")
            Log.d("getBlockedTerms","response.code --> ${response.code()}")
            Log.d("getBlockedTerms","response.message --> ${response.message()}")
            val data = response.body()?.data ?: listOf<BlockedTerm>()
            emit(Response.Success(data))
        } else {
            Log.d("getBlockedTerms","FAILED")
            Log.d("getBlockedTerms","response.code --> ${response.code()}")
            Log.d("getBlockedTerms","response.message --> ${response.message()}")
            emit(Response.Failure(Exception("Failed action")))
        }
    }.catch { cause ->
        Log.d("getBlockedTerms","ERROR CAUGHT")
        Log.d("getBlockedTerms","cause.message --> ${cause.message}")
        Log.d("manageAutoModMessage","cause --> ${cause}")
        emit(Response.Failure(Exception("Error")))

    }

    override fun deleteBlockedTerm(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String,
        moderatorId: String,
        id: String
    ): Flow<Response<Boolean>> = flow{
        emit(Response.Loading)
        val response = twitchClient.deleteBlockedTerm(
            authorizationToken = "Bearer $oAuthToken",
            clientId = clientId,
            broadcasterId=broadcasterId,
            moderatorId =moderatorId,
            id =id
        )

        if (response.isSuccessful) {
            Log.d("deleteBlockedTerm","SUCCESS")
            Log.d("deleteBlockedTerm","response.code --> ${response.code()}")
            Log.d("deleteBlockedTerm","response.message --> ${response.message()}")
            emit(Response.Success(true))
        } else {
            Log.d("deleteBlockedTerm","FAILED")
            Log.d("deleteBlockedTerm","response.code --> ${response.code()}")
            Log.d("deleteBlockedTerm","response.message --> ${response.message()}")
            emit(Response.Failure(Exception("Failed action")))
        }

    }.catch { cause ->
        Log.d("getBlockedTerms","ERROR CAUGHT")
        Log.d("getBlockedTerms","cause.message --> ${cause.message}")
        Log.d("manageAutoModMessage","cause --> ${cause}")
        emit(Response.Failure(Exception("Error")))

    }


}