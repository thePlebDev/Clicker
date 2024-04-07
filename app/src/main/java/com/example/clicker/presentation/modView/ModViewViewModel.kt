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
import com.example.clicker.network.domain.TwitchEventSubscriptionWebSocket
import com.example.clicker.network.domain.TwitchEventSubscriptions
import com.example.clicker.network.repository.TwitchEventSub
import com.example.clicker.network.websockets.TwitchEventSubWebSocket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RequestIds(
    val oAuthToken:String ="",
    val clientId:String="",
    val broadcasterId:String="",
    val moderatorId:String ="",
)

@HiltViewModel
class ModViewViewModel @Inject constructor(
    private val twitchEventSubWebSocket: TwitchEventSubscriptionWebSocket,
    private val twitchEventSub: TwitchEventSubscriptions
): ViewModel() {
    private var _requestIds: MutableState<RequestIds> = mutableStateOf(
        RequestIds()
    )
    init{
        viewModelScope.launch {
            twitchEventSubWebSocket.newWebSocket()
            delay(12000)
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
                    twitchEventSub.createEventSubSubscription(
                        oAuthToken =_requestIds.value.oAuthToken,
                        clientId =_requestIds.value.clientId,
                        broadcasterId =_requestIds.value.broadcasterId,
                        moderatorId =_requestIds.value.moderatorId,
                        sessionId = sessionId
                    ).collect{value ->
                        Log.d("monitorForSessionId","emittedValue -->$value")
                    }

                }
            }
        }
    }


    fun updateAutoModTokens(oAuthToken:String,clientId:String,broadcasterId:String,moderatorId:String,){
        _requestIds.value = _requestIds.value.copy(
            oAuthToken = oAuthToken,
            clientId =clientId,
            broadcasterId =broadcasterId,
            moderatorId =moderatorId

        )

    }




}