package com.example.clicker.util.objectMothers

import com.example.clicker.network.websockets.MessageType
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.network.websockets.MessageToken

class TwitchUserDataObjectMother private constructor() {


    companion object {

        private var twitchUserData: TwitchUserData = TwitchUserData(
            badgeInfo = null,
            badges = listOf(),
            clientNonce = null,
            color = "#000000",
            displayName = null,
            emotes = null,
            firstMsg = null,
            flags = null,
            id = null,
            mod = null,
            returningChatter = null,
            roomId = null,
            subscriber = false,
            tmiSentTs = null,
            turbo = false,
            userId = null,
            userType = null,
            messageType = MessageType.CLEARCHAT,
            bannedDuration = null,
            systemMessage = null,
            isMonitored =false
        )

        fun build(): TwitchUserData {
            return twitchUserData
        }
        fun addBadgeInfo(badgeInfo: String) = apply {
            twitchUserData = twitchUserData.copy(
                badgeInfo = badgeInfo
            )
        }
        fun addMessageTokens(messageTokenList: List<MessageToken>)= apply{
            twitchUserData = twitchUserData.copy(
                messageList = messageTokenList
            )
        }
        fun addBadges(badges: List<String>) = apply {
            twitchUserData = twitchUserData.copy(
                badges = badges
            )
        }
        fun addClientNonce(clientNonce: String) = apply {
            twitchUserData = twitchUserData.copy(
                clientNonce = clientNonce
            )
        }
        fun addColor(color: String) = apply {
            twitchUserData = twitchUserData.copy(
                color = color
            )
        }
        fun addDisplayName(displayName: String) = apply {
            twitchUserData = twitchUserData.copy(
                displayName = displayName
            )
        }
        fun addEmotes(emotes: String) = apply {
            twitchUserData = twitchUserData.copy(
                emotes = emotes
            )
        }
        fun addFirstMsg(firstMsg: String) = apply {
            twitchUserData.copy(
                firstMsg = firstMsg
            )
        }
        fun addFlags(flags: String) = apply {
            twitchUserData = twitchUserData.copy(
                flags = flags
            )
        }
        fun addId(id: String?) = apply {
            twitchUserData = twitchUserData.copy(
                id = id
            )
        }
        fun addMod(mod: String) = apply {
            twitchUserData = twitchUserData.copy(
                mod = mod
            )
        }
        fun addMonitored(isMonitored: Boolean) = apply {
            twitchUserData = twitchUserData.copy(
                isMonitored = isMonitored
            )
        }
        fun addReturningChatter(returningChatter: String) = apply {
            twitchUserData = twitchUserData.copy(
                returningChatter = returningChatter
            )
        }
        fun addRoomId(roomId: String) = apply {
            twitchUserData = twitchUserData.copy(
                roomId = roomId
            )
        }
        fun addSubscriber(subscriber: Boolean) = apply {
            twitchUserData = twitchUserData.copy(
                subscriber = subscriber
            )
        }
        fun addTmiSentTs(tmiSentTs: Long) = apply {
            twitchUserData = twitchUserData.copy(
                tmiSentTs = tmiSentTs
            )
        }
        fun addTurbo(turbo: Boolean) = apply {
            twitchUserData = twitchUserData.copy(
                turbo = turbo
            )
        }
        fun addUserId(userId: String) = apply {
            twitchUserData = twitchUserData.copy(
                userId = userId
            )
        }
        fun addUserType(userType: String?) = apply {
            twitchUserData = twitchUserData.copy(
                userType = userType
            )
        }
        fun addMessageType(messageType: MessageType) = apply {
            twitchUserData = twitchUserData.copy(
                messageType = messageType
            )
        }
        fun addBannedDuration(bannedDuration: Int?) = apply {
            twitchUserData = twitchUserData.copy(
                bannedDuration = bannedDuration
            )
        }
        fun addSystemMessage(systemMessage: String) = apply {
            twitchUserData = twitchUserData.copy(
                systemMessage = systemMessage
            )
        }
    }
}