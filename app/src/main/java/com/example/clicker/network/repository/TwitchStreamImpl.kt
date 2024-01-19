package com.example.clicker.network.repository

import android.util.Log
import com.example.clicker.network.clients.BanUser
import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.domain.TwitchStream
import com.example.clicker.network.models.twitchStream.AutoModSettings
import com.example.clicker.network.models.twitchStream.BanUserResponse
import com.example.clicker.network.models.twitchStream.IndividualAutoModSettings
import com.example.clicker.network.models.twitchStream.UpdateChatSettings
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.net.UnknownHostException
import javax.inject.Inject

class TwitchStreamImpl @Inject constructor(
    private val twitchClient: TwitchClient
): TwitchStream {
    override suspend fun getChatSettings(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String
    ) = flow {
        emit(Response.Loading)

        val response = twitchClient.getChatSettings(
            authorization = oAuthToken,
            clientId = clientId,
            broadcasterId = broadcasterId
        )
        if (response.isSuccessful) {
            emit(Response.Success(response.body()!!))
        } else {
            emit(Response.Failure(Exception(response.message())))
        }
    }.catch { cause ->
        Log.d("GETTINGLIVESTREAMS", "CAUSE IS CAUSE")
        // Log.d("GETTINGLIVESTREAMS","RUNNING THE METHOD USER--> $user ")
        if (cause is UnknownHostException) {
            emit(
                Response.Failure(
                    Exception("Network Error! Please check your connection and try again")
                )
            )
        } else {
            emit(Response.Failure(Exception("Logout Error! Please try again")))
        }
    }

    override suspend fun updateChatSettings(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String,
        moderatorId: String,
        body: UpdateChatSettings
    ): Flow<Response<Boolean>> = flow {
        emit(Response.Loading)

        val response = twitchClient.updateChatSettings(
            authorizationToken = "Bearer $oAuthToken",
            clientId = clientId,
            broadcasterId = broadcasterId,
            moderatorId = moderatorId,
            body = body
        )
        Log.d("changeChatSettingsUpdate", "${response.message()}")
        if (response.isSuccessful) {
            emit(Response.Success(true))
        } else {
            emit(Response.Failure(Exception(response.message())))
        }
    }.catch { cause ->
        Log.d("GETTINGLIVESTREAMS", "CAUSE IS CAUSE")
        // Log.d("GETTINGLIVESTREAMS","RUNNING THE METHOD USER--> $user ")
        if (cause is UnknownHostException) {
            emit(Response.Failure(Exception("response.message()")))
        } else {
            emit(Response.Failure(Exception("response.message()")))
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
            authorizationToken = "Bearer $oAuthToken",
            clientId = clientId,
            broadcasterId = broadcasterId,
            moderatorId = moderatorId,
            messageId = messageId
        )
        if (response.isSuccessful) {
            emit(Response.Success(true))
        } else {
            emit(Response.Failure(Exception("Unable to delete message")))
        }
    }.catch { cause ->

        // Log.d("GETTINGLIVESTREAMS","RUNNING THE METHOD USER--> $user ")
        if (cause is UnknownHostException) {
            emit(Response.Failure(Exception("Failed. Network connection error")))
        } else {
            emit(Response.Failure(Exception("Unable to delete message")))
        }
    }

    override suspend fun banUser(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String,
        moderatorId: String,
        body: BanUser
    ): Flow<Response<BanUserResponse>> = flow {
        emit(Response.Loading)
        val response = twitchClient.banUser(
            authorizationToken = "Bearer $oAuthToken",
            clientId = clientId,
            broadcasterId = broadcasterId,
            moderatorId = moderatorId,
            body = body
        )

        if (response.isSuccessful) {
            val data = response.body()
            data?.let {
                emit(Response.Success(it))
            }
        } else {
            emit(Response.Failure(Exception("Unable to ban user")))
        }
    }.catch { cause ->

        // Log.d("GETTINGLIVESTREAMS","RUNNING THE METHOD USER--> $user ")
        if (cause is UnknownHostException) {
            Log.d("BANUSEREXCEPTION", "UnknownHostException")
            emit(Response.Failure(Exception("Network connection error")))
        } else {
            Log.d("BANUSEREXCEPTION", "Exception Happened")
            emit(Response.Failure(Exception("Unable to ban user")))
        }
    }

    override suspend fun unBanUser(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String,
        moderatorId: String,
        userId: String
    ): Flow<Response<Boolean>> = flow {
        emit(Response.Loading)
        val response = twitchClient.unBanUser(
            authorizationToken = "Bearer $oAuthToken",
            clientId = clientId,
            broadcasterId = broadcasterId,
            moderatorId = moderatorId,
            userId = userId
        )
        if (response.isSuccessful) {
            emit(Response.Success(true))
        } else {
            emit(Response.Failure(Exception("ERROR BANNING USER")))
        }
    }.catch { cause ->

        // Log.d("GETTINGLIVESTREAMS","RUNNING THE METHOD USER--> $user ")
        if (cause is UnknownHostException) {
            Log.d("BANUSEREXCEPTION", "UnknownHostException")
            emit(Response.Failure(Exception("Network connection error")))
        } else {
            Log.d("BANUSEREXCEPTION", "Exception Happened")
            emit(Response.Failure(Exception("Unable to ban user")))
        }
    }

    override suspend fun getAutoModSettings(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String,
        moderatorId: String
    ): Flow<Response<AutoModSettings>> = flow{
        emit(Response.Loading)
        val response = twitchClient.getAutoModSettings(
            authorizationToken = "Bearer $oAuthToken",
            clientId = clientId,
            broadcasterId = broadcasterId,
            moderatorId = moderatorId,
        )
        if(response.isSuccessful){
            val data = response.body()
            Log.d("getAutoModSettings","success data ->${data?.data}")
            data?.let{
                emit(Response.Success(it))
            }

        }else{

            when(response.code()){
                400 ->{
                    Log.d("getAutoModSettings","Bad Request")
                    emit(Response.Failure(Exception("You are not a moderator")))
                }
                401 ->{
                    Log.d("getAutoModSettings","UnAuthorized")
                    emit(Response.Failure(Exception("You are not a moderator")))
                }
                403 ->{
                    Log.d("getAutoModSettings","Forbidden you are not a moderator")
                    emit(Response.Failure(Exception("You are not a moderator")))
                }
                else ->{
                    Log.d("getAutoModSettings","Unable to get Mod settings")
                    emit(Response.Failure(Exception("You are not a moderator")))
                }
            }

        }
    }.catch { cause ->

        // Log.d("GETTINGLIVESTREAMS","RUNNING THE METHOD USER--> $user ")
        if (cause is UnknownHostException) {
            Log.d("getAutoModSettings", "UnknownHostException")
            emit(Response.Failure(Exception("Network connection error")))
        } else {
            Log.d("getAutoModSettings", "Exception Happened")
            emit(Response.Failure(Exception("Unable to get AutoMod settings")))
        }
    }

    override suspend fun updateAutoModSettings(
        oAuthToken: String,
        clientId: String,
        autoModSettings: IndividualAutoModSettings
    ): Flow<Response<AutoModSettings>> = flow{
        emit(Response.Loading)
        val response = twitchClient.updateAutoModSettings(
            authorizationToken = "Bearer $oAuthToken",
            clientId = clientId,
            autoModSettings = autoModSettings
        )
        if(response.isSuccessful){
            response.body()?.let{
                emit(Response.Success(it))
                Log.d("updateAutoModSettingsRequest","Success ->${response.code()}")
            }
        }else{
            Log.d("updateAutoModSettingsRequest","FAILED ->${response.code()}")
            emit(Response.Failure(Exception("Failed to update")))
        }
    }.catch { cause ->

        // Log.d("GETTINGLIVESTREAMS","RUNNING THE METHOD USER--> $user ")
        if (cause is UnknownHostException) {
            Log.d("getAutoModSettings", "UnknownHostException")
            emit(Response.Failure(Exception("Network connection error")))
        } else {
            Log.d("getAutoModSettings", "Exception Happened")
            emit(Response.Failure(Exception("Unable to get AutoMod settings")))
        }
    }
}