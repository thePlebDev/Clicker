package com.example.clicker.presentation.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.BuildConfig
import com.example.clicker.util.Response
import kotlinx.coroutines.launch
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.repository.TwitchRepoImpl
import com.example.clicker.network.websockets.TwitchWebSocket
import kotlinx.coroutines.flow.MutableStateFlow

data class HomeUIState(
    val userLogginIn:Boolean = false,
    val userProfile:String? = null,
    val authenticationCode:String? = null,
    val loadingLoginText:String ="Casting teleportation spell",
    val hideModal:Boolean = false,
    val loginStep1:Response<Boolean>? = Response.Loading,
    val width:Int =0,
    val aspectHeight:Int =0,
    val loginStep2:Response<Boolean>? = null,
    val loginStep3:Response<Boolean>? = null,


)


class HomeViewModel(
    //val twitchRepoImpl: TwitchRepo = TwitchRepoImpl(),
    //val webSocket: TwitchWebSocket = TwitchWebSocket()
): ViewModel(){

    private val CLIENT_ID = BuildConfig.CLIENT_ID
    private val CLIENT_SECRET = BuildConfig.CLIENT_SECRET

    private val _urlList = mutableStateListOf<StreamInfo>()
    val urlList: List<StreamInfo> = _urlList


    private var _uiState: MutableState<HomeUIState> = mutableStateOf(HomeUIState())
    val state:State<HomeUIState> = _uiState

    private val appAccessToken:MutableStateFlow<String?> = MutableStateFlow(null) //received from Twitch OAuth login


    init {
        viewModelScope.launch {

            appAccessToken.collect{token ->
                if (token != null){

                }

            }
        }
    }


    fun updateAuthenticationCode(token:String){

        _uiState.value = _uiState.value.copy(
            loadingLoginText = "Reading ancient magic tablet",
            userLogginIn =true,
            loginStep1 = Response.Success(true),
            loginStep2 = Response.Loading
        )
        appAccessToken.tryEmit(token)
    }
    fun updateLoginUI(
        loginStep1Status: Response<Boolean>? = null,
        loginStep2Status: Response<Boolean>? = null,
        loginStep3Status: Response<Boolean>? = null
    ){
        _uiState.value = _uiState.value.copy(
            loadingLoginText = "Practicing spell casting",
            loginStep1 = loginStep1Status,
            loginStep2 = loginStep2Status,
            loginStep3 = loginStep3Status,

        )
    }


    fun changeLoginStatus(status:Boolean){
        _uiState.value = _uiState.value.copy(
            userLogginIn =status
        )
    }

    fun updateAspectWidthHeight(width:Int, aspectHeight: Int){
        _uiState.value = _uiState.value.copy(
            aspectHeight = aspectHeight,
            width = width
            )

    }


    override fun onCleared() {
        super.onCleared()
        //webSocket.close()

    }


}
