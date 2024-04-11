package com.example.clicker.presentation.modView

import android.content.res.Resources
import android.os.MessageQueue
import android.util.Log
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.network.clients.ManageAutoModMessage
import com.example.clicker.network.domain.TwitchEventSubscriptionWebSocket
import com.example.clicker.network.domain.TwitchEventSubscriptions
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.network.repository.TwitchEventSub
import com.example.clicker.network.websockets.AutoModQueueMessage
import com.example.clicker.network.websockets.TwitchEventSubWebSocket
import com.example.clicker.util.Response
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
    val sessionId:String =""
)
data class ModViewViewModelUIState(
    val showSubscriptionEventError:Response<Boolean> = Response.Loading,
    val showAutoModMessageQueueErrorMessage:Boolean = false,
)



@HiltViewModel
class ModViewViewModel @Inject constructor(
    private val twitchEventSubWebSocket: TwitchEventSubscriptionWebSocket,
    private val twitchEventSub: TwitchEventSubscriptions
): ViewModel() {
    private var _requestIds: MutableState<RequestIds> = mutableStateOf(RequestIds())

    //this is all the messages for the AutoModQueue
    val  autoModMessageList = mutableStateListOf<AutoModQueueMessage>()
    private val _uiState: MutableState<ModViewViewModelUIState> = mutableStateOf(ModViewViewModelUIState())
    val uiState: State<ModViewViewModelUIState> = _uiState


//    init{
//        //todo: THIS SHOULD BE CALLED WHEN THE USER CLICKS A STREAM PAGE, NOT IN THIS INIT FUCNTION
//        viewModelScope.launch {
//            twitchEventSubWebSocket.newWebSocket()
//
//        }
//    }
    init{
        monitorForSessionId()
    }

    init{
        monitorForAutoModMessages()

    }
    fun createNewTwitchEventWebSocket(){
        twitchEventSubWebSocket.newWebSocket()
    }


    /**
     * This is the function that calls createEventSubSubscription()
     * */
    private fun monitorForSessionId(){
        viewModelScope.launch {
            twitchEventSubWebSocket.parsedSessionId.collect{nullableSessionId ->
                nullableSessionId?.also {  sessionId ->
                    _requestIds.value = _requestIds.value.copy(
                        sessionId = sessionId
                    )
                    createEventSubSubscription()
                    //then with this session Id we need to make a call to subscribe to our event


                }
            }
        }
    }

    fun createEventSubSubscription(){
        // TODO: ON SUCCESS HAVE THIS MAKE ANOTHER SUBSCIRPTION TO THE UPDATE AUTOMOD MESSAGES
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                showSubscriptionEventError = Response.Loading
            )
            delay(200)
            twitchEventSub.createEventSubSubscription(
                oAuthToken =_requestIds.value.oAuthToken,
                clientId =_requestIds.value.clientId,
                broadcasterId =_requestIds.value.broadcasterId,
                moderatorId =_requestIds.value.moderatorId,
                sessionId = _requestIds.value.sessionId
            ).collect{response ->
                when(response){
                    is Response.Loading->{}
                    is Response.Success->{
                        _uiState.value = _uiState.value.copy(
                            showSubscriptionEventError = Response.Success(true)
                        )
                    }
                    is Response.Failure->{
                        _uiState.value = _uiState.value.copy(
                            showSubscriptionEventError = Response.Failure(response.e)
                        )
                    }
                }
            }
        }
    }
    fun monitorForAutoModMessages(){
        viewModelScope.launch {
            twitchEventSubWebSocket.autoModMessageQueue.collect{nullableAutoModMessage->
                nullableAutoModMessage?.also {autoModMessage ->
                    autoModMessageList.add(autoModMessage)
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
    fun manageAutoModMessage(
        msgId:String,
        userId:String,
        action:String
    ){
        viewModelScope.launch {
            val requestBody =ManageAutoModMessage(
                userId =_requestIds.value.moderatorId,
                msgId=msgId,
                action=action
            )

            val item =autoModMessageList.find { it.messageId == msgId}
            item?.swiped = true
//
            twitchEventSub.manageAutoModMessage(
                oAuthToken = _requestIds.value.oAuthToken,
                clientId = _requestIds.value.clientId,
                manageAutoModMessageData = requestBody
            ).collect{response ->
                when(response){
                    is Response.Loading ->{}
                    is Response.Success ->{

                        if(action == "ALLOW"){
                            item?.approved = true
                            item?.swiped = true
                        }
                        if(action == "DENY"){
                            item?.approved = false
                            item?.swiped = true
                        }

                    }
                    is Response.Failure ->{

                        item?.swiped = false
                    }
                }

            }
        }

    }

    override fun onCleared() {
        super.onCleared()
        twitchEventSubWebSocket.closeWebSocket()
        Log.d("ModViewViewModel","onCleared()")
    }




}