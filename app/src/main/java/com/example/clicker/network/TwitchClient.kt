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

interface TwitchClient {

    @GET
    suspend fun validateToken(
        @Url url: String = "https://id.twitch.tv/oauth2/validate",
        @Header("Authorization") authorization: String
    ): Response<ValidatedUser>

    @GET("streams/followed")
    suspend fun getFollowedStreams(
        @Header("Authorization") authorization: String,
        @Header("Client-Id") clientId: String,
        @Query("user_id") userId: String
    ): Response<FollowedLiveStreams>

    @Headers("Content-Typ: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    suspend fun logout(
        @Url url: String = "https://id.twitch.tv/oauth2/revoke",
        @Field("client_id") clientId: String,
        @Field("token") token: String
    ): Response<Void>

    @GET("chat/settings")
    suspend fun getChatSettings(
        @Header("Authorization") authorization: String,
        @Header("Client-Id") clientId: String,
        @Query("broadcaster_id") broadcasterId: String
    ): Response<ChatSettings>

    @Headers("Content-Type: application/json")
    @PATCH("chat/settings")
    suspend fun updateChatSettings(
        @Header("Authorization") authorizationToken: String,
        @Header("Client-Id") clientId: String,
        @Query("broadcaster_id") broadcasterId: String,
        @Query("moderator_id") moderatorId: String,
        @Body body: UpdateChatSettings

    ): Response<ChatSettingsResponse>

    @DELETE("moderation/chat")
    suspend fun deleteChatMessage(
        @Header("Authorization") authorizationToken: String,
        @Header("Client-Id") clientId: String,
        @Query("broadcaster_id") broadcasterId: String,
        @Query("moderator_id") moderatorId: String,
        @Query("message_id") messageId: String
    ): Response<Void>

    @Headers("Content-Type: application/json")
    @POST("moderation/bans")
    suspend fun banUser(
        @Header("Authorization") authorizationToken: String,
        @Header("Client-Id") clientId: String,
        @Query("broadcaster_id") broadcasterId: String,
        @Query("moderator_id") moderatorId: String,
        @Body body: BanUser
    ): Response<BanUserResponse>

    @DELETE("moderation/bans")
    suspend fun unBanUser(
        @Header("Authorization") authorizationToken: String,
        @Header("Client-Id") clientId: String,
        @Query("broadcaster_id") broadcasterId: String,
        @Query("moderator_id") moderatorId: String,
        @Query("user_id") userId: String
    ): Response<Void>

    @GET("moderation/automod/settings")
    suspend fun getAutoModSettings(
        @Header("Authorization") authorizationToken: String,
        @Header("Client-Id") clientId: String,
        @Query("broadcaster_id") broadcasterId: String,
        @Query("moderator_id") moderatorId: String,
    ):Response<AutoModSettings>

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
