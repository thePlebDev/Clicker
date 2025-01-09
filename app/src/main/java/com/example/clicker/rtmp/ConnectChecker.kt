package com.example.clicker.rtmp

interface ConnectChecker:BitrateChecker {
    fun onConnectionStarted(url: String)
    fun onConnectionSuccess()
    fun onConnectionFailed(reason: String)
    fun onDisconnect()
    fun onAuthError()
    fun onAuthSuccess()
}