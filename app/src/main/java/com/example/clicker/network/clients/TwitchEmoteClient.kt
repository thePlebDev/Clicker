package com.example.clicker.network.clients

import com.example.clicker.network.models.emotes.ChannelEmoteResponse
import com.example.clicker.network.models.emotes.EmoteData
import com.example.clicker.network.models.emotes.GlobalChatBadgesData
import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.network.models.twitchStream.ChatSettings
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * TwitchEmoteClient is the interface that Retrofit will use and turn into a HTTP client. Specifically, this interface
 * is meant to interact with the Twitch API Emote servers
 *
 * @property getGlobalEmotes a function, when called, will get the Twitch global emotes
 * @property getChannelEmotes a function, when called, will get the channel specific Twitch emotes
 * @property getGlobalChatBadges a function, when called, will get the Twitch global subscriber badges
 * */
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



