package com.example.clicker.network.repository

import android.util.Log

import com.example.clicker.network.clients.BetterTTVEmoteClient
import com.example.clicker.network.domain.BetterTTVEmotes
import com.example.clicker.network.repository.util.handleNetworkAuthExceptions
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BetterTTVEmotesImpl @Inject constructor(
    private val betterTTVClient: BetterTTVEmoteClient
): BetterTTVEmotes {



    override suspend fun getBetterTTVGlobalEmotes()= flow{
        emit(Response.Loading)
        Log.d("getGlobalBetterTTVEmotes", "LOADING")
        val response = betterTTVClient.getGlobalEmotes()
        if (response.isSuccessful) {

            val data = response.body() ?: listOf()
            Log.d("getGlobalBetterTTVEmotes", "DATA ->${data}")
            emit(Response.Success(data))
        } else {
            Log.d("getGlobalBetterTTVEmotes", "message ->${response.message()}")
            Log.d("getGlobalBetterTTVEmotes", "code ->${response.code()}")
            Log.d("getGlobalBetterTTVEmotes", "FAILED ->${response.body()}")
            emit(Response.Failure(Exception("Failed to get emote")))
        }
    }

}