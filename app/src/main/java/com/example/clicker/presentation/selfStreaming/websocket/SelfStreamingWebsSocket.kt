package com.example.clicker.presentation.selfStreaming.websocket

import android.util.Log
import com.example.clicker.presentation.selfStreaming.domain.SelfStreamingSocket
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class SelfStreamingWebsSocket: SelfStreamingSocket, WebSocketListener() {


    private var client: OkHttpClient = OkHttpClient.Builder().build()
    var webSocket: WebSocket? = null
    private val hardcodedRMTPURL =""



    override fun runWebSocket(){
        newWebSocket()

    }
    override fun closeWebSocket(){
        close()

    }

//    this does not come from WebSocketListener
    private fun close() {
        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
        client.dispatcher.executorService.shutdown()
        webSocket?.close(1009, "Manually closed ")
        webSocket = null
    }
    private fun newWebSocket() {
        Log.d("StreamingWebsocket", "newWebSocket()")

        val request: Request = Request.Builder()
            .url(hardcodedRMTPURL)
            .build()
        client = OkHttpClient.Builder()
            .readTimeout(1000, TimeUnit.MILLISECONDS)
            .writeTimeout(1000, TimeUnit.MILLISECONDS)
            .build()

        webSocket = client.newWebSocket(request, this)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        // t.printStackTrace()
        Log.d("StreamingWebsocket", "onFailure stack->: ${t.printStackTrace()}")
        Log.d("StreamingWebsocket", "onFailure message->: ${t.message}")
        Log.d("StreamingWebsocket", "onFailure cause-> : ${t.cause}")
        Log.d("StreamingWebsocket", "onFailure localizedMessage-> : ${t.localizedMessage}")
        Log.d("StreamingWebsocket", "onFailure stackTraceToString-> : ${t.stackTraceToString()}")

        close()


        // _state.tryEmit(errorValue)
    }


    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("StreamingWebsocket","text ->$text")
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Log.d("StreamingWebsocket","onOpen ->${response.message}")
        Log.d("StreamingWebsocket","onOpen ->${response.code}")

    }
    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(1000, null)

        Log.d("StreamingWebsocket", "onClosing: $code $reason")
    }


}