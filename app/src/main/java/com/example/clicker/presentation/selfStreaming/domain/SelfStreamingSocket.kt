package com.example.clicker.presentation.selfStreaming.domain

interface SelfStreamingSocket {

    fun runWebSocket()

    fun closeWebSocket()
}