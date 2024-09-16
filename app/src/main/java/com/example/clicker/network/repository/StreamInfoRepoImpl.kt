package com.example.clicker.network.repository

import android.util.Log
import com.example.clicker.network.clients.ChannelInfo
import com.example.clicker.network.clients.Game
import com.example.clicker.network.clients.GameInfo
import com.example.clicker.network.clients.TwitchStreamInfoClient
import com.example.clicker.network.domain.StreamInfoRepo
import com.example.clicker.network.repository.util.handleNetworkAuthExceptions
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StreamInfoRepoImpl @Inject constructor(
    private val twitchStreamInfoRepo: TwitchStreamInfoClient
): StreamInfoRepo {
    override suspend fun getChannelInformation(
        authorizationToken: String,
        clientId: String,
        broadcasterId: String
    ): Flow<Response<ChannelInfo>> = flow {
        emit(Response.Loading)

        val response = twitchStreamInfoRepo.getChannelInformation(
            authorization = "Bearer $authorizationToken",
            clientId = clientId,
            broadcasterId = broadcasterId
        )
        if(response.isSuccessful){

            val data = response.body()?.data?.get(0)
            Log.d("getChannelInformationInformation","SUCCESS")
            Log.d("getChannelInformationInformation","data -->$data")
            emit(Response.Success(data!!))
        }else{
            Log.d("getChannelInformationInformation","FAILED")
            Log.d("getChannelInformationInformation","message -->${response.code()}")
            Log.d("getChannelInformationInformation","message -->${response.message()}")
            Log.d("getChannelInformationInformation","message -->${response.errorBody()}")

        }

    }.catch { cause ->
        emit(Response.Failure(Exception("FAILED")))
    }

    override suspend fun getCategoryInformation(
        authorizationToken: String,
        clientId: String,
        gameName: String,
        gameId:String
    ): Flow<Response<List<Game>>>  = flow{
        emit(Response.Loading)
        Log.d("getCategoryInformationRepo","CALLED")
        Log.d("getCategoryInformationRepo","gameName ->$gameName")
        val response = twitchStreamInfoRepo.getCategories(
            authorization = "Bearer $authorizationToken",
            clientId = clientId,
            gameName = gameName
        )
        if(response.isSuccessful){

            val data = response.body()?.data ?: listOf()
            val gameItemInfo = data.filter { it.id == gameId }
            Log.d("getCategoryInformationRepo","SUCCESS")
            Log.d("getCategoryInformationRepo","data -->$data")
            emit(Response.Success(gameItemInfo))
        }else{
            emit(Response.Failure(Exception("Failed")))
            Log.d("getCategoryInformationRepo","FAILED")
            Log.d("getCategoryInformationRepo","message -->${response.code()}")
            Log.d("getCategoryInformationRepo","message -->${response.message()}")
            Log.d("getCategoryInformationRepo","message -->${response.errorBody()}")

        }
    }.catch {cause ->
        Log.d("getCategoryInformationRepo"," ERROR")
        emit(Response.Failure(Exception("Failed")))
    }

    override suspend fun searchCategories(
        authorizationToken: String,
        clientId: String,
        gameName: String
    ): Flow<Response<List<Game>>> = flow {
        emit(Response.Loading)
        val response = twitchStreamInfoRepo.getCategories(
            authorization = "Bearer $authorizationToken",
            clientId = clientId,
            gameName = gameName
        )
        if(response.isSuccessful){

            val data = response.body()?.data ?: listOf()
            Log.d("getCategoryInformationRepo","SUCCESS")
            Log.d("getCategoryInformationRepo","data -->$data")
            emit(Response.Success(data))
        }else{
            emit(Response.Failure(Exception("Failed")))
            Log.d("getCategoryInformationRepo","FAILED")
            Log.d("getCategoryInformationRepo","message -->${response.code()}")
            Log.d("getCategoryInformationRepo","message -->${response.message()}")
            Log.d("getCategoryInformationRepo","message -->${response.errorBody()}")

        }
    }.catch {cause ->
        Log.d("getCategoryInformationRepo"," ERROR")
        emit(Response.Failure(Exception("Failed")))
    }
}