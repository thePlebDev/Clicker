package com.example.clicker.presentation.authentication

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.domain.TwitchDataStore
import com.example.clicker.domain.TwitchTokenValidationWorker
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.presentation.home.MainBusState
import com.example.clicker.util.Response
import com.example.clicker.util.logCoroutineInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AuthenticationUIState(

    val showLoginButton: Boolean = true,

    val loginStep1: Response<Boolean> = Response.Loading,
    val logoutError: Boolean = false,

    val authenticationCode: String = "", //this is the oAuthToken
    val clientId: String = "",
    val userId: String = "",

    val authenticated: Boolean = false,

    val showErrorModal: Boolean = false,

    val showLoginModal: Boolean = false,
    val modalText: String = "Login to continue"

)

data class CertifiedUser(
    val oAuthToken: String = "",
    val clientId: String = "",
    val userId: String = ""
)

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authentication: TwitchAuthentication,
    private val tokenDataStore: TwitchDataStore,
    private val tokenValidationWorker: TwitchTokenValidationWorker,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private var _authenticationUIState: MutableState<AuthenticationUIState> = mutableStateOf(
        AuthenticationUIState()
    )
    val authenticationUIState: State<AuthenticationUIState> = _authenticationUIState

    // 1)CREATE THE AUTHENTICATION STATE
    private val authenticatedUserFlow = combine(
        flow = MutableStateFlow<String?>(null),
        flow2 = MutableStateFlow<ValidatedUser?>(null)
    ) {
            oAuthToken, validatedUser ->
        MainBusState(oAuthToken, validatedUser)
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        MainBusState(oAuthToken = null, authUser = null)
    )

    val mutableAuthenticatedUserFlow = MutableStateFlow(authenticatedUserFlow.value)

    init {
        /**
         * starts the observing of the hot flow, mutableAuthenticatedUserFlow
         * */
        collectAuthenticatedUserFlow()
        getOAuthToken()
    }

    private fun collectAuthenticatedUserFlow() = viewModelScope.launch {
        mutableAuthenticatedUserFlow.collect { mainState ->
            mainState.oAuthToken?.let { notNullToken ->
                validateOAuthToken(
                    notNullToken
                )

                // todo:send off the worker request
                tokenValidationWorker.enqueueRequest(notNullToken)
            }
            mainState.authUser?.let { user ->
                _authenticationUIState.value = _authenticationUIState.value.copy(
                    clientId = user.clientId,
                    userId = user.userId,
                    authenticated = true
                )
                // todo:the getLivesStreams() code will not run in this viewModel
            }
        }
    }

    fun validatedUser(): CertifiedUser {
        val user = CertifiedUser(
            oAuthToken = _authenticationUIState.value.authenticationCode,
            clientId = _authenticationUIState.value.clientId,
            userId = _authenticationUIState.value.userId
        )
        return user
    }

    // 2) implement the methods
    /**
     * This is the first function to run during the login sequence
     *
     * upon a successful retrieval of a stored OAuth token. It is emitted to mutableAuthenticatedUserFlow as oAuthToken
     *
     * upon a failed retrieval of a stored OAuth token. _loginUIState is updated accordingly
     * */
    private fun getOAuthToken() = viewModelScope.launch {
        tokenDataStore.getOAuthToken().collect { storedOAuthToken ->

            if (storedOAuthToken.length > 2) {
                mutableAuthenticatedUserFlow.tryEmit(
                    mutableAuthenticatedUserFlow.value.copy(
                        oAuthToken = storedOAuthToken
                    )
                )
            } else {
                _authenticationUIState.value = _authenticationUIState.value.copy(
                    showLoginModal = true,
                    modalText = "You're new here!"
                )
            }
        }
    }

    /**
     * The second method to be called in the authentication flow.
     * This function is used to make a request to Twitch's API and validate the oAuthenticationToken
     * */
    fun validateOAuthToken(
        oAuthenticationToken: String
    ) = viewModelScope.launch {
        withContext(ioDispatcher + CoroutineName("TokenValidator")) {
            authentication.validateToken("https://id.twitch.tv/oauth2/validate",oAuthenticationToken).collect { response ->

                when (response) {
                    is Response.Loading -> {
                        // the loading state is to be left empty because it is being handled by the HomeViewModel
                    }
                    is Response.Success -> {
                        logCoroutineInfo("CoroutineDebugging", "GOT ITEMS from remote")
                        Log.d("VALIDATINGTOKEN", "TOKEN ---> SUCCESS.....")

                        _authenticationUIState.value = _authenticationUIState.value.copy(
                            authenticationCode = oAuthenticationToken,
                            showLoginModal = false,
                            modalText = "Login with Twitch"
                        )
                        mutableAuthenticatedUserFlow.tryEmit(
                            mutableAuthenticatedUserFlow.value.copy(
                                authUser = response.data
                            )
                        )
                        tokenDataStore.setUsername(response.data.login)
                    }
                    is Response.Failure -> {
                        Log.d("VALIDATINGTOKEN", "TOKEN ---> FAILED.....")

                        _authenticationUIState.value = _authenticationUIState.value.copy(
                            showLoginModal = true,
                            modalText = "Oops! Please login again"
                        )
                    }
                }
            }
        }
    } // end validateOAuthToken

    // BEGIN LOGOUT STAGE
    fun beginLogout(clientId: String,oAuthToken: String) = viewModelScope.launch {
//
        _authenticationUIState.value = _authenticationUIState.value.copy(
            showLoginModal = true,
            modalText = "Logging out..."

        )
        withContext(ioDispatcher + CoroutineName("BeginLogout")) {
            authentication.logout(
                clientId = clientId,
                token = oAuthToken
            )
                .collect { response ->
                    when (response) {
                        is Response.Loading -> {}
                        is Response.Success -> {
                            _authenticationUIState.value = _authenticationUIState.value.copy(
                                modalText = "Success! Login with Twitch",
                                authenticated = false
                            )
                        }
                        is Response.Failure -> {
                            _authenticationUIState.value = _authenticationUIState.value.copy(
                                modalText = "Logout Error! Please try again",
                                loginStep1 = Response.Failure(Exception("Error Logging out")),
                                logoutError = true,
                                authenticated = true
                            )
                        }
                    }
                }
        }
    }

    /**
     * Below could be possibly moved
     */
    fun setOAuthToken(oAuthToken: String) = viewModelScope.launch {
        // need to make a call to exchange the authCode for a validationToken
        _authenticationUIState.value = _authenticationUIState.value.copy(
            showLoginModal = false,
            modalText = "Login with Twitch"
        )
        Log.d("setOAuthToken", "token -> $oAuthToken")
        tokenDataStore.setOAuthToken(oAuthToken)
        mutableAuthenticatedUserFlow.tryEmit(
            mutableAuthenticatedUserFlow.value.copy(
                oAuthToken = oAuthToken
            )
        )
    }
} // end of the view model