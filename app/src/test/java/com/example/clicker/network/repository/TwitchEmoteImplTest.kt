package com.example.clicker.network.repository

import com.example.clicker.network.clients.BetterTTVEmoteClient
import com.example.clicker.network.clients.TwitchAuthenticationClient
import com.example.clicker.network.clients.TwitchEmoteClient
import com.example.clicker.network.domain.TwitchEmoteRepo
import com.example.clicker.network.domain.TwitchRepo
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
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
    fun `testing to see if it will work`()= runTest{
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
        /**WHEN*/
        //val actualResponse = underTest.getChannelEmotes("","","").last()

        Assert.assertEquals(1, 1)
    }

    @Test
    fun `Testing to see if this will work with the URL`(){
        val url ="https://static-cdn.jtvnw.net/cf_vods/d2nvs31859zcd8/c95762008d772477a222_hasanabi_44729954811_1724617099//thumb/thumb0-%{width}x%{height}.jpg"




        Assert.assertEquals(1, 1)
    }
}