package com.example.clicker.network.domain

import com.example.clicker.presentation.home.StreamInfo
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow

interface TwitchRepo {

    suspend fun getFollowedLiveStreams(
        authorizationToken: String,
        clientId: String,
        userId: String
    ): Flow<Response<List<StreamInfo>>>
}