package com.example.clicker.network.domain

import com.example.clicker.network.models.ValidatedUser
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow

interface TwitchRepo {
    suspend fun validateToken(token:String): Flow<Response<ValidatedUser>>


}