package com.example.clicker.network.repository

import android.util.Log
import com.example.clicker.network.clients.GetModChannels
import com.example.clicker.network.clients.TwitchAuthenticationClient
import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.clients.TwitchHomeClient
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.network.models.twitchRepo.FollowedLiveStreams
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.network.models.twitchRepo.toStreamInfo
import com.example.clicker.network.repository.util.TwitchHomeClientBuilder
import com.example.clicker.network.repository.util.createJsonBodyFrom
import com.example.clicker.presentation.home.StreamInfo

import com.example.clicker.presentation.stream.util.Scanner
import com.example.clicker.presentation.stream.util.Token
import com.example.clicker.presentation.stream.util.TokenType
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.Response
import com.google.gson.Gson
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.jupiter.api.BeforeEach
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TwitchRepoImplTest {

    private lateinit var underTest: TwitchRepo
    private lateinit var mockWebServer: MockWebServer

    /**WHAT TO TEST FOR getModeratedChannels() AND getFollowedLiveStreams()*/
    //1) success with interceptors (DONE FOR BOTH)
    //2) network interceptor throws errors (DONE FOR BOTH)
    //3) 401 interceptor throws error (DONE FOR BOTH)
    //4) 500 response error (DONE FOR BOTH

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
    fun `calls getModeratedChannels() but Authentication401Interceptor throws exception`() = runTest {

        // Arrange - setup mocks and dependencies for each test
        val retrofitClient = TwitchHomeClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(true)
            .build()
        underTest = TwitchRepoImpl(retrofitClient)

        // The expected response and body from calling underTest.validateToken("", "")
        val expectedBody = GetModChannels(data = listOf())
        val expectedResponse = NetworkAuthResponse.Auth401Failure(Exception("Authentication error, please try again later"))

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setBody(jsonBody))

        // Act - perform the test operation
        val actualResponse = underTest.getModeratedChannels("", "", "").last()

        // Assert - verify the expected outcome
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }


    @Test//PASS
    fun `when getModeratedChannels() returns a successful response with all interceptors`()= runTest{
        // make the retrofit client
        /**GIVEN*/
        val retrofitClient:TwitchHomeClient = TwitchHomeClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchRepoImpl(retrofitClient)

        //The expected response and body from calling underTest.validateToken("","")
        val expectedBody = GetModChannels(data= listOf())
        val expectedResponse = NetworkAuthResponse.Success(expectedBody)

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setBody(jsonBody))

        /**WHEN*/
        val actualResponse = underTest.getModeratedChannels("","","").last()


        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test//PASS
    fun `calls getModeratedChannels() but NetworkInterceptor throws exception`()= runTest{
        /**GIVEN*/
        val retrofitClient:TwitchHomeClient = TwitchHomeClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(false)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchRepoImpl(retrofitClient)

        //The expected response and body from calling underTest.validateToken("","")
        val expectedBody = GetModChannels(data= listOf())
        val expectedResponse = NetworkAuthResponse.NetworkFailure(Exception("Network error, please try again later"))

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setBody(jsonBody))

        /**WHEN*/
        val actualResponse = underTest.getModeratedChannels("","","").last()


        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }
    @Test //PASS
    fun `calls getModeratedChannels() but the call is not successful 500 response code`()= runTest{
        // make the retrofit client
        /**GIVEN*/
        val retrofitClient = TwitchHomeClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchRepoImpl(retrofitClient)

        //The expected response and body from calling underTest.validateToken("","")
        val expectedBody = GetModChannels(data= listOf())
        val expectedResponse = NetworkAuthResponse.Failure(Exception("Error!, Please try again"))

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        /**WHEN*/
        val actualResponse = underTest.getModeratedChannels("","","").last()


        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }


//todo: place the test here when done

    /*************************************** BELOW HERE SHOULD BE ONLY getFollowedLiveStreams() TESTS***********************************************/

    @Test //PASS
    fun `when getFollowedLiveStreams() returns a successful response with all interceptors`()= runTest{
        // make the retrofit client

        /**GIVEN*/
        val retrofitClient = TwitchHomeClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchRepoImpl(retrofitClient)

        val expectedBody = FollowedLiveStreams(data =listOf<StreamData>(
            StreamData("", "","","",
                "","","","",0,
                "","","", listOf(""), listOf(""),false
            )
        ))

        val testing = StreamData("", "","","", "","","","",0,
            "","","", listOf(""), listOf(""),false
        )
        val expectedResponse = NetworkAuthResponse.Success(listOf(testing))


        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setBody(jsonBody))

        /**WHEN*/
        val actualResponse = underTest.getFollowedLiveStreams("","","").last()


        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test //PASS
    fun ` how toStreamInfo() handles an empty body`()= runTest{
        // make the retrofit client

        /**GIVEN*/
        val emptyBody = FollowedLiveStreams(listOf<StreamData>())
        val expectedResponse = listOf<StreamInfo>()


        /**WHEN*/
        val actualResponse = emptyBody.data.map { it.toStreamInfo() }


        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }




    @Test //PASS
    fun `calls getFollowedLiveStreams() but NetworkInterceptor throws exception`()= runTest{
        /**GIVEN*/
        val retrofitClient = TwitchHomeClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(false)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchRepoImpl(retrofitClient)

        //The expected response

        val expectedResponse = NetworkAuthResponse.NetworkFailure(Exception("Network error! Pull down to refresh"))


        /**WHEN*/
        val actualResponse = underTest.getFollowedLiveStreams("","","").last()


        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test //PASS
    fun `calls getFollowedLiveStreams() but Authentication401Interceptor throws exception`()= runTest{
        /**GIVEN*/

        val retrofitClient = TwitchHomeClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(true)
            .build()
        underTest = TwitchRepoImpl(retrofitClient)

        //The expected response and body from calling underTest.validateToken("","")
        val expectedBody = GetModChannels(data= listOf())
        val expectedResponse = NetworkAuthResponse.Auth401Failure(Exception("Error! Re-login with Twitch"))

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setBody(jsonBody))

        /**WHEN*/
        val actualResponse = underTest.getFollowedLiveStreams("","","").last()


        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test //PASS
    fun `calls getFollowedLiveStreams() but the call is not successful 500 response code`()= runTest{
        /**GIVEN*/

        val retrofitClient = TwitchHomeClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchRepoImpl(retrofitClient)

        //The expected response and body from calling underTest.validateToken("","")
        val expectedBody = GetModChannels(data= listOf())
        val expectedResponse = NetworkAuthResponse.Failure(Exception("Error!, Please try again"))

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        /**WHEN*/
        val actualResponse = underTest.getFollowedLiveStreams("","","").last()


        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }


}
