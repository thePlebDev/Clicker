package com.example.clicker.presentation.modView

import android.content.res.Resources
import android.os.MessageQueue
import android.util.Log
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.R
import com.example.clicker.network.clients.BlockedTerm
import com.example.clicker.network.clients.ManageAutoModMessage
import com.example.clicker.network.domain.TwitchEventSubscriptionWebSocket
import com.example.clicker.network.domain.TwitchEventSubscriptions
import com.example.clicker.network.models.twitchStream.ChatSettings
import com.example.clicker.network.models.twitchStream.ChatSettingsData
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.network.repository.TwitchEventSub
import com.example.clicker.network.websockets.AutoModQueueMessage
import com.example.clicker.network.websockets.TwitchEventSubWebSocket
import com.example.clicker.presentation.stream.StreamUIState
import com.example.clicker.util.Response
import com.example.clicker.util.WebSocketResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Flow.Subscriber
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
    val chatSettings: ChatSettingsData = ChatSettingsData(false,null,false,null,false,false),
    val enabledChatSettings:Boolean = true,
    val selectedSlowMode:ListTitleValue =ListTitleValue("Off",null),
    val selectedFollowerMode:ListTitleValue =ListTitleValue("Off",null),
    val autoModQuePedingMessages:Int =0,
    val emoteOnly:Boolean = false,
    val subscriberOnly:Boolean = false,

)
data class ListTitleValue(
    val title:String,
    val value:Int?
)

data class ModViewStatus(
    val modActions:WebSocketResponse<Boolean> = WebSocketResponse.Loading
)

/**
 * - ModActionData represents an individual event sent by the Twitch servers when a moderator takes action inside of the chat
 * - You can read more about the moderation action [HERE](https://dev.twitch.tv/docs/eventsub/eventsub-subscription-types/#channelmoderate)
 *
 * @param title a String that represents the main information shown to the user when a moderation action takes place. This should be as short as possible
 * @param message a String that represents information that needs to be shown to the user. It is meant to elaborate on [title].
 * Should tell the details of this moderation action
 * @param iconId a Int that represents the id of the drawable resource that is going to be used as the icon.
 * This will be turned into a [Painter] object and shown to the user as an icon next to [title]
 * @param secondaryMessage a nullable String object that represents a message that can be shown to the user. The text is shown in red.
 * This is mainly only used for displaying text that was deleted during a message deleted moderation event.
 *
 * */
data class ModActionData(
    val title:String,
    val message:String,
    val iconId: Int,
    val secondaryMessage:String? =null
)

val followerModeList =listOf(
ListTitleValue("Off",null),ListTitleValue("0 minutes(any followers)",0),
ListTitleValue("10 minutes(most used)",10),
ListTitleValue("30 minutes",30),ListTitleValue( "1 hour",60),
ListTitleValue("1 day",1440),
    ListTitleValue("1 week",10080 ),
    ListTitleValue("1 month",43200 ),
    ListTitleValue("3 months",129600 )

)
//1 week 10080
//1 month 43200
//3 months 129600

val slowModeList =listOf(
    ListTitleValue("Off",null),
    ListTitleValue("3s",3),
    ListTitleValue("5s",5),
    ListTitleValue("10s",10),
    ListTitleValue( "20s",20),
    ListTitleValue("30s",30),
    ListTitleValue("60s",60 )
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

    private var _modViewStatus: MutableState<ModViewStatus> = mutableStateOf(ModViewStatus())
    val modViewStatus: State<ModViewStatus> = _modViewStatus

//
//        val modActionsList = listOf<ModActionData>(
//        ModActionData("Emotes-Only Chat","Removed by thepleddev", R.drawable.emote_face_24),
//        ModActionData("Followers-Only Chat","Removed by thepleddev", R.drawable.favorite_24),
//        ModActionData("Slow Mode","Enabled with 20s wait time, by theplebdev", R.drawable.person_outline_24),
//        ModActionData("meenermeeny","Message Deleted by by thepleddev:", R.drawable.delete_outline_24,"wtf this is some bull shit"),
//    )
    val modActionsList =mutableStateListOf<ModActionData>()

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
        modActionsList.clear()
        twitchEventSubWebSocket.newWebSocket()
    }
    init{
        monitorForChatSettingsUpdate()
    }
    init{
        monitorForModActions()
    }


    private fun monitorForModActions() = viewModelScope.launch(Dispatchers.IO){
        twitchEventSubWebSocket.modActions.collect{nullableModAction ->
            nullableModAction?.also {nonNullableModAction ->
                Log.d("ModActionsHappending","action ->$nonNullableModAction")
                modActionsList.add(nonNullableModAction)

            }
        }
    }

    private fun monitorForChatSettingsUpdate(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                twitchEventSubWebSocket.updatedChatSettingsData.collect{nullableChatData->
                    nullableChatData?.also {chatSettingsData ->
                        checkSlowModeWaitTime(chatSettingsData.slowModeWaitTime)
                        checkFollowerModeDuration(chatSettingsData.followerModeDuration)
                        Log.d("monitorForChatSettingsUpdate","emoteMode--> ${chatSettingsData.emoteMode}")
                        Log.d("monitorForChatSettingsUpdate","subscriberMode--> ${chatSettingsData.subscriberMode}")

                        _uiState.value = _uiState.value.copy(
                            chatSettings = _uiState.value.chatSettings.copy(
                                slowMode = chatSettingsData.slowMode,
                                slowModeWaitTime = chatSettingsData.slowModeWaitTime,
                                followerMode =chatSettingsData.followerMode,
                                followerModeDuration =chatSettingsData.followerModeDuration,
                                subscriberMode=chatSettingsData.subscriberMode,
                                emoteMode = chatSettingsData.emoteMode,

                                )
                        )

                    }
                }
            }
        }
    }

    /**
     * This is the function that calls createEventSubSubscription()
     * */
    private fun monitorForSessionId(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                twitchEventSubWebSocket.parsedSessionId.collect{nullableSessionId ->
                    nullableSessionId?.also {  sessionId ->
                        Log.d("monitorForSessionId","sessionId --> $sessionId")
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


    /**
     * monitorForAutoModMessageUpdates monitors the updates to current automod messages.
     * It will also minus 1 to the `_uiState.value.autoModQuePedingMessages` state
     *
     * */
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
                            val updatedMessage=_uiState.value.autoModQuePedingMessages -1
                            _uiState.value =_uiState.value.copy(
                                autoModQuePedingMessages =updatedMessage
                            )
                        }
                    }
                }
            }
        }
    }


    /**
     * - createModerationActionSubscription is a private function that is meant to establish a EventSub subsctiption type of `channel.moderate`. This will send a
     * notification when a moderator performs a moderation action in a channel.
     * - You can read more about this subscription type on Twitch's documentation site, [HERE](https://dev.twitch.tv/docs/eventsub/eventsub-subscription-types/#channelmoderate)
     * */
    private fun createModerationActionSubscription(){
        viewModelScope.launch(Dispatchers.IO) {
            _modViewStatus.value = _modViewStatus.value.copy(
                modActions = WebSocketResponse.Loading
            )

            twitchEventSub.createEventSubSubscription(
                oAuthToken = _requestIds.value.oAuthToken,
                clientId = _requestIds.value.clientId,
                broadcasterId = _requestIds.value.broadcasterId,
                moderatorId = _requestIds.value.moderatorId,
                sessionId = _requestIds.value.sessionId,
                type = "channel.moderate"
            ).collect { response ->
                when (response) {
                    is WebSocketResponse.Loading -> {}
                    is WebSocketResponse.Success -> {
                        _modViewStatus.value = _modViewStatus.value.copy(
                            modActions = WebSocketResponse.Success(true)
                        )
                        _uiState.value = _uiState.value.copy(
                            showSubscriptionEventError = Response.Success(true)
                        )
                        createAnotherSubscriptionEvent()
                    }

                    is WebSocketResponse.Failure -> {
                        _uiState.value = _uiState.value.copy(
                            showSubscriptionEventError = Response.Failure(response.e)
                        )
                        _modViewStatus.value = _modViewStatus.value.copy(
                            modActions = WebSocketResponse.Failure(Exception("failed to register subscription"))
                        )
                    }
                    is WebSocketResponse.FailureAuth403 ->{
                        _modViewStatus.value = _modViewStatus.value.copy(
                            modActions = WebSocketResponse.FailureAuth403(Exception("Improper Exception"))
                        )
                    }
                }
            }
        }

    }


    /**
     * To read more about the subscripting events, look [HERE][https://dev.twitch.tv/docs/eventsub/manage-subscriptions/#subscribing-to-events]
     * at the docs
     * */
    fun createEventSubSubscription(){
        // TODO: ON SUCCESS HAVE THIS MAKE ANOTHER SUBSCIRPTION TO THE UPDATE AUTOMOD MESSAGES
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                createModerationActionSubscription()
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
                        is WebSocketResponse.Loading -> {}
                        is WebSocketResponse.Success -> {
                            _uiState.value = _uiState.value.copy(
                                showSubscriptionEventError = Response.Success(true)
                            )
                            createAnotherSubscriptionEvent()
                        }

                        is WebSocketResponse.Failure -> {
                            _uiState.value = _uiState.value.copy(
                                showSubscriptionEventError = Response.Failure(response.e)
                            )
                        }
                        is WebSocketResponse.FailureAuth403 ->{

                        }


                    }
                }
            }
        }
    }
    private fun createAnotherSubscriptionEvent(){
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
                    is WebSocketResponse.Loading -> {
                        Log.d("createAnotherSubscriptionEvent", "response -->LOADING")
                    }

                    is WebSocketResponse.Success -> {
                        Log.d("createAnotherSubscriptionEvent", "response -->SUCCESS")
                        _uiState.value = _uiState.value.copy(
                            showSubscriptionEventError = Response.Success(true)
                        )
                        createChatSettingsSubscriptionEvent()
                    }

                    is WebSocketResponse.Failure -> {
                        Log.d("createAnotherSubscriptionEvent", "response -->FAILED")
                        _uiState.value = _uiState.value.copy(
                            showSubscriptionEventError = Response.Failure(response.e)
                        )
                    }
                    is WebSocketResponse.FailureAuth403 ->{

                    }
                }
            }
            }
        }
    }

    private fun createChatSettingsSubscriptionEvent(){

        viewModelScope.launch {
            withContext(Dispatchers.IO){
                twitchEventSub.createEventSubSubscriptionUserId(
                    oAuthToken =_requestIds.value.oAuthToken,
                    clientId =_requestIds.value.clientId,
                    broadcasterId =_requestIds.value.broadcasterId,
                    moderatorId =_requestIds.value.moderatorId,
                    sessionId = _requestIds.value.sessionId,
                    type = "channel.chat_settings.update"
                ).collect { response ->
                    when (response) {
                        is Response.Loading -> {
                            Log.d("createChatSettingsSubscriptionEvent", "response -->LOADING")
                        }

                        is Response.Success -> {
                            Log.d("createChatSettingsSubscriptionEvent", "response -->SUCCESS")
                            _uiState.value = _uiState.value.copy(
                                showSubscriptionEventError = Response.Success(true)
                            )
                        }

                        is Response.Failure -> {
                            Log.d("createChatSettingsSubscriptionEvent", "response -->FAILED")
                            _uiState.value = _uiState.value.copy(
                                showSubscriptionEventError = Response.Failure(response.e)
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * monitorForAutoModMessages monitors the websocket for new messages that are being held by the AutoMod.
     * Once a message from AutoMod arrives, it needs to be added to the [autoModMessageList].
     * It will also add 1 to the `_uiState.value.autoModQuePedingMessages` state
     * */
    private fun monitorForAutoModMessages(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                twitchEventSubWebSocket.autoModMessageQueue.collect { nullableAutoModMessage ->
                    nullableAutoModMessage?.also { autoModMessage ->
                        autoModMessageList.add(autoModMessage)
                        val updatedMessage=_uiState.value.autoModQuePedingMessages +1
                        _uiState.value =_uiState.value.copy(
                            autoModQuePedingMessages =updatedMessage
                        )
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

        getChatSettings(
            oAuthToken=oAuthToken,
            clientId =clientId,
            broadcasterId=broadcasterId,
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

    fun deleteBlockedTerm(id:String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                twitchEventSub.deleteBlockedTerm(
                    oAuthToken = _requestIds.value.oAuthToken,
                    clientId = _requestIds.value.clientId,
                    broadcasterId = _requestIds.value.broadcasterId,
                    moderatorId = _requestIds.value.moderatorId,
                    id = id
                ).collect{response ->
//                    val item =blockedTermsList.find { it.id == id}!!
//                    val indexOfItem = blockedTermsList.toList().indexOf(item)
                    when(response){
                        is Response.Loading ->{
                            Log.d("deleteBlockedTerm","LOADING")

                        }
                        is Response.Success ->{
                            Log.d("deleteBlockedTerm","SUCCESS")
                            blockedTermsList.removeIf { it.id == id }


                        }
                        is Response.Failure ->{
                            Log.d("deleteBlockedTerm","FAILURE")
                        }
                    }
                }
            }
        }
    }

    private fun getChatSettings(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String
    ){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                twitchEventSub.getChatSettings(
                    oAuthToken = oAuthToken,
                    clientId = clientId,
                    broadcasterId = broadcasterId,
                ).collect{response ->
                    when(response){
                        is Response.Loading ->{
                            Log.d("checkSlowModeWaitTime", "getChatSettings")
                        }
                        is Response.Success ->{
                            val data = response.data.data[0]
                            Log.d("getChatSettings", "LOADING")
                            checkSlowModeWaitTime(data.slowModeWaitTime)
                            checkFollowerModeDuration(data.followerModeDuration)

                            _uiState.value = _uiState.value.copy(
                                chatSettings = _uiState.value.chatSettings.copy(
                                    slowMode = data.slowMode,
                                    slowModeWaitTime = data.slowModeWaitTime,
                                    followerMode =data.followerMode,
                                    followerModeDuration =data.followerModeDuration,
                                    subscriberMode=data.subscriberMode,
                                    emoteMode = data.emoteMode,
                                ),
                                emoteOnly = data.emoteMode,
                                subscriberOnly = data.subscriberMode
                            )
                            Log.d("getChatSettings", "COLLECT SUCCESS")
                            Log.d("DataForChatSettings", "COLLECT data ->${response.data}")
                        }
                        is Response.Failure ->{
                            Log.d("getChatSettings", "COLLECT FAILED")
                        }
                    }
                }
            }
        }
    }

    /**
     * checkSlowModeWaitTime private function used to set the value of the selectedSlowMode ui state
     * */
    private fun checkSlowModeWaitTime(
        slowModeWaitTime:Int?,
    ){
        try{
            val foundSlowItem = slowModeList.first { it.value == slowModeWaitTime }
            _uiState.value = _uiState.value.copy(
                selectedSlowMode = foundSlowItem
            )

        }catch (e:NoSuchElementException){
            _uiState.value = _uiState.value.copy(
                selectedSlowMode = ListTitleValue("Custom",slowModeWaitTime)
            )

        }
    }

    /**
     * checkFollowerModeDuration private function used to set the value of the selectedFollowerMode ui state
     * */
    private fun checkFollowerModeDuration(followerModeDuration:Int?){
        try{
            val foundFollowerModeDuration = followerModeList.first { it.value == followerModeDuration }
            _uiState.value = _uiState.value.copy(
                selectedFollowerMode = foundFollowerModeDuration,
            )

        }catch (e:NoSuchElementException){
            _uiState.value = _uiState.value.copy(
                selectedFollowerMode = ListTitleValue("Custom",followerModeDuration)
            )

        }
    }


    fun updateEmoteOnly(emoteValue:Boolean){
        _uiState.value = _uiState.value.copy(
            chatSettings = _uiState.value.chatSettings.copy(emoteMode = emoteValue),
            emoteOnly = emoteValue
        )
        val body =ChatSettingsData(
            emoteMode = emoteValue,
            subscriberMode = _uiState.value.chatSettings.subscriberMode,
            followerMode = _uiState.value.chatSettings.followerMode,
            followerModeDuration = _uiState.value.chatSettings.followerModeDuration,
            slowMode =_uiState.value.chatSettings.slowMode,
            slowModeWaitTime = _uiState.value.chatSettings.slowModeWaitTime
        )
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                twitchEventSub.updateModViewChatSettings(
                    authorizationToken = _requestIds.value.oAuthToken,
                    clientId = _requestIds.value.clientId,
                    broadcasterId = _requestIds.value.broadcasterId,
                    moderatorId = _requestIds.value.moderatorId,
                    body = body
                ).collect{response ->
                    when(response){
                        is Response.Loading ->{

                        }
                        is Response.Success ->{
                            val data = response.data.data[0]
                            Log.d("checkSlowModeWaitTime","updateEmoteOnly")
                            checkSlowModeWaitTime(data.slow_mode_wait_time)
                            checkFollowerModeDuration(data.follower_mode_duration)

                            _uiState.value = _uiState.value.copy(
                                chatSettings = _uiState.value.chatSettings.copy(
                                    slowMode = data.slow_mode,
                                    slowModeWaitTime = data.slow_mode_wait_time,
                                    followerMode =data.follower_mode,
                                    followerModeDuration =data.follower_mode_duration,
                                    subscriberMode=data.subscriber_mode,
                                    emoteMode = data.emote_mode,
                                ),
                                emoteOnly = emoteValue
                            )
                        }
                        is Response.Failure ->{
                            _uiState.value = _uiState.value.copy(
                                chatSettings = _uiState.value.chatSettings.copy(
                                    emoteMode = !emoteValue,
                                    subscriberMode = _uiState.value.chatSettings.subscriberMode,
                                    followerMode = _uiState.value.chatSettings.followerMode,
                                    followerModeDuration = _uiState.value.chatSettings.followerModeDuration,
                                    slowMode =_uiState.value.chatSettings.slowMode,
                                    slowModeWaitTime = _uiState.value.chatSettings.slowModeWaitTime
                                ),
                                emoteOnly = !emoteValue
                            )

                        }
                    }
                }
            }
        }



    }
    fun updateSubscriberOnly(subscriberValue:Boolean){
        _uiState.value = _uiState.value.copy(
            chatSettings = _uiState.value.chatSettings.copy(subscriberMode = subscriberValue),
            subscriberOnly = subscriberValue
        )
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val body =ChatSettingsData(
                    emoteMode = _uiState.value.chatSettings.emoteMode,
                    subscriberMode = subscriberValue,
                    followerMode = _uiState.value.chatSettings.followerMode,
                    followerModeDuration = _uiState.value.chatSettings.followerModeDuration,
                    slowMode =_uiState.value.chatSettings.slowMode,
                    slowModeWaitTime = _uiState.value.chatSettings.slowModeWaitTime
                )

                twitchEventSub.updateModViewChatSettings(
                    authorizationToken = _requestIds.value.oAuthToken,
                    clientId = _requestIds.value.clientId,
                    broadcasterId = _requestIds.value.broadcasterId,
                    moderatorId = _requestIds.value.moderatorId,
                    body = body
                ).collect{response ->
                    when(response){
                        is Response.Loading ->{

                        }
                        is Response.Success ->{
                            val data = response.data.data[0]
                            Log.d("checkSlowModeWaitTime","updateSubscriberOnly")
                            checkSlowModeWaitTime(data.slow_mode_wait_time)
                            checkFollowerModeDuration(data.follower_mode_duration)

                            _uiState.value = _uiState.value.copy(
                                chatSettings = _uiState.value.chatSettings.copy(
                                    slowMode = data.slow_mode,
                                    slowModeWaitTime = data.slow_mode_wait_time,
                                    followerMode =data.follower_mode,
                                    followerModeDuration =data.follower_mode_duration,
                                    subscriberMode=data.subscriber_mode,
                                    emoteMode = data.emote_mode,
                                ),
                                subscriberOnly = subscriberValue
                            )
                        }
                        is Response.Failure ->{
                            _uiState.value = _uiState.value.copy(
                                chatSettings = _uiState.value.chatSettings.copy(
                                    emoteMode = _uiState.value.chatSettings.emoteMode,
                                    subscriberMode = !subscriberValue,
                                    followerMode = _uiState.value.chatSettings.followerMode,
                                    followerModeDuration = _uiState.value.chatSettings.followerModeDuration,
                                    slowMode =_uiState.value.chatSettings.slowMode,
                                    slowModeWaitTime = _uiState.value.chatSettings.slowModeWaitTime
                                ),
                                subscriberOnly = !subscriberValue
                            )
                        }
                    }
                }
            }
        }

    }
    fun changeSelectedFollowersModeItem(selectedFollowerMode: ListTitleValue){
        val oldSelectedValue = _uiState.value.selectedFollowerMode
        _uiState.value = _uiState.value.copy(
            selectedFollowerMode = selectedFollowerMode,
        )

        val followerModeDuration = selectedFollowerMode.value
        val followerMode = followerModeDuration != null

        val body =ChatSettingsData(
            emoteMode = _uiState.value.chatSettings.emoteMode,
            subscriberMode = _uiState.value.chatSettings.subscriberMode,
            followerMode = followerMode,
            followerModeDuration = followerModeDuration,
            slowMode =_uiState.value.chatSettings.slowMode,
            slowModeWaitTime = _uiState.value.chatSettings.slowModeWaitTime
        )
        viewModelScope.launch {
            twitchEventSub.updateModViewChatSettings(
                authorizationToken = _requestIds.value.oAuthToken,
                clientId = _requestIds.value.clientId,
                broadcasterId = _requestIds.value.broadcasterId,
                moderatorId = _requestIds.value.moderatorId,
                body = body
            ).collect{response ->
                when(response){
                    is Response.Loading ->{

                    }
                    is Response.Success ->{

                        val data = response.data.data[0]
                        checkSlowModeWaitTime(data.slow_mode_wait_time)
                        checkFollowerModeDuration(data.follower_mode_duration)

                        _uiState.value = _uiState.value.copy(
                            chatSettings = _uiState.value.chatSettings.copy(
                                slowMode = data.slow_mode,
                                slowModeWaitTime = data.slow_mode_wait_time,
                                followerMode =data.follower_mode,
                                followerModeDuration =data.follower_mode_duration,
                                subscriberMode=data.subscriber_mode,
                                emoteMode = data.emote_mode,
                                )
                        )
                    }
                    is Response.Failure ->{
                        checkFollowerModeDuration(oldSelectedValue.value)
                        _uiState.value = _uiState.value.copy(
                            chatSettings = _uiState.value.chatSettings.copy(
                                slowMode = _uiState.value.chatSettings.slowMode,
                                slowModeWaitTime = _uiState.value.chatSettings.slowModeWaitTime,
                                followerMode =oldSelectedValue.value !=null,
                                followerModeDuration =oldSelectedValue.value,
                                subscriberMode=_uiState.value.chatSettings.subscriberMode,
                                emoteMode = _uiState.value.chatSettings.emoteMode,
                            )
                        )
                    }
                }
            }
        }



    }
    fun changeSelectedSlowModeItem(selectedSlowMode: ListTitleValue){
        val oldSelectedSlowMode= _uiState.value.selectedSlowMode
        _uiState.value = _uiState.value.copy(
            selectedSlowMode =selectedSlowMode
        )

        val slowModeWaitTime = selectedSlowMode.value
        val slowMode = slowModeWaitTime != null

        val body =ChatSettingsData(
            emoteMode = _uiState.value.chatSettings.emoteMode,
            subscriberMode = _uiState.value.chatSettings.subscriberMode,
            followerMode = _uiState.value.chatSettings.followerMode,
            followerModeDuration = _uiState.value.chatSettings.followerModeDuration,
            slowMode =slowMode,
            slowModeWaitTime = slowModeWaitTime
        )
        viewModelScope.launch {
            twitchEventSub.updateModViewChatSettings(
                authorizationToken = _requestIds.value.oAuthToken,
                clientId = _requestIds.value.clientId,
                broadcasterId = _requestIds.value.broadcasterId,
                moderatorId = _requestIds.value.moderatorId,
                body = body
            ).collect{response ->
                when(response){
                    is Response.Loading ->{

                    }
                    is Response.Success ->{
                        val data = response.data.data[0]
                        checkSlowModeWaitTime(data.slow_mode_wait_time)
                        checkFollowerModeDuration(data.follower_mode_duration)

                        _uiState.value = _uiState.value.copy(
                            chatSettings = _uiState.value.chatSettings.copy(
                                slowMode = data.slow_mode,
                                slowModeWaitTime = data.slow_mode_wait_time,
                                followerMode =data.follower_mode,
                                followerModeDuration =data.follower_mode_duration,
                                subscriberMode=data.subscriber_mode,
                                emoteMode = data.emote_mode,
                            )
                        )
                    }
                    is Response.Failure ->{
                        checkSlowModeWaitTime(oldSelectedSlowMode.value)
                        _uiState.value = _uiState.value.copy(
                            chatSettings = _uiState.value.chatSettings.copy(
                                slowMode = oldSelectedSlowMode.value !=null,
                                slowModeWaitTime = oldSelectedSlowMode.value,
                                followerMode =_uiState.value.chatSettings.followerMode,
                                followerModeDuration =_uiState.value.chatSettings.followerModeDuration,
                                subscriberMode=_uiState.value.chatSettings.subscriberMode,
                                emoteMode = _uiState.value.chatSettings.emoteMode,
                            )
                        )
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

