package com.example.clicker.network.repository

import android.util.Log
import com.example.clicker.network.clients.TwitchModClient
import com.example.clicker.network.domain.TwitchModRepo
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

data class ClickedUnbanRequestInfo(
    val profileImageURL:String,
    val profileDescription:String,
    val profileCreatedAt:String,
    val displayName:String
)
class TwitchModRepoImpl @Inject constructor(
    private val twitchModClient: TwitchModClient
) : TwitchModRepo {

    //I first need to get the loading UI
    // then the failed/try UI
    // then the success UI



    override suspend fun getUserInformation(
        authorizationToken: String,
        clientId: String,
        userId: String
    ): Flow<Response<ClickedUnbanRequestInfo>> = flow{
        emit(Response.Loading)
        val response = twitchModClient.getUserInformation(
            authorizationToken = "Bearer $authorizationToken",
            clientId = clientId,
            userId = userId
        )
        if (response.isSuccessful) {
            val body = response.body()?.data ?: listOf()
            Log.d("getUserInformation", "SUCCESS ->${response.message()}")
            Log.d("getUserInformation", "description ->${body[0].description}")
            Log.d("getUserInformation", "profileImageURL ->${body[0].profile_image_url}")
            Log.d("getUserInformation", "createdAt ->${body[0].created_at}")
            val createdAt = body[0].created_at.replace("T"," ").replace("Z"," UTC")
            emit(Response.Success(
                ClickedUnbanRequestInfo(
                    profileImageURL =body[0].profile_image_url,
                    profileDescription =body[0].description,
                    displayName =body[0].display_name,
                    profileCreatedAt = createdAt
                )
            ))
        } else {
            Log.d("getUserInformation", "FAILED ->${response.body()}")
            Log.d("getUserInformation", "message ->${response.message()}")
            Log.d("getUserInformation", "code ->${response.code()}")
            emit(Response.Failure(Exception("Error! Please try again")))
        }
    }.catch {
        emit(Response.Failure(Exception("Error! Please try again")))
    }
}

