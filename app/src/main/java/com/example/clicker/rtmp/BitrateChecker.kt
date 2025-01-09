package com.example.clicker.rtmp

interface BitrateChecker {
    fun onNewBitrate(bitrate: Long) {}
}