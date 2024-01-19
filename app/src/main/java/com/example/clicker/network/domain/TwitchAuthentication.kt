package com.example.clicker.network.domain

import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow

interface TwitchAuthentication {
    suspend fun validateToken(token: String): Flow<Response<ValidatedUser>>

    fun logout(clientId: String, token: String): Flow<Response<String>>
}