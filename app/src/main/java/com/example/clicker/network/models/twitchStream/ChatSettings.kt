package com.example.clicker.network.models.twitchStream

import com.google.gson.annotations.SerializedName

data class ChatSettings(
    val data: List<ChatSettingsData>
)

/**
 * Represent the data that the Switches in [ChatSettingsContainer][com.example.clicker.presentation.stream.views.ChatSettingsContainer]
 * are manipulating
 * */
data class ChatSettingsData(
    @SerializedName("slow_mode")
    val slowMode: Boolean,
    @SerializedName("slow_mode_wait_time")
    val slowModeWaitTime: Int?,
    @SerializedName("follower_mode")
    val followerMode: Boolean, //
    @SerializedName("follower_mode_duration")
    val followerModeDuration: Int?, //
    @SerializedName("subscriber_mode")
    val subscriberMode: Boolean,
    @SerializedName("emote_mode") //
    val emoteMode: Boolean,
//    @SerializedName("unique_chat_mode")
//    val uniqueChatMode: Boolean
)

/**
 * Represent the data that is sent as the body to the endpoint: PATCH https://api.twitch.tv/helix/chat/settings
 * - Read more in the documentation, [HERE](https://dev.twitch.tv/docs/api/reference/#update-chat-settings)
 * */
data class UpdateChatSettings(
    val emote_mode: Boolean,
    val follower_mode: Boolean,
    val slow_mode: Boolean,
    val subscriber_mode: Boolean
)

