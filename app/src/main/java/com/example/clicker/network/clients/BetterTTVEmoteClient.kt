package com.example.clicker.network.clients

import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface BetterTTVEmoteClient {


    @GET("cached/emotes/global")
     suspend fun getGlobalEmotes(
    ): Response<List<IndivBetterTTVEmote>>


    @GET("cached/users/twitch/{broadcasterId}")
    suspend fun getChannelEmotes(
        @Path("broadcasterId") broadcasterId: String
    ): Response<BetterTTVChannelEmotes>



}

/**
 * */
data class IndivBetterTTVEmote(
    val id: String,
    val code: String,
    val imageType: String,
    val animated: Boolean,
    val userId: String,
    val modifier: Boolean
)

/**BELOW IS ALL THE INFORMATION FOR THE BETTERTTV CHANNEL EMOTES*/
data class BetterTTVChannelEmotes(
    val id: String="",
    val bots: List<String> = listOf(),
    val avatar: String="",
    val channelEmotes: List<BetterTTVChannelEmote> = listOf(),
    val sharedEmotes:List<BetterTTVSharedEmote> = listOf()
)

data class BetterTTVChannelEmote(
    val id: String,
    val code: String,
    val imageType: String,
    val animated: Boolean,
    val userId: String,
)

data class BetterTTVSharedEmote(
    val id: String,
    val code: String,
    val imageType: String,
    val animated: Boolean,
    val user: BetterTTVSharedEmoteUser
)

data class BetterTTVSharedEmoteUser(
    val id: String,
    val name: String,
    val displayName: String,
    val providerId: String
)
