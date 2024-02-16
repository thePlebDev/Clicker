package com.example.clicker.network.domain

import com.example.clicker.network.clients.GetModChannels
import com.example.clicker.presentation.home.StreamInfo
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow

/**
 * TwitchRepo is the interface that acts as the API for all the methods needed to interact with Twitch's base API. So any
 * end point that is not related to a individual users stream
 *
 * @property getFollowedLiveStreams a function meant to be called to get the logged in user's live channels
 * */
interface TwitchRepo {

    /**
     * - getFollowedLiveStreams a function meant to be called to get the logged in user's live channels
     *
     * @param authorizationToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param userId a String used to represent the unique identifier of the current logged in user
     * */
    suspend fun getFollowedLiveStreams(
        authorizationToken: String,
        clientId: String,
        userId: String
    ): Flow<NetworkAuthResponse<List<StreamInfo>>>

    /**
     * - getModeratedChannels() Gets a list of channels that the specified user has moderator privileges in.
     *
     * @param authorizationToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param userId a String used to represent the unique identifier of the current logged in user
     * */
    suspend fun getModeratedChannels(
        authorizationToken: String,
        clientId: String,
        userId: String
    ):Flow<NetworkAuthResponse<GetModChannels>>

}