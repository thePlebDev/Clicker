package com.example.clicker.domain

import kotlinx.coroutines.flow.Flow

interface TwitchDataStore {

    suspend fun setOAuthToken(oAuthToken: String)

    fun getOAuthToken(): Flow<String>

    suspend fun setUsername(username: String)

    fun getUsername(): Flow<String>


}