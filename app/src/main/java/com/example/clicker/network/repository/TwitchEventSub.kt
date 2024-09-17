package com.example.clicker.network.repository

import android.util.Log
import com.example.clicker.network.clients.BlockedTerm
import com.example.clicker.network.clients.Condition
import com.example.clicker.network.clients.EvenSubSubscription
import com.example.clicker.network.clients.ManageAutoModMessage
import com.example.clicker.network.clients.ModViewChatSettings
import com.example.clicker.network.clients.Transport
import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.clients.TwitchHomeClient
import com.example.clicker.network.domain.TwitchEventSubscriptions
import com.example.clicker.network.models.twitchStream.ChatSettings
import com.example.clicker.network.models.twitchStream.ChatSettingsData
import com.example.clicker.network.repository.util.handleNetworkNewUserExceptions
import com.example.clicker.util.NetworkNewUserResponse
import com.example.clicker.util.Response
import com.example.clicker.util.WebSocketResponse
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
    ):Flow<WebSocketResponse<Boolean>> = flow {
        emit(WebSocketResponse.Loading)
        Log.d("createEventSubSubscription","oAuthToken ->$oAuthToken")
        Log.d("createEventSubSubscription","clientId ->$clientId")
        Log.d("createEventSubSubscription","broadcasterId ->$broadcasterId")
        Log.d("createEventSubSubscription","moderatorId ->$moderatorId")
        Log.d("createEventSubSubscription","sessionId ->$sessionId")
       // Log.d("createEventSubSubscription","type ->$type")

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
            Log.d("createEventSubSubscription","SUCCESS type ->$type")
            emit(WebSocketResponse.Success(true))
        } else {
            if(response.code() == 403){
                Log.d("createEventSubSubscription","403 type ->$type")
                Log.d("createEventSubSubscription","403")
                Log.d("createEventSubSubscription", response.message())
                Log.d("createEventSubSubscription", "body -> ${response.body() }")

                emit(WebSocketResponse.FailureAuth403(Exception("Token error")))

            }else{
                Log.d("createEventSubSubscription","type ->$type")
                Log.d("createEventSubSubscription","code is 403 ->${response.code() == 403}")
                Log.d("createEventSubSubscription","message ->${response.message()}")
                Log.d("createEventSubSubscription","body ->${response.body()}")
                emit(WebSocketResponse.Failure(Exception("failed request")))
            }

        }
        Log.d("createEventSubSubscription","-----------------------END--------------------")

    }.catch { cause ->
        emit(WebSocketResponse.Failure(Exception("Error caught")))
    }

    data class ConditionUserId(
        val broadcaster_user_id: String,
        val user_id: String
    )
    data class EvenSubSubscriptionUserId(
        val type: String,
        val version: String,
        val condition: ConditionUserId,
        val transport: Transport
    )
    override fun createEventSubSubscriptionUserId(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String,
        moderatorId: String,
        sessionId: String,
        type: String
    ): Flow<Response<Boolean>>  = flow{
        emit(Response.Loading)


        val body2 = EvenSubSubscriptionUserId(
            type = type,
            version="1",
            condition = ConditionUserId(broadcaster_user_id =broadcasterId,user_id = moderatorId ),
            transport = Transport(session_id = sessionId)
        )

        val response = twitchClient.createEventSubSubscriptionUserId(
            authorizationToken = "Bearer $oAuthToken",
            clientId = clientId,
            broadcasterId = broadcasterId,
            evenSubSubscription =body2

        )




        if (response.isSuccessful) {
            emit(Response.Success(true))
        } else {
            emit(Response.Failure(Exception("failed request")))
        }
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
            Log.d("manageAutoModMessage","SUCCESS")
            emit(Response.Success(true))
        } else {

            Log.d("manageAutoModMessage","ERROR")
            Log.d("manageAutoModMessage","ERROR code->${response.code()}")
            Log.d("manageAutoModMessage","ERROR message->${response.message()}")
            Log.d("manageAutoModMessage","ERROR body->${response.body()}")
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

    override suspend fun getChatSettings(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String
    ) = flow {
        emit(Response.Loading)

        val response = twitchClient.getChatSettings(
            authorization = "Bearer $oAuthToken",
            clientId = clientId,
            broadcasterId = broadcasterId
        )
        if (response.isSuccessful) {
            Log.d("getChatSettings", "SUCCESS")
            Log.d("getChatSettings", "code ->${response.code()}")
            Log.d("getChatSettings", "message ->${response.message()}")
            emit(Response.Success(response.body()!!))
        } else {
            Log.d("getChatSettings", "FAILED")
            Log.d("getChatSettings", "code -->${response.code()}")
            Log.d("getChatSettings", "messaeg ---> ${response.message()}")
            emit(Response.Failure(Exception("Error! Please try again")))
        }
    }.catch { cause ->
        Log.d("getChatSettings", "error caught")

        emit(Response.Failure(Exception("Error")))
    }

    override fun updateModViewChatSettings(
        authorizationToken: String,
        clientId: String,
        broadcasterId: String,
        moderatorId: String,
        body: ChatSettingsData
    ): Flow<Response<ModViewChatSettings>> = flow {
        emit(Response.Loading)
        val response = twitchClient.updateModViewChatSettings(
            authorizationToken = "Bearer $authorizationToken",
            clientId = clientId,
            broadcasterId=broadcasterId,
            moderatorId =moderatorId,
            body=body
        )

        if (response.isSuccessful) {
            Log.d("updateModViewChatSettings", "SUCCESS")
            Log.d("updateModViewChatSettings", "code ->${response.code()}")
            Log.d("updateModViewChatSettings", "message ->${response.message()}")
            emit(Response.Success(response.body()!!))
        } else {
            Log.d("updateModViewChatSettings", "FAILED")
            Log.d("updateModViewChatSettings", "code -->${response.code()}")
            Log.d("updateModViewChatSettings", "body ---> ${response.body()}")
            Log.d("updateModViewChatSettings", "message ---> ${response.message()}")
            emit(Response.Failure(Exception("Error! Please try again")))
        }
    }.catch { cause ->
        Log.d("updateModViewChatSettings", "error caught")
        Log.d("updateModViewChatSettings", "cause -->${cause.message}")
        emit(Response.Failure(Exception("Error")))
    }


}