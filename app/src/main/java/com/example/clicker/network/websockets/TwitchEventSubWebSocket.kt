package com.example.clicker.network.websockets

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.clicker.R
import com.example.clicker.network.domain.TwitchEventSubscriptionWebSocket
import com.example.clicker.network.models.twitchStream.ChatSettingsData
import com.example.clicker.network.repository.util.ChatSettingsParsing
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
    private val modActionParsing:ModActionParsing,
    private val channelSettingsParsing: ChatSettingsParsing
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

                }
                "automod.message.update" -> {
                    val messageId = parseMessageId(text) ?: ""
                    val messageUpdate = checkUpdateStatus(text, messageId)
                    _messageIdForAutoModQueue.tryEmit(messageUpdate)
                }
                "channel.chat_settings.update" -> {
                    val parsedChatSettingsData = channelSettingsParsing.parseChatSettingsData(text)
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

        val data = ModActionData(
            title ="Connection Error",
            message="There was an error while trying to connect with Twitch's servers",
            iconId = R.drawable.error_outline_24,
            secondaryMessage = "To fix this issue try going back to the home page and click on the stream again"
        )
        _modActions.tryEmit(data)
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