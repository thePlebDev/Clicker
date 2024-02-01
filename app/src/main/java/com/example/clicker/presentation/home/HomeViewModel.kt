package com.example.clicker.presentation.home

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.domain.TwitchDataStore
import com.example.clicker.network.domain.NetworkMonitorRepo
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.presentation.authentication.CertifiedUser
import com.example.clicker.services.NetworkMonitorService
import com.example.clicker.util.NetworkResponse
import com.example.clicker.util.Response
import com.example.clicker.util.logCoroutineInfo
import com.example.clicker.util.mapWithRetry
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
    val oAuthToken: String = "",

    val networkConnectionState:Boolean = true,

)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val twitchRepoImpl: TwitchRepo,
    private val ioDispatcher: CoroutineDispatcher,
    private val tokenDataStore: TwitchDataStore,
    private val authentication: TwitchAuthentication,
    private val networkMonitorRepo: NetworkMonitorRepo
) : ViewModel() {

    private val _newUrlList = MutableStateFlow<List<StreamInfo>?>(null)
    val newUrlList: StateFlow<List<StreamInfo>?> = _newUrlList

    private var _uiState: MutableState<HomeUIState> = mutableStateOf(HomeUIState())
    val state: State<HomeUIState> = _uiState



    private val _validatedUser = MutableStateFlow<ValidatedUser?>(null)
    val validatedUser = _validatedUser.value
    private val _oAuthToken = MutableStateFlow<String?>(null)
    val oAuthToken:String? =  _oAuthToken.value
    /**BELOW IS THE NETWORK REQUEST BUILDER*/


    /**
     * monitorForNetworkConnection is a private function that is called to monitor the hot state from [networkMonitorRepo].
     *
     * */
    private fun monitorForNetworkConnection(){
        viewModelScope.launch {
            withContext(ioDispatcher){
                networkMonitorRepo.networkAvailable.collect{isConnectionLive ->
                    val currentConnectionState = _uiState.value.networkConnectionState
                    if(currentConnectionState && isConnectionLive){
                        //do nothing. THis is the initial state
                    }
                    if(!currentConnectionState && isConnectionLive){
                        //network reconnected
                        _uiState.value = _uiState.value.copy(
                            networkConnectionState = true
                        )
                        refreshFromConnection()

                    }
                    if(currentConnectionState && !isConnectionLive){
                        // network disconnection
                        Log.d("monitorForNetworkConnection","disconnected")
                        _uiState.value = _uiState.value.copy(
                            networkConnectionState = false
                        )
                    }
                }
            }

        }
    }

    /**
     * refreshFromConnection is a private function that will get called when [monitorForNetworkConnection] detects a
     * reconnection to the network. First it will get the locally stored OAuth token, then if [validatedUser] is
     * not null [getLiveStreams] is called. If [validatedUser] is null then [validateOAuthToken] is run.
     * */
    private fun refreshFromConnection(){
        viewModelScope.launch {
            tokenDataStore.getOAuthToken().collect{oAuthToken ->
                if(oAuthToken.length > 2 ){
                    when(validatedUser){
                        null ->{
                            validateOAuthToken(oAuthToken)
                        }
                        else ->{
                            getLiveStreams(
                                clientId = validatedUser.clientId,
                                userId = validatedUser.clientId,
                                oAuthToken =oAuthToken
                            )
                        }
                    }
                }


            }
        }
    }


    fun registerDomian(isRegistered: Boolean) {
        _uiState.value = _uiState.value.copy(
            domainIsRegistered = isRegistered
        )
    }

    init{
        monitorForOAuthToken()
    }
    init {
        monitorForValidatedUser()
    }
    init{
        getOAuthToken()
    }
    init {
        monitorForNetworkConnection()
    }


/**
 * monitorForValidatedUser is a private function that upon the initialization of this viewModel is meant to monitor the [_validatedUser] hot flow for any non null values
 * to be emitted. Once a non null value is emitted to [_validatedUser] this function will then call [getLiveStreams]
 * */
    private fun monitorForValidatedUser(){
        viewModelScope.launch {
            _validatedUser.collect{nullableValidatedUser ->
                nullableValidatedUser?.also{nonNullValidatedUser ->
                    Log.d("nullableValidatedUser","RUNNING")
                    getLiveStreams(
                        clientId = nonNullValidatedUser.clientId,
                        userId = nonNullValidatedUser.userId,
                        oAuthToken = _uiState.value.oAuthToken
                    )
                }
            }
        }
    }

    /**
     * monitorForOAuthToken is a private function that upon the initialization of this viewModel is meant to monitor the [_oAuthToken] hot flow for any non null values. Once
     * a new non null value is emitted to [_oAuthToken], [validateOAuthToken] will be called
     * */
    private fun monitorForOAuthToken(){
        viewModelScope.launch {
            _oAuthToken.collect{nullableOAuthToken ->
                nullableOAuthToken?.also { nonNullOAuthToken ->
                    validateOAuthToken(nonNullOAuthToken)
                }

            }
        }
    }


    /**
     * getOAuthToken is a private function that upon the initialization of this viewModel is meant to try and retrieve the locally
     * stored oAuth token from [tokenDataStore]. If successful the oAuth token is emitted to [_oAuthToken]. If a oAuth token
     * is not found then the user is notified by telling them they need to sign in
     * */
    private fun getOAuthToken() = viewModelScope.launch {
        tokenDataStore.getOAuthToken().collect { storedOAuthToken ->
            Log.d("monitorForNetworkConnection","getOAuthToken  ---> TOKKEN:$storedOAuthToken")


            if (storedOAuthToken.length > 2) {
                //need to call the validateToken
                //this should emit a value to a HOT storedOAuthToken flow which then runs the validateOAuthToken
                _oAuthToken.tryEmit(storedOAuthToken)


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
     * setOAuthToken is a function called to set the locally stored authentication token
     *
     * @param oAuthToken a string representing the authentication token that is to be stored locally
     */
    fun setOAuthToken(oAuthToken: String) = viewModelScope.launch {
        // need to make a call to exchange the authCode for a validationToken
        _uiState.value = _uiState.value.copy(
            showLoginModal = false,
        )
        Log.d("setOAuthToken", "token -> $oAuthToken")
        tokenDataStore.setOAuthToken(oAuthToken)
        _oAuthToken.tryEmit(oAuthToken)
    }

    /**
     * The second method to be called in the authentication flow.
     * This function is used to make a request to Twitch's API and validate the oAuthenticationToken
     * */
    private fun validateOAuthToken(
        oAuthenticationToken: String
    ) = viewModelScope.launch {
        withContext(ioDispatcher + CoroutineName("TokenValidator")) {
            authentication.validateToken("https://id.twitch.tv/oauth2/validate",oAuthenticationToken)
                .collect { response ->
                    Log.d("monitorForNetworkConnection","validateOAuthTokenResponse ->${response}")

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
                        //todo:THIS SHOULD GET REMOVED. TOO MUCH IS GOING ON INSIDE OF THIS FUNCTION
                        tokenDataStore.setUsername(response.data.login)
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

                twitchRepoImpl
                    .getFollowedLiveStreams(
                        authorizationToken = _oAuthToken.value ?: "",
                        clientId = _validatedUser.value?.clientId ?:"",
                        userId = _validatedUser.value?.userId ?:""
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

    override fun onCleared() {
        super.onCleared()
        Log.d("onclearedCalled","DEATH TO US ALL")
    }


} /***END OF VIEWMODEL**/

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
