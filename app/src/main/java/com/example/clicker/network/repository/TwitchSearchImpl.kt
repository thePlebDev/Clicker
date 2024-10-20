package com.example.clicker.network.repository

import android.util.Log
import com.example.clicker.network.clients.TopGame
import com.example.clicker.network.clients.TwitchHomeClient
import com.example.clicker.network.clients.TwitchSearchClient
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.domain.TwitchSearch
import com.example.clicker.network.models.twitchRepo.FollowedLiveStreams
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.network.repository.util.handleNetworkNewUserExceptions
import com.example.clicker.util.NetworkNewUserResponse
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TwitchSearchImpl @Inject constructor(
    private val twitchHomeClient: TwitchSearchClient,
) : TwitchSearch {



    //make the response call
    override suspend fun getTopGames(
        authorizationToken: String,
        clientId: String
    ): Flow<Response<List<TopGame>>> = flow {
        emit(Response.Loading)


        val response = twitchHomeClient.getTopGames(
            authorization = "Bearer $authorizationToken",
            clientId = clientId,
        )
        Log.d("getTopGames","getFollowedLiveStreams code -->${response.code()}")


        val body = response.body()?.data ?: listOf()


        if (response.isSuccessful) {
            Log.d("getTopGames","SUCCESS")
            Log.d("getTopGames","body --->$body")
            emit(Response.Success(body))
        } else {
            Log.d("getTopGames","FAILED")
            emit(Response.Failure(Exception("Error!, Please try again")))
        }
    }.catch { cause ->
        Log.d("getTopGames","CAUGHT EXCEPTION")
        emit(Response.Failure(Exception("Exception caught")))
    }
}