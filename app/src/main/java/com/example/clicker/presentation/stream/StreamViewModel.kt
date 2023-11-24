package com.example.clicker.presentation.stream

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.substring
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.data.TokenDataStore
import com.example.clicker.domain.TwitchDataStore
import com.example.clicker.network.BanUser
import com.example.clicker.network.BanUserData
import com.example.clicker.network.domain.TwitchStream
import com.example.clicker.network.models.ChatSettingsData
import com.example.clicker.network.models.UpdateChatSettings
import com.example.clicker.network.websockets.MessageType
import com.example.clicker.network.websockets.TwitchWebSocket
import com.example.clicker.network.websockets.domain.TwitchSocket
import com.example.clicker.network.websockets.models.LoggedInUserData
import com.example.clicker.network.websockets.models.TwitchUserData
import com.example.clicker.util.Response
import com.example.clicker.util.objectMothers.TwitchUserDataObjectMother
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.regex.Pattern
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChattingUser(
    val username: String,
    val message: String
)
data class StreamUIState(
    val chatSettings: Response<ChatSettingsData> = Response.Loading, //websocket twitchImpl
    val loggedInUserData: LoggedInUserData? = null, //websocket

    val clientId: String = "", //twitchRepoImpl
    val broadcasterId: String = "", //twitchRepoImpl
    val userId: String = "", //twitchRepoImpl
    val oAuthToken: String = "", //twitchRepoImpl

    val showChatSettingAlert: Boolean = false, //twitchRepoImpl

    val enableSlowMode: Boolean = true, //twitchRepoImpl
    val enableFollowerMode: Boolean = true, //twitchRepoImpl
    val enableSubscriberMode: Boolean = true, //twitchRepoImpl
    val enableEmoteMode: Boolean = true, //twitchRepoImpl

    val banDuration: Int = 0, //twitchRepoImpl
    val banReason: String = "", //twitchRepoImpl
    val timeoutDuration: Int = 10, //twitchRepoImpl
    val timeoutReason: String = "", //twitchRepoImpl
    val banResponse: Response<Boolean> = Response.Success(false), //twitchRepoImpl
    val banResponseMessage: String = "", //twitchRepoImpl
    val undoBanResponse: Boolean = false, //twitchRepoImpl
    val showStickyHeader: Boolean = false, //twitchRepoImpl

    val chatSettingsFailedMessage: String = ""
)
data class ClickedUIState(
    val clickedUsername:String ="", //websocket
    val clickedUserId: String ="",
    val clickedUsernameBanned: Boolean=false,
    val clickedUsernameIsMod:Boolean =false
)

@HiltViewModel
class StreamViewModel @Inject constructor(
    private val webSocket: TwitchSocket,
    private val tokenDataStore: TwitchDataStore,
    private val twitchRepoImpl: TwitchStream,
    private val ioDispatcher: CoroutineDispatcher,
    private val autoCompleteChat: AutoCompleteChat
) : ViewModel() {

    private val _channelName: MutableStateFlow<String?> = MutableStateFlow(null)
    val channelName: StateFlow<String?> = _channelName

    private val _clientId: MutableState<String?> = mutableStateOf(null)
    val clientId: State<String?> = _clientId

    val listChats = mutableStateListOf<TwitchUserData>()


    private var _uiState: MutableState<StreamUIState> = mutableStateOf(StreamUIState())
    val state: State<StreamUIState> = _uiState

    private val _clickedUIState = mutableStateOf(ClickedUIState())
    val clickedUIState = _clickedUIState



    private var currentUsername: String = ""

    val textFieldValue = mutableStateOf(
        TextFieldValue(
            text = "",
            selection = TextRange(0)
        )
    )


    var filteredChatList = mutableStateListOf<String>()
    val testingFilteredList = autoCompleteChat.filteredChatList
    val clickedUsernameChats = mutableStateListOf<String>()

    private val allChatters = mutableStateListOf<String>()

    /**THis is the data for the new filter methods*/


    private val filterMethodStartingIndex = mutableStateOf(0)

     val chatTextRange= mutableStateOf(TextRange(0))

    fun changeTextRange(currentTextRange:TextRange){
        chatTextRange.value = currentTextRange
    }





    /**THis is the data for the new filter methods*/

    init {
        viewModelScope.launch {
            withContext(ioDispatcher + CoroutineName("MessageToDeleteId")) {
                webSocket.messageToDeleteId.collect { nullableMsgId ->
                    nullableMsgId?.let { nonNullMsgId ->
                        filterMessages(nonNullMsgId)
                    }
                }
            }
        }
    }

    fun closeStickyHeader() {
        _uiState.value = _uiState.value.copy(
            showStickyHeader = false
        )
    }

    fun changeTimeoutDuration(duration: Int) {
        _uiState.value = _uiState.value.copy(
            timeoutDuration = duration
        )
    }
    fun changeTimeoutReason(reason: String) {
        _uiState.value = _uiState.value.copy(
            timeoutReason = reason
        )
    }
    fun changeBanDuration(duration: Int) {
        _uiState.value = _uiState.value.copy(
            banDuration = duration
        )
    }
    fun changeBanReason(reason: String) {
        _uiState.value = _uiState.value.copy(
            banReason = reason
        )
    }

    // TODO: NOTES FOR WHEN I COME BACK
    // this should be hooked up to a hot flow and run eachtime a new messageId is sent to it
    //todo:chat method
    fun filterMessages(messageId: String) {
        try{
            val found = listChats.first { it.id == messageId }
            val foundIndex = listChats.indexOf(found)
            listChats[foundIndex] = found.copy(
                deleted = true
            )
        }catch (e:Exception){
            Log.d("FilterMessageCrash","messageId-----> $messageId")
            Log.d("FilterMessageCrash","messageId-----> ${e.message}")
        }

    }
    //chat method
    private fun banUserFilter(username: String, banDuration: Int?) {
        listChats.filter { it.displayName == username }.forEach {
            val index = listChats.indexOf(it)
            listChats[index] = it.copy(
                banned = true,
                bannedDuration = banDuration
            )
        }
    }

    //TWITCH METHOD
    fun deleteChatMessage(messageId: String) = viewModelScope.launch {
        withContext(ioDispatcher + CoroutineName("DeleteChatMessage")) {
            twitchRepoImpl.deleteChatMessage(
                oAuthToken = _uiState.value.oAuthToken,
                clientId = _uiState.value.clientId,
                broadcasterId = _uiState.value.broadcasterId,
                moderatorId = _uiState.value.userId,
                messageId = messageId
            ).collect { response ->

                when (response) {
                    is Response.Loading -> {
                        Log.d("deleteChatMessage", "LOADING")
                    }
                    is Response.Success -> {
                        Log.d("deleteChatMessage", "SUCCESS")
                    }
                    is Response.Failure -> {
                        Log.d("deleteChatMessage", "FAILURE")
                        _uiState.value = _uiState.value.copy(
                            showStickyHeader = true,
                            undoBanResponse = false,
                            banResponseMessage = "${response.e.message}"
                        )
                    }
                }
            }
        }
    }

    //CHAT METHOD
    fun addChatter(username: String, message: String) {
        if (!allChatters.contains(username)) {
            allChatters.add(username)
        }
    }
    //CHAT METHOD
    fun updateClickedChat(
        clickedUsername: String,
        clickedUserId: String,
        banned: Boolean,
        isMod: Boolean
    ) {

        clickedUsernameChats.clear()
        val messages = listChats.filter { it.displayName == clickedUsername }.map { if (it.deleted) it.userType!! + " (deleted by mod)" else it.userType!! }

        clickedUsernameChats.addAll(messages)
        _clickedUIState.value = _clickedUIState.value.copy(
            clickedUsername = clickedUsername,
            clickedUserId = clickedUserId,
            clickedUsernameBanned = banned,
            clickedUsernameIsMod = isMod
        )

    }

    //TODO: CHAT METHOD
    var atIndex: Int? = null
    fun filterChatters(username: String, text: String) {
        Log.d("mostRecentChats", text)
        if (text.isNotBlank()) {
            // TODO: MAKE THIS A GLOBAL VARIABLE
            val lastCharacter = text[text.length - 1].toString()
//
            if (lastCharacter == " ") {
                filteredChatList.clear()
                atIndex = null
            }
//
            if (lastCharacter == "@") {
                atIndex = text.length
                filteredChatList.addAll(allChatters.toList())
            }

            if (atIndex != null && lastCharacter != "@") {
                val substring = text.subSequence(atIndex!!, text.lastIndex)

                val newList = mutableStateListOf<String>()
                newList.addAll(allChatters.filter { it.contains(substring) })
                filteredChatList.clear()
                filteredChatList.addAll(newList.toList())
            }
        } else {
            filteredChatList.clear()
        }
    }

    //TODO: CHAT METHOD
    fun autoTextChange(fullText: String, clickedText: String): String {
        val pattern = Pattern.compile("\\s|@")
        val pattern2 = Regex("@(\\s)|@")
        val lastFind = pattern2.findAll(fullText).last()
        val foundIndex = filterMethodStartingIndex.value
        //val subString = fullText.subSequence(foundIndex,clickedText.length)
        Log.d("FOUNDLASTONETest", "$foundIndex")

        val foundOne = lastFind.value
        val range = lastFind.range

        val newerString = fullText.removeRange(range)
        Log.d("FOUNDLASTONE", "$newerString")
        val newString = newerString + "@$clickedText "

        return newString
    }

    init {
        // todo: NEED TO COPY THIS VALUE OVER TO THE loggedInUserData
        viewModelScope.launch {
            webSocket.loggedInUserUiState.collect {
                it?.let {
                    _uiState.value = _uiState.value.copy(
                        loggedInUserData = it
                    )

                }
            }
        }
    }

    init {
        Log.d("twitchNameonCreateViewVIewModel", "CREATED")
    }

    /**
     * This is the hot state receiving the main chat messages
     * //TODO: SOCKET METHOD
     * */
    init {
        viewModelScope.launch {
            // withContext(Dispatchers.IO + CoroutineName("ChatMessages")){
            monitorSocketForChatMessages()
             //}
        }
    }
    init {
        //TODO: SOCKET METHOD
        viewModelScope.launch {
            withContext(ioDispatcher + CoroutineName("StartingWebSocket")) {
                _channelName.collect { channelName ->
                    channelName?.let {
                        startWebSocket(channelName)
                    }
                }
            }
        }
    }
    init {
        //TODO: SOCKET METHOD
        viewModelScope.launch {
            withContext(ioDispatcher + CoroutineName("RoomState")) {
                monitorSocketRoomState()
            }
        }
    }

    suspend fun monitorSocketForChatMessages(){
        webSocket.state.collect { twitchUserMessage ->
            Log.d("loggedMessage", "${twitchUserMessage.id}")
            listChats.add(twitchUserMessage)
            if (twitchUserMessage.displayName == _clickedUIState.value.clickedUsername) {

                clickedUsernameChats.add(twitchUserMessage.userType!!)
            }
            when(twitchUserMessage.messageType){
                MessageType.CLEARCHAT ->{
                    notifyChatOfBanTimeoutEvent(listChats,twitchUserMessage.userType)
                }
                MessageType.CLEARCHATALL->{
                    clearAllChatMessages(listChats)
                }
                MessageType.USER ->{
                    Log.d("CheckingChattersNmae","${twitchUserMessage.displayName!!}")
                    Log.d("CheckingChattersNmae","${twitchUserMessage.userType!!}")
                    autoCompleteChat.addChatter(twitchUserMessage.displayName!!)
                    addChatter(twitchUserMessage.displayName!!,twitchUserMessage.userType!!)
                }
                else -> {}
            }


            //todo:CLEAR THIS MESS OUT ABOVE
        }
    }
fun clearAllChatMessages(chatList: SnapshotStateList<TwitchUserData>){
    chatList.clear()
    val data =TwitchUserDataObjectMother
        .addMessageType(MessageType.JOIN)
        .addUserType("Chat cleared by moderator")
        .addColor("#000000")
        .build()

    chatList.add(data)
}
    fun notifyChatOfBanTimeoutEvent(chatList: SnapshotStateList<TwitchUserData>,message: String?){
        val data = TwitchUserDataObjectMother
            .addMessageType(MessageType.JOIN)
            .addUserType(message)
            .addColor("#000000")
            .build()
        chatList.add(data)
    }
    suspend fun monitorSocketRoomState(){
        webSocket.roomState.collect { nullableRoomState ->
            nullableRoomState?.let { nonNullroomState ->
                Log.d("theCurrentRoomState","$nonNullroomState")
                // todo: update the _uiState chatSettings with these values
                _uiState.value = _uiState.value.copy(
                    chatSettings = Response.Success(
                        ChatSettingsData(
                            slowMode = nonNullroomState.slowMode,
                            slowModeWaitTime = nonNullroomState.slowModeDuration,
                            followerMode = nonNullroomState.followerMode,
                            followerModeDuration = nonNullroomState.followerModeDuration,
                            subscriberMode = nonNullroomState.subMode,
                            emoteMode = nonNullroomState.emoteMode,

                        )
                    )

                )

            }
        }

    }

    //TODO: CHAT METHOD
    fun closeChatSettingAlert() {
        _uiState.value = _uiState.value.copy(
            showChatSettingAlert = false
        )
    }

    //TODO: SOCKET METHOD
    fun restartWebSocket() {
        val channelName = _channelName.value ?: ""
        startWebSocket(channelName)
    }

    //TODO: SOCKET METHOD
    private fun startWebSocket(channelName: String) = viewModelScope.launch {
        tokenDataStore.getUsername().collect { username ->
            if (username.isNotEmpty()) {
                currentUsername = username
                Log.d("startWebSocket", "username --->$username")
                webSocket.run(channelName, username)
            }
        }
    }

    //TODO: SOCKET METHOD
    fun sendMessage(chatMessage: String) {
        val messageResult = webSocket.sendMessage(chatMessage)
        textFieldValue.value = TextFieldValue(
            text = "",
            selection = TextRange(0)
        )
        listChats.add(
            TwitchUserData(
                badgeInfo = null,
                badges = null,
                clientNonce = null,
                color = "#BF40BF",
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
        Log.d("messageResult", messageResult.toString())
    }

    fun updateChannelNameAndClientIdAndUserId(
        channelName: String,
        clientId: String,
        broadcasterId: String,
        userId: String
    ) {
        _channelName.tryEmit(channelName)

        _uiState.value = _uiState.value.copy(
            clientId = clientId,
            broadcasterId = broadcasterId,
            userId = userId
        )

        getChatSettings(clientId, broadcasterId)
        listChats.clear()
    }
    fun retryGettingChatSetting() {
        getChatSettings(
            clientId = _uiState.value.clientId,
            broadcasterId = _uiState.value.broadcasterId
        )
    }

    //TODO: TWITCH METHOD
    private fun getChatSettings(
        clientId: String,
        broadcasterId: String
    ) = viewModelScope.launch {

        withContext(Dispatchers.IO + CoroutineName("GetChatSettings")) {
            tokenDataStore.getOAuthToken().collect { oAuthToken ->

                if (oAuthToken.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        oAuthToken = oAuthToken
                    )
                    twitchRepoImpl.getChatSettings("Bearer $oAuthToken", clientId, broadcasterId).collect { response ->
                        when (response) {
                            is Response.Loading -> {
                                _uiState.value = _uiState.value.copy(
                                    chatSettings = Response.Loading
                                )
                            }
                            is Response.Success -> {

                                _uiState.value = _uiState.value.copy(
                                    chatSettings = Response.Success(response.data.data[0])
                                )
                            }
                            is Response.Failure -> {
                                _uiState.value = _uiState.value.copy(
                                    chatSettings = Response.Failure(response.e)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    //TODO: TWITCH METHOD
    fun slowModeChatSettings(chatSettings: ChatSettingsData) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(
            showChatSettingAlert = false,
            enableSlowMode = false
        )
        withContext(ioDispatcher + CoroutineName("SlowModeChatSettings")) {
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
            ).collect { response ->
                when (response) {
                    is Response.Loading -> {
                        Log.d("changeChatSettings", "LOADING")
                    }
                    is Response.Success -> {
                        val newChatSettingsData = ChatSettingsData(
                            slowMode = chatSettings.slowMode,
                            slowModeWaitTime = chatSettings.slowModeWaitTime,
                            followerMode = chatSettings.followerMode,
                            followerModeDuration = chatSettings.followerModeDuration,
                            subscriberMode = chatSettings.subscriberMode,
                            emoteMode = chatSettings.emoteMode,


                        )
                        _uiState.value = _uiState.value.copy(
                            chatSettings = Response.Success(newChatSettingsData),
                            enableSlowMode = true
                        )
                    }
                    is Response.Failure -> {
                        Log.d("changeChatSettings", "FAILED -> ${response.e.message}")
                        val newChatSettingsData = ChatSettingsData(
                            slowMode = !chatSettings.slowMode,
                            slowModeWaitTime = chatSettings.slowModeWaitTime,
                            followerMode = chatSettings.followerMode,
                            followerModeDuration = chatSettings.followerModeDuration,
                            subscriberMode = chatSettings.subscriberMode,
                            emoteMode = chatSettings.emoteMode,

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
    }

    //TODO: TWICH METHOD
    fun followerModeToggle(chatSettings: ChatSettingsData) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(
            showChatSettingAlert = false,
            enableFollowerMode = false
        )
        withContext(ioDispatcher + CoroutineName("FollowerModeToggle")) {
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

            ).collect { response ->
                when (response) {
                    is Response.Loading -> {
                        Log.d("changeChatSettings", "LOADING")
                    }
                    is Response.Success -> {
                        Log.d("changeChatSettings", "SUCCESS")
                        val newChatSettingsData = ChatSettingsData(
                            slowMode = chatSettings.slowMode,
                            slowModeWaitTime = chatSettings.slowModeWaitTime,
                            followerMode = chatSettings.followerMode,
                            followerModeDuration = chatSettings.followerModeDuration,
                            subscriberMode = chatSettings.subscriberMode,
                            emoteMode = chatSettings.emoteMode,

                        )
                        _uiState.value = _uiState.value.copy(
                            chatSettings = Response.Success(newChatSettingsData),
                            enableFollowerMode = true
                        )
                    }
                    is Response.Failure -> {
                        Log.d("changeChatSettings", "FAILED -> ${response.e.message}")
                        val newChatSettingsData = ChatSettingsData(
                            slowMode = chatSettings.slowMode,
                            slowModeWaitTime = chatSettings.slowModeWaitTime,
                            followerMode = !chatSettings.followerMode,
                            followerModeDuration = chatSettings.followerModeDuration,
                            subscriberMode = chatSettings.subscriberMode,
                            emoteMode = chatSettings.emoteMode,

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
    }

    //TODO: TWICH METHOD
    fun subscriberModeToggle(chatSettings: ChatSettingsData) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(
            showChatSettingAlert = false,
            enableSubscriberMode = false
        )
        withContext(ioDispatcher + CoroutineName("SubscriberModeToggle")) {
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
            ).collect { response ->
                when (response) {
                    is Response.Loading -> {
                        Log.d("changeChatSettings", "LOADING")
                    }
                    is Response.Success -> {
                        val newChatSettingsData = ChatSettingsData(
                            slowMode = chatSettings.slowMode,
                            slowModeWaitTime = chatSettings.slowModeWaitTime,
                            followerMode = chatSettings.followerMode,
                            followerModeDuration = chatSettings.followerModeDuration,
                            subscriberMode = chatSettings.subscriberMode,
                            emoteMode = chatSettings.emoteMode,

                        )
                        _uiState.value = _uiState.value.copy(
                            chatSettings = Response.Success(newChatSettingsData),
                            enableSubscriberMode = true
                        )
                    }
                    is Response.Failure -> {
                        Log.d("changeChatSettings", "FAILED -> ${response.e.message}")
                        val newChatSettingsData = ChatSettingsData(
                            slowMode = chatSettings.slowMode,
                            slowModeWaitTime = chatSettings.slowModeWaitTime,
                            followerMode = chatSettings.followerMode,
                            followerModeDuration = chatSettings.followerModeDuration,
                            subscriberMode = !chatSettings.subscriberMode,
                            emoteMode = chatSettings.emoteMode,

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
    }
    //TODO: TWICH METHOD

    fun oneClickTimeoutUser(userId:String) = viewModelScope.launch{
        withContext(ioDispatcher + CoroutineName("TimeoutUser")) {
            val timeoutUser = BanUser(
                data = BanUserData( //TODO: THIS DATA SHOULD BE PASSED INTO THE METHOD
                    user_id = userId,
                    reason = "",
                    duration = 600
                )
            )
            twitchRepoImpl.banUser(
                oAuthToken = _uiState.value.oAuthToken,
                clientId = _uiState.value.clientId,
                moderatorId = _uiState.value.userId,
                broadcasterId = _uiState.value.broadcasterId,
                body = timeoutUser
            ).collect { response ->
                when (response) {
                    is Response.Loading -> {
                        Log.d("TIMEOUTUSERRESPONSE", "LOADING")
                    }
                    is Response.Success -> {
                        Log.d("TIMEOUTUSERRESPONSE", "SUCCESS")
                        _uiState.value = _uiState.value.copy(
                            banResponse = Response.Success(true),
                            timeoutReason = "",
                            undoBanResponse = false
                        )
                    }
                    is Response.Failure -> {
                        Log.d("TIMEOUTUSERRESPONSE", "FAILED")
                        _uiState.value = _uiState.value.copy(
                            showStickyHeader = true,
                            undoBanResponse = false,
                            banResponseMessage = "${response.e.message}"
                        )
                    }
                }
            }
        }

    }
    fun timeoutUser() = viewModelScope.launch {
        withContext(ioDispatcher + CoroutineName("TimeoutUser")) {
            val timeoutUser = BanUser(
                data = BanUserData( //TODO: THIS DATA SHOULD BE PASSED INTO THE METHOD
                    user_id = _clickedUIState.value.clickedUserId,
                    reason = _uiState.value.timeoutReason,
                    duration = _uiState.value.timeoutDuration
                )
            )
            twitchRepoImpl.banUser(
                oAuthToken = _uiState.value.oAuthToken,
                clientId = _uiState.value.clientId,
                moderatorId = _uiState.value.userId,
                broadcasterId = _uiState.value.broadcasterId,
                body = timeoutUser
            ).collect { response ->
                when (response) {
                    is Response.Loading -> {
                        Log.d("TIMEOUTUSERRESPONSE", "LOADING")
                    }
                    is Response.Success -> {
                        Log.d("TIMEOUTUSERRESPONSE", "SUCCESS")
                        _uiState.value = _uiState.value.copy(
                            banResponse = Response.Success(true),
                            timeoutReason = "",
                            undoBanResponse = false
                        )
                    }
                    is Response.Failure -> {
                        Log.d("TIMEOUTUSERRESPONSE", "FAILED")
                        _uiState.value = _uiState.value.copy(
                            showStickyHeader = true,
                            undoBanResponse = false,
                            banResponseMessage = "${response.e.message}"
                        )
                    }
                }
            }
        }
    }

    //TODO: TWICH METHOD
    fun banUser(banUser: BanUser) = viewModelScope.launch {
        val banUserNew = BanUser(
            data = BanUserData( //TODO:SHOULD BE PASSED IN
                user_id =_clickedUIState.value.clickedUserId,
                reason = banUser.data.reason,
                duration = _uiState.value.banDuration

            )
        )
        Log.d("deleteChatMessageException", "banUser.user_id ${banUserNew.data.user_id}")
        // Log.d("deleteChatMessageException", "clickedUserId ${clickedUserId}")
        withContext(ioDispatcher + CoroutineName("BanUser")) {
            twitchRepoImpl.banUser(
                oAuthToken = _uiState.value.oAuthToken,
                clientId = _uiState.value.clientId,
                moderatorId = _uiState.value.userId,
                broadcasterId = _uiState.value.broadcasterId,
                body = banUserNew
            ).collect { response ->
                when (response) {
                    is Response.Loading -> {
                        Log.d("BANUSERRESPONSE", "LOADING")
                    }
                    is Response.Success -> {
                        Log.d("BANUSERRESPONSE", "SUCCESS")
                        _uiState.value = _uiState.value.copy(
                            banResponse = Response.Success(true),
                            banReason = "",
                            undoBanResponse = false
                        )
                    }
                    is Response.Failure -> {
                        Log.d("BANUSERRESPONSE", "FAILED")
                        _uiState.value = _uiState.value.copy(
                            showStickyHeader = true,
                            undoBanResponse = false,
                            banResponseMessage = "ban attempt unsuccessful"
                        )
                    }
                }
            }
        }
    }
    //TODO: TWICH METHOD
    fun unBanUser() = viewModelScope.launch {
        withContext(ioDispatcher + CoroutineName("UnBanUser")) {
            twitchRepoImpl.unBanUser(
                oAuthToken = _uiState.value.oAuthToken,
                clientId = _uiState.value.clientId,
                moderatorId = _uiState.value.userId,
                broadcasterId = _uiState.value.broadcasterId,
                userId = _clickedUIState.value.clickedUserId //TODO:PASS IT IN

            ).collect { response ->
                when (response) {
                    is Response.Loading -> {
                        Log.d("TESTINGTHEUNBANRESPONSE", "LOADING")
                    }
                    is Response.Success -> {
                        _uiState.value = _uiState.value.copy(
                            banResponse = Response.Success(true),
                            undoBanResponse = true
                        )
                        Log.d("TESTINGTHEUNBANRESPONSE", "SUCCESS")
                    }
                    is Response.Failure -> {
                        Log.d("TESTINGTHEUNBANRESPONSE", "FAILED")
                        _uiState.value = _uiState.value.copy(
                            showStickyHeader = true,
                            undoBanResponse = false,
                            banResponseMessage = "Fail. User may be unbanned"
                        )
                    }
                }
            }
        }
    }
    fun removeUnBanButton() {
        _uiState.value = _uiState.value.copy(
            banResponse = Response.Success(true),
            undoBanResponse = true
        )
    }

    //TODO: TWICH METHOD
    fun emoteModeToggle(chatSettings: ChatSettingsData) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(
            showChatSettingAlert = false,
            enableEmoteMode = false
        )
        withContext(ioDispatcher + CoroutineName("EmoteModeToggle")) {
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
            ).collect { response ->
                when (response) {
                    is Response.Loading -> {
                        Log.d("changeChatSettings", "LOADING")
                    }
                    is Response.Success -> {
                        val newChatSettingsData = ChatSettingsData(
                            slowMode = chatSettings.slowMode,
                            slowModeWaitTime = chatSettings.slowModeWaitTime,
                            followerMode = chatSettings.followerMode,
                            followerModeDuration = chatSettings.followerModeDuration,
                            subscriberMode = chatSettings.subscriberMode,
                            emoteMode = chatSettings.emoteMode,


                        )
                        _uiState.value = _uiState.value.copy(
                            chatSettings = Response.Success(newChatSettingsData),
                            enableEmoteMode = true
                        )
                    }
                    is Response.Failure -> {
                        Log.d("changeChatSettings", "FAILED -> ${response.e.message}")
                        val newChatSettingsData = ChatSettingsData(
                            slowMode = chatSettings.slowMode,
                            slowModeWaitTime = chatSettings.slowModeWaitTime,
                            followerMode = chatSettings.followerMode,
                            followerModeDuration = chatSettings.followerModeDuration,
                            subscriberMode = chatSettings.subscriberMode,
                            emoteMode = !chatSettings.emoteMode,

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
    }

    override fun onCleared() {
        super.onCleared()
        webSocket.close()
    }




    fun filterMethodBetter(text:String,currentCharacter:Char,currentIndex:Int){

        filterAgain(text,currentCharacter.toString(),currentIndex)


    }
    fun filterAgain(text: String,currentCharacter:String,currentIndex: Int){
        try{
            if(text.isEmpty()){
                autoCompleteChat.setShowFilteredUsernames(true)
            }
            val subString:CharSequence
            if(currentCharacter == "@"){

                autoCompleteChat.setShowFilteredUsernames(true)

                filterMethodStartingIndex.value = currentIndex


            }

            if(autoCompleteChat.showFilteredUsernames.value  == true){

                subString = text.subSequence(filterMethodStartingIndex.value,currentIndex).removePrefix("@")


                Log.d("filterIndexingText","substring ----> $subString")
                val filteredList = allChatters.filter { item -> item.startsWith(subString,true) }
                val newList = mutableStateListOf<String>()
                newList.addAll(filteredList)
                filteredChatList.clear()
                autoCompleteChat.clearFilteredChatList()
                autoCompleteChat.addAllToFilteredChatList(newList.toList())
                filteredChatList.addAll(newList.toList())
//            Log.d("filterIndexingText","allChatters ----> ${allChatters.toList()}")
                Log.d("filterIndexingText","filteredList ----> $filteredList")
            }

            if(currentCharacter == " "){
                filteredChatList.clear()
                autoCompleteChat.setShowFilteredUsernames(false)
            }
        }catch (e:Exception){
            filteredChatList.clear()
            autoCompleteChat.setShowFilteredUsernames(false)
        }





    }

}


