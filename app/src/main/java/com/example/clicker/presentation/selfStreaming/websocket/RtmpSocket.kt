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

    abstract suspend fun connect() // todo: THIS IS FIRST ONE I CARE ABOUT
    abstract suspend fun close()

}