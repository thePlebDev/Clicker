package com.example.clicker.presentation.home

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.data.TokenDataStore
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.repository.TwitchRepoImpl
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DataStoreViewModel(
    application:Application,
):AndroidViewModel(application) {

    private val tokenDataStore:TokenDataStore = TokenDataStore(application)
    val twitchRepoImpl: TwitchRepo = TwitchRepoImpl()
//
//    private val _uiState = MutableStateFlow("")
//    val uiState: StateFlow<String> = _uiState.as

    private val _uiState = mutableStateOf("")
    val state = _uiState

    private val authorizationCode:MutableStateFlow<String?> = MutableStateFlow(null)


    init {
        subscribeToValidationToken()
    }
    init {
        authCodeForValidateToken()
    }


    private fun authCodeForValidateToken() = viewModelScope.launch{
        authorizationCode.collect{authCode ->
            authCode?.let{code ->
                //validate auth code and get validation token
                Log.d("authCodeForValidateToken()",code)
                twitchRepoImpl.validateToken(code).collect{response ->
                    when(response){
                        is Response.Loading ->{
                            Log.d("validatingUser","LOADING")
                        }
                        is Response.Success ->{
                            Log.d("validatingUser","SUCCESS")
                            Log.d("validatingUserTOKEN",code)
                            Log.d("validatingUserUSERID",response.data.userId)
                            Log.d("validatingUserCLIENTID",response.data.clientId)
//                            getLiveFollowedStreams(
//                                authorizationHeaderToken = "Bearer $token",
//                                userId = response.data.userId,
//                                clientId = response.data.clientId
//
//                            )
                        }
                        is Response.Failure ->{
                            Log.d("validatingUser","FAILURE")
                        }
                    }
                }
            }

        }
    }





    private fun subscribeToValidationToken()= viewModelScope.launch {
        tokenDataStore.getToken().stateIn(viewModelScope, SharingStarted.Lazily,"").collect{authCode ->
            Log.d("DataStoreViewModel SUB",authCode)
            if(authCode.length > 2){
                Log.d("DataStoreViewModel SUB","validate token request")
            }
            _uiState.value = authCode

        }
    }

    fun getValidationToken() = viewModelScope.launch{
        tokenDataStore.getToken().collect{token ->
            Log.d("DataStoreViewModel getToken()",token)
        }
    }

    fun setToken(loginToken:String) = viewModelScope.launch{
        tokenDataStore.updateToken(loginToken)
    }

    fun updateAuthorizationCode(authCode:String){
        authorizationCode.tryEmit(authCode)
    }
}