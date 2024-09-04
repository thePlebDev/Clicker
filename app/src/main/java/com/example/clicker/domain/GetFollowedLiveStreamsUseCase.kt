package com.example.clicker.domain

import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.twitchRepo.FollowedLiveStreams
import com.example.clicker.network.models.twitchRepo.toStreamInfo
import com.example.clicker.util.Response
import javax.inject.Inject

class GetFollowedLiveStreamsUseCase constructor(

) {
    suspend operator fun invoke(authorizationToken: String, clientId: String, userId: String) {
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

}