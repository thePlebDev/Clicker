package com.example.clicker.presentation.home

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.data.TokenDataStore
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.repository.TwitchRepoImpl
import com.example.clicker.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataStoreViewModel @Inject constructor(
    private val twitchRepoImpl: TwitchRepo,
    private val tokenDataStore:TokenDataStore
): ViewModel() {

//
//    private val _uiState = MutableStateFlow("")
//    val uiState: StateFlow<String> = _uiState.as

    private val _uiState = mutableStateOf("")
    val state = _uiState

    private val authorizationCode:MutableStateFlow<String?> = MutableStateFlow(null)


    init {
        subscribeToValidationToken()
    }
//    init {
//        authCodeForValidateToken()
//    }


    //This should be called after the login
    private fun authCodeForValidateToken(authCode:String) = viewModelScope.launch{
             //need to make a call to exchange the authCode for a validationToken
        twitchRepoImpl

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