package com.example.clicker.network.clients

import com.example.clicker.network.models.emotes.BetterTTVChannelEmotes
import com.example.clicker.network.models.emotes.IndivBetterTTVEmote
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * - **BetterTTVEmoteClient** is an interface that will act as the API to the BetterTTV emote servers
 *
 * @property getGlobalEmotes a function used to get the global BetterBTTV Emote
 * @property getChannelEmotes a function used to get the channel specific BetterBTTV Emote
 * */
interface BetterTTVEmoteClient {

    /**
     * - **getGlobalEmotes** is a GET request to the BetterTTV backend to request all global emotes.
     * - You can read more about the BetterTTV documentation, [HERE](https://betterttv.com/developers/api#global-emotes)
     * */
    @GET("cached/emotes/global")
     suspend fun getGlobalEmotes(
    ): Response<List<IndivBetterTTVEmote>>


    /**
     * - **getGlobalEmotes** is a GET request to the BetterTTV backend to request all channel specific emotes.
     * - You can read more about the BetterTTV documentation, [HERE](https://betterttv.com/developers/api#channel-emotes)
     *
     * @param broadcasterId a String representing the unique identifier of the Twitch streamer
     * */
    @GET("cached/users/twitch/{broadcasterId}")
    suspend fun getChannelEmotes(
        @Path("broadcasterId") broadcasterId: String
    ): Response<BetterTTVChannelEmotes>


}


