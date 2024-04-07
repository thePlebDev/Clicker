package com.example.clicker.network.websockets

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TwitchEventSubWebSocket @Inject constructor(): WebSocketListener() {
    private var client: OkHttpClient = OkHttpClient.Builder().build()
    var webSocket: WebSocket? = null

    private val _parsedSessionId: MutableStateFlow<String?> = MutableStateFlow(null)
    // The UI collects from this StateFlow to get its state updates
    val parsedSessionId: StateFlow<String?> = _parsedSessionId

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)

        Log.d("TwitchEventSubWebSocket","onOpen() response ->$response")

    }


    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        val parsedSessionId = parseEventSubWelcomeMessage(text)
        _parsedSessionId.tryEmit(parsedSessionId)
        Log.d("TwitchEventSubWebSocket","onMessage() parsedSessionId ->$parsedSessionId")

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
    }

     fun newWebSocket() {
         if(webSocket!=null){
             close()
             createNewSocket()
         }else{
             createNewSocket()
         }

    }

    fun closeWebSocket(){
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


}

fun parseEventSubWelcomeMessage(stringToParse:String):String?{
    val pattern = "\"id\":([^,]+)".toRegex()
    val messageId = pattern.find(stringToParse)?.groupValues?.get(1)
    val parsedMessageId = messageId?.replace("\"","")
    return parsedMessageId

}