package com.example.clicker.network.repository

import android.util.Log
import com.example.clicker.network.clients.BanUser
import com.example.clicker.network.clients.ChannelInformation
import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.clients.WarnUserBody
import com.example.clicker.network.domain.TwitchStream
import com.example.clicker.network.models.twitchStream.AutoModSettings
import com.example.clicker.network.models.twitchStream.BanUserResponse
import com.example.clicker.network.models.twitchStream.IndividualAutoModSettings
import com.example.clicker.network.models.twitchStream.UpdateChatSettings
import com.example.clicker.network.repository.util.handleException
import com.example.clicker.util.Response
import com.example.clicker.util.WebSocketResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.net.UnknownHostException
import javax.inject.Inject

class TwitchStreamImpl @Inject constructor(
    private val twitchClient: TwitchClient,

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
            emit(Response.Failure(Exception("Error! Please try again")))
        }
    }.catch { cause ->
        Log.d("GETTINGLIVESTREAMS", "CAUSE IS CAUSE")

        handleException(cause)
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
            emit(Response.Failure(Exception("Error! Please try again")))
        }
    }.catch { cause ->
        Log.d("GETTINGLIVESTREAMS", "CAUSE IS CAUSE")
        handleException(cause)
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
        handleException(cause)
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
        Log.d("banUserImpl","code -->${response.code()}")
        Log.d("banUserImpl","message -->${response.message()}")
        Log.d("banUserImpl","body -->${response.body()}")

        if (response.isSuccessful) {
            val data = response.body()

            emit(Response.Success(data!!))

        } else {
            emit(Response.Failure(Exception("Unable to ban user")))
        }
    }.catch { cause ->

        // Log.d("GETTINGLIVESTREAMS","RUNNING THE METHOD USER--> $user ")
        handleException(cause)
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
        handleException(cause)
    }

    override suspend fun warnUser(
        oAuthToken: String,
        clientId: String,
        moderatorId: String,
        broadcasterId: String,
        body: WarnUserBody
    ): Flow<Response<Boolean>> =flow{
        emit(Response.Loading)
        val response = twitchClient.warnUser(
            authorizationToken = "Bearer $oAuthToken",
            clientId = clientId,
            broadcasterId = broadcasterId,
            moderatorId = moderatorId,
            body = body
        )
        if(response.isSuccessful){
            Log.d("warnUserResponse","SUCCESS")
            val data = response.body()?.data?.get(0)
            Log.d("warnUserResponse","response -> $data")
            emit(Response.Success(true))
        }else{
            Log.d("warnUserResponse","FAILED")
            emit(Response.Failure(Exception("There was a problem")))
            Log.d("warnUserResponse","code ->${response.code()}")
            Log.d("warnUserResponse","code ->${response.message()}")

        }

    }.catch {cause ->
        Log.d("warnUserResponse","EXCEPTION")
        Log.d("warnUserResponse","message ->${cause.message}")
        Log.d("warnUserResponse","cause ->${cause.cause}")
        emit(Response.Failure(Exception("Error caught")))
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

            emit(Response.Success(data!!))


        }else{

            emit(Response.Failure(Exception("You are not a moderator")))

        }
    }.catch { cause ->

        handleException(cause)
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

                emit(Response.Success(response.body()!!))
                Log.d("updateAutoModSettingsRequest","Success ->${response.code()}")

        }else{
            Log.d("updateAutoModSettingsRequest","FAILED ->${response.code()}")
            emit(Response.Failure(Exception("Failed to update")))
        }
    }.catch { cause ->

        // Log.d("GETTINGLIVESTREAMS","RUNNING THE METHOD USER--> $user ")
        handleException(cause)
    }

    override suspend fun updateChannelInformation(
        authorizationToken: String,
        clientId: String,
        broadcasterId: String,
        channelInformation: ChannelInformation
    ): Flow<Response<Boolean>> =flow{
        Log.d("updateChannelInformation","authorizationToken -> Bearer $authorizationToken")
        Log.d("updateChannelInformation","clientId -> $clientId")
        Log.d("updateChannelInformation","broadcasterId -> $broadcasterId")
        Log.d("updateChannelInformation","channelInformation -> $channelInformation")

        emit(Response.Loading)
        val response = twitchClient.updateChannelInformation(
            authorizationToken = "Bearer $authorizationToken",
            clientId = clientId,
            broadcasterId = broadcasterId,
            channelInformation = channelInformation
        )
        if (response.isSuccessful) {
            emit(Response.Success(true))
            Log.d("updateChannelInformation","message ->${response.message()}")
            Log.d("updateChannelInformation","code ->${response.code()}")
            Log.d("updateChannelInformation","body ->${response.body()}")
        } else {
            Log.d("updateChannelInformation","message ->${response.message()}")
            Log.d("updateChannelInformation","code ->${response.code()}")
            Log.d("updateChannelInformation","body ->${response.body()}")
            emit(Response.Failure(Exception("Failed to update channel info")))
        }
    }.catch { cause ->

        // Log.d("GETTINGLIVESTREAMS","RUNNING THE METHOD USER--> $user ")
        handleException(cause)
    }
}