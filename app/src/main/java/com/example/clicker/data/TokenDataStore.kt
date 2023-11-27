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
import kotlinx.coroutines.flow.map

// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tokens")

class TokenDataStore @Inject constructor(
    private val context: Context
):TwitchDataStore {

    private val oAuthTokenKey = stringPreferencesKey("oAuth_token")
    private val usernameKey = stringPreferencesKey("username_value")

    private val oneClickActionsKey = booleanPreferencesKey("one_click_action")

    override suspend fun setOAuthToken(oAuthToken: String) {
        context.dataStore.edit { tokens ->
            tokens[oAuthTokenKey] = oAuthToken
        }
    }
    override suspend fun setOneClickAction(isOneClickOn:Boolean){
        context.dataStore.edit { item ->
            item[oneClickActionsKey] = isOneClickOn
        }
    }
    override fun getOAuthToken(): Flow<String> {
        val oAuthToken: Flow<String> = context.dataStore.data
            .map { preferences ->
                preferences[oAuthTokenKey] ?: ""
            }
        return oAuthToken
    }
    override fun getOneClickAction():Flow<Boolean>{
        val isOneClickOn: Flow<Boolean> = context.dataStore.data
            .map { preferences ->
                preferences[oneClickActionsKey] ?: false
            }
        return isOneClickOn
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
}


