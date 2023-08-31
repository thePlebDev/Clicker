package com.example.clicker.presentation.stream

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.data.TokenDataStore
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.ChatSettings
import com.example.clicker.network.models.ChatSettingsData
import com.example.clicker.network.models.UpdateChatSettings
import com.example.clicker.network.websockets.LoggedInUserData
import com.example.clicker.network.websockets.MessageType
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
import java.util.regex.Pattern
import javax.inject.Inject

data class ChattingUser(
    val username:String,
    val message:String
)
data class StreamUIState(
    val chatSettings: Response<ChatSettingsData> = Response.Loading,
    val loggedInUserData: LoggedInUserData? = null,
    val clientId:String = "",
    val broadcasterId: String ="",
    val userId:String ="",
    val oAuthToken:String="",
    val showChatSettingAlert:Boolean = false,

    val enableSlowMode:Boolean = true,
    val enableFollowerMode:Boolean = true,
    val enableSubscriberMode:Boolean = true,
    val enableEmoteMode:Boolean = true,

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


    private val _clickedUsername:MutableState<String> = mutableStateOf("")
    val clickedUsername:State<String> = _clickedUsername


    private var _uiState: MutableState<StreamUIState> = mutableStateOf(StreamUIState())
    val state:State<StreamUIState> = _uiState

    private var currentUsername:String = ""

    val textFieldValue = mutableStateOf(
            TextFieldValue(
                text = "",
                selection = TextRange(0)
            )
        )


    val testingThings = webSocket.loggedInUserUiState

    var filteredChatList = mutableStateListOf<String>(

    )
    val clickedUsernameChats = mutableStateListOf<String>()

    private val allChatters = mutableStateListOf<String>()



    fun addChatter(username:String, message:String){
        if(!allChatters.contains(username)){
            allChatters.add(username)
        }
        //updateClickedUsernameChats(username,message)

    }
    fun updateClickedChat(clickedUsername:String){
        _clickedUsername.value = clickedUsername
        clickedUsernameChats.clear()
        val messages = listChats.filter { it.displayName == clickedUsername }.map { it.userType!! }

        clickedUsernameChats.addAll(messages)

    }
    private fun updateClickedUsernameChats(username:String, message:String){
        //clickedUsernameChats.clear()
        if(username == _clickedUsername.value){
            Log.d("mostRecentChats",message)


            //clickedUsernameChats.add(message)
        }
    }



    var atIndex:Int? = null
    fun filterChatters(username:String,text:String){
        Log.d("mostRecentChats",text)
        if(text.isNotBlank()){
             //TODO: MAKE THIS A GLOBAL VARIABLE
            val lastCharacter = text[text.length - 1].toString()
//
            if(lastCharacter == " "){
                filteredChatList.clear()
                atIndex = null
            }
//
            if(lastCharacter == "@"){
                atIndex = text.length
               filteredChatList.addAll(allChatters.toList())
            }

            if(atIndex != null && lastCharacter != "@"){

                val substring = text.subSequence(atIndex!!,text.lastIndex)



                val newList = mutableStateListOf<String>()
                newList.addAll(allChatters.filter { it.contains(substring) })
                filteredChatList.clear()
                filteredChatList.addAll(newList.toList())

            }

        }else{
            filteredChatList.clear()
        }



    }

    fun autoTextChange(fullText:String,clickedText:String):String{
        val pattern = Pattern.compile("\\s|@")
        val pattern2 = Regex("@(\\s)|@")
        val lastFind = pattern2.findAll(fullText).last()

        val foundOne = lastFind.value
        val range = lastFind.range



        val newerString = fullText.removeRange(range)
        Log.d("FOUNDLASTONE","$newerString")
        val newString =newerString + "@$clickedText "

        return newString

    }


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
    init{
        viewModelScope.launch {
            webSocket.roomState.collect{nullableRoomState ->
                nullableRoomState?.let {roomState ->
                    //todo: update the _uiState chatSettings with these values
                    when(val response =_uiState.value.chatSettings){
                        is Response.Success ->{
                            val slowMode = roomState.slowMode ?: response.data.slowMode
                            val emoteMode = roomState.emoteMode ?: response.data.emoteMode
                            val followerMode = roomState.followerMode ?: response.data.followerMode
                            val subMode = roomState.subMode ?: response.data.subscriberMode

                            _uiState.value = _uiState.value.copy(
                                chatSettings = Response.Success(
                                    ChatSettingsData(
                                        broadcasterId = response.data.broadcasterId,
                                        slowMode = slowMode,
                                        slowModeWaitTime = response.data.slowModeWaitTime,
                                        followerMode = followerMode,
                                        followerModeDuration = response.data.followerModeDuration,
                                        subscriberMode = subMode,
                                        emoteMode = emoteMode,
                                        uniqueChatMode = response.data.uniqueChatMode
                                    )
                                )
                            )

                        }
                        else->{

                        }
                    }

                }

            }
        }

    }



    private fun startWebSocket(channelName: String) = viewModelScope.launch{
        tokenDataStore.getUsername().collect{username ->
            if(username.isNotEmpty()){
                currentUsername = username
                Log.d("startWebSocket","username --->$username")
                webSocket.run(channelName,username)
            }
        }


    }



    fun sendMessage(chatMessage:String){
        val messageResult = webSocket.sendMessage(chatMessage)
        listChats.add(
            TwitchUserData(
                badgeInfo = null,
                badges = null,
                clientNonce = null,
                color = "#000000",
                displayName = currentUsername,
                emotes = null,
                firstMsg = null,
                flags = null,
                id = null,
                mod = "mod",
                returningChatter = null,
                roomId = null,
                subscriber = false,
                tmiSentTs = null,
                turbo = false,
                userId = null,
                userType = chatMessage,
                messageType = MessageType.USER
            )
        )
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
            showChatSettingAlert = false,
            enableSlowMode = false
        )

        twitchRepoImpl.updateChatSettings(
            oAuthToken = _uiState.value.oAuthToken,
            clientId = _uiState.value.clientId,
            moderatorId = _uiState.value.userId,
            broadcasterId = _uiState.value.broadcasterId,
            body = UpdateChatSettings(
                emote_mode = chatSettings.emoteMode,
                follower_mode = chatSettings.followerMode,
                slow_mode = chatSettings.slowMode,
                subscriber_mode = chatSettings.subscriberMode
            )
        ).collect{response ->
            when(response){
                is Response.Loading ->{
                    Log.d("changeChatSettings","LOADING")
                }
                is Response.Success ->{
                    val newChatSettingsData = ChatSettingsData(
                        slowMode = chatSettings.slowMode,
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
                        enableSlowMode = true
                    )
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
                        showChatSettingAlert = true,
                        enableSlowMode = true
                    )

                }
            }

        }
    }

    fun followerModeToggle(chatSettings:ChatSettingsData) = viewModelScope.launch{
        _uiState.value = _uiState.value.copy(
            showChatSettingAlert = false,
            enableFollowerMode = false
        )
        twitchRepoImpl.updateChatSettings(
            oAuthToken = _uiState.value.oAuthToken,
            clientId = _uiState.value.clientId,
            moderatorId = _uiState.value.userId,
            broadcasterId = _uiState.value.broadcasterId,
            body = UpdateChatSettings(
                emote_mode = chatSettings.emoteMode,
                follower_mode = chatSettings.followerMode,
                slow_mode = chatSettings.slowMode,
                subscriber_mode = chatSettings.subscriberMode
            )

        ).collect{response ->
            when(response){
                is Response.Loading ->{
                    Log.d("changeChatSettings","LOADING")
                }
                is Response.Success ->{
                    Log.d("changeChatSettings","SUCCESS")
                    val newChatSettingsData = ChatSettingsData(
                        slowMode = chatSettings.slowMode,
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
                        enableFollowerMode = true
                    )

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
                        showChatSettingAlert = true,
                        enableFollowerMode = true
                    )

                }
            }

        }

    }

    fun subscriberModeToggle(chatSettings:ChatSettingsData) = viewModelScope.launch{
        _uiState.value = _uiState.value.copy(
            showChatSettingAlert = false,
            enableSubscriberMode = false
        )
        twitchRepoImpl.updateChatSettings(
            oAuthToken = _uiState.value.oAuthToken,
            clientId = _uiState.value.clientId,
            moderatorId = _uiState.value.userId,
            broadcasterId = _uiState.value.broadcasterId,
            body = UpdateChatSettings(
                emote_mode = chatSettings.emoteMode,
                follower_mode = chatSettings.followerMode,
                slow_mode = chatSettings.slowMode,
                subscriber_mode = chatSettings.subscriberMode
            )
        ).collect{response ->
            when(response){
                is Response.Loading ->{
                    Log.d("changeChatSettings","LOADING")
                }
                is Response.Success ->{
                    val newChatSettingsData = ChatSettingsData(
                        slowMode = chatSettings.slowMode,
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
                        enableSubscriberMode = true
                    )
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
                        showChatSettingAlert = true,
                        enableSubscriberMode = true

                    )

                }
            }

        }
    }

    fun emoteModeToggle(chatSettings:ChatSettingsData) = viewModelScope.launch{
        _uiState.value = _uiState.value.copy(
            showChatSettingAlert = false,
            enableEmoteMode = false
        )
        twitchRepoImpl.updateChatSettings(
            oAuthToken = _uiState.value.oAuthToken,
            clientId = _uiState.value.clientId,
            moderatorId = _uiState.value.userId,
            broadcasterId = _uiState.value.broadcasterId,
            body = UpdateChatSettings(
                emote_mode = chatSettings.emoteMode,
                follower_mode = chatSettings.followerMode,
                slow_mode = chatSettings.slowMode,
                subscriber_mode = chatSettings.subscriberMode
            )
        ).collect{response ->
            when(response){
                is Response.Loading ->{
                    Log.d("changeChatSettings","LOADING")
                }
                is Response.Success ->{
                    val newChatSettingsData = ChatSettingsData(
                        slowMode = chatSettings.slowMode,
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
                        enableEmoteMode = true
                    )
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
                        showChatSettingAlert = true,
                        enableEmoteMode = true
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

