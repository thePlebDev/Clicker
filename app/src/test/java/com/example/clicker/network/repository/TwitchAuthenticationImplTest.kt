package com.example.clicker.network.repository

import com.example.clicker.network.clients.TwitchAuthenticationClient
import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.network.interceptors.NetworkMonitor
import com.example.clicker.network.interceptors.NetworkMonitorInterceptor
import com.example.clicker.network.interceptors.NoNetworkException
import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.network.repository.util.TwitchAuthenticationClientBuilder
import com.example.clicker.util.NetworkResponse
import com.example.clicker.util.Response
import com.google.gson.Gson
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.QueueDispatcher
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.InetAddress
import java.net.InetSocketAddress


class TwitchAuthenticationImplTest {
    private lateinit var underTest: TwitchAuthentication
    private lateinit var twitchClient: TwitchAuthenticationClient
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
    fun validateTokenNoNetworkResponse()= runTest {

        /**GIVEN*/
        twitchClient = TwitchAuthenticationClientBuilder
            .addFailingNetworkInterceptor()
            .buildClientWithURL(mockWebServer.url("/").toString()
            )
        underTest = TwitchAuthenticationImpl(twitchClient)




        /**WHEN*/
        val actualResponse = underTest.validateToken("","").last()
        val expected = actualResponse is NetworkResponse.NetworkFailure


        /**THEN*/
        Assert.assertEquals(true, expected)
    }

    @Test
    fun validateLogoutNoNetworkResponse() = runTest {
        /**GIVEN*/
        twitchClient = TwitchAuthenticationClientBuilder
            .addFailingNetworkInterceptor()
            .buildClientWithURL(mockWebServer.url("/").toString()
            )
        underTest = TwitchAuthenticationImpl(twitchClient)
        val expectedResponse = Response.Failure(Exception("Network error, please try again later"))

        /**WHEN*/
        val actualResponse = underTest.logout("","").last()


        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())

    }
}


