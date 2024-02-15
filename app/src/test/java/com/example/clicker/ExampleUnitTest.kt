package com.example.clicker

import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.twitchRepo.FollowedLiveStreams
import com.example.clicker.network.repository.TwitchRepoImpl
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

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
    fun addition_isCorrect() = runTest {
        /* Given */
        val testingCode = retrofit2.Response.success(
            200,
            FollowedLiveStreams(data = listOf())

        )

       // twitchRepository = TwitchRepoImpl(twitchClient)
        // todo: I THINK I NEED TO DO Response.success() WITH A CODE OF 200
        Mockito.`when`(twitchClient.getFollowedStreams("", "", "")).thenReturn(testingCode)

        /* When */
        val getFollowedStreams = twitchRepository.getFollowedLiveStreams("", "", "").first()
//
//
//        /* Then */
//        // Then check it's the expected item
        assertEquals(Response.Loading, getFollowedStreams)
//        Log.d("TESTINGEXCEPTIONS",getFollowedStreams.toString())
//
        //      assertEquals(true, testingCode.isSuccessful)
    }
}