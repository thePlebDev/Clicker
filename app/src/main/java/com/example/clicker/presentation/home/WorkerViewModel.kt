package com.example.clicker.presentation.home

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkManager
import com.example.clicker.data.TokenDataStore
import com.example.clicker.data.TokenValidationWorker
import com.example.clicker.data.workManager.OAuthTokeValidationWorker
import com.example.clicker.network.models.ValidatedUser
import com.example.clicker.util.Response
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkerViewModel @Inject constructor(
    private val tokenValidationWorker: TokenValidationWorker,
    private val tokenDataStore: TokenDataStore
): ViewModel() {



    private val _oAuthUserToken: MutableStateFlow<String?> = MutableStateFlow(null)


    init{
        viewModelScope.launch {
            _oAuthUserToken.collect{token ->
                token?.let{
                    Log.d("OAuthTokenThingy","token -->  $token")
                    runWorkManager(token)
                }

            }
        }
    }
    init {
        getOAuthToken()
    }

    private fun getOAuthToken() = viewModelScope.launch{
        tokenDataStore.getOAuthToken().collect{storedOAuthToken ->
            if(storedOAuthToken.length > 2){
                Log.d("getOAuthToken",storedOAuthToken)
                _oAuthUserToken.tryEmit(storedOAuthToken)
            }else{
                //todo: THIS NEEDS TO BE ADDRESSED EVENTUALLY
//                Log.d("getOAuthToken","no token ->  $storedOAuthToken")
//                _uiState.value = _uiState.value.copy(
//                    authState = "No Authentication Token"
//                )
//                _showLogin.value = Response.Failure(Exception("NO OAuthToken"))
            }
        }
    }
    private fun runWorkManager(oAuthToken:String){
        //TODO: GIVE IT THE OAUTH TOKEN
        tokenValidationWorker.enqueueRequest(oAuthToken).observeForever{



            if(it.outputData.getString("result_key") != null){
                val serializedValue = it.outputData.getString("result_key")
                val customObject = Gson().fromJson(serializedValue, ValidatedUser::class.java)
                Log.d("OAuthTokenThingy","WorkInfo -->  ${customObject.userId}")
                Log.d("OAuthTokenThingy","WorkInfo -->  ${customObject.clientId}")
                Log.d("OAuthTokenThingy","WorkInfo -->  ${customObject.login}") // this is the user's username
                Log.d("OAuthTokenThingy","WorkInfo -->  ${customObject.scopes}")
            }
        }

    }

}