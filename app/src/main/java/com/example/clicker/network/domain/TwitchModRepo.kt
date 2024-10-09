package com.example.clicker.network.domain

import com.example.clicker.network.clients.UnbanRequestItem
import com.example.clicker.network.clients.UserSubscriptionData
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.network.repository.ClickedUnbanRequestInfo
import com.example.clicker.util.NetworkNewUserResponse
import com.example.clicker.util.Response
import com.example.clicker.util.UnAuthorizedResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Header
import retrofit2.http.Query

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


    /**
     * - getUnbanRequests a function meant to be called to get all available unban requests and their status
     *
     * @param authorizationToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param moderatorID a String used to represent the unique identifier of the user making the getUnbanRequests() function call
     * @param broadcasterId a String used to represent the unique identifier of the broadcaster the viewer is watching
     * */
    suspend fun getUnbanRequests(
        authorizationToken: String,
        clientId: String,
        broadcasterId: String,
        moderatorID: String,
        status: UnbanStatusFilter
    ): Flow<UnAuthorizedResponse<List<UnbanRequestItem>>>

    /**
     * - approveUnbanRequests a function meant to approve a user's unban request
     *
     * @param authorizationToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param moderatorID a String used to represent the unique identifier of the user making the approveUnbanRequests() function call
     * @param broadcasterId a String used to represent the unique identifier of the broadcaster the viewer is watching
     * @param unbanRequestId a String used to represent the unban request that is meant to be approved
     * */
    suspend fun approveUnbanRequests(
        authorizationToken: String,
        clientId: String,
        broadcasterId: String,
        moderatorID: String,
        status: UnbanStatusFilter,
        unbanRequestId:String,
        resolutionText:String,
    ): Flow<UnAuthorizedResponse<Boolean>>
}


/**
 * UnbanStatusFilter is a object meant to represent the filter used to tell the Twitch servers which types of unban request is
 * needed
 *
 * - [official unban request endpoint documentation](https://dev.twitch.tv/docs/api/reference/#get-unban-requests)
 * */
enum class UnbanStatusFilter {
    PENDING{
        override fun toString(): String {
            return "pending"
        }
    },
    APPROVED{
        override fun toString(): String {
            return "approved"
        }
    },
    DENIED{
        override fun toString(): String {
            return "denied"
        }
    },
    ACKNOWLEDGED{
        override fun toString(): String {
            return "acknowledged"
        }
    },
    CANCELED{
        override fun toString(): String {
            return "canceled"
        }
    },
}