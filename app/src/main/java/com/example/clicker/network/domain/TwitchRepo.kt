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

    suspend fun getFollowedLiveStreams(
        authorizationToken:String,
        clientId:String,
        userId:String
    ):Flow<Response<List<StreamInfo>>>




}