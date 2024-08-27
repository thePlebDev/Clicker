package com.example.clicker.network.domain

import com.example.clicker.network.clients.VODData
import com.example.clicker.network.models.twitchStream.ChatSettings
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow


/**
 * TwitchRepo is the interface that acts as the API for all the methods needed to interact with Twitch's base API. So any
 * end point that is not related to a individual users stream
 *
 * @property getFollowedLiveStreams a function meant to be called to get the logged in user's live channels
 * @property getModeratedChannels a function meant to be called to get the logged in user's live and offline channels they mod for
 * */
interface TwitchVODRepo {




    /**
     * - getChannelVODs is used to get all the channels VODs
     *
     * @param oAuthToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param userId a String used to represent the user we are getting VODS for
     * */
    suspend fun getChannelVODs(oAuthToken: String, clientId: String, userId: String): Flow<Response<VODData>>
}