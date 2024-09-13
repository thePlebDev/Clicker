package com.example.clicker.network.repository

import android.util.Log
import com.example.clicker.network.clients.ChannelInfo
import com.example.clicker.network.clients.TwitchStreamInfoClient
import com.example.clicker.network.domain.StreamInfoRepo
import com.example.clicker.network.repository.util.handleNetworkAuthExceptions
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StreamInfoRepoImpl @Inject constructor(
    private val twitchStreamInfoRepo: TwitchStreamInfoClient
): StreamInfoRepo {
    override suspend fun getChannelInformation(
        authorizationToken: String,
        clientId: String,
        broadcasterId: String
    ): Flow<Response<ChannelInfo>> = flow {
        emit(Response.Loading)

        val response = twitchStreamInfoRepo.getChannelInformation(
            authorization = "Bearer $authorizationToken",
            clientId = clientId,
            broadcasterId = broadcasterId
        )
        if(response.isSuccessful){

            val data = response.body()?.data?.get(0)
            Log.d("getChannelInformationInformation","SUCCESS")
            Log.d("getChannelInformationInformation","data -->$data")
            emit(Response.Success(data!!))
        }else{
            Log.d("getChannelInformationInformation","FAILED")
            Log.d("getChannelInformationInformation","message -->${response.code()}")
            Log.d("getChannelInformationInformation","message -->${response.message()}")
            Log.d("getChannelInformationInformation","message -->${response.errorBody()}")

        }

    }.catch { cause ->
        emit(Response.Failure(Exception("FAILED")))
    }
}