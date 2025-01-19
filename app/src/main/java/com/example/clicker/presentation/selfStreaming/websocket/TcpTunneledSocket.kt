package com.example.clicker.presentation.selfStreaming.websocket

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.atomic.AtomicLong
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory



class RtmpsClient2(
    private val host: String,
    private val port: Int,
    private val app: String
) {
    companion object {
        private const val TAG = "RtmpsClient"
    }

    private lateinit var sslSocket: SSLSocket
    private lateinit var outputStream: OutputStream

    // Perform connection on background thread using Coroutine


    private suspend fun performRtmpHandshake() {
        withContext(Dispatchers.IO) {
            try {
                val handshake = ByteArray(1537)
                handshake[0] = 0x03 // RTMP version 3 byte
                outputStream.write(handshake)
                outputStream.flush()

                // Read the server's handshake response
                val inputStream = sslSocket.inputStream
                val response = ByteArray(1537)
                inputStream.read(response)

                if (response[0] != 0x03.toByte()) {
                    throw IllegalStateException("Invalid RTMP handshake version from server")
                }

                Log.i(TAG, "RTMP handshake successful")
            } catch (e: Exception) {
                Log.e(TAG, "Handshake failed: ${e.message}", e)
            }
        }
    }

    suspend fun disconnect() {
        withContext(Dispatchers.IO) {
            try {
                sslSocket.close() // Close the SSLSocket on the background thread
                Log.i(TAG, "Disconnected from RTMPS server")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to disconnect: ${e.message}", e)
            }
        }
    }
}