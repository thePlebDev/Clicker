package com.example.clicker.presentation

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.BuildConfig
import com.example.clicker.network.repository.GitHubRepoImpl
import com.example.clicker.util.Response
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
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
    val gitHubRepo: GitHubRepoImpl = GitHubRepoImpl(),
    val twitchRepoImpl: TwitchRepoImpl = TwitchRepoImpl()
): ViewModel(){

    private val CLIENT_ID = BuildConfig.CLIENT_ID
    private val CLIENT_SECRET = BuildConfig.CLIENT_SECRET

    private var _uiState: MutableState<HomeUIState> = mutableStateOf(HomeUIState())
    val state:State<HomeUIState> = _uiState

    private val appAccessToken:MutableStateFlow<String?> = MutableStateFlow(null) //received from GitHub login
    private val accessToken:MutableStateFlow<String?> = MutableStateFlow(null) // exchanged for authenticationToken

    init {
        viewModelScope.launch {
            accessToken.collect{accessToken ->
                if(accessToken != null){
                    getProfileData("https://api.github.com/user", "Bearer $accessToken")
                }else{
                   // Log.d("GITHUB","NULL ACCESS TOKEN")
                }
            }
        }
    }
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
        Log.d("twitch",token)

        _uiState.value = _uiState.value.copy(
            loadingLoginText = "Reading ancient magic tablet",
            userLogginIn =true,
            loginStep1 = Response.Success(true),
            loginStep2 = Response.Loading
        )
        appAccessToken.tryEmit(token)
    }


    private fun makeGitHubRequest(clientId:String, clientSecret:String, code:String) = viewModelScope.launch{

        gitHubRepo.getAccessToken(clientId,clientSecret,code).collect{ response ->
            when(response){
                is Response.Loading ->{Log.d("GITHUB","makeGitHubRequest LOADING")}
                is Response.Success ->{

                    _uiState.value = _uiState.value.copy(
                        loadingLoginText = "Learning new spells",
                        loginStep1 = Response.Success(true),
                        loginStep2 = Response.Success(true),
                        loginStep3 = Response.Loading
                    )
                    accessToken.emit(response.data.accessToken)
                }
                is Response.Failure ->{Log.d("GITHUB","makeGitHubRequest FAILURE")}
            }
        }
    }

    private fun getProfileData(url:String,authorizationHeader:String) = viewModelScope.launch{
        gitHubRepo.getProfileData(
            url = url,
            authorizationHeader = authorizationHeader
        ).collect{ response ->
            when(response){
                is Response.Loading ->{
                    Log.d("GITHUB","getProfileData LOADING")
                }
                is Response.Success ->{
                    Log.d("GITHUB","getProfileData SUCCESS")

                    _uiState.value = _uiState.value.copy(
                        loadingLoginText = "Casting true invisibility",
                        loginStep1 = Response.Success(true),
                        loginStep2 = Response.Success(true),
                        loginStep3 = Response.Success(true),
                        hideModal = true,
                        userProfile = response.data.login

                    )
                }
                is Response.Failure ->{
                    Log.d("GITHUB","getProfileData FAILURE")
                }
            }
        }

    }

    fun changeLoginStatus(status:Boolean){
        _uiState.value = _uiState.value.copy(
            userLogginIn =status
        )
    }





}