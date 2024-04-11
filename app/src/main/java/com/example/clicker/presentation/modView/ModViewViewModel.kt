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
import com.example.clicker.network.clients.BlockedTerm
import com.example.clicker.network.clients.ManageAutoModMessage
import com.example.clicker.network.domain.TwitchEventSubscriptionWebSocket
import com.example.clicker.network.domain.TwitchEventSubscriptions
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.network.repository.TwitchEventSub
import com.example.clicker.network.websockets.AutoModQueueMessage
import com.example.clicker.network.websockets.TwitchEventSubWebSocket
import com.example.clicker.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

     val blockedTermsList = mutableStateListOf<BlockedTerm>()

    init{
        monitorForSessionId()
    }

    init{
        monitorForAutoModMessages()
    }
    init{
        monitorForAutoModMessageUpdates()
    }
    fun createNewTwitchEventWebSocket(){
        twitchEventSubWebSocket.newWebSocket()
    }


    /**
     * This is the function that calls createEventSubSubscription()
     * */
    private fun monitorForSessionId(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
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
    }

    private fun monitorForAutoModMessageUpdates(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                twitchEventSubWebSocket.messageIdForAutoModQueue.collect { nullableAutoModMessage ->
                    nullableAutoModMessage?.also { autoModMessage ->
                        Log.d(
                            "monitorForAutoModMessageUpdates",
                            "autoModMessage -->$autoModMessage"
                        )
                        val item =
                            autoModMessageList.find { it.messageId == autoModMessage.messageId }
                        item?.also {
                            val indexOfItem = autoModMessageList.indexOf(item)
                            autoModMessageList[indexOfItem] = item.copy(
                                approved = autoModMessage.approved,
                                swiped = true
                            )
                        }
                    }
                }
            }
        }
    }

    fun createEventSubSubscription(){
        // TODO: ON SUCCESS HAVE THIS MAKE ANOTHER SUBSCIRPTION TO THE UPDATE AUTOMOD MESSAGES
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                _uiState.value = _uiState.value.copy(
                    showSubscriptionEventError = Response.Loading
                )
                delay(200)
                twitchEventSub.createEventSubSubscription(
                    oAuthToken = _requestIds.value.oAuthToken,
                    clientId = _requestIds.value.clientId,
                    broadcasterId = _requestIds.value.broadcasterId,
                    moderatorId = _requestIds.value.moderatorId,
                    sessionId = _requestIds.value.sessionId,
                    type = "automod.message.hold"
                ).collect { response ->
                    when (response) {
                        is Response.Loading -> {}
                        is Response.Success -> {
                            _uiState.value = _uiState.value.copy(
                                showSubscriptionEventError = Response.Success(true)
                            )
                            createAnotherSubscriptionEvent()
                        }

                        is Response.Failure -> {
                            _uiState.value = _uiState.value.copy(
                                showSubscriptionEventError = Response.Failure(response.e)
                            )
                        }
                    }
                }
            }
        }
    }
    fun createAnotherSubscriptionEvent(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
            twitchEventSub.createEventSubSubscription(
                oAuthToken =_requestIds.value.oAuthToken,
                clientId =_requestIds.value.clientId,
                broadcasterId =_requestIds.value.broadcasterId,
                moderatorId =_requestIds.value.moderatorId,
                sessionId = _requestIds.value.sessionId,
                type = "automod.message.update"
            ).collect { response ->
                when (response) {
                    is Response.Loading -> {
                        Log.d("createAnotherSubscriptionEvent", "response -->LOADING")
                    }

                    is Response.Success -> {
                        Log.d("createAnotherSubscriptionEvent", "response -->SUCCESS")
                        _uiState.value = _uiState.value.copy(
                            showSubscriptionEventError = Response.Success(true)
                        )
                    }

                    is Response.Failure -> {
                        Log.d("createAnotherSubscriptionEvent", "response -->FAILED")
                        _uiState.value = _uiState.value.copy(
                            showSubscriptionEventError = Response.Failure(response.e)
                        )
                    }
                }
            }
            }
        }
    }
    fun monitorForAutoModMessages(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                twitchEventSubWebSocket.autoModMessageQueue.collect { nullableAutoModMessage ->
                    nullableAutoModMessage?.also { autoModMessage ->
                        autoModMessageList.add(autoModMessage)
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
        getBlockedTerms(
            oAuthToken=oAuthToken,
            clientId =clientId,
            broadcasterId=broadcasterId,
            moderatorId=moderatorId
        )

    }
    private fun getBlockedTerms(oAuthToken:String, clientId:String, broadcasterId:String, moderatorId:String){
        Log.d("getBlockedTerms","oAuthToken -->$oAuthToken")
        Log.d("getBlockedTerms","clientId -->$clientId")
        Log.d("getBlockedTerms","broadcasterId -->$broadcasterId")
        Log.d("getBlockedTerms","moderatorId -->$moderatorId")
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                twitchEventSub.getBlockedTerms(
                    oAuthToken=oAuthToken,
                    clientId =clientId,
                    broadcasterId=broadcasterId,
                    moderatorId=moderatorId
                ).collect{response ->
                    when(response){
                        is Response.Loading ->{
                            Log.d("getBlockedTerms","response.code --> Loading")
                        }
                        is Response.Success ->{
                            Log.d("getBlockedTerms","response.code --> Success")
                            blockedTermsList.addAll(response.data)
                        }
                        is Response.Failure ->{
                            Log.d("getBlockedTerms","response.code --> Failure")
                        }
                    }

                }
            }
        }
    }
    fun manageAutoModMessage(
        msgId:String,
        userId:String,
        action:String
    ){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
            val requestBody =ManageAutoModMessage(
                userId =_requestIds.value.moderatorId,
                msgId=msgId,
                action=action
            )

            val item =autoModMessageList.find { it.messageId == msgId}!!
            val indexOfItem = autoModMessageList.indexOf(item)

//
            twitchEventSub.manageAutoModMessage(
                oAuthToken = _requestIds.value.oAuthToken,
                clientId = _requestIds.value.clientId,
                manageAutoModMessageData = requestBody
            ).collect { response ->
                when (response) {
                    is Response.Loading -> {}
                    is Response.Success -> {

                        if (action == "ALLOW") {
                            autoModMessageList[indexOfItem] = item.copy(
                                approved = true,
                                swiped = true
                            )


                        }
                        if (action == "DENY") {
                            autoModMessageList[indexOfItem] = item.copy(
                                approved = false,
                                swiped = true
                            )
                        }

                    }

                    is Response.Failure -> {
                        autoModMessageList[indexOfItem] = item.copy(
                            swiped = false
                        )
                    }
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

