package com.example.clicker.presentation.logout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.domain.TwitchDataStore
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.NetworkNewUserResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(
    private val authentication: TwitchAuthentication,
    private val tokenDataStore: TwitchDataStore,
): ViewModel() {



    fun logout(clientId:String,oAuthToken:String)  = viewModelScope.launch{
        //so I need to logout and on success I need to set the internal logout flag to true
        Log.d("newlogoutFunction","LogoutViewModel.logout() called")
        withContext(Dispatchers.IO) {
            authentication.logout(
                clientId = clientId,
                token = oAuthToken
            )
                .collect { response ->
                    when (response) {
                        is NetworkAuthResponse.Loading -> {
                            Log.d("newlogoutFunction","LOADING")

                        }
                        is NetworkAuthResponse.Success -> {
                            tokenDataStore.setLoggedOutStatus(true)
                            Log.d("newlogoutFunction","SUCCESS")
                        }
                        is NetworkAuthResponse.Failure -> {
                            Log.d("newlogoutFunction","FAILED")

                        }
                        is NetworkAuthResponse.NetworkFailure->{
                            Log.d("newlogoutFunction","NETWORK FAILURE")

                        }
                        is NetworkAuthResponse.Auth401Failure ->{
                            Log.d("newlogoutFunction","401 AUTH FAILURE")

                        }


                    }
                }
        }
    }

}