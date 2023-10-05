package com.example.clicker.network.websockets.models


/**
 * Represents the state of the logged in User
 *
 * This class has no useful logic; it's just a documentation example.
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