package com.example.clicker.network.domain

import kotlinx.coroutines.flow.Flow

interface TwitchRepo {
    suspend fun validateToken(token:String): Flow<Boolean>


}