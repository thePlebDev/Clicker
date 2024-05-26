package com.example.clicker.domain

import kotlinx.coroutines.flow.Flow

interface TwitchDataStore {

    suspend fun setOAuthToken(oAuthToken: String)

    fun getOAuthToken(): Flow<String>

    suspend fun setUsername(username: String)

    fun getUsername(): Flow<String>

    suspend fun setLoggedOutStatus(loggedOut:Boolean)
    fun getLoggedOutStatus(): Flow<Boolean>

    suspend fun setLoggedOutLoading(loggedOutStatus:Boolean)
    fun getLoggedOutLoading(): Flow<Boolean>




}