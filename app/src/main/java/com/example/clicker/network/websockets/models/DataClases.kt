package com.example.clicker.network.websockets.models

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
    val color:String?,
    val displayName: String,
    val sub:Boolean,
    val mod:Boolean
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
    var userType: String?,
    val messageType: MessageType,
    val deleted:Boolean = false,
    val banned:Boolean = false,
    val bannedDuration:Int? = null,
    val systemMessage:String? = null
)