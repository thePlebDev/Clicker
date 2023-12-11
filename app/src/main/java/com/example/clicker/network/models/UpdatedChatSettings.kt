package com.example.clicker.network.models

import com.google.gson.annotations.SerializedName

/**
 * The UpdatedChatSettings holds all the data that needs to be sent to the Twitch servers to update the current chat settings.
 * Can read more about it in the Twitch documentation, [HERE](https://dev.twitch.tv/docs/api/reference/#update-chat-settings)
 * */
data class UpdatedChatSettings(
    @SerializedName("emote_mode")
    val emoteMode: Boolean,
    @SerializedName("follower_mode")
    val followerMode: Boolean,
    @SerializedName("follower_mode_duration")
    val followerModeDuration: Int,
    @SerializedName("non_moderator_chat_delay")
    val nonModeratorChatDelay: Boolean,
    @SerializedName("non_moderator_chat_delay_duration")
    val nonModeratorChatDelayDuration: Int,
    @SerializedName("slow_mode")
    val slowMode: Boolean,
    @SerializedName("slow_mode_wait_time")
    val slowModeWaitTime: Int,
    @SerializedName("subscriber_mode")
    val subscriberMode: Boolean,
    @SerializedName("unique_chat_mode")
    val uniqueChatMode: Boolean
)

data class ChatSettingsResponse(
    val data: List<ChatSetting>
)

data class ChatSetting(
    val broadcaster_id: String,
    val moderator_id: String,
    val slow_mode: Boolean,
    val slow_mode_wait_time: Int,
    val follower_mode: Boolean,
    val follower_mode_duration: Int?,
    val subscriber_mode: Boolean,
    val emote_mode: Boolean,
    val unique_chat_mode: Boolean,
    val non_moderator_chat_delay: Boolean,
    val non_moderator_chat_delay_duration: Int?
)
