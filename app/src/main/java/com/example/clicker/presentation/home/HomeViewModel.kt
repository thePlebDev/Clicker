package com.example.clicker.presentation.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.BuildConfig
import com.example.clicker.data.TokenDataStore
import com.example.clicker.data.TokenValidationWorker
import com.example.clicker.util.Response
import kotlinx.coroutines.launch
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.AuthenticatedUser
import com.example.clicker.network.models.StreamData
import com.example.clicker.network.models.ValidatedUser
import com.example.clicker.network.models.toStreamInfo
import com.example.clicker.network.repository.TwitchRepoImpl
import com.example.clicker.network.websockets.TwitchWebSocket
import com.example.clicker.util.logCoroutineInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class HomeUIState(
    val userLogginIn:Boolean = false,
    val userProfile:String? = null,
    val authenticationCode:String? = null,
    val hideModal:Boolean = false,

    val width:Int =0,
    val aspectHeight:Int =0,

    val loadingLoginText:String ="Getting authentication token",
    val loginStep:Response<Boolean>? = Response.Loading,
    val clientId:String = "",
    val userId:String ="",
    val failedNetworkRequest:Boolean = false



)
data class LoginStatus(
    val showLoginModal:Boolean = true,
    val showLoginButton:Boolean = true,
    val loginStatusText:String = "Retrieving authentication token",
    val loginStep1: Response<Boolean> = Response.Loading,
    val loginStep2: Response<Boolean>? = null,
    val loginStep3: Response<Boolean>? = null,
    val logoutError:Boolean = false


)


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tokenDataStore: TokenDataStore,
    private val twitchRepoImpl: TwitchRepo,
    private val tokenValidationWorker: TokenValidationWorker,
): ViewModel(){


    init{
//        viewModelScope.launch {
//            withContext( CoroutineName("TokenValidatorDebugging")){
//                twitchRepoImpl.validateToken("").collect{
//                    logCoroutineInfo("CoroutineDebugging","GOT ITEMS from remote")
//
//
//                }
//            }
//
//        }
    }


    private val _newUrlList =MutableStateFlow<List<StreamInfo>?>(null)
    val newUrlList:StateFlow<List<StreamInfo>?> = _newUrlList


    private val _modStreamList  = mutableStateListOf<StreamInfo?>(null)
    val exposedModList get() = _modStreamList.toList()
    // Private mutable list





    private var _uiState: MutableState<HomeUIState> = mutableStateOf(HomeUIState())
    val state:State<HomeUIState> = _uiState

    private var _loginUIState: MutableState<LoginStatus> = mutableStateOf(LoginStatus())
    val loginState:State<LoginStatus> = _loginUIState

    init{
        viewModelScope.launch {
            _newUrlList.collect{streamInfoList ->
                streamInfoList?.let{list ->
                    for (item in list){
                        Log.d("URLLISTREQUEST","WILL MAKE REQUEST SINGLE REQUEST!!")
                    }
                }

            }
        }

    }

    fun filterChannelList(channelName:String){
        val listItem = _newUrlList.value?.firstOrNull { it.streamerName == channelName }
        _modStreamList.add(listItem)

    }


    //todo: THIS COULD ALL BE MOVED TO ITS OWN STATE MANAGEMENT CLASS
    private val authenticatedUserFlow = combine(
        flow =MutableStateFlow<String?>(null),
        flow2 =MutableStateFlow<ValidatedUser?>(null)
    ){
        oAuthToken,validatedUser ->
        MainBusState(oAuthToken,validatedUser)

    }.stateIn(viewModelScope, SharingStarted.Lazily,
        MainBusState(oAuthToken = null, authUser = null)
    )
    private val mutableAuthenticatedUserFlow = MutableStateFlow(authenticatedUserFlow.value)


    init {
        getOAuthToken()
    }

    init{
        /**
         * starts the observing of the hot flow, mutableAuthenticatedUserFlow*/
        collectAuthenticatedUserFlow()
    }



    private fun collectAuthenticatedUserFlow() =viewModelScope.launch {
        mutableAuthenticatedUserFlow.collect{mainState ->
            mainState.oAuthToken?.let{notNullToken ->
                validateOAuthToken(notNullToken)
                //todo:send off the worker request
                tokenValidationWorker.enqueueRequest(notNullToken)

            }
            mainState.authUser?.let {user ->
                _uiState.value = _uiState.value.copy(
                    clientId = user.clientId,
                    userId = user.userId
                )
               // tokenDataStore.setUsername()

                getLiveStreams(validatedUser = user,mainState.oAuthToken!!)
            }
        }
    }


    fun pullToRefreshGetLiveStreams(resetUI: suspend()->Unit){
        viewModelScope.launch {

            twitchRepoImpl
                .getFollowedLiveStreams(
                    authorizationToken = _uiState.value.authenticationCode?:"",
                    clientId=_uiState.value.clientId,
                    userId =_uiState.value.userId,
                )
                .collect{response ->
                    when(response){
                        is Response.Loading ->{

                        }
                        is Response.Success ->{
                            val replacedWidthHeightList =response.data.map{
                                it.changeUrlWidthHeight(_uiState.value.width,_uiState.value.aspectHeight)
                            }
                            resetUI()
                            _newUrlList.tryEmit(replacedWidthHeightList)

                        }
                        is Response.Failure ->{
                            Log.d("testingGetLiveStreams","FAILED")
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

    private suspend fun getLiveStreams(validatedUser: ValidatedUser, oAuthToken:String){
        Log.d("ValidatedUserUserId","user_id -> ${validatedUser.userId}")
        Log.d("ValidatedUserUserId","client_id -> ${validatedUser.clientId}")
        withContext(Dispatchers.IO +CoroutineName("GetLiveStreams")){
            twitchRepoImpl.getFollowedLiveStreams(
                authorizationToken = oAuthToken,
                clientId = validatedUser.clientId,
                userId = validatedUser.userId
            ).collect{response ->
                when(response){
                    is Response.Loading ->{
                        _loginUIState.value = _loginUIState.value.copy(
                            loginStatusText = "Getting live streams",
                            loginStep1 = Response.Loading
                        )

                    }
                    is Response.Success ->{


                        val replacedWidthHeight =response.data.map{
                            it.changeUrlWidthHeight(_uiState.value.width,_uiState.value.aspectHeight)
                        }

                        _newUrlList.tryEmit(replacedWidthHeight)


                        _loginUIState.value = _loginUIState.value.copy(

                            loginStep1 = Response.Success(true),
                            showLoginModal = false
                        )
                    }
                    is Response.Failure ->{
                        _loginUIState.value = _loginUIState.value.copy(
                            loginStatusText = "Error getting streams. Please login again",
                            loginStep1 = Response.Failure(Exception("No authentication token found"))


                        )
                    }
                }

            }

        }

    }


    fun updateAspectWidthHeight(width:Int, aspectHeight: Int){
        _uiState.value = _uiState.value.copy(
            aspectHeight = aspectHeight,
            width = width
            )

    }

    /**
     * This is the first function to run during the login sequence
     *
     * upon a successful retrieval of a stored OAuth token. It is emitted to mutableAuthenticatedUserFlow as oAuthToken
     *
     * upon a failed retrieval of a stored OAuth token. _loginUIState is updated accordingly
     * */
    private fun getOAuthToken() = viewModelScope.launch{
        tokenDataStore.getOAuthToken().collect{storedOAuthToken ->

            if(storedOAuthToken.length > 2){

                mutableAuthenticatedUserFlow.tryEmit(
                    mutableAuthenticatedUserFlow.value.copy(
                        oAuthToken = storedOAuthToken
                    )
                )
            }else{

                _loginUIState.value = _loginUIState.value.copy(
                    loginStatusText ="Please login with Twitch",
                    loginStep1 = Response.Failure(Exception("No authentication token found"))
                )
            }
        }
    }
    /**
     * The second method to be called in the authentication flow.
     * This function is used to make a request to Twitch's API and validate the oAuthenticationToken
     * */
    private fun validateOAuthToken(oAuthenticationToken:String) =viewModelScope.launch{
        withContext( Dispatchers.IO +CoroutineName("TokenValidator")){
            twitchRepoImpl.validateToken(oAuthenticationToken).collect{response ->

                when(response){
                    is Response.Loading ->{}
                    is Response.Success ->{
                        logCoroutineInfo("CoroutineDebugging","GOT ITEMS from remote")

                        _uiState.value = _uiState.value.copy(
                            authenticationCode =oAuthenticationToken
                        )
                        mutableAuthenticatedUserFlow.tryEmit(
                            mutableAuthenticatedUserFlow.value.copy(
                                authUser = response.data
                            )
                        )
                        tokenDataStore.setUsername(response.data.login)
                    }
                    is Response.Failure ->{

                        _loginUIState.value = _loginUIState.value.copy(
                            loginStatusText="Failed to validate token. Please login again",
                            loginStep1 = Response.Failure(Exception("failed to validate Token. Please login again"))
                        )
                    }
                }
            }


        }


    } //end


    fun setOAuthToken(oAuthToken:String) = viewModelScope.launch{
        //need to make a call to exchange the authCode for a validationToken
        Log.d("setOAuthToken","token -> $oAuthToken")
        tokenDataStore.setOAuthToken(oAuthToken)
        mutableAuthenticatedUserFlow.tryEmit(
            mutableAuthenticatedUserFlow.value.copy(
                oAuthToken = oAuthToken
            )
        )

    }

    fun beginLogout() = viewModelScope.launch{
        _loginUIState.value = _loginUIState.value.copy(
            showLoginModal = true,
            loginStep1 = Response.Loading,
            loginStatusText = "Logging out",

        )
        withContext( Dispatchers.IO +CoroutineName("BeginLogout")){
            twitchRepoImpl.logout(
                clientId = mutableAuthenticatedUserFlow.value.authUser?.clientId!!,
                token = mutableAuthenticatedUserFlow.value.oAuthToken!!)
                .collect{response ->
                    // Log.d("logoutResponse", "beginLogoutCollecting ->${it}")
                    when(response){
                        is Response.Loading ->{}
                        is Response.Success ->{
                            _loginUIState.value = _loginUIState.value.copy(
                                loginStatusText = "Success! Please log in with Twitch",
                                showLoginModal = true,
                                logoutError = false,
                                loginStep1 = Response.Failure(Exception("Please login again")),
                            )
                        }
                        is Response.Failure ->{
                            _loginUIState.value = _loginUIState.value.copy(
                                loginStatusText = "Logout Error! Please try again",
                                showLoginModal = true,
                                loginStep1 = Response.Failure(Exception("Error Logging out")),
                                logoutError = true
                            )
                        }
                        else -> {}
                    }
                }

        }


}

    override fun onCleared() {
        super.onCleared()
        //webSocket.close()

    }


}
data class StreamInfo(
    val streamerName:String,
    val streamTitle: String,
    val gameTitle:String,
    val views:Int,
    val url:String,
    val broadcasterId:String,
)
fun StreamInfo.changeUrlWidthHeight(aspectWidth:Int,aspectHeight: Int):StreamInfo{
    return StreamInfo(
        streamerName = this.streamerName,
        streamTitle = this.streamTitle,
        gameTitle = this.gameTitle,
        views = this.views,
        url = this.url.replace("{width}","${aspectWidth}").replace("{height}","${aspectHeight}"),
        broadcasterId = this.broadcasterId
    )
}

data class MainBusState(
    val oAuthToken:String? = null,
    val authUser:ValidatedUser? = null,
)
