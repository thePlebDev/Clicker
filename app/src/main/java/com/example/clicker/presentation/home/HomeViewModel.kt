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
import com.example.clicker.util.Response
import kotlinx.coroutines.launch
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.AuthenticatedUser
import com.example.clicker.network.models.StreamData
import com.example.clicker.network.models.ValidatedUser
import com.example.clicker.network.models.toStreamInfo
import com.example.clicker.network.repository.TwitchRepoImpl
import com.example.clicker.network.websockets.TwitchUserData
import com.example.clicker.network.websockets.TwitchWebSocket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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
    val userId:String =""



)
data class LoginStatus(
    val showLoginModal:Boolean = true,
    val showLoginButton:Boolean = true,
    val loginStatusText:String = "Retrieving authentication token",
    val loginStep1: Response<Boolean>? = Response.Loading,
    val loginStep2: Response<Boolean>? = null,
    val loginStep3: Response<Boolean>? = null,

)


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tokenDataStore: TokenDataStore,
    private val twitchRepoImpl: TwitchRepo,
): ViewModel(){

    private val CLIENT_ID = BuildConfig.CLIENT_ID
    private val CLIENT_SECRET = BuildConfig.CLIENT_SECRET


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
        collectAuthenticatedUserFlow()
    }

    private fun collectAuthenticatedUserFlow() =viewModelScope.launch {
        mutableAuthenticatedUserFlow.collect{mainState ->
            mainState.oAuthToken?.let{notNullToken ->
                validateOAuthToken(notNullToken)
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



    private suspend fun getLiveStreams(validatedUser: ValidatedUser, oAuthToken:String){
        Log.d("ValidatedUserUserId","user_id -> ${validatedUser.userId}")
        Log.d("ValidatedUserUserId","client_id -> ${validatedUser.clientId}")
        twitchRepoImpl.getFollowedLiveStreams(
            authorizationToken = oAuthToken,
            clientId = validatedUser.clientId,
            userId = validatedUser.userId
        ).collect{response ->
            when(response){
                is Response.Loading ->{}
                is Response.Success ->{


                    val replacedWidthHeight =response.data.map{
                        it.changeUrlWidthHeight(_uiState.value.width,_uiState.value.aspectHeight)
                    }

                    _newUrlList.tryEmit(replacedWidthHeight)


                    _loginUIState.value = _loginUIState.value.copy(
                        loginStatusText ="Success!!!",
                        loginStep3 = Response.Success(true),
                        showLoginModal = false,
                    )
                }
                is Response.Failure ->{
                    _loginUIState.value = _loginUIState.value.copy(
                        loginStatusText ="Error occurred!! Please try logging in again",
                        loginStep3 = Response.Failure(Exception("Unable to getStream")),
                    )
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

    private fun getOAuthToken() = viewModelScope.launch{
        tokenDataStore.getOAuthToken().collect{storedOAuthToken ->

            if(storedOAuthToken.length > 2){

                _loginUIState.value = _loginUIState.value.copy(
                    loginStatusText ="Validating Authentication token",
                    loginStep1 = Response.Success(true),
                    loginStep2 = Response.Loading
                )
                mutableAuthenticatedUserFlow.tryEmit(
                    mutableAuthenticatedUserFlow.value.copy(
                        oAuthToken = storedOAuthToken
                    )
                )
            }else{

                _loginUIState.value = _loginUIState.value.copy(
                    loginStatusText ="Looks like you are new here. Please login with Twitch to be give a authentication token",
                    loginStep1 = Response.Failure(Exception("No authentication token found"))
                )
            }
        }
    }
    private fun validateOAuthToken(oAuthenticationToken:String) = viewModelScope.launch{
        twitchRepoImpl.validateToken(oAuthenticationToken).collect{response ->
            when(response){
                is Response.Loading ->{}
                is Response.Success ->{
                    _loginUIState.value = _loginUIState.value.copy(
                        loginStatusText ="Retrieving live streams",
                        loginStep2 = Response.Success(true),
                        loginStep3 = Response.Loading,
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
                        loginStatusText ="Please login with Twitch to be issued a new authentication token",
                        loginStep2 = Response.Failure(Exception("failed to validate authentication token"))
                    )
                }
            }
        }

    }


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
            loginStatusText = "Logging out",
            loginStep1 = Response.Success(true),
            loginStep2 = Response.Loading,
            loginStep3 = null,
        )
        twitchRepoImpl.logout(clientId = mutableAuthenticatedUserFlow.value.authUser?.clientId!!,token = mutableAuthenticatedUserFlow.value.oAuthToken!!)
            .collect{response ->
           // Log.d("logoutResponse", "beginLogoutCollecting ->${it}")
            when(response){
                is Response.Loading ->{}
                is Response.Success ->{
                    _loginUIState.value = _loginUIState.value.copy(
                        loginStatusText = "Success!! Please log in with Twitch",
                        loginStep1 = Response.Success(true),
                        loginStep2 = Response.Success(true),
                        loginStep3 = null,
                    )
                }
                is Response.Failure ->{}
                else -> {}
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
