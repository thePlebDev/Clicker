package com.example.clicker.network.domain

import androidx.compose.runtime.State
import com.example.clicker.network.repository.EmoteListMap
import com.example.clicker.network.repository.EmoteNameUrl
import com.example.clicker.network.repository.EmoteNameUrlList
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow


/**
 * TwitchEmoteRepo is the interface that acts as the API for all the methods needed to interact with Twitch's emote servers
 *
 * @property emoteList a [State] object containing a map of all the emotes
 * @property emoteBoardGlobalList a [State] object containing a map of all the global
 * @property emoteBoardChannelList a [State] object containing a map of all the channel emotes
 *
 * @property getGlobalEmotes()
 * @property getChannelEmotes()
 *
 * */
interface TwitchEmoteRepo {

    val emoteList: State<EmoteListMap>

    val emoteBoardGlobalList: State<EmoteNameUrlList>

    val emoteBoardChannelList:State<EmoteNameUrlList>


    /**
     * getGlobalEmotes a function used to make a request to the Twitch servers to access the global emotes
     *
     * @param oAuthToken a String object used to represent the oAuth-Token granted from the Twitch servers to the user. Each
     * oAuth-Token represents a unique user experience
     * @param clientId a String object used to represent the id of the developer account
     *
     * @return a [Flow] object containing a [Response] object that is used to determine if the request was a success or not
     * */
    fun getGlobalEmotes(
        oAuthToken: String,
        clientId: String,
    ): Flow<Response<Boolean>>

    /**
     * getChannelEmotes a function used to make a request to the Twitch servers to access the channel specific emotes
     * @param oAuthToken a String object used to represent the oAuth-Token granted from the Twitch servers to the user. Each
     * oAuth-Token represents a unique user experience
     * @param clientId a String object used to represent the id of the developer account
     * @param broadcasterId a String object used to represent the id of the channel that we are requesting the emotes from
     *
     * @return a [Flow] object containing a [Response] object that is used to determine if the request was a success or not
     * */
    fun getChannelEmotes(
        oAuthToken: String,
        clientId: String,
        broadcasterId:String
    ): Flow<Response<Boolean>>
}