package com.example.clicker.network.websockets

import android.util.Log

class ParsingEngine {
//THIS IS TO CLEAR EVERYTHING @room-id=520593641;tmi-sent-ts=1696019043159 :tmi.twitch.tv CLEARCHAT #theplebdev
    //THIS IS TO BAN USER @room-id=520593641;target-user-id=949335660;tmi-sent-ts=1696019132494 :tmi.twitch.tv CLEARCHAT #theplebdev :meanermeeny
    //todo: SO I THINK WE NEED TO PARSE OUT THE target-user-id=949335660
    //TODO: IF THAT IS FOUND WE NOW KNOW THAT THIS IS A BAN AND NOT A CLEAR ALL MESSAGES REQUEST
    public fun clearChat(text:String,streamerChannelName:String):TwitchUserData{
    val banDurationPattern = "ban-duration=(\\d+)".toRegex()

    val banDurationMatch = banDurationPattern.find(text)
    val foundDuration = banDurationMatch?.groupValues?.last()?.toInt()

    val userData: TwitchUserData



    val pattern2 = "#$streamerChannelName$".toRegex()
    val matcher2 = pattern2.find(text)
    val found = matcher2?.value
    if(found !=null){
         userData = TwitchUserData(
            badgeInfo = null,
            badges = null,
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
            userType = "Connected to chat!",
            messageType = MessageType.CLEARCHAT,
            bannedDuration = foundDuration
        )

    }else{
        val pattern3 = ":(\\w+)\\s*$".toRegex()

// Use a Matcher to find the pattern in the input string
        val matcher3 = pattern3.find(text)
        val username = matcher3?.groupValues?.last()
         userData = TwitchUserData(
            badgeInfo = null,
            badges = null,
            clientNonce = null,
            color = "#000000",
            displayName = username,
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
            userType = "Connected to chat!",
            messageType = MessageType.CLEARCHAT,
            bannedDuration = foundDuration
        )

    }
    return userData

    }

    fun clearChatTesting(text:String,streamerName:String):TwitchUserData{

        //THIS IS TO CLEAR EVERYTHING @room-id=520593641;tmi-sent-ts=1696019043159 :tmi.twitch.tv CLEARCHAT #theplebdev
    //THIS IS TO BAN USER @room-id=520593641;target-user-id=949335660;tmi-sent-ts=1696019132494 :tmi.twitch.tv CLEARCHAT #theplebdev :meanermeeny
        val streamerNamePattern = "$streamerName$".toRegex()


        val clearChat = streamerNamePattern.find(text)
        val channelNameFound = clearChat?.value

        if(channelNameFound == null){
            return banUserParsing(text)

        }else{
            //todo: this should return a TwitchUserData object
            //todo: 1) Basically I want this to send a message to clear the chat
            //todo: 2) Basically I want this to display a message stating that the chat was cleared
            return clearChatParsing(channelNameFound)
        }


    }
    private fun banUserParsing(text: String):TwitchUserData{
        //THIS IS TO BAN USER @room-id=520593641;target-user-id=949335660;tmi-sent-ts=1696019132494 :tmi.twitch.tv CLEARCHAT #theplebdev :meanermeeny
        //todo: I want this to modify the messages like it currently is but also sent a little message stating what user was banned
        //todo: I need to parse out the userId. target-user-id=949335660
        val banUserPattern = "[a-zA-Z0-9_]+$".toRegex()
        val bannedUserIdPattern = "target-user-id=(\\d+)".toRegex()

        val bannedUserUsername = banUserPattern.find(text)
        val bannedUserId = bannedUserIdPattern.find(text)


        val usernameFound = bannedUserUsername?.value ?: "User"
        val bannedUserIdFound = bannedUserId?.groupValues?.get(1)

        return TwitchUserData(
            badgeInfo = null,
            badges = null,
            clientNonce = null,
            color = "#000000",
            displayName = usernameFound,
            emotes = null,
            firstMsg = null,
            flags = null,
            id = bannedUserIdFound,
            mod = null,
            returningChatter = null,
            roomId = null,
            subscriber = false,
            tmiSentTs = null,
            turbo = false,
            userId = null,
            userType = "$usernameFound banned by moderator",
            messageType = MessageType.CLEARCHAT,
            bannedDuration = null
        )

    }
    private fun clearChatParsing(channelName:String):TwitchUserData{
        return TwitchUserData(
            badgeInfo = null,
            badges = null,
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
            userType = "Chat cleared by moderator",
            messageType = MessageType.CLEARCHAT,
            bannedDuration = null
        )


    }

}