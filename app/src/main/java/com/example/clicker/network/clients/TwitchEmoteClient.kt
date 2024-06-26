package com.example.clicker.network.clients

import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.network.models.twitchStream.ChatSettings
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface TwitchEmoteClient {

    /**
     * - represented as a GET method. This function is used to get the available Global Twitch Emotes
     *
     * @param authorization a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * */
    @GET("chat/emotes/global")
    suspend fun getGlobalEmotes(
        @Header("Authorization") authorization: String,
        @Header("Client-Id") clientId: String,
    ): Response<EmoteData>

    @GET("chat/emotes")
    suspend fun getChannelEmotes(
        @Header("Authorization") authorization: String,
        @Header("Client-Id") clientId: String,
        @Query("broadcaster_id") broadcasterId: String
    ): Response<ChannelEmoteResponse>

    /**
     * getGlobalChatBadges is a function meant to get the all the global chat badges that Twitch has available. You can read more about
     * getting global chat badges [HERE](https://dev.twitch.tv/docs/api/reference/#get-global-chat-badges)
     * */
    @GET("chat/badges/global")
    suspend fun getGlobalChatBadges(
        @Header("Authorization") authorization: String,
        @Header("Client-Id") clientId: String,
    ):Response<GlobalChatBadgesData>
}

data class GlobalChatBadgesData(
    val data: List<GlobalBadgesSet>
)

data class GlobalBadgesSet(
    val set_id: String,
    val versions: List<GlobalBadgesVersion>
)

data class GlobalBadgesVersion(
    val id: String,
    val image_url_1x: String,
    val image_url_2x: String,
    val image_url_4x: String,
    val title: String,
    val description: String,
    val click_action: String,
    val click_url: String
)




//below
data class Emote(
    val id: String,
    val name: String,
    val images: Images,
    val format: List<String>,
    val scale: List<String>,
    val theme_mode: List<String>
)

data class Images(
    val url_1x: String,
    val url_2x: String,
    val url_4x: String
)

data class EmoteData(
    val data: List<Emote>
)


data class ChannelEmoteResponse(
    val data: List<ChannelEmote>
)

data class ChannelEmote(
    val id: String,
    val name: String,
    val images: ChannelImages,
    val format: List<String>,
    val scale: List<String>,
    val theme_mode: List<String>,
    val emote_type:String
)

data class ChannelImages(
    val url_1x: String,
    val url_2x: String,
    val url_4x: String
)

