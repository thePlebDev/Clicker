package com.example.clicker.network.repository

import com.example.clicker.network.domain.TwitchRepo
import okhttp3.mockwebserver.MockWebServer

class TwitchEmoteImplTest {

    private lateinit var underTest: TwitchRepo
    private lateinit var mockWebServer: MockWebServer
}