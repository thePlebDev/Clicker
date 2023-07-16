package com.example.clicker.presentation

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.BuildConfig
import com.example.clicker.util.Response
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.repository.TwitchRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow

data class HomeUIState(
    val userLogginIn:Boolean = false,
    val userProfile:String? = null,
    val authenticationCode:String? = null,
    val loadingLoginText:String ="Casting teleportation spell",
    val hideModal:Boolean = false,
    val loginStep1:Response<Boolean>? = Response.Loading,
    val loginStep2:Response<Boolean>? = null,
    val loginStep3:Response<Boolean>? = null,


)


class HomeViewModel(
    val twitchRepoImpl: TwitchRepo = TwitchRepoImpl()
): ViewModel(){

    private val CLIENT_ID = BuildConfig.CLIENT_ID
    private val CLIENT_SECRET = BuildConfig.CLIENT_SECRET

    private var _uiState: MutableState<HomeUIState> = mutableStateOf(HomeUIState())
    val state:State<HomeUIState> = _uiState

    private val appAccessToken:MutableStateFlow<String?> = MutableStateFlow(null) //received from Twitch OAuth login


    init {
        viewModelScope.launch {

            appAccessToken.collect{token ->
                if (token != null){
                    twitchRepoImpl.validateToken(token).collect{
                        Log.d("Twitchval","response " + it)
                    }
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


    fun changeLoginStatus(status:Boolean){
        _uiState.value = _uiState.value.copy(
            userLogginIn =status
        )
    }





}