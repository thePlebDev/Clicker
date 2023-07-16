package com.example.clicker.network.repository

import com.example.clicker.network.TwitchClient
import com.example.clicker.network.TwitchRetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TwitchRepoImpl(
    private val twitchClient: TwitchClient = TwitchRetrofitInstance.api
) {

    suspend fun validateToken(token:String):Flow<Boolean> = flow{
       val response= twitchClient.validateToken(
            authorization = "OAuth $token"
        ).isSuccessful
        emit(response)
    }
}