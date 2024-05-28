package com.example.clicker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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
):TwitchDataStore {

    private val _oneClickActionState = MutableStateFlow(false)
     val oneClickActionState = _oneClickActionState.asStateFlow() // this is the text data shown to the user

    private val oAuthTokenKey = stringPreferencesKey("oAuth_token")
    private val usernameKey = stringPreferencesKey("username_value")


    private val userLoggedOutKey = stringPreferencesKey("user_logged_out")
    private val userLoggedOutStatusKey = booleanPreferencesKey("user_logged_out_loading_status")


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
}


