package com.example.clicker.network

import com.example.clicker.network.models.ChatSettings
import com.example.clicker.network.models.FollowedLiveStreams
import com.example.clicker.network.models.ValidatedUser



import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded


import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
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

    @Headers("Content-Typ: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    suspend fun logout(
        @Url url: String = "https://id.twitch.tv/oauth2/revoke",
        @Field("client_id") clientId:String,
        @Field("token") token:String
    ):Response<Void>

    @GET("chat/settings")
    suspend fun getChatSettings(
        @Header("Authorization") authorization:String,
        @Header("Client-Id") clientId:String,
        @Query("broadcaster_id") broadcasterId:String
    ):Response<ChatSettings>



}

















