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
import com.example.clicker.network.websockets.LoggedInUserData
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
    val chatSettings: Response<ChatSettingsData> = Response.Loading,
    val loggedInUserData: LoggedInUserData? = null,
    val clientId:String = "",
    val broadcasterId: String ="",
    val userId:String ="",
    val oAuthToken:String="",
    val showChatSettingAlert:Boolean = false

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

    val listChats = mutableStateListOf<TwitchUserData>()

    private var _uiState: MutableState<StreamUIState> = mutableStateOf(StreamUIState())
    val state:State<StreamUIState> = _uiState

    val testingThings = webSocket.loggedInUserUiState
    init{
        //todo: NEED TO COPY THIS VALUE OVER TO THE loggedInUserData
        viewModelScope.launch {
            webSocket.loggedInUserUiState.collect{
                it?.let {
                    _uiState.value = _uiState.value.copy(
                        loggedInUserData = it
                    )
                    Log.d("loggedInUserUiStateViewModel","$it")
                }
            }
        }
    }

    init{
        Log.d("twitchNameonCreateViewVIewModel","CREATED")
    }
    init {
        viewModelScope.launch{
            webSocket.state.collect{twitchUserMessage ->
                    listChats.add(twitchUserMessage)

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

    fun updateChannelNameAndClientIdAndUserId(
        channelName: String,
        clientId:String,
        broadcasterId:String,
        userId:String
    ){
        _channelName.tryEmit(channelName)

        _uiState.value = _uiState.value.copy(
            clientId = clientId,
            broadcasterId = broadcasterId,
            userId = userId
        )

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
                _uiState.value = _uiState.value.copy(
                    oAuthToken = oAuthToken
                )
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


    fun slowModeChatSettings(chatSettings:ChatSettingsData) = viewModelScope.launch{
        _uiState.value = _uiState.value.copy(
            chatSettings = Response.Success(chatSettings),
            showChatSettingAlert = false
        )

        twitchRepoImpl.updateChatSettings(
            oAuthToken = _uiState.value.oAuthToken,
            clientId = _uiState.value.clientId,
            moderatorId = _uiState.value.userId,
            broadcasterId = _uiState.value.broadcasterId
        ).collect{response ->
            when(response){
                is Response.Loading ->{
                    Log.d("changeChatSettings","LOADING")
                }
                is Response.Success ->{
                    Log.d("changeChatSettings","SUCCESS")
                }
                is Response.Failure ->{
                    Log.d("changeChatSettings","FAILED -> ${response.e.message}")
                    val newChatSettingsData = ChatSettingsData(
                        slowMode = !chatSettings.slowMode,
                        broadcasterId = chatSettings.broadcasterId,
                        slowModeWaitTime = chatSettings.slowModeWaitTime,
                        followerMode = chatSettings.followerMode,
                        followerModeDuration = chatSettings.followerModeDuration,
                        subscriberMode = chatSettings.subscriberMode,
                        emoteMode = chatSettings.emoteMode,
                        uniqueChatMode = chatSettings.uniqueChatMode


                    )
                    _uiState.value = _uiState.value.copy(
                        chatSettings = Response.Success(newChatSettingsData),
                        showChatSettingAlert = true
                    )

                }
            }

        }
    }

    fun followerModeToggle(chatSettings:ChatSettingsData) = viewModelScope.launch{
        _uiState.value = _uiState.value.copy(
            chatSettings = Response.Success(chatSettings),
            showChatSettingAlert = false
        )
        twitchRepoImpl.updateChatSettings(
            oAuthToken = _uiState.value.oAuthToken,
            clientId = _uiState.value.clientId,
            moderatorId = _uiState.value.userId,
            broadcasterId = _uiState.value.broadcasterId
        ).collect{response ->
            when(response){
                is Response.Loading ->{
                    Log.d("changeChatSettings","LOADING")
                }
                is Response.Success ->{
                    Log.d("changeChatSettings","SUCCESS")
                }
                is Response.Failure ->{
                    Log.d("changeChatSettings","FAILED -> ${response.e.message}")
                    val newChatSettingsData = ChatSettingsData(
                        slowMode = chatSettings.slowMode,
                        broadcasterId = chatSettings.broadcasterId,
                        slowModeWaitTime = chatSettings.slowModeWaitTime,
                        followerMode = !chatSettings.followerMode,
                        followerModeDuration = chatSettings.followerModeDuration,
                        subscriberMode = chatSettings.subscriberMode,
                        emoteMode = chatSettings.emoteMode,
                        uniqueChatMode = chatSettings.uniqueChatMode


                    )
                    _uiState.value = _uiState.value.copy(
                        chatSettings = Response.Success(newChatSettingsData),
                        showChatSettingAlert = true
                    )

                }
            }

        }

    }

    fun subscriberModeToggle(chatSettings:ChatSettingsData) = viewModelScope.launch{
        _uiState.value = _uiState.value.copy(
            chatSettings = Response.Success(chatSettings),
            showChatSettingAlert = false
        )
        twitchRepoImpl.updateChatSettings(
            oAuthToken = _uiState.value.oAuthToken,
            clientId = _uiState.value.clientId,
            moderatorId = _uiState.value.userId,
            broadcasterId = _uiState.value.broadcasterId
        ).collect{response ->
            when(response){
                is Response.Loading ->{
                    Log.d("changeChatSettings","LOADING")
                }
                is Response.Success ->{
                    Log.d("changeChatSettings","SUCCESS")
                }
                is Response.Failure ->{
                    Log.d("changeChatSettings","FAILED -> ${response.e.message}")
                    val newChatSettingsData = ChatSettingsData(
                        slowMode = chatSettings.slowMode,
                        broadcasterId = chatSettings.broadcasterId,
                        slowModeWaitTime = chatSettings.slowModeWaitTime,
                        followerMode = chatSettings.followerMode,
                        followerModeDuration = chatSettings.followerModeDuration,
                        subscriberMode = !chatSettings.subscriberMode,
                        emoteMode = chatSettings.emoteMode,
                        uniqueChatMode = chatSettings.uniqueChatMode


                    )
                    _uiState.value = _uiState.value.copy(
                        chatSettings = Response.Success(newChatSettingsData),
                        showChatSettingAlert = true
                    )

                }
            }

        }
    }

    fun emoteModeToggle(chatSettings:ChatSettingsData) = viewModelScope.launch{
        _uiState.value = _uiState.value.copy(
            chatSettings = Response.Success(chatSettings),
            showChatSettingAlert = false
        )
        twitchRepoImpl.updateChatSettings(
            oAuthToken = _uiState.value.oAuthToken,
            clientId = _uiState.value.clientId,
            moderatorId = _uiState.value.userId,
            broadcasterId = _uiState.value.broadcasterId
        ).collect{response ->
            when(response){
                is Response.Loading ->{
                    Log.d("changeChatSettings","LOADING")
                }
                is Response.Success ->{
                    Log.d("changeChatSettings","SUCCESS")
                }
                is Response.Failure ->{
                    Log.d("changeChatSettings","FAILED -> ${response.e.message}")
                    val newChatSettingsData = ChatSettingsData(
                        slowMode = chatSettings.slowMode,
                        broadcasterId = chatSettings.broadcasterId,
                        slowModeWaitTime = chatSettings.slowModeWaitTime,
                        followerMode = chatSettings.followerMode,
                        followerModeDuration = chatSettings.followerModeDuration,
                        subscriberMode = chatSettings.subscriberMode,
                        emoteMode = !chatSettings.emoteMode,
                        uniqueChatMode = chatSettings.uniqueChatMode


                    )
                    _uiState.value = _uiState.value.copy(
                        chatSettings = Response.Success(newChatSettingsData),
                        showChatSettingAlert = true
                    )

                }
            }

        }
    }


    override fun onCleared() {
        super.onCleared()
        webSocket.close()
    }
}