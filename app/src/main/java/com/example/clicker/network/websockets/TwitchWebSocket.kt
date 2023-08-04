package com.example.clicker.network.websockets

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import okio.ByteString.Companion.decodeHex
import java.util.concurrent.TimeUnit

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
    val userType: String?
)

class TwitchWebSocket(): WebSocketListener() {


    private val webSocketURL = "wss://irc-ws.chat.twitch.tv:443"
    var streamerChannelName = ""


    private val _state = MutableStateFlow("initialValue")
    val state = _state.asStateFlow()

    private lateinit var client: OkHttpClient
    var webSocket:WebSocket? = null





     fun run(channelName:String?) {
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


    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        webSocket.send("CAP REQ :twitch.tv/tags twitch.tv/commands");

        val token = "ez7yaqr9hm2jviay6ldubuvszl8vbb"

        webSocket.send("PASS oauth:$token");
        webSocket.send("NICK theplebdev");
        webSocket.send("JOIN #$streamerChannelName");


    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.d("websocketStoof","onMessage()byte: ${bytes.hex()}")
    }

     override fun onMessage(webSocket: WebSocket, text: String) {
         val pattern ="PRIVMSG".toRegex()
         val pattern2 = "#$streamerChannelName".toRegex()

//         pattern.find(text)?.let{
//             val substring = text.substring(it.range.first,text.length)
//             findLastIndex(substring)
//         }
         val indexValue =text.indexOf("#$streamerChannelName").let { index ->
             if(index != -1){
                 val message = text.substring((index + "#$streamerChannelName".length +2),text.length)
                 Log.d("websocketStoof","onMessage-> ${message}")
             }
         }

    }
    private fun findLastIndex(substring:String){
        val pattern = ":".toRegex()
        pattern.find(substring)?.let {

            _state.tryEmit(substring.substring(it.range.first+ 1,substring.length))
            Log.d("websocketStoof","onMessage: ${substring.substring(it.range.first+ 1,substring.length)}")
        }


    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(1000, null)
        println("CLOSE: $code $reason")
        Log.d("websocketStoof","onClosing: $code $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        //t.printStackTrace()
        Log.d("websocketStoof","onFailure: ${t.printStackTrace()}")
        Log.d("websocketStoof","onFailure: ${t.message.toString()}")
        Log.d("websocketStoof","onFailure: ${webSocket.toString()}")
    }
}


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

fun mapToTwitchUserData(parsedData: Map<String, String>): TwitchUserData {
    return TwitchUserData(
        badgeInfo = parsedData["badge-info"],
        badges = parsedData["badges"],
        clientNonce = parsedData["client-nonce"],
        color = parsedData["color"],
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
        userType = parsedData["user-type"],
        userId = parsedData["user-id"],
    )
}



