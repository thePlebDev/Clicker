package com.example.clicker.presentation.selfStreaming.websocket

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.nio.ByteBuffer
import java.util.Random
import javax.net.ssl.HandshakeCompletedEvent
import javax.net.ssl.HandshakeCompletedListener
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

    //surface to buffer encoder
    // where the video frames are sent to be encoded
    private lateinit var inputSurface: Surface

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
                val handshake = ByteArray(1537).apply {
                    //C0
                    this[0] = 3 // RTMP version

                    //C1
                    // Copy timestamp (4 bytes) directly
                    val timestampBytes = ByteBuffer.allocate(4).putInt(timestamp).array()
                    this[1] = timestampBytes[0]
                    this[2] = timestampBytes[1]
                    this[3] = timestampBytes[2]
                    this[4] = timestampBytes[3]

                    // Copy 4 zero bytes directly
                    this[5] = 0
                    this[6] = 0
                    this[7] = 0
                    this[8] = 0

                    // Copy randomData (1528 bytes) directly
                    for (i in randomData.indices) {
                        this[9 + i] = randomData[i]
                    }
                }

                // Send C0 + C1
                val outputStream = sslSocket.getOutputStream()
                outputStream.write(handshake)
                outputStream.flush()

                // Read S0 + S1
                val inputStream = sslSocket.getInputStream()
                val response = ByteArray(1537)
                inputStream.read(response)
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
                inputStream.read(s2)

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

    fun prepareVideoEncoder(){
        val codec = MediaCodec.createEncoderByType("video/avc") // H.264 codec
        val format = MediaFormat.createVideoFormat("video/avc", 200, 300).apply {
            setInteger(MediaFormat.KEY_BIT_RATE, 500000) // Adjust bitrate
            setInteger(MediaFormat.KEY_FRAME_RATE, 30)  // Adjust frame rate
            setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
            setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1) // Interval between I-frames
        }
        //moves the codec to the Configured stage
        codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        //createInputSurface() must be called after configure
        inputSurface = codec.createInputSurface()

        //moves the codec to the Executing stage
        codec.start()

    }


    fun sendConnectCommand(outputStream: OutputStream, app: String, tcUrl: String) {
//        val amfData = AmfEncoder()
//        amfData.writeString("connect")
//        amfData.writeNumber(1.0) // Transaction ID
//        amfData.writeObject(mapOf(
//            "app" to app,
//            "tcUrl" to tcUrl,
//            "fpad" to false,
//            "capabilities" to 239,
//            "audioCodecs" to 3191,
//            "videoCodecs" to 252,
//            "videoFunction" to 1
//        ))
//
//        outputStream.write(amfData.toByteArray())
//        outputStream.flush()
    }

    fun sendPublishCommand(outputStream: OutputStream, streamKey: String) {
//        val amfData = AmfEncoder()
//        amfData.writeString("publish")
//        amfData.writeNumber(1.0) // Transaction ID
//        amfData.writeNull() // Optional metadata
//        amfData.writeString(streamKey) // Stream key
//
//        outputStream.write(amfData.toByteArray())
//        outputStream.flush()
    }

    fun sendVideoFrame(outputStream: OutputStream, frameData: ByteArray) {
//        val header = RtmpHeader.createVideoHeader()
//        outputStream.write(header)
//        outputStream.write(frameData)
//        outputStream.flush()
    }
}