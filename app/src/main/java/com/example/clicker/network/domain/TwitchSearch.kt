package com.example.clicker.network.domain

import com.example.clicker.network.clients.TopGame
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.util.NetworkNewUserResponse
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow

interface TwitchSearch {

    suspend fun getTopGames(
        authorizationToken: String,
        clientId: String,

    ): Flow<Response<List<TopGame>>>
}