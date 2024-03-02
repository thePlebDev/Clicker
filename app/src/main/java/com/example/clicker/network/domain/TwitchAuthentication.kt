package com.example.clicker.network.domain

import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.NetworkNewUserResponse
import com.example.clicker.util.NetworkResponse
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

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
     *
     * @return a flow containing a [NetworkAuthResponse] object of type [ValidatedUser]
     * */
    suspend fun validateToken(
        token: String
    ): Flow<NetworkNewUserResponse<ValidatedUser>>

    /**
     * logout is a function that is called to end the user's logged in session
     *
     * @param token a String representing a oAuth token
     * @param clientId a String representing the unique identifier of this device
     * */
    fun logout(clientId: String, token: String): Flow<NetworkAuthResponse<String>>


}