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
import com.example.clicker.presentation.AuthenticationEvent
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
import java.lang.Exception

data class AuthenticationUIState(

    val showLoginButton: Boolean = true,

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
    private val ioDispatcher: CoroutineDispatcher,
    private val authenticationEventBus: AuthenticationEvent
) : ViewModel() {

    private var _authenticationUIState: MutableState<AuthenticationUIState> = mutableStateOf(
        AuthenticationUIState()
    )
    val authenticationUIState: State<AuthenticationUIState> = _authenticationUIState

    init{
        viewModelScope.launch {
            authenticationEventBus.authenticationStatus.collect{
                Log.d("authenticationEventBus","AuthenticationViewModel-Bus --->$it")
            }
        }
    }


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



    /**KEEP beginLogout IN THIS VIEWMODEL*/
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
                        is Response.Loading -> {
                            authenticationEventBus.setLoggedInt(Response.Loading)
                        }
                        is Response.Success -> {
                            _authenticationUIState.value = _authenticationUIState.value.copy(
                                modalText = "Success! Login with Twitch",
                                authenticated = false
                            )
                            authenticationEventBus.setLoggedInt(Response.Success(true))
                        }
                        is Response.Failure -> {
                            authenticationEventBus.setLoggedInt(Response.Failure(Exception("Failed to logout")))
                            _authenticationUIState.value = _authenticationUIState.value.copy(
                                modalText = "Logout Error! Please try again",
                                logoutError = true,
                                authenticated = true
                            )
                        }
                    }
                }
        }
    }

} // end of the view model