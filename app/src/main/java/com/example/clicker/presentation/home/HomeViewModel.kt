package com.example.clicker.presentation.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.domain.TwitchDataStore
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.presentation.authentication.CertifiedUser
import com.example.clicker.util.NetworkResponse
import com.example.clicker.util.Response
import com.example.clicker.util.logCoroutineInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * StreamInfo is a data class that represents all the information that is shown to the user when their followed streams
 * are fetched
 *
 * */
data class StreamInfo(
    val streamerName: String,
    val streamTitle: String,
    val gameTitle: String,
    val views: Int,
    val url: String,
    val broadcasterId: String
)
data class HomeUIState(

    val hideModal: Boolean = false,
    val width: Int = 0,
    val aspectHeight: Int = 0,
    val screenDensity: Float = 0f,


    val failedNetworkRequest: Boolean = false,
    val failedNetworkRequestMessage:String ="Network error, please try again later",
    val streamersListLoading: NetworkResponse<Boolean> = NetworkResponse.Loading,
    val showLoginModal: Boolean = false,
    val domainIsRegistered: Boolean = false,
    val oAuthToken: String = ""

)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val twitchRepoImpl: TwitchRepo,
    private val ioDispatcher: CoroutineDispatcher,
    private val tokenDataStore: TwitchDataStore,
    private val authentication: TwitchAuthentication,
) : ViewModel() {

    private val _newUrlList = MutableStateFlow<List<StreamInfo>?>(null)
    val newUrlList: StateFlow<List<StreamInfo>?> = _newUrlList

    private var _uiState: MutableState<HomeUIState> = mutableStateOf(HomeUIState())
    val state: State<HomeUIState> = _uiState

    private val _authenticatedUser = MutableStateFlow<CertifiedUser?>(null)
    val authenticatedUser: StateFlow<CertifiedUser?> = _authenticatedUser

    private val _validatedUser = MutableStateFlow<ValidatedUser?>(null)


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
    init{
        getOAuthToken()
    }

    init {
        monitorForValidatedUser()
    }
    private fun monitorForValidatedUser(){
        viewModelScope.launch {
            _validatedUser.collect{nullableValidatedUser ->
                nullableValidatedUser?.also{
                    getLiveStreams(
                        clientId = it.clientId,
                        userId = it.userId,
                        oAuthToken = _uiState.value.oAuthToken
                    )
                }
            }
        }
    }

    private fun getOAuthToken() = viewModelScope.launch {
        tokenDataStore.getOAuthToken().collect { storedOAuthToken ->

            if (storedOAuthToken.length > 2) {
                //need to call the validateToken
                validateOAuthToken(storedOAuthToken)


            } else {
                _uiState.value = _uiState.value.copy(
                    streamersListLoading = NetworkResponse.Failure(
                        Exception("You're new! Please login with Twitch")
                    ),
                    showLoginModal = true
                )


            }


        }
    }

    /**
     * The second method to be called in the authentication flow.
     * This function is used to make a request to Twitch's API and validate the oAuthenticationToken
     * */
    private fun validateOAuthToken(
        oAuthenticationToken: String
    ) = viewModelScope.launch {
        withContext(ioDispatcher + CoroutineName("TokenValidator")) {
            authentication.validateToken("https://id.twitch.tv/oauth2/validate",oAuthenticationToken).collect { response ->

                when (response) {
                    is NetworkResponse.Loading -> {
                        // the loading state is to be left empty because its initial state is loading
                    }
                    is NetworkResponse.Success -> {
                        logCoroutineInfo("CoroutineDebugging", "GOT ITEMS from remote")
                        Log.d("VALIDATINGTOKEN", "TOKEN ---> SUCCESS.....")

                        _uiState.value = _uiState.value.copy(
                            oAuthToken = oAuthenticationToken
                        )
                        _validatedUser.tryEmit(response.data)


                        // I think we need the below for the streamViewModel
                        //tokenDataStore.setUsername(response.data.login)
                    }
                    is NetworkResponse.Failure -> {
                        Log.d("VALIDATINGTOKEN", "TOKEN ---> FAILED.....")

                        _uiState.value = _uiState.value.copy(
                            streamersListLoading = NetworkResponse.Failure(
                                Exception("Please login with Twitch")
                            ),
                            showLoginModal = true
                        )
                    }
                    is NetworkResponse.NetworkFailure ->{
                        _uiState.value = _uiState.value.copy(
                            failedNetworkRequest =true
                        )
                        delay(3000)
                        _uiState.value = _uiState.value.copy(
                            failedNetworkRequest =false
                        )
                    }
                }
            }
        }
    } // end validateOAuthToken

    // THIS IS THE END

    fun pullToRefreshGetLiveStreams(resetUI: suspend() -> Unit) {
        viewModelScope.launch {
            withContext(ioDispatcher + CoroutineName("GetLiveStreamsPull")) {
                Log.d("testingGetLiveStreams", "userid ->${_authenticatedUser.value?.userId}")
                Log.d("testingGetLiveStreams", "clientId ->${_authenticatedUser.value?.clientId}")
                Log.d("testingGetLiveStreams", "authorizationBearer ->${_authenticatedUser.value?.oAuthToken}")
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
                Log.d("testingGetLiveStreams", "userid ->${_authenticatedUser.value?.userId}")
                Log.d("testingGetLiveStreams", "clientId ->${_authenticatedUser.value?.clientId}")
                Log.d("testingGetLiveStreams", "authorizationBearer ->${_authenticatedUser.value?.oAuthToken}")
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
                                streamersListLoading = NetworkResponse.Success(true)
                            )
                            _newUrlList.tryEmit(replacedWidthHeightList)
                        }
                        // end
                        is Response.Failure -> {
                            _uiState.value = _uiState.value.copy(
                                failedNetworkRequest = true,

                            )

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

    fun updateAspectWidthHeight(width: Int, aspectHeight: Int,screenDensity:Float) {

        _uiState.value = _uiState.value.copy(
            aspectHeight = aspectHeight,
            width = width,
            screenDensity =screenDensity
        )
    }
    fun updateUrlWidthHeight(aspectWidth: Int, aspectHeight: Int){
        val replacedWidthHeightList = _newUrlList.value?.map {
            it.changeUrlWidthHeight(
                aspectWidth,
                aspectHeight
            )
        }

        _newUrlList.tryEmit(replacedWidthHeightList)
    }
}

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
