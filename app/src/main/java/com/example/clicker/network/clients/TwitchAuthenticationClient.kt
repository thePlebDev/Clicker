package com.example.clicker.network.clients

import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface TwitchAuthenticationClient {

    /**
     * - represented as a GET method. This function is used to verify the validity of the [authorization] token with
     * the Twitch servers
     *
     * @param url a String used to represent a dynamic URL.
     * @param authorization a String used to represent the OAuth token that is being sent to be validated
     * */
    @GET("validate")
    suspend fun validateToken(
        @Header("Authorization") authorization: String
    ): Response<ValidatedUser>

    /**
     * - logout represents a POST method. This function meant to end the users logged in session with the Twitch server
     *
     * @param url a String used to represent a dynamic URL of where this request is being sent.
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param token a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * */
    @Headers("Content-Typ: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("revoke")
    suspend fun logout(
        @Field("client_id") clientId: String,
        @Field("token") token: String
    ): Response<Void>
}