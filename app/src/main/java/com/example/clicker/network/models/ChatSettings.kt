package com.example.clicker.network.models

import com.google.gson.annotations.SerializedName

data class ChatSettings(
    val data: List<ChatSettingsData>
)

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

// data class UpdateChatSettings(
//    val emote_mode: Boolean,
//    val follower_mode: Boolean,
//    val follower_mode_duration: Int,
//    val non_moderator_chat_delay: Boolean,
//    val non_moderator_chat_delay_duration: Int,
//    val slow_mode: Boolean,
//    val slow_mode_wait_time: Int,
//    val subscriber_mode: Boolean,
//    val unique_chat_mode: Boolean
// )
data class UpdateChatSettings(
    val emote_mode: Boolean,
    val follower_mode: Boolean,
    val slow_mode: Boolean,
    val subscriber_mode: Boolean
)

// The data ChatSettingsData is representing
// {
//    "broadcaster_id": "26610234",
//    "slow_mode": false,
//    "slow_mode_wait_time": null,
//    "follower_mode": true,
//    "follower_mode_duration": 1800,
//    "subscriber_mode": false,
//    "emote_mode": false,
//    "unique_chat_mode": false
// }