package com.example.clicker.presentation.home

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.clicker.data.TokenDataStore
import com.example.clicker.data.TokenValidationWorker
import com.example.clicker.data.workManager.OAuthTokeValidationWorker
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.AuthenticatedUser
import com.example.clicker.network.models.StreamData
import com.example.clicker.network.models.ValidatedUser
import com.example.clicker.util.Response
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkerUIState(

    val streamStatus:Response<List<StreamData>> = Response.Loading,
    val authStatus:String = "Checking if token is available"
)
@HiltViewModel
class WorkerViewModel @Inject constructor(
    private val tokenValidationWorker: TokenValidationWorker,
    private val tokenDataStore: TokenDataStore,
    private val twitchRepoImpl: TwitchRepo,
): ViewModel() {

    private var _uiState: MutableState<WorkerUIState> = mutableStateOf(WorkerUIState())
    val state: State<WorkerUIState> = _uiState


    private val _oAuthUserToken: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _AuthenticatedUser: MutableStateFlow<AuthenticatedUser?> = MutableStateFlow(null)
    var liveDataWork: LiveData<WorkInfo>? = null

    private val authenticatedUserFlow = combine(_oAuthUserToken,_AuthenticatedUser){
        _oAuthUserToken,
        authenticatedUser
        ->
        MainStates(
            oAuthToken = _oAuthUserToken,
            authUser = authenticatedUser
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily,
        MainStates(oAuthToken = null,authUser = null)
    )

    init {
        getOAuthToken()
    }
    init{
        registerSubscribers()
    }

    private fun registerSubscribers() = viewModelScope.launch{
        authenticatedUserFlow.collect{mainState ->

            mainState.oAuthToken?.let{token ->
                _uiState.value = _uiState.value.copy(
                    authStatus = "validating OAuthToken"
                )
                runWorkManager(token)
            }
            mainState.authUser?.let {authUser ->
                getLiveStreams(authUser)
            }

        }
    }

    private suspend fun getLiveStreams( authUser: AuthenticatedUser){
        twitchRepoImpl.getFollowedLiveStreams(
            authorizationToken = _oAuthUserToken.value!!,
            clientId = authUser.clientId,
            userId = authUser.userId
        ).collect{response ->
            when(response){
                is Response.Loading ->{
                    Log.d("workerGetFollowedLiveStreams","LOADING")
                }
                is Response.Success ->{
                    Log.d("workerGetFollowedLiveStreams",response.data.data.toString())
                    _uiState.value = _uiState.value.copy(
                        streamStatus = Response.Success(response.data.data),
                        authStatus = "This many streams -----> ${response.data.data.size}"
                    )
                }
                is Response.Failure ->{
                    Log.d("workerGetFollowedLiveStreams","FAILED")
                    _uiState.value = _uiState.value.copy(
                        streamStatus = Response.Failure(Exception("getting live streams failed"))
                    )
                }
            }
        }
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
                _uiState.value = _uiState.value.copy(
                    authStatus = "No OAuthToken. Please login",
                    streamStatus = Response.Failure(Exception("No token found"))
                )
            }
        }
    }
    private fun runWorkManager(oAuthToken:String){

        liveDataWork= tokenValidationWorker.enqueueRequest(oAuthToken)


    }

    fun setAuthenticatedUser(authenticatedUser: AuthenticatedUser){
        _AuthenticatedUser.tryEmit(authenticatedUser)
    }

    fun oAuthTokenValidationFailed(){
        _uiState.value = _uiState.value.copy(
            authStatus = "OAuthToken validation failed",
            streamStatus = Response.Failure(Exception("oAuthToken validation failed"))
        )
    }

    fun setOAuthToken(oAuthToken:String) = viewModelScope.launch{
        //need to make a call to exchange the authCode for a validationToken
        Log.d("setOAuthToken","token -> $oAuthToken")
        tokenDataStore.setOAuthToken(oAuthToken)
        _oAuthUserToken.tryEmit(oAuthToken)


    }

}

data class MainStates(
    val oAuthToken:String? = null,
    val authUser:AuthenticatedUser? = null,
)