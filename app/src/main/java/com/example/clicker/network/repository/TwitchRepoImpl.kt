package com.example.clicker.network.repository

import android.util.Log
import com.example.clicker.network.clients.BanUser
import com.example.clicker.network.clients.GetModChannels
import com.example.clicker.network.clients.GetModChannelsData
import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.domain.TwitchStream
import com.example.clicker.network.interceptors.NoNetworkException
import com.example.clicker.network.models.twitchRepo.FollowedLiveStreams
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.network.models.twitchStream.UpdateChatSettings
import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.network.models.twitchRepo.toStreamInfo
import com.example.clicker.network.models.twitchStream.AutoModSettings
import com.example.clicker.network.models.twitchStream.BanUserResponse
import com.example.clicker.network.models.twitchStream.IndividualAutoModSettings
import com.example.clicker.network.repository.util.handleException
import com.example.clicker.network.repository.util.handleNetworkAuthExceptions
import com.example.clicker.presentation.home.StreamInfo
import com.example.clicker.util.LogWrap
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.Response
import com.example.clicker.util.logCoroutineInfo
import java.net.UnknownHostException
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class TwitchRepoImpl @Inject constructor(
    private val twitchClient: TwitchClient
) : TwitchRepo  {


    override suspend fun getFollowedLiveStreams(
        authorizationToken: String,
        clientId: String,
        userId: String
    ): Flow<Response<List<StreamInfo>>> = flow {
        emit(Response.Loading)

        val response = twitchClient.getFollowedStreams(
            authorization = "Bearer $authorizationToken",
            clientId = clientId,
            userId = userId
        )

        val emptyBody = FollowedLiveStreams(listOf<StreamData>())
        val body = response.body() ?: emptyBody
        val exported = body.data.map { it.toStreamInfo() }

        if (response.isSuccessful) {
            emit(Response.Success(exported))
        } else {
            emit(Response.Failure(Exception("Error!, code: {${response.code()}}")))
        }
    }.catch { cause ->
        handleException(cause)
    }

    override suspend fun getModeratedChannels(
        authorizationToken: String,
        clientId: String,
        userId: String
    ):Flow<NetworkAuthResponse<GetModChannels>> = flow{
        emit(NetworkAuthResponse.Loading)
        val emptyBody = GetModChannels(data= listOf())
        val response = twitchClient.getModeratedChannels(
            authorizationToken = "Bearer $authorizationToken",
            clientId = clientId,
            userId = userId
        )
        val body = response.body() ?: emptyBody
        Log.d("getModeratedChannels","code --->${response.code()}")
        Log.d("getModeratedChannels","message -> ${response.message()}")
        Log.d("getModeratedChannels","errorbody -> ${response.errorBody()}")
        if (response.isSuccessful) {
            emit(NetworkAuthResponse.Success(body))
        } else {
            emit(NetworkAuthResponse.Failure(Exception("Error!, code: {${response.code()}}")))
        }
    }.catch { cause ->
        handleNetworkAuthExceptions(cause)
    }
}