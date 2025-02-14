package com.example.clicker.presentation.selfStreaming.websocket

import android.hardware.camera2.CameraCharacteristics
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.opengl.EGL14
import android.os.Build
import android.util.Log
import android.view.Surface
import androidx.annotation.RequiresApi
import com.example.clicker.presentation.selfStreaming.EncoderWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.EOFException
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.SocketException
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
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
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
//
                    }
                })
                sslSocket.startHandshake() // Perform SSL handshake
                outputStream = sslSocket.outputStream
               // Log.i(TAG, "Connected to RTMPS server at $host:$port")

                // Step 3: Perform the RTMP handshake
                //performRtmpHandshake()
                performRtmpHandshakeAgain()


                Log.i(TAG, "RTMPS handshake completed successfully")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to connect: ${e.message}", e)
        }
    }



    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private  suspend fun performRtmpHandshakeAgain(){
        val timestamp = System.currentTimeMillis().toInt()
        val randomData = ByteArray(1528).apply { Random().nextBytes(this) }
        val C0 = ByteArray(1)

        C0[0] =3

        val C1 = ByteArray(1536).apply {
            // time (4 bytes)
            // Copy timestamp (4 bytes) directly
            this[0] = 0
            this[1] = 0
            this[2] = 0
            this[3] = 0




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
            System.arraycopy(C1, 0, this, 1, C1.size)
        }

        // Send C0 + C1
        val outputStream = sslSocket.getOutputStream()
        outputStream.write(handshake)
        outputStream.flush()

        // Read S0 + S1
        val inputStream = sslSocket.getInputStream()
        val allBytesS1 = inputStream.readAllBytes()


        //TODO: DOUBLE CHECK THAT THIS 9 INDEX IS CORRECT
        val firstHalfOfS1 = allBytesS1.copyOfRange(9, 1537)


// Optionally, log the first few bytes for verification
       // Log.d("HandshakeVerification", "First few bytes of firstHalfOfS1: ${firstHalfOfS1.take(10).joinToString(", ") { it.toString() }}")

        val C2 = ByteArray(1536).apply {
            // Set the first 8 bytes to 0 (timestamp)
            this[0] = 0
            this[1] = 0
            this[2] = 0
            this[3] = 0
            this[4] = 0
            this[5] = 0
            this[6] = 0
            this[7] = 0

            // Copy the remaining 1528 bytes from firstHalfOfS1
            for (i in 0 until 1528) {
                this[8 + i] = firstHalfOfS1[i] // Correctly copy all 1528 bytes
            }
        }


// Compare the last 10 bytes of S1 and C2
        val lastFewBytesOfS1 = firstHalfOfS1.copyOfRange(firstHalfOfS1.size - 10, firstHalfOfS1.size)
        val lastFewBytesOfC2 = C2.copyOfRange(C2.size - 10, C2.size)
        Log.d("HandshakeAgain", "Last 10 bytes of S1: ${lastFewBytesOfS1.joinToString(", ") { it.toString() }}")
        Log.d("HandshakeAgain", "Last 10 bytes of C2: ${lastFewBytesOfC2.joinToString(", ") { it.toString() }}")

        outputStream.write(C2)
        outputStream.flush()


        val bytesRead = inputStream.readAllBytes()
        Log.d("TESTINGHANDSHAKEAGAIN", "Server response to connect: ${bytesRead.size} bytes")







    }







    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
                if (response[0] == 3.toByte()) {

                    Log.i(TAG, "S0 == 3.TObYTE")
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

                Log.d("TESTINGHANDSHAKEAGAIN","readAllBytes --->${inputStream.readAllBytes().size}")
                Log.d("TESTINGHANDSHAKEAGAIN","c1 size --->${c1.size}")

                //MUST wait until S2
                // Verify S2 matches C1
                if (s2.contentEquals(c1)) {

                    Log.i(TAG, "Invalid RTMP handshake response from server")
                }else{
                    Log.i(TAG, "RTMP handshake successful. Everything matches. begin sending data")
                }



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

    // Function to listen for the "Window Acknowledgement Size" (RTMP type 5)
    fun listenForWindowAcknowledgementSizeOrigianl(inputStream: InputStream) {
        try {
            // RTMP protocol uses a 12-byte header for each message
            val header = ByteArray(12)

            // Read the RTMP header
            val headerLength = inputStream.read(header)
            if (headerLength != 12) {
                throw EOFException("Failed to read RTMP message header.")
            }

            // Extract message type (Message Type is 1 byte at position 7 in the header)
            val messageType = header[7].toInt()

            if (messageType == 5) { // Window Acknowledgement Size (Message Type 5)
                val windowSizeBytes = ByteArray(4)
                val bytesRead = inputStream.read(windowSizeBytes)
                if (bytesRead != 4) {
                    throw EOFException("Failed to read Window Acknowledgement Size.")
                }

                // Convert the byte array to an integer (window size)
                val windowSize = ((windowSizeBytes[0].toInt() and 0xFF) shl 24) or
                        ((windowSizeBytes[1].toInt() and 0xFF) shl 16) or
                        ((windowSizeBytes[2].toInt() and 0xFF) shl 8) or
                        (windowSizeBytes[3].toInt() and 0xFF)

                Log.d("listenForWindowAcknowledgementSize","Received Window Acknowledgement Size: $windowSize bytes")
            } else {
                Log.d("listenForWindowAcknowledgementSize","Received non-Window Acknowledgement message type: $messageType")
            }
        } catch (e: Exception) {
            Log.d("listenForWindowAcknowledgementSize","Error while reading RTMP message: ${e.message}")
        }
    }
    fun listenForWindowAcknowledgementSize(inputStream: InputStream) {
        try {
            val header = ByteArray(12)
            var bytesRead = 0

            // Read the RTMP header, ensuring we get all 12 bytes
            while (bytesRead < 12) {
                val result = inputStream.read(header, bytesRead, 12 - bytesRead)
                if (result == -1) {
                    throw EOFException("Unexpected end of stream while reading RTMP header.")
                }
                bytesRead += result
            }

            // Extract message type (Message Type is 1 byte at position 7 in the header)
            val messageType = header[7].toInt()

            if (messageType == 5) { // Window Acknowledgement Size (Message Type 5)
                val windowSizeBytes = ByteArray(4)
                bytesRead = 0

                // Read the Window Acknowledgement Size (next 4 bytes)
                while (bytesRead < 4) {
                    val result = inputStream.read(windowSizeBytes, bytesRead, 4 - bytesRead)
                    if (result == -1) {
                        throw EOFException("Unexpected end of stream while reading Window Acknowledgement Size.")
                    }
                    bytesRead += result
                }

                // Convert the byte array to an integer (window size)
                val windowSize = ((windowSizeBytes[0].toInt() and 0xFF) shl 24) or
                        ((windowSizeBytes[1].toInt() and 0xFF) shl 16) or
                        ((windowSizeBytes[2].toInt() and 0xFF) shl 8) or
                        (windowSizeBytes[3].toInt() and 0xFF)

                Log.d("listenForWindowAcknowledgementSize","Received Window Acknowledgement Size: $windowSize bytes")
            } else {
                Log.d("listenForWindowAcknowledgementSize","Received non-Window Acknowledgement message type: $messageType")
            }
        } catch (e: IOException) {
            Log.d("listenForWindowAcknowledgementSize","Error while reading RTMP message: ${e.message}")
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

    // Helper function to read 3-byte unsigned integer (for message length)


}

// AMF0 Encoding Utility (Minimal example)
class AMF0Encoder {
    fun encode(command: String, transactionId: Int, commandObject: Map<String, Any>): ByteArray {
        val buffer = ByteBuffer.allocate(1024)

        // Encode command name (string)
        buffer.put(0x02) // AMF0 string marker
        buffer.putShort(command.length.toShort())
        buffer.put(command.toByteArray(Charsets.UTF_8))

        // Encode transaction ID (number)
        buffer.put(0x00) // AMF0 number marker
        buffer.putDouble(transactionId.toDouble())

        // Encode command object (AMF0 object)
        buffer.put(0x03) // AMF0 object marker
        for ((key, value) in commandObject) {
            buffer.putShort(key.length.toShort())
            buffer.put(key.toByteArray(Charsets.UTF_8))
            when (value) {
                is String -> {
                    buffer.put(0x02) // String marker
                    buffer.putShort(value.length.toShort())
                    buffer.put(value.toByteArray(Charsets.UTF_8))
                }
                is Number -> {
                    buffer.put(0x00) // Number marker
                    buffer.putDouble(value.toDouble())
                }
                is Boolean -> {
                    buffer.put(0x01) // Boolean marker
                    buffer.put(if (value) 1 else 0)
                }
            }
        }
        buffer.put(0x00) // Object end marker
        buffer.put(0x00)
        buffer.put(0x09)

        return buffer.array().copyOf(buffer.position())
    }
}


