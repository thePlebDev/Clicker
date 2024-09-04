package com.example.clicker.domain

import kotlinx.coroutines.flow.Flow

/**
 * - TwitchDataStore is the interface that acts as the API for all the methods accessing the locally stored DataStore
 * - You can read more about the token data store, [HERE](https://developer.android.com/topic/libraries/architecture/datastore)
 *
 * @property setOAuthToken a function meant to SET the locally stored oAuthToken
 * @property getOAuthToken aa function meant to GET the locally stored oAuthToken
 *
 * @property setUsername a function meant to SET the locally stored username of the logged in user
 * @property getUsername a function meant to GET the locally stored username of the logged in user
 *
 * @property setLoggedOutStatus a function meant to SET the locally stored loggedOutStatus, which is a string that determines if the user is logged in or not
 * @property getLoggedOutStatus a function meant to GET the locally stored loggedOutStatus, which is a string that determines if the user is logged in or not
 *
 * @property setClientId a function meant to SET the locally stored clientId, Which is the the unique identifier of the application
 * @property getClientId a function meant to GET the locally stored clientId, Which is the the unique identifier of the application
 *
 * */
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