package com.example.clicker.network.clients

import com.example.clicker.network.models.twitchStream.ChatSettings
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Query

interface TwitchStreamInfoClient {

    /**
     * - getChannelInformation represents a GET method. A function meant to get the chat settings of the stream currently views
     *
     * @param authorization a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param broadcasterId a String used to represent the unique identifier of the streamer being currently viewed
     * */
    @GET("channels")
    suspend fun getChannelInformation(
        @Header("Authorization") authorization: String,
        @Header("Client-Id") clientId: String,
        @Query("broadcaster_id") broadcasterId: String
    ): Response<ChannelInformation>

    /**
     * - getChannelInformation represents a GET method. A function meant to get the chat settings of the stream currently views
     *
     * @param authorization a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param gameName a String used to represent the unique identifier of game we want to get more information about
     * */
    @GET("search/categories")
    suspend fun getCategories(
        @Header("Authorization") authorization: String,
        @Header("Client-Id") clientId: String,
        @Query("query") gameName: String
    ): Response<GameInfo>

    /**
     * - getChannelInformation represents a GET method. A function meant to get the chat settings of the stream currently views
     *
     * @param authorization a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param broadcasterId a String used to represent the unique identifier of the streamer being currently viewed
     * */
    @PATCH("channels")
    suspend fun modifyChannelInformation(
        @Header("Authorization") authorization: String,
        @Header("Client-Id") clientId: String,
        @Query("broadcaster_id") broadcasterId: String,
        @Body body: GameRequest
    ): Response<ChannelInformation>


}

data class ContentClassificationLabel(
    val id: String,
    val is_enabled: Boolean
)

data class GameRequest(
    val game_id: String,
    val content_classification_labels: List<ContentClassificationLabel>,
    val is_branded_content: Boolean,
    val broadcaster_language:String,
    val title:String,
    val tags: List<String>,
)

data class ChannelInformation(
    val data: List<ChannelInfo>
)

data class ChannelInfo(
    val broadcaster_id: String,
    val broadcaster_login: String,
    val broadcaster_name: String,
    val broadcaster_language: String,
    val game_id: String,
    val game_name: String,
    val title: String,
    val delay: Int,
    val tags: List<String>,
    val content_classification_labels: List<String>,
    val is_branded_content: Boolean
)

data class GameInfo(
    val data: List<Game>
)

data class Game(
    val id: String,
    val name: String,
    val box_art_url: String
)