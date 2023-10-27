package com.example.clicker.presentation

import com.example.clicker.data.TokenDataStore
import com.example.clicker.data.TokenValidationWorker
import com.example.clicker.domain.GetFollowedLiveStreamsUseCase
import com.example.clicker.network.TwitchClient
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.util.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    //todo: I need to mock all of these values



     var twitchRepoImpl: TwitchRepo = mock(TwitchRepo::class.java)






    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testingGetLiveChannels()= runTest {

        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        try{
//            val homeViewModel = HomeViewModel(
//                twitchRepoImpl,
//            )
            Assert.assertEquals(2, 2)
        }finally {
            Dispatchers.resetMain()
        }





    }
}