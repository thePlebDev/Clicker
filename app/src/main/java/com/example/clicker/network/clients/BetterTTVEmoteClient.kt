package com.example.clicker.network.clients

import com.example.clicker.network.models.emotes.BetterTTVChannelEmotes
import com.example.clicker.network.models.emotes.IndivBetterTTVEmote
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
interface BetterTTVEmoteClient {

    /**
     * - A GET request to the BetterTTV backend to request all global emotes.
     * - You can read more about the BetterTTV documentation, [HERE](https://betterttv.com/developers/api#global-emotes)
     * */
    @GET("cached/emotes/global")
     suspend fun getGlobalEmotes(
    ): Response<List<IndivBetterTTVEmote>>


    /**
     * - A GET request to the BetterTTV backend to request all channel specific emotes.
     * - You can read more about the BetterTTV documentation, [HERE](https://betterttv.com/developers/api#channel-emotes)
     *
     * @param broadcasterId a String representing the unique identifier of the Twitch streamer
     * */
    @GET("cached/users/twitch/{broadcasterId}")
    suspend fun getChannelEmotes(
        @Path("broadcasterId") broadcasterId: String
    ): Response<BetterTTVChannelEmotes>


}


