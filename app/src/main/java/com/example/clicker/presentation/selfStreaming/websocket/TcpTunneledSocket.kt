package com.example.clicker.presentation.selfStreaming.websocket

import android.hardware.camera2.CameraCharacteristics
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.opengl.EGL14
import android.util.Log
import android.view.Surface
import com.example.clicker.presentation.selfStreaming.EncoderWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.EOFException
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.SocketException
import java.nio.ByteBuffer
import java.util.Random
import java.util.logging.Level
import java.util.logging.Logger
import javax.net.ssl.HandshakeCompletedEvent
import javax.net.ssl.HandshakeCompletedListener
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory


class RtmpsClient2(
    private val host: String,
    private val port: Int,
    private val app: String,

) {
    companion object {
        private const val TAG = "RtmpsClient"
    }

    private lateinit var sslSocket: SSLSocket
    private lateinit var outputStream: OutputStream

    //surface to buffer encoder
    // where the video frames are sent to be encoded
    private lateinit var inputSurface: Surface

    // parameters for the encoder
    private val MIME_TYPE = "video/avc" // H.264 Advanced Video Coding
    private val FRAME_RATE = 30 // 30fps
    private val IFRAME_INTERVAL = 5 // 5 seconds between I-frames
    private val DURATION_SEC: Long = 8 // 8 seconds of video

    // allocate one of these up front so we don't need to do it every time
    private var mBufferInfo: MediaCodec.BufferInfo? = null

//    private lateinit var  mInputSurface: CodecInputSurface

//    private val mInputSurface: Surface by lazy {
//        encoder.getInputSurface()
//    }


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
                // Add a HandshakeCompletedListener
                sslSocket.addHandshakeCompletedListener(object : HandshakeCompletedListener {
                    override fun handshakeCompleted(event: HandshakeCompletedEvent) {
                        Log.i(TAG, "Handshake completed successfully! NEW LISTENER")
                        Log.i(TAG, "Cipher Suite: ${event.cipherSuite}")
                        Log.i(TAG, "Session: ${event.session}")
                        Log.i(TAG, "Peer Principal: ${event.peerPrincipal}")
                    }
                })
                sslSocket.startHandshake() // Perform SSL handshake
                outputStream = sslSocket.outputStream
                Log.i(TAG, "Connected to RTMPS server at $host:$port")

                // Step 3: Perform the RTMP handshake
                performRtmpHandshake()
                // ✅ Step 4: Check RTMP Connection Status
//                checkRtmpConnectionStatus(sslSocket)

                Log.i(TAG, "RTMPS handshake completed successfully")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to connect: ${e.message}", e)
        }
    }


    private suspend fun performRtmpHandshake() {
        withContext(Dispatchers.IO) {
            try {
                val timestamp = System.currentTimeMillis().toInt()
                val randomData = ByteArray(1528).apply { Random().nextBytes(this) }


                // Build C0 + C1
                val c1 = ByteArray(1536).apply {
                   // time (4 bytes)
                    // Copy timestamp (4 bytes) directly
                    val timestampBytes = ByteBuffer.allocate(4).putInt(timestamp).array()
                    this[0] = timestampBytes[0]
                    this[1] = timestampBytes[1]
                    this[2] = timestampBytes[2]
                    this[3] = timestampBytes[3]

                    //zero (4 bytes)
                    // Copy 4 zero bytes directly
                    this[4] = 0
                    this[5] = 0
                    this[6] = 0
                    this[7] = 0

                    //random bytes
                    // Copy randomData (1528 bytes) directly
                    for (i in randomData.indices) {
                        this[8 + i] = randomData[i]
                    }
                }

                val handshake = ByteArray(1537).apply {
                    this[0] = 3 // C0: RTMP version
                    System.arraycopy(c1, 0, this, 1, c1.size) // C1
                }

                // Send C0 + C1
                val outputStream = sslSocket.getOutputStream()
                outputStream.write(handshake)
                outputStream.flush()

                // Read S0 + S1
                val inputStream = sslSocket.getInputStream()
                val response = ByteArray(1537)
                inputStream.readFully(response)

                //MUST wait until S1 has been received before sending C2
                // Verify S0
                if (response[0] != 3.toByte()) {
                    throw IllegalStateException("Invalid RTMP handshake version from server")
                }

                val s1 = response.copyOfRange(1, 1537)

                // Build C2
                val c2 = ByteArray(1536).apply {
                    // Copy the first 4 bytes of S1 (timestamp)
                    this[0] = s1[0]
                    this[1] = s1[1]
                    this[2] = s1[2]
                    this[3] = s1[3]

                    // Copy the current timestamp (4 bytes) into the next 4 bytes
                    val currentTimestamp = ByteBuffer.allocate(4).putInt(System.currentTimeMillis().toInt()).array()
                    this[4] = currentTimestamp[0]
                    this[5] = currentTimestamp[1]
                    this[6] = currentTimestamp[2]
                    this[7] = currentTimestamp[3]

                    // Copy the random data from S1 (starting from the 8th byte)
                    for (i in 8 until 1536) {
                        this[i] = s1[i]
                    }
                }

                // Send C2
                outputStream.write(c2)
                outputStream.flush()

                // Read S2
                val s2 = ByteArray(1536)
                inputStream.readFully(s2)

                //MUST wait until S2
                // Verify S2 matches C1
                if (!s2.contentEquals(c1)) {
                    throw IllegalStateException("Invalid RTMP handshake response from server")
                }

                Log.i(TAG, "RTMP handshake successful. Everything matches. begin sending data")
                connect("app")
            } catch (e: Exception) {
                Log.e(TAG, "Handshake failed: ${e.message}", e)
            }
        }
    }

    fun InputStream.readFully(buffer: ByteArray) {
        var bytesRead = 0
        while (bytesRead < buffer.size) {
            val result = this.read(buffer, bytesRead, buffer.size - bytesRead)
            if (result == -1) {
                throw EOFException("Unexpected end of stream")
            }
            bytesRead += result
        }
    }

    fun connect(appName: String) {
        try {

           // rtmp://ingest.global-contribute.live-video.net/app/{stream_key}

            val commandName = "connect"
            val transactionId = 1 // Always 1 for connect
            val commandObject = mapOf(
                "app" to appName,
                "flashver" to "FMSc/1.0",
                "swfUrl" to "file://C:/FlvPlayer.swf",
                "tcUrl" to "rtmps://ingest.global-contribute.live-video.net:443/app/",
                "fpad" to false,
                "audioCodecs" to 0x0FFF,
                "videoCodecs" to 0x00FF,
                "videoFunction" to 1,
                "pageUrl" to "http://somehost/sample.html",
                "objectEncoding" to 3 // AMF3
            )

            val encodedMessage = encodeAmf(commandName, transactionId, commandObject)
            outputStream.write(encodedMessage)
            outputStream.flush()

            listenForResponse()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    private fun encodeAmf(commandName: String, transactionId: Int, commandObject: Map<String, Any>): ByteArray {
        val buffer = ByteBuffer.allocate(1024) // Allocate enough space

        // Encode command name as AMF string
        buffer.put(0x02) // AMF0 String marker
        buffer.putShort(commandName.length.toShort())
        buffer.put(commandName.toByteArray(Charsets.UTF_8))

        // Encode transaction ID as AMF number
        buffer.put(0x00) // AMF0 Number marker
        buffer.putDouble(transactionId.toDouble())

        // Encode command object as AMF object
        buffer.put(0x03) // AMF0 Object marker
        commandObject.forEach { (key, value) ->
            buffer.putShort(key.length.toShort())
            buffer.put(key.toByteArray(Charsets.UTF_8))

            when (value) {
                is String -> {
                    buffer.put(0x02) // AMF0 String marker
                    buffer.putShort(value.length.toShort())
                    buffer.put(value.toByteArray(Charsets.UTF_8))
                }
                is Boolean -> {
                    buffer.put(0x01) // AMF0 Boolean marker
                    buffer.put(if (value) 1 else 0)
                }
                is Number -> {
                    buffer.put(0x00) // AMF0 Number marker
                    buffer.putDouble(value.toDouble())
                }
            }
        }
        buffer.put(0x00) // End of object
        buffer.put(0x00)
        buffer.put(0x09)

        return buffer.array().copyOf(buffer.position())
    }



    private fun listenForResponse() {
        val inputStream = sslSocket.getInputStream()
        val responseBuffer = ByteArray(1024)
        val bytesRead = inputStream.read(responseBuffer)

        if (bytesRead > 0) {
            val response = String(responseBuffer, 0, bytesRead, Charsets.UTF_8)
            Log.d(TAG,"listenForResponse() Server Response: $response")
        }else{
            Log.d(TAG,"bytesRead < 0 --->: $bytesRead")
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


    suspend fun checkRtmpConnectionStatus(socket: SSLSocket) {
        Log.i(TAG, "CHECKING RESPONSE")
        withContext(Dispatchers.IO) {
            try {
                val inputStream = socket.getInputStream()
                val buffer = ByteArray(4096) // Buffer for incoming RTMP messages
                val bytesRead = inputStream.read(buffer)

                if (bytesRead > 0) {
                    // Log raw RTMP response in hex format
                    val responseHex = buffer.copyOf(bytesRead).joinToString(" ") { String.format("%02X", it) }
                    Log.i(TAG, "Raw RTMP Response (Hex): $responseHex")

                    // Try to decode the response as UTF-8 text
                    val responseText = String(buffer.copyOf(bytesRead), Charsets.UTF_8)
                    Log.i(TAG, "Decoded RTMP Response: $responseText")

                    // Check if the RTMP server sent a successful connection response
                    if (responseText.contains("NetConnection.Connect.Success")) {
                        Log.i(TAG, "RTMP connection established successfully!")
                    } else {
                        Log.e(TAG, "RTMP connection failed or pending: $responseText")
                    }
                } else {
                    Log.e(TAG, "No data received from RTMP server")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error reading RTMP response: ${e.message}", e)
            }
        }
    }



    data class RTMPChunk(
        val c_chunk: ByteArray,
        val c_header: ByteArray,
        val c_chunkSize: Int,
        val c_headerSize: Int
    )

    fun RTMP_SendChunk(chunk: RTMPChunk): Int {
        val logger = Logger.getLogger("RTMP")
        var wrote = 0
        val hbuf = ByteArray(RTMP_MAX_HEADER_SIZE)

        logger.log(Level.FINE, "{0}: size={1}", arrayOf("RTMP_SendChunk", chunk.c_chunkSize))
        logger.log(Level.FINE, chunk.c_header.joinToString("") { String.format("%02x", it) })

        if (chunk.c_chunkSize > 0) {
            val ptr = ByteArray(chunk.c_headerSize + chunk.c_chunkSize)
            System.arraycopy(chunk.c_chunk, 0, ptr, chunk.c_headerSize, chunk.c_chunkSize)
            logger.log(Level.FINE, chunk.c_chunk.joinToString("") { String.format("%02x", it) })

            // Save header bytes we're about to overwrite
            System.arraycopy(ptr, 0, hbuf, 0, chunk.c_headerSize)
            System.arraycopy(chunk.c_header, 0, ptr, 0, chunk.c_headerSize)

//            outputStream.write(ptr)
//            outputStream.flush()

            if (sslSocket.session.isValid) {
                Log.d("RTMP", "SSL Handshake successful")
            } else {
                Log.e("RTMP", "SSL Handshake failed, server might be rejecting connection")
            }
            if (sslSocket.inputStream.read() == -1) {
                Log.e("RTMP", "Server closed the connection immediately")
            }

            if (!sslSocket.isClosed && sslSocket.isConnected) {
                try {
                    outputStream.write(ptr)
                    outputStream.flush()
                    Log.d("RTMP", "Chunk sent successfully")
                } catch (e: IOException) {
                    Log.e("RTMP", "Error writing to stream: ${e.message}")
                }
            } else {
                Log.e("RTMP", "Socket is closed, cannot write")
            }
            wrote = chunk.c_headerSize + chunk.c_chunkSize

            // Restore the original header bytes
            System.arraycopy(hbuf, 0, ptr, 0, chunk.c_headerSize)
        } else {
           outputStream.write(chunk.c_header)
            outputStream.flush()
            wrote = chunk.c_headerSize
        }

        return wrote
    }

     val RTMP_MAX_HEADER_SIZE = 18


    fun createRTMPChunk(encodedData: ByteBuffer?): RTMPChunk {
        // Define the header size (example value)
        val headerSize = 12

        // Extract data from the ByteBuffer
        val data = ByteArray(encodedData?.remaining() ?: 0)
        encodedData?.get(data)

        // Define the chunk header (example values)
        val header = ByteArray(headerSize)
        // Populate the header with example values
        // In a real scenario, you would set this based on your protocol requirements
        header[0] = 0x02 // Example header byte

        // Create the RTMPChunk instance
        return RTMPChunk(
            c_chunk = data,
            c_header = header,
            c_chunkSize = data.size/2,
            c_headerSize = header.size
        )
    }

    fun rtmp_open_for_write(){
        val rtmp = rtmpInit()

    }
    fun rtmpInit():RTMP{
        val RTMP_DEFAULT_CHUNKSIZE = 128

        val rtmpChunk =RTMP()
        rtmpChunk.mSb?.socket = -1
        rtmpChunk.mInChunkSize =RTMP_DEFAULT_CHUNKSIZE
        rtmpChunk.mOutChunkSize =RTMP_DEFAULT_CHUNKSIZE

        rtmpChunk.mNBufferMS =30000
        rtmpChunk.mNClientBW =2500000
        rtmpChunk.mNClientBW2 =2
        rtmpChunk.mNServerBW=2500000
        rtmpChunk.mFAudioCodecs =3191.0
        rtmpChunk.mFVideoCodecs =252.0
        rtmpChunk.link?.receiveTimeoutInMs = 10000
        rtmpChunk.link?.swfAge = 30


        return rtmpChunk

    }


}


