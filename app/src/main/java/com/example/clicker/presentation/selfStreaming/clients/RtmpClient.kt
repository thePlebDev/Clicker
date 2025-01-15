package com.example.clicker.presentation.selfStreaming.clients

import com.example.clicker.presentation.selfStreaming.util.UrlParser
import com.example.clicker.presentation.selfStreaming.websocket.RtmpSocket
import com.example.clicker.presentation.selfStreaming.websocket.TcpTunneledSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class RtmpClient {

    private var scope = CoroutineScope(Dispatchers.IO)
    private var scopeRetry = CoroutineScope(Dispatchers.IO)
    private var job: Job? = null
    private var socket: RtmpSocket? = null
    private var tlsEnabled = false
    private val validSchemes = arrayOf("rtmp", "rtmps", "rtmpt", "rtmpts")


    fun connect(url: String?, isRetry: Boolean) {

        job = scope.launch {
            if (url == null) {

                return@launch
            }

        }
        val urlParser =
            UrlParser.parse(url?:"", validSchemes)

    }

    @Throws(IOException::class)
    private suspend fun establishConnection(): Boolean {
        //val socket = TcpTunneledSocket(commandsManager.host, 80, tlsEnabled)
        return true
    }


//    private suspend fun closeConnection() {
//        socket?.close()
//        commandsManager.reset()
//    }
}