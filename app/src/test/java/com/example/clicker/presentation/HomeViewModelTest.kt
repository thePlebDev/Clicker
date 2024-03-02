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
import com.example.clicker.util.NetworkAuthResponse
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
            authentication =FakeAuthentication(),
            twitchRepoImpl = FakeTwitchImplRepo(),
            tokenDataStore = FakeTokenDataStore(
                userIsNewUser = true
            )
        )
        /**WHEN*/
        delay(1000)
        val actualShowLoginModal = homeViewModel.state.value.showLoginModal
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
    fun testing_get_live_channels_exception() = runTest {
        /**GIVEN*/

    }
}

class FakeAuthentication: TwitchAuthentication{
    override suspend fun validateToken(
        url: String,
        token: String
    ): Flow<NetworkAuthResponse<ValidatedUser>> =flow{
        val response = NetworkAuthResponse.Failure(Exception("another"))
         emit(response)
    }

    override fun logout(clientId: String, token: String): Flow<NetworkAuthResponse<String>> =flow{
        emit(NetworkAuthResponse.Loading)
    }

}
class FakeTwitchImplRepo:TwitchRepo{
    override suspend fun getFollowedLiveStreams(
        authorizationToken: String,
        clientId: String,
        userId: String
    ): Flow<NetworkAuthResponse<List<StreamData>>> = flow{
        emit(NetworkAuthResponse.Loading)
    }

    override suspend fun getModeratedChannels(
        authorizationToken: String,
        clientId: String,
        userId: String
    ): Flow<NetworkAuthResponse<GetModChannels>> =flow{
        emit(NetworkAuthResponse.Loading)
    }

}
class FakeTokenDataStore(
    private val userIsNewUser:Boolean
): TwitchDataStore {

    override suspend fun setOAuthToken(oAuthToken: String) {

    }

    override fun getOAuthToken(): Flow<String> = flow{
        if(userIsNewUser){
            emit("")
        }else{
            emit("fakeOAuthToken")
        }

    }

    override suspend fun setUsername(username: String) {

    }

    override fun getUsername(): Flow<String> = flow{
        emit("")
    }

}