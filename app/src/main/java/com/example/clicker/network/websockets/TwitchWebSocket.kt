package com.example.clicker.network.websockets

import android.util.Log
import com.example.clicker.data.TokenDataStore
import com.example.clicker.domain.TwitchDataStore
import com.example.clicker.network.websockets.models.LoggedInUserData
import com.example.clicker.network.websockets.models.RoomState
import com.example.clicker.network.websockets.models.TwitchUserData
import com.example.clicker.util.objectMothers.TwitchUserDataObjectMother
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

enum class MessageType {
    USER, NOTICE, USERNOTICE, ANNOUNCEMENT, RESUB, SUB, MYSTERYGIFTSUB, GIFTSUB, ERROR, JOIN, CLEARCHAT
}

class TwitchWebSocket @Inject constructor(
    private val tokenDataStore: TwitchDataStore,
    private val twitchParsingEngine: ParsingEngine
) : WebSocketListener() {

    private val webSocketScope = CoroutineScope(
        Dispatchers.Default + CoroutineName("webSocketScope")
    )

    private val initialValue = TwitchUserData(
        badgeInfo = "subscriber/77",
        badges = "subscriber/36,sub-gifter/50",
        clientNonce = "d7a543c7dc514886b439d55826eeeb5b",
        color = "FF0000",
        displayName = "verifying data",
        emotes = "",
        firstMsg = "0",
        flags = "",
        id = "fd594314-969b-4f5e-a83f-5e2f74261e6c",
        mod = "0",
        returningChatter = "0",
        roomId = "19070311",
        subscriber = true,
        tmiSentTs = 1690747946900L,
        turbo = false,
        userId = "144252234",
        userType = "Connecting to chat",
        messageType = MessageType.USER
    )

    private val webSocketURL = "wss://irc-ws.chat.twitch.tv:443"
    var streamerChannelName = ""
    private var loggedInUsername = ""

    private val _state = MutableStateFlow(initialValue)
    val state = _state.asStateFlow() // this is the text data shown to the user

    private val _loggedInUserUiState = MutableStateFlow<LoggedInUserData?>(null)
    val loggedInUserUiState = _loggedInUserUiState

    private var client: OkHttpClient = OkHttpClient.Builder().build()
    var webSocket: WebSocket? = null

    var sentMessageString: String = ""

    private val _roomState = MutableStateFlow<RoomState?>(null)
    val roomState = _roomState.asStateFlow()

    private val _messageToDeleteId: MutableStateFlow<String?> = MutableStateFlow(null)
    val messageToDeleteId = _messageToDeleteId.asStateFlow() // this is the text data shown to the user

    private val _bannedUsername: MutableStateFlow<String?> = MutableStateFlow(null)
    val bannedUsername = _bannedUsername.asStateFlow() // this is the text data shown to the user

    fun run(channelName: String?, username: String) {
        loggedInUsername = username
        if (channelName != null) {
            streamerChannelName = channelName
            if (webSocket != null) {
                close()
                newWebSocket()
            } else {
                newWebSocket()
            }
        } else {
        }
    }
    fun close() {
        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
        client.dispatcher.executorService.shutdown()
        webSocket?.close(1009, "Manually closed ")
        webSocket = null
    }
    private fun newWebSocket() {
        val request: Request = Request.Builder()
            .url(webSocketURL)
            .build()
        client = OkHttpClient.Builder()
            .readTimeout(1000, TimeUnit.MILLISECONDS)
            .writeTimeout(1000, TimeUnit.MILLISECONDS)
            .build()

        webSocket = client.newWebSocket(request, this)
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)

        // todo: I think I am going to create a custom scope tied to the lifecycle of this websocket
        openChat(webSocket)
    }

    fun openChat(webSocket: WebSocket) = GlobalScope.launch {
        webSocket.send("CAP REQ :twitch.tv/tags twitch.tv/commands")

        tokenDataStore.getOAuthToken().collect { oAuthToken ->
            Log.d("NICKUSERNAME", "state --> $loggedInUsername")
            Log.d("OAuthtokenStoof", oAuthToken)
            webSocket.send("PASS oauth:$oAuthToken")
            webSocket.send("NICK $loggedInUsername")
            webSocket.send("JOIN #$streamerChannelName")
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.d("websocketStoof", "onMessage()byte: ${bytes.hex()}")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("onMessageSocketStoof", "state --> $text")

        if (text.contains("PING")) {
            twitchParsingEngine.sendPong(webSocket)
        }

        if (text.contains(" CLEARCHAT ")) {
            val parsedTwitchUserData = twitchParsingEngine.clearChatTesting(
                text,
                streamerChannelName
            )
            _state.tryEmit(parsedTwitchUserData)
        }

        if (text.contains(" USERSTATE ")) {
            val parsedTwitchInUserData = twitchParsingEngine.userStateParsing(text)

            _loggedInUserUiState.tryEmit(
                parsedTwitchInUserData
            )
        }
        if (text.contains(" CLEARMSG ")) {
            val messageId = twitchParsingEngine.clearMsgParsing(text)
            _messageToDeleteId.tryEmit(messageId)
        }

        if (text.contains(" JOIN ")) {
            val joinObject = twitchParsingEngine.createJoinObject()
            _state.tryEmit(joinObject)
        }
        if (text.contains(" NOTICE ")) {
            Log.d("NOTICETriggered", "NOTICE --> $text")

            _state.tryEmit(
                twitchParsingEngine.noticeParsing(text, streamerChannelName)
            )
        }
        if (text.contains(" USERNOTICE ")) {
            val userStateData = twitchParsingEngine.userNoticeParsing(text, streamerChannelName)

            _state.tryEmit(userStateData)
        }

        if (text.contains("PRIVMSG")) {
            Log.d("loggedInDataOnMessage", "PRIVMSG --> $text")

            val parsedPRIVMSG = twitchParsingEngine.privateMessageParsing(text)
            _state.tryEmit(parsedPRIVMSG)
        }

        if (text.contains("ROOMSTATE")) {
            Log.d("logginTheRoomState", "Roomstate --> $text")
            val roomState = twitchParsingEngine.roomStateParsing(text)
            _roomState.tryEmit(roomState)
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(1000, null)
        println("CLOSE: $code $reason")
        Log.d("websocketStoof", "onClosing: $code $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        // t.printStackTrace()
        Log.d("websocketStooffail", "onFailure: ${t.printStackTrace()}")
        Log.d("websocketStooffail", "onFailure: ${t.message}")
        Log.d("websocketStooffail", "onFailure: ${t.cause}")

        val errorValue = TwitchUserDataObjectMother
            .addColor("#FF0000")
            .addDisplayName("Connection Error")
            .addMessageType(MessageType.ERROR)
            .addUserType(
                "Disconnected from chat. Check internet connection. Click button to attempt reconnect. If issue persists, your token may be expired and you have to logout to be issued a new one"
            )
            .build()
        _state.tryEmit(errorValue)
    }

    fun sendMessage(chatMessage: String): Boolean {
        val sendingText = webSocket?.send("PRIVMSG #$streamerChannelName :$chatMessage")
        sentMessageString = chatMessage
        // 'PRIVMSG #$channelName :$message'
        Log.d("websocketStooffail", "sendMessageResult:--> $sendingText")
        return sendingText ?: false
    }
} /*********END OF THE TwitchWebSocket CLASS*******/
