package com.example.clicker.network.repository

import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.network.interceptors.NetworkMonitor
import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
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


class NetworkTesting(): NetworkMonitor {
    override fun isConnected(): Boolean {
        return false
    }

}



class TwitchAuthenticationImplTest {
    private lateinit var underTest: TwitchAuthentication
    private lateinit var twitchClient: TwitchClient
    private lateinit var mockWebServer: MockWebServer

    object RetrofitHelper {
        val loggingClient = OkHttpClient.Builder()
            //.addInterceptor(NetworkMonitorInterceptor(NetworkTesting()))
            .build()

        fun testClientInstance(url: String): TwitchClient {
            return Retrofit.Builder()
                .baseUrl(url)
                .client(loggingClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(TwitchClient::class.java)
        }
    }
    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        twitchClient = RetrofitHelper.testClientInstance(mockWebServer.url("/").toString())
        underTest = TwitchAuthenticationImpl(twitchClient)
    }

    //
    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun validateTokenNoNetworkResponse()= runTest {
        /**GIVEN*/

        val response = ValidatedUser("","", listOf(),"",2)
        val expectedJson = Gson().toJson(response)

        // Enqueue a MockResponse with the expected JSON
        val expectedServerResponse = MockResponse()
            .setResponseCode(200)
            .setBody(expectedJson)
        mockWebServer.enqueue(expectedServerResponse)

        /**WHEN*/
        val dynamicUrl = "/dynamic/endpoint"
        val actualResponse = underTest.validateToken("","").last()
        val expectedResponse =Response.Success(response)

        Assert.assertEquals(expectedResponse, actualResponse)
    }
}


