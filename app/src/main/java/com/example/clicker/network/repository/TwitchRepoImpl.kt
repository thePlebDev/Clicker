package com.example.clicker.network.repository

import android.util.Log
import com.example.clicker.network.TwitchClient
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.FollowedLiveStreams
import com.example.clicker.network.models.ValidatedUser
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TwitchRepoImpl @Inject constructor(
    private val twitchClient: TwitchClient
): TwitchRepo {

    override suspend fun validateToken(token:String):Flow<Response<ValidatedUser>> = flow{
        emit(Response.Loading)
       val response= twitchClient.validateToken(
            authorization = "OAuth $token"
        )
        if(response.isSuccessful){
           emit(Response.Success(response.body()!!))
        }else{
            emit(Response.Failure(Exception("Error! Please login again")))
        }

    }

    override suspend fun getFollowedLiveStreams(
        authorizationToken: String,
        clientId: String,
        userId: String
    ): Flow<Response<FollowedLiveStreams>> = flow{
        emit(Response.Loading)
        val response = twitchClient.getFollowedStreams(
            authorization = "Bearer $authorizationToken",
            clientId = clientId,
            userId = userId
        )
        if (response.isSuccessful){

            emit(Response.Success(response.body()!!))
        }else{

            emit(Response.Failure(Exception("Error!, code: {${response.code()}}")))

        }
    }

}