package com.example.clicker.network.domain

import androidx.compose.runtime.State
import com.example.clicker.network.repository.EmoteListMap
import com.example.clicker.network.repository.EmoteNameUrl
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow

interface TwitchEmoteRepo {
    /**
     * emoteList is what eventually gets passed to the composable
     * */
    val emoteList: State<EmoteListMap>

    val emoteBoardGlobalList: List<EmoteNameUrl>

    /**
     * getGlobalEmotes
     * */
    fun getGlobalEmotes(
        oAuthToken: String,
        clientId: String,
    ): Flow<Response<Boolean>>
}