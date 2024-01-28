package com.example.clicker.presentation.authentication

import com.example.clicker.domain.TwitchDataStore
import com.example.clicker.domain.TwitchTokenValidationWorker
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.util.NetworkResponse
import com.example.clicker.util.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
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
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.`when`

/**
 * author another one
 * */
class MeatDispatcherRule(
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
class AuthenticationViewModelTest {

    //To make sure that there’s only one scheduler in our test, create the MainDispatcherRule property first.
    //Then reuse its dispatcher
    @get:Rule
    val mainDispatcherRule = MeatDispatcherRule()

    var twitchAuthentication: TwitchAuthentication = Mockito.mock(TwitchAuthentication::class.java)
    var twitchDataStore: TwitchDataStore = Mockito.mock(TwitchDataStore::class.java)
    var tokenValidationWorker: TwitchTokenValidationWorker = Mockito.mock(TwitchTokenValidationWorker::class.java)

    //        `when`(twitchRepoImpl.getFollowedLiveStreams("", "", "")).thenReturn(flow { emit(response) } )








    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testing_get_live_channels_no_stored_oAuth_token() = runTest{// Takes scheduler from Main
        // Any TestDispatcher created here also takes the scheduler from Main
        /**GIVEN*/
        `when`(twitchDataStore.getOAuthToken()).thenReturn(flow{emit("")})


        /**WHEN*/
        val authViewModel = AuthenticationViewModel(
            twitchAuthentication,
            twitchDataStore,
            tokenValidationWorker,
            mainDispatcherRule.testDispatcher
        )
        advanceUntilIdle()

        val expectedShowLoginModal= true
        val actualShowLoginModal  = authViewModel.authenticationUIState.value.showLoginModal

        val expectedModalText = "You're new here!"
        val actualModalText = authViewModel.authenticationUIState.value.modalText




        /**THEN*/

        Assert.assertEquals(expectedModalText, actualModalText)
        Assert.assertEquals(expectedShowLoginModal, actualShowLoginModal)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testing_get_live_channels_found_stored_oAuthToken() = runTest{// Takes scheduler from Main
        // Any TestDispatcher created here also takes the scheduler from Main
        /**GIVEN*/
        val validatedUser = ValidatedUser("","", listOf(""),"",32)

        val fakeAuthenticationToken ="fdsa85930fjw[tnv;0"
        `when`(twitchDataStore.getOAuthToken()).thenReturn(flow{emit(fakeAuthenticationToken)})
        `when`(twitchAuthentication.validateToken("",fakeAuthenticationToken))
            .thenReturn(flow{
                emit(NetworkResponse.Success(validatedUser))
            })



        /**WHEN*/
        val authViewModel = AuthenticationViewModel(
            twitchAuthentication,
            twitchDataStore,
            tokenValidationWorker,
            mainDispatcherRule.testDispatcher
        )
        advanceUntilIdle()

        val expectedAuthenticationCode= fakeAuthenticationToken
        val actualAuthenticationCode = authViewModel.authenticationUIState.value.authenticationCode

        /**THEN*/

        Assert.assertEquals(expectedAuthenticationCode, actualAuthenticationCode)

    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testing_get_live_channels_found_stored_oAuthToken_validate_token_failure() = runTest{// Takes scheduler from Main
        // Any TestDispatcher created here also takes the scheduler from Main
        /**GIVEN*/

        val fakeAuthenticationToken ="fdsa85930fjw[tnv;0"
        `when`(twitchDataStore.getOAuthToken()).thenReturn(flow{emit(fakeAuthenticationToken)})
        `when`(twitchAuthentication.validateToken("",fakeAuthenticationToken))
            .thenReturn(flow{
                emit(NetworkResponse.Failure(Exception("failed to validate")))
            })



        /**WHEN*/
        val authViewModel = AuthenticationViewModel(
            twitchAuthentication,
            twitchDataStore,
            tokenValidationWorker,
            mainDispatcherRule.testDispatcher
        )
        advanceUntilIdle()


        val expectedShowLoginModal= true
        val actualShowLoginModal = authViewModel.authenticationUIState.value.showLoginModal
        val expectedModalText = "Oops! Please login again"
        val actualModelText = authViewModel.authenticationUIState.value.modalText

        /**THEN*/

        Assert.assertEquals(expectedModalText, actualModelText)
        Assert.assertEquals(expectedShowLoginModal, actualShowLoginModal)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testing_begin_logout_success() = runTest{// Takes scheduler from Main
        // Any TestDispatcher created here also takes the scheduler from Main
        /**GIVEN*/
        val fakeClientId = "fdsafdsa"
        val fakeOAuthToken = "fdskapfupa"

        `when`(twitchDataStore.getOAuthToken()).thenReturn(flow{emit("")})
        `when`(twitchAuthentication.logout(fakeClientId,fakeOAuthToken)).thenReturn(flow{emit(Response.Success(""))})





        /**WHEN*/
        val authViewModel = AuthenticationViewModel(
            twitchAuthentication,
            twitchDataStore,
            tokenValidationWorker,
            mainDispatcherRule.testDispatcher
        )
        authViewModel.beginLogout(
            fakeClientId,fakeOAuthToken
        )

        advanceUntilIdle()
        val expectedModalText = "Success! Login with Twitch"
        val actualModelText = authViewModel.authenticationUIState.value.modalText


        /**THEN*/

        Assert.assertEquals(expectedModalText, actualModelText)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testing_begin_logout_failure() = runTest{// Takes scheduler from Main
        // Any TestDispatcher created here also takes the scheduler from Main
        /**GIVEN*/
        val fakeClientId = "fdsafdsa"
        val fakeOAuthToken = "fdskapfupa"

        `when`(twitchDataStore.getOAuthToken()).thenReturn(flow{emit("")})
        `when`(twitchAuthentication.logout(fakeClientId,fakeOAuthToken))
            .thenReturn(flow{emit(Response.Failure(Exception("failed to logout")))})





        /**WHEN*/
        val authViewModel = AuthenticationViewModel(
            twitchAuthentication,
            twitchDataStore,
            tokenValidationWorker,
            mainDispatcherRule.testDispatcher
        )
        authViewModel.beginLogout(
            fakeClientId,fakeOAuthToken
        )

        advanceUntilIdle()
        val expectedModalText = "Logout Error! Please try again"
        val actualModelText = authViewModel.authenticationUIState.value.modalText


        /**THEN*/
        Assert.assertEquals(expectedModalText, actualModelText)

    }
}