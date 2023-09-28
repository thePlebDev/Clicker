package com.example.clicker

import android.util.Log
import com.example.clicker.network.TwitchClient
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.FollowedLiveStreams
import com.example.clicker.network.repository.TwitchRepoImpl
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.lang.RuntimeException

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(MockitoJUnitRunner::class)
class ExampleUnitTest {

    @Mock
    lateinit var twitchClient: TwitchClient
    lateinit var twitchRepository: TwitchRepo


    @Test
    fun addition_isCorrect() =runTest {
        /* Given */
        val testingCode = retrofit2.Response.success(
            200,
            FollowedLiveStreams(data = listOf())

        )


        twitchRepository  = TwitchRepoImpl(twitchClient)
        //todo: I THINK I NEED TO DO Response.success() WITH A CODE OF 200
        Mockito.`when`(twitchClient.getFollowedStreams("","","")).thenReturn(testingCode)


        /* When */
        val getFollowedStreams = twitchRepository.getFollowedLiveStreams("","","").first()
//
//
//        /* Then */
//        // Then check it's the expected item
        assertEquals(Response.Loading,getFollowedStreams)
//        Log.d("TESTINGEXCEPTIONS",getFollowedStreams.toString())
//
  //      assertEquals(true, testingCode.isSuccessful)
    }
}