package com.example.clicker.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey

import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkManager
import com.example.clicker.data.workManager.OAuthTokeValidationWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tokens")

class TokenDataStore @Inject constructor(
    private val context:Context
    ){
    private val TOKEN_KEY = stringPreferencesKey("login_token")
    private val oAuthTokenKey = stringPreferencesKey("oAuth_token")




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
    suspend fun setOAuthToken(oAuthToken:String){
        context.dataStore.edit { tokens ->
            tokens[oAuthTokenKey] = oAuthToken
        }

    }
    fun getOAuthToken():Flow<String>{
        val oAuthToken:Flow<String> = context.dataStore.data
            .map { preferences ->
                preferences[oAuthTokenKey] ?: ""
            }
        return oAuthToken
    }


}