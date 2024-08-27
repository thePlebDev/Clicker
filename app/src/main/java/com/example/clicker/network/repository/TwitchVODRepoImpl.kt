package com.example.clicker.network.repository

import android.util.Log
import com.example.clicker.network.clients.TwitchVODClient
import com.example.clicker.network.clients.VODData
import com.example.clicker.network.domain.TwitchVODRepo
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TwitchVODRepoImpl @Inject constructor(
    val twitchVODClient: TwitchVODClient
): TwitchVODRepo {





    override suspend fun getChannelVODs(
        oAuthToken: String,
        clientId: String,
        userId: String
    ): Flow<Response<VODData>> = flow<Response<VODData>> {

        val response = twitchVODClient.getChannelVODs(
            authorization = "Bearer $oAuthToken",
            clientId = clientId,
            userId = userId
        )
        if (response.isSuccessful) {
            val body = response.body()
            Log.d("getChannelVODs","SUCCESS")
            Log.d("getChannelVODs","list -> $body")

            emit(Response.Success(body!!))
        } else {
            Log.d("getChannelVODs","FAILED")
            Log.d("getChannelVODs","code -> ${response.code()}")
            emit(Response.Failure(Exception("Error! Please try again")))
        }
    }.catch {
        emit(Response.Failure(Exception("Error! Please try again")))
    }
}