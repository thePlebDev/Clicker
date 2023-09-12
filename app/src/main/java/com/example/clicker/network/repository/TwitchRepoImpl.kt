package com.example.clicker.network.repository

import android.util.Log
import com.example.clicker.network.BanUser
import com.example.clicker.network.BanUserResponse
import com.example.clicker.network.TwitchClient
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.ChatSettings
import com.example.clicker.network.models.FollowedLiveStreams
import com.example.clicker.network.models.UpdateChatSettings
import com.example.clicker.network.models.ValidatedUser
import com.example.clicker.network.models.toStreamInfo
import com.example.clicker.presentation.home.StreamInfo
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TwitchRepoImpl @Inject constructor(
    private val twitchClient: TwitchClient
): TwitchRepo {

    override suspend fun validateToken(token:String):Flow<Response<ValidatedUser>> = flow{
        emit(Response.Loading)
       val response= twitchClient.validateToken(
            authorization = "OAuth $token"
        )
        if(response.isSuccessful){
           emit(Response.Success(response.body()!!))
        }else{
            emit(Response.Failure(Exception("Error! Please login again")))
        }

    }

    override suspend fun getFollowedLiveStreams(
        authorizationToken: String,
        clientId: String,
        userId: String
    ): Flow<Response<List<StreamInfo>>> = flow{
        emit(Response.Loading)
        val response = twitchClient.getFollowedStreams(
            authorization = "Bearer $authorizationToken",
            clientId = clientId,
            userId = userId
        )
        if (response.isSuccessful){

            val transformedData = response.body()!!.data.map { it.toStreamInfo() }
            emit(Response.Success(transformedData))
        }else{

            emit(Response.Failure(Exception("Error!, code: {${response.code()}}")))

        }
    }

    override fun logout(clientId:String,token:String): Flow<Response<String>> = flow{
        emit(Response.Loading)
        val response = twitchClient.logout(clientId = clientId, token = token)
        if (response.isSuccessful){
            Log.d("logoutResponse", "SUCCESS ->${response.message()}")
            emit(Response.Success("true"))
        }else{

            Log.d("logoutResponse", "FAILED ->${response.message()}")
            emit(Response.Failure(Exception("Error!, code: {${response.code()}}")))

        }
    }

    override suspend fun getChatSettings(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String
    )= flow {
        emit(Response.Loading)
         val response = twitchClient.getChatSettings(
            authorization = oAuthToken,
            clientId = clientId,
            broadcasterId = broadcasterId
        )
        if (response.isSuccessful){
            emit(Response.Success(response.body()!!))
        }else{

            emit(Response.Failure(Exception(response.message())))
        }
    }

    override suspend fun updateChatSettings(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String,
        moderatorId: String,
        body: UpdateChatSettings
    ): Flow<Response<Boolean>>  = flow{
        emit(Response.Loading)
        val response =twitchClient.updateChatSettings(
            authorizationToken = "Bearer $oAuthToken",
            clientId = clientId,
            broadcasterId = broadcasterId,
            moderatorId= moderatorId,
            body = body
        )
        Log.d("changeChatSettingsUpdate","REQUEST MADE")
        if (response.isSuccessful){
            emit(Response.Success(true))
        }else{
            emit(Response.Failure(Exception(response.message())))
        }
    }

    override suspend fun deleteChatMessage(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String,
        moderatorId: String,
        messageId: String
    ): Flow<Response<Boolean>> = flow {
        emit(Response.Loading)
        val response = twitchClient.deleteChatMessage(
            authorizationToken = "Bearer ${oAuthToken}",
            clientId = clientId,
            broadcasterId = broadcasterId,
            moderatorId = moderatorId,
            messageId = messageId
        )
        if(response.isSuccessful){
            emit(Response.Success(true))
        }else{
            Log.d("deleteChatMessageException",response.message())
            Log.d("deleteChatMessageException",response.code().toString())
            emit(Response.Failure(Exception("MESSAGE NOT DELETED")))
        }
    }

    override suspend fun banUser(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String,
        moderatorId: String,
        body:BanUser
    ): Flow<Response<BanUserResponse>> = flow{
        emit(Response.Loading)
        val response = twitchClient.banUser(
            authorizationToken = "Bearer ${oAuthToken}",
            clientId = clientId,
            broadcasterId = broadcasterId,
            moderatorId = moderatorId,
            body = body
        )
        Log.d("BANUSERRESPONSE","moderatorId --> ${moderatorId}")
        Log.d("BANUSERRESPONSE","oAuthToken --> ${oAuthToken}")
        Log.d("BANUSERRESPONSE","clientId --> ${clientId}")
        Log.d("BANUSERRESPONSE","broadcasterId --> ${broadcasterId}")
        Log.d("BANUSERRESPONSE","body.data --> ${body.data}")
        Log.d("BANUSERRESPONSE","message --> ${response.message()}")


        if(response.isSuccessful){
            val data = response.body()
            data?.let{
                emit(Response.Success(it))
            }
        }else{
            Log.d("BANUSERRESPONSE","code --> ${response.message()}")
            Log.d("BANUSERRESPONSE","message --> ${response.message()}")
            emit(Response.Failure(Exception("Ban User exception")))
        }
    }


}