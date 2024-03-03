package com.example.clicker.presentation

import com.example.clicker.data.TokenDataStore
import com.example.clicker.domain.TwitchDataStore
import com.example.clicker.network.clients.GetModChannels
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.presentation.home.StreamInfo
import com.example.clicker.presentation.util.FakeAuthentication
import com.example.clicker.presentation.util.FakeTokenDataStore
import com.example.clicker.presentation.util.FakeTwitchImplRepo
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.NetworkNewUserResponse
import com.example.clicker.util.Response
import java.io.IOException

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
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
import kotlin.Exception

// Reusable JUnit4 TestRule to override the Main dispatcher
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
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


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testing_new_user_no_oAuthToken_found() = runTest {
        /**GIVEN*/
        val dispatcher = StandardTestDispatcher(testScheduler)
        val homeViewModel:HomeViewModel = HomeViewModel(
            ioDispatcher =dispatcher,
            authentication = FakeAuthentication(),
            twitchRepoImpl = FakeTwitchImplRepo(),
            tokenDataStore = FakeTokenDataStore(
                userIsNewUser = true
            )
        )
        /**WHEN*/
        delay(1000)
        val actualShowLoginModal = false
        val expectedShowLoginModalValue = true


        /**THEN*/

        Assert.assertEquals(expectedShowLoginModalValue, actualShowLoginModal)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testing_get_live_channels_fail() = runTest {
        // TODO: NEED TO ASK SOMEONE HELP FOR TESTING COROUTINES AND HOW TO ADVANCE THE TIME MANUALLY
        // TODO: THE DELAY(2000) IS SOMETHING I JUST CAN'T FIGURE OUT
        /**GIVEN*/





        /**THEN*/
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun validateOAuthToken_generic_error() = runTest {
        /**GIVEN*/
        val dispatcher = StandardTestDispatcher(testScheduler)
        val fakeAuthentication =FakeAuthentication()
        val fakeReturnType =NetworkNewUserResponse.Failure(Exception("Failed to authenticate"))
        fakeAuthentication.setValidateTokenReturnType(fakeReturnType)
        val homeViewModel:HomeViewModel = HomeViewModel(
            ioDispatcher =dispatcher,
            authentication = fakeAuthentication,
            twitchRepoImpl = FakeTwitchImplRepo(),
            tokenDataStore = FakeTokenDataStore(
                userIsNewUser = false
            )
        )
        /**WHEN*/
        delay(1000)
        val actualUIState = homeViewModel.state.value.streamersListLoading
        val expectedUIState= fakeReturnType


        /**THEN*/

        Assert.assertEquals(expectedUIState, actualUIState)

    }
    @Test
    fun validateOAuthToken_401Authentication_error() = runTest {
        /**GIVEN*/
        val dispatcher = StandardTestDispatcher(testScheduler)
        val fakeAuthentication =FakeAuthentication()
        val fakeReturnType =NetworkNewUserResponse.Auth401Failure(Exception("Token Expired"))
        fakeAuthentication.setValidateTokenReturnType(fakeReturnType)
        val homeViewModel:HomeViewModel = HomeViewModel(
            ioDispatcher =dispatcher,
            authentication = fakeAuthentication,
            twitchRepoImpl = FakeTwitchImplRepo(),
            tokenDataStore = FakeTokenDataStore(
                userIsNewUser = false
            )
        )
        /**WHEN*/
        delay(1000)
        val actualUIState = homeViewModel.state.value.streamersListLoading
        val expectedUIState= fakeReturnType


        /**THEN*/

        Assert.assertEquals(expectedUIState, actualUIState)

    }

    @Test
    fun validateOAuthToken_NetworkFailure_error() = runTest {
        /**GIVEN*/
        val dispatcher = StandardTestDispatcher(testScheduler)
        val fakeAuthentication =FakeAuthentication()
        val fakeReturnType =NetworkNewUserResponse.NetworkFailure(Exception("Network error"))
        fakeAuthentication.setValidateTokenReturnType(fakeReturnType)
        val homeViewModel:HomeViewModel = HomeViewModel(
            ioDispatcher =dispatcher,
            authentication = fakeAuthentication,
            twitchRepoImpl = FakeTwitchImplRepo(),
            tokenDataStore = FakeTokenDataStore(
                userIsNewUser = false
            )
        )
        /**WHEN*/
        delay(1000)
        val actualUIState = homeViewModel.state.value.streamersListLoading
        val expectedUIState= fakeReturnType


        /**THEN*/

        Assert.assertEquals(expectedUIState, actualUIState)

    }
}

