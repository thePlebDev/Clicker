package com.example.clicker.presentation.stream

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.network.websockets.TwitchUserData
import com.example.clicker.network.websockets.TwitchWebSocket
import com.example.clicker.presentation.home.StreamInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StreamViewModel @Inject constructor(
    private val webSocket: TwitchWebSocket
): ViewModel() {

    private val _channelName:MutableState<String?> = mutableStateOf(null)
    val channelName:State<String?> = _channelName

    val listChats = mutableStateListOf<TwitchUserData>()

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

    fun startWebSocket(channelName: String){
        webSocket.run(channelName)

    }


    fun addItem(chatText:String){
       // listChats.add(chatText)
    }
    fun sendMessage(chatMessage:String){
        val messageResult = webSocket.sendMessage(chatMessage)
        Log.d("messageResult",messageResult.toString())
    }

    fun updateChannelName(channelName: String){
        Log.d("twitchNameonCreateViewVIewModel",channelName)
        _channelName.value = channelName
        listChats.clear()
    }

    override fun onCleared() {
        super.onCleared()
        webSocket.close()
    }
}