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
         pattern.find(text)?.let{
             val substring = text.substring(it.range.first,text.length)
             findLastIndex(substring)
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