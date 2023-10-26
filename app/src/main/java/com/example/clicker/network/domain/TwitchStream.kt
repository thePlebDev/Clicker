package com.example.clicker.network.domain

import com.example.clicker.network.BanUser
import com.example.clicker.network.BanUserResponse
import com.example.clicker.network.models.ChatSettings
import com.example.clicker.network.models.UpdateChatSettings
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow

interface TwitchStream {



    suspend fun getChatSettings(oAuthToken:String,clientId: String,broadcasterId:String): Flow<Response<ChatSettings>>

    suspend fun updateChatSettings(
        oAuthToken:String,
        clientId: String,
        broadcasterId:String,
        moderatorId:String,
        body: UpdateChatSettings
    ): Flow<Response<Boolean>>

    suspend fun deleteChatMessage(
        oAuthToken:String,
        clientId: String,
        broadcasterId:String,
        moderatorId:String,
        messageId:String,

        ): Flow<Response<Boolean>>

    suspend fun banUser(
        oAuthToken:String,
        clientId: String,
        broadcasterId:String,
        moderatorId:String,
        body: BanUser
    ): Flow<Response<BanUserResponse>>

    suspend fun unBanUser(
        oAuthToken:String,
        clientId: String,
        broadcasterId:String,
        moderatorId:String,
        userId: String

    ): Flow<Response<Boolean>>
}