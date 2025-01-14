package com.example.clicker.presentation.selfStreaming.domain

import com.example.clicker.util.NetworkAuthResponse
import kotlinx.coroutines.flow.Flow

/**
 * SelfStreaming is the interface that acts as the API for all the methods needed to Stream directly to Twitch's servers. You can
 * read more about Twitch's streaming API [HERE](https://dev.twitch.tv/docs/video-broadcast/)
 *
 * @property getStreamKey a function, when called with a [Authentication Token](https://dev.twitch.tv/docs/authentication/getting-tokens-oauth/#implicit-grant-flow)
 * and the App's [Client-ID](https://dev.twitch.tv/docs/authentication/register-app/), return the information needed to stream to Twitch
 *
 * */
interface SelfStreaming {


    /**
     * - **getStreamKey** a function, when called, will return the channel’s stream key. This stream key is required for streaming
     *
     * @return a [Flow] containing a String that represents the user's stream key
     * */
    fun getStreamKey(oAuthToken:String, clientId:String,broadcasterId:String): Flow<NetworkAuthResponse<String>>
}