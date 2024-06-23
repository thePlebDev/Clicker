package com.example.clicker.network.domain

import com.example.clicker.network.clients.IndivBetterTTVEmote
import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.util.NetworkNewUserResponse
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow

interface BetterTTVEmotes {

    suspend fun getGlobalEmotes(): Flow<Response<List<IndivBetterTTVEmote>>>
}