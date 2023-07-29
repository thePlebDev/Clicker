package com.example.clicker.presentation.home

import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.data.TokenDataStore
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.AuthenticatedUser
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

data class DataStoreUIState(
    val width:Int =0,
    val aspectHeight:Int =0
)
@HiltViewModel
class DataStoreViewModel @Inject constructor(
    private val twitchRepoImpl: TwitchRepo,
    private val tokenDataStore:TokenDataStore
): ViewModel() {


    private var _uiState: MutableState<DataStoreUIState> = mutableStateOf(DataStoreUIState())
    val state:State<DataStoreUIState> = _uiState

    private val _urlList = mutableStateListOf<StreamInfo>()
    val urlList: List<StreamInfo> = _urlList



    private val _oAuthUserToken:MutableStateFlow<String?> = MutableStateFlow(null)
    private val _clientId: MutableStateFlow<AuthenticatedUser?> = MutableStateFlow(null)

    private val _showLogin:MutableState<Response<Boolean>> = mutableStateOf(Response.Loading)
    val showLogin:State<Response<Boolean>> = _showLogin


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
            mainState.hasClientId?.let{authenticatedUser ->
                Log.d("validateOAuthUserTokens", "AuthUser -> $authenticatedUser")
                getFollowedStreams(
                    oAuthUserToken = _oAuthUserToken.value!!,
                    authUser = authenticatedUser

                )

                // get the streams
            }
        }
    }


    private fun getFollowedStreams(oAuthUserToken: String,authUser:AuthenticatedUser) = viewModelScope.launch{
        twitchRepoImpl.getFollowedLiveStreams(
            authorizationToken = oAuthUserToken,
            clientId = authUser.clientId,
            userId = authUser.userId
        ).collect{response ->
            when(response){
                is Response.Loading ->{
                    Log.d("validateOAuthUserTokens", "getFollowedStreams -> LOADING")
                }
                is Response.Success ->{
                    Log.d("validateOAuthUserTokens", "getFollowedStreams -> SUCCESS")
                    _showLogin.value = Response.Success(true)
                    response.data.data.forEach {item ->

                         val newUrl = item.thumbNailUrl
                             .replace("{width}","${_uiState.value.width}")
                             .replace("{height}","${_uiState.value.aspectHeight}")
                        _urlList.add(
                            StreamInfo(
                                streamerName = item.userName,
                                streamTitle = item.title,
                                gameTitle = item.gameName,
                                views = item.viewerCount,
                                url = newUrl
                            )

                        )

                    }


                }
                is Response.Failure ->{
                    Log.d("validateOAuthUserTokens", "getFollowedStreams -> FAILURE")
                }
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
                    val userId =response.data.userId
                    val clientId = response.data.clientId
                    val authenticatedUser = AuthenticatedUser(clientId = clientId,userId = userId)
                    _clientId.tryEmit(authenticatedUser)

                }
                is Response.Failure ->{
                    Log.d("validateOAuthUserToken", "FAILURE")
                    _showLogin.value = Response.Failure(Exception("NO OAuthToken"))

                }

                else -> {}
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
            }else{
                _showLogin.value = Response.Failure(Exception("NO OAuthToken"))
            }
        }
    }

    fun updateAspectWidthHeight(width:Int, aspectHeight: Int){
        _uiState.value = _uiState.value.copy(
            aspectHeight = aspectHeight,
            width = width
        )

    }


}
data class MainState(
    val hasOAuthToken:String? = null,
    val hasClientId:AuthenticatedUser? = null,
)
data class StreamInfo(
    val streamerName:String,
    val streamTitle: String,
    val gameTitle:String,
    val views:Int,
    val url:String
)