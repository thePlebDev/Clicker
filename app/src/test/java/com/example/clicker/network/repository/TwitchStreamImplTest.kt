package com.example.clicker.network.repository


import com.example.clicker.network.clients.BanUser
import com.example.clicker.network.clients.BanUserData
import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.clients.UnbanRequestItem
import com.example.clicker.network.domain.TwitchStream
import com.example.clicker.network.models.twitchStream.AutoModSettings
import com.example.clicker.network.models.twitchStream.BanUserResponse
import com.example.clicker.network.models.twitchStream.ChatSettings
import com.example.clicker.network.models.twitchStream.UpdateChatSettings
import com.example.clicker.network.repository.util.TwitchClientBuilder
import com.example.clicker.network.repository.util.createJsonBodyFrom
import com.example.clicker.network.websockets.models.MessageType

import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.Response
import com.example.clicker.util.objectMothers.IndividualAutoModSettingsDataObjectMother
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
    // (DONE FOR getChatSettings(),updateChatSettings(),deleteChatMessage(),
    // banUser(),unBanUser(),getAutoModSettings(),updateAutoModSettings()

    //2) network interceptor throws errors
    // (DONE FOR getChatSettings(),updateChatSettings(),deleteChatMessage()
    // ,banUser(),unBanUser(),getAutoModSettings(),updateAutoModSettings()

    //3) 401 interceptor throws error
    // (DONE FOR getChatSettings(),updateChatSettings(),deleteChatMessage(),
    // banUser(),unBanUser(),getAutoModSettings(),updateAutoModSettings()

    //4) 500 response error
    // (DONE FOR getChatSettings(),updateChatSettings(),deleteChatMessage()
    // ,banUser(),unBanUser(),getAutoModSettings(),updateAutoModSettings()

    //5) if applicable, test empty body response
    // (DONE FOR getChatSettings(),banUser()


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
    fun `getChatSettings() but the call returns an empty response body`()= runTest{
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


    /*************************TESTING updateChatSettings()*****************************************************/


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

    /*************************TESTING deleteChatMessage()*****************************************************/
    @Test
    fun `deleteChatMessage() returns a successful response with all interceptors`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected  response
        val expectedResponse = Response.Success(true)

        // Schedule a successful response
        mockWebServer.enqueue(MockResponse())


        /**WHEN*/
        val actualResponse = underTest.deleteChatMessage("","","","","").last()

        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun `deleteChatMessage() but NetworkInterceptor throws exception`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(false)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected  response
        val expectedResponse = Response.Failure(Exception("Network error, please try again later"))

        // Schedule a successful response
        mockWebServer.enqueue(MockResponse())


        /**WHEN*/
        val actualResponse = underTest.deleteChatMessage("","","","","").last()



        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun `deleteChatMessage() but Authentication401Interceptor throws exception`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(true)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected body and response
        val expectedResponse = Response.Failure(Exception("Improper Authentication"))

        // Schedule a successful response
        mockWebServer.enqueue(MockResponse())


        /**WHEN*/
        val actualResponse = underTest.deleteChatMessage("","","","","").last()

        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun `deleteChatMessage() but the call returns a 500 response code`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected body and response
        val expectedResponse = Response.Failure(Exception("Unable to delete message"))

        // Schedule a successful response
        mockWebServer.enqueue(MockResponse().setResponseCode(500))


        /**WHEN*/
        val actualResponse = underTest.deleteChatMessage("","","","","").last()

        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    /*************************TESTING banUser()*****************************************************/

    @Test
    fun `banUser() returns a successful response with all interceptors`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected  response
        val expectedBody = BanUserResponse(data= listOf())
        val expectedResponse = Response.Success(expectedBody)

        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonBody))
        val banUserBody = BanUser(data = BanUserData("","",0))


        /**WHEN*/
        val actualResponse = underTest.banUser("","","","",banUserBody).last()

        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun `banUser() but NetworkInterceptor throws exception`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(false)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected  response
        val expectedBody = BanUserResponse(data= listOf())
        val expectedResponse = Response.Failure(Exception("Network error, please try again later"))

        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonBody))
        val banUserBody = BanUser(data = BanUserData("","",0))


        /**WHEN*/
        val actualResponse = underTest.banUser("","","","",banUserBody).last()

        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun `banUser() but Authentication401Interceptor throws exception`()= runTest{
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
        val banUserBody = BanUser(data = BanUserData("","",0))


        /**WHEN*/
        val actualResponse = underTest.banUser("","","","",banUserBody).last()

        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun `banUser() but the call returns a 500 response code`()= runTest{
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
        val expectedResponse = Response.Failure(Exception("Unable to ban user"))

        // Schedule a successful response
        val jsonBody = createJsonBodyFrom(expectedBody)
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody(jsonBody))
        val banUserBody = BanUser(data = BanUserData("","",0))


        /**WHEN*/
        val actualResponse = underTest.banUser("","","","",banUserBody).last()

        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }
    @Test
    fun `banUser() but the call returns an empty response body`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected  response
        val expectedResponse = Response.Failure(Exception("Error! Please try again"))

        // Schedule a successful response
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(""))
        val banUserBody = BanUser(data = BanUserData("","",0))


        /**WHEN*/
        val actualResponse = underTest.banUser("","","","",banUserBody).last()

        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    /*************************TESTING unbanUser()*****************************************************/

    @Test
    fun `unBanUser() returns a successful response with all interceptors`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected  response

        val expectedResponse = Response.Success(true)

        // Schedule a successful response
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(""))



        /**WHEN*/
        val actualResponse = underTest.unBanUser("","","","","").last()

        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun `unBanUser() but NetworkInterceptor throws exception`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(false)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected  response
        val expectedBody = BanUserResponse(data= listOf())
        val expectedResponse = Response.Failure(Exception("Network error, please try again later"))

        // Schedule a successful response
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(""))



        /**WHEN*/
        val actualResponse = underTest.unBanUser("","","","","").last()

        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun `unBanUser() but Authentication401Interceptor throws exception`()= runTest{
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
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(""))



        /**WHEN*/
        val actualResponse = underTest.unBanUser("","","","","").last()

        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun `unBanUser() but the call returns a 500 response code`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected body and response
        val expectedResponse = Response.Failure(Exception("ERROR BANNING USER"))

        // Schedule a successful response
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody(""))



        /**WHEN*/
        val actualResponse = underTest.unBanUser("","","","","").last()

        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    /*************************TESTING getAutoModSettings()*****************************************************/

    @Test
    fun `getAutoModSettings() returns a successful response with all interceptors`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected  response
        val expectedBody = AutoModSettings(listOf())
        val expectedResponse = Response.Success(expectedBody)
        val jsonBody = createJsonBodyFrom(expectedBody)

        // Schedule a successful response
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonBody))


        /**WHEN*/
        val actualResponse = underTest.getAutoModSettings("","","","").last()

        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun `getAutoModSettings() but NetworkInterceptor throws exception`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(false)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected body and response
        val expectedBody = AutoModSettings(listOf())
        val jsonBody = createJsonBodyFrom(expectedBody)
        val expectedResponse = Response.Failure(Exception("Network error, please try again later"))

        // Schedule a successful response
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonBody))


        /**WHEN*/
        val actualResponse = underTest.getAutoModSettings("","","","").last()

        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun `getAutoModSettings() but Authentication401Interceptor throws exception`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(true)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected body and response
        val expectedBody = AutoModSettings(listOf())
        val jsonBody = createJsonBodyFrom(expectedBody)
        val expectedResponse = Response.Failure(Exception("Improper Authentication"))


        // Schedule a successful response
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonBody))


        /**WHEN*/
        val actualResponse = underTest.getAutoModSettings("","","","").last()

        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun `getAutoModSettings() but the call returns a 500 response code`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected body and response
        val expectedBody = AutoModSettings(listOf())
        val jsonBody = createJsonBodyFrom(expectedBody)
        val expectedResponse = Response.Failure(Exception("You are not a moderator"))

        // Schedule a successful response
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody(jsonBody))


        /**WHEN*/
        val actualResponse = underTest.getAutoModSettings("","","","").last()

        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun `getAutoModSettings() but the call returns an empty response body`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected  response
        val expectedResponse = Response.Failure(Exception("Error! Please try again"))

        // Schedule a successful response
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(""))


        /**WHEN*/
        val actualResponse = underTest.getAutoModSettings("","","","").last()

        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    /*************************TESTING updateAutoModSettings()*****************************************************/

    @Test
    fun `updateAutoModSettings() returns a successful response with all interceptors`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected  response

        val individualAutoModSettings =IndividualAutoModSettingsDataObjectMother.build()
        val expectedBody = AutoModSettings(listOf())
        val expectedResponse = Response.Success(expectedBody)
        val jsonBody = createJsonBodyFrom(expectedBody)

        // Schedule a successful response
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonBody))


        /**WHEN*/
        val actualResponse = underTest.updateAutoModSettings("","",individualAutoModSettings).last()

        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun `updateAutoModSettings() but NetworkInterceptor throws exception`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(false)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected body and response
        val expectedBody = AutoModSettings(listOf())
        val jsonBody = createJsonBodyFrom(expectedBody)
        val expectedResponse = Response.Failure(Exception("Network error, please try again later"))
        val individualAutoModSettings =IndividualAutoModSettingsDataObjectMother.build()

        // Schedule a successful response
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonBody))


        /**WHEN*/
        val actualResponse = underTest.updateAutoModSettings("","",individualAutoModSettings).last()

        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun `updateAutoModSettings() but Authentication401Interceptor throws exception`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(true)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected body and response
        val expectedBody = AutoModSettings(listOf())
        val jsonBody = createJsonBodyFrom(expectedBody)
        val expectedResponse = Response.Failure(Exception("Improper Authentication"))


        val individualAutoModSettings =IndividualAutoModSettingsDataObjectMother.build()

        // Schedule a successful response
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonBody))


        /**WHEN*/
        val actualResponse = underTest.updateAutoModSettings("","",individualAutoModSettings).last()

        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun `updateAutoModSettings() but the call returns a 500 response code`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected body and response
        val expectedBody = AutoModSettings(listOf())
        val jsonBody = createJsonBodyFrom(expectedBody)
        val expectedResponse = Response.Failure(Exception("Failed to update"))


        val individualAutoModSettings =IndividualAutoModSettingsDataObjectMother.build()

        // Schedule a successful response
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody(jsonBody))


        /**WHEN*/
        val actualResponse = underTest.updateAutoModSettings("","",individualAutoModSettings).last()

        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }

    @Test
    fun `updateAutoModSettings() but the call returns an empty response body`()= runTest{
        /**GIVEN*/
        // make the retrofit client
        val retrofitClient: TwitchClient = TwitchClientBuilder
            .addMockedUrl(mockWebServer.url("/").toString())
            .addNetworkInterceptor(true)
            .addAuthentication401Interceptor(false)
            .build()
        underTest = TwitchStreamImpl(retrofitClient)

        //make the expected body and response
        val expectedResponse = Response.Failure(Exception("Error! Please try again"))


        val individualAutoModSettings =IndividualAutoModSettingsDataObjectMother.build()

        // Schedule a successful response
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(""))


        /**WHEN*/
        val actualResponse = underTest.updateAutoModSettings("","",individualAutoModSettings).last()

        /**THEN*/
        Assert.assertEquals(expectedResponse.toString(), actualResponse.toString())
    }
    @Test
    fun `parsing date text`(){
        //what I want to parse 2023-08-28T18:34:29Z
        //GIVEN
        val givenString = "2023-08-28T18:34:29Z"
        val expectedString = "2023-08-28 18:34:29 UTC"

        //WHEN
        val parsedString =givenString.replace("T"," ").replace("Z"," UTC")


        //THEN

        Assert.assertEquals(expectedString, parsedString)

    }

    @Test
    fun `replacing words in list`(){
        val requestId = "111"
        val expectedStatus = "APPROVED"

        //GIVEN
        val unbanRequest = UnbanRequestItem(
            id = requestId,
            broadcaster_name = "StreamerName",
            broadcaster_login = "streamer_login",
            broadcaster_id = "broadcaster_id_123",
            moderator_id = "mod_id_678",
            moderator_login = "mod_login",
            moderator_name = "ModeratorName",
            user_id = "user_id_456",
            user_login = "user_login",
            user_name = "UserName",
            text = "Unban request text",
            status = "Pending",
            created_at = "2024-08-17T12:34:56Z",
            resolved_at = null,  // Or provide a date string if resolved
            resolution_text = null  // Or provide a resolution text if available
        )
        val listofUnbanRequests = mutableListOf<UnbanRequestItem>(unbanRequest)


        //WHEN
        val index = listofUnbanRequests.indexOfFirst { it.id == requestId }
        if (index != -1) {
            // Update the item's status to "approved"
            val updatedItem = listofUnbanRequests[index].copy(status = expectedStatus)
            listofUnbanRequests[index] = updatedItem
        }

        //THEN

        val actualStatus =listofUnbanRequests[0].status

        Assert.assertEquals(expectedStatus, actualStatus)

    }

    @Test
    fun `first time chatter parsing`(){
        //GIVEN
        val whatIWantToParse ="first-msg=1"
        val expectedValue = MessageType.FIRSTTIMECHATTER
        val stringToParse ="@badge-info=;badges=;client-nonce=cb19f16caec7180ecae3f0676f2fdf53;color=;display-name=themodymoder;emotes=;first-msg=1;flags=24-31:P.3/S.3;id=ad50270b-8970-43b7-9814-68c48bc61882;mod=0;returning-chatter=0;room-id=72614351;subscriber=0;tmi-sent-ts=1724182575873;turbo=0;user-id=946933663;user-type= :themodymoder!themodymoder@themodymoder.tmi.twitch.tv PRIVMSG #varidetta :rabbits have at least 2 nipples. therefore I am a rabbit"

        //WHEN
       // val actualValue = checkFirstTimeChatter(stringToParse)

        //THEN
       // Assert.assertEquals(expectedValue, actualValue)
    }




}