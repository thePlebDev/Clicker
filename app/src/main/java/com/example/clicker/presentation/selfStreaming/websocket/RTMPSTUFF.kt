package com.example.clicker.presentation.selfStreaming.websocket


/* needs to fit largest number of bytes recv() may return */
val  RTMP_BUFFER_CACHE_SIZE =(16*1024)
val RTMP_MAX_HEADER_SIZE =18

data class RTMP(
    var mInChunkSize: Int = 0,
    var mOutChunkSize: Int = 0,
    var mNBWCheckCounter: Int = 0,
    var mNBytesIn: Int = 0,
    var mNBytesInSent: Int = 0,
    var mNBufferMS: Int = 0,
    var mStreamId: Int = 0,  // Returned in _result from createStream
    var mMediaChannel: Int = 0,
    var mMediaStamp: UInt = 0u,
    var mPauseStamp: UInt = 0u,
    var mPausing: Int = 0,
    var mNServerBW: Int = 0,
    var mNClientBW: Int = 0,
    var mNClientBW2: Byte = 0,
    var mBPlaying: Byte = 0,
    var mBSendEncoding: Byte = 0,
    var mBSendCounter: Byte = 0,

    var mNumInvokes: Int = 0,
    var mNumCalls: Int = 0,
    var mMethodCalls: Array<RTMP_METHOD>? = null, // Assuming RTMP_METHOD is a defined data class

    var mChannelsAllocatedIn: Int = 0,
    var mChannelsAllocatedOut: Int = 0,
    var mVecChannelsIn: Array<RTMPPacket>? = null,
    var mVecChannelsOut: Array<RTMPPacket>? = null,
    var mChannelTimestamp: IntArray? = null, // Using IntArray for primitive array efficiency

    var mFAudioCodecs: Double = 0.0,
    var mFVideoCodecs: Double = 0.0,
    var mFEncoding: Double = 0.0, // AMF0 or AMF3

    var mFDuration: Double = 0.0, // Duration of stream in seconds

    var mMsgCounter: Int = 0, // RTMPT stuff
    var mPolling: Int = 0,
    var mResplen: Int = 0,
    var mUnackd: Int = 0,
    var mClientID: AVal? = null, // Assuming AVal is a defined data class

    var mRead: RTMP_READ? = null,
    var mWrite: RTMPPacket? = null,
    var mSb: RTMPSockBuf? = null,
    var link: RTMPLink? = null
)
data class RTMPPacket(
    var headerType: UByte = 0u,
    var packetType: UByte = 0u,
    var hasAbsTimestamp: UByte = 0u, // 0 or 1 (absolute or relative timestamp)
    var nChannel: Int = 0,
    var nTimeStamp: UInt = 0u, // Unsigned 32-bit timestamp
    var nInfoField2: Int = 0, // Signed 32-bit
    var nBodySize: UInt = 0u, // Unsigned 32-bit
    var nBytesRead: UInt = 0u, // Unsigned 32-bit
    var chunk: RTMPChunk? = null, // Assuming RTMPChunk is another Kotlin data class
    var body: ByteArray? = null // Replaces `char*` with ByteArray for safe memory handling
)

data class RTMPSockBuf(
    var socket: Int = -1, // Socket file descriptor
    var size: Int = 0, // Number of unprocessed bytes
    var start: Int = 0, // Index into `buffer` (replacing char pointer)
    var buffer: ByteArray = ByteArray(RTMP_BUFFER_CACHE_SIZE), // Fixed-size buffer
    var timedOut: Boolean = false, // Replacing `int sb_timedout` with Boolean
    var ssl: Any? = null // Placeholder for SSL context (platform-specific)
)
data class RTMPChunk(
    var headerSize: Int = 0,
    var chunkSize: Int = 0,
    var chunk: ByteArray? = null, // Replacing `char* c_chunk` with ByteArray
    var header: ByteArray = ByteArray(RTMP_MAX_HEADER_SIZE) // Fixed-size header buffer
)
data class RTMP_READ(
    var buf: ByteArray? = null, // Replacing `char* buf`
    var bufPos: Int = 0, // Replacing `char* bufpos` with an index
    var bufLen: UInt = 0u, // Unsigned buffer length
    var timestamp: UInt = 0u, // Unsigned 32-bit timestamp
    var dataType: UByte = 0u, // Unsigned 8-bit
    var flags: UByte = 0u, // Bit flags

    var status: Byte = 0, // Using Byte (-128 to 127) for status codes

    // If `bResume == TRUE`
    var initialFrameType: UByte = 0u, // Unsigned 8-bit
    var nResumeTS: UInt = 0u, // Unsigned 32-bit timestamp
    var metaHeader: ByteArray? = null, // Replacing `char* metaHeader`
    var initialFrame: ByteArray? = null, // Replacing `char* initialFrame`
    var nMetaHeaderSize: UInt = 0u, // Unsigned 32-bit
    var nInitialFrameSize: UInt = 0u, // Unsigned 32-bit
    var nIgnoredFrameCounter: UInt = 0u, // Unsigned 32-bit
    var nIgnoredFlvFrameCounter: UInt = 0u // Unsigned 32-bit
) {
    companion object {
        // Flag constants
        const val READ_HEADER = 0x01
        const val READ_RESUME = 0x02
        const val READ_NO_IGNORE = 0x04
        const val READ_GOTKF = 0x08
        const val READ_GOTFLVK = 0x10
        const val READ_SEEKING = 0x20

        // Status constants
        const val READ_COMPLETE: Byte = -3
        const val READ_ERROR: Byte = -2
        const val READ_EOF: Byte = -1
        const val READ_IGNORE: Byte = 0
    }
}

data class RTMPLink(
    var hostname: String = "",
    var sockshost: String = "",

    var playpath0: String = "", // Parsed from URL
    var playpath: String = "", // Explicitly passed
    var tcUrl: String = "",
    var swfUrl: String = "",
    var pageUrl: String = "",
    var app: String = "",
    var auth: String = "",
    var flashVer: String = "",
    var subscribePath: String = "",
    var usherToken: String = "",
    var token: String = "",
    var pubUser: String = "",
    var pubPasswd: String = "",

    var extras: AMFObject? = null, // Replacing AMFObject with nullable type
    var edepth: Int = 0,

    var seekTime: Int = 0,
    var stopTime: Int = 0,

    var lFlags: Int = 0, // Flags bitmask
    var swfAge: Int = 0,

    var protocol: Int = 0,
    var receiveTimeoutInMs: Int = 0,
    var sendTimeoutInMs: Int = 0,

    var pFlags: Int = 0, // Publisher flags

    var socksPort: UShort = 0u,
    var port: UShort = 0u,

    // Crypto-related fields (optional)
    var dh: Any? = null, // Placeholder for encryption
    var rc4keyIn: Any? = null,
    var rc4keyOut: Any? = null,
    var swfSize: UInt = 0u,
    var swfHash: ByteArray = ByteArray(RTMP_SWF_HASHLEN),
    var swfVerificationResponse: ByteArray = ByteArray(RTMP_SWF_HASHLEN + 10)
) {
    companion object {
        // lFlags definitions
        const val LF_AUTH = 0x0001 // Using auth param
        const val LF_LIVE = 0x0002 // Stream is live
        const val LF_SWFV = 0x0004 // SWF verification
        const val LF_PLST = 0x0008 // Send playlist before play
        const val LF_BUFX = 0x0010 // Toggle stream on BufferEmpty msg
        const val LF_FTCU = 0x0020 // Free tcUrl on close

        // pFlags definitions
        const val PUB_NAME = 0x0001 // Send login to server
        const val PUB_RESP = 0x0002 // Send salted password hash
        const val PUB_ALLOC = 0x0004 // Allocated data for new tcUrl & app
        const val PUB_CLEAN = 0x0008 // Free allocated data for newer tcUrl & app at exit
        const val PUB_CLATE = 0x0010 // Late clean tcUrl & app at exit

        const val RTMP_SWF_HASHLEN = 32 // Hash length for SWF verification
    }
}
data class AMFObjectProperty(
    val pName: AVal,      // Equivalent to p_name (name of the property)
    val pType: AMFDataType,  // Equivalent to p_type (type of the property)
    val pValue: AMFValue  // Equivalent to the union, can be of multiple types
)

// Sealed class to represent the possible types for pValue (union)
sealed class AMFValue {
    data class Number(val value: Double): AMFValue()  // AMF_NUMBER
    data class Boolean(val value: Boolean): AMFValue()  // AMF_BOOLEAN
    data class String(val value: String): AMFValue()  // AMF_STRING
    data class Object(val value: Any): AMFValue()  // AMF_OBJECT
    data class Null(val value: Unit = Unit): AMFValue()  // AMF_NULL
    // Add other types as needed (e.g., for AMF_ECMA_ARRAY, AMF_DATE, etc.)
}
data class AMFObject(
    val oNum: Int,               // Equivalent to o_num (number of properties)
    val oProps: List<AMFObjectProperty> // Equivalent to o_props (list of properties)
)
data class AVal(
    val avVal: String,  // Equivalent to av_val (string data)
    val avLen: Int      // Equivalent to av_len (length of the string)
)

data class RTMP_METHOD(
    val name: AVal,  // Equivalent to AVal (name of the method)
    val num: Int     // Equivalent to num (number associated with the method)
)
enum class AMFDataType(val value: Int) {
    AMF_NUMBER(0),
    AMF_BOOLEAN(1),
    AMF_STRING(2),
    AMF_OBJECT(3),
    AMF_MOVIECLIP(4),  // Reserved, not used
    AMF_NULL(5),
    AMF_UNDEFINED(6),
    AMF_REFERENCE(7),
    AMF_ECMA_ARRAY(8),
    AMF_OBJECT_END(9),
    AMF_STRICT_ARRAY(10),
    AMF_DATE(11),
    AMF_LONG_STRING(12),
    AMF_UNSUPPORTED(13),
    AMF_RECORDSET(14),  // Reserved, not used
    AMF_XML_DOC(15),
    AMF_TYPED_OBJECT(16),
    AMF_AVMPLUS(17),  // Switch to AMF3
    AMF_INVALID(0xFF);

    companion object {
        fun fromInt(value: Int): AMFDataType? {
            return values().find { it.value == value }
        }
    }
}