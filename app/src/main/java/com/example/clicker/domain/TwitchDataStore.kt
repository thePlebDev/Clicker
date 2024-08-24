package com.example.clicker.domain

import kotlinx.coroutines.flow.Flow

interface TwitchDataStore {

    suspend fun setOAuthToken(oAuthToken: String)

    fun getOAuthToken(): Flow<String>

    suspend fun setUsername(username: String)

    fun getUsername(): Flow<String>

    //so that means that these should be used to set the state of the current user. ie: are the logged in or not
    //so both of these should be changed to strings
    suspend fun setLoggedOutStatus(loggedOut:String)
    fun getLoggedOutStatus(): Flow<String?>

    /**
     * setLoggedOutLoading() is used to SET internal flags to determine if there should be a
     * loading icon or not
     *
     * @param loggedOutStatus a Boolean object used to determine if a loading icon should be shown on the
     * screen or not
     * */
    suspend fun setLoggedOutLoading(loggedOutStatus:Boolean)

    /**
     * getLoggedOutLoading() is used to GET internal flags to determine if there should be a
     * loading icon or not
     * */
    fun getLoggedOutLoading(): Flow<Boolean>

    suspend fun setClientId(clientId:String)
    fun getClientId(): Flow<String>





}