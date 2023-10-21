package com.example.clicker.presentation.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.BuildConfig
import com.example.clicker.authentication.TwitchAuthentication
import com.example.clicker.data.TokenDataStore
import com.example.clicker.data.TokenValidationWorker
import com.example.clicker.domain.GetFollowedLiveStreamsUseCase
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
import kotlinx.coroutines.CoroutineScope
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
    private val getFollowedLiveStreamsUseCase: GetFollowedLiveStreamsUseCase
): ViewModel(){


    private val _newUrlList =MutableStateFlow<List<StreamInfo>?>(null)
    val newUrlList:StateFlow<List<StreamInfo>?> = _newUrlList






    private var _uiState: MutableState<HomeUIState> = mutableStateOf(HomeUIState())
    val state:State<HomeUIState> = _uiState

    private var _loginUIState: MutableState<LoginStatus> = mutableStateOf(LoginStatus())
    val loginState:State<LoginStatus> = _loginUIState

    // really don't like this but It will work for now. Too tightly coupled and should be a dependency
    private val twitchAuthentication= TwitchAuthentication(
        twitchRepoImpl = twitchRepoImpl,
        tokenDataStore = tokenDataStore,
        scope = viewModelScope,
        _uiState = _uiState,
        _loginUIState = _loginUIState
    )

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






    //TODO: THIS SHOULD COME FROM THE TwitchAuthentication CLASS
    private val mutableAuthenticatedUserFlow = twitchAuthentication.mutableAuthenticatedUserFlow

    init{
        /**
         * starts the observing of the hot flow, mutableAuthenticatedUserFlow*/
        collectAuthenticatedUserFlow()
    }



    //TODO:THIS CAN BE MOVED
    private fun collectAuthenticatedUserFlow() =viewModelScope.launch {
        mutableAuthenticatedUserFlow.collect{mainState ->
            mainState.oAuthToken?.let{notNullToken ->
                twitchAuthentication.validateOAuthToken(
                    notNullToken,
                )
                //todo:send off the worker request
                tokenValidationWorker.enqueueRequest(notNullToken)

            }
            mainState.authUser?.let {user ->
                _uiState.value = _uiState.value.copy(
                    clientId = user.clientId,
                    userId = user.userId
                )
                getLiveStreams(validatedUser = user,mainState.oAuthToken!!)
            }
        }
    }


    fun setOAuthToken(oAuthToken:String) = viewModelScope.launch{
        //need to make a call to exchange the authCode for a validationToken
        Log.d("setOAuthToken","token -> $oAuthToken")
        twitchAuthentication.setOAuthToken(oAuthToken = oAuthToken)

    }

    fun beginLogout() = viewModelScope.launch{
      twitchAuthentication.beginLogout()
    }



    //THIS IS THE END


    fun pullToRefreshGetLiveStreams(resetUI: suspend()->Unit){
        viewModelScope.launch {

            withContext(Dispatchers.IO +CoroutineName("GetLiveStreamsPull")){
                getFollowedLiveStreamsUseCase
                    .invoke(
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
    }

    private suspend fun getLiveStreams(validatedUser: ValidatedUser, oAuthToken:String){
        Log.d("ValidatedUserUserId","user_id -> ${validatedUser.userId}")
        Log.d("ValidatedUserUserId","client_id -> ${validatedUser.clientId}")
        withContext(Dispatchers.IO +CoroutineName("GetLiveStreams")){
            getFollowedLiveStreamsUseCase.invoke(
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
                    //end
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
