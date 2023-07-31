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

    init{
        Log.d("twitchNameonCreateViewVIewModel","CREATED")
    }


    fun addItem(chatText:String){
        listChats.add(chatText)
    }

    fun updateChannelName(channelName: String){
        Log.d("twitchNameonCreateViewVIewModel",channelName)
        _channelName.value = channelName
    }
}