package com.example.clicker.presentation.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.ValidatedUser
import com.example.clicker.presentation.authentication.CertifiedUser
import com.example.clicker.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class HomeUIState(
    val userLogginIn: Boolean = false,
    val userProfile: String? = null,
    val hideModal: Boolean = false,
    val width: Int = 0,
    val aspectHeight: Int = 0,
    val loadingLoginText: String = "Getting authentication token",
    val loginStep: Response<Boolean>? = Response.Loading,
    val failedNetworkRequest: Boolean = false,
    val streamersListLoading: Response<Boolean> = Response.Loading,
    val domainIsRegistered: Boolean = false

)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val twitchRepoImpl: TwitchRepo,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _newUrlList = MutableStateFlow<List<StreamInfo>?>(null)
    val newUrlList: StateFlow<List<StreamInfo>?> = _newUrlList

    private var _uiState: MutableState<HomeUIState> = mutableStateOf(HomeUIState())
    val state: State<HomeUIState> = _uiState

    private val _authenticatedUser = MutableStateFlow<CertifiedUser?>(null)
    val authenticatedUser: StateFlow<CertifiedUser?> = _authenticatedUser

    fun registerDomian(isRegistered: Boolean) {
        _uiState.value = _uiState.value.copy(
            domainIsRegistered = isRegistered
        )
    }
    fun updateAuthenticatedUser(certifiedUser: CertifiedUser) {
        _authenticatedUser.tryEmit(certifiedUser)
    }

    init {
        viewModelScope.launch {
            _authenticatedUser.collect { authUser ->
                authUser?.also {
                    getLiveStreams(
                        userId = it.userId,
                        clientId = it.clientId,
                        oAuthToken = it.oAuthToken
                    )
                }
            }
        }
    }

    // THIS IS THE END

    fun pullToRefreshGetLiveStreams(resetUI: suspend() -> Unit) {
        viewModelScope.launch {
            withContext(ioDispatcher + CoroutineName("GetLiveStreamsPull")) {
                Log.d("testingGetLiveStreams", "userid ->${_authenticatedUser.value?.userId}")
                Log.d("testingGetLiveStreams", "clientId ->${_authenticatedUser.value?.clientId}")
                twitchRepoImpl
                    .getFollowedLiveStreams(
                        authorizationToken = _authenticatedUser.value?.oAuthToken ?: "",
                        clientId = _authenticatedUser.value?.clientId ?: "",
                        userId = _authenticatedUser.value?.userId ?: ""
                    )
                    .collect { response ->
                        when (response) {
                            is Response.Loading -> {
                            }
                            is Response.Success -> {
                                val replacedWidthHeightList = response.data.map {
                                    it.changeUrlWidthHeight(
                                        _uiState.value.width,
                                        _uiState.value.aspectHeight
                                    )
                                }
                                resetUI()
                                _newUrlList.tryEmit(replacedWidthHeightList)
                            }
                            is Response.Failure -> {
                                Log.d("testingGetLiveStreams", "FAILED")
                                _uiState.value = _uiState.value.copy(
                                    failedNetworkRequest = true
                                )
                                resetUI()
                                delay(2000)
                                _uiState.value = _uiState.value.copy(
                                    failedNetworkRequest = false
                                )
                            }
                        }
                    }
            }
        }
    }

    suspend fun getLiveStreams(
        clientId: String,
        userId: String,
        oAuthToken: String
    ) {
        try {
            withContext(Dispatchers.IO + CoroutineName("GetLiveStreams")) {
                twitchRepoImpl.getFollowedLiveStreams(
                    authorizationToken = oAuthToken,
                    clientId = clientId,
                    userId = userId
                ).collect { response ->
                    when (response) {
                        is Response.Loading -> {
                        }
                        is Response.Success -> {
                            val liveStreamLists = response.data
                            Log.d(
                                "AuthenticationViewModelGetLiveStreams",
                                "size -> ${liveStreamLists.size}"
                            )

                            val replacedWidthHeightList = response.data.map {
                                it.changeUrlWidthHeight(
                                    _uiState.value.width,
                                    _uiState.value.aspectHeight
                                )
                            }

                            _uiState.value = _uiState.value.copy(
                                streamersListLoading = Response.Success(true)
                            )
                            _newUrlList.tryEmit(replacedWidthHeightList)
                        }
                        // end
                        is Response.Failure -> {
                            _uiState.value = _uiState.value.copy(
                                failedNetworkRequest = true,
                                streamersListLoading = response
                            )
                            print("before the delay")
                            delay(2000)
                            _uiState.value = _uiState.value.copy(
                                failedNetworkRequest = false
                            )
                        }
                    }
                }
            }
        } catch (e: IOException) {
        }
    }

    fun updateAspectWidthHeight(width: Int, aspectHeight: Int) {
        _uiState.value = _uiState.value.copy(
            aspectHeight = aspectHeight,
            width = width
        )
    }
}
data class StreamInfo(
    val streamerName: String,
    val streamTitle: String,
    val gameTitle: String,
    val views: Int,
    val url: String,
    val broadcasterId: String
)
fun StreamInfo.changeUrlWidthHeight(aspectWidth: Int, aspectHeight: Int): StreamInfo {
    return StreamInfo(
        streamerName = this.streamerName,
        streamTitle = this.streamTitle,
        gameTitle = this.gameTitle,
        views = this.views,
        url = this.url.replace("{width}", "$aspectWidth").replace("{height}", "$aspectHeight"),
        broadcasterId = this.broadcasterId
    )
}

data class MainBusState(
    val oAuthToken: String? = null,
    val authUser: ValidatedUser? = null
)
