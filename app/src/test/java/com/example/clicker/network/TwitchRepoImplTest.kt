package com.example.clicker.network

import com.example.clicker.network.repository.TwitchRepoImpl
import com.example.clicker.presentation.home.StreamInfo
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import com.example.clicker.util.Response
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.RecordedRequest


class TwitchRepoImplTest {
    object RetrofitHelper {

        fun testApiInstance(): TwitchClient {
            return Retrofit.Builder()
                .baseUrl("https://api.twitch.tv/helix/")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(TwitchClient::class.java)
        }

    }
    //1) repository
    //2) its dependency
    //3) the mock web server

//    private lateinit var underTest:TwitchRepo
//    private lateinit var twitchClient: TwitchClient
//    private lateinit var mockWebServer: MockWebServer
//
//    @Before
//    fun setUp() {
//        mockWebServer = MockWebServer()
//        mockWebServer.start()
////        twitchClient = RetrofitHelper.testApiInstance
//        twitchClient= RetrofitHelper.testApiInstance(mockWebServer.url("/").toString())
//        underTest = TwitchRepoImpl(twitchClient)
//    }
//
//    @After
//    fun tearDown() {
//        mockWebServer.shutdown()
//    }

    @Test
    fun testingGetAllFollowedStreams()= runTest {
        /**GIVEN*/

        val mockWebServer = MockWebServer()
        mockWebServer.start()

        // Set up your API client with the base URL from the mock server
        val api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/")) // Use a relative URL because MockWebServer provides the absolute URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TwitchClient::class.java)


        val repository = TwitchRepoImpl(api)

        val expectedResponseJson = """
            {
                "data": [
                    {
                        "id": "1",
                        "title": "Stream 1",
                        "viewer_count": 100
                    },
                    {
                        "id": "2",
                        "title": "Stream 2",
                        "viewer_count": 150
                    }
                ]
            }
        """

        // Enqueue a MockResponse with the expected JSON
        val expectedResponse = MockResponse()
            .setResponseCode(200)
            .setBody(expectedResponseJson)
        mockWebServer.enqueue(expectedResponse)



        /**WHEN*/

        val actualResponse = repository.getFollowedLiveStreams("dsfgsg","trewtfds","gfdsgf").first()

        /**THEN*/

        assertEquals(expectedResponse, actualResponse)

        mockWebServer.shutdown()

    }

}
