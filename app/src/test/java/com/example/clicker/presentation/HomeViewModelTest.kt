package com.example.clicker.presentation

import com.example.clicker.data.TokenDataStore
import com.example.clicker.data.TokenValidationWorker
import com.example.clicker.domain.GetFollowedLiveStreamsUseCase
import com.example.clicker.network.TwitchClient
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.presentation.home.StreamInfo
import com.example.clicker.util.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    //todo: I need to mock all of these values



     var twitchRepoImpl: TwitchRepo = mock(TwitchRepo::class.java)






    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testingGetLiveChannels()= runTest {
        /**GIVEN*/

        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val dispatcher = StandardTestDispatcher(testScheduler)
        val response = Response.Success(listOf(StreamInfo("","","",0,"","")))

        `when`(twitchRepoImpl.getFollowedLiveStreams("","","")).thenReturn(flow { emit(response) })
        try{
            val homeViewModel = HomeViewModel(
                twitchRepoImpl=twitchRepoImpl,
                ioDispatcher=dispatcher
            )
            
            /**WHEN*/
            homeViewModel.getLiveStreams("","","")

            val actualValue = homeViewModel.state.value.streamersListLoading
            val expectedResponse = Response.Success(true)



            /**THEN*/

            Assert.assertEquals(expectedResponse, actualValue)
        }finally {
            Dispatchers.resetMain()
        }





    }
}