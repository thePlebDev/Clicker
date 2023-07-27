package com.example.clicker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey

import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tokens")

class TokenDataStore @Inject constructor(
    private val context:Context
    ){
    private val TOKEN_KEY = stringPreferencesKey("login_token")

    fun getToken():Flow<String>{

        val loginToken: Flow<String> = context.dataStore.data
            .map { preferences ->
                // No type safety.
                preferences[TOKEN_KEY] ?: ""
            }

        return loginToken

    }

    suspend fun updateToken(loginToken:String) {
        context.dataStore.edit { tokens ->
            //val currentCounterValue = tokens[TOKEN_KEY] ?: ""
            tokens[TOKEN_KEY] = loginToken
        }
    }

}