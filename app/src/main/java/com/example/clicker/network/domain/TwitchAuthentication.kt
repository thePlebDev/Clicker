package com.example.clicker.network.domain

import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow

/**
 * TwitchAuthentication is the interface that acts as the API for all the methods needed to interact with Twitch's authentication API
 *
 * @property validateToken a function meant to validate a user's OAuth token
 * @property logout a function meant to end the user's logged in session
 * */
interface TwitchAuthentication {

    /**
     * validateToken is a function that is called to validate [token] with the Twitch servers
     *
     * @param token a String representing a oAuth token
     * */
    suspend fun validateToken(
        url:String="https://id.twitch.tv/oauth2/validate",
        token: String): Flow<Response<ValidatedUser>>

    /**
     * logout is a function that is called to end the user's logged in session
     *
     * @param token a String representing a oAuth token
     * @param clientId a String representing the unique identifier of this device
     * */
    fun logout(clientId: String, token: String): Flow<Response<String>>
}