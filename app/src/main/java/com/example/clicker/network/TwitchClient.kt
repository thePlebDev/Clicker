package com.example.clicker.network

import com.example.clicker.network.models.AccessToken
import com.example.clicker.network.models.FollowedLiveStreams
import com.example.clicker.network.models.GitHubProfile
import com.example.clicker.network.models.ValidatedUser



import retrofit2.Response


import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.Url

interface TwitchClient {

    @GET
    suspend fun validateToken(
        @Url url:String = "https://id.twitch.tv/oauth2/validate",
        @Header("Authorization") authorization:String,
    ): Response<ValidatedUser>

    @GET("streams/followed")
    suspend fun getFollowedStreams(
        @Header("Authorization") authorization:String,
        @Header("Client-Id") clientId:String,
        @Query("user_id") userId:String
    ): Response<FollowedLiveStreams>
}

















