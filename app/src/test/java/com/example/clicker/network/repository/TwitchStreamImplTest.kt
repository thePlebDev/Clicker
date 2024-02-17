package com.example.clicker.network.repository


import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.domain.TwitchStream
import com.example.clicker.network.models.twitchStream.ChatSettings
import com.example.clicker.network.models.twitchStream.UpdateChatSettings
import com.example.clicker.network.repository.util.TwitchClientBuilder
import com.example.clicker.network.repository.util.createJsonBodyFrom
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class TwitchStreamImplTest {

    private lateinit var underTest: TwitchStream
    private lateinit var mockWebServer: MockWebServer

    /**WHAT TO TEST FOR ALL METHODS IN TwitchStream*/
    //1) success with  all interceptors
    // (DONE FOR getChatSettings(),updateChatSettings()

    //2) network interceptor throws errors
    // (DONE FOR getChatSettings(),updateChatSettings()

    //3) 401 interceptor throws error
    // (DONE FOR getChatSettings(),updateChatSettings()

    //4) 500 response error
    // (DONE FOR getChatSettings(),updateChatSettings()

    //5) if applicable, test empty body response
    // (DONE FOR getChatSettings()


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
    fun `getChatSettings() returns a successful response with all interceptors`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected body and response
        val expectedBody = ChatSettings(data = listOf())
        val expectedResponse = Response.Success(expectedBody)

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonBody))


        /**WHEN*/
        val actualResponse = underTest.getChatSettings("","","").last()



        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }
    @Test
    fun `getChatSettings() but NetworkInterceptor throws exception`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(false)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected body and response
        val expectedBody = ChatSettings(data = listOf())
        val expectedResponse = Response.Failure(Exception("Network error, please try again later"))

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonBody))


        /**WHEN*/
        val actualResponse = underTest.getChatSettings("","","").last()



        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun `getChatSettings() but Authentication401Interceptor throws exception`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(true)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected body and response
        val expectedBody = ChatSettings(data = listOf())
        val expectedResponse = Response.Failure(Exception("Improper Authentication"))

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonBody))


        /**WHEN*/
        val actualResponse = underTest.getChatSettings("","","").last()



        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun `getChatSettings() but the call returns a 500 response code`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected body and response
        val expectedBody = ChatSettings(data = listOf())
        val expectedResponse = Response.Failure(Exception("Error! Please try again"))

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody(jsonBody))


        /**WHEN*/
        val actualResponse = underTest.getChatSettings("","","").last()



        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun `getChatSettings() but the call empty body`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected body and response
        val expectedBody = ChatSettings(data = listOf())
        val expectedResponse = Response.Failure(Exception("Error! Please try again"))

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(""))


        /**WHEN*/
        val actualResponse = underTest.getChatSettings("","","").last()



        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    /**TESTING updateChatSettings()*/
    @Test
    fun `updateChatSettings() returns a successful response with all interceptors`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected body and response
        val expectedBody = ChatSettings(data = listOf())
        val expectedResponse = Response.Success(true)
        val updateChatSettings = UpdateChatSettings(false,false,false,false)

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonBody))


        /**WHEN*/
        val actualResponse = underTest.updateChatSettings("","","","",updateChatSettings).last()

        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun `updateChatSettings() but NetworkInterceptor throws exception`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(false)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected body and response
        val expectedBody = ChatSettings(data = listOf())
        val expectedResponse = Response.Failure(Exception("Network error, please try again later"))
        val updateChatSettings = UpdateChatSettings(false,false,false,false)

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonBody))


        /**WHEN*/
        val actualResponse = underTest
            .updateChatSettings("","","","",updateChatSettings).last()



        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun `updateChatSettings() but Authentication401Interceptor throws exception`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(true)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected body and response
        val expectedBody = ChatSettings(data = listOf())
        val expectedResponse = Response.Failure(Exception("Improper Authentication"))
        val updateChatSettings = UpdateChatSettings(false,false,false,false)

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonBody))


        /**WHEN*/
        val actualResponse = underTest
            .updateChatSettings("","","","",updateChatSettings).last()



        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun `updateChatSettings() but the call returns a 500 response code`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected body and response
        val expectedBody = ChatSettings(data = listOf())
        val expectedResponse = Response.Failure(Exception("Error! Please try again"))
        val updateChatSettings = UpdateChatSettings(false,false,false,false)

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody(jsonBody))


        /**WHEN*/
        val actualResponse = underTest
            .updateChatSettings("","","","",updateChatSettings).last()



        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }
}