package com.example.clicker.utility

import com.example.clicker.network.websockets.MessageType
import com.example.clicker.network.websockets.TwitchUserData

class TwitchUserDataObjectMother private constructor() {


    companion object{

        private val twitchUserData: TwitchUserData =TwitchUserData(
            badgeInfo = null,
            badges = null,
            clientNonce = null,
            color =null,
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
            bannedDuration = null
        )

        fun build():TwitchUserData{
            return twitchUserData
        }
        fun addBadgeInfo(badgeInfo:String)=apply{
            twitchUserData.copy(
                badgeInfo = badgeInfo
            )
        }
        fun addBadges(badges:String)=apply{
            twitchUserData.copy(
                badges = badges
            )
        }
        fun addClientNonce(clientNonce:String)=apply{
            twitchUserData.copy(
                clientNonce = clientNonce
            )
        }
        fun addColor(color:String)=apply{
            twitchUserData.copy(
                color = color
            )
        }
        fun addDisplayName(displayName:String)=apply{
            twitchUserData.copy(
                displayName = displayName
            )
        }
        fun addEmotes(emotes:String)=apply{
            twitchUserData.copy(
                emotes = emotes
            )
        }
        fun addFirstMsg(firstMsg:String)=apply{
            twitchUserData.copy(
                firstMsg = firstMsg
            )
        }
        fun addFlags(flags:String)=apply{
            twitchUserData.copy(
                flags = flags
            )
        }
        fun addId(id:String)=apply{
            twitchUserData.copy(
                id = id
            )
        }
        fun addMod(mod:String)=apply{
            twitchUserData.copy(
                mod = mod
            )
        }
        fun addReturningChatter(returningChatter:String)=apply{
            twitchUserData.copy(
                returningChatter = returningChatter
            )
        }
        fun addRoomId(roomId:String)=apply{
            twitchUserData.copy(
                roomId = roomId
            )
        }
        fun addSubscriber(subscriber:Boolean)=apply{
            twitchUserData.copy(
                subscriber = subscriber
            )
        }
        fun addTmiSentTs(tmiSentTs:Long)=apply{
            twitchUserData.copy(
                tmiSentTs = tmiSentTs
            )
        }
        fun addTurbo(turbo:Boolean)=apply{
            twitchUserData.copy(
                turbo = turbo
            )
        }
        fun addUserId(userId:String)=apply{
            twitchUserData.copy(
                userId = userId
            )
        }
        fun addUserType(userType:String)=apply{
            twitchUserData.copy(
                userType = userType
            )
        }
        fun addMessageType(messageType:MessageType)=apply{
            twitchUserData.copy(
                messageType = messageType
            )
        }
        fun addBannedDuration(bannedDuration:Int)=apply{
            twitchUserData.copy(
                bannedDuration = bannedDuration
            )
        }

    }
}