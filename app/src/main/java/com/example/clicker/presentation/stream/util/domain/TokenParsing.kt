package com.example.clicker.presentation.stream.util.domain

import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.network.websockets.models.MessageToken
import com.example.clicker.presentation.stream.util.TextCommands
import kotlinx.coroutines.flow.StateFlow

/**
 * TokenParsing is the interface that acts as the API for all the methods needed to parse [TextCommands] objects that the
 * user sends in chat
 *
 * @property runMonitorToken the function that will parse the [TextCommands] objects
 * */
interface TokenParsing {

    /**
     * this is a testing thing
     *
     * @param tokenCommand a [TextCommands] object the will determine what action the function will take
     * @param chatMessage a String representing what the user has typed and sent
     * @param isMod a Boolean determining if the user is a moderator or not
     * @param currentUsername a String representing the username of the currently logged in User
     * @param sendToWebSocket a function that will send the [chatMessage] to the websocket to be seen by other users
     * @param addMessageToListChats a function that will send the [chatMessage] to the UI so that is can be see user (not seen by others)
     * @param banUserSlashCommand a function called when a user types /ban
     * @param getUserId a function used to find a user in the chat session
     * @param unbanUserSlash a function called when a user types /unban
     * @param messageTokenList a List of [MessageToken] representing all the individual words the user has typed out
     * @param warnUser a function called when a user types /warn
     * */
    fun runMonitorToken(
        tokenCommand: TextCommands,
        chatMessage:String,
        isMod: Boolean,
        currentUsername:String,
        sendToWebSocket:(String) ->Unit,
        addMessageToListChats:(TwitchUserData)->Unit,
        banUserSlashCommand:(String,String)->Unit,
        getUserId:((TwitchUserData)->Boolean)->String?,
        unbanUserSlash:(String)->Unit,
        messageTokenList: List<MessageToken>,
        warnUser:(String,String,String) ->Unit,

        )
}