package com.example.clicker.network.repository

import android.util.Log
import com.example.clicker.network.clients.TwitchHomeClient
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.twitchClient.GetModChannels
import com.example.clicker.network.models.twitchRepo.FollowedLiveStreams
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.network.repository.util.handleNetworkAuthExceptions
import com.example.clicker.network.repository.util.handleNetworkNewUserExceptions
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.NetworkNewUserResponse
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class TwitchRepoImpl @Inject constructor(
    private val twitchHomeClient: TwitchHomeClient,
) : TwitchRepo  {

     override suspend fun getFollowedLiveStreams(
        authorizationToken: String,
        clientId: String,
        userId: String
    ): Flow<NetworkNewUserResponse<List<StreamData>>> = flow {
        emit(NetworkNewUserResponse.Loading)


        val response = twitchHomeClient.getFollowedStreams(
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
        val response = twitchHomeClient.getModeratedChannels(
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

}