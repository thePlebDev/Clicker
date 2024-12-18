package com.example.clicker.presentation.enhancedModView.viewModels

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.network.clients.BlockedTerm
import com.example.clicker.network.clients.ManageAutoModMessage
import com.example.clicker.network.clients.UnbanRequestItem
import com.example.clicker.network.domain.TwitchEventSubscriptionWebSocket
import com.example.clicker.network.domain.TwitchEventSubscriptions
import com.example.clicker.network.domain.TwitchModRepo
import com.example.clicker.network.domain.UnbanStatusFilter
import com.example.clicker.network.models.twitchStream.ChatSettingsData
import com.example.clicker.network.repository.ClickedUnbanRequestInfo
import com.example.clicker.network.repository.util.AutoModQueueMessage
import com.example.clicker.presentation.enhancedModView.AutoModMessageListImmutableCollection
import com.example.clicker.presentation.enhancedModView.ClickedUnbanRequestUser
import com.example.clicker.presentation.enhancedModView.ImmutableModeList
import com.example.clicker.presentation.enhancedModView.ListTitleValue
import com.example.clicker.presentation.enhancedModView.ModActionData
import com.example.clicker.presentation.enhancedModView.ModActionListImmutableCollection
import com.example.clicker.presentation.enhancedModView.ModViewStatus
import com.example.clicker.presentation.enhancedModView.ModViewViewModelUIState
import com.example.clicker.presentation.enhancedModView.RequestIds
import com.example.clicker.presentation.enhancedModView.UnbanRequestItemImmutableCollection
import com.example.clicker.util.Response
import com.example.clicker.util.UnAuthorizedResponse
import com.example.clicker.util.WebSocketResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject





val followerModeList =listOf(
ListTitleValue("Off",null), ListTitleValue("0 minutes(any followers)",0),
ListTitleValue("10 minutes(most used)",10),
ListTitleValue("30 minutes",30), ListTitleValue( "1 hour",60),
ListTitleValue("1 day",1440),
    ListTitleValue("1 week",10080 ),
    ListTitleValue("1 month",43200 ),
    ListTitleValue("3 months",129600 )

)
val followerModeListImmutable = ImmutableModeList(followerModeList)


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
val slowModeListImmutable = ImmutableModeList(slowModeList)


@HiltViewModel
class ModViewViewModel @Inject constructor(
    private val twitchEventSubWebSocket: TwitchEventSubscriptionWebSocket,
    private val twitchEventSub: TwitchEventSubscriptions,
    private val twitchModRepo: TwitchModRepo,
    private val ioDispatcher: CoroutineDispatcher,
): ViewModel() {
    private var _requestIds: MutableState<RequestIds> = mutableStateOf(RequestIds())


    /**
     * This is the data that is shown to in the modal once the unban request is selected
     * */
    private val _clickedUnbanRequestUser: MutableState<ClickedUnbanRequestUser> = mutableStateOf(
        ClickedUnbanRequestUser("","","","")
    )
    val clickedUnbanRequestUser: State<ClickedUnbanRequestUser> = _clickedUnbanRequestUser

    private val _optionalResolutionText: MutableState<String> = mutableStateOf("")
    val optionalResolutionText: State<String> = _optionalResolutionText

    fun updateOptionalResolutionText(text:String){
        _optionalResolutionText.value = text
    }

    fun updateClickedUnbanRequestUser(username:String,message:String,userId:String,requestId:String,status:String){
        _clickedUnbanRequestUser.value = _clickedUnbanRequestUser.value.copy(
            message=message,
            userName = username,
            requestId = requestId,
            status = status
        )
        Log.d("updateClickedUnbanRequestUser","oAuthToken->${_requestIds.value.oAuthToken}")
        Log.d("updateClickedUnbanRequestUser","clientId->${_requestIds.value.clientId}")
        Log.d("updateClickedUnbanRequestUser","broadcasterId->${_requestIds.value.broadcasterId}")
        getUserInformation(
            userId
        )
    }
    /**
     * This is a list of all the individual unban requests
     * */

    private val _getUnbanRequestResponse: MutableState<UnAuthorizedResponse<List<UnbanRequestItem>>> = mutableStateOf(
        UnAuthorizedResponse.Success(listOf())
    )
    val unbanRequestResponse: State<UnAuthorizedResponse<List<UnbanRequestItem>>> = _getUnbanRequestResponse

/**
 *
 *--------------------------------------------------- Unban request list items------------------------------------------------------------------------------------
 * */
    var unbanRequestItemList = mutableStateListOf<UnbanRequestItem>()
    private val _getUnbanRequestList: MutableState<UnbanRequestItemImmutableCollection> = mutableStateOf(
        UnbanRequestItemImmutableCollection(
        unbanRequestItemList
    )
    )
    fun sortUnbanRequestList(status:String){
        val updatedList = _getUnbanRequestList.value.list.toMutableList()
       val frontList= _getUnbanRequestList.value.list.filter { it.status == status }
        val backList= _getUnbanRequestList.value.list.filter { it.status != status }
        val newList = frontList+backList

        _getUnbanRequestList.value = _getUnbanRequestList.value.copy(list = newList)

    }

    // Publicly exposed immutable state as State
    val getUnbanRequestList: State<UnbanRequestItemImmutableCollection> = _getUnbanRequestList

    fun addAllUnbanRequestItemList(unbanRequestList:List<UnbanRequestItem>){
        unbanRequestItemList.addAll(unbanRequestList)
        _getUnbanRequestList.value = UnbanRequestItemImmutableCollection(unbanRequestItemList)

    }

    fun clearUnbanRequestItemList(){
        unbanRequestItemList.clear()
        _getUnbanRequestList.value = UnbanRequestItemImmutableCollection(listOf())

    }


    private val _clickedUnbanRequestInfo: MutableState<Response<ClickedUnbanRequestInfo>> = mutableStateOf(Response.Failure(Exception("Another one")))
    val clickedUnbanRequestInfo: State<Response<ClickedUnbanRequestInfo>> = _clickedUnbanRequestInfo




    /**
     * END OF THE IMMUTABLE LIST
     * */

    private val _resolveUnbanRequest: MutableState<Response<Boolean>> = mutableStateOf(Response.Success(true))
    val resolveUnbanRequest: State<Response<Boolean>> = _resolveUnbanRequest



    fun getUserInformation(userId:String)=viewModelScope.launch(ioDispatcher){
        _clickedUnbanRequestInfo.value = Response.Loading
        twitchModRepo.getUserInformation(
            authorizationToken=_requestIds.value.oAuthToken,
            clientId =_requestIds.value.clientId ,
            userId = userId
        ).collect{response ->
            when(response){
                is Response.Loading ->{
                    //this is fine being empty. The first line of the function takes care of it
                    _clickedUnbanRequestInfo.value = Response.Loading
                }
                is Response.Success ->{

                    _clickedUnbanRequestInfo.value = Response.Success(response.data)
                }
                is Response.Failure ->{
                    _clickedUnbanRequestInfo.value = Response.Failure(Exception("antoher one"))
                }
            }

        }

    }
    /**
     * - [twitch documentation. Resolve unban Request](https://dev.twitch.tv/docs/api/reference/#resolve-unban-requests)
     * */
    fun resolveUnbanRequest(
        unbanRequestId:String,
        status:UnbanStatusFilter
    ) = viewModelScope.launch(ioDispatcher){
        _resolveUnbanRequest.value = Response.Loading
        Log.d("resolveUnbanRequestId","Testing --->$unbanRequestId")


        twitchModRepo.approveUnbanRequests(
            authorizationToken=_requestIds.value.oAuthToken,
            clientId =_requestIds.value.clientId ,
            moderatorID = _requestIds.value.moderatorId,
            broadcasterId = _requestIds.value.broadcasterId,
            status = status,
            unbanRequestId = unbanRequestId,
            resolutionText = _optionalResolutionText.value
        ).collect{response ->
            when(response){
                is UnAuthorizedResponse.Loading ->{
                    Log.d("resolveUnbanRequest","Loading")
                }
                is UnAuthorizedResponse.Success ->{
                    Log.d("resolveUnbanRequest","SUCCESS")

                    //todo: this needs to be removed from the UI

                    _resolveUnbanRequest.value = Response.Success(false)
                    delay(1000)
                    _resolveUnbanRequest.value = Response.Success(true)
                }
                is UnAuthorizedResponse.Failure ->{
                    Log.d("resolveUnbanRequest","FAILED")
                    _resolveUnbanRequest.value = Response.Failure(Exception("FAILED AGAIN"))
                    delay(1000)
                    _resolveUnbanRequest.value = Response.Success(true)
                }
                is UnAuthorizedResponse.Auth401Failure ->{
                    Log.d("resolveUnbanRequest","Auth401Failure")
                    _resolveUnbanRequest.value = Response.Failure(Exception("FAILED AGAIN"))
                    delay(3000)
                    _resolveUnbanRequest.value = Response.Success(true)
                }
            }

        }




    }

    fun retryGetUnbanRequest(){

        getUnbanRequests(
            oAuthToken = _requestIds.value.oAuthToken,
            clientId = _requestIds.value.clientId,
            moderatorId=_requestIds.value.moderatorId,
            broadcasterId=_requestIds.value.broadcasterId
        )
    }
    fun getUnbanRequests(
         oAuthToken:String,
         clientId: String,
         moderatorId: String,
         broadcasterId: String
    )=viewModelScope.launch(ioDispatcher){
        //TODO: CLEAR THE OLD UnbanRequestItemList
        clearUnbanRequestItemList()
        Log.d("getUnbanRequestsBegin","oAuthToken -->$oAuthToken")



        twitchModRepo.getUnbanRequests(
            authorizationToken=oAuthToken,
            clientId =clientId ,
            moderatorID = moderatorId,
            broadcasterId = broadcasterId,
            status = UnbanStatusFilter.PENDING
        ).collect{response ->
            when(response){
                is UnAuthorizedResponse.Loading ->{
                    _getUnbanRequestResponse.value = UnAuthorizedResponse.Loading
                    Log.d("getUnbanRequestsFunc","LOADING")
                }
                is UnAuthorizedResponse.Success ->{
                    //todo: I need to change when the response gets added
                    val data = response.data
                    if(data.isNotEmpty()){
                        val newData = data.map {
                            Log.d("getUnbanRequestsTesting","id -->${it.id}")
                            Log.d("getUnbanRequestsTesting","user_id -->${it.user_id}")
                            it.copy(

                                created_at = it.created_at.split("T")[0]
                            )
                        }
                        _getUnbanRequestResponse.value = UnAuthorizedResponse.Success(newData)
                        addAllUnbanRequestItemList(newData)
                    }
                    _getUnbanRequestResponse.value = UnAuthorizedResponse.Success(listOf())

                    Log.d("getUnbanRequestsFunc","SUCCESS -->${data}")

                }
                is UnAuthorizedResponse.Auth401Failure ->{
                    Log.d("getUnbanRequestsFunc","Auth401Failure")
                    _getUnbanRequestResponse.value = UnAuthorizedResponse.Auth401Failure(Exception("FAILED"))
                }
                is UnAuthorizedResponse.Failure->{
                    Log.d("getUnbanRequestsFunc","Failure")
                    _getUnbanRequestResponse.value = UnAuthorizedResponse.Failure(Exception("FAILED"))

                }
            }

        }


    }




    /*****autoModMessageList START*****/
    val  autoModMessageList = mutableStateListOf<AutoModQueueMessage>()
    // Immutable state holder
    private var _autoModMessageListImmutableCollection by mutableStateOf(
        AutoModMessageListImmutableCollection(autoModMessageList)
    )

    // Publicly exposed immutable state as State
    val autoModMessageListImmutable: State<AutoModMessageListImmutableCollection>
        get() = mutableStateOf(_autoModMessageListImmutableCollection)

    private fun addAllAutoModMessageList(commands:List<AutoModQueueMessage>){
        autoModMessageList.addAll(commands)
        _autoModMessageListImmutableCollection = AutoModMessageListImmutableCollection(autoModMessageList)

    }


    /*****autoModMessageList END*****/
    private val _uiState: MutableState<ModViewViewModelUIState> = mutableStateOf(
        ModViewViewModelUIState()
    )
    val uiState: State<ModViewViewModelUIState> = _uiState

     val blockedTermsList = mutableStateListOf<BlockedTerm>()

    private var _modViewStatus: MutableState<ModViewStatus> = mutableStateOf(ModViewStatus())
    val modViewStatus: State<ModViewStatus> = _modViewStatus


    /**modActionsList START*/
    val modActionsList =mutableStateListOf<ModActionData>()
    // Immutable state holder
    private var _modActionListImmutableCollection by mutableStateOf(
        ModActionListImmutableCollection(modActionsList)
    )

    // Publicly exposed immutable state as State
    val modActionListImmutable: State<ModActionListImmutableCollection>
        get() = mutableStateOf(_modActionListImmutableCollection)

    //Now I need to implement the methods
    private fun addAllModActionList(actionList:List<ModActionData>){
        modActionsList.addAll(actionList)
        _modActionListImmutableCollection = ModActionListImmutableCollection(modActionsList)
    }
    private fun clearModActionList(){
        modActionsList.clear()
        _modActionListImmutableCollection = ModActionListImmutableCollection(modActionsList)
    }
    /**modActionsList END*/

    init{
        monitorForSessionId()
    }

    init{
        monitorForAutoModMessages()
    }
    init{
        monitorForAutoModMessageUpdates()
    }
    init{
        monitorForMostRecentResolvedUnbanRequests()
    }
    init{
        monitorForMostRecentUnbanRequests()
    }

    fun createNewTwitchEventWebSocket(){
       // modActionsList.clear()
        clearModActionList()
        Log.d("CREATINGNEWEVENTSUBSOCKET","CREATED")
        twitchEventSubWebSocket.newWebSocket()
    }
    fun createNewTwitchEventWebSocketHorizontalLongPress(
        oAuthToken:String,clientId:String,broadcasterId:String,moderatorId:String,
    ){
        _requestIds.value = _requestIds.value.copy(
            oAuthToken = oAuthToken,
            clientId =clientId,
            broadcasterId =broadcasterId,
            moderatorId =moderatorId

        )
      //  modActionsList.clear()
        clearModActionList()
        Log.d("CREATINGNEWEVENTSUBSOCKET","CREATED")
        twitchEventSubWebSocket.newWebSocket()

    }

    init{
        monitorForChatSettingsUpdate()
    }
    init{
        monitorForModActions()
    }


    private fun monitorForModActions() = viewModelScope.launch(ioDispatcher){
        twitchEventSubWebSocket.modActions.collect{nullableModAction ->
            nullableModAction?.also {nonNullableModAction ->
                Log.d("ModActionsHappending","action ->$nonNullableModAction")
               // modActionsList.add(nonNullableModAction)
                addAllModActionList(listOf(nonNullableModAction))
                if(_uiState.value.modActionNotifications){
                    val updatedMessage=_uiState.value.modViewTotalNotifications +1
                    _uiState.value =_uiState.value.copy(
                        modViewTotalNotifications =updatedMessage
                    )
                }


            }
        }
    }

    fun clearModViewNotifications(){

        _uiState.value =_uiState.value.copy(
            modViewTotalNotifications =0
        )
    }

    fun changeAutoModQueueChecked(value:Boolean){
        _uiState.value =_uiState.value.copy(
            autoModMessagesNotifications = value
        )
    }
    fun changeUnbanRequestChecked(value:Boolean){
        _uiState.value =_uiState.value.copy(
            unbanRequestNotifications = value
        )
    }

    fun changeModActionsChecked(value:Boolean){
        _uiState.value =_uiState.value.copy(
            modActionNotifications = value
        )
    }

    private fun monitorForChatSettingsUpdate(){
        viewModelScope.launch {
            withContext(ioDispatcher){
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
                                ),
                            //todo: remove the two below-> this is a hotfix and should be reworked
                            emoteOnly = chatSettingsData.emoteMode,
                            subscriberOnly = chatSettingsData.subscriberMode,
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
            withContext(ioDispatcher){
                twitchEventSubWebSocket.parsedSessionId.collect{nullableSessionId ->
                    nullableSessionId?.also {  sessionId ->
                        Log.d("monitorForSessionId","sessionId --> $sessionId")
                        _requestIds.value = _requestIds.value.copy(
                            sessionId = sessionId
                        )
                        createSubscriptionEvents(
                            moderatorActionSubscription={
                               createModerationActionSubscription()
                                                        },
                            autoModMessageUpdateSubscription={
                                createAutoModMessageUpdateSubscriptionEvent() // This is registering but not updating the UI
                                                             },
                            autoModMessageHoldSubscription={
                                createAutoModMessageHoldSubscriptionEvent()  // This is registering but not updating the UI
                                                           },
                            chatSettingsSubscription={
                                createChatSettingsSubscriptionEvent()
                            }
                        )
                        //then with this session Id we need to make a call to subscribe to our event


                    }
                }
            }

        }
    }

    /**
     * - createSubscriptionEvents() is a private function that calls all the methods that are making EventSub subscriptions.
     * - You can read more about EventSub subscriptions. [HERE](https://dev.twitch.tv/docs/eventsub/)
     *
     * @param moderatorActionSubscription full description [HERE][createModerationActionSubscription]
     * @param autoModMessageUpdateSubscription full description [HERE][createAutoModMessageUpdateSubscriptionEvent]
     * @param autoModMessageHoldSubscription full description [HERE][createAutoModMessageHoldSubscriptionEvent]
     * @param chatSettingsSubscription full description [HERE][createChatSettingsSubscriptionEvent]
     * */
    private fun createSubscriptionEvents(
        moderatorActionSubscription:()->Unit,
        autoModMessageUpdateSubscription:()->Unit,
        autoModMessageHoldSubscription:()->Unit,
        chatSettingsSubscription:()->Unit,
    ){
        moderatorActionSubscription()
//        autoModMessageUpdateSubscription()
//        autoModMessageHoldSubscription()
        chatSettingsSubscription()

        createUnbanRequestResolveSubscriptionEvent()
        createUnbanRequestCreateSubscriptionEvent()

    }


    /**
     * monitorForAutoModMessageUpdates monitors the updates to current automod messages.
     * It will also minus 1 to the `_uiState.value.autoModQuePedingMessages` state
     *
     * */
    private fun monitorForAutoModMessageUpdates(){
        viewModelScope.launch {
            withContext(ioDispatcher) {
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
                        addAllAutoModMessageList(autoModMessageList.toList())
                    }
                }
            }
        }
    }
    private fun monitorForMostRecentResolvedUnbanRequests(){
        viewModelScope.launch {
            withContext(ioDispatcher) {
                twitchEventSubWebSocket.mostRecentResolvedUnbanRequest.collect { nullableUnbanReqeust ->
                    nullableUnbanReqeust?.also { nonNullableUnbanReqeust ->
                        Log.d("monitorForMostRecentResolvedUnbanRequests","nonNull -->$nonNullableUnbanReqeust")
                        val unbanRequestId =nonNullableUnbanReqeust.id
                        val status =nonNullableUnbanReqeust.status
                        val foundRequest = _getUnbanRequestList.value.list.find { it.id == unbanRequestId }
                        foundRequest?.let {
                            val index = _getUnbanRequestList.value.list.indexOf(it)

                            if (index != -1) {
                                val updatedStatus = if (status =="approved") "approved" else "denied"
                                val updatedItem = it.copy(
                                    status = updatedStatus
                                )
                                _clickedUnbanRequestUser.value = _clickedUnbanRequestUser.value.copy(
                                    status = updatedStatus
                                )
                                // Create a new list with the updated item
                                val updatedList = _getUnbanRequestList.value.list.toMutableList()
                                updatedList[index] = updatedItem
                                _getUnbanRequestList.value = _getUnbanRequestList.value.copy(list = updatedList)
                            }
                        }

                    }
                }
            }
        }
    }

    private fun monitorForMostRecentUnbanRequests(){
        viewModelScope.launch {
            withContext(ioDispatcher) {
                twitchEventSubWebSocket.mostRecentUnbanRequest.collect { nullableUnbanReqeust ->
                    nullableUnbanReqeust?.also { nonNullableUnbanReqeust ->
                        Log.d("monitorForMostRecentResolvedUnbanRequests","nonNull -->$nonNullableUnbanReqeust")
                        addAllUnbanRequestItemList(listOf(nonNullableUnbanReqeust))
                        val updatedMessage=_uiState.value.modViewTotalNotifications +1
                        if(_uiState.value.unbanRequestNotifications){
                            _uiState.value =_uiState.value.copy(
                                modViewTotalNotifications =updatedMessage
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
        viewModelScope.launch(ioDispatcher) {
            _modViewStatus.value = _modViewStatus.value.copy(
                modActions = WebSocketResponse.Loading
            )
            Log.d("createModerationActionSubscriptionTESTING","oAuthToken->${_requestIds.value.oAuthToken}")
            Log.d("createModerationActionSubscriptionTESTING","clientId->${_requestIds.value.clientId}")
            Log.d("createModerationActionSubscriptionTESTING","broadcasterId->${_requestIds.value.broadcasterId}")
            Log.d("createModerationActionSubscriptionTESTING","moderatorId->${_requestIds.value.moderatorId}")
            Log.d("createModerationActionSubscriptionTESTING","sessionId->${_requestIds.value.sessionId}")

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
                        Log.d("createModerationActionSubscriptionTESTING","Success")
                        _modViewStatus.value = _modViewStatus.value.copy(
                            modActions = WebSocketResponse.Success(true)
                        )
                        _uiState.value = _uiState.value.copy(
                            showSubscriptionEventError = Response.Success(true)
                        )
                    }

                    is WebSocketResponse.Failure -> {
                        Log.d("createModerationActionSubscriptionTESTING","Failure")
                        _uiState.value = _uiState.value.copy(
                            showSubscriptionEventError = Response.Failure(response.e)
                        )
                        _modViewStatus.value = _modViewStatus.value.copy(
                            modActions = WebSocketResponse.Failure(Exception("failed to register subscription"))
                        )
                    }
                    is WebSocketResponse.FailureAuth403 ->{
                        Log.d("createModerationActionSubscriptionTESTING","FailureAuth403")
                        _modViewStatus.value = _modViewStatus.value.copy(
                            modActions = WebSocketResponse.FailureAuth403(Exception("Improper Exception"))
                        )
                    }
                }
            }
        }

    }


    /**
     * - createAutoModMessageHoldSubscriptionEvent is a private function that is meant to establish a EventSub subsctiption type of `automod.message.hold`. This will
     * send a notification when a moderator performs a moderation action in a channel
     * - You can read more about this subscription type on Twitch's documentation site, [HERE](https://dev.twitch.tv/docs/eventsub/manage-subscriptions/#subscribing-to-events)
     * */
    private fun createAutoModMessageHoldSubscriptionEvent(){
        // TODO: ON SUCCESS HAVE THIS MAKE ANOTHER SUBSCIRPTION TO THE UPDATE AUTOMOD MESSAGES
        Log.d("CheckingSessionsEvents","createAutoModMessageHoldSubscriptionEvent()   oAuthToken--> ${_requestIds.value.oAuthToken}")
        viewModelScope.launch {
            withContext(ioDispatcher) {
                _modViewStatus.value = _modViewStatus.value.copy(
                    autoModMessageStatus = WebSocketResponse.Loading
                )

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
                            _modViewStatus.value = _modViewStatus.value.copy(
                                autoModMessageStatus = WebSocketResponse.Success(true)
                            )
                        }

                        is WebSocketResponse.Failure -> {
                            _modViewStatus.value = _modViewStatus.value.copy(
                                autoModMessageStatus = WebSocketResponse.Failure(Exception("Failed to connect"))
                            )
                        }
                        is WebSocketResponse.FailureAuth403 ->{
                            _modViewStatus.value = _modViewStatus.value.copy(
                                autoModMessageStatus = WebSocketResponse.FailureAuth403(Exception("Token Error"))
                            )

                        }


                    }
                }
            }
        }
    }


    /**
     * - createAutoModMessageSubscriptionEvent is a private function that  is meant to establish a EventSub subsctiption type of `automod.message.update`.
     * This will send a notification when a message in the automod queue has its status changed.
     * - - You can read more about this subscription type on Twitch's documentation site, [HERE](https://dev.twitch.tv/docs/eventsub/eventsub-subscription-types/#automodmessageupdate)
     * */
    private fun createAutoModMessageUpdateSubscriptionEvent(){
        Log.d("CheckingSessionsEvents","createAutoModMessageUpdateSubscriptionEvent sessionId -->${_requestIds.value.moderatorId}")
        viewModelScope.launch {
            withContext(ioDispatcher){
                _modViewStatus.value = _modViewStatus.value.copy(
                    autoModMessageStatus = WebSocketResponse.Loading
                )

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
                        _modViewStatus.value = _modViewStatus.value.copy(
                            autoModMessageStatus = WebSocketResponse.Success(true)
                        )

                    }

                    is WebSocketResponse.Failure -> {
                        _modViewStatus.value = _modViewStatus.value.copy(
                            autoModMessageStatus = WebSocketResponse.Failure(Exception("Failed to connect"))
                        )
                    }
                    is WebSocketResponse.FailureAuth403 ->{
                        _modViewStatus.value = _modViewStatus.value.copy(
                            autoModMessageStatus = WebSocketResponse.FailureAuth403(Exception("Token Error"))
                        )

                    }
                }
            }
            }
        }
    }

    private fun createUnbanRequestResolveSubscriptionEvent(){
        viewModelScope.launch {
            withContext(ioDispatcher){
                _modViewStatus.value = _modViewStatus.value.copy(
                    autoModMessageStatus = WebSocketResponse.Loading
                )


                twitchEventSub.createEventSubSubscription(
                    oAuthToken =_requestIds.value.oAuthToken,
                    clientId =_requestIds.value.clientId,
                    broadcasterId =_requestIds.value.broadcasterId,
                    moderatorId =_requestIds.value.moderatorId,
                    sessionId = _requestIds.value.sessionId,
                    type = "channel.unban_request.resolve"
                ).collect { response ->
                    when (response) {
                        is WebSocketResponse.Loading -> {
                            _modViewStatus.value = _modViewStatus.value.copy(
                                channelPointsRewardQueueStatus = response
                            )
                            Log.d("createUnbanRequestResolveSubscriptionEvent", "response -->LOADING")
                        }

                        is WebSocketResponse.Success -> {
                            Log.d("createUnbanRequestResolveSubscriptionEvent", "response -->SUCCESS")
                            _modViewStatus.value = _modViewStatus.value.copy(
                                channelPointsRewardQueueStatus = response
                            )


                        }

                        is WebSocketResponse.Failure -> {
                            Log.d("createUnbanRequestResolveSubscriptionEvent", "response -->NORMAL FAIL")
                            _modViewStatus.value = _modViewStatus.value.copy(
                                channelPointsRewardQueueStatus = response
                            )
                        }
                        is WebSocketResponse.FailureAuth403 ->{
                            Log.d("createUnbanRequestResolveSubscriptionEvent", "response -->FailureAuth403 FAIL")
                            _modViewStatus.value = _modViewStatus.value.copy(
                                channelPointsRewardQueueStatus = response
                            )


                        }
                    }
                }
            }
        }
    }

    private fun createUnbanRequestCreateSubscriptionEvent(){
        viewModelScope.launch {
            withContext(ioDispatcher){
                _modViewStatus.value = _modViewStatus.value.copy(
                    autoModMessageStatus = WebSocketResponse.Loading
                )


                twitchEventSub.createEventSubSubscription(
                    oAuthToken =_requestIds.value.oAuthToken,
                    clientId =_requestIds.value.clientId,
                    broadcasterId =_requestIds.value.broadcasterId,
                    moderatorId =_requestIds.value.moderatorId,
                    sessionId = _requestIds.value.sessionId,
                    type = "channel.unban_request.create"
                ).collect { response ->
                    when (response) {
                        is WebSocketResponse.Loading -> {
                            _modViewStatus.value = _modViewStatus.value.copy(
                                channelPointsRewardQueueStatus = response
                            )
                            Log.d("createUnbanRequestCreateSubscriptionEvent", "response -->LOADING")
                        }

                        is WebSocketResponse.Success -> {
                            Log.d("createUnbanRequestCreateSubscriptionEvent", "response -->SUCCESS")
                            _modViewStatus.value = _modViewStatus.value.copy(
                                channelPointsRewardQueueStatus = response
                            )


                        }

                        is WebSocketResponse.Failure -> {
                            Log.d("createUnbanRequestCreateSubscriptionEvent", "response -->NORMAL FAIL")
                            _modViewStatus.value = _modViewStatus.value.copy(
                                channelPointsRewardQueueStatus = response
                            )
                        }
                        is WebSocketResponse.FailureAuth403 ->{
                            Log.d("createUnbanRequestCreateSubscriptionEvent", "response -->FailureAuth403 FAIL")
                            _modViewStatus.value = _modViewStatus.value.copy(
                                channelPointsRewardQueueStatus = response
                            )


                        }
                    }
                }
            }
        }
    }

    /**
     * - createChatSettingsSubscriptionEvent is a private function that is meant to establish a EventSub subsctiption type of `channel.chat_settings.update`. This will
     * send a notification when a moderator updates the chat's settings
     * - You can read more about this subscription type on Twitch's documentation site, (HERE)[https://dev.twitch.tv/docs/eventsub/eventsub-subscription-types/#channelchat_settingsupdate]
     * */
    private fun createChatSettingsSubscriptionEvent(){
        Log.d("CheckingSessionsEvents","createChatSettingsSubscriptionEvent()   oAuthToken--> ${_requestIds.value.oAuthToken}")

        viewModelScope.launch {
            withContext(ioDispatcher){
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
            withContext(ioDispatcher) {
                twitchEventSubWebSocket.autoModMessageQueue.collect { nullableAutoModMessage ->
                    nullableAutoModMessage?.also { autoModMessage ->
                        autoModMessageList.add(autoModMessage)
                        Log.d("monitorForAutoModMessagesNew","autoModMessage ->${autoModMessage}")
                        addAllAutoModMessageList(autoModMessageList.toList())
                        if(_uiState.value.autoModMessagesNotifications){
                            val updatedMessage=_uiState.value.modViewTotalNotifications +1
                            _uiState.value =_uiState.value.copy(
                                modViewTotalNotifications =updatedMessage
                            )
                        }

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
            withContext(ioDispatcher){
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

    /**
     * manageAutoModMessage is a function that is used to send a request to Twitch servers and update the held AutoMod message
     * - You can read more about managing held AutoMod messages, [HERE](https://dev.twitch.tv/docs/api/reference/#manage-held-automod-messages)
     *
     * */
    fun manageAutoModMessage(
        msgId:String,
        action:String
    ){
        viewModelScope.launch {
            withContext(ioDispatcher){
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
                            addAllAutoModMessageList(autoModMessageList)


                        }
                        if (action == "DENY") {
                            autoModMessageList[indexOfItem] = item.copy(
                                approved = false,
                                swiped = true
                            )
                            addAllAutoModMessageList(autoModMessageList)
                        }

                    }

                    is Response.Failure -> {
                        autoModMessageList[indexOfItem] = item.copy(
                            swiped = false
                        )
                        addAllAutoModMessageList(autoModMessageList)
                    }
                }
            }

            }
        }

    }

    fun deleteBlockedTerm(id:String){
        viewModelScope.launch {
            withContext(ioDispatcher){
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
            withContext(ioDispatcher){
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
            withContext(ioDispatcher){
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
            withContext(ioDispatcher){
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
        viewModelScope.launch(ioDispatcher) {
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

