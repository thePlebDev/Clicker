package com.example.clicker.network.repository

import android.util.Log
import com.example.clicker.network.clients.TwitchEmoteClient
import com.example.clicker.network.repository.util.handleException
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TwitchEmoteImpl @Inject constructor(
    private val twitchEmoteClient: TwitchEmoteClient
){


      fun getGlobalEmotes(
        oAuthToken: String,
        clientId: String,
    ): Flow<Response<Boolean>> = flow {
        emit(Response.Loading)
         val response = twitchEmoteClient.getGlobalEmotes(
             authorization = "Bearer $oAuthToken",
             clientId = clientId
         )


        if (response.isSuccessful) {
            val data = response.body()?.data
            Log.d("getGlobalEmotes","SUCCESS ->${data}")


            Log.d("getGlobalEmotes","SUCCESS size->${response.body()?.data?.size}")
            emit(Response.Success(true))
        } else {
            Log.d("getGlobalEmotes","FAIL")
            Log.d("getGlobalEmotes","MESSAGE --> ${response.code()}")
            Log.d("getGlobalEmotes","MESSAGE--> ${response.message()}")
            emit(Response.Failure(Exception("Unable to delete message")))
        }
    }.catch { cause ->
          Log.d("getGlobalEmotes","caught error message ->${cause.message}")
          Log.d("getGlobalEmotes","caught error cause ->${cause.cause}")

        handleException(cause)
    }



}