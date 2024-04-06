package com.example.clicker.presentation.modView

import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.network.websockets.TwitchEventSubWebSocket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class ModViewViewModel @Inject constructor(
    private val twitchEventSubWebSocket: TwitchEventSubWebSocket
): ViewModel() {
    init{
        viewModelScope.launch {
            twitchEventSubWebSocket.newWebSocket()
            delay(5000)
            twitchEventSubWebSocket.closeWebSocket()
        }
    }
    init{
        monitorForSessionId()
    }


    private fun monitorForSessionId(){
        viewModelScope.launch {
            twitchEventSubWebSocket.parsedSessionId.collect{nullableSessionId ->
                nullableSessionId?.also {  sessionId ->
                    //then with this session Id we need to make a call to subscribe to our event
                    Log.d("monitorForSessionId","monitorForSessionId -->$sessionId")
                }
            }
        }
    }


    fun updateAutoModTokens(oAuthToken:String,clientId:String,broadcasterId:String,moderatorId:String,){
        Log.d("updateAutoModTokens","oAuthToken --> $oAuthToken")
        Log.d("updateAutoModTokens","clientId --> $clientId")
        Log.d("updateAutoModTokens","broadcasterId --> $broadcasterId")
        Log.d("updateAutoModTokens","moderatorId --> $moderatorId")

    }




}