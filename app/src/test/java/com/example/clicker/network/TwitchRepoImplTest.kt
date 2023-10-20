package com.example.clicker.network

import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.repository.TwitchRepoImpl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import java.net.HttpURLConnection
import org.junit.Assert.*

class TwitchRepoImplTest {
    object RetrofitHelper {

        fun testApiInstance(baseUrl: String): TwitchClient {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .build()
                .create(TwitchClient::class.java)
        }

    }
    //1) repository
    //2) its dependency
    //3) the mock web server

    private lateinit var underTest:TwitchRepo
    private lateinit var twitchClient: TwitchClient
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
//        twitchClient = RetrofitHelper.testApiInstance
        twitchClient= RetrofitHelper.testApiInstance(mockWebServer.url("/").toString())
        underTest = TwitchRepoImpl(twitchClient)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testingGetAllFollowedStreams(){
        /**GIVEN*/
        val expectedResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
        mockWebServer.enqueue(expectedResponse)

        /**WHEN*/
       // val actualResponse = underTest.getFollowedLiveStreams("","","")

        /**THEN*/
        assertEquals(true, true)

    }




}