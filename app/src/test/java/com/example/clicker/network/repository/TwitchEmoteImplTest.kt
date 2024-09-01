package com.example.clicker.network.repository

import com.example.clicker.network.clients.BetterTTVEmoteClient
import com.example.clicker.network.clients.ChannelEmote
import com.example.clicker.network.clients.ChannelEmoteResponse
import com.example.clicker.network.clients.ChannelImages
import com.example.clicker.network.clients.GetModChannels
import com.example.clicker.network.clients.TwitchAuthenticationClient
import com.example.clicker.network.clients.TwitchEmoteClient
import com.example.clicker.network.domain.TwitchEmoteRepo
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.repository.util.createJsonBodyFrom
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TwitchEmoteImplTest {

    private lateinit var underTest: TwitchEmoteRepo
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }


    @Test
    fun `underTest_getChannelEmotes() SUCCESS`()= runTest{
        val retrofitClient = Retrofit.Builder().baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TwitchEmoteClient::class.java)
        val betterTTVEmoteClient = Retrofit.Builder().baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BetterTTVEmoteClient::class.java)

        underTest = TwitchEmoteImpl(retrofitClient,betterTTVEmoteClient)
        /**WHEN*/

        val  singleEmote =ChannelEmote("","", ChannelImages("","",""), listOf(), listOf(),
            listOf(),"subscriptions"
        )
        val expectedBody =  ChannelEmoteResponse(listOf())
        val expectedResponse = Response.Success(true)

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setBody(jsonBody))

        val actualResponse = underTest.getChannelEmotes("","","").last()

        Assert.assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `Testing to see if this will work with the URL`(){
        val url ="https://static-cdn.jtvnw.net/cf_vods/d2nvs31859zcd8/c95762008d772477a222_hasanabi_44729954811_1724617099//thumb/thumb0-%{width}x%{height}.jpg"




        Assert.assertEquals(1, 1)
    }
}