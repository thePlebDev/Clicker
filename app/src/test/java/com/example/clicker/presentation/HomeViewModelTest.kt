package com.example.clicker.presentation

import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.presentation.home.StreamInfo
import com.example.clicker.util.Response
import java.io.IOException
import java.lang.Exception
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doThrow

// Reusable JUnit4 TestRule to override the Main dispatcher
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    // I think with the simplicity of my code, it will literall be easier to write my own mocked object

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    var twitchRepoImpl: TwitchRepo = mock(TwitchRepo::class.java)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testing_get_live_channels_success() = runTest {
        /**GIVEN*/

        val unconfinedDispatcher = UnconfinedTestDispatcher(testScheduler)

        val response = Response.Success(listOf(StreamInfo("", "", "", 0, "", "")))

        `when`(twitchRepoImpl.getFollowedLiveStreams("", "", "")).thenReturn(
            flow { emit(response) }
        )

        val homeViewModel = HomeViewModel(
            twitchRepoImpl = twitchRepoImpl,
            ioDispatcher = unconfinedDispatcher
        )

        /**WHEN*/
        homeViewModel.getLiveStreams("", "", "")

        val actualValue = homeViewModel.state.value.streamersListLoading
        val expectedResponse = Response.Success(true)

        /**THEN*/

        Assert.assertEquals(expectedResponse, actualValue)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testing_get_live_channels_fail() = runTest(StandardTestDispatcher()) {
        // TODO: NEED TO ASK SOMEONE HELP FOR TESTING COROUTINES AND HOW TO ADVANCE THE TIME MANUALLY
        // TODO: THE DELAY(2000) IS SOMETHING I JUST CAN'T FIGURE OUT
        /**GIVEN*/

        val standardDispatcher = StandardTestDispatcher()

        val response = Response.Failure(Exception("Failed to make request"))

        `when`(twitchRepoImpl.getFollowedLiveStreams("", "", "")).thenReturn(
            flow { emit(response) }
        )

        val homeViewModel = HomeViewModel(
            twitchRepoImpl = twitchRepoImpl,
            ioDispatcher = standardDispatcher
        )

        /**WHEN*/
//         launch{
//            homeViewModel.getLiveStreams("","","")
//
//        }
//        standardDispatcher.scheduler.advanceTimeBy(1000)
//        homeViewModel.getLiveStreams("","","")
//
//
//        val actualValue = homeViewModel.state.value.failedNetworkRequest
//        val expectedResponse = true
//        Assert.assertEquals(expectedResponse, actualValue)

        // standardDispatcher.scheduler.advanceUntilIdle()

        /**THEN*/
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testing_get_live_channels_exception() = runTest {
        /**GIVEN*/

        val unconfinedDispatcher = UnconfinedTestDispatcher(testScheduler)

        val response = Response.Success(listOf(StreamInfo("", "", "", 0, "", "")))

        `when`(twitchRepoImpl.getFollowedLiveStreams("", "", "")).doThrow(IOException(""))

        val homeViewModel = HomeViewModel(
            twitchRepoImpl = twitchRepoImpl,
            ioDispatcher = unconfinedDispatcher
        )

        /**WHEN*/
        homeViewModel.getLiveStreams("", "", "")

        val actualValue = homeViewModel.state.value.streamersListLoading
        val expectedResponse = Response.Success(true)

        /**THEN*/

        Assert.assertEquals(expectedResponse, actualValue)
    }
}