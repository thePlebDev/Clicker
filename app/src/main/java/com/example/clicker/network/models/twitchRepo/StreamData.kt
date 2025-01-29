package com.example.clicker.network.models.twitchRepo


import android.util.Log
import com.example.clicker.presentation.home.models.StreamInfo
import com.google.gson.annotations.SerializedName

/**
 * - StreamData represents a broadcasters that the user follows and that are streaming live.
 *
 * @param id An ID that identifies the stream. You can use this ID later to look up the video on demand (VOD).
 * @param userId The ID of the user that’s broadcasting the stream.
 * @param userLogin The user’s login name.
 * @param userName 	The user’s display name.
 * @param gameId 	The ID of the category or game being played.
 * @param gameName 	The ID of the category or game being played.
 * @param type The type of stream. Possible values are: live
 * @param title The stream’s title. Is an empty string if not set.
 * @param viewerCount The number of users watching the stream.
 * @param startedAt The UTC date and time (in RFC3339 format) of when the broadcast began.
 * @param language 	The language that the stream uses.
 * @param thumbNailUrl 	A URL to an image of a frame from the last 5 minutes of the stream.
 * @param tags The tags applied to the stream.
 * @param tagIds legacy empty list. documentation states this will always be an empty list
 * @param isMature A Boolean value that indicates whether the stream is meant for mature audiences.
 * */
data class StreamData(
    val id: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("user_login")
    val userLogin: String,
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("game_id")
    val gameId: String,
    @SerializedName("game_name")
    val gameName: String,
    val type: String,
    val title: String,
    @SerializedName("viewer_count")
    val viewerCount: Int,
    @SerializedName("started_at")
    val startedAt: String,
    val language: String,
    @SerializedName("thumbnail_url")
    val thumbNailUrl: String,
    @SerializedName("tag_ids")
    val tagIds: List<String>,
    val tags: List<String>,
    @SerializedName("is_mature")
    val isMature: Boolean

)

/**
 * - toStreamInfo() is used to convert StreamData objects to [StreamInfo] objects
 * */
fun StreamData.toStreamInfo(): StreamInfo {
    return StreamInfo(
        streamerName = this.userLogin,
        streamTitle = this.title,
        gameTitle = this.gameName,
        views = this.viewerCount,
        url = this.thumbNailUrl,
        broadcasterId = this.userId
    )
}


/**
 *  - changeUrlWidthHeight() Is used to change the thumbNailUrl parameter on a [StreamData] object
 *  - You can read the full documentation on getting live streams,[HERE](https://dev.twitch.tv/docs/api/reference/#get-streams)
 * */
fun StreamData.changeUrlWidthHeight(aspectWidth: Int, aspectHeight: Int): StreamData {

    Log.d("StreamDataChangeUrlWidth","StreamData ->${this}")

    return copy(
        thumbNailUrl = thumbNailUrl.replace("{width}", "$aspectWidth")
            .replace("{height}", "$aspectHeight"),
        tags = if(tags == null) listOf() else tags
    )
}
