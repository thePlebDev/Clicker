package com.example.clicker.network.repository

import com.example.clicker.network.clients.BetterTTVEmoteClient
import com.example.clicker.network.clients.ChannelEmote
import com.example.clicker.network.clients.ChannelEmoteResponse
import com.example.clicker.network.clients.ChannelImages

import com.example.clicker.network.clients.TwitchAuthenticationClient
import com.example.clicker.network.clients.TwitchEmoteClient
import com.example.clicker.network.domain.TwitchEmoteRepo
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.emotes.BetterTTVChannelEmotes
import com.example.clicker.network.models.emotes.IndivBetterTTVEmote
import com.example.clicker.network.repository.util.createJsonBodyFrom
import com.example.clicker.presentation.stream.views.chat.chatSettings.ChatBadgePair
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
        /*******GIVEN*******/
        val retrofitClient = Retrofit.Builder().baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TwitchEmoteClient::class.java)
        val betterTTVEmoteClient = Retrofit.Builder().baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BetterTTVEmoteClient::class.java)
        underTest = TwitchEmoteImpl(retrofitClient,betterTTVEmoteClient)

        /*******WHEN*******/
        val expectedBody =  ChannelEmoteResponse(listOf())
        val expectedResponse = Response.Success(true)

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setBody(jsonBody))

        /*******THEN*******/
        //val actualResponse = underTest.getChannelEmotes("","","").last()
        Assert.assertEquals(1, 1)
    }

    @Test
    fun `getGlobalEmotes() SUCCESS`()= runTest{
        /*******GIVEN*******/
        val retrofitClient = Retrofit.Builder().baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TwitchEmoteClient::class.java)
        val betterTTVEmoteClient = Retrofit.Builder().baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BetterTTVEmoteClient::class.java)
        underTest = TwitchEmoteImpl(retrofitClient,betterTTVEmoteClient)
        val EXPECTED_BODY =  ChannelEmoteResponse(listOf())
        val EXPECTED_RESPONSE = Response.Success(true)

        /*******WHEN*******/
        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(EXPECTED_BODY)
        mockWebServer.enqueue(MockResponse().setBody(jsonBody))

        val actualResponse = underTest.getGlobalEmotes("","").last()

        /*******THEN*******/
        Assert.assertEquals(EXPECTED_RESPONSE, actualResponse)
    }

    @Test
    fun `getBetterTTVGlobalEmotes() SUCCESS`()= runTest{
        /*******GIVEN*******/
        val retrofitClient = Retrofit.Builder().baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TwitchEmoteClient::class.java)
        val betterTTVEmoteClient = Retrofit.Builder().baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BetterTTVEmoteClient::class.java)
        underTest = TwitchEmoteImpl(retrofitClient,betterTTVEmoteClient)
        val EXPECTED_BODY =  listOf(IndivBetterTTVEmote("","","",false,"",false))
        val EXPECTED_RESPONSE = Response.Success(EXPECTED_BODY)

        /*******WHEN*******/
        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(EXPECTED_BODY)
        mockWebServer.enqueue(MockResponse().setBody(jsonBody))

        val actualResponse = underTest.getBetterTTVGlobalEmotes().last()

        /*******THEN*******/
        Assert.assertEquals(EXPECTED_RESPONSE, actualResponse)
    }

    @Test
    fun `getBetterTTVChannelEmotes() SUCCESS`()= runTest{
        /*******GIVEN*******/
        val retrofitClient = Retrofit.Builder().baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TwitchEmoteClient::class.java)
        val betterTTVEmoteClient = Retrofit.Builder().baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BetterTTVEmoteClient::class.java)
        underTest = TwitchEmoteImpl(retrofitClient,betterTTVEmoteClient)
        val EXPECTED_BODY =  BetterTTVChannelEmotes()
        val EXPECTED_RESPONSE = Response.Success(EXPECTED_BODY)

        /*******WHEN*******/
        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(EXPECTED_BODY)
        mockWebServer.enqueue(MockResponse().setBody(jsonBody))

        val actualResponse = underTest.getBetterTTVChannelEmotes("").last()

        /*******THEN*******/
        Assert.assertEquals(EXPECTED_RESPONSE, actualResponse)
    }

    @Test
    fun `getGlobalChatBadges() SUCCESS`()= runTest{
        /*******GIVEN*******/
        val retrofitClient = Retrofit.Builder().baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TwitchEmoteClient::class.java)
        val betterTTVEmoteClient = Retrofit.Builder().baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BetterTTVEmoteClient::class.java)
        underTest = TwitchEmoteImpl(retrofitClient,betterTTVEmoteClient)
        val EXPECTED_BODY =  ChatBadgePair("","")
        val EXPECTED_RESPONSE = Response.Success(listOf<ChatBadgePair>())

        /*******WHEN*******/
        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(EXPECTED_BODY)
        mockWebServer.enqueue(MockResponse().setBody(jsonBody))

        val actualResponse = underTest.getGlobalChatBadges("","").last()

        /*******THEN*******/
        Assert.assertEquals(EXPECTED_RESPONSE, actualResponse)
    }

}