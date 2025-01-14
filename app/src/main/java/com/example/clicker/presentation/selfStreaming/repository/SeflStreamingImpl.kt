package com.example.clicker.presentation.selfStreaming.repository

import android.util.Log
import com.example.clicker.network.clients.TwitchStreamInfoClient
import com.example.clicker.presentation.selfStreaming.clients.StreamToTwitchClient
import com.example.clicker.presentation.selfStreaming.domain.SelfStreaming
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SelfStreamingImpl @Inject constructor(
    private val streamToTwitch: StreamToTwitchClient
): SelfStreaming {



    override fun getStreamKey(
        oAuthToken: String,
        clientId: String,
        broadcasterId:String,

    ): Flow<NetworkAuthResponse<String>> = flow{
        emit(NetworkAuthResponse.Loading)
        Log.d("getStreamKeyResponse","LOADING")

        val response = streamToTwitch.getStreamKey(
            authorization = "Bearer $oAuthToken",
            clientId = clientId,
            broadcasterId = broadcasterId

        )

        if (response.isSuccessful) {
            val streamKey = response.body()?.data?.get(0)?.stream_key ?:""
            Log.d("getStreamKeyResponse", "SUCCESS")
            Log.d("getStreamKeyResponse", "streamKey-->$streamKey")
            emit(NetworkAuthResponse.Success(streamKey))
        }
        else if(response.code() == 401){
            Log.d("getStreamKeyResponse", "401 FAIL")
            Log.d("getStreamKeyResponse", "message ->${response.message()}")
            emit(NetworkAuthResponse.Auth401Failure(Exception("Failed again")))
        }
        else {
            Log.d("getStreamKeyResponse", "FAILED")
            Log.d("getStreamKeyResponse", "${response.code()}")
            Log.d("getStreamKeyResponse", "${response.message()}")

            emit(NetworkAuthResponse.Failure(Exception("Error! Please try again")))
        }
    }.catch{cause ->
        Log.d("getStreamKeyResponse","EXCEPTION")
        Log.d("getStreamKeyResponse","message ->${cause.message}")
        Log.d("getStreamKeyResponse","cause ->${cause.cause}")
        Log.d("getStreamKeyResponse","cause ->${cause.javaClass}")

    }
}