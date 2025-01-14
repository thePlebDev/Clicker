package com.example.clicker.presentation.selfStreaming.websocket

/**
 * Socket implementation that accept:
 * - TCP
 * - TCP SSL/TLS
 * - UDP
 * - Tunneled HTTP
 * - Tunneled HTTPS
 */
abstract class RtmpSocket {

    abstract suspend fun flush(isPacket: Boolean = false)
    abstract suspend fun connect() // todo: THIS IS FIRST ONE I CARE ABOUT
    abstract suspend fun close()
    abstract fun isConnected(): Boolean
    abstract fun isReachable(): Boolean
    abstract suspend fun write(b: Int)
    abstract suspend fun write(b: ByteArray)
    abstract suspend fun write(b: ByteArray, offset: Int, size: Int)
    abstract suspend fun writeUInt16(b: Int)
    abstract suspend fun writeUInt24(b: Int)
    abstract suspend fun writeUInt32(b: Int)
    abstract suspend fun writeUInt32LittleEndian(b: Int)
    abstract suspend fun read(): Int
    abstract suspend fun readUInt16(): Int
    abstract suspend fun readUInt24(): Int
    abstract suspend fun readUInt32(): Int
    abstract suspend fun readUInt32LittleEndian(): Int
    abstract suspend fun readUntil(b: ByteArray)
}