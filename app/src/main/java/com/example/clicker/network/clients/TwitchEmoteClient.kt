package com.example.clicker.network.clients

import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface TwitchEmoteClient {

    /**
     * - represented as a GET method. This function is used to verify the validity of the [authorization] token with
     * the Twitch servers
     *
     * @param url a String used to represent a dynamic URL.
     * @param authorization a String used to represent the OAuth token that is being sent to be validated
     * */
    @GET("chat/emotes/global")
    suspend fun getGlobalEmotes(
        @Header("Authorization") authorization: String,
        @Header("Client-Id") clientId: String,
    ): Response<EmoteData>
}



data class Daum(
    @SerializedName("set_id")
    val setId: String,
    val versions: List<Version>,
)

data class Version(
    val id: String,
    val image_url_1x: String,
    val image_url_2x: String,
    val image_url_4x: String,
    val title: String,
    val description: String,
    val click_action: String,
    val click_url: String,
)

//below
data class Emote(
    val id: String,
    val name: String,
    val images: Images,
    val format: List<String>,
    val scale: List<String>,
    val theme_mode: List<String>
)

data class Images(
    val url_1x: String,
    val url_2x: String,
    val url_4x: String
)

data class EmoteData(
    val data: List<Emote>
)