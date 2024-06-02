package com.example.clicker.presentation.authentication.logout

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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

    private val _navigateToLoginWithTwitch: MutableState<Boolean> = mutableStateOf(false)
    val navigateToLoginWithTwitch: State<Boolean> = _navigateToLoginWithTwitch

    private val _navigateHome: MutableState<Boolean?> = mutableStateOf(null)
    val navigateHome: State<Boolean?> = _navigateHome
    private val _showLoginWithTwitchButton: MutableState<Boolean> = mutableStateOf(false)
    val showLoginWithTwitchButton: State<Boolean> = _showLoginWithTwitchButton
    private val _showErrorMessage: MutableState<Boolean> = mutableStateOf(false)
    val showErrorMessage: State<Boolean> = _showErrorMessage
    private val _errorMessage: MutableState<String> = mutableStateOf("Failed")
    val errorMessage: State<String> = _errorMessage



    private val _newUserNavigateHome: MutableState<Boolean> = mutableStateOf(false)
    val newUserNavigateHome: State<Boolean> = _newUserNavigateHome

    /**
     * _loggedOutStatus is what we use to make this offline first
     * */
    private val _loggedOutStatus: MutableState<String> = mutableStateOf("FALSE")
    val loggedOutStatus: State<String> = _loggedOutStatus // if this is WAITING the we make the login with Twitch logout first

    /**
     * login with Twitch Button disabled
     * */
    private val _loginWithTwitchButtonEnabled: MutableState<Boolean> = mutableStateOf(true)
    val buttonEnabled: State<Boolean> = _loginWithTwitchButtonEnabled

    init {
        _navigateHome.value = false

        getInitialLoggedOut()
    }

    init {
        viewModelScope.launch {
            val loggedInStatus =tokenDataStore.getLoggedOutStatus().first()
            _loggedOutStatus.value = loggedInStatus ?:""
        }
    }

    private fun getInitialLoggedOut(){
        viewModelScope.launch {
            tokenDataStore.getLoggedOutLoading().collect{loggedOutStatus ->
                Log.d("LoginViewModelLifecycle","getInitialLoggedOutStatus --> $loggedOutStatus")
                _showLoading.value = loggedOutStatus
            }

        }
    }

    fun setNavigateToLoginWithTwitch(value:Boolean){
        _navigateToLoginWithTwitch.value = value
    }
    fun setShowLoginWithTwitchButton(value:Boolean){
        _showLoginWithTwitchButton.value = value
    }

    fun setShowLogin(value:Boolean)=viewModelScope.launch{
        _showLoading.value = value
       // tokenDataStore.setLoggedOutLoading(value)
    }
    fun setNavigateHome(value:Boolean)=viewModelScope.launch{
        _navigateHome.value = value
        Log.d("setNavigateHome","_navigateHome.value = value -->${_navigateHome.value}")

    }
    /**
     * setLoggedOutStatus() is a function used to set a token data store value to one of 3 values:
     *
     * 1) FALSE - the user is not logged out
     * 2) WAITING - the user is waiting to be logged out
     * 3) TRUE - the user is logged out
     * */
    fun setLoggedOutStatus(value:String){
        viewModelScope.launch {
            tokenDataStore.setLoggedOutStatus(value)
            _loggedOutStatus.value = value
        }
    }

    private suspend fun setLogoutWaitingState(clientId: String){
        setLoggedOutStatus("WAITING")
        setShowLogin(true)
        tokenDataStore.setClientId(clientId)
        _loggedOutStatus.value = "WAITING"
        _loginWithTwitchButtonEnabled.value = false
    }


    //
    fun logout(clientId:String,oAuthToken:String)  = viewModelScope.launch{
        //so I need to logout and on success I need to set the internal logout flag to true
        Log.d("newlogoutFunction","clientId -->$clientId")
        setLogoutWaitingState(clientId)
        withContext(Dispatchers.IO) {
            authentication.logout(
                clientId = clientId,
                token =oAuthToken
            ).collect{response ->
                // to on success I need to set waiting to FALSE setLoggedOutStatus("FALSE")
                when(response){
                    is  NetworkAuthResponse.Loading ->{
                        Log.d("newlogoutFunction","LOADING")
                    }
                    is  NetworkAuthResponse.Success ->{
                        Log.d("newlogoutFunction","SUCCESS")
                        setLoggedOutStatus("TRUE")
                        tokenDataStore.setOAuthToken("loggedOut")
                        _loginWithTwitchButtonEnabled.value = true
                    }
                    is  NetworkAuthResponse.Failure ->{
                        Log.d("newlogoutFunction","FAILURE")
                        _loginWithTwitchButtonEnabled.value = true
                    }
                    is  NetworkAuthResponse.NetworkFailure ->{
                        Log.d("newlogoutFunction","NETWORK FAILURE")
                        _loginWithTwitchButtonEnabled.value = true
                    }
                    is  NetworkAuthResponse.Auth401Failure ->{
                        Log.d("newlogoutFunction","AUTH 401 FAILURE")
                        _loginWithTwitchButtonEnabled.value = true
                    }

                }

            }
        }
    }


    fun logoutAgain() = viewModelScope.launch(Dispatchers.IO){
        Log.d("logoutAgainTesting", "logoutAgain")
        val oAuthToken = tokenDataStore.getOAuthToken().first()
        val clientId = tokenDataStore.getClientId().first()
        Log.d("logoutAgainTesting", "clientId --> $clientId")
        Log.d("logoutAgainTesting", "oAuthToken --> $oAuthToken")
        setShowLogin(true)
        authentication.logout(
            clientId = clientId,
            token =oAuthToken
        ).collect{response ->
            // to on success I need to set waiting to FALSE setLoggedOutStatus("FALSE")
            when(response){
                is  NetworkAuthResponse.Loading ->{
                    Log.d("newlogoutFunction","LOADING")
                }
                is  NetworkAuthResponse.Success ->{
                    Log.d("newlogoutFunction","SUCCESS")
                    setLoggedOutStatus("TRUE")
                    tokenDataStore.setOAuthToken("loggedOut")
                    _navigateToLoginWithTwitch.value = true
                }
                is  NetworkAuthResponse.Failure ->{
                    Log.d("newlogoutFunction","FAILURE")
                    setResponseState("Failed. Try again")

                }
                is  NetworkAuthResponse.NetworkFailure ->{
                    Log.d("newlogoutFunction","NETWORK FAILURE")
                    setResponseState("Network Error please try again")

                }
                is  NetworkAuthResponse.Auth401Failure ->{
                    Log.d("newlogoutFunction","AUTH 401 FAILURE")
                    setResponseState("Error. Please try again")

                }

            }

        }

    }





    fun validateOAuthToken(oAuthToken: String){
        Log.d("validateOAuthTokenCall","VALIDATING TOKEN")
       viewModelScope.launch(Dispatchers.IO){
           val getLoginStatus = tokenDataStore.getLoggedOutStatus().first()
           setShowLogin(true)
           delay(1000)
           Log.d("validateOAuthTokenCall","getLoginStatus --> $getLoginStatus")

               authentication.validateToken(oAuthToken).collect{response ->
                   when (response) {
                       is NetworkNewUserResponse.Loading->{
                       }
                       is NetworkNewUserResponse.Success ->{
                           Log.d("validateOAuthTokenCall","Success")
                           //todo: might also have to do something with the validated User but We will see
                           returningUserReturnHome(oAuthToken)
                       }
                       is NetworkNewUserResponse.Failure ->{
                           Log.d("validateOAuthTokenCall","Failure")
                           setResponseState("Failed. Please login again")
                       }
                       is NetworkNewUserResponse.NetworkFailure->{
                           Log.d("validateOAuthTokenCall","NetworkFailure")
                           setResponseState("Network error. Please try again later")
                       }
                       is NetworkNewUserResponse.Auth401Failure ->{
                           setResponseState("Verification failed. Please login again")
                       }
                   }
               }

       }
    }

    private suspend fun returningUserReturnHome(oAuthToken: String){
        tokenDataStore.setOAuthToken(oAuthToken)
        tokenDataStore.setLoggedOutStatus("FALSE")

        _navigateHome.value = true
    }
private suspend fun setResponseState(message:String){
    setShowLogin(false)
    _errorMessage.value = message
    _showErrorMessage.value = true
    delay(1000)
    _showErrorMessage.value = false
}

    /*********ALL FUNCTIONS THAT ARE MEANT FOR NEW USERS SHOULD BE BELOW THIS LINE************/

    fun validateTokenNewUser(oAuthToken:String){
        viewModelScope.launch(Dispatchers.IO){
            setShowLogin(true)

            authentication.validateToken(oAuthToken).collect{response ->
                when (response) {
                    is NetworkNewUserResponse.Loading->{
                    }
                    is NetworkNewUserResponse.Success ->{
                        Log.d("validateOAuthTokenCall","Success")
                        //todo: might also have to do something with the validated User but We will see
                        newUserReturnHome(oAuthToken)
                    }
                    is NetworkNewUserResponse.Failure ->{
                        Log.d("validateOAuthTokenCall","Failure")
                        setResponseState("Failed. Please login again")
                    }
                    is NetworkNewUserResponse.NetworkFailure->{
                        Log.d("validateOAuthTokenCall","NetworkFailure")
                        setResponseState("Network error. Please try again later")
                    }
                    is NetworkNewUserResponse.Auth401Failure ->{
                        setResponseState("Verification failed. Please login again")
                    }
                }
            }

        }
    }
    private suspend fun newUserReturnHome(oAuthToken: String){
        tokenDataStore.setOAuthToken(oAuthToken)
        tokenDataStore.setLoggedOutStatus("FALSE")
        _newUserNavigateHome.value = true
    }

    fun setNewUserNavigateHome(value:Boolean){
        _newUserNavigateHome.value = value
    }

}