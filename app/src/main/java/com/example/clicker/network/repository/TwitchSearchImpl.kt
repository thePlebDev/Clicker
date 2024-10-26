package com.example.clicker.network.repository

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.clicker.network.clients.Game
import com.example.clicker.network.clients.SearchStreamData
import com.example.clicker.network.clients.TopGame
import com.example.clicker.network.clients.TwitchHomeClient
import com.example.clicker.network.clients.TwitchSearchClient
import com.example.clicker.network.clients.UnbanRequestItem
import com.example.clicker.network.domain.StreamType
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.domain.TwitchSearch
import com.example.clicker.network.models.twitchRepo.FollowedLiveStreams
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.network.repository.util.handleNetworkNewUserExceptions
import com.example.clicker.util.NetworkNewUserResponse
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TwitchSearchImpl @Inject constructor(
    private val twitchHomeClient: TwitchSearchClient,
) : TwitchSearch {

    private val _mostRecentPaginationRequestId: MutableStateFlow<String?> = MutableStateFlow(null)
    // The UI collects from this StateFlow to get its state updates
    override val mostRecentPaginationRequestId: StateFlow<String?> = _mostRecentPaginationRequestId

    //todo: change this to be like [_mostRecentPaginationRequestId] and [mostRecentPaginationRequestId]
    private val _mostRecentStreamModalPaginationRequestId: MutableStateFlow<String?> = MutableStateFlow(null)

    override val mostRecentStreamModalPaginationRequestId: StateFlow<String?> = _mostRecentStreamModalPaginationRequestId
    // The UI collects from this StateFlow to get its state updates






    //make the response call
    override suspend fun getTopGames(
        authorizationToken: String,
        clientId: String,
        after:String
    ): Flow<Response<List<TopGame>>> = flow {
        emit(Response.Loading)


        val response = twitchHomeClient.getTopGames(
            authorization = "Bearer $authorizationToken",
            clientId = clientId,
            after = after
        )
        Log.d("getTopGames","getFollowedLiveStreams code -->${response.code()}")


        val body = response.body()?.data ?: listOf()


        if (response.isSuccessful) {
            val cursor = response.body()?.pagination?.cursor
            _mostRecentPaginationRequestId.tryEmit(response.body()?.pagination?.cursor)
            Log.d("getTopGamescursor","cursor---?$cursor")
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

    override suspend fun getGameInfo(
        authorizationToken: String,
        clientId: String,
        id: String
    ): Flow<Response<Game?>>  = flow{

        val response = twitchHomeClient.getGameInfo(
            authorization = "Bearer $authorizationToken",
            clientId = clientId,
            id = id
        )

        val body = response.body()?.data ?: listOf()

        if (response.isSuccessful) {
            Log.d("getGameInfo","SUCCESS")
            if (body.isEmpty()){
                Log.d("getGameInfo","EMPTY BODY")
                //I should make this nullable
                emit(Response.Success(null))
            }else{
                Log.d("getGameInfo","BODY->${body[0]}")
                emit(Response.Success(body[0]))
            }

        } else {
            Log.d("getGameInfo","FAILED")
            emit(Response.Failure(Exception("Error!, Please try again")))
        }
    }.catch { cause ->
        Log.d("getGameInfo","CAUGHT EXCEPTION")
        emit(Response.Failure(Exception("Exception caught")))
    }

    override suspend fun getStreams(
        authorization: String,
        clientId: String,
        gameId: String,
        type: StreamType,
        language: String,
        after: String
    ): Flow<Response<List<SearchStreamData>>> = flow{
        emit(Response.Loading)
        Log.d("getStreamsSearch","LOADING")
        Log.d("getStreamsSearchPagination","paginationId->${_mostRecentStreamModalPaginationRequestId.value}")
        val response = twitchHomeClient.getStreams(
            authorization = "Bearer $authorization",
            clientId = clientId,
            gameId = gameId,
            type=type.toString(),
            language="en",
            after=after
        )

        val body = response.body()?.data ?: listOf()
        val paginationId =response.body()?.pagination?.cursor ?:""
        if (response.isSuccessful) {
            Log.d("getStreamsSearch","SUCCESS")
            Log.d("getStreamsSearch","body->$body")
            _mostRecentStreamModalPaginationRequestId.tryEmit(paginationId)

            emit(Response.Success(body))

        } else {
            Log.d("getStreamsSearch","FAILED")
            emit(Response.Failure(Exception("Error!, Please try again")))
        }
    }.catch { cause ->
        Log.d("getStreamsSearch","CAUGHT EXCEPTION")
        emit(Response.Failure(Exception("Exception caught")))
    }
}