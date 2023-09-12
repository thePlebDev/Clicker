package com.example.clicker.network.domain

import com.example.clicker.network.BanUser
import com.example.clicker.network.BanUserResponse
import com.example.clicker.network.models.ChatSettings
import com.example.clicker.network.models.FollowedLiveStreams
import com.example.clicker.network.models.UpdateChatSettings
import com.example.clicker.network.models.ValidatedUser
import com.example.clicker.presentation.home.StreamInfo
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Header
import retrofit2.http.Query

interface TwitchRepo {
    suspend fun validateToken(token:String): Flow<Response<ValidatedUser>>

    suspend fun getFollowedLiveStreams(
        authorizationToken:String,
        clientId:String,
        userId:String
    ):Flow<Response<List<StreamInfo>>>

    fun logout(clientId:String,token:String):Flow<Response<String>>

    suspend fun getChatSettings(oAuthToken:String,clientId: String,broadcasterId:String):Flow<Response<ChatSettings>>

    suspend fun updateChatSettings(
        oAuthToken:String,
        clientId: String,
        broadcasterId:String,
        moderatorId:String,
        body: UpdateChatSettings
    ):Flow<Response<Boolean>>

    suspend fun deleteChatMessage(
        oAuthToken:String,
        clientId: String,
        broadcasterId:String,
        moderatorId:String,
        messageId:String,

    ):Flow<Response<Boolean>>

    suspend fun banUser(
        oAuthToken:String,
        clientId: String,
        broadcasterId:String,
        moderatorId:String,
        body:BanUser
    ):Flow<Response<BanUserResponse>>



}