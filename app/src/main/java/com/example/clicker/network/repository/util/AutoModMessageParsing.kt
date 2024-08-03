package com.example.clicker.network.repository.util

import com.example.clicker.network.websockets.parseStatusType
import javax.annotation.concurrent.Immutable

class AutoModMessageParsing {
    fun parseMessageId(stringToParse:String):String?{
        val messageIdRegex = "\"message_id\":([^=]+)".toRegex()
        val allFoundMessageIdList = messageIdRegex.findAll(stringToParse).toList()
        return if(allFoundMessageIdList.size >=2){
            val messageIdNoQuotes=allFoundMessageIdList[1].groupValues[1].replace("\"","")
            val newMessageRegex = "[^,]+".toRegex()
            val desiredMessageId = newMessageRegex.find(messageIdNoQuotes)?.value
            desiredMessageId
        }else{
            null
        }
    }
    fun checkUpdateStatus(
        text:String,
        messageId: String
    ): AutoModMessageUpdate {
        val type = parseStatusType(text)
        if(type =="denied"){
            return AutoModMessageUpdate(
                approved = false,
                messageId = messageId
            )
        }else{
            return AutoModMessageUpdate(
                approved = true,
                messageId = messageId
            )
        }
    }

    /**
     * parseAutoModQueueMessage is a function meant to parse out the username, category and fullText from the
     * message that is sent from the Twitch servers.
     *
     * @param stringToParse represents what was sent from the twitch servers
     * @return [AutoModQueueMessage] object
     * */
    fun parseAutoModQueueMessage(stringToParse:String): AutoModQueueMessage {
        val usernameRegex = "\"user_name\":([^,]+)".toRegex()
        val textRegex = "\"text\":([^,]+)".toRegex()
        val categoryRegex = "\"category\":([^,]+)".toRegex()
        val userIdRegex = "\"user_id\":([^,]+)".toRegex()

        //extra parsing because there is multiple `message_id`
        val messageIdRegex = "\"message_id\":([^=]+)".toRegex()
        val allFoundMessageIdList = messageIdRegex.findAll(stringToParse).toList()
        val messageIdNoQuotes=allFoundMessageIdList[1].groupValues[1].replace("\"","")
        val newMessageRegex = "[^,]+".toRegex()
        val desiredMessageId = newMessageRegex.find(messageIdNoQuotes)?.value


        val username = usernameRegex.find(stringToParse)?.groupValues?.get(1)?.replace("\"","")
        val fullText = textRegex.find(stringToParse)?.groupValues?.get(1)?.replace("\"","")
        val category = categoryRegex.find(stringToParse)?.groupValues?.get(1)?.replace("\"","")
        val userId = userIdRegex.find(stringToParse)?.groupValues?.get(1)?.replace("\"","")
        val messageId = desiredMessageId

        return AutoModQueueMessage(
            username = username ?:"",
            fullText = fullText ?:"",
            category = category ?:"",
            userId = userId ?:"",
            messageId = messageId ?:""
        )
    }
}

/**
 * AutoModQueueMessage is a data class that will represent the parsed data send from
 * [Notification message Websocket](https://dev.twitch.tv/docs/eventsub/handling-websocket-events/#notification-message)
 *
 * @param username represents the user that sent the [fullText] message
 * @param fullText represents the message that the user sent
 * @param category represents the category that the user's [fullText] message falls under, ie swearing
 * @param messageId represents the unique ID of the user's flagged message
 * @param userId represents the unique ID of the user sending the message
 * */
@Immutable
data class AutoModQueueMessage(
    val username:String ="",
    val fullText:String ="",
    val category: String = "",
    val messageId:String,
    val userId:String,
    val approved:Boolean? = null, // changing this to val
    val swiped:Boolean = false, // changing this to  val
)

data class AutoModMessageUpdate(
    val approved: Boolean,
    val messageId: String
)