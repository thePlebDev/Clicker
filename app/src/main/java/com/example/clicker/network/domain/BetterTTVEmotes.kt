package com.example.clicker.network.domain


import com.example.clicker.network.models.emotes.IndivBetterTTVEmote

import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow

interface BetterTTVEmotes {

    suspend fun getBetterTTVGlobalEmotes(): Flow<Response<List<IndivBetterTTVEmote>>>
}