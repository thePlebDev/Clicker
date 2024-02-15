package com.example.clicker.network.repository

import com.example.clicker.network.clients.TwitchAuthenticationClient
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.network.repository.util.TwitchAuthenticationClientBuilder
import com.example.clicker.network.repository.util.TwitchClientBuilderUtil
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.NetworkResponse
import com.example.clicker.util.Response
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


class TwitchAuthenticationImplTest {

    private lateinit var underTest: TwitchAuthentication
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

//



    /**TESTING TwitchAuthentication.validateToken()*/
    @Test
    fun `when validateToken() returns a successful response BETTER version`()= runTest{
        /**GIVEN*/
        val retrofitClient: TwitchAuthenticationClient = TwitchClientBuilderUtil
            .addMockedUrl(mockWebServer.url("/").toString())
            .build()
        underTest = TwitchAuthenticationImpl(retrofitClient)

        //The expected response and body from calling underTest.validateToken("","")
        val expectedBody = ValidatedUser("","", listOf(""),"",0)
        val expectedResponse = Response.Success(expectedBody)

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setBody(jsonBody))

        /**WHEN*/
        val actualResponse = underTest.validateToken("","").last()


        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())

    }

    fun <T> createJsonBodyFrom(body:T): String {
        val gson = Gson()
        return gson.toJson(body)
    }




    @Test
    fun `when validateToken() returns a successful response with all interceptors`()= runTest{

        /**GIVEN*/
        val retrofitClient: TwitchAuthenticationClient = TwitchClientBuilderUtil
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchAuthenticationImpl(retrofitClient)

        //The expected response and body from calling underTest.validateToken("","")
        val expectedBody = ValidatedUser("","", listOf(""),"",0)
        val expectedResponse = Response.Success(expectedBody)

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setBody(jsonBody))
        /**WHEN*/
        val actualResponse = underTest.validateToken("","").last()
        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())

    }

    @Test
    fun `when validateToken() is run but the NetworkInterceptor throws an error`()= runTest{

        /**GIVEN*/
        val retrofitClient: TwitchAuthenticationClient = TwitchClientBuilderUtil
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(false)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchAuthenticationImpl(retrofitClient)

        //The expected response and body from calling underTest.validateToken("","")
        val expectedBody = ValidatedUser("","", listOf(""),"",0)
        val expectedResponse = NetworkAuthResponse.NetworkFailure(Exception("Network error, please try again later"))

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setBody(jsonBody))
        /**WHEN*/
        val actualResponse = underTest.validateToken("","").last()
        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())

    }

    @Test
    fun `when validateToken() is run but the Authentication401Interceptor throws an error`()= runTest{

        /**GIVEN*/
        val retrofitClient: TwitchAuthenticationClient = TwitchClientBuilderUtil
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(true)
            .build()
        underTest = TwitchAuthenticationImpl(retrofitClient)

        //The expected response and body from calling underTest.validateToken("","")
        val expectedBody = ValidatedUser("","", listOf(""),"",0)
        val expectedResponse = NetworkAuthResponse.Auth401Failure(Exception("Authentication error, please try again later"))

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setBody(jsonBody))
        /**WHEN*/
        val actualResponse = underTest.validateToken("","").last()
        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())

    }

    @Test
    fun `when validateToken() is run but returns a 500 error code`()= runTest{

        /**GIVEN*/
        val retrofitClient: TwitchAuthenticationClient = TwitchClientBuilderUtil
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchAuthenticationImpl(retrofitClient)

        //The expected response and body from calling underTest.validateToken("","")
        val expectedBody = ValidatedUser("","", listOf(""),"",0)
        val expectedResponse = NetworkAuthResponse.Failure(Exception("Error! Please try again"))

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody(jsonBody))
        /**WHEN*/
        val actualResponse = underTest.validateToken("","").last()
        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())

    }


    /**TESTING TwitchAuthentication.logout()*/

    @Test
    fun `when logout() is run but NetworkInterceptor throw an error`()= runTest{

        /**GIVEN*/
        val retrofitClient: TwitchAuthenticationClient = TwitchClientBuilderUtil
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(false)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchAuthenticationImpl(retrofitClient)

        //The expected response and body from calling underTest.validateToken("","")
        val expectedBody = ValidatedUser("","", listOf(""),"",0)
        val expectedResponse = Response.Failure(Exception("Network error, please try again later"))

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody(jsonBody))
        /**WHEN*/
        val actualResponse = underTest.logout("","").last()
        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())

    }

    @Test
    fun `when logout() is run but Authentication401Interceptor throws an error`()= runTest{

        /**GIVEN*/
        val retrofitClient: TwitchAuthenticationClient = TwitchClientBuilderUtil
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(true)
            .build()
        underTest = TwitchAuthenticationImpl(retrofitClient)

        //The expected response and body from calling underTest.validateToken("","")
        val expectedBody = ValidatedUser("","", listOf(""),"",0)
        val expectedResponse = Response.Failure(Exception("Improper Authentication"))

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody(jsonBody))
        /**WHEN*/
        val actualResponse = underTest.logout("","").last()
        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())

    }
}


