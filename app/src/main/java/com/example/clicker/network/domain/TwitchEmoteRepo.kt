package com.example.clicker.network.domain

import androidx.compose.runtime.State
import com.example.clicker.network.clients.BetterTTVChannelEmotes
import com.example.clicker.network.clients.IndivBetterTTVEmote
import com.example.clicker.network.repository.EmoteListMap
import com.example.clicker.network.repository.EmoteNameUrl
import com.example.clicker.network.repository.EmoteNameUrlEmoteTypeList
import com.example.clicker.network.repository.EmoteNameUrlList
import com.example.clicker.network.repository.IndivBetterTTVEmoteList
import com.example.clicker.presentation.stream.views.chat.chatSettings.ChatBadgePair
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow


/**
 * TwitchEmoteRepo is the interface that acts as the API for all the methods needed to interact with Twitch's emote servers

 * @property emoteBoardGlobalList a [State] object containing a map of all the global emotes. These are the global emotes that the
 * user is shown inside of the emote board(mock soft keyboard with emotes instead of keys)
 * @property emoteBoardChannelList a [State] object containing a map of all the channel emotes.These are the channel emotes that the
 * user is shown inside of the emote board(mock soft keyboard with emotes instead of keys)
 * @property globalChatBadges a [State] object containing a map of all the global chat badges.
 * This holds all of the combined values from Twitch's global emote chat badge enpoint
 *
 * @property globalBetterTTVEmotes a [State] object containing a [IndivBetterTTVEmoteList] object that represents all the global BetterTTV emotes. You can
 * read more about the BetterTTV global emote, [HERE](https://betterttv.com/developers/api#global-emotes)
 * @property channelBetterTTVEmotes a [State] object containing a [IndivBetterTTVEmoteList] object that represents all the channel specific BetterTTV emotes.
 * You can read more about the BetterTTV channel emotes, [HERE](https://betterttv.com/developers/api#user)
 * @property sharedBetterTTVEmotes a [State] object containing a [IndivBetterTTVEmoteList] object that represents all the shared BetterTTV emotes.
 * You can read more about the BetterTTV shared emotes, [HERE](https://betterttv.com/developers/api#user)
 *
 * @property globalTwitchEmoteList a [StateFlow] object containing a list of [EmoteNameUrl] objects that represents all the Global Twitch emotes.
 * You can read more about the Global Twitch emotes, [HERE](https://dev.twitch.tv/docs/api/reference/#get-global-emotes)
 * @property channelEmoteList a [StateFlow] object used to hold all the Twitch channel emotes shown to the user in chat
 * @property globalBetterTTVEmoteList a [StateFlow] object used to hold all the betterTTV global emotes shown to the user in chat
 * @property channelBetterTTVEmoteList a [StateFlow] object used to hold all the betterTTV channel emotes shown to the user in chat
 * @property sharedBetterTTVEmoteList a [StateFlow] object used to hold all the betterTTV shared emotes shown to the user in chat
 *
 *
 * @property getGlobalEmotes()  a function called to get Twitch  global emotes
 * @property getChannelEmotes() a function called to get channel  global emotes
 * @property getBetterTTVGlobalEmotes() a function called to get better ttv  global emotes
 * @property getBetterTTVChannelEmotes() a function called to get better ttv  channel emotes
 * @property getGlobalChatBadges() a function called to get Twitch  channel emotes
 *
 * */
interface TwitchEmoteRepo {

    val emoteBoardGlobalList: State<EmoteNameUrlList>
    val emoteBoardChannelList:State<EmoteNameUrlEmoteTypeList>
    val globalChatBadges: State<EmoteListMap>

    val globalBetterTTVEmotes:State<IndivBetterTTVEmoteList>
    val channelBetterTTVEmotes:State<IndivBetterTTVEmoteList>
    val sharedBetterTTVEmotes: State<IndivBetterTTVEmoteList>

    val globalTwitchEmoteList:StateFlow<List<EmoteNameUrl>>
    val channelEmoteList:StateFlow<List<EmoteNameUrl>> // add documenation for this
    val globalBetterTTVEmoteList:StateFlow<List<EmoteNameUrl>> // add documenation for this
    val channelBetterTTVEmoteList:StateFlow<List<EmoteNameUrl>> // add documenation for this
    val sharedBetterTTVEmoteList:StateFlow<List<EmoteNameUrl>> // add documenation for this

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



    /**
     * getBetterTTVGlobalEmotes a function used to make a request to the BetterTTV servers to access the global emote
     *
     * @return a [Flow] object containing a [Response] object that is used to determine if the request was a success or not
     * */
    suspend fun getBetterTTVGlobalEmotes(): Flow<Response<List<IndivBetterTTVEmote>>>

    /**
     * getBetterTTVChannelEmotes a function used to make a request to the BetterTTV servers to access the channel specific emotes
     *
     * @return a [Flow] object containing a [Response] object that is used to determine if the request was a success or not
     * */
    suspend fun getBetterTTVChannelEmotes(broadCasterId:String): Flow<Response<BetterTTVChannelEmotes>>


    /**
     * getGlobalChatBadges a function used to make a request to the Twitch servers to access the global chat badges
     * - Documentation for the global badges can be found [HERE](https://dev.twitch.tv/docs/api/reference/#get-global-chat-badges)
     *
     * @param oAuthToken a String object used to represent the oAuth-Token granted from the Twitch servers to the user. Each
     * oAuth-Token represents a unique user experience
     * @param clientId a String object used to represent the id of the developer account
     *
     * @return a [Flow] object containing a [Response] object that is used to determine if the request was a success or not
     * */
    suspend fun getGlobalChatBadges(oAuthToken: String, clientId: String): Flow<Response<List<ChatBadgePair>>>

}







































