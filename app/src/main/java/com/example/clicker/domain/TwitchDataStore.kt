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

    /**
     * - **setOAuthToken** a function, when called with a String, will set the Authentication token representing the Twitch
     * authentication session
     *
     * @param oAuthToken a String representing the Twitch Authentication token
     *
     * */
    suspend fun setOAuthToken(oAuthToken: String)

    /**
     * - **setOAuthToken** a function, when called with a String, will set the Authentication token representing the Twitch
     * authentication session
     *
     * @return a [Flow] containing a String representing the Twitch authentication system
     *
     * */
    fun getOAuthToken(): Flow<String>

    /**
     * - **setUsername** a function, when called with a String, will set the locally stored username of the logged in user
     * @param username a String representing the logged in user's name
     *
     * */
    suspend fun setUsername(username: String)


    /**
     * - **getUsername** a function, when called, will get the locally stored username of the logged in user
     *
     * @return a [Flow] a String representing the logged in user's name
     *
     * */
    fun getUsername(): Flow<String>

    /**
     * - **setLoggedOutStatus** a function, when called with a String, will determine if the user is logged in or not
     * @param loggedOut a String representing the status of the logged in user
     *
     * */
    suspend fun setLoggedOutStatus(loggedOut:String)

    /**
     * - **getLoggedOutStatus** a function, when called, will get the logged in users status
     *
     * @return a [Flow] a String? representing the logged in user's status
     *
     * */
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


    /**
     * - **setClientId** is used to store the uniqie device id for this application
     * @param clientId A String representing the device's unique id
     * */
    suspend fun setClientId(clientId:String)

    /**
     * - **getClientId** is used to store the uniqie device id for this application
     * @return a [Flow] containing a  String representing the device's unique id
     * */
    fun getClientId(): Flow<String>





}