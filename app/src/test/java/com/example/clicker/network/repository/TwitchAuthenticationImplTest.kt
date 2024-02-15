package com.example.clicker.network.repository

import com.example.clicker.network.clients.TwitchAuthenticationClient
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.network.repository.util.TwitchAuthenticationClientBuilder
import com.example.clicker.network.repository.util.TwitchClientBuilderUtil
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



    /**this is the better version*/
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



    //successful tests with all the interceptors
    @Test
    fun `when validateToken() returns a successful response with all interceptors`()= runTest{

    }
}


