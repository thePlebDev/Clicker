package com.example.clicker.network.clients

import com.example.clicker.network.models.twitchStream.ChatSettingsResponse
import com.example.clicker.network.models.twitchStream.UpdateChatSettings
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.Query

interface TwitchModClient {

    /**
     * - updateChatSettings represents a GET method. a function meant to update the currently viewed chat settings
     *
     * @param authorizationToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param userId a String used to represent the unique identifier of the user we want to get the information on
     * */
    @GET("users")
    suspend fun getUserInformation(
        @Header("Authorization") authorizationToken: String,
        @Header("Client-Id") clientId: String,
        @Query("id") userId: String,

    ): Response<UserDataResponse>

    /**
     * - updateChatSettings represents a GET method. a function meant to update the currently viewed chat settings
     *
     * @param authorizationToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param userId a String used to represent the unique identifier of the user we want to get the information on
     * */
    @GET("subscriptions")
    suspend fun checkUserSubscriptionStatus(
        @Header("Authorization") authorizationToken: String,
        @Header("Client-Id") clientId: String,
        @Query("broadcaster_id") broadcasterId: String,
        @Query("user_id") userId: String,
        ): Response<UserSubscription>

    /**
     * - getUnbanRequests represents a GET method. a function meant to get a broadcasters unban request
     * - [Official documentation on getting unban requests](https://dev.twitch.tv/docs/api/reference/#get-unban-requests)
     *
     * @param authorizationToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param broadcasterId a String used to represent the unique identifier of the broadcaster the user is watching
     * @param moderatorID a String used to represent the unique identifier of the user making the request
     * @param status a String used to represent the filter of what requests we want
     * */
    @GET("moderation/unban_requests")
    suspend fun getUnbanRequests(
        @Header("Authorization") authorizationToken: String,
        @Header("Client-Id") clientId: String,
        @Query("broadcaster_id") broadcasterId: String,
        @Query("moderator_id") moderatorID: String,
        @Query("status") status: String,
    ): Response<UnbanRequestData>

    /**
     * - getUnbanRequests represents a GET method. a function meant to get a broadcasters unban request
     * - [Official documentation on getting unban requests](https://dev.twitch.tv/docs/api/reference/#get-unban-requests)
     *
     * @param authorizationToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param broadcasterId a String used to represent the unique identifier of the broadcaster the user is watching
     * @param moderatorID a String used to represent the unique identifier of the user making the request
     * @param status a String used to represent the filter of what requests we want
     * */
    @PATCH("moderation/unban_requests")
    suspend fun approveUnbanRequest(
        @Header("Authorization") authorizationToken: String,
        @Header("Client-Id") clientId: String,
        @Query("broadcaster_id") broadcasterId: String,
        @Query("moderator_id") moderatorID: String,
        @Query("status") status: String,
        @Query("unban_request_id") unbanRequestID: String,
    ): Response<UnbanRequestData>





}

data class UserDataResponse(
    val data: List<UserData>
)

data class UserData(
    val id: String,
    val login: String,
    val display_name: String,
    val type: String,
    val broadcaster_type: String,
    val description: String,
    val profile_image_url: String,
    val offline_image_url: String,
    val view_count: Int,
    val created_at: String
)


data class UserSubscription(
    val data: List<UserSubscriptionData>
)

data class UserSubscriptionData(
    val broadcaster_id: String,
    val broadcaster_name: String,
    val broadcaster_login: String,
    val is_gift: Boolean,
    val tier: String
)

data class UnbanRequestData(
    val data: List<UnbanRequestItem>
)


data class UnbanRequestItem(
    val id: String,
    val broadcaster_name: String,
    val broadcaster_login: String,
    val broadcaster_id: String,
    val moderator_id: String?,
    val moderator_login: String?,
    val moderator_name: String?,
    val user_id: String,
    val user_login: String,
    val user_name: String,
    val text: String,
    val status: String,
    val created_at: String,
    val resolved_at: String?,
    val resolution_text: String?
)


