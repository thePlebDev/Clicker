package com.example.clicker.network.domain

import com.example.clicker.network.clients.UserSubscriptionData
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.network.repository.ClickedUnbanRequestInfo
import com.example.clicker.util.NetworkNewUserResponse
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow

interface TwitchModRepo {

    /**
     * - getUserInformation a function meant to be called to get all available information about a single user
     *
     * @param authorizationToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param userId a String used to represent the unique identifier of the user we want information on
     * */
    suspend fun getUserInformation(
        authorizationToken: String,
        clientId: String,
        userId: String
    ): Flow<Response<ClickedUnbanRequestInfo>>

    /**
     * - THIS ONLY WORKS FOR CHECKING CURRENT USERS ID
     * - getUserSubscriptionStatus a function meant to be called to get all available information about a single user's
     * subscription information
     *
     * @param authorizationToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param userId a String used to represent the unique identifier of the user we want information on
     * @param broadcasterId a String used to represent the unique identifier of the broadcaster the viewer is watching
     * */
    suspend fun getUserSubscriptionStatus(
        authorizationToken: String,
        clientId: String,
        userId: String,
        broadcasterId: String
    ): Flow<Response<UserSubscriptionData>>
}