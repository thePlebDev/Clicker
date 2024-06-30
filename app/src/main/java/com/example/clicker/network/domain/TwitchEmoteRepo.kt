package com.example.clicker.network.domain

import androidx.compose.runtime.State
import com.example.clicker.network.clients.BetterTTVChannelEmotes
import com.example.clicker.network.clients.IndivBetterTTVEmote
import com.example.clicker.network.repository.EmoteListMap
import com.example.clicker.network.repository.EmoteNameUrl
import com.example.clicker.network.repository.EmoteNameUrlEmoteTypeList
import com.example.clicker.network.repository.EmoteNameUrlList
import com.example.clicker.network.repository.IndivBetterTTVEmoteList
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow


/**
 * TwitchEmoteRepo is the interface that acts as the API for all the methods needed to interact with Twitch's emote servers
 *
 * @property emoteList a [State] object containing a map of all the emotes.
 * This holds all of the combined values that will be shown in chat. So all the values inside of this object represent all the emotes
 * that will be visible inside of chat
 * @property emoteBoardGlobalList a [State] object containing a map of all the global emotes. These are the global emotes that the
 * user is shown inside of the emote board(mock soft keyboard with emotes instead of keys)
 * @property emoteBoardChannelList a [State] object containing a map of all the channel emotes.These are the channel emotes that the
 * user is shown inside of the emote board(mock soft keyboard with emotes instead of keys)
 *
 * @property globalBetterTTVEmotes a [State] object containing a [IndivBetterTTVEmoteList] object that represents all the global BetterTTV emotes. You can
 * read more about the BetterTTV global emote, [HERE](https://betterttv.com/developers/api#global-emotes)
 * @property channelBetterTTVEmotes a [State] object containing a [IndivBetterTTVEmoteList] object that represents all the channel specific BetterTTV emotes.
 * You can read more about the BetterTTV channel emotes, [HERE](https://betterttv.com/developers/api#user)
 * @property sharedBetterTTVEmotes a [State] object containing a [IndivBetterTTVEmoteList] object that represents all the shared BetterTTV emotes.
 * You can read more about the BetterTTV shared emotes, [HERE](https://betterttv.com/developers/api#user)
 *
 * @property getGlobalEmotes()
 * @property getChannelEmotes()
 *
 * */
interface TwitchEmoteRepo {

    val emoteList: State<EmoteListMap>

    val emoteBoardGlobalList: State<EmoteNameUrlList>

    val emoteBoardChannelList:State<EmoteNameUrlEmoteTypeList>

    val globalBetterTTVEmotes:State<IndivBetterTTVEmoteList>

    val channelBetterTTVEmotes:State<IndivBetterTTVEmoteList>
    val sharedBetterTTVEmotes: State<IndivBetterTTVEmoteList>

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



    suspend fun getBetterTTVGlobalEmotes(): Flow<Response<List<IndivBetterTTVEmote>>>

    suspend fun getBetterTTVChannelEmotes(broadCasterId:String): Flow<Response<BetterTTVChannelEmotes>>


    /*****BELOW ARE THE METHODS THAT ARE USED TO GET THE CHAT BADGES****/
    suspend fun getGlobalChatBadges(oAuthToken: String, clientId: String): Flow<Response<Boolean>>

}







































