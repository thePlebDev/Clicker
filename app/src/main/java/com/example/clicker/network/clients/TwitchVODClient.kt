package com.example.clicker.network.clients

import com.example.clicker.network.models.twitchRepo.FollowedLiveStreams
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import javax.annotation.concurrent.Immutable

interface TwitchVODClient {


    /**
     * - getChannelVODs represents a GET method. a function meant to get all of the user's VOD videos
     * - [Official Twitch documentation on getting VODS](https://dev.twitch.tv/docs/api/reference/#get-videos)
     *
     * @param authorization a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param userId a String used to represent the user we are getting VODS for
     *
     * @return a [Response] object containing [VODData]
     * */

    @GET("videos")
    suspend fun getChannelVODs(
        @Header("Authorization") authorization: String,
        @Header("Client-Id") clientId: String,
        @Query("user_id") userId: String
    ): Response<VODData>
}


@Immutable //adding immutable to make it stable
data class VODData(
    val data: List<VOD>
)

data class VOD(
    val id: String,
    val stream_id: String,
    val user_id: String,
    val user_login: String,
    val user_name: String,
    val title: String,
    val description: String,
    val created_at: String,
    val published_at: String,
    val url: String,
    val thumbnail_url: String,
    val viewable: String,
    val view_count: Int,
    val language: String,
    val type: String,
    val duration: String,
    val muted_segments: List<MutedSegment>? // This can be more specific if you know the structure
)

data class MutedSegment(
    val duration: Int,
    val offset: Int
)