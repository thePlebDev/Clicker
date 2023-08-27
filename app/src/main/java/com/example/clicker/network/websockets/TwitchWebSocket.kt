package com.example.clicker.network.websockets

import android.util.Log
import com.example.clicker.data.TokenDataStore
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import okio.ByteString.Companion.decodeHex
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

enum class MessageType {
    USER, NOTICE
}
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
    val messageType: MessageType
)
data class LoggedInUserData(
    val color:String?,
    val displayName: String,
    val sub:Boolean,
    val mod:Boolean
)


data class RoomState(
    val emoteMode:Boolean?,
    val followerMode:Boolean?,
    val slowMode:Boolean?,
    val subMode:Boolean?
)

class TwitchWebSocket @Inject constructor(
    private val tokenDataStore: TokenDataStore
): WebSocketListener() {

    private val webSocketScope = CoroutineScope(Dispatchers.Default + CoroutineName("webSocketScope"))

    private val initialValue =TwitchUserData(
        badgeInfo = "subscriber/77",
        badges = "subscriber/36,sub-gifter/50",
        clientNonce = "d7a543c7dc514886b439d55826eeeb5b",
        color = "FF0000",
        displayName = "verifying data",
        emotes = "",
        firstMsg = "0",
        flags = "",
        id = "fd594314-969b-4f5e-a83f-5e2f74261e6c",
        mod = "0",
        returningChatter = "0",
        roomId = "19070311",
        subscriber = true,
        tmiSentTs = 1690747946900L,
        turbo = false,
        userId = "144252234",
        userType = "Connecting to chat",
        messageType = MessageType.USER
    )


    private val webSocketURL = "wss://irc-ws.chat.twitch.tv:443"
    var streamerChannelName = ""
    private var loggedInUsername = ""


    private val _state = MutableStateFlow(initialValue)
    val state = _state.asStateFlow() //this is the text data shown to the user

    private val _loggedInUserUiState = MutableStateFlow<LoggedInUserData?>(null)
    val loggedInUserUiState = _loggedInUserUiState

    private var client: OkHttpClient = OkHttpClient.Builder().build()
    var webSocket:WebSocket? = null

    var sentMessageString:String = ""

    private val _roomState = MutableStateFlow<RoomState?>(null)
    val roomState = _roomState.asStateFlow()





     fun run(channelName:String?,username:String) {
         loggedInUsername = username
        if(channelName !=null){
            streamerChannelName = channelName
            if(webSocket != null){
                close()
                newWebSocket()
            }else{
                newWebSocket()
            }
        }else{

        }

    }
     fun close(){
        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
        client.dispatcher.executorService.shutdown()
        webSocket?.close(1009,"Manually closed ")
         webSocket = null


    }
    private fun newWebSocket(){
        val request: Request = Request.Builder()
            .url(webSocketURL)
            .build()
        client = OkHttpClient.Builder()
            .readTimeout(1000,TimeUnit.MILLISECONDS)
            .writeTimeout(1000,TimeUnit.MILLISECONDS)
            .build()

        webSocket =client.newWebSocket(request, this)

    }


    override fun onOpen(webSocket: WebSocket, response: Response){
        super.onOpen(webSocket, response)

        //todo: I think I am going to create a custom scope tied to the lifecycle of this websocket
        openChat(webSocket)




    }

    fun openChat(webSocket: WebSocket) = GlobalScope.launch{
        webSocket.send("CAP REQ :twitch.tv/tags twitch.tv/commands");
        tokenDataStore.getOAuthToken().collect{oAuthToken ->
            Log.d("OAuthtokenStoof",oAuthToken)
            webSocket.send("PASS oauth:$oAuthToken");
            webSocket.send("NICK $loggedInUsername");
            webSocket.send("JOIN #$streamerChannelName");
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.d("websocketStoof","onMessage()byte: ${bytes.hex()}")
    }

     override fun onMessage(webSocket: WebSocket, text: String) {
         Log.d("onMessageSocket","state --> $text")

         if(text.contains(" USERSTATE ")){
             Log.d("loggedInDataOnMessage","USERSTATE --> $text")
             _loggedInUserUiState.tryEmit(
                 getLoggedInUserInfo(text)
             )

         }
         if(text.contains("NOTICE")){
             Log.d("NOTICE","NOTICE --> $text")
             val pattern = "#theplebdev\\s*:(.+)".toRegex()
             val matchResult = pattern.find(text)
             val extractedInfo = matchResult?.groupValues?.get(1)?.trim() ?: "Room information updated"

             val userData = TwitchUserData(
                 badgeInfo = null,
                 badges = null,
                 clientNonce = null,
                 color = "#000000",
                 displayName = "Room update",
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
                 userType = extractedInfo,
                 messageType = MessageType.NOTICE
             )
             _state.tryEmit(userData)

         }
         if(text.contains("PRIVMSG")){
             val anotherTesting = parseStringBaby(text)
             val mappedString = mapToTwitchUserData(anotherTesting, sentMessage = sentMessageString)
             _state.tryEmit(mappedString)
         }

         if(text.contains("USERSTATE")){
             Log.d("onMessageSocketUSERSTATE","USERSTATE --> $text")
         }
         if(text.contains("ROOMSTATE")){
             Log.d("onMessageSocketROOMSTATE","ROOMSTATE --> $text")
            val slowMode= getValueFromInput(text,"slow")

             val emoteMode = getValueFromInput(text,"emote-only")
             val followersMode = getValueFromInput(text,"followers-only")


             val subMode = getValueFromInput(text,"subs-only")
             val roomState = RoomState(
                 emoteMode=emoteMode,
                 followerMode = followersMode,
                 slowMode = slowMode,
                 subMode = subMode
             )
             Log.d("onMessageSocketROOMSTATE","ROOMSTATE --> $roomState")
             _roomState.tryEmit(roomState)


         }

    }


    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(1000, null)
        println("CLOSE: $code $reason")
        Log.d("websocketStoof","onClosing: $code $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        //t.printStackTrace()
        Log.d("websocketStooffail","onFailure: ${t.printStackTrace()}")
        Log.d("websocketStooffail","onFailure: ${t.message.toString()}")
        Log.d("websocketStooffail","onFailure: ${webSocket.toString()}")
    }


    fun sendMessage(chatMessage:String):Boolean{
       val sendingText = webSocket?.send("PRIVMSG #$streamerChannelName :$chatMessage")
        sentMessageString = chatMessage
       // 'PRIVMSG #$channelName :$message'
        return sendingText ?: false
    }


}/*********END OF THE TwitchWebSocket CLASS*******/


fun parseStringBaby(input: String): Map<String, String> {
    val pattern = "([^;@]+)=([^;]+)".toRegex()
    val matchResults = pattern.findAll(input)

    val parsedData = mutableMapOf<String, String>()


    for (matchResult in matchResults) {
        val (key, value) = matchResult.destructured
        parsedData[key] = value
    }

    return parsedData
}

fun mapToTwitchUserData(parsedData: Map<String, String>,sentMessage: String): TwitchUserData {
    return TwitchUserData(
        badgeInfo = parsedData["badge-info"],
        badges = parsedData["badges"],
        clientNonce = parsedData["client-nonce"],
        color = parsedData["color"] ?: "#000000",
        displayName = parsedData["display-name"],
        emotes = parsedData["emotes"],
        firstMsg = parsedData["first-msg"],
        flags = parsedData["flags"],
        id = parsedData["id"],
        mod = parsedData["mod"],
        returningChatter = parsedData["returning-chatter"],
        subscriber = parsedData["subscriber"]?.toIntOrNull() == 1,
        roomId = parsedData["room-id"],
        tmiSentTs = parsedData["tmi-sent"]?.toLongOrNull(),
        turbo = parsedData["turbo"]?.toIntOrNull() == 1,
        userType = checkStrings(filterText(parsedData["user-type"].toString()),sentMessage),
        userId = parsedData["user-id"],
        messageType = MessageType.USER
    )
}

fun checkStrings(parsedText:String,sentMessage:String):String{
    if(parsedText.isEmpty()){
        return sentMessage
    }else{
        return parsedText
    }
}


fun filterText(chatText:String):String{

    val regex = ":(.*?):(.*)".toRegex()
    val matchResult = regex.find(chatText)
    //Log.d("websocketStoofs","onMessageLoggers-> $streamerName")

    return matchResult?.groupValues?.getOrNull(2)?.trim() ?: ""
}
fun getLoggedInUserInfo(text:String):LoggedInUserData{
    val colorPattern = "color=([^;]+)".toRegex()
    val displayNamePattern = "display-name=([^;]+)".toRegex()
    val modStatusPattern = "mod=([^;]+)".toRegex()
    val subStatusPattern = "subscriber=([^;]+)".toRegex()


    val colorMatch = colorPattern.find(text)
    val displayNameMatch = displayNamePattern.find(text)
    val modStatusMatch = modStatusPattern.find(text)
    val subStatusMatch = subStatusPattern.find(text)



    val loggedData =LoggedInUserData(
        color =colorMatch?.groupValues?.get(1),
        displayName = displayNameMatch?.groupValues?.get(1)!!,
        mod = stringToBoolean(modStatusMatch?.groupValues?.get(1)!!),
        sub = stringToBoolean(subStatusMatch?.groupValues?.get(1)!!)

    )
    Log.d("loggedData","loggedDataObject --> $loggedData")


    return loggedData
}
fun stringToBoolean( subOrModText:String):Boolean{
    val convertedString = subOrModText.toInt()

    return convertedString ==1

}

fun getValueFromInput(input: String, key: String): Boolean? {
    val pattern = "$key=([^;:\\s]+)".toRegex()
    val match = pattern.find(input)
    val returnedValue = match?.groupValues?.get(1) ?: return null
        if( returnedValue == "-1"){
        return false
    }
    if(key == "followers-only" && returnedValue == "0"){
        return true
    }
    else{
        return returnedValue != "0"
    }

}


