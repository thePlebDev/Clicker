package com.example.clicker.network.repository

import android.util.Log
import com.example.clicker.network.clients.TwitchAuthenticationClient
import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.domain.TwitchRepo

import com.example.clicker.presentation.stream.util.Scanner
import com.example.clicker.presentation.stream.util.Token
import com.example.clicker.presentation.stream.util.TokenType
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TwitchRepoImplTest {

    private lateinit var underTest: TwitchRepo
    private lateinit var twitchClient: TwitchClient
    private lateinit var mockWebServer: MockWebServer

    /**WHAT TO TEST*/
    //1) success with interceptors
    //2) network interceptor throws errors
    //3) 401 interceptor throws error
    //4) empty response body
    //5) 500 response error

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
    fun `when validateToken() returns a successful response BETTER version`()= runTest{
        // make the retrofit client
        val retrofitClient = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/").toString())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TwitchClient::class.java)
        underTest = TwitchRepoImpl(retrofitClient)
    }









}
