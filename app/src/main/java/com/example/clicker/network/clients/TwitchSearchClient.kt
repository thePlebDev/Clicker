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
     *  scrolling feature
     * */
    @GET("games")
    suspend fun getGameInfo(
        @Header("Authorization") authorization: String,
        @Header("Client-Id") clientId: String,
        @Query("id") id: String
    ): Response<GameInfoResponse>




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
