package com.example.clicker.network.models.twitchClient

import com.google.gson.annotations.SerializedName

/**
 * GetModChannels represents the data that Twitch's servers send back from a `get moderated channels` request
 * - [HERE](https://dev.twitch.tv/docs/api/reference/#get-moderated-channels) is the official twitch documentation on getting moderated
 * channels.
 *
 * @param data a list of [GetModChannelsData] objects representing the individual channel the user moderates for
 * */
data class GetModChannels(
    val data:List<GetModChannelsData>
)

/**
 * GetModChannelsData represents a channel that a user moderates for
 *
 * @param broadcasterId  uniquely identifies the channel this user can moderate.
 * @param broadcasterLogin The channel’s login name
 * @param broadcasterName The channel's display name.
 * */
data class GetModChannelsData(
    @SerializedName("broadcaster_id")
    val broadcasterId: String,
    @SerializedName("broadcaster_login")
    val broadcasterLogin: String,
    @SerializedName("broadcaster_name")
    val broadcasterName: String,
)