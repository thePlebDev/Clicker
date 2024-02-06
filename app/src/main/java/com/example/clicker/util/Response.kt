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

/**
 * Represents a network response and its specific error
 *
 * This class represents the 4 possible states of network responses in this application.
 * - [NetworkResponse.Loading]
 * - [NetworkResponse.Success]
 * - [NetworkResponse.Failure]
 * - [NetworkResponse.NetworkFailure]
 *
 * @param T the value returned from the network request
 */
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

/**
 * Represents a network response and specific response code errors
 *
 * This class represents the 5 possible states of network responses in this application.
 * - [NetworkAuthResponse.Loading]
 * - [NetworkAuthResponse.Success]
 * - [NetworkAuthResponse.Failure]
 * - [NetworkAuthResponse.NetworkFailure]
 * - [NetworkAuthResponse.Auth401Failure]
 *
 * @param T the value returned from the network request
 */
sealed class NetworkAuthResponse<out T> {

    object Loading : NetworkAuthResponse<Nothing>()

    data class Success<out T>(
        val data: T
    ) : NetworkAuthResponse<T>()

    data class Failure(
        val e: Exception
    ) : NetworkAuthResponse<Nothing>()

    data class NetworkFailure(
        val e: Exception
    ) : NetworkAuthResponse<Nothing>()

    data class Auth401Failure(
        val e: Exception
    ) : NetworkAuthResponse<Nothing>()
}



