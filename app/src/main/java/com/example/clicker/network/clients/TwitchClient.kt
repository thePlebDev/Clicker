package com.example.clicker.network.clients


import com.example.clicker.network.models.twitchStream.ChatSettings
import com.example.clicker.network.models.twitchStream.ChatSettingsResponse
import com.example.clicker.network.models.twitchRepo.FollowedLiveStreams
import com.example.clicker.network.models.twitchStream.UpdateChatSettings
import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.network.models.twitchStream.AutoModSettings
import com.example.clicker.network.models.twitchStream.BanUserResponse
import com.example.clicker.network.models.twitchStream.IndividualAutoModSettings
import com.google.gson.annotations.SerializedName
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
 * @property getChatSettings a function meant to get the chat settings of the stream currently views
 * @property updateChatSettings a function meant to update the currently viewed chat settings
 * @property deleteChatMessage a function meant to delete a specific chat message
 * @property banUser a function meant to ban a specific user
 * @property unBanUser a function meant to unban a specific user
 * @property getAutoModSettings a function meant to get the AutoMod settings of the currently viewed stream
 * @property updateAutoModSettings a function meant to update the AutoMod settings of the currently viewed stream
 * */
interface TwitchClient {


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

    /**
     * - updateAutoModSettings represents a PATCH method. a function meant to update the channel's information
     * - You can read more about this endpoint, [HERE](https://dev.twitch.tv/docs/api/reference/#modify-channel-information)
     *
     * @param authorizationToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param broadcasterId a String used to represent the unique identifier of the streamer being currently viewed
     * @param channelInformation a object of [ChannelInformation] that will be sent to the Twitch servers to update the channel's information
     * */
    @Headers("Content-Type: application/json")
    @PATCH("channels")
    suspend fun updateChannelInformation(
        @Header("Authorization") authorizationToken: String,
        @Header("Client-Id") clientId: String,
        @Query("broadcaster_id") broadcasterId: String,
        @Body channelInformation: ChannelInformation
    ):Response<Void>


    @Headers("Content-Type: application/json")
    @POST("eventsub/subscriptions")
    suspend fun createEventSubSubscription(
        @Header("Authorization") authorizationToken: String,
        @Header("Client-Id") clientId: String,
        @Query("broadcaster_id") broadcasterId: String,
        @Body evenSubSubscription: EvenSubSubscription
    ):Response<EvenSubSubscriptionResponse>

    @Headers("Content-Type: application/json")
    @POST("moderation/automod/message")
    suspend fun manageAutoModMessage(
        @Header("Authorization") authorizationToken: String,
        @Header("Client-Id") clientId: String,
        @Body manageAutoModMessageData: ManageAutoModMessage
    ):Response<Void>





}
/**
 * ManageAutoModMessage is a data class containing all the necessary data to send in a request body to the
 * [Manage Held AutoMod Messages](https://dev.twitch.tv/docs/api/reference/#manage-held-automod-messages) endpoint.
 *
 *
 * @param userId represents the id of the user whos message is being evaluated
 * @param msgId represents the id of the message that is being evaluated
 * @param action ALLOW or DENY are the only two allowed string values. This is the value that will be used to determine if the
 * message is meant to be approved or denied.
 */
data class ManageAutoModMessage(
    @SerializedName("user_id")
    val userId:String,
    @SerializedName("msg_id")
    val msgId:String,
    val action:String,
)
data class ChannelInformation(
    val title:String ="ti do be like that sometimes",
    @SerializedName("is_enabled")
    val isLabelEnabled: Boolean = false,
    val id:String = "ProfanityVulgarity",
    @SerializedName("broadcaster_language")
    val preferredLanguage: String = "ko",
    @SerializedName("game_id")
    val gameId: String = "0",


)
data class GetModChannels(
    val data:List<GetModChannelsData>
)
data class GetModChannelsData(

    @SerializedName("broadcaster_id")
    val broadcasterId: String,
    @SerializedName("broadcaster_login")
    val broadcasterLogin: String,
    @SerializedName("broadcaster_name")
    val broadcasterName: String,
)

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


data class EvenSubSubscription(
    val type: String,
    val version: String,
    val condition: Condition,
    val transport: Transport
)

data class Condition(
    val broadcaster_user_id: String,
    val moderator_user_id:String,
)

data class Transport(
    val method: String ="websocket",
    val session_id: String
)
// This i

data class EvenSubSubscriptionResponse(
    val data: List<UserUpdateItem>,
    val total: Int,
    val total_cost: Int,
    val max_total_cost: Int
)

data class UserUpdateItem(
    val id: String,
    val status: String,
    val type: String,
    val version: String,
    val condition: Condition,
    val created_at: String,
    val transport: Transport,
    val cost: Int
)

