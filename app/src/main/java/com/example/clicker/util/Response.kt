package com.example.clicker.util

/**
 * Represents a network response
 *
 * This class represents the 3 possible states of network responses in this application.
 * - [Response.Loading]
 * - [Response.Success]
 * - [Response.Failure]
 *
 * @param T the value returned from the network request
 */
sealed class Response<out T> {

    object Loading : Response<Nothing>()

    data class Success<out T>(
        val data: T
    ) : Response<T>()

    data class Failure(
        val e: Exception
    ) : Response<Nothing>()
}

sealed class NetworkResponse<out T> {

    object Loading : NetworkResponse<Nothing>()

    data class Success<out T>(
        val data: T
    ) : NetworkResponse<T>()

    data class Failure(
        val e: Exception
    ) : NetworkResponse<Nothing>()

    data class NetworkFailure(
        val e: Exception
    ) : NetworkResponse<Nothing>()
}