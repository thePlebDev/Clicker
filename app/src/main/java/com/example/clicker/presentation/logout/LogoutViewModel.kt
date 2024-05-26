package com.example.clicker.presentation.logout

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.domain.TwitchDataStore
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.presentation.home.HomeUIState
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.NetworkNewUserResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(
    private val authentication: TwitchAuthentication,
    private val tokenDataStore: TwitchDataStore,
): ViewModel() {

    private var _showLoading: MutableState<Boolean> = mutableStateOf(false)
    val showLoading: State<Boolean> = _showLoading

    private var _navigateHome: MutableState<Boolean> = mutableStateOf(false)
    val navigateHome: State<Boolean> = _navigateHome

    init {

        getInitialLoggedOut()
    }

    fun setShowLogin(value:Boolean)=viewModelScope.launch{
        _showLoading.value = value
        tokenDataStore.setLoggedOutLoading(value)
    }
    fun setNavigateHome(value:Boolean)=viewModelScope.launch{
        _navigateHome.value = value

    }

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
                            setNavigateHome(true)
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

    fun getInitialLoggedOut(){
        viewModelScope.launch {
            //tokenDataStore.setLoggedOutLoading(false)
            tokenDataStore.getLoggedOutLoading().collect{loggedOutStatus ->
                Log.d("LoginViewModelLifecycle","getInitialLoggedOutStatus --> $loggedOutStatus")
                _showLoading.value = loggedOutStatus
            }

        }
    }
//
//    NetworkNewUserResponse.Loading
//    NetworkNewUserResponse.NewUser
//    NetworkNewUserResponse.Success
//    NetworkNewUserResponse.Failure
//    NetworkNewUserResponse.NetworkFailure
    //todo: the documentation for NetworkNewUserResponse is out of date

    fun validateOAuthToken(oAuthToken: String){
       viewModelScope.launch(Dispatchers.IO){
           delay(1000)
           authentication.validateToken(oAuthToken).collect{response ->
               when (response) {
                   is NetworkNewUserResponse.Loading->{
                       Log.d("validateOAuthTokenCall","Loading")
                   }
                   is NetworkNewUserResponse.NewUser->{
                       Log.d("validateOAuthTokenCall","NewUser")
                   }
                   is NetworkNewUserResponse.Success ->{
                       Log.d("validateOAuthTokenCall","Success")
                       setShowLogin(false)
                       setNavigateHome(true)
                   }
                   is NetworkNewUserResponse.Failure ->{
                       Log.d("validateOAuthTokenCall","Failure")
                       setShowLogin(false)
                   }
                   is NetworkNewUserResponse.NetworkFailure->{
                       Log.d("validateOAuthTokenCall","NetworkFailure")
                       setShowLogin(false)
                   }
                   is NetworkNewUserResponse.Auth401Failure ->{
                       Log.d("validateOAuthTokenCall","Auth401Failure")
                       setShowLogin(false)
                   }
               }

           }

       }
    }


}