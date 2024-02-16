package com.example.clicker.network.repository

import com.example.clicker.network.clients.BanUser
import com.example.clicker.network.clients.BanUserData
import com.example.clicker.network.clients.TwitchAuthenticationClient
import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.network.domain.TwitchStream
import com.example.clicker.network.models.twitchStream.ChatSettings
import com.example.clicker.network.models.twitchStream.ChatSettingsData
import com.example.clicker.network.models.twitchStream.UpdateChatSettings
import com.example.clicker.util.Response
import com.example.clicker.util.objectMothers.IndividualAutoModSettingsDataObjectMother
import com.google.gson.Gson
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


class TwitchStreamTest {

    private lateinit var underTest: TwitchStream
    private lateinit var twitchClient: TwitchClient
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
    fun getChatSettingsNoNetworkResponse()= runTest {
        /**GIVEN*/

    }

    @Test
    fun updateChatSettingsNoNetworkResponse()= runTest {
        /**GIVEN*/

    }
    @Test
    fun deleteChatMessageNoNetworkResponse()= runTest {
        /**GIVEN*/

    }
    @Test
    fun banUserNoNetworkResponse()= runTest {
        /**GIVEN*/

    }

    @Test
    fun unbanUserNoNetworkResponse()= runTest {
        /**GIVEN*/

    }
    @Test
    fun getAutoModSettingsNoNetworkResponse()= runTest {
        /**GIVEN*/

    }

    @Test
    fun updateAutoModSettingsNoNetworkResponse()= runTest {
        /**GIVEN*/

    }





}