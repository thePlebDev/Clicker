package com.example.clicker.network.repository.util

import com.example.clicker.network.interceptors.NoNetworkException
import com.example.clicker.network.interceptors.responseCodeInterceptors.Authentication401Exception
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.NetworkNewUserResponse
import com.example.clicker.util.NetworkResponse
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.FlowCollector


/**
 * handleException is a extension function on [FlowCollector]. This function is used to emit [Response] in response
 * to http requests
 *
 * @param cause the [Throwable] object that will be used to emit the proper response
 * */
 suspend fun <T>FlowCollector<Response<T>>.handleException(cause: Throwable) {
    when (cause) {
        is NoNetworkException -> {

            emit(Response.Failure(Exception("Network error, please try again later")))
        }
        is Authentication401Exception ->{
            emit(Response.Failure(Exception("Improper Authentication")))
        }

        else -> {
            emit(Response.Failure(Exception("Error! Please try again")))
        }
    }
}

/**
 * handleException is a extension function on [FlowCollector]. This function is used to emit [Response] in response
 * to http requests
 *
 * @param cause the [Throwable] object that will be used to emit the proper response
 * */


/**
 * handleException is a extension function on [FlowCollector]. This function is used to emit [Response] in response
 * to http requests
 *
 * @param cause the [Throwable] object that will be used to emit the proper response
 * */
suspend fun <T>FlowCollector<NetworkAuthResponse<T>>.handleNetworkAuthExceptions(cause: Throwable) {
    when (cause) {
        is NoNetworkException -> {
            emit(NetworkAuthResponse.NetworkFailure(Exception("Network error, please try again later")))
        }
        is Authentication401Exception ->{
            emit(NetworkAuthResponse.Auth401Failure(Exception("Authentication error, please try again later")))
        }

        else -> {
            emit(NetworkAuthResponse.Failure(Exception("Error! Please try again")))
        }
    }
}

suspend fun <T>FlowCollector<NetworkNewUserResponse<T>>.handleNetworkNewUserExceptions(cause: Throwable) {
    when (cause) {
        is NoNetworkException -> {
            emit(NetworkNewUserResponse.NetworkFailure(Exception("Network error, please try again later")))
        }
        is Authentication401Exception ->{
            emit(NetworkNewUserResponse.Auth401Failure(Exception("Authentication error, please try again later")))
        }

        else -> {
            emit(NetworkNewUserResponse.Failure(Exception("Error! Please try again")))
        }
    }
}
