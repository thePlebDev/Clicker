package com.example.clicker.network.domain

import com.example.clicker.network.clients.ChannelInfo
import com.example.clicker.network.clients.Game
import com.example.clicker.network.clients.GameInfo
import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.NetworkNewUserResponse
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow

interface StreamInfoRepo {

    /**
     * validateToken is a function that is called to validate [token] with the Twitch servers
     *
     * @param token a String representing a oAuth token
     *
     * @return a flow containing a [NetworkAuthResponse] object of type [ValidatedUser]
     * */
    suspend fun getChannelInformation(
        authorizationToken: String,
        clientId: String,
        broadcasterId: String
    ): Flow<Response<ChannelInfo>>

    /**
     * validateToken is a function that is called to validate [token] with the Twitch servers
     *
     * @param token a String representing a oAuth token
     *
     * @return a flow containing a [NetworkAuthResponse] object of type [ValidatedUser]
     * */
    suspend fun getCategoryInformation(
        authorizationToken: String,
        clientId: String,
        gameName: String
    ): Flow<Response<Game>>
}


