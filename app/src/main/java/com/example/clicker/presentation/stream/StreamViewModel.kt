package com.example.clicker.presentation.stream

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.network.websockets.TwitchWebSocket
import com.example.clicker.presentation.home.StreamInfo
import kotlinx.coroutines.launch


class StreamViewModel(
    val webSocket: TwitchWebSocket = TwitchWebSocket()
): ViewModel() {

    private val _channelName:MutableState<String?> = mutableStateOf(null)
    val channelName:State<String?> = _channelName

    val listChats = mutableStateListOf<String>(
        "LOL GET RECKED KIK",
        "IT DO BE LIKE THAT SOMETIMES",
        "WHAT THE DOG DOING and more things that it do be like that and does like the thing")

    init{
        Log.d("twitchNameonCreateViewVIewModel","CREATED")
    }
    init {
        viewModelScope.launch{
//            webSocket.state.collect{comment ->
//                listChats.add(comment)
//
//            }
        }

    }

    fun startWebSocket(channelName: String){
        webSocket.run(channelName)

    }


    fun addItem(chatText:String){
        listChats.add(chatText)
    }

    fun updateChannelName(channelName: String){
        Log.d("twitchNameonCreateViewVIewModel",channelName)
        _channelName.value = channelName
//        listChats.clear()
    }

    override fun onCleared() {
        super.onCleared()
        webSocket.close()
    }
}