package com.example.clicker.presentation.stream

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.data.TokenDataStore
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.ChatSettings
import com.example.clicker.network.models.ChatSettingsData
import com.example.clicker.network.websockets.TwitchUserData
import com.example.clicker.network.websockets.TwitchWebSocket
import com.example.clicker.presentation.home.HomeUIState
import com.example.clicker.presentation.home.StreamInfo
import com.example.clicker.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StreamUIState(
    val chatSettings: Response<ChatSettingsData> = Response.Loading
)

@HiltViewModel
class StreamViewModel @Inject constructor(
    private val webSocket: TwitchWebSocket,
    private val tokenDataStore: TokenDataStore,
    private val twitchRepoImpl: TwitchRepo,
): ViewModel() {

    private val _channelName: MutableStateFlow<String?> = MutableStateFlow(null)
    val channelName: StateFlow<String?> = _channelName

    private val _clientId:MutableState<String?> = mutableStateOf(null)
    val clientId:State<String?> = _clientId

    private val _broadCasterId:MutableState<String?> = mutableStateOf(null)
    val broadcasterIdId:State<String?> = _broadCasterId

    val listChats = mutableStateListOf<TwitchUserData>()

    private var _uiState: MutableState<StreamUIState> = mutableStateOf(StreamUIState())
    val state:State<StreamUIState> = _uiState

    init{
        Log.d("twitchNameonCreateViewVIewModel","CREATED")
    }
    init {
        viewModelScope.launch{
            webSocket.state.collect{twitchUser ->
                    listChats.add(twitchUser)

            }
        }

    }
    init{
        viewModelScope.launch {
            _channelName.collect{channelName ->
                channelName?.let{
                    startWebSocket(channelName)
                }
            }
        }
    }



    fun startWebSocket(channelName: String) = viewModelScope.launch{
        tokenDataStore.getUsername().collect{username ->
            if(username.isNotEmpty()){
//                Log.d("startWebSocket","username --->$it")
                webSocket.run(channelName,username)
            }
        }


    }


    fun addItem(chatText:String){
       // listChats.add(chatText)
    }
    fun sendMessage(chatMessage:String){
        val messageResult = webSocket.sendMessage(chatMessage)
        Log.d("messageResult",messageResult.toString())
    }

    fun updateChannelNameAndClientId(
        channelName: String,
        clientId:String,
        broadcasterId:String
    ){
        _channelName.tryEmit(channelName)

        getChatSettings(clientId, broadcasterId)
        listChats.clear()

    }

    private fun getChatSettings(
        clientId: String,
        broadcasterId: String
    ) = viewModelScope.launch{
        tokenDataStore.getOAuthToken().collect{oAuthToken ->
            Log.d("twitchNameonCreateViewVIewModel","clientId ->$clientId")
            Log.d("twitchNameonCreateViewVIewModel","broadcasterId ->$broadcasterId")
            Log.d("twitchNameonCreateViewVIewModel","oAuthToken ->$oAuthToken")
            if(oAuthToken.isNotEmpty()){
                twitchRepoImpl.getChatSettings("Bearer $oAuthToken",clientId,broadcasterId).collect{response ->
                    when(response){
                        is Response.Loading ->{
                            Log.d("twitchNameonCreateViewVIewModel","LOADING")
                        }
                        is Response.Success ->{
                            Log.d("twitchNameonCreateViewVIewModel","SUCCESS -> ${response.data.data}")
                            _uiState.value = _uiState.value.copy(
                                chatSettings = Response.Success(response.data.data[0])
                            )
                        }
                        is Response.Failure ->{
                            Log.d("twitchNameonCreateViewVIewModel","FAILED -> ${response.e.message}")
                        }
                    }
                }

            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocket.close()
    }
}