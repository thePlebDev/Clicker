package com.example.clicker.presentation.stream

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.clicker.presentation.home.StreamInfo


class StreamViewModel(
): ViewModel() {

    private val _channelName = mutableStateOf("")
    val channelName:State<String> = _channelName

    val listChats = mutableStateListOf<String>()


    fun setChannelName(channelName:String){
        _channelName.value = channelName
        Log.d("twitchNamesetChannelName",channelName)
    }
    fun addItem(chatText:String){
        listChats.add(chatText)
    }
}