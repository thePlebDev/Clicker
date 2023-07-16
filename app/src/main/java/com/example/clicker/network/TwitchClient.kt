package com.example.clicker.network

import com.example.clicker.network.models.AccessToken
import com.example.clicker.network.models.GitHubProfile
import com.example.clicker.network.models.ValidatedUser


import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface TwitchClient {

    @GET
    suspend fun validateToken(
        @Url url:String = "https://id.twitch.tv/oauth2/validate",
        @Header("Authorization") authorization:String,
    ): Response<ValidatedUser>
}

