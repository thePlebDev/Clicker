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
import kotlinx.coroutines.flow.MutableStateFlow

data class HomeUIState(
    val userLogginIn:Boolean = false,
    val authenticationCode:String? = null,
    val loadingLoginText:String ="Casting transportation spell",
    val loginStep1:Response<Boolean>? = Response.Loading,
    val loginStep2:Response<Boolean>? = null,
    val loginStep3:Response<Boolean>? = null,

)


class HomeViewModel(
    val gitHubRepo: GitHubRepoImpl = GitHubRepoImpl()
): ViewModel(){

    private val CLIENT_ID = BuildConfig.CLIENT_ID
    private val CLIENT_SECRET = BuildConfig.CLIENT_SECRET

    private var _uiState: MutableState<HomeUIState> = mutableStateOf(HomeUIState())
    val state:State<HomeUIState> = _uiState

    private val authenticationToken:MutableStateFlow<String?> = MutableStateFlow(null) //received from GitHub login
    private val accessToken:MutableStateFlow<String?> = MutableStateFlow(null) // exchanged for authenticationToken

    init {
        viewModelScope.launch {
            accessToken.collect{accessToken ->
                if(accessToken != null){
                    Log.d("GITHUB",accessToken +"CAN NOW MAKE REQUESTS ON BEHALF OF USER")
                }else{
                   // Log.d("GITHUB","NULL ACCESS TOKEN")
                }
            }
        }
    }
    init {
        viewModelScope.launch {
            authenticationToken.collect{authenticationToken ->
                if(authenticationToken != null){
                    makeGitHubRequest(
                        clientId= CLIENT_ID,
                        clientSecret = CLIENT_SECRET,
                        code = authenticationToken
                    )
                }else{
                    //Log.d("GITHUB","authenticationToken is NULL")
                }

            }
        }
    }

    fun updateAuthenticationCode(authenticationCode:String){

        _uiState.value = _uiState.value.copy(
            loadingLoginText = "Reading ancient magic tablet",
            userLogginIn =true,
            loginStep1 = Response.Success(true),
            loginStep2 = Response.Loading
        )
        authenticationToken.tryEmit(authenticationCode)
    }
    fun userIsLogginIn(){
        _uiState.value = _uiState.value.copy(
            userLogginIn =true
        )

    }

    fun makeGitHubRequest(clientId:String,clientSecret:String,code:String) = viewModelScope.launch{

        gitHubRepo.getAccessToken(clientId,clientSecret,code).collect{ response ->
            when(response){
                is Response.Loading ->{Log.d("GITHUB","makeGitHubRequest LOADING")}
                is Response.Success ->{

                    _uiState.value = _uiState.value.copy(
                        loadingLoginText = "learning new spells",
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







}