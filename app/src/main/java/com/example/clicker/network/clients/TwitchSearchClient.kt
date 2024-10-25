package com.example.clicker.network.clients

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface TwitchSearchClient {



    /**
     * - getTopGames represents a GET method. A function meant to get the top games on the Twitch website
     * - you can read more on the official documentation, [HERE](https://dev.twitch.tv/docs/api/reference/#get-top-games)
     *
     * @param authorization a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param after a String used to represent the id needed to make a pagination request. This is necessary for a unlimited
     *  scrolling feature
     * */
    @GET("games/top")
    suspend fun getTopGames(
        @Header("Authorization") authorization: String,
        @Header("Client-Id") clientId: String,
        @Query("after") after: String
    ): Response<TopGameResponse>

    /**
     * - getGameInfo represents a GET method. A function meant to get info on one specific games
     * - you can read more on the official documentation, [HERE](https://dev.twitch.tv/docs/api/reference/#get-games)
     *
     * @param authorization a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param id a String used to represent the unique id of the game we want to get information about
     *
     * */
    @GET("games")
    suspend fun getGameInfo(
        @Header("Authorization") authorization: String,
        @Header("Client-Id") clientId: String,
        @Query("id") id: String
    ): Response<GameInfoResponse>

    /**
     * - getStreams represents a GET method. A function meant to get all the live streams
     * - you can read more on the official documentation, [HERE](https://dev.twitch.tv/docs/api/reference/#get-streams)
     *
     * @param authorization a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param gameId a String used to represent the unique id of the game we want to get information about
     * @param type  a String used to represent the type of streams we want. The two options being, live or all
     * @param language a String used to represent the language of the streams we want
     * @param after a String used to represent the pagination id and allow endless scrolling
     *
     * */
    @GET("streams")
    suspend fun getStreams(
        @Header("Authorization") authorization: String,
        @Header("Client-Id") clientId: String,
        @Query("game_id") gameId: String,
        @Query("type") type: String,
        @Query("language") language: String,
        @Query("after") after: String,
    ): Response<GetStreamResponseData>




}

data class TopGameResponse(
    val data: List<TopGame>,
    val pagination: Pagination
)

data class TopGame(
    val id: String,
    val name: String,
    val box_art_url: String,
    val igdb_id: String,
    val clicked:Boolean = false
)

data class Pagination(
    val cursor: String
)


data class GameInfoSearch(
    val id: String,
    val name: String,
    val boxArtUrl: String,
    val igdbId: String
)

data class GameInfoResponse(
    val data: List<Game>
)

data class GetStreamResponseData(
    val data: List<SearchStreamData>,
    val pagination: Pagination
)

data class SearchStreamData(
    val id: String,
    val user_id: String,
    val user_login: String,
    val user_name: String,
    val game_id: String,
    val game_name: String,
    val type: String,
    val title: String,
    val tags: List<String>,
    val viewer_count: Int,
    val started_at: String,
    val language: String,
    val thumbnail_url: String,
    val tag_ids: List<String>,
    val is_mature: Boolean
)

