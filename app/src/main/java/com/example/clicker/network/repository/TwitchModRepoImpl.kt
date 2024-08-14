package com.example.clicker.network.repository

import com.example.clicker.network.clients.TwitchModClient
import com.example.clicker.network.domain.TwitchModRepo
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class TwitchModRepoImpl @Inject constructor(
    twitchModClient: TwitchModClient
) : TwitchModRepo {

    //I first need to get the loading UI
    // then the failed/try UI
    // then the success UI




    override suspend fun getUserInformation(
        authorizationToken: String,
        clientId: String,
        userId: String
    ): Flow<Response<Boolean>> = flow{
        emit(Response.Loading)
        TODO("Not yet implemented")
    }
}