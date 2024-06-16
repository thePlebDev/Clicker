package com.example.clicker.network.websockets

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.clicker.R
import com.example.clicker.network.domain.TwitchEventSubscriptionWebSocket
import com.example.clicker.network.models.twitchStream.ChatSettingsData
import com.example.clicker.network.repository.util.ModActionParsing
import com.example.clicker.presentation.modView.ModActionData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TwitchEventSubWebSocket @Inject constructor(
    private val modActionParsing:ModActionParsing
): TwitchEventSubscriptionWebSocket, WebSocketListener() {
    private var client: OkHttpClient = OkHttpClient.Builder().build()
    var webSocket: WebSocket? = null

    private val _parsedSessionId: MutableStateFlow<String?> = MutableStateFlow(null)
    // The UI collects from this StateFlow to get its state updates
    override val parsedSessionId: StateFlow<String?> = _parsedSessionId

    private val _autoModMessageQueue: MutableStateFlow<AutoModQueueMessage?> = MutableStateFlow(null)
    // The UI collects from this StateFlow to get its state updates
    override val autoModMessageQueue: StateFlow<AutoModQueueMessage?> = _autoModMessageQueue

    private val _messageIdForAutoModQueue: MutableStateFlow<AutoModMessageUpdate?> = MutableStateFlow(null)
    // The UI collects from this StateFlow to get its state updates
    override val messageIdForAutoModQueue: StateFlow<AutoModMessageUpdate?> = _messageIdForAutoModQueue

    private val _updatedChatSettingsData: MutableStateFlow<ChatSettingsData?> = MutableStateFlow(null)
    // The UI collects from this StateFlow to get its state updates
    override val updatedChatSettingsData: StateFlow<ChatSettingsData?> = _updatedChatSettingsData

    private val _modActions: MutableStateFlow<ModActionData?> = MutableStateFlow(null)
    override val modActions: StateFlow<ModActionData?> = _modActions



    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)

        Log.d("TwitchEventSubWebSocket","onOpen() response ->$response")

    }


    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        // this needs to run only when the notifiction type is session_welcome
        //TODO: THIS SHOULD CHECK IF IT IS A WELCOMING MESSAGE. similar to notificationTypeIsNotification()
         //TODO: THIS SHOULD NOT BE DOING THIS EVERY TIME
        //if()
        Log.d("EventWebsocketMessage", text)
        if(notificationTypeIsWelcome(text)){
            val parsedSessionId = parseEventSubWelcomeMessage(text)
            _parsedSessionId.tryEmit(parsedSessionId)
        }

        if(notificationTypeIsNotification(text)){
            //Log.d("TwitchEventSubWebSocket","notificationTypeIsNotification  ->${parseAutoModQueueMessage(text)}")
            val subscriptionType = parseSubscriptionType(text)

            //todo: These could probably be stored in maps
            when (subscriptionType) {
                "automod.message.hold" -> {
                    _autoModMessageQueue.tryEmit(parseAutoModQueueMessage(text))
                }
                "channel.moderate" -> {
                    Log.d("ChannelModerateParsing", "TIME TO PARSE!!!!")
                    val action = modActionParsing.parseActionFromString(text)
                    modActionParsing.whenAction(
                        action =action,
                        stringToParse =text,
                        emitData ={modActionData ->_modActions.tryEmit(modActionData)}
                    )
//                    whenAction(
//                        action,text
//                    )
                }
                "automod.message.update" -> {
                    val messageId = parseMessageId(text) ?: ""
                    val messageUpdate = checkUpdateStatus(text, messageId)
                    _messageIdForAutoModQueue.tryEmit(messageUpdate)
                }
                "channel.chat_settings.update" -> {
                    val parsedChatSettingsData = parseChatSettingsData(text)
                    _updatedChatSettingsData.tryEmit(parsedChatSettingsData)
                }
            }

        }
        Log.d("TwitchEventSubWebSocket","onMessage() text ->$text")
        Log.d("createAnotherSubscriptionEvent","onMessage text -->$text")

    }
    fun checkUpdateStatus(
        text:String,
        messageId: String
    ):AutoModMessageUpdate{
        val type =parseStatusType(text)
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

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        Log.d("TwitchEventSubWebSocket","onClosing()")

    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        Log.d("TwitchEventSubWebSocket","onClosed()")

    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.d("TwitchEventSubWebSocket","onFailure()")
        Log.d("TwitchEventSubWebSocket","response --> ${response?.message}")
        Log.d("TwitchEventSubWebSocket","body --> ${response?.body}")
    }

     override fun newWebSocket() {
         if(webSocket!=null){
             close()
             createNewSocket()
         }else{
             createNewSocket()
         }

    }

    override fun closeWebSocket(){
        webSocket?.close(1009,"Bye")
    }
    private fun createNewSocket(){
        val request: Request = Request.Builder()
            .url("wss://eventsub.wss.twitch.tv/ws")
            .build()
        client = OkHttpClient.Builder()
            .readTimeout(1000, TimeUnit.MILLISECONDS)
            .writeTimeout(1000, TimeUnit.MILLISECONDS)
            .build()

        webSocket = client.newWebSocket(request, this)
    }
    private fun close() {
        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
        client.dispatcher.executorService.shutdown()
        webSocket?.close(1009, "Manually closed ")
        webSocket = null
    }

    /***************BELOW IS ALL THE PARSING FOR THE channel.moderate***********************/
//1) get the action(DONE) 2) determine the action 3) parse and send data to UI



    private fun whenAction(action:String?,stringToParse: String){
        when(action){
            "untimeout" ->{
                //moderator name, user id, username
                Log.d("TimeoutActions","untimeout")
                val data = ModActionData(
                    title = modActionParsing.getUserName(stringToParse),
                    message="Timeout removed by ${modActionParsing.getModeratorUsername(stringToParse)}",
                    iconId = R.drawable.baseline_check_24
                )
                _modActions.tryEmit(data)

            }
            "timeout" ->{
                Log.d("TimeoutActions","text ->$stringToParse")
                val data = ModActionData(
                    title = modActionParsing.getUserName(stringToParse) ,
                    message="Timed out by ${modActionParsing.getModeratorUsername(stringToParse)} for ${modActionParsing.getExpiresAt(stringToParse)} seconds. ${modActionParsing.getReason(stringToParse)}",
                    iconId = R.drawable.time_out_24
                )
                _modActions.tryEmit(data)

            }
            "ban"->{
                println("BAN ACTION")
                val data = ModActionData(
                    title = modActionParsing.getUserName(stringToParse),
                    message="Banned by ${modActionParsing.getModeratorUsername(stringToParse)}. ${modActionParsing.getReason(stringToParse)}",
                    iconId = R.drawable.clear_chat_alt_24
                )
                _modActions.tryEmit(data)
            }
            "unban" ->{
                val data = ModActionData(
                    title = modActionParsing.getUserName(stringToParse),
                    message="Unbanned  by ${modActionParsing.getModeratorUsername(stringToParse)}.",
                    iconId = R.drawable.baseline_check_24
                )
                _modActions.tryEmit(data)

            }
            "delete"->{
                val data = ModActionData(
                    title = modActionParsing.getUserName(stringToParse),
                    message="Message deleted by ${modActionParsing.getModeratorUsername(stringToParse)}.",
                    iconId = R.drawable.delete_outline_24,
                    secondaryMessage = modActionParsing.getMessageBody(stringToParse)
                )
                _modActions.tryEmit(data)

            }

            "remove_blocked_term"->{
                val data = ModActionData(
                    title = modActionParsing.getBlockedTerms(stringToParse),
                    message="Removed as Blocked Term by ${modActionParsing.getModeratorUsername(stringToParse)}.",
                    iconId = R.drawable.lock_open_24,
                )
                _modActions.tryEmit(data)
            }

            "add_blocked_term"->{
                val data = ModActionData(
                    title = modActionParsing.getBlockedTerms(stringToParse),
                    message="Added as Blocked Term by ${modActionParsing.getModeratorUsername(stringToParse)}.",
                    iconId = R.drawable.lock_24,
                )
                _modActions.tryEmit(data)

            }
            "emoteonly"->{
                val data = ModActionData(
                    title = "Emote-Only Chat",
                    message="Enabled by ${modActionParsing.getModeratorUsername(stringToParse)}.",
                    iconId = R.drawable.emote_face_24,
                )
                _modActions.tryEmit(data)

            }
            "followers"->{
                val data = ModActionData(
                    title = "Follower-Only Chat",
                    message="Enabled with ${modActionParsing.getFollowerTime(stringToParse)} min following age, by ${modActionParsing.getModeratorUsername(stringToParse)}.",
                    iconId = R.drawable.favorite_24,
                )
                _modActions.tryEmit(data)
            }
            "slow" ->{
                val data = ModActionData(
                    title = "Slow Mode",
                    message="Enabled with ${modActionParsing.getSlowModeTime(stringToParse)}s wait time, by ${modActionParsing.getModeratorUsername(stringToParse)}.",
                    iconId = R.drawable.baseline_hourglass_empty_24,
                )
                _modActions.tryEmit(data)


            }
            "slowoff"->{
                val data = ModActionData(
                    title = "Slow Mode Off",
                    message="Removed by ${modActionParsing.getModeratorUsername(stringToParse)}.",
                    iconId = R.drawable.baseline_hourglass_empty_24,
                )
                _modActions.tryEmit(data)

            }
            "followersoff"->{
                val data = ModActionData(
                    title = "Followers-Only Off",
                    message="Removed by ${modActionParsing.getModeratorUsername(stringToParse)}.",
                    iconId = R.drawable.favorite_24,
                )
                _modActions.tryEmit(data)

            }
            "emoteonlyoff"->{
                val data = ModActionData(
                    title = "Emotes-Only Off",
                    message="Removed by ${modActionParsing.getModeratorUsername(stringToParse)}.",
                    iconId = R.drawable.emote_face_24,
                )
                _modActions.tryEmit(data)

            }
            else ->{

            }

        }
    }






    /***************ABOVE IS ALL THE PARSING FOR THE channel.moderate***********************/


} /***END OF TwitchEventSubWebSocket****/

/**
 * parseEventSubWelcomeMessage is a function meant to parse out the session id from the
 * [welcome message](https://dev.twitch.tv/docs/eventsub/handling-websocket-events/#welcome-message)
 *
 * @param stringToParse represents what was sent from the twitch servers
 * @return String? object
 * */
fun parseEventSubWelcomeMessage(stringToParse:String):String?{
    val pattern = "\"id\":([^,]+)".toRegex()
    val messageId = pattern.find(stringToParse)?.groupValues?.get(1)
    val parsedMessageId = messageId?.replace("\"","")
    Log.d("parseEventSubWelcomeMessageData","parsedMessagId --> $parsedMessageId")
    return parsedMessageId
}

/**
 * notificationTypeIsNotification is a function meant to determine if the data sent from the Twitch servers
 * has a type of notification
 *
 * @param stringToParse represents what was sent from the twitch servers
 * @return Boolean object
 * */
fun notificationTypeIsNotification(stringToParse:String):Boolean{
    val wantedNotification ="notification"
    val messageTypeRegex = "\"message_type\":([^,]+)".toRegex()
    val messageType = messageTypeRegex.find(stringToParse)?.groupValues?.get(1)?.replace("\"","")
    return messageType == wantedNotification
}
/**
 * notificationTypeIsWelcome is a function that is used to determine if the [stringToParse] is a welcome message or not
 *
 * @param stringToParse a String that represents a piece of meta data that is sent by the Twitch websocket
 *
 * @return a Boolean that is used to determine if the [stringToParse] contains a `session_welcome` parameter
 *
 * */
fun notificationTypeIsWelcome(stringToParse:String):Boolean{
    val wantedNotification ="session_welcome"
    val messageTypeRegex = "\"message_type\":([^,]+)".toRegex()
    val messageType = messageTypeRegex.find(stringToParse)?.groupValues?.get(1)?.replace("\"","")
    Log.d("MessageTypeIsWelcome","string to parse -->$stringToParse")
    Log.d("MessageTypeIsWelcome","is it message type -->${messageType == wantedNotification}")
    return messageType == wantedNotification
}

fun parseSubscriptionType(stringToParse:String):String{
    val messageTypeRegex = "\"subscription_type\":([^,]+)".toRegex()
    val subscriptionType = messageTypeRegex.find(stringToParse)?.groupValues?.get(1)?.replace("\"","")
    return subscriptionType?:""
}
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

fun parseStatusType(stringToParse:String):String?{
    val messageIdRegex = "\"status\":([^,]+)".toRegex()
    val allFoundMessageIdList = messageIdRegex.findAll(stringToParse).toList()
    println("size -->${allFoundMessageIdList.size}")
    return if(allFoundMessageIdList.size >=2){
        val messageIdNoQuotes=allFoundMessageIdList[1].groupValues[1].replace("\"","")
        val newMessageRegex = "[^,]+".toRegex()
        val desiredMessageId = newMessageRegex.find(messageIdNoQuotes)?.value
        desiredMessageId
    }else{
        null
    }
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


/**
 * parseAutoModQueueMessage is a function meant to parse out the username, category and fullText from the
 * message that is sent from the Twitch servers.
 *
 * @param stringToParse represents what was sent from the twitch servers
 * @return [AutoModQueueMessage] object
 * */
fun parseAutoModQueueMessage(stringToParse:String):AutoModQueueMessage{
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
data class AutoModQueueMessage(
    val username:String ="",
    val fullText:String ="",
    val category: String = "",
    val messageId:String,
    val userId:String,
    var approved:Boolean? = null,
    var swiped:Boolean = false,
)

data class AutoModMessageUpdate(
    val approved: Boolean,
    val messageId: String
)