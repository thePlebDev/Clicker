package com.example.clicker.domain

import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.FollowedLiveStreams
import com.example.clicker.network.models.toStreamInfo
import com.example.clicker.presentation.home.StreamInfo
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFollowedLiveStreamsUseCase @Inject constructor(
    private val twitchRepoImpl: TwitchRepo,
) {
    suspend operator fun invoke(authorizationToken:String, clientId:String, userId:String){

//        val items =twitchRepoImpl.getFollowedLiveStreams(
//            authorizationToken = authorizationToken,
//            clientId = clientId,
//            userId = userId
//        )
//
//        return items.map { response ->
//            followedLiveStreamToStreamInfo(response)
//        }
    }

    private fun followedLiveStreamToStreamInfo(stream:Response<FollowedLiveStreams>):Response<List<StreamInfo>>{
        return when(stream){
            is Response.Loading ->Response.Loading
            is Response.Success ->Response.Success(stream.data.data.map { it.toStreamInfo() })
            is Response.Failure ->Response.Failure(stream.e)
        }
    }
}