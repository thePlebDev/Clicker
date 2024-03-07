package com.example.clicker.network.repository

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.example.clicker.network.clients.TwitchAuthenticationClient
import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.network.interceptors.NoNetworkException
import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.network.repository.util.handleException
import com.example.clicker.network.repository.util.handleNetworkAuthExceptions
import com.example.clicker.network.repository.util.handleNetworkNewUserExceptions

import com.example.clicker.util.LogWrap
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.NetworkNewUserResponse
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


    override fun logout(clientId: String, token: String): Flow<NetworkAuthResponse<String>> = flow {
        Log.d("logoutResponse", "LOADING")

        val response = twitchClient.logout(clientId = clientId, token = token)
        if (response.isSuccessful) {
            Log.d("logoutResponse", "SUCCESS ->${response.message()}")
            emit(NetworkAuthResponse.Success("true"))
        } else {
            Log.d("logoutResponse", "message ->${response.message()}")
            Log.d("logoutResponse", "code ->${response.code()}")
            Log.d("logoutResponse", "FAILED ->${response.body()}")
            emit(NetworkAuthResponse.Failure(Exception("Error! Please try again")))
        }
    }.catch { cause ->
        handleNetworkAuthExceptions(cause)
    }



    override suspend fun validateToken(
        token: String,
    ): Flow<NetworkNewUserResponse<ValidatedUser>> = flow {
        logCoroutineInfo("CoroutineDebugging", "Fetching from remote")


        emit(NetworkNewUserResponse.Loading)
        LogWrap.d(tag = "VALIDATINGTHETOKEN", message = "IT DO BE LogWrap LOADING")
        val response = twitchClient.validateToken(
            authorization = "OAuth $token"
        )

        if (response.isSuccessful) {
            LogWrap.d("VALIDATINGTHETOKEN", "LOGWRAP SUCCESS")
            emit(NetworkNewUserResponse.Success(response.body()!!))
        } else {
            emit(NetworkNewUserResponse.Failure(Exception("Error! Please try again")))
            Log.d("VALIDATINGTHETOKEN", "ERROR")
        }
    }.catch { cause ->
        LogWrap.d(tag = "VALIDATINGTHETOKEN", message = "cause --> ${cause.message}")

        handleNetworkNewUserExceptions(cause)

    }
}



