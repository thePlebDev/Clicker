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



    // I think with the simplicity of my code, it will literally be easier to write my own mocked object

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    //runTest{} creates a TestScope, which is an implementation of CoroutineScope that will always use a TestDispatcher.
    @Test
    fun testing_returning_user_oAuthToken_found() = runTest {
        /**GIVEN*/
        val dispatcher = StandardTestDispatcher(testScheduler)
        val homeViewModel:HomeViewModel = HomeViewModel(
            ioDispatcher =dispatcher,
            authentication = FakeAuthentication.validateTokenReturn_Success().build(),
            twitchRepoImpl = FakeTwitchImplRepo.build(),
            tokenDataStore = FakeTokenDataStore.fullOAuthToken().build()
        )

        /**WHEN*/
        advanceUntilIdle() //this is the key to make it all work
        val actualValue = homeViewModel.validatedUser.value?.userId
        val expectedValue ="11"




        /**THEN*/

        Assert.assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `pullToRefreshModChannels Failure`()= runTest{
        /**GIVEN*/
        val dispatcher = StandardTestDispatcher(testScheduler)
        val homeViewModel:HomeViewModel = HomeViewModel(
            ioDispatcher =dispatcher,
            authentication = FakeAuthentication.validateTokenReturn_Success().build(),
            twitchRepoImpl = FakeTwitchImplRepo.getFollowedLiveStreams_Failure().build(),
            tokenDataStore = FakeTokenDataStore.build()
        )

        /**WHEN*/
        //this is the key to make it all work
        homeViewModel.pullToRefreshModChannels()
        advanceUntilIdle()
        val actualValue = homeViewModel.state.value.streamersListLoading
        val expectedValue = NetworkNewUserResponse.Failure(Exception("Failed"))


        /**THEN*/

        Assert.assertEquals(expectedValue.javaClass, actualValue.javaClass)

    }
    @Test
    fun `pullToRefreshModChannels Success`()= runTest{
        /**GIVEN*/
        val dispatcher = StandardTestDispatcher(testScheduler)
        val homeViewModel:HomeViewModel = HomeViewModel(
            ioDispatcher =dispatcher,
            authentication = FakeAuthentication.validateTokenReturn_Success().build(),
            twitchRepoImpl = FakeTwitchImplRepo.getFollowedLiveStreams_Success().build(),
            tokenDataStore = FakeTokenDataStore.build()
        )

        /**WHEN*/
        //this is the key to make it all work
        homeViewModel.pullToRefreshModChannels()
        advanceUntilIdle()
        val actualValue = homeViewModel.state.value.streamersListLoading
        val expectedValue = NetworkNewUserResponse.Success(listOf<StreamData>())


        /**THEN*/

        Assert.assertEquals(expectedValue.javaClass, actualValue.javaClass)

    }
//
//    @Test
//    fun testing_get_live_channels_fail() = runTest {}
//
//
//
//    @Test
//    fun validateOAuthToken_generic_error() = runTest {
//        /**GIVEN*/
//        val dispatcher = StandardTestDispatcher(testScheduler)
//        val fakeAuthentication =FakeAuthentication()
//        val fakeReturnType =NetworkNewUserResponse.Failure(Exception("Failed to authenticate"))
//        fakeAuthentication.setValidateTokenReturnType(fakeReturnType)
//        val homeViewModel:HomeViewModel = HomeViewModel(
//            ioDispatcher =dispatcher,
//            authentication = fakeAuthentication,
//            twitchRepoImpl = FakeTwitchImplRepo(),
//            tokenDataStore = FakeTokenDataStore(
//                userIsNewUser = false
//            )
//        )
//        /**WHEN*/
//        delay(1000)
//        val actualUIState = homeViewModel.state.value.streamersListLoading
//        val expectedUIState= fakeReturnType
//
//
//        /**THEN*/
//
//        Assert.assertEquals(expectedUIState, actualUIState)
//
//    }
//    @Test
//    fun validateOAuthToken_401Authentication_error() = runTest {
//        /**GIVEN*/
//        val dispatcher = StandardTestDispatcher(testScheduler)
//        val fakeAuthentication =FakeAuthentication()
//        val fakeReturnType =NetworkNewUserResponse.Auth401Failure(Exception("Token Expired"))
//        fakeAuthentication.setValidateTokenReturnType(fakeReturnType)
//        val homeViewModel:HomeViewModel = HomeViewModel(
//            ioDispatcher =dispatcher,
//            authentication = fakeAuthentication,
//            twitchRepoImpl = FakeTwitchImplRepo(),
//            tokenDataStore = FakeTokenDataStore(
//                userIsNewUser = false
//            )
//        )
//        /**WHEN*/
//        delay(1000)
//        val actualUIState = homeViewModel.state.value.streamersListLoading
//        val expectedUIState= fakeReturnType
//
//
//        /**THEN*/
//
//        Assert.assertEquals(expectedUIState, actualUIState)
//
//    }
//
//    @Test
//    fun validateOAuthToken_NetworkFailure_error() = runTest {
//        /**GIVEN*/
//        val dispatcher = StandardTestDispatcher(testScheduler)
//        val fakeAuthentication =FakeAuthentication()
//        val fakeReturnType =NetworkNewUserResponse.NetworkFailure(Exception("Network error"))
//        fakeAuthentication.setValidateTokenReturnType(fakeReturnType)
//        val homeViewModel:HomeViewModel = HomeViewModel(
//            ioDispatcher =dispatcher,
//            authentication = fakeAuthentication,
//            twitchRepoImpl = FakeTwitchImplRepo(),
//            tokenDataStore = FakeTokenDataStore(
//                userIsNewUser = false
//            )
//        )
//        /**WHEN*/
//        delay(1000)
//        val actualUIState = homeViewModel.state.value.streamersListLoading
//        val expectedUIState= fakeReturnType
//
//
//        /**THEN*/
//
//        Assert.assertEquals(expectedUIState, actualUIState)
//
//    }
//
//    @Test
//    fun beginLogout_success() = runTest {
//        /**GIVEN*/
//        val dispatcher = StandardTestDispatcher(testScheduler)
//        val fakeAuthentication =FakeAuthentication()
//        val fakeReturnType =NetworkAuthResponse.Success(false)
//        fakeAuthentication.setLogoutReturnType(fakeReturnType)
//
//        val homeViewModel:HomeViewModel = HomeViewModel(
//            ioDispatcher =dispatcher,
//            authentication = fakeAuthentication,
//            twitchRepoImpl = FakeTwitchImplRepo(),
//            tokenDataStore = FakeTokenDataStore(
//                userIsNewUser = true
//            )
//        )
//        /**WHEN*/
//        delay(1000)
//        homeViewModel.beginLogout("fakeClientId","fakeoAuthToken")
//        delay(1000)
//        val actualUIState= homeViewModel.state.value.userIsLoggedIn
//        val expectedUIState =fakeReturnType
//
//
//
//        /**THEN*/
//
//        Assert.assertEquals(expectedUIState, actualUIState)
//
//    }
//
//    @Test
//    fun beginLogout_NetworkAuthResponse_Failure() = runTest {
//        /**GIVEN*/
//        val dispatcher = StandardTestDispatcher(testScheduler)
//        val fakeAuthentication =FakeAuthentication()
//        val fakeReturnType =NetworkAuthResponse.Failure(Exception("fake exception"))
//        fakeAuthentication.setLogoutReturnType(fakeReturnType)
//
//        val homeViewModel:HomeViewModel = HomeViewModel(
//            ioDispatcher =dispatcher,
//            authentication = fakeAuthentication,
//            twitchRepoImpl = FakeTwitchImplRepo(),
//            tokenDataStore = FakeTokenDataStore(
//                userIsNewUser = true
//            )
//        )
//        /**WHEN*/
//        delay(1000)
//        homeViewModel.beginLogout("fakeClientId","fakeoAuthToken")
//        delay(1000)
//        val actualUIState= homeViewModel.state.value.userIsLoggedIn
//        val expectedUIState =fakeReturnType
//
//
//
//        /**THEN*/
//
//        Assert.assertEquals(expectedUIState, actualUIState)
//
//    }
//
//    @Test
//    fun beginLogout_NetworkAuthResponse_NetworkFailure() = runTest {
//        /**GIVEN*/
//        val dispatcher = StandardTestDispatcher(testScheduler)
//        val fakeAuthentication =FakeAuthentication()
//        val fakeReturnType =NetworkAuthResponse.NetworkFailure(Exception("fake exception"))
//        fakeAuthentication.setLogoutReturnType(fakeReturnType)
//
//        val homeViewModel:HomeViewModel = HomeViewModel(
//            ioDispatcher =dispatcher,
//            authentication = fakeAuthentication,
//            twitchRepoImpl = FakeTwitchImplRepo(),
//            tokenDataStore = FakeTokenDataStore(
//                userIsNewUser = true
//            )
//        )
//        /**WHEN*/
//        delay(1000)
//        homeViewModel.beginLogout("fakeClientId","fakeoAuthToken")
//        delay(1000)
//        val actualUIState= homeViewModel.state.value.userIsLoggedIn
//        val expectedUIState =fakeReturnType
//
//
//
//        /**THEN*/
//
//        Assert.assertEquals(expectedUIState, actualUIState)
//
//    }
//    @Test
//    fun beginLogout_NetworkAuthResponse_Auth401Failure() = runTest {
//        /**GIVEN*/
//        val dispatcher = StandardTestDispatcher(testScheduler)
//        val fakeAuthentication =FakeAuthentication()
//        val fakeReturnType =NetworkAuthResponse.Auth401Failure(Exception("fake exception"))
//        fakeAuthentication.setLogoutReturnType(fakeReturnType)
//
//        val homeViewModel:HomeViewModel = HomeViewModel(
//            ioDispatcher =dispatcher,
//            authentication = fakeAuthentication,
//            twitchRepoImpl = FakeTwitchImplRepo(),
//            tokenDataStore = FakeTokenDataStore(
//                userIsNewUser = true
//            )
//        )
//        /**WHEN*/
//        delay(1000)
//        homeViewModel.beginLogout("fakeClientId","fakeoAuthToken")
//        delay(1000)
//        val actualUIState= homeViewModel.state.value.userIsLoggedIn
//        val expectedUIState =fakeReturnType
//
//
//
//        /**THEN*/
//
//        Assert.assertEquals(expectedUIState, actualUIState)
//
//    }
}

