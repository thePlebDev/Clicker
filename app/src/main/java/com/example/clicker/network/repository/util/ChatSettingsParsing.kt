package com.example.clicker.network.repository.util

import com.example.clicker.network.models.twitchStream.ChatSettingsData


class ChatSettingsParsing {

    fun parseChatSettingsData(stringToParse: String): ChatSettingsData {
        return ChatSettingsData(
            slowMode = parseSlowModeValue(stringToParse),
            emoteMode = parseEmoteModeValue(stringToParse),
            followerMode = parseFollowerModeValue(stringToParse),
            subscriberMode = parseSubscriberModeValue(stringToParse),
            followerModeDuration = parseFollowerModeDurationValue(stringToParse),
            slowModeWaitTime = parseSlowModeDurationValue(stringToParse)
        )
    }

    fun parseEmoteModeValue(stringToParse:String):Boolean{
        val emoteModeRegex ="\"emote_mode\":([^,]+)".toRegex()
        val emoteModeValue  = emoteModeRegex.find(stringToParse)?.groupValues?.get(1)?.replace("\"","") ?:""
        return emoteModeValue == "true"

    }
    fun parseSubscriberModeValue(stringToParse:String):Boolean{
        val subscriberModeRegex ="\"subscriber_mode\":([^,]+)".toRegex()
        val subscriberModeValue  = subscriberModeRegex.find(stringToParse)?.groupValues?.get(1)?.replace("\"","") ?:""
        return subscriberModeValue == "true"

    }
    fun parseFollowerModeValue(stringToParse:String):Boolean{
        val followerModeRegex ="\"follower_mode\":([^,]+)".toRegex()
        val followerModeValue  = followerModeRegex.find(stringToParse)?.groupValues?.get(1)?.replace("\"","") ?:""
        return followerModeValue == "true"

    }
    fun parseFollowerModeDurationValue(stringToParse:String):Int?{
        val followerModeDurationRegex ="\"follower_mode_duration_minutes\":([^,]+)".toRegex()
        val followerModeDurationValue  = followerModeDurationRegex.find(stringToParse)?.groupValues?.get(1)?.replace("\"","") ?:""
        val returnValue = if(followerModeDurationValue == "null") null else followerModeDurationValue.toInt()
        return returnValue

    }

    fun parseSlowModeValue(stringToParse:String):Boolean{
        val slowModeRegex ="\"slow_mode\":([^,]+)".toRegex()
        val slowModeValue  = slowModeRegex.find(stringToParse)?.groupValues?.get(1)?.replace("\"","") ?:""
        return slowModeValue == "true"
    }
    fun parseSlowModeDurationValue(stringToParse:String):Int?{
        val slowModeDurationRegex ="\"slow_mode_wait_time_seconds\":([^,]+)".toRegex()
        val slowModeDurationValue  = slowModeDurationRegex.find(stringToParse)?.groupValues?.get(1)?.replace("\"","") ?:""
        val returnValue = if(slowModeDurationValue == "null") null else slowModeDurationValue.toInt()
        return returnValue

    }
}