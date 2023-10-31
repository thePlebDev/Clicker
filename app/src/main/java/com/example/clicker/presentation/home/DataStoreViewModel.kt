package com.example.clicker.presentation.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.data.TokenDataStore
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.network.models.AuthenticatedUser
import com.example.clicker.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DataStoreUIState(
    val width: Int = 0,
    val aspectHeight: Int = 0,
    val authState: String? = null
)

@HiltViewModel
class DataStoreViewModel @Inject constructor(
    private val twitchRepoImpl: TwitchAuthentication,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private var _uiState: MutableState<DataStoreUIState> = mutableStateOf(DataStoreUIState())
    val state: State<DataStoreUIState> = _uiState

    private val _urlList = mutableStateListOf<StreamInfo>()
    val urlList: List<StreamInfo> = _urlList

    private val _oAuthUserToken: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _clientId: MutableStateFlow<AuthenticatedUser?> = MutableStateFlow(null)

    private val _showLogin: MutableState<Response<Boolean>> = mutableStateOf(Response.Loading)
    val showLogin: State<Response<Boolean>> = _showLogin

    private val authenticatedUserFlow = combine(
        _oAuthUserToken,
        _clientId

    ) { oAuthToken,
            clientId
        ->
        MainState(
            hasOAuthToken = oAuthToken,
            hasClientId = clientId
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        MainState(hasOAuthToken = null, hasClientId = null)
    )

    init {
        watchAuthenticatedUserFlow()
    }
    init {
        getOAuthToken()
    }

    private fun watchAuthenticatedUserFlow() = viewModelScope.launch {
        authenticatedUserFlow.collect { mainState ->
            mainState.hasOAuthToken?.let { oAuthToken ->
                Log.d("validateOAuthUserToken", "OAuthToken -> $oAuthToken")
                // run the token validation
                validateOAuthToken(oAuthToken)
            }
            mainState.hasClientId?.let { authenticatedUser ->
                Log.d("validateOAuthUserTokens", "AuthUser -> $authenticatedUser")

                // get the streams
            }
        }
    }

    private suspend fun validateOAuthToken(oAuthUserToken: String) {
        twitchRepoImpl.validateToken(oAuthUserToken).collect { response ->
            when (response) {
                is Response.Loading -> {
                    Log.d("validateOAuthUserToken", "LOADING")
                    _uiState.value = _uiState.value.copy(
                        authState = "validating OAuthToken"
                    )
                }

                is Response.Success -> {
                    val userId = response.data.userId
                    val clientId = response.data.clientId
                    val userName = response.data.login
                    val authenticatedUser = AuthenticatedUser(
                        clientId = clientId,
                        userId = userId,
                        userName = userName
                    )
                    _clientId.tryEmit(authenticatedUser)
                }
                is Response.Failure -> {
                    Log.d("validateOAuthUserToken", "FAILURE")
                    _showLogin.value = Response.Failure(Exception("NO OAuthToken"))
                    _uiState.value = _uiState.value.copy(
                        authState = "OAuth token validation failed. Token expired or revoked"
                    )
                }

                else -> {}
            }
        }
    }

    fun setOAuthToken(oAuthToken: String) = viewModelScope.launch {
        // need to make a call to exchange the authCode for a validationToken
        Log.d("setOAuthToken", "token -> $oAuthToken")
        tokenDataStore.setOAuthToken(oAuthToken)
        _oAuthUserToken.tryEmit(oAuthToken)
    }
    private fun getOAuthToken() = viewModelScope.launch {
        tokenDataStore.getOAuthToken().collect { storedOAuthToken ->
            if (storedOAuthToken.length > 2) {
                Log.d("getOAuthToken", storedOAuthToken)
                _oAuthUserToken.tryEmit(storedOAuthToken)
            } else {
                Log.d("getOAuthToken", "no token ->  $storedOAuthToken")
                _uiState.value = _uiState.value.copy(
                    authState = "No Authentication Token"
                )
                _showLogin.value = Response.Failure(Exception("NO OAuthToken"))
            }
        }
    }

    fun updateAspectWidthHeight(width: Int, aspectHeight: Int) {
        _uiState.value = _uiState.value.copy(
            aspectHeight = aspectHeight,
            width = width
        )
    }
}
data class MainState(
    val hasOAuthToken: String? = null,
    val hasClientId: AuthenticatedUser? = null
)
