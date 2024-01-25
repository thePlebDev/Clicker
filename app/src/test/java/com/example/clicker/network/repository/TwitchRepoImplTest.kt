package com.example.clicker.network.repository

import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.interceptors.LoggingInterceptor
import com.example.clicker.network.models.twitchRepo.FollowedLiveStreams
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.network.repository.util.TwitchStreamClientBuilder
import com.example.clicker.util.Response
import com.example.clicker.util.objectMothers.IndividualAutoModSettingsDataObjectMother
import com.google.gson.Gson
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TwitchRepoImplTest {

    private lateinit var underTest: TwitchRepo
    private lateinit var twitchClient: TwitchClient
    private lateinit var mockWebServer: MockWebServer

//
    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

    }

//
    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getAllFollowedStreamsNoNetworkResponse() = runTest {
        twitchClient = TwitchStreamClientBuilder
            .addFailingNetworkInterceptor()
            .buildClientWithURL(mockWebServer.url("/").toString()
            )
        underTest = TwitchRepoImpl(twitchClient)
        val expectedResponse = Response.Failure(Exception("Network error, please try again later"))


        /**WHEN*/
        val actualResponse = underTest
            .getFollowedLiveStreams("","","")
            .last()


        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }




}
