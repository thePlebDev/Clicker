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
import com.example.clicker.network.models.ValidatedUser
import com.example.clicker.network.repository.TwitchRepoImpl
import com.example.clicker.network.websockets.TwitchWebSocket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val _urlList = mutableStateListOf<StreamInfo>()
    val urlList: List<StreamInfo> = _urlList



    private var _uiState: MutableState<HomeUIState> = mutableStateOf(HomeUIState())
    val state:State<HomeUIState> = _uiState

    private var _loginUIState: MutableState<LoginStatus> = mutableStateOf(LoginStatus())
    val loginState:State<LoginStatus> = _loginUIState

    private val oAuthAuthenticationToken:MutableStateFlow<String?> = MutableStateFlow(null) //received from Twitch OAuth login
    private val validatedUser:MutableStateFlow<ValidatedUser?> = MutableStateFlow(null)


    init {
        getOAuthToken()
    }
    init{
        viewModelScope.launch {
            oAuthAuthenticationToken.collect{oAuthToken ->
                Log.d("oAuthAuthenticationTokenFound","token -> $oAuthToken")
                oAuthToken?.let{notNullToken ->
                    validateOAuthToken(notNullToken)
                }
            }
        }

    }
    init{

        viewModelScope.launch{
            validatedUser.collect{user ->

                user?.let {
                    Log.d("clientId",it.clientId)
                    getLiveStreams(validatedUser = user,oAuthAuthenticationToken.value!!)
                }
            }
        }
    }



    private suspend fun getLiveStreams(validatedUser: ValidatedUser, oAuthToken:String){
        twitchRepoImpl.getFollowedLiveStreams(
            authorizationToken = oAuthToken,
            clientId = validatedUser.clientId,
            userId = validatedUser.userId
        ).collect{response ->
            when(response){
                is Response.Loading ->{}
                is Response.Success ->{


                    for(item in response.data.data){
                        val newUrl = item.thumbNailUrl
                            .replace("{width}","${_uiState.value.width}")
                            .replace("{height}","${_uiState.value.aspectHeight}")
                        _urlList.add(
                            StreamInfo(
                                streamerName = item.userName,
                                streamTitle = item.title,
                                gameTitle = item.gameName,
                                views = item.viewerCount,
                                url = newUrl,
                                broadcasterId = item.userId
                        )
                        )
                    }
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
                oAuthAuthenticationToken.tryEmit(storedOAuthToken)
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
                    validatedUser.tryEmit(response.data)
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
        oAuthAuthenticationToken.tryEmit(oAuthToken)


    }

    fun beginLogout() = viewModelScope.launch{
        _loginUIState.value = _loginUIState.value.copy(
            showLoginModal = true,
            loginStatusText = "Logging out",
            loginStep1 = Response.Success(true),
            loginStep2 = Response.Loading,
            loginStep3 = null,
        )
        twitchRepoImpl.logout(clientId = validatedUser.value?.clientId!!,token = oAuthAuthenticationToken.value!!)
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
