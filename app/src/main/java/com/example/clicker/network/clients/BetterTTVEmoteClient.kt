package com.example.clicker.network.clients

import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface BetterTTVEmoteClient {


    @GET("cached/emotes/global")
     suspend fun getGlobalEmotes(
    ): Response<List<IndivBetterTTVEmote>>


}




data class IndivBetterTTVEmote(
    val id: String,
    val code: String,
    val imageType: String,
    val animated: Boolean,
    val userId: String,
    val modifier: Boolean
)

data class BetterTTVEmoteList(
    val emotes: List<IndivBetterTTVEmote>
)