package com.example.clicker.presentation.selfStreaming.websocket

import android.util.Log
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.atomic.AtomicLong
import javax.net.ssl.HttpsURLConnection

class TcpTunneledSocket(private val host: String, private val port: Int, private val secured: Boolean) {
    val TAG = "TcpTunneledSocket"
    private val headers = mapOf(
        "Content-Type" to "application/x-fcs",
        "User-Agent" to "Shockwave Flash"
    )

    private val timeout = 5000
    private var connectionId: String = ""
    private var connected = false
    private var index = AtomicLong(0)
    private var output = ByteArrayOutputStream()
    private var input = ByteArrayInputStream(byteArrayOf())
    private val sync = Any()
    private var storedPackets = 0
    //send video/audio packets in packs of 10 on each HTTP request.
    private val maxStoredPackets = 10

     suspend fun connect() {
        synchronized(sync) {

            try {
                val openResult = requestRead("open/1", secured)
                connectionId = String(openResult).trimIndent()
                requestWrite("idle/$connectionId/${index.get()}", secured, byteArrayOf(0x00))
                connected = true
                Log.i("TcpTunneledSocket", "Connection success")
            } catch (e: IOException) {
                Log.e("TcpTunneledSocket", "Connection failed: ${e.message}")
                connected = false
            }
        }
    }

    @Throws(IOException::class)
    private fun requestWrite(path: String, secured: Boolean, data: ByteArray) {
        val socket = configureSocket(path, secured)
        try {
            socket.connect()
            socket.outputStream.write(data)
            val bytes = socket.inputStream.readBytes()
            if (bytes.size > 1) input = ByteArrayInputStream(bytes, 1, bytes.size)
            val success = socket.responseCode == HttpURLConnection.HTTP_OK
            if (!success) throw IOException("send packet failed: ${socket.responseMessage}, broken pipe")
        } finally {
            socket.disconnect()
        }
    }

    @Throws(IOException::class)
    private fun requestRead(path: String, secured: Boolean): ByteArray {
        val socket = configureSocket(path, secured)
        try {
            socket.connect()
            val data = socket.inputStream.readBytes()
            val success = socket.responseCode == HttpURLConnection.HTTP_OK
            if (!success) throw IOException("receive packet failed: ${socket.responseMessage}, broken pipe")
            return data
        } finally {
            socket.disconnect()
        }
    }

    private fun configureSocket(path: String, secured: Boolean): HttpURLConnection {
        val schema = if (secured) "https" else "http"
        val url = URL("$schema://$host:$port/$path")
        val socket = if (secured) {
            url.openConnection() as HttpsURLConnection
        } else {
            url.openConnection() as HttpURLConnection
        }
        Log.i(TAG, "open: $url")
        socket.requestMethod = "POST"
        headers.forEach { (key, value) ->
            socket.addRequestProperty(key, value)
        }
        socket.doOutput = true
        socket.connectTimeout = timeout
        socket.readTimeout = timeout
        return socket
    }
}