package com.example.clicker.network.domain

import com.example.clicker.network.clients.Game
import com.example.clicker.network.clients.SearchStreamData
import com.example.clicker.network.clients.TopGame
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.util.NetworkNewUserResponse
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.http.Header
import retrofit2.http.Query


/**
 * TwitchSearch is the interface representing all of the functionality the user has when on the application's `search page`
 *
 * @property mostRecentPaginationRequestId a variable that is used as an event bus to handle when a new pagination id
 * is returned from [getTopGames]
 * @property getTopGames a function meant to get a list of all the top game categories on Twitch
 * @property getGameInfo a function meant to get info on a specific game
 * */
interface TwitchSearch {

    /**
     * a [StateFlow] variable that is used as an event bus to handle when a new pagination id is returned from [getTopGames].
     * This is needed for the unlimited scrolling feature
     * */
    val mostRecentPaginationRequestId: StateFlow<String?>

    val mostRecentStreamModalPaginationRequestId: StateFlow<String?>


    /**
     * - getTopGames represents a GET method. A function meant to get a list of all the tops games on Twitch
     *
     * @param authorizationToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param after a String used to represent the pagination id and is passed to [mostRecentPaginationRequestId]
     * */
    suspend fun getTopGames(
        authorizationToken: String,
        clientId: String,
        after:String

    ): Flow<Response<List<TopGame>>>

    /**
     * - getGameInfo represents a GET method. A function meant to get the chat settings of the stream currently views
     *
     * @param authorizationToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param id a String used to represent the unique id of the game we are requesting information about
     * */
    suspend fun getGameInfo(
        authorizationToken: String,
        clientId: String,
        id:String
    ): Flow<Response<Game?>>

    /**
     * - getGameInfo represents a GET method. A function meant to get the chat settings of the stream currently views
     *
     * @param authorizationToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param id a String used to represent the unique id of the game we are requesting information about
     * */
    suspend fun getStreams(
        authorization: String,
        clientId: String,
        gameId: String,
        type: StreamType,
       language: String,
        after: String,
    ): Flow<Response<List<SearchStreamData>>>


}

/**
 * an enum used to represent the
 *
 * */
enum class StreamType {
    All{
        override fun toString(): String {
            return "all"
        }
    },
    LIVE{
        override fun toString(): String {
            return "live"
        }
    }
}