package com.example.clicker.presentation.selfStreaming.viewModels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.presentation.selfStreaming.domain.SelfStreaming
import com.example.clicker.util.NetworkAuthResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


data class oAuthClinetId(
    val oAuthToken: String,
    val clientId:String
)

@HiltViewModel
class SelfStreamingViewModel @Inject constructor(
    streamToTwitch: SelfStreaming
): ViewModel() {


    /**
     * private mutable version of [streamKeyResponse]
     * */
    private val _streamKeyResponse: MutableState<NetworkAuthResponse<String>> = mutableStateOf(NetworkAuthResponse.Loading) //this needs to be a response

    /**
     * a [StateFlow] String object used to hold the stream key of the user attempting to stream
     * - [GET stream key documentation](https://dev.twitch.tv/docs/api/reference/#get-stream-key)
     * */
    val streamKeyResponse: State<NetworkAuthResponse<String>> = _streamKeyResponse

    /**
     * private mutable version of [clientId]
     * */
    private val _streamIsLive: MutableState<Boolean> = mutableStateOf(false)
    /**
     * a [State] nullable-String object used to hold the unique identifier of the Android application
     * */
    val streamIsLive: State<Boolean> = _streamIsLive


    /**
     * a [StateFlow] oAuthClinetId object used to hold the clientId and the OAuth token
     * */
    private val _oAuthTokenClientId: MutableStateFlow<oAuthClinetId?> = MutableStateFlow(null)





    init{
        monitorStreamKey()
    }

    fun setIsStreamLive(newValue:Boolean){
        _streamIsLive.value = newValue

    }




    private fun monitorStreamKey()=viewModelScope.launch(Dispatchers.IO){
//        _streamKey.collect{nullableStreamKey->
//            nullableStreamKey?.let { streamKey ->
//
//            }
        //}
    }




    //I need to wait for the oAuthToken and the clientId
    //Once I have both I need to make a request to get the client ID

    fun setClientIdOAuthToken(
        clientId: String,
        oAuthToken: String
    )=viewModelScope.launch(Dispatchers.IO){
        Log.d("setClientIdOAuthTokenSelfStreaming","clientId -->$clientId")
        Log.d("setClientIdOAuthTokenSelfStreaming","oAuthToken -->$oAuthToken")
        _oAuthTokenClientId.emit(
            oAuthClinetId(clientId,oAuthToken)
        )
        //make the request to get the stream key
        }

}