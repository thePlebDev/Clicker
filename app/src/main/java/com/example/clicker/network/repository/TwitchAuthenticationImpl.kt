package com.example.clicker.network.repository

import android.util.Log
import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.network.interceptors.NoNetworkException
import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.util.LogWrap
import com.example.clicker.util.Response
import com.example.clicker.util.logCoroutineInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.net.UnknownHostException
import javax.inject.Inject

class TwitchAuthenticationImpl @Inject constructor(
    private val twitchClient: TwitchClient
): TwitchAuthentication {

    override fun logout(clientId: String, token: String): Flow<Response<String>> = flow {
        emit(Response.Loading)

        val response = twitchClient.logout(clientId = clientId, token = token)
        if (response.isSuccessful) {
            Log.d("logoutResponse", "SUCCESS ->${response.message()}")
            emit(Response.Success("true"))
        } else {
            Log.d("logoutResponse", "FAILED ->${response.message()}")
            emit(Response.Failure(Exception("Error!, code: {${response.code()}}")))
        }
    }.catch { cause ->
        Log.d("GETTINGLIVESTREAMS", "CAUSE IS CAUSE")
        // Log.d("GETTINGLIVESTREAMS","RUNNING THE METHOD USER--> $user ")
        if (cause is UnknownHostException) {
            emit(
                Response.Failure(
                    Exception("Network Error! Please check your connection and try again")
                )
            )
        } else {
            emit(Response.Failure(Exception("Logout Error! Please try again")))
        }
    }

    override suspend fun validateToken(token: String): Flow<Response<ValidatedUser>> = flow {
        logCoroutineInfo("CoroutineDebugging", "Fetching from remote")


        emit(Response.Loading)
        LogWrap.d(tag = "VALIDATINGTHETOKEN", message = "IT DO BE LogWrap LOADING")
        val response = twitchClient.validateToken(
            authorization = "OAuth $token"
        )
        if (response.isSuccessful) {
            LogWrap.d("VALIDATINGTHETOKEN", "LOGWRAP SUCCESS")
            emit(Response.Success(response.body()!!))
        } else {
            emit(Response.Failure(Exception("Error! Please login again")))
            Log.d("VALIDATINGTHETOKEN", "ERROR")
        }
    }.catch { cause ->
        if(cause is NoNetworkException){
            Log.d("NoNetworkException","NO NETWORK AVALIABLE")
            emit(
                Response.Failure(
                    Exception("Network Error! Please check your connection and try again")
                )
            )
        }
        if (cause is UnknownHostException) {
            emit(
                Response.Failure(
                    Exception("Network Error! Please check your connection and try again")
                )
            )
        } else {
            emit(Response.Failure(Exception("Error! Please try again")))
        }
    }
}