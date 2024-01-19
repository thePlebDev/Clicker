package com.example.clicker.network

import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.twitchRepo.FollowedLiveStreams
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.network.repository.TwitchRepoImpl
import com.example.clicker.util.Response
import com.google.gson.Gson
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TwitchRepoImplTest {
    object RetrofitHelper {

        fun testClientInstance(url: String): TwitchClient {
            return Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(TwitchClient::class.java)
        }
    }

    private lateinit var underTest: TwitchRepo
    private lateinit var twitchClient: TwitchClient
    private lateinit var mockWebServer: MockWebServer

//
    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        twitchClient = RetrofitHelper.testClientInstance(mockWebServer.url("/").toString())
        underTest = TwitchRepoImpl(twitchClient)
    }

//
    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testingGetAllFollowedStreamsSuccess() = runTest {
        /**GIVEN*/

        val response = FollowedLiveStreams(
            data = listOf<StreamData>()
        )
        val expectedJson = Gson().toJson(response)

        // Enqueue a MockResponse with the expected JSON
        val expectedServerResponse = MockResponse()
            .setResponseCode(200)
            .setBody(expectedJson)
        mockWebServer.enqueue(expectedServerResponse)

        /**WHEN*/
        val actualResponse = underTest.getFollowedLiveStreams("dsfgsg", "trewtfds", "gfdsgf").last()

        /**THEN*/
        assertEquals(Response.Success(response), actualResponse)
    }




}
