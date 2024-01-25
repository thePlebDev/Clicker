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
import com.example.clicker.network.repository.util.TwitchAuthenticationClientBuilder
import com.example.clicker.network.repository.util.TwitchStreamClientBuilder
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
        twitchClient = TwitchStreamClientBuilder
            .addFailingNetworkInterceptor()
            .buildClientWithURL(mockWebServer.url("/").toString()
            )
        underTest = TwitchStreamImpl(twitchClient)
        val expectedResponse = Response.Failure(Exception("Network error, please try again later"))

        /**WHEN*/
        val actualResponse = underTest.getChatSettings("","","").last()


        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun updateChatSettingsNoNetworkResponse()= runTest {
        /**GIVEN*/
        twitchClient = TwitchStreamClientBuilder
            .addFailingNetworkInterceptor()
            .buildClientWithURL(mockWebServer.url("/").toString()
            )
        underTest = TwitchStreamImpl(twitchClient)
        val expectedResponse = Response.Failure(Exception("Network error, please try again later"))
        val updateUpdateChatSettings = UpdateChatSettings(true,true,true,true)

        /**WHEN*/
        val actualResponse = underTest
            .updateChatSettings("","","","",updateUpdateChatSettings)
            .last()


        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }
    @Test
    fun deleteChatMessageNoNetworkResponse()= runTest {
        /**GIVEN*/
        twitchClient = TwitchStreamClientBuilder
            .addFailingNetworkInterceptor()
            .buildClientWithURL(mockWebServer.url("/").toString()
            )
        underTest = TwitchStreamImpl(twitchClient)
        val expectedResponse = Response.Failure(Exception("Network error, please try again later"))

        /**WHEN*/
        val actualResponse = underTest
            .deleteChatMessage("","","","","")
            .last()


        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }
    @Test
    fun banUserNoNetworkResponse()= runTest {
        /**GIVEN*/
        twitchClient = TwitchStreamClientBuilder
            .addFailingNetworkInterceptor()
            .buildClientWithURL(mockWebServer.url("/").toString()
            )
        underTest = TwitchStreamImpl(twitchClient)
        val expectedResponse = Response.Failure(Exception("Network error, please try again later"))
        val mockBannedUser = BanUser(
            data= BanUserData("","",0)
        )

        /**WHEN*/
        val actualResponse = underTest
            .banUser("","","","",mockBannedUser)
            .last()


        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun unbanUserNoNetworkResponse()= runTest {
        /**GIVEN*/
        twitchClient = TwitchStreamClientBuilder
            .addFailingNetworkInterceptor()
            .buildClientWithURL(mockWebServer.url("/").toString()
            )
        underTest = TwitchStreamImpl(twitchClient)
        val expectedResponse = Response.Failure(Exception("Network error, please try again later"))


        /**WHEN*/
        val actualResponse = underTest
            .unBanUser("","","","","")
            .last()


        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }
    @Test
    fun getAutoModSettingsNoNetworkResponse()= runTest {
        /**GIVEN*/
        twitchClient = TwitchStreamClientBuilder
            .addFailingNetworkInterceptor()
            .buildClientWithURL(mockWebServer.url("/").toString()
            )
        underTest = TwitchStreamImpl(twitchClient)
        val expectedResponse = Response.Failure(Exception("Network error, please try again later"))


        /**WHEN*/
        val actualResponse = underTest
            .getAutoModSettings("","","","")
            .last()


        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun updateAutoModSettingsNoNetworkResponse()= runTest {
        /**GIVEN*/
        twitchClient = TwitchStreamClientBuilder
            .addFailingNetworkInterceptor()
            .buildClientWithURL(mockWebServer.url("/").toString()
            )
        underTest = TwitchStreamImpl(twitchClient)
        val expectedResponse = Response.Failure(Exception("Network error, please try again later"))
        val indivAutoModSettings = IndividualAutoModSettingsDataObjectMother.build()


        /**WHEN*/
        val actualResponse = underTest
            .updateAutoModSettings("","",indivAutoModSettings)
            .last()


        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }





}