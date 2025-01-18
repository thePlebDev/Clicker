package com.example.clicker.presentation.selfStreaming.clients

import com.example.clicker.presentation.selfStreaming.util.UrlParser
import com.example.clicker.presentation.selfStreaming.websocket.RtmpSocket

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class RtmpClient @Inject constructor() {

    private var scope = CoroutineScope(Dispatchers.IO)
    private var scopeRetry = CoroutineScope(Dispatchers.IO)
    private var job: Job? = null
    private var socket: RtmpSocket? = null
    private var tlsEnabled = false
    private val validSchemes = arrayOf("rtmp", "rtmps", "rtmpt", "rtmpts")

    private val tunneled = true
    private val port = 80


    suspend fun connect(url: String?, isRetry: Boolean) {

        job = scope.launch {
            if (url == null) {

                return@launch
            }

        }
        val urlParser =
            UrlParser.parse(url?:"", validSchemes)

        establishConnection(urlParser.host)

    }

    @Throws(IOException::class)
    private suspend fun establishConnection(
         host:String
    ): Boolean {
//        val socket = TcpTunneledSocket(host, 1935, tlsEnabled)
//        socket.connect()
        return true
    }

    //TODO: THIS IS WHAT IS GOING TO GET CALLED WHEN THE user ends the stream
    fun disconnect() {
        CoroutineScope(Dispatchers.IO).launch {
            disconnect(true)
        }
    }
    private suspend fun disconnect(clear: Boolean) {

        closeConnection()

//            jobRetry?.cancelAndJoin() THIS IS ONLY NEEDED FOR RETRYS
//            jobRetry = null
            scopeRetry.cancel()
            scopeRetry = CoroutineScope(Dispatchers.IO)

        job?.cancelAndJoin()
        job = null
        scope.cancel()
        scope = CoroutineScope(Dispatchers.IO)

    }


    private suspend fun closeConnection() {
        socket?.close()

    }

}