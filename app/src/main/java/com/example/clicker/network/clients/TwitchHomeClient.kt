package com.example.clicker.network.clients

import com.example.clicker.network.models.twitchRepo.FollowedLiveStreams
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query


/**
 * TwitchHomeClient is the interface that Retrofit will use and turn into a HTTP client. Specifically, this interface
 * is meant to interact with the Twitch API for the home page
 *
 * @property getFollowedStreams a function meant to get all of the user's live followed streams
 * @property getModeratedChannels a function meant to get all of the user's channels they are a moderator for (offline and online)
 * */
interface TwitchHomeClient {


    /**
     * - getFollowedStreams represents a GET method. a function meant to get all of the user's live followed streams
     *
     * @param authorization a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param userId a String used to represent the OAuth token that uniquely identifies this user
     *
     * @return a [Response] object containing [FollowedLiveStreams]
     * */

    @GET("streams/followed")
    suspend fun getFollowedStreams(
        @Header("Authorization") authorization: String,
        @Header("Client-Id") clientId: String,
        @Query("user_id") userId: String
    ): Response<FollowedLiveStreams>

    /**
     * - getModeratedChannels represents a GET method. a function meant to get all of the user's channels they are a moderator for
     *
     * @param authorizationToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param userId a String used to represent the OAuth token that uniquely identifies this user
     *
     * @return a [Response] object containing [GetModChannels]
     * */
    @GET("moderation/channels")
    suspend fun getModeratedChannels(
        @Header("Authorization") authorizationToken: String,
        @Header("Client-Id") clientId: String,
        @Query("user_id") userId: String
    ):Response<GetModChannels>
}