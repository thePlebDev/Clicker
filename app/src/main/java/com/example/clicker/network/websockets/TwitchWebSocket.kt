package com.example.clicker.network.websockets

import android.util.Log
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

    private val client: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(1000,TimeUnit.MILLISECONDS)
        .writeTimeout(1000,TimeUnit.MILLISECONDS)
        .build()
    var webSocket:WebSocket? = null

    init {
        run()
    }


    private fun run() {


        val request: Request = Request.Builder()
            .url(webSocketURL)
            .build()
        webSocket =client.newWebSocket(request, this)


    }
    fun close(){
        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
        client.dispatcher.executorService.shutdown()
        webSocket?.close(1009,"Manually closed ")

    }


    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        webSocket.send("CAP REQ :twitch.tv/tags twitch.tv/commands");

        val token = "ez7yaqr9hm2jviay6ldubuvszl8vbb"

        webSocket.send("PASS oauth:$token");
        webSocket.send("NICK theplebdev");
        webSocket.send("JOIN #AdmiralBahroo");


    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.d("websocketStoof","onMessage()byte: ${bytes.hex()}")
    }

     override fun onMessage(webSocket: WebSocket, text: String) {
        //println("MESSAGE: " + bytes.hex())
        // Log.d("websocketStoof","onMessage CALLED: ${text}")
        Log.d("websocketStoof","onMessage: ${text}")
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