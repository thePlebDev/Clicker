package com.example.clicker.presentation.selfStreaming.clients

import com.example.clicker.network.models.emotes.IndivBetterTTVEmote
import retrofit2.Response
import retrofit2.http.GET


/**
 * - **StreamToTwitchClient** is an interface that will act as the API to stream to the Twitch servers
 *
 * @property getStreamKey a function used to get the Stream key needed to stream to the Twitch ingestion server
 * */
interface StreamToTwitchClient {


    /**
     * - **getStreamKey** is a GET request to the Twitch servers to get a user's stream key
     * - You can read more about the stream key documentation, [HERE](https://dev.twitch.tv/docs/api/reference/#get-stream-key)
     * */
    @GET("streams/key")
    suspend fun getStreamKey(
    ): Response<StreamKeyResponse>
}


data class StreamKeyResponse(
    val data: List<StreamData>
)

data class StreamData(
    val stream_key: String
)