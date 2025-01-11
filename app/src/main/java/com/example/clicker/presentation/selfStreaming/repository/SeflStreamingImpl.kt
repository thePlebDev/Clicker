package com.example.clicker.presentation.selfStreaming.repository

import com.example.clicker.network.clients.TwitchStreamInfoClient
import com.example.clicker.presentation.selfStreaming.clients.StreamToTwitchClient
import com.example.clicker.presentation.selfStreaming.domain.SelfStreaming
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SelfStreamingImpl @Inject constructor(
    private val streamToTwitch: StreamToTwitchClient
): SelfStreaming {



    override fun getStreamKey(oAuthToken: String, clientId: String): Flow<String> {
        TODO("Not yet implemented")
    }
}