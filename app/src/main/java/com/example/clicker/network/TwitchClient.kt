package com.example.clicker.network

import com.example.clicker.network.domain.AutoModSettings
import com.example.clicker.network.domain.IndividualAutoModSettings
import com.example.clicker.network.models.ChatSettings
import com.example.clicker.network.models.ChatSettingsResponse
import com.example.clicker.network.models.FollowedLiveStreams
import com.example.clicker.network.models.UpdateChatSettings
import com.example.clicker.network.models.ValidatedUser
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import retrofit2.http.Url


/**
 * TwitchClient is the interface that Retrofit will use and turn into a HTTP client. Specifically, this interface
 * is meant to interact with the Twitch API servers
 *
 * @property validateToken a function meant to validate a token with the Twitch servers
 * @property logout a function meant to end the users logged in session
 * @property getFollowedStreams a function meant to get all of the user's live followed streams
 * @property getChatSettings a function meant to get the chat settings of the stream currently views
 * @property updateChatSettings a function meant to update the currently viewed chat settings
 * @property deleteChatMessage a function meant to delete a specific chat message
 * @property banUser a function meant to ban a specific user
 * @property unBanUser a function meant to unban a specific user
 * @property getAutoModSettings a function meant to get the AutoMod settings of the currently viewed stream
 * @property updateAutoModSettings a function meant to update the AutoMod settings of the currently viewed stream
 * */
interface TwitchClient {

    // TODO:NEED TO DO SPECIAL TESTING WITH A INTERCEPTOR FOR THE METHODS WITH THE DYNAMIC URLS
    // TODO: THIS WILL DO DONE WHEN I ADD A NETWORK MONITOR INTERCEPTOR
    /**
     * - represented as a GET method. This function is used to verify the validity of the [authorization] token with
     * the Twitch servers
     *
     * @param url a String used to represent a dynamic URL.
     * @param authorization a String used to represent the OAuth token that is being sent to be validated
     * */
    @GET
    suspend fun validateToken(
        @Url url: String = "https://id.twitch.tv/oauth2/validate",
        @Header("Authorization") authorization: String
    ): Response<ValidatedUser>

    /**
     * - logout represents a POST method. This function meant to end the users logged in session with the Twitch server
     *
     * @param url a String used to represent a dynamic URL of where this request is being sent.
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param token a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * */
    @Headers("Content-Typ: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    suspend fun logout(
        @Url url: String = "https://id.twitch.tv/oauth2/revoke",
        @Field("client_id") clientId: String,
        @Field("token") token: String
    ): Response<Void>

    /**
     * - getFollowedStreams represents a GET method. a function meant to get all of the user's live followed streams
     *
     * @param authorization a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param userId a String used to represent the OAuth token that uniquely identifies this user
     * */
    @GET("streams/followed")
    suspend fun getFollowedStreams(
        @Header("Authorization") authorization: String,
        @Header("Client-Id") clientId: String,
        @Query("user_id") userId: String
    ): Response<FollowedLiveStreams>

    /**
     * - getChatSettings represents a GET method. A function meant to get the chat settings of the stream currently views
     *
     * @param authorization a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param broadcasterId a String used to represent the unique identifier of the streamer being currently viewed
     * */
    @GET("chat/settings")
    suspend fun getChatSettings(
        @Header("Authorization") authorization: String,
        @Header("Client-Id") clientId: String,
        @Query("broadcaster_id") broadcasterId: String
    ): Response<ChatSettings>


    /**
     * - updateChatSettings represents a PATCH method. a function meant to update the currently viewed chat settings
     *
     * @param authorizationToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param broadcasterId a String used to represent the unique identifier of the streamer being currently viewed
     * @param moderatorId A String used to represent the unique identifier of the current user and their moderator abilities
     * @param body a [UpdateChatSettings] object that represents the new settings
     * */
    @Headers("Content-Type: application/json")
    @PATCH("chat/settings")
    suspend fun updateChatSettings(
        @Header("Authorization") authorizationToken: String,
        @Header("Client-Id") clientId: String,
        @Query("broadcaster_id") broadcasterId: String,
        @Query("moderator_id") moderatorId: String,
        @Body body: UpdateChatSettings

    ): Response<ChatSettingsResponse>

    /**
     * - deleteChatMessage represents a DELETE method. a function meant to delete a specific chat message
     *
     * @param authorizationToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param broadcasterId a String used to represent the unique identifier of the streamer being currently viewed
     * @param moderatorId A String used to represent the unique identifier of the current user and their moderator abilities
     * @param messageId a String used to represent the unique identifier of the message to be deleted
     * */
    @DELETE("moderation/chat")
    suspend fun deleteChatMessage(
        @Header("Authorization") authorizationToken: String,
        @Header("Client-Id") clientId: String,
        @Query("broadcaster_id") broadcasterId: String,
        @Query("moderator_id") moderatorId: String,
        @Query("message_id") messageId: String
    ): Response<Void>

    /**
     * - banUser represents a POST method. a function meant to ban a specific user
     *
     * @param authorizationToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param broadcasterId a String used to represent the unique identifier of the streamer being currently viewed
     * @param moderatorId A String used to represent the unique identifier of the current user and their moderator abilities
     * @param body a [BanUser] object meant to represent details of the ban and the user to be banned
     * */
    @Headers("Content-Type: application/json")
    @POST("moderation/bans")
    suspend fun banUser(
        @Header("Authorization") authorizationToken: String,
        @Header("Client-Id") clientId: String,
        @Query("broadcaster_id") broadcasterId: String,
        @Query("moderator_id") moderatorId: String,
        @Body body: BanUser
    ): Response<BanUserResponse>

    /**
     * - unBanUser represents a POST method. a function meant to unban a specific user
     *
     * @param authorizationToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param broadcasterId a String used to represent the unique identifier of the streamer being currently viewed
     * @param moderatorId A String used to represent the unique identifier of the current user and their moderator abilities
     * @param userId a String representing the Id of the user to be unbanned with this method call
     * */
    @DELETE("moderation/bans")
    suspend fun unBanUser(
        @Header("Authorization") authorizationToken: String,
        @Header("Client-Id") clientId: String,
        @Query("broadcaster_id") broadcasterId: String,
        @Query("moderator_id") moderatorId: String,
        @Query("user_id") userId: String
    ): Response<Void>


    /**
     * - getAutoModSettings represents a GET method. a function meant to get the AutoMod settings of the currently viewed stream
     *
     * @param authorizationToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param broadcasterId a String used to represent the unique identifier of the streamer being currently viewed
     * @param moderatorId A String used to represent the unique identifier of the current user and their moderator abilities
     * */
    @GET("moderation/automod/settings")
    suspend fun getAutoModSettings(
        @Header("Authorization") authorizationToken: String,
        @Header("Client-Id") clientId: String,
        @Query("broadcaster_id") broadcasterId: String,
        @Query("moderator_id") moderatorId: String,
    ):Response<AutoModSettings>


    /**
     * - updateAutoModSettings represents a PUT method. a function meant to update the AutoMod settings of the currently viewed stream
     *
     * @param authorizationToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param autoModSettings A [AutoModSettings] object used to represent the new updated AutoMod settings for the channel
     * */
    @Headers("Content-Type: application/json")
    @PUT("moderation/automod/settings")
    suspend fun updateAutoModSettings(
        @Header("Authorization") authorizationToken: String,
        @Header("Client-Id") clientId: String,
        @Body autoModSettings: IndividualAutoModSettings
    ):Response<AutoModSettings>

}

data class BanUser(
    val data: BanUserData
)

/**
 * BanUserData represents all of the data necessary to ban a user from chat
 *
 * @param user_id the unique identifier of this user
 * @param reason The reason a user was banned
 * @param duration a integer used to represent the length of the users ban
 * */
data class BanUserData(
    val user_id: String,
    val reason: String,
    val duration: Int? = null
)

data class BanUserResponse(
    val data: List<BanUserResponseData>
)

data class BanUserResponseData(
    val broadcaster_id: String,
    val moderator_id: String,
    val user_id: String,
    val created_at: String,
    val end_time: String? // Note that end_time can be null
)
