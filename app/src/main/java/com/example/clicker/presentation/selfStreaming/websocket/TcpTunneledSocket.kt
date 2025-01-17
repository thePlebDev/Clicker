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

class TcpTunneledSocket(private val host: String, private val port: Int, private val secured: Boolean):
    RtmpSocket() {
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

      override suspend fun connect() {
        synchronized(sync) {

            try {
                val openResult = requestRead("open/1", true)
                connectionId = String(openResult).trimIndent()
                requestWrite("idle/$connectionId/${index.get()}", secured, byteArrayOf(0x00))
                connected = true
                Log.i("TcpTunneledSocket", "Connection success")
            } catch (e: IOException) {
                Log.e("TcpTunneledSocket", "Connection failed: ${e.message}")
                Log.e("TcpTunneledSocket", "Connection failed: ${e.cause}")
                Log.e("TcpTunneledSocket", "Connection failed: ${e.localizedMessage}")

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
            Log.i(TAG, "socket status: $success")
            if (!success) throw IOException("receive packet failed: ${socket.responseMessage}, broken pipe")
            return data
        } finally {
            socket.disconnect()
        }
    }

    private fun configureSocket(path: String, secured: Boolean): HttpURLConnection {
        val schema = if (secured) "https" else "http"
        val urlTesting = "rtmps://ingest.global-contribute.live-video.net:443/app/" //try running this
        //Also there is no stream key in the url -> so that might by why nothing is working
        //the path should be the stream key???
        //ingest.global-contribute.live-video.net:443
        //TODO: i NEED TO ADD THE STREAM KEY TO THE URL BELOW
        val urlAgain ="$schema://$host:443/app/"
        Log.i("configureSocket",urlAgain)
        val url = URL(urlAgain)
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
        Log.i(TAG, "open: $url")

        return socket
    }

    override suspend fun close() {
        Log.i(TAG, "closing tunneled socket...")
        connected = false
        synchronized(sync) {
            Thread {
                try {
                    requestWrite("close/$connectionId", secured, byteArrayOf(0x00))
                    Log.i(TAG, "Close success")
                } catch (e: IOException) {
                    Log.e(TAG, "Close request failed: ${e.message}")
                } finally {
                    index.set(0)
                    connectionId = ""
                }
            }.start()
        }
    }
}

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
    suspend fun connect() {
        try {
            withContext(Dispatchers.IO) {
                // Step 1: Create SSL context and factory
                val sslContext: SSLContext = SSLContext.getInstance("TLS")
                sslContext.init(null, null, null)
                val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory

                // Step 2: Establish a secure connection to the RTMP server
                sslSocket = sslSocketFactory.createSocket(host, port) as SSLSocket
                sslSocket.startHandshake() // Perform SSL handshake
                outputStream = sslSocket.outputStream
                Log.i(TAG, "Connected to RTMPS server at $host:$port")

                // Step 3: Perform the RTMP handshake
                performRtmpHandshake()

                Log.i(TAG, "RTMPS handshake completed successfully")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to connect: ${e.message}", e)
        }
    }

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