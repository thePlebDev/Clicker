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

    private val oAuthUserToken:MutableStateFlow<String?> = MutableStateFlow(null)

    init{

        validateOAuthUserToken()
    }
    init {
        getOAuthToken()
    }


    private fun validateOAuthUserToken() = viewModelScope.launch{
        oAuthUserToken.collect{oAuthUserToken ->
            if(oAuthUserToken != null){
                Log.d("validateOAuthUserToken", oAuthUserToken)
                //so now we need to validate and get the client_id
                validateOAuthToken(oAuthUserToken)
            }else{
                Log.d("validateOAuthUserToken", "NULL")
            }
        }
    }

    private suspend fun validateOAuthToken(oAuthUserToken:String){
        twitchRepoImpl.validateToken(oAuthUserToken).collect{response ->
            when(response){
                is Response.Loading ->{
                    Log.d("validateOAuthUserToken", "LOADING")
                }
                is Response.Success ->{
                    Log.d("validateOAuthUserToken", "SUCCESS")
                    Log.d("validateOAuthUserToken", "CLIENT_ID -->" +response.data.clientId)

                }
                is Response.Failure ->{
                    Log.d("validateOAuthUserToken", "FAILURE")

                }
            }

        }
    }



    fun setOAuthToken(oAuthToken:String) = viewModelScope.launch{
             //need to make a call to exchange the authCode for a validationToken
        tokenDataStore.setOAuthToken(oAuthToken)
        oAuthUserToken.tryEmit(oAuthToken)


    }
    private fun getOAuthToken() = viewModelScope.launch{
        tokenDataStore.getOAuthToken().collect{storedOAuthToken ->
            if(storedOAuthToken.length > 2){
                oAuthUserToken.tryEmit(storedOAuthToken)
            }
        }
    }


}