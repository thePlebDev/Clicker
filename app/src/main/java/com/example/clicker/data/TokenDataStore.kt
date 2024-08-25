package com.example.clicker.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.clicker.domain.ChatSettingsDataStore
import com.example.clicker.domain.TwitchDataStore
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tokens")

class TokenDataStore @Inject constructor(
    private val context: Context
):TwitchDataStore,ChatSettingsDataStore {

    private val _oneClickActionState = MutableStateFlow(false)
     val oneClickActionState = _oneClickActionState.asStateFlow() // this is the text data shown to the user

    private val oAuthTokenKey = stringPreferencesKey("oAuth_token")
    private val usernameKey = stringPreferencesKey("username_value")


    private val userLoggedOutKey = stringPreferencesKey("user_logged_out")
    private val userLoggedOutStatusKey = booleanPreferencesKey("user_logged_out_loading_status")

    private val clientIdKey = stringPreferencesKey("client_id")

    /**below are all the variables used to store data about the chat settings*/
    private val badgeSizeIdKey = floatPreferencesKey("badge_size_id")
    private val usernameSizeIdKey = floatPreferencesKey("username_size_id")
    private val messageSizeIdKey = floatPreferencesKey("message_size_id")
    private val lineHeightIdKey = floatPreferencesKey("line_height_id")
    private val customUsernameColorIdKey = booleanPreferencesKey("custom_username_color_id")


    override suspend fun setOAuthToken(oAuthToken: String) {
        val another =context.dataStore.edit { tokens ->
            tokens[oAuthTokenKey] = oAuthToken
        }
    }

    override fun getOAuthToken(): Flow<String> {
        val oAuthToken: Flow<String> = context.dataStore.data
            .map { preferences ->
                preferences[oAuthTokenKey] ?: ""
            }
        return oAuthToken
    }



    override suspend fun setUsername(username: String) {
        context.dataStore.edit { database ->
            database[usernameKey] = username
        }
    }

    override fun getUsername(): Flow<String> {
        val username: Flow<String> = context.dataStore.data
            .map { preferences ->
                preferences[usernameKey] ?: ""
            }
        return username
    }

    /*****CHANGIGN THE ONES BELOW******/
    override suspend fun setLoggedOutStatus(loggedOut: String) {

        context.dataStore.edit { tokens ->
            tokens[userLoggedOutKey] = loggedOut
        }
    }

    override fun getLoggedOutStatus(): Flow<String?> {

        val username: Flow<String?> = context.dataStore.data
            .map { preferences ->
                preferences[userLoggedOutKey]
            }

        return username
    }

    /*****CHANGIGN THE ONES ABOVE******/

    override suspend fun setLoggedOutLoading(loggedOutStatus: Boolean) {
        context.dataStore.edit { tokens ->
            tokens[userLoggedOutStatusKey] = loggedOutStatus
        }
    }

    override fun getLoggedOutLoading(): Flow<Boolean> {
        val loggedOutStatus: Flow<Boolean> = context.dataStore.data
            .map { preferences ->
                preferences[userLoggedOutStatusKey] ?: false
            }
        return loggedOutStatus
    }

    override suspend fun setClientId(clientId: String) {
        context.dataStore.edit { tokens ->
            tokens[clientIdKey] = clientId
        }
    }

    override fun getClientId(): Flow<String> {
        val clientId: Flow<String> = context.dataStore.data
            .map { preferences ->
                preferences[clientIdKey] ?: ""
            }
        return clientId
    }

    override suspend fun setBadgeSize(badgeSize: Float) {
        try {
            context.dataStore.edit { tokens ->
                tokens[badgeSizeIdKey] = badgeSize
            }
            Log.d("TokenDataStoreException", "SUCCESS")
        } catch (e: Exception) {
        }

    }

    override fun getBadgeSize(): Flow<Float> {
        val badgeSize: Flow<Float> = context.dataStore.data
            .map { preferences ->
                preferences[badgeSizeIdKey] ?: 20f
            }
        return badgeSize
    }

    override suspend fun setUsernameSize(usernameSize: Float) {
        try {
            context.dataStore.edit { tokens ->
                tokens[usernameSizeIdKey] = usernameSize
            }
            Log.d("TokenDataStoreException", "SUCCESS")
        } catch (e: Exception) {
        }

    }

    override fun getUsernameSize(): Flow<Float> {
        val badgeSize: Flow<Float> = context.dataStore.data
            .map { preferences ->
                preferences[usernameSizeIdKey] ?: 15f
            }
        return badgeSize
    }

    override suspend fun setMessageSize(messageSize: Float) {
        try {
            context.dataStore.edit { tokens ->
                tokens[messageSizeIdKey] = messageSize
            }
            Log.d("TokenDataStoreException", "SUCCESS")
        } catch (e: Exception) {
        }

    }

    override fun getMessageSize(): Flow<Float> {
        val badgeSize: Flow<Float> = context.dataStore.data
            .map { preferences ->
                preferences[messageSizeIdKey] ?: 15f
            }
        return badgeSize
    }

    override suspend fun setLineHeight(lineHeight: Float) {
            context.dataStore.edit { tokens ->
                tokens[lineHeightIdKey] = lineHeight
            }


    }

    override fun getLineHeight(): Flow<Float> {
        val lineHeight: Flow<Float> = context.dataStore.data
            .map { preferences ->
                preferences[lineHeightIdKey] ?: (15f *1.6f)
            }
        return lineHeight
    }

    override suspend fun setCustomUsernameColor(showCustomUsernameColor: Boolean) {

        context.dataStore.edit { tokens ->
            tokens[customUsernameColorIdKey] = showCustomUsernameColor
        }
    }

    override fun getCustomUsernameColor(): Flow<Boolean> {
        val showCustomUsernameColor: Flow<Boolean> = context.dataStore.data
            .map { preferences ->
                preferences[customUsernameColorIdKey] ?: true
            }
        return showCustomUsernameColor
    }
}


