package com.example.clicker.network

import com.example.clicker.network.models.AccessToken
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers

interface GitHubClient {

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("login/oauth/access_token/")
    suspend fun getAccessToken(
        @Field("client_id") client_id:String,
        @Field("client_secret") client_secret:String,
        @Field("code") code:String,

    ):Response<AccessToken>

}