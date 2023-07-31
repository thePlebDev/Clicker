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
//    init{
//        val pattern ="PRIVMSG".toRegex()
//
//        val anotherString ="@badge-info=subscriber/77;badges=subscriber/36,sub-gifter/50;client-nonce=d7a543c7dc514886b439d55826eeeb5b;color=;display-name=marc_malabanan;emotes=;first-msg=0;flags=;id=fd594314-969b-4f5e-a83f-5e2f74261e6c;mod=0;returning-chatter=0;room-id=19070311;subscriber=1;tmi-sent-ts=1690747946900;turbo=0;user-id=144252234;user-type= :marc_malabanan!marc_malabanan@marc_malabanan.tmi.twitch.tv PRIVMSG #a_seagull :sumSmash"
//
//        val testing = pattern.find(anotherString)?.let {
//            val another = it.range
//            val substring = anotherString.substring(it.range.first,anotherString.length)
//            findLastIndex(substring)
//
//        }
////
//
//    }



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
        webSocket.send("JOIN #F1NN5TER");


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