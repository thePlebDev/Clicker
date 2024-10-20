package com.example.clicker.network.clients

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface TwitchSearchClient {



    /**
     * - getTopGames represents a GET method. A function meant to get the chat settings of the stream currently views
     *
     * @param authorization a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * */
    @GET("games/top")
    suspend fun getTopGames(
        @Header("Authorization") authorization: String,
        @Header("Client-Id") clientId: String,
    ): Response<TopGameResponse>
}

data class TopGameResponse(
    val data: List<TopGame>,
)

data class TopGame(
    val id: String,
    val name: String,
    val box_art_url: String,
    val igdb_id: String
)
