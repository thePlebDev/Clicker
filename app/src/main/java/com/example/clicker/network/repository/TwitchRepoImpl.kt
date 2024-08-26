package com.example.clicker.network.repository

import android.util.Log
import com.example.clicker.network.clients.AllFollowedStreamers
import com.example.clicker.network.clients.BanUser
import com.example.clicker.network.clients.GetModChannels
import com.example.clicker.network.clients.GetModChannelsData
import com.example.clicker.network.clients.TwitchAuthenticationClient
import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.clients.TwitchHomeClient
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
import com.example.clicker.network.repository.util.handleNetworkNewUserExceptions
import com.example.clicker.presentation.home.StreamInfo
import com.example.clicker.util.LogWrap
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.NetworkNewUserResponse
import com.example.clicker.util.Response
import com.example.clicker.util.logCoroutineInfo
import java.net.UnknownHostException
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class TwitchRepoImpl @Inject constructor(
    private val twitchClient: TwitchHomeClient,
) : TwitchRepo  {

     override suspend fun getFollowedLiveStreams(
        authorizationToken: String,
        clientId: String,
        userId: String
    ): Flow<NetworkNewUserResponse<List<StreamData>>> = flow {
        emit(NetworkNewUserResponse.Loading)


        val response = twitchClient.getFollowedStreams(
            authorization = "Bearer $authorizationToken",
            clientId = clientId,
            userId = userId
        )
        Log.d("TwitchRepoImpl","getFollowedLiveStreams code -->${response.code()}")

        val emptyBody = FollowedLiveStreams(listOf<StreamData>())
        val body = response.body() ?: emptyBody


        if (response.isSuccessful) {
            emit(NetworkNewUserResponse.Success(body.data))
        } else {
            emit(NetworkNewUserResponse.Failure(Exception("Error!, Please try again")))
        }
    }.catch { cause ->
         handleNetworkNewUserExceptions(cause)
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
        Log.d("TwitchRepoImpl","getModeratedChannels code -->${response.code()}")
        val body = response.body() ?: emptyBody

        if (response.isSuccessful) {
            emit(NetworkAuthResponse.Success(body))
        } else {
            emit(NetworkAuthResponse.Failure(Exception("Error!, Please try again")))
        }
    }.catch { cause ->
        handleNetworkAuthExceptions(cause)
    }

    //todo: this needs to called after the get all streams API call
    override suspend fun getAllFollowedStreamers(
        authorizationToken: String,
        clientId: String,
        userId: String
    ): Flow<Response<AllFollowedStreamers>>  = flow{
        val response = twitchClient.getAllFollowedStreamers(

            authorization = "Bearer $authorizationToken",
            clientId = clientId,
            userId = userId
        )
        val body = response.body() ?:  AllFollowedStreamers(0, listOf())

        if (response.isSuccessful) {
            Log.d("getAllFollowedStreamers","SUCCESS")
            emit(Response.Success(body))
        } else {
            Log.d("getAllFollowedStreamers","FAILED")
            Log.d("getAllFollowedStreamers","code = ${response.code()}")
            Log.d("getAllFollowedStreamers","message = ${response.message()}")
            emit(Response.Failure(Exception("Error!, Please try again")))
        }
    }.catch { cause ->
        emit(Response.Failure(Exception("Error!, Please try again")))
    }
}