package com.example.clicker.authentication

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.clicker.data.TokenDataStore
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.ValidatedUser
import com.example.clicker.presentation.home.HomeUIState
import com.example.clicker.presentation.home.LoginStatus
import com.example.clicker.presentation.home.MainBusState
import com.example.clicker.util.Response
import com.example.clicker.util.logCoroutineInfo
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TwitchAuthentication constructor(
    private val twitchRepoImpl: TwitchRepo,
    private val tokenDataStore: TokenDataStore,
    private val scope: CoroutineScope,
    private val _uiState: MutableState<HomeUIState>,
    private val _loginUIState: MutableState<LoginStatus>
) {


    init {
        //checks to see if the current device has a token stored
        getOAuthToken()
    }

    //1)CREATE THE AUTHENTICATION STATE
        private val authenticatedUserFlow = combine(
        flow = MutableStateFlow<String?>(null),
        flow2 =MutableStateFlow<ValidatedUser?>(null)
    ){
        oAuthToken,validatedUser ->
        MainBusState(oAuthToken,validatedUser)

    }.stateIn(scope, SharingStarted.Lazily,
        MainBusState(oAuthToken = null, authUser = null)
    )

     val mutableAuthenticatedUserFlow = MutableStateFlow(authenticatedUserFlow.value)

    //2) implement the methods
    /**
     * This is the first function to run during the login sequence
     *
     * upon a successful retrieval of a stored OAuth token. It is emitted to mutableAuthenticatedUserFlow as oAuthToken
     *
     * upon a failed retrieval of a stored OAuth token. _loginUIState is updated accordingly
     * */
    private fun getOAuthToken() = scope.launch{
        tokenDataStore.getOAuthToken().collect{storedOAuthToken ->

            if(storedOAuthToken.length > 2){


                mutableAuthenticatedUserFlow.tryEmit(
                    mutableAuthenticatedUserFlow.value.copy(
                        oAuthToken = storedOAuthToken
                    )
                )
            }else{

                _loginUIState.value = _loginUIState.value.copy(
                    loginStatusText ="Please login with Twitch",
                    loginStep1 = Response.Failure(Exception("No authentication token found"))
                )
            }
        }
    }

    /**
     * The second method to be called in the authentication flow.
     * This function is used to make a request to Twitch's API and validate the oAuthenticationToken
     * */
    fun validateOAuthToken(
        oAuthenticationToken:String,
    ) =scope.launch{
        withContext( Dispatchers.IO + CoroutineName("TokenValidator")){
            twitchRepoImpl.validateToken(oAuthenticationToken).collect{response ->
                Log.d("VALIDATINGTOKEN","TOKEN ---> VALIDATING.....")

                when(response){
                    is Response.Loading ->{}
                    is Response.Success ->{
                        logCoroutineInfo("CoroutineDebugging","GOT ITEMS from remote")
                        Log.d("VALIDATINGTOKEN","TOKEN ---> SUCCESS.....")

                        _uiState.value = _uiState.value.copy(
                            authenticationCode =oAuthenticationToken
                        )
                        mutableAuthenticatedUserFlow.tryEmit(
                            mutableAuthenticatedUserFlow.value.copy(
                                authUser = response.data
                            )
                        )
                        tokenDataStore.setUsername(response.data.login)
                    }
                    is Response.Failure ->{
                        Log.d("VALIDATINGTOKEN","TOKEN ---> FAILED.....")

                        _loginUIState.value = _loginUIState.value.copy(
                            loginStatusText="Failed to validate token. Please login again",
                            loginStep1 = Response.Failure(Exception("failed to validate Token. Please login again"))
                        )
                    }
                }
            }


        }


    }
    // BEGIN LOGOUT STAGE
    suspend fun beginLogout(){
        _loginUIState.value = _loginUIState.value.copy(
            showLoginModal = true,
            loginStep1 = Response.Loading,
            loginStatusText = "Logging out",

            )
        withContext( Dispatchers.IO +CoroutineName("BeginLogout")){
            twitchRepoImpl.logout(
                clientId = mutableAuthenticatedUserFlow.value.authUser?.clientId!!,
                token = mutableAuthenticatedUserFlow.value.oAuthToken!!)
                .collect{response ->
                    // Log.d("logoutResponse", "beginLogoutCollecting ->${it}")
                    when(response){
                        is Response.Loading ->{}
                        is Response.Success ->{
                            _loginUIState.value = _loginUIState.value.copy(
                                loginStatusText = "Success! Please log in with Twitch",
                                showLoginModal = true,
                                logoutError = false,
                                loginStep1 = Response.Failure(Exception("Please login again")),
                            )
                        }
                        is Response.Failure ->{
                            _loginUIState.value = _loginUIState.value.copy(
                                loginStatusText = "Logout Error! Please try again",
                                showLoginModal = true,
                                loginStep1 = Response.Failure(Exception("Error Logging out")),
                                logoutError = true
                            )
                        }
                        else -> {}
                    }
                }

        }


    }

    /**
     * Below could be possibly moved
     */
    suspend fun setOAuthToken(oAuthToken:String){
        //need to make a call to exchange the authCode for a validationToken
        Log.d("setOAuthToken","token -> $oAuthToken")
        tokenDataStore.setOAuthToken(oAuthToken)
        mutableAuthenticatedUserFlow.tryEmit(
            mutableAuthenticatedUserFlow.value.copy(
                oAuthToken = oAuthToken
            )
        )

    }


}