package com.example.clicker.presentation.home

import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
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
import kotlinx.coroutines.flow.combine

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

    private val _oAuthUserToken:MutableStateFlow<String?> = MutableStateFlow(null)

    private val _clientId: MutableStateFlow<String?> = MutableStateFlow(null)


    private val authenticatedUserFlow = combine(
        _oAuthUserToken,
        _clientId

    ){ oAuthToken,
       clientId
        ->
        MainState(
            hasOAuthToken = oAuthToken,
            hasClientId =  clientId
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily,
        MainState(hasOAuthToken = null,hasClientId = null)
    )


    init{
        watchAuthenticatedUserFlow()
    }
    init {
        getOAuthToken()
    }

    private fun watchAuthenticatedUserFlow() = viewModelScope.launch{
        authenticatedUserFlow.collect{mainState ->
            mainState.hasOAuthToken?.let{oAuthToken ->
                Log.d("validateOAuthUserToken", "OAuthToken -> $oAuthToken")
                //run the token validation
                validateOAuthToken(oAuthToken)
            }
            mainState.hasClientId?.let{clientId ->
                Log.d("validateOAuthUserToken", "clientId -> $clientId")
                // get the streams

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
//                    Log.d("validateOAuthUserToken", "SUCCESS")
//                    Log.d("validateOAuthUserToken", "CLIENT_ID -->" +response.data.clientId)
                    _clientId.tryEmit(response.data.clientId)

                }
                is Response.Failure ->{
                    Log.d("validateOAuthUserToken", "FAILURE")

                }

                else -> {}
            }

        }
    }

    fun getFollowedStreams() = viewModelScope.launch{
        _clientId.collect{
            it?.also{

            }
        }
    }



    fun setOAuthToken(oAuthToken:String) = viewModelScope.launch{
             //need to make a call to exchange the authCode for a validationToken
        tokenDataStore.setOAuthToken(oAuthToken)
        _oAuthUserToken.tryEmit(oAuthToken)


    }
    private fun getOAuthToken() = viewModelScope.launch{
        tokenDataStore.getOAuthToken().collect{storedOAuthToken ->
            if(storedOAuthToken.length > 2){
                _oAuthUserToken.tryEmit(storedOAuthToken)
            }
        }
    }


}
data class MainState(
    val hasOAuthToken:String? = null,
    val hasClientId:String? = null,
)