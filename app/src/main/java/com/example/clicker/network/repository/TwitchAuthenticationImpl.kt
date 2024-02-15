package com.example.clicker.network.repository

import android.util.Log
import com.example.clicker.network.clients.TwitchAuthenticationClient
import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.network.interceptors.NoNetworkException
import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.network.repository.util.handleException
import com.example.clicker.network.repository.util.handleNetworkAuthExceptions
import com.example.clicker.network.repository.util.handleNoNetworkException
import com.example.clicker.util.LogWrap
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.NetworkResponse
import com.example.clicker.util.Response
import com.example.clicker.util.logCoroutineInfo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject

/**
 * TwitchAuthenticationImpl the implementation class of [TwitchAuthentication]. This class contains all the methods
 * relating to Twitch's authentication system
 * */
class TwitchAuthenticationImpl @Inject constructor(
    private val twitchClient: TwitchAuthenticationClient
): TwitchAuthentication {


    override fun logout(clientId: String, token: String): Flow<Response<String>> = flow {
        emit(Response.Loading)
        Log.d("logoutResponse", "LOADING")

        val response = twitchClient.logout(clientId = clientId, token = token)
        if (response.isSuccessful) {
            Log.d("logoutResponse", "SUCCESS ->${response.message()}")
            emit(Response.Success("true"))
        } else {
            Log.d("logoutResponse", "message ->${response.message()}")
            Log.d("logoutResponse", "code ->${response.code()}")
            Log.d("logoutResponse", "FAILED ->${response.body()}")
            emit(Response.Failure(Exception("Error!, code: {${response.code()}}")))
        }
    }.catch { cause ->
        handleException(cause)
    }



    override suspend fun validateToken(
        url :String,
        token: String,
    ): Flow<NetworkAuthResponse<ValidatedUser>> = flow {
        logCoroutineInfo("CoroutineDebugging", "Fetching from remote")


        emit(NetworkAuthResponse.Loading)
        LogWrap.d(tag = "VALIDATINGTHETOKEN", message = "IT DO BE LogWrap LOADING")
        val response = twitchClient.validateToken(
            authorization = "OAuth $token"
        )

        if (response.isSuccessful) {
            LogWrap.d("VALIDATINGTHETOKEN", "LOGWRAP SUCCESS")
            emit(NetworkAuthResponse.Success(response.body()!!))
        } else {
            emit(NetworkAuthResponse.Failure(Exception("Error! Please login again")))
            Log.d("VALIDATINGTHETOKEN", "ERROR")
        }
    }.catch { cause ->
        LogWrap.d(tag = "VALIDATINGTHETOKEN", message = "cause --> ${cause.message}")

        handleNetworkAuthExceptions(cause)

    }
}


