package com.example.clicker.presentation.stream

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
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
    val chatSettings: Response<ChatSettingsData> = Response.Loading,
    val loggedInUserData: LoggedInUserData? = null,

    val clientId: String = "",
    val broadcasterId: String = "",
    val userId: String = "",
    val oAuthToken: String = "",

    val showChatSettingAlert: Boolean = false,

    val enableSlowMode: Boolean = true,
    val enableFollowerMode: Boolean = true,
    val enableSubscriberMode: Boolean = true,
    val enableEmoteMode: Boolean = true,

    val banDuration: Int = 0,
    val banReason: String = "",
    val timeoutDuration: Int = 10,
    val timeoutReason: String = "",
    val banResponse: Response<Boolean> = Response.Success(false),
    val banResponseMessage: String = "",
    val undoBanResponse: Boolean = false,
    val showStickyHeader: Boolean = false,

    val chatSettingsFailedMessage: String = ""
)

@HiltViewModel
class StreamViewModel @Inject constructor(
    private val webSocket: TwitchWebSocket,
    private val tokenDataStore: TwitchDataStore,
    private val twitchRepoImpl: TwitchStream,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _channelName: MutableStateFlow<String?> = MutableStateFlow(null)
    val channelName: StateFlow<String?> = _channelName

    private val _clientId: MutableState<String?> = mutableStateOf(null)
    val clientId: State<String?> = _clientId

    val listChats = mutableStateListOf<TwitchUserData>()

    private val _clickedUsername: MutableState<String> = mutableStateOf("")
    val clickedUsername: State<String> = _clickedUsername

    private val _clickedUserId: MutableState<String> = mutableStateOf("")
    val clickedUserId: State<String> = _clickedUsername

    private val _clickedUsernameBanned: MutableState<Boolean> = mutableStateOf(false)
    val clickedUsernameBanned: State<Boolean> = _clickedUsernameBanned

    private val _clickedUsernameIsMod: MutableState<Boolean> = mutableStateOf(false)
    val clickedUsernameIsMod: State<Boolean> = _clickedUsernameIsMod

    private var _uiState: MutableState<StreamUIState> = mutableStateOf(StreamUIState())
    val state: State<StreamUIState> = _uiState

    private val _modStreamList = mutableStateListOf<String?>(null)
    val exposedModList: List<String?> get() = _modStreamList

    private var currentUsername: String = ""

    val textFieldValue = mutableStateOf(
        TextFieldValue(
            text = "",
            selection = TextRange(0)
        )
    )

    val testingThings = webSocket.loggedInUserUiState

    var filteredChatList = mutableStateListOf<String>()
    val clickedUsernameChats = mutableStateListOf<String>()

    private val allChatters = mutableStateListOf<String>()

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
    fun filterMessages(messageId: String) {
        val found = listChats.first { it.id == messageId }
        val foundIndex = listChats.indexOf(found)
        listChats[foundIndex] = found.copy(
            deleted = true
        )
    }
    private fun banUserFilter(username: String, banDuration: Int?) {
        listChats.filter { it.displayName == username }.forEach {
            val index = listChats.indexOf(it)
            listChats[index] = it.copy(
                banned = true,
                bannedDuration = banDuration
            )
        }
    }

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

    fun addChatter(username: String, message: String) {
        if (!allChatters.contains(username)) {
            allChatters.add(username)
        }
    }
    fun updateClickedChat(
        clickedUsername: String,
        clickedUserId: String,
        banned: Boolean,
        isMod: Boolean
    ) {
        _clickedUsername.value = clickedUsername
        _clickedUserId.value = clickedUserId
        clickedUsernameChats.clear()
        val messages = listChats.filter { it.displayName == clickedUsername }.map { if (it.deleted) it.userType!! + " (deleted by mod)" else it.userType!! }

        clickedUsernameChats.addAll(messages)
        _clickedUsernameBanned.value = banned
        _clickedUsernameIsMod.value = isMod
    }

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

    fun autoTextChange(fullText: String, clickedText: String): String {
        val pattern = Pattern.compile("\\s|@")
        val pattern2 = Regex("@(\\s)|@")
        val lastFind = pattern2.findAll(fullText).last()

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

                    //  Log.d("loggedInUserUiStateViewModel","mod --> ${it.mod}")
                }
            }
        }
    }

    init {
        Log.d("twitchNameonCreateViewVIewModel", "CREATED")
    }

    /**
     * This is the hot state receiving the main chat messages
     * */
    init {
        viewModelScope.launch {
            // withContext(Dispatchers.IO + CoroutineName("ChatMessages")){
            webSocket.state.collect { twitchUserMessage ->
                Log.d("loggedMessage", "$twitchUserMessage")
                listChats.add(twitchUserMessage)
                if (twitchUserMessage.displayName == _clickedUsername.value) {

                    clickedUsernameChats.add(twitchUserMessage.userType!!)
                }
                if (twitchUserMessage.messageType == MessageType.CLEARCHAT && twitchUserMessage.displayName == null) {
                    listChats.clear()
                    // todo: add the ability to send a little message saying that the chat was cleard by a mod
                    val data = TwitchUserDataObjectMother
                        .addMessageType(MessageType.JOIN)
                        .addUserType("Chat cleared by moderator")
                        .addColor("#000000")
                        .build()
                    listChats.add(data)
                }
                if (twitchUserMessage.messageType == MessageType.CLEARCHAT && twitchUserMessage.displayName != null) {
                    Log.d(
                        "collectingdatathingy",
                        "foundDuration --> $twitchUserMessage.bannedDuration"
                    )
                    banUserFilter(
                        username = twitchUserMessage.displayName,
                        banDuration = twitchUserMessage.bannedDuration
                    )
                    val data = TwitchUserDataObjectMother
                        .addMessageType(MessageType.JOIN)
                        .addUserType("${twitchUserMessage.displayName} banned by moderators")
                        .addColor("#000000")
                        .build()
                    listChats.add(data)
                }
            }
            // }
        }
    }
    init {
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
        viewModelScope.launch {
            withContext(ioDispatcher + CoroutineName("RoomState")) {
                webSocket.roomState.collect { nullableRoomState ->
                    nullableRoomState?.let { roomState ->
                        // todo: update the _uiState chatSettings with these values
                        when (val response = _uiState.value.chatSettings) {
                            is Response.Success -> {
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
                            else -> {
                            }
                        }
                    }
                }
            }
        }
    }

    fun closeChatSettingAlert() {
        _uiState.value = _uiState.value.copy(
            showChatSettingAlert = false
        )
    }

    fun restartWebSocket() {
        val channelName = _channelName.value ?: ""
        startWebSocket(channelName)
    }

    private fun startWebSocket(channelName: String) = viewModelScope.launch {
        tokenDataStore.getUsername().collect { username ->
            if (username.isNotEmpty()) {
                currentUsername = username
                Log.d("startWebSocket", "username --->$username")
                webSocket.run(channelName, username)
            }
        }
    }

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

    private fun getChatSettings(
        clientId: String,
        broadcasterId: String
    ) = viewModelScope.launch {
//        tokenDataStore.getClientId().collect{
//            Log.d("twitchNameonCreateViewVIewModel","tokenDataStoreclientId ->$clientId")
//        }
        withContext(Dispatchers.IO + CoroutineName("GetChatSettings")) {
            tokenDataStore.getOAuthToken().collect { oAuthToken ->
                Log.d("getChatSettingsCalled", "clientId ->${_uiState.value.clientId}")
                Log.d("getChatSettingsCalled", "broadcasterId ->${_uiState.value.broadcasterId}")
                Log.d("getChatSettingsCalled", "oAuthToken ->$oAuthToken")
//            Log.d("twitchNameonCreateViewVIewModel","broadcasterId ->$broadcasterId")
//            Log.d("twitchNameonCreateViewVIewModel","oAuthToken ->$oAuthToken")
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
                                Log.d(
                                    "twitchNameonCreateViewVIewModel",
                                    "SUCCESS -> ${response.data.data}"
                                )
                                _uiState.value = _uiState.value.copy(
                                    chatSettings = Response.Success(response.data.data[0])
                                )
                            }
                            is Response.Failure -> {
                                Log.d(
                                    "twitchNameonCreateViewVIewModel",
                                    "FAILED -> ${response.e.message}"
                                )
                                _uiState.value = _uiState.value.copy(
//
                                    chatSettings = Response.Failure(response.e)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

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
                    is Response.Failure -> {
                        Log.d("changeChatSettings", "FAILED -> ${response.e.message}")
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
    }

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
                    is Response.Failure -> {
                        Log.d("changeChatSettings", "FAILED -> ${response.e.message}")
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
    }

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
                    is Response.Failure -> {
                        Log.d("changeChatSettings", "FAILED -> ${response.e.message}")
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
    }
    fun timeoutUser() = viewModelScope.launch {
        withContext(ioDispatcher + CoroutineName("TimeoutUser")) {
            val timeoutUser = BanUser(
                data = BanUserData(
                    user_id = _clickedUserId.value,
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

    fun banUser(banUser: BanUser) = viewModelScope.launch {
        val banUserNew = BanUser(
            data = BanUserData(
                user_id = _clickedUserId.value,
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
    fun unBanUser() = viewModelScope.launch {
        withContext(ioDispatcher + CoroutineName("UnBanUser")) {
            twitchRepoImpl.unBanUser(
                oAuthToken = _uiState.value.oAuthToken,
                clientId = _uiState.value.clientId,
                moderatorId = _uiState.value.userId,
                broadcasterId = _uiState.value.broadcasterId,
                userId = _clickedUserId.value

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
                    is Response.Failure -> {
                        Log.d("changeChatSettings", "FAILED -> ${response.e.message}")
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
    }

    override fun onCleared() {
        super.onCleared()
        webSocket.close()
    }
}
