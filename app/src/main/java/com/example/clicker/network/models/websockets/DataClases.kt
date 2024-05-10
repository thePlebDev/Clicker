package com.example.clicker.network.models.websockets

import androidx.compose.runtime.Immutable
import com.example.clicker.network.websockets.EmoteInText
import com.example.clicker.network.websockets.MessageType

/**
 * Represents the state of the logged in User
 *
 * This class is used primarily in the view for its mod status
 *
 * @property color   representing the color of the username
 * @property displayName  representing the name of the user which is displayed on screen and to other users
 * @property sub representing if the user is a subscriber or not
 * @property mod  representing if the user is a moderator or not
 * @constructor Creates the state of a loggedIn user.
 */
data class LoggedInUserData(
    val color: String?,
    val displayName: String,
    val sub: Boolean,
    val mod: Boolean
)

/**
 * Represents the state of the chatting user
 *
 * This class is used constantly to represent each individual chat message
 *
 * @property badgeInfo   representing the color of the username
 * @property badges  representing the name of the user which is displayed on screen and to other users
 * @property clientNonce representing if the user is a subscriber or not
 * @property color  representing if the user is a moderator or not
 * @property displayName   representing the color of the username
 * @property emotes  representing the name of the user which is displayed on screen and to other users
 * @property firstMsg representing if the user is a subscriber or not
 * @property flags  representing if the user is a moderator or not
 *
 * @property id   representing the color of the username
 * @property mod  representing the name of the user which is displayed on screen and to other users
 * @property returningChatter representing if the user is a subscriber or not
 * @property roomId  representing if the user is a moderator or not
 * @property subscriber   representing the color of the username
 * @property tmiSentTs  representing the name of the user which is displayed on screen and to other users
 * @property turbo representing if the user is a subscriber or not
 * @property userId  representing if the user is a moderator or not
 * @property roomId  representing if the user is a moderator or not
 *
 * @property userType   representing the color of the username
 * @property messageType  representing the name of the user which is displayed on screen and to other users
 * @property deleted representing if the user is a subscriber or not
 * @property banned  representing if the user is a moderator or not
 * @property bannedDuration  representing if the user is a moderator or not
 *
 * @property systemMessage representing a message sent by the Twitch irc server
 * @constructor Creates the state representing a chatting user.
 */
@Immutable
data class TwitchUserData(
    val badgeInfo: String?,
    val badges: String?,
    val clientNonce: String?,
    val color: String?,
    val displayName: String?,
    val emotes: String?,
    val firstMsg: String?,
    val flags: String?,
    val id: String?,
    val mod: String?,
    val returningChatter: String?,
    val roomId: String?,
    val subscriber: Boolean,
    val tmiSentTs: Long?,
    val turbo: Boolean,
    val userId: String?,
    val userType: String?,
    val messageType: MessageType,
    val deleted: Boolean = false,
    val banned: Boolean = false,
    val bannedDuration: Int? = null,
    val systemMessage: String? = null,
    val isMonitored:Boolean = false,
    val emoteInTextList:List<EmoteInText> = listOf()
)

/**
 * DEPRECIATED
 *
 * This was originally used for parsing a USERNOTICE message. However, I no longer user it and I
 * don't want to delete it encase I have to use it again
 */
data class TwitchUserAnnouncement(
    val badgeInfo: String,
    val badges: String,
    val color: String,
    val displayName: String,
    val emotes: String,
    val flags: String,
    val id: String,
    val login: String,
    val mod: Int,
    val msgId: String,
    val msgParamCumulativeMonths: Int,
    val msgParamMonths: Int,
    val msgParamMultimonthDuration: Int,
    val msgParamMultimonthTenure: Int,
    val msgParamShouldShareStreak: Int,
    val msgParamStreakMonths: Int,
    val msgParamSubPlanName: String,
    val msgParamSubPlan: String,
    val msgParamWasGifted: Boolean,
    val roomId: Long,
    val subscriber: Int,
    val systemMsg: String,
    val tmiSentTs: Long,
    val userId: Long,
    val userType: String
)

/**
 * Represents the state of the current chat rooms state
 *
 * This class is used primarily to notify the user of what the rules of the chat are
 *
 * @property emoteMode   representing if chatters are only restricted to emotes
 * @property followerMode  representing if only followers are allowed to follow or not
 * @property slowMode representing if the chat room is in slow mode or not
 * @property subMode  representing if only subscribers are allowed to chat or not
 * @constructor Creates the state for the current state of the chat room.
 */
data class RoomState(
    val emoteMode: Boolean,
    val followerMode: Boolean,
    val slowMode: Boolean,
    val subMode: Boolean,

    val followerModeDuration:Int,
    val slowModeDuration:Int
)