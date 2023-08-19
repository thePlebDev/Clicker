package com.example.clicker.network.domain

import com.example.clicker.network.models.FollowedLiveStreams
import com.example.clicker.network.models.ValidatedUser
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow

interface TwitchRepo {
    suspend fun validateToken(token:String): Flow<Response<ValidatedUser>>

    suspend fun getFollowedLiveStreams(
        authorizationToken:String,
        clientId:String,
        userId:String
    ):Flow<Response<FollowedLiveStreams>>

    fun logout(clientId:String,token:String):Flow<Response<Boolean>>


}