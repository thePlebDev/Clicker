package com.example.clicker.presentation.selfStreaming.viewModels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.presentation.selfStreaming.clients.RtmpClient
import com.example.clicker.presentation.selfStreaming.domain.SelfStreaming
import com.example.clicker.presentation.selfStreaming.domain.SelfStreamingSocket
import com.example.clicker.presentation.selfStreaming.websocket.RtmpsClient2
import com.example.clicker.util.NetworkAuthResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


data class OAuthClinetId(
    val oAuthToken: String,
    val clientId:String,
    val broadcasterId: String
)

@HiltViewModel
class SelfStreamingViewModel @Inject constructor(
    private val streamToTwitch: SelfStreaming,
    private val rtmpClient: RtmpClient
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
    private val _oAuthTokenClientId: MutableStateFlow<OAuthClinetId?> = MutableStateFlow(null)

    /**
     * private mutable version of [showBottomModalSheet]
     * */
    private val _showBottomModalSheet: MutableState<Boolean> = mutableStateOf(false)
    /**
     * a [State] nullable-String object used to hold the unique identifier of the Android application
     * */
    val showBottomModalSheet: State<Boolean> = _showBottomModalSheet

    fun setShowBottomModalSheet(newValue: Boolean){
        _showBottomModalSheet.value = newValue
    }

    val host = "ingest.global-contribute.live-video.net"
    val port = 443 // RTMPS secure port
    val app = "app" // RTMP application name



    init{
        viewModelScope.launch {
            val rtmpsClient = RtmpsClient2(host, port, app)
            rtmpsClient.connect()

            // After connection and handshake, you can start sending video/audio data

            // Disconnect after finishing
           // delay(10000)
            rtmpsClient.disconnect()
        }

    }



    init{
        monitorStreamKey()
    }
    init {
        monitorOAuthTokenClientId()
    }


    fun setIsStreamLive(newValue:Boolean){
        _streamIsLive.value = newValue

    }

    private fun monitorOAuthTokenClientId()=viewModelScope.launch(Dispatchers.IO){

        _oAuthTokenClientId.collect{nullableOAuthClientId ->
            nullableOAuthClientId?.let{oAuthClientId ->
                getStreamKey(
                    oAuthToken = oAuthClientId.oAuthToken,
                    clientId = oAuthClientId.clientId,
                    broadcasterId = oAuthClientId.broadcasterId
                )
            }
        }
    }

    private fun getStreamKey(
        oAuthToken: String,
        clientId: String,
        broadcasterId:String,
    ){
        Log.d("GetStreamKeyRequest","oAuthToken -->$oAuthToken")
        Log.d("GetStreamKeyRequest","clientId -->$clientId")
        Log.d("GetStreamKeyRequest","broadcasterId -->$broadcasterId")
        viewModelScope.launch(Dispatchers.IO) {
            _streamKeyResponse.value = NetworkAuthResponse.Loading


            streamToTwitch.getStreamKey(
                oAuthToken=oAuthToken,
                clientId = clientId,
                broadcasterId=broadcasterId
            ).collect{response ->
                when(response){
                    is NetworkAuthResponse.Loading ->{}
                    is NetworkAuthResponse.Success ->{
                        //this should actually update the stream key
                        _streamKeyResponse.value = response
                        val streamKey =response.data

//                        rtmps://ingest.global-contribute.live-video.net:443/app/

                        rtmpClient.connect(
                            url ="rtmps://ingest.global-contribute.live-video.net/app/$streamKey?bandwidthtest=true",
                            isRetry = false
                        )
                        Log.d("GetStreamKeyRequest","streamKey -->${response.data}")

                    }
                    is NetworkAuthResponse.Failure ->{
                        //needs to move this back to success after
                        _streamKeyResponse.value = response
                    }
                    is NetworkAuthResponse.NetworkFailure ->{
                        //needs to move this back to success after
                        _streamKeyResponse.value = response
                    }
                    is NetworkAuthResponse.Auth401Failure ->{
                        _streamKeyResponse.value = response
                        setShowBottomModalSheet(true)
                    }
            }

            }
        }
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
        oAuthToken: String,
        broadcasterId: String
    )=viewModelScope.launch(Dispatchers.IO){
        Log.d("setClientIdOAuthTokenSelfStreaming","clientId -->$clientId")
        Log.d("setClientIdOAuthTokenSelfStreaming","oAuthToken -->$oAuthToken")
        _oAuthTokenClientId.emit(
            OAuthClinetId(
                clientId=clientId,
                oAuthToken=oAuthToken,
                broadcasterId=broadcasterId
            )
        )
        //make the request to get the stream key
        }

    override fun onCleared() {
        super.onCleared()
        rtmpClient.disconnect()
    }
}