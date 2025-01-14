package com.example.clicker.presentation.selfStreaming.clients

import com.example.clicker.presentation.selfStreaming.websocket.RtmpSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RtmpClient {

    private var scope = CoroutineScope(Dispatchers.IO)
    private var scopeRetry = CoroutineScope(Dispatchers.IO)
    private var job: Job? = null
    private var socket: RtmpSocket? = null

    fun connect(url: String?, isRetry: Boolean) {

        job = scope.launch {
            if (url == null) {

                return@launch
            }

        }

    }

//    private suspend fun closeConnection() {
//        socket?.close()
//        commandsManager.reset()
//    }
}