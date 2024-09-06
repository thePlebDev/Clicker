package com.example.clicker.presentation.stream

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.network.clients.BanUser
import com.example.clicker.network.clients.BanUserData
import com.example.clicker.network.clients.WarnData
import com.example.clicker.network.clients.WarnUserBody
import com.example.clicker.network.domain.TwitchEmoteRepo
import com.example.clicker.network.domain.TwitchStream
import com.example.clicker.network.models.twitchStream.ChatSettingsData
import com.example.clicker.network.domain.TwitchSocket
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.network.repository.models.EmoteNameUrl
import com.example.clicker.network.repository.models.EmoteNameUrlList

import com.example.clicker.network.websockets.MessageScanner
import com.example.clicker.network.websockets.MessageToken
import com.example.clicker.network.websockets.PrivateMessageType
import com.example.clicker.network.websockets.models.MessageType
import com.example.clicker.presentation.stream.models.AdvancedChatSettings
import com.example.clicker.presentation.stream.models.ClickedStreamInfo
import com.example.clicker.presentation.stream.models.ClickedUIState
import com.example.clicker.presentation.stream.models.ClickedUserBadgesImmutable
import com.example.clicker.presentation.stream.models.ClickedUserNameChats
import com.example.clicker.presentation.stream.models.ClickedUsernameChatsWithDateSentImmutable
import com.example.clicker.presentation.stream.models.ModChatSettings
import com.example.clicker.presentation.stream.models.StreamUIState
import com.example.clicker.presentation.stream.models.TextFieldValueImmutable


import com.example.clicker.presentation.stream.util.Scanner
import com.example.clicker.presentation.stream.util.TextCommands
import com.example.clicker.presentation.stream.util.TextParsing
import com.example.clicker.presentation.stream.util.TokenCommand
import com.example.clicker.presentation.stream.util.TokenMonitoring
import com.example.clicker.util.Response
import com.example.clicker.util.mapWithRetry
import com.example.clicker.util.objectMothers.TwitchUserDataObjectMother
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext




@HiltViewModel
class StreamViewModel @Inject constructor(
    private val webSocket: TwitchSocket,
    private val twitchRepoImpl: TwitchStream,
    private val ioDispatcher: CoroutineDispatcher,
    private val twitchEmoteImpl: TwitchEmoteRepo,
    private val textParsing:TextParsing = TextParsing(),
    private val tokenMonitoring: TokenMonitoring= TokenMonitoring(),
    private val tokenCommand: TokenCommand =TokenCommand(),
) : ViewModel() {

    /**
     * private mutable version of [channelName]
     * */
    private val _channelName: MutableStateFlow<String?> = MutableStateFlow(null)

    /**
     * a [StateFlow] String object used to hold the channel name of the stream the channel is viewing
     * */
    val channelName: StateFlow<String?> = _channelName

    /**
     * private mutable version of [clientId]
     * */
    private val _clientId: MutableState<String?> = mutableStateOf(null)
    /**
     * a [State] nullable-String object used to hold the unique identifier of the Android application
     * */
    val clientId: State<String?> = _clientId

    /********THIS IS ALL THE EMOTE RELATED CALLS**************************************/
    /**
     * - a State object containing a [EmoteNameUrlList] object which represents all the
     * global Twitch emotes to be shown in the emote board
     * */
    val globalEmoteUrlList = twitchEmoteImpl.emoteBoardGlobalList
    /**
     * - a State object containing a [EmoteNameUrlEmoteTypeList][com.example.clicker.network.repository.models.EmoteNameUrlEmoteTypeList] object which represents all the channel
     * specific Twitch emotes to be shown in the emote board
     * */
    val channelEmoteUrlList = twitchEmoteImpl.emoteBoardChannelList
    /**
     * - a State object containing a [EmoteListMap][com.example.clicker.network.repository.models.EmoteListMap] object which represents all the
     * global Twitch badges to be shown in the emote board
     * */
    val badgeListMap = twitchEmoteImpl.globalChatBadges
    /**
     * - a State object containing a [IndivBetterTTVEmoteList][com.example.clicker.network.repository.models.IndivBetterTTVEmoteList]
     * object which represents all the global BetterTTV emotes to be shown in the emote board
     * */
    val globalBetterTTVEmotes=twitchEmoteImpl.globalBetterTTVEmotes
    /**
     * - a State object containing a [IndivBetterTTVEmoteList][com.example.clicker.network.repository.models.IndivBetterTTVEmoteList]
     * object which represents all the channel specific BetterTTV emotes to be shown in the emote board
     * */
    val channelBetterTTVEmote = twitchEmoteImpl.channelBetterTTVEmotes
    /**
     * - a State object containing a [IndivBetterTTVEmoteList][com.example.clicker.network.repository.models.IndivBetterTTVEmoteList]
     * object which represents all the channel shared BetterTTV emotes to be shown in the emote board
     * */
    val sharedChannelBetterTTVEmote = twitchEmoteImpl.sharedBetterTTVEmotes

    /*****LOW POWER MODE******/
    private var _lowPowerModeActive: MutableState<Boolean> = mutableStateOf(false)
    val lowPowerModeActive: State<Boolean> = _lowPowerModeActive
    fun changeLowPowerModeActive(newValue:Boolean){
        _lowPowerModeActive.value = newValue
    }


    /**
     * A list representing all the most recent clicked emotes
     * */
    val mostFrequentEmoteListTesting = mutableStateOf(EmoteNameUrlList())
    private val temporaryMostFrequentList = mutableStateListOf<EmoteNameUrl>()

    /**
     * A list representing all the chats users have sent
     * */
    val listChats = mutableStateListOf<TwitchUserData>()

    /**
     * A list representing all the actions taken by moderators
     * */
    private val modActionList= mutableStateListOf<TwitchUserData>()


    private var _uiState: MutableState<StreamUIState> = mutableStateOf(StreamUIState())
    val state: State<StreamUIState> = _uiState

    private val _clickedUIState = mutableStateOf(ClickedUIState())
    val clickedUIState = _clickedUIState

    private val _clickedStreamInfo = mutableStateOf(ClickedStreamInfo())
    val clickedStreamInfo = _clickedStreamInfo


    /**
     * The UI state that represents all the data meant for the [ChatSettingsContainer.EnhancedChatSettingsBox] composable
     * */
    private val _advancedChatSettingsState = mutableStateOf(AdvancedChatSettings())
    val advancedChatSettingsState = _advancedChatSettingsState


    /**
     * represents what the user is typing in the text field
     * */
    //todo:I THINK I COULD MOVE THIS TO ANOTHER---(DONE)
    val textFieldValue:MutableState<TextFieldValue> = textParsing.textFieldValue

    val openTimeoutDialog = mutableStateOf(false)
    val openBanDialog = mutableStateOf(false)





    /**
     * A list of Strings that represents the list of users that are being searched when the user enters the ***@***
     * into the text box
     * */
    val filteredChatListImmutable = textParsing.filteredChatListImmutable

    val forwardSlashCommandImmutable = textParsing.forwardSlashCommandsState

    fun clearFilteredChatterList(){
        textParsing.clearFilteredChatterList()
    }

    val clickedUsernameChatsWithDateSent = mutableStateListOf<ClickedUserNameChats>()
    val clickedUserBadges =mutableStateListOf<String>() //this needs to be make stable
    private var _clickedUserBadgesImmutable by mutableStateOf(
        ClickedUserBadgesImmutable(clickedUserBadges)
    )

    /**THis is the data for the new filter methods*/
    private val _idOfLatestBan = mutableStateOf("")



    val openWarningDialog =mutableStateOf(false)
    val warningText = mutableStateOf("")


    // Publicly exposed immutable state as State
    val clickedUserBadgesImmutable: State<ClickedUserBadgesImmutable>
        get() = mutableStateOf(_clickedUserBadgesImmutable)
    private fun addAllClickedUserBadgesImmutable(clickedBadges:List<String>){
        clickedUserBadges.addAll(clickedBadges)
        _clickedUserBadgesImmutable = ClickedUserBadgesImmutable(clickedUserBadges)
    }
    private fun clearAllClickedUserBadgesImmutable(){
        clickedUserBadges.clear()
        _clickedUserBadgesImmutable = ClickedUserBadgesImmutable(listOf())
    }
    /**
     * I need to make the immutable version of clickedUsernameChatsWithDateSent
     * */
    // this is the immutable clickedUsernameChatsWithDateSentImmutable
    // Immutable state holder
    private var _clickedUsernameChatsDateSentImmutable by mutableStateOf(
        ClickedUsernameChatsWithDateSentImmutable(clickedUsernameChatsWithDateSent)
    )

    // Publicly exposed immutable state as State
    val clickedUsernameChatsDateSentImmutable: State<ClickedUsernameChatsWithDateSentImmutable>
        get() = mutableStateOf(_clickedUsernameChatsDateSentImmutable)

    private fun addAllClickedUsernameChatsDateSent(clickedChats:List<ClickedUserNameChats>){
        clickedUsernameChatsWithDateSent.addAll(clickedChats)
        _clickedUsernameChatsDateSentImmutable = ClickedUsernameChatsWithDateSentImmutable(clickedUsernameChatsWithDateSent)

    }
    private fun clearClickedUsernameChatsDateSent(){
        clickedUsernameChatsWithDateSent.clear()
        _clickedUsernameChatsDateSentImmutable = ClickedUsernameChatsWithDateSentImmutable(listOf())

    }


    /*** END OF THE MUTABLE LIST OF ClickedUsernameChatsWithDateSentImmutable ***/

    private val allChatters = mutableStateListOf<String>()

    private val monitoredUsers = mutableStateListOf<String>()




    init{
        monitorForWebSocketFailure()
    }

    init {
        monitorForLatestBannedMessageId()
    }
    init{
        monitorForLatestBannedUserId()
    }
    init {
        monitorForLoggedInUserData()
    }
    init {
        monitorSocketForChatMessages()
    }
    init {
        monitorForChannelName()
    }
    init {
        monitorSocketRoomState()
    }



    /**
     * monitorForWebSocketFailure is a function meant to monitor the state of the chat websocket. If the websocket
     * throws an error or closes unexpectedly,this function will add an error message to the user's chat message
     * */
    private fun monitorForWebSocketFailure(){
        viewModelScope.launch(ioDispatcher) {
            webSocket.hasWebSocketFailed.collect{nullableValue ->
                nullableValue?.also { value ->
                    val errorValue = TwitchUserDataObjectMother
                        .addColor("#FF0000")
                        .addDisplayName("Connection Error")
                        .addMessageType(MessageType.ERROR)
                        .addUserType(
                            "Disconnected from chat."
                        )
                        .build()
                    listChats.add(errorValue)
                }

            }
        }
    }



    /**
     * monitorForLatestBannedMessageId is a function meant to monitor the chat websocket for the latest user banned
     * */
    private fun monitorForLatestBannedMessageId(){
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



    /**
     * monitorForLatestBannedUserId is a function meant to monitor of the latest ban/timeout messages
     *
     * */
    private fun monitorForLatestBannedUserId(){
        viewModelScope.launch(ioDispatcher) {
            webSocket.latestBannedUserId.collect{latestBannedId ->
                latestBannedId?.also{
                    Log.d("latestBannedId", "latestBannedId --> ${latestBannedId}")
                    _idOfLatestBan.value = latestBannedId
                }

            }
        }
    }


    //

    /**
     * monitorForLoggedInUserData is a function meant to monitor and determine if the user is a moderator or not
     * */
    private fun monitorForLoggedInUserData(){
        viewModelScope.launch(ioDispatcher) {
            webSocket.loggedInUserUiState.collect {nullableLoggedInData ->
                nullableLoggedInData?.let {LoggedInData ->
                    _uiState.value = _uiState.value.copy(
                        loggedInUserData = LoggedInData
                    )
                }
            }
        }
    }

    /**
     * monitorForChannelName is a function meant to monitor for a change in the [_channelName] parameter. If there is a change
     * a new websocket will be started
     * */
    private fun monitorForChannelName(){
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

    /**
     * monitorSocketRoomState is a function meant to monitor for a change in the chat settings. If there is a change then
     * the [_uiState] chatSettings parameter is changed
     * */
    private fun monitorSocketRoomState(){
        viewModelScope.launch {
            withContext(ioDispatcher + CoroutineName("RoomState")) {
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
        }

    }

    /**monitorSocketForChatMessages is a function that checks for types of messages that come from the
     * websocket.
     * */
    private  fun monitorSocketForChatMessages(){
        viewModelScope.launch {//HAS TO BE ON THE MAIN THREAD!!!
            webSocket.state.collect { twitchUserMessage ->
                Log.d("loggedMessage", " tmiSentTs --> ${twitchUserMessage.tmiSentTs}")
                Log.d("twitchUserMessage", " messageType --> ${twitchUserMessage.messageType}")
                Log.d("twitchUserMessage", " twitchUserMessage --> ${twitchUserMessage}")
                Log.d("twitchUserMessage", "-----------------------------------------------------")
                Log.d("twitchUserMessageTesting", "displayName ->${twitchUserMessage.displayName}")
                Log.d("twitchUserMessageTesting", "clickedUsername ->${_clickedUIState.value.clickedUsername}")
                Log.d("twitchUserMessageTesting", "equal ->${twitchUserMessage.displayName == _clickedUIState.value.clickedUsername}")

                if (twitchUserMessage.displayName == _clickedUIState.value.clickedUsername) {

                    addAllClickedUsernameChatsDateSent(
                        listOf(
                            ClickedUserNameChats(
                                message =twitchUserMessage.userType?:"",
                                dateSent = twitchUserMessage.dateSend,
                                messageTokenList = MessageScanner(twitchUserMessage.userType?:"").tokenList
                            )
                        )
                    )

                }

                when(twitchUserMessage.messageType){
                    MessageType.CLEARCHAT ->{
                        modActionList.add(twitchUserMessage)
                        listChats.add(twitchUserMessage)
                        // notifyChatOfBanTimeoutEvent(listChats,twitchUserMessage.userType)
                    }
                    MessageType.NOTICE ->{
                        modActionList.add(twitchUserMessage)
                        listChats.add(twitchUserMessage)
                        // notifyChatOfBanTimeoutEvent(listChats,twitchUserMessage.userType)
                    }
                    MessageType.CLEARCHATALL->{
                        modActionList.add(twitchUserMessage)
                        clearAllChatMessages(listChats)
                    }
                    MessageType.USER ->{
                        Log.d("CheckingChattersNmae","${twitchUserMessage.displayName!!}")
                        Log.d("CheckingChattersNmae","${twitchUserMessage.userType!!}")
                        addChatter(twitchUserMessage.displayName!!,twitchUserMessage.userType!!)
                        listChats.add(twitchUserMessage)
                    }
                    MessageType.SUB ->{
                        if(_advancedChatSettingsState.value.showSubs){
                            listChats.add(twitchUserMessage)
                        }

                    }
                    MessageType.RESUB ->{
                        if(_advancedChatSettingsState.value.showReSubs){
                            listChats.add(twitchUserMessage)
                        }
                    }
                    MessageType.GIFTSUB ->{
                        if(_advancedChatSettingsState.value.showGiftSubs){
                            listChats.add(twitchUserMessage)
                        }
                    }
                    MessageType.MYSTERYGIFTSUB ->{
                        if(_advancedChatSettingsState.value.showAnonSubs){
                            listChats.add(twitchUserMessage)
                        }
                    }

                    else -> {
                        listChats.add(twitchUserMessage)
                    }
                }


                //todo:CLEAR THIS MESS OUT ABOVE
            }
        }


    }


    fun updateTemporaryMostFrequentList(clickedItem:EmoteNameUrl){
        if(!temporaryMostFrequentList.contains(clickedItem)){
            temporaryMostFrequentList.add(clickedItem)

        }
        Log.d("updateTemporaryMostFrequentList","list ->${temporaryMostFrequentList.toList()}")
    }


    fun updateMostFrequentEmoteList(){
        //Need to do some sorting between the two
        val oldList = mostFrequentEmoteListTesting.value.list.toMutableList()
        val oldTemporaryList = temporaryMostFrequentList.filter { !oldList.contains(it) }
        val newList = oldList + oldTemporaryList
        //need to do sorting and validation checks

        mostFrequentEmoteListTesting.value = mostFrequentEmoteListTesting.value.copy(
            list =newList
        )
        temporaryMostFrequentList.clear()


    }

    fun updateClickedStreamInfo(clickedStreamInfo:ClickedStreamInfo){
        //todo: need to do some adjusting for the thumbnail url
        _clickedStreamInfo.value =clickedStreamInfo
        _channelName.value = clickedStreamInfo.channelName
    }




    fun getBetterTTVGlobalEmotes(){
        viewModelScope.launch(ioDispatcher) {
            twitchEmoteImpl.getBetterTTVGlobalEmotes().collect{response ->
                //Nothing is done when collecting values


            }
        }
    }
    fun getBetterTTVChannelEmotes(broadcasterId: String){
        Log.d("getBetterTTVChannelEmotes", "broadcasterId ->$broadcasterId")
        viewModelScope.launch(ioDispatcher) {
            twitchEmoteImpl.getBetterTTVChannelEmotes(broadcasterId).collect{response ->

            }
        }
    }


    fun setOpenTimeoutDialogFalse(){
        openTimeoutDialog.value = false
    }
    fun setOpenTimeoutDialogTrue(){
        openTimeoutDialog.value = true
    }
    fun setOpenBanDialogFalse(){
        openBanDialog.value = false
    }
    fun setOpenBanDialogTrue(){
        openBanDialog.value = true
    }

    fun changeWarningText(newValue:String){
        warningText.value = newValue
    }
    fun changeOpenWarningDialog(newValue:Boolean){
        openWarningDialog.value = newValue
    }



    fun changeActualTextFieldValue(
        text:String,
        textRange:TextRange
    ){
        textFieldValue.value = TextFieldValue(
            text = text,
            selection = textRange
        )

    }



    /**
     * updateAdvancedChatSettings is used to update the [_advancedChatSettingsState] UI state
     *
     * @param advancedChatSettings the new state that will now represent the [_advancedChatSettingsState] UI state
     */
    fun updateAdvancedChatSettings(advancedChatSettings: AdvancedChatSettings){
        _advancedChatSettingsState.value =advancedChatSettings
    }


    //for right now this has been removed 2024-08-24
    //todo:THIS IS THE MONITORING of the network status


    fun getGlobalChatBadges(
        oAuthToken: String,
        clientId: String,
    ){
        viewModelScope.launch(ioDispatcher) {
            twitchEmoteImpl.getGlobalChatBadges(
                oAuthToken,clientId
            ).collect{

            }
        }

    }

    /***/
    fun getChannelEmotes(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String
    ){
//        Log.d("getGlobalEmotes","oAuthToken-->${oAuthToken}")
//        Log.d("getGlobalEmotes","clientId ->$clientId")
//        Log.d("getGlobalEmotes","broadcasterId ->$broadcasterId")
        //todo: this needs to become a get channel specific emotes
        viewModelScope.launch {
            withContext(ioDispatcher){
                twitchEmoteImpl.getChannelEmotes(
                    oAuthToken,clientId,broadcasterId
                ).mapWithRetry(
                    action={
                        // result is the result from getChannelEmotes()
                            result -> result
                    },
                    predicate = { result, attempt ->
                        val repeatResult = result is Response.Failure && attempt < 3
                        repeatResult
                    }
                ).collect{}
            }
            }

    }

    fun setNoChatMode(status: Boolean){
        _advancedChatSettingsState.value = _advancedChatSettingsState.value.copy(
            noChatMode = status
        )
        if(status){
            webSocket.close()
            listChats.clear()

        }else{
            startWebSocket(channelName.value ?:"")
        }
        viewModelScope.launch(ioDispatcher) {
            delay(200)
            listChats.clear()
        }


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
    private fun filterMessages(messageId: String) {
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
    fun sendDoubleTapEmote(username:String){
        Log.d("SendingDoubleClick","username -->$username")
        if(username.isNotEmpty()){
            webSocket.sendMessage("@$username SeemsGood")
        }
    }


    //TWITCH METHOD
    fun deleteChatMessage(messageId: String) = viewModelScope.launch {
        withContext(ioDispatcher + CoroutineName("DeleteChatMessage")) {
            val isMod = _uiState.value.loggedInUserData?.mod ?: false
            if(isMod){
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
                                banResponseMessage = "Message delete failed"
                            )
                        }
                    }
                }
            }else{
                _uiState.value = _uiState.value.copy(
                    showStickyHeader = true,
                    undoBanResponse = false,
                    banResponseMessage = "Not a moderator"
                )
            }

        }
    }

    //CHAT METHOD
    private fun addChatter(username: String, message: String) {
        if (!allChatters.contains(username)) {
            allChatters.add(username)
        }
    }
    fun clearAllChatters(){
        allChatters.clear()
    }

    //CHAT METHOD
    fun updateClickedChat(
        clickedUsername: String,
        clickedUserId: String,
        banned: Boolean,
        isMod: Boolean
    ) {
        Log.d("updateClickedChat","CLICKED")
        Log.d("updateClickedChat","clickedUsername ->${clickedUsername}")


        clearClickedUsernameChatsDateSent()

        clearAllClickedUserBadgesImmutable()

        val clickedUserChats = listChats.filter { it.displayName == clickedUsername }
        val clickedUserMessages = clickedUserChats.map {
            val scanner = MessageScanner(it.userType?:"")


            /**WHEN*/
            scanner.startScanningTokens()
            ClickedUserNameChats(
                message =it.userType?:"",
                dateSent = it.dateSend,
                messageTokenList = scanner.tokenList
            )
        }
        val badges = clickedUserChats.first().badges

        addAllClickedUserBadgesImmutable(badges)

        addAllClickedUsernameChatsDateSent(clickedUserMessages)
        _clickedUIState.value = _clickedUIState.value.copy(
            clickedUsername = clickedUsername,
            clickedUserId = clickedUserId,
            clickedUsernameBanned = banned,
            clickedUsernameIsMod = isMod
        )

    }

//todo:*******************************************Parsing methods*************************************************

    /**
     * newParsingAgain is function that gets called everytime a user types in the textField chat bar
     *
     * @param textFieldValue  a [TextFieldValue] representing what the user is typing
     * */
    fun newParsingAgain(textFieldValue: TextFieldValue){
        textParsing.parsingMethod(textFieldValue,allChatters.toList())

    }

    /**
     * autoTextChange is function that is used to change the value of [textFieldValue] with [username]
     *
     * @param username  a string meant to represent the username that was clicked on by the user
     * */
    fun autoTextChange(username: String) {
        textParsing.clickUsernameAutoTextChange(
            username =username,
        )
    }
    fun addEmoteToText(emoteText:String){
        textParsing.updateTextField(" $emoteText")
    }


    fun deleteEmote(){

        if (textFieldValue.value.text.isEmpty()) {
            Log.d("DeleteTokenFunc", "string message -> EMPTY")
        } else {
            val cursorIndex = textFieldValue.value.selection.start

            // Ensure that the cursor is not at the beginning of the text
            if (cursorIndex > 0) {
                val newText = textFieldValue.value.text.removeRange(cursorIndex - 1, cursorIndex)
                Log.d("DeleteTokenFunc", "newText -> $newText")

                // Update the TextFieldValue with the new text and adjust the cursor position
                textFieldValue.value = TextFieldValue(
                    text = newText,
                    selection = TextRange(cursorIndex - 1) // Move the cursor back by one position
                )
            }
        }
    }

    /**
     * autoTextChangeCommand is function that is used to change the value of [textFieldValue] with [command]
     *
     * @param command  a string meant to represent the slash command that was clicked on by the user
     * */
    fun clickedCommandAutoCompleteText(command: String) {
        textParsing.clickSlashCommandTextAutoChange(
            command =command,
        )
    }

    //TODO****************************todo:SOCKET RELATED BELOW*******************************************************

    private fun clearAllChatMessages(chatList: SnapshotStateList<TwitchUserData>){
        chatList.clear()
        val data =TwitchUserDataObjectMother
            .addMessageType(MessageType.JOIN)
            .addUserType("Chat cleared by moderator")
            .addColor("#000000")
            .build()

        chatList.add(data)
    }





    fun restartWebSocketFromLongClickMenu(channelName: String){
        startWebSocket(channelName)
    }


    //TODO: SOCKET METHOD
    /**
     * startWebSocket() is a private method meant to be called by methods inside of [StreamViewModel]
     * It is used to start and connect a Websocket using the [TwitchSocket]
     * */
    private fun startWebSocket(channelName: String) = viewModelScope.launch(ioDispatcher) {
        Log.d("startWebSocket", "startWebSocket() is being called")
        val oAuthToken =_uiState.value.oAuthToken



        if(_advancedChatSettingsState.value.noChatMode|| _lowPowerModeActive.value){
            //this is meant to be empty to represent doing nothing and the user being in no chat mode
            //no actions are to be commited in this conditional branch
        }else{

            val username = _uiState.value.login
            webSocket.run(channelName, username,oAuthToken)
            listChats.clear()
        }
    }
    fun sendMessage(chatMessage: String){
        //the scanner should be inside of the tokenCommand. I should be able to just call
        //tokenCommand.checkForSlashCommands(chatMessage) and everything gets done automatically
        // Why do I even tokenCommand.tokenCommand to be a state view?
        val scanner = Scanner(chatMessage)

        scanner.scanTokens()
        val tokenList = scanner.tokenList
       // Log.d("TokenTextCommand","tokenList ->${tokenList}")
        val messageTokenList = tokenList.map { MessageToken(PrivateMessageType.MESSAGE, messageValue = it.lexeme) }
        // todo: I need to test this
       val textCommands = tokenCommand.checkForSlashCommands(tokenList)

        Log.d("TokenTextCommand","text command username ->${textCommands.username}")
        Log.d("TokenTextCommand","type ->${textCommands.javaClass}")


        monitorToken(
            textCommands,
            chatMessage,
            isMod = _uiState.value.loggedInUserData?.mod ?: false,
            addMessageToListChats ={message -> listChats.add(message)},
            messageTokenList=messageTokenList
        )

        textFieldValue.value = TextFieldValue(
            text = "",
            selection = TextRange(0)
        )

    }

    /**
     * This is the function that is responsible for running the commands of the slash commands
     *
     * */
    private fun monitorToken(
        tokenCommand: TextCommands,
        chatMessage:String,
        isMod: Boolean,
        addMessageToListChats:(TwitchUserData)->Unit,
        messageTokenList: List<MessageToken>

    ){

            tokenMonitoring.runMonitorToken(
                tokenCommand = tokenCommand,
                chatMessage = chatMessage,isMod = isMod,
                addMessageToListChats = {message ->addMessageToListChats(message)},
                banUserSlashCommandTest ={ userId, reason ->
                    banUserSlashCommand(userId, reason)
                },
                unbanUserSlashTest={userId ->
                    unBanUserSlashCommand(userId)

                },
                getUserId = {conditional ->
                    listChats.find { conditional(it) }?.userId
                },
                addToMonitorUser={username -> },
                removeFromMonitorUser ={username -> monitoredUsers.remove(username)},
                currentUsername = _uiState.value.loggedInUserData?.displayName?:"",
                sendToWebSocket = {message ->
                    webSocket.sendMessage(message)
                },
                messageTokenList=messageTokenList,
                warnUser = {userId,reason,username ->warnUserSlashCommand(userId,reason,username)}

            )

    }


    /********END OF SOCKET METHODS***********/



    /**
     * updateChannelNameAndClientIdAndUserId is the method that gets called whenever the user clicks on a stream title when
     * they want to navigate to the streamer's page. It updates the ***clientId*** ***broadcasterId*** and ***userId***
     * */
    fun updateChannelNameAndClientIdAndUserId(
        channelName: String,
        clientId: String,
        broadcasterId: String,
        userId: String,
        login:String,
        oAuthToken: String
    ) {
        _uiState.value = _uiState.value.copy(
            oAuthToken = oAuthToken
        )
        Log.d("updateChannelNameAndClientIdAndUserId","broadcasterId --->${broadcasterId}")
        Log.d("updateChannelNameAndClientIdAndUserId","userId --->${userId}")
        Log.d("updateChannelNameAndClientIdAndUserId","oAuthToken --->${_uiState.value.oAuthToken}")

        _channelName.tryEmit(channelName)

        _uiState.value = _uiState.value.copy(
            clientId = clientId,
            broadcasterId = broadcasterId,
            userId = userId,
            login =login
        )

        listChats.clear()

    }





/******************************WARNING USER**********************************************************/
fun warnUser()=viewModelScope.launch(ioDispatcher){

        Log.d("WarningTextExpty","FALSE")
        val warnUserBody = WarnUserBody(
            data = WarnData(
                user_id = _clickedUIState.value.clickedUserId,
                reason = warningText.value
            )
        )
        Log.d("warnUserFunc","oAuthToken ->${_uiState.value.oAuthToken}")
        Log.d("warnUserFunc","clientId ->${_uiState.value.clientId}")
        Log.d("warnUserFunc","userId ->${_uiState.value.userId}")
        Log.d("warnUserFunc","broadcasterId ->${_uiState.value.broadcasterId}")
        Log.d("warnUserFunc","warnUserBody ->${warnUserBody.data}")

        twitchRepoImpl.warnUser(
            oAuthToken = _uiState.value.oAuthToken,
            clientId = _uiState.value.clientId,
            moderatorId = _uiState.value.userId,
            broadcasterId = _uiState.value.broadcasterId,
            body=warnUserBody
        ).collect{response ->
            when(response){
                is Response.Loading->{}
                is Response.Success->{
                    if(response.data){
                        val successMessage = TwitchUserDataObjectMother
                            .addMessageType(MessageType.ANNOUNCEMENT)
                            .addUserType("${_clickedUIState.value.clickedUsername} has been warned")
                            .addSystemMessage("${_clickedUIState.value.clickedUsername} has been warned")
                            .build()
                        listChats.add(successMessage)
                    }else{
                        val successMessage = TwitchUserDataObjectMother
                            .addMessageType(MessageType.NOTICE)
                            .addUserType("Warn command failed due to authentication error. Please login again to be issued a token with proper authentication ")
                            .addSystemMessage("Warn command failed due to authentication error. Please login again to be issued a token with proper authentication ")
                            .build()
                        listChats.add(successMessage)
                    }

                }
                is Response.Failure->{
                    val failedMessage = TwitchUserDataObjectMother
                        .addMessageType(MessageType.NOTICE)
                        .addUserType("Warn command failed. Please try again ")
                        .addSystemMessage("Warn command failed. Please try again ")
                        .build()
                    listChats.add(failedMessage)

                }
            }

    }


}
    private fun warnUserSlashCommand(
        userId:String,reason: String,username:String
    ) = viewModelScope.launch(ioDispatcher){
        val warnUserBody = WarnUserBody(
            data = WarnData(
                user_id = userId,
                reason = reason
            )
        )

        twitchRepoImpl.warnUser(
            oAuthToken = _uiState.value.oAuthToken,
            clientId = _uiState.value.clientId,
            moderatorId = _uiState.value.userId,
            broadcasterId = _uiState.value.broadcasterId,
            body=warnUserBody
        ).collect{response ->
            when(response){
                is Response.Loading->{}
                is Response.Success->{
                    if(response.data){
                        val successMessage = TwitchUserDataObjectMother
                            .addMessageType(MessageType.ANNOUNCEMENT)
                            .addUserType("${username} has been warned")
                            .addSystemMessage("${username} has been warned")
                            .build()
                        listChats.add(successMessage)
                    }else{
                        val successMessage = TwitchUserDataObjectMother
                            .addMessageType(MessageType.NOTICE)
                            .addUserType("/warn command failed due to authentication error. Please login again to be issued a token with proper authentication ")
                            .addSystemMessage("/warn command failed due to authentication error. Please login again to be issued a token with proper authentication ")
                            .build()
                        listChats.add(successMessage)
                    }


                }
                is Response.Failure->{
                    val failedMessage = TwitchUserDataObjectMother
                        .addMessageType(MessageType.NOTICE)
                        .addUserType("/warn command failed. Please try again ")
                        .addSystemMessage("/warn command failed. Please try again ")
                        .build()
                    listChats.add(failedMessage)
                }
            }

        }

    }



    fun timeoutUser() = viewModelScope.launch {
        withContext(ioDispatcher + CoroutineName("TimeoutUser")) {
            val isMod:Boolean = _uiState.value.loggedInUserData?.mod ?: false
            val timeoutUser = BanUser(
                data = BanUserData( //TODO: THIS DATA SHOULD BE PASSED INTO THE METHOD
                    user_id = _clickedUIState.value.clickedUserId,
                    reason = _uiState.value.timeoutReason,
                    duration = _uiState.value.timeoutDuration
                )
            )
            if(isMod){
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
                                banResponseMessage = "Timeout user failed"
                            )
                        }
                    }
                }
            }else{
                _uiState.value = _uiState.value.copy(
                    showStickyHeader = true,
                    undoBanResponse = false,
                    timeoutUserError = true,
                    banResponseMessage = "Not a moderator"
                )
                delay(2000)
                _uiState.value = _uiState.value.copy(
                    timeoutUserError = false,
                )
                val message = TwitchUserDataObjectMother
                    .addUserType("You are not a moderator in this chat. You do not have the proper permissions for this command")
                    .addColor("#BF40BF")
                    .addDisplayName("System message")
                    .addMod("mod")
                    .addSystemMessage("")
                    .addMessageType(MessageType.ERROR)
                    .build()
                listChats.add(message)
            }
            }

    }



    private fun unBanUserSlashCommand(userId: String) = viewModelScope.launch{
        withContext(ioDispatcher + CoroutineName("UnBanUser")) {
            twitchRepoImpl.unBanUser(
                oAuthToken = _uiState.value.oAuthToken,
                clientId = _uiState.value.clientId,
                moderatorId = _uiState.value.userId,
                broadcasterId = _uiState.value.broadcasterId,
                userId = userId //TODO:PASS IT IN

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

                        val unBanSuccessMessage =TwitchUserDataObjectMother.addColor("#FFBB86FC")
                            .addDisplayName("Room update")
                            .addUserType("Unban successful")
                            .addMessageType(MessageType.NOTICE)
                            .build()
                        listChats.add(unBanSuccessMessage)
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
    private fun banUserSlashCommand(userId: String, reason:String){
        Log.d("banUserSlashCommand","oAuthToken-->  ${_uiState.value.oAuthToken}")
        Log.d("banUserSlashCommand","clientId-->${_uiState.value.clientId}")
        Log.d("banUserSlashCommand","userId -->${_uiState.value.userId}")
        Log.d("banUserSlashCommand","broadcasterId -->${_uiState.value.broadcasterId}")
        viewModelScope.launch {
            withContext(ioDispatcher + CoroutineName("BanUser")) {

                val banUserNew = BanUser(
                    data = BanUserData( //TODO:SHOULD BE PASSED IN
                        user_id =userId,
                        reason = reason,
                        duration = 0

                    )
                )

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
                            val unBanSuccessMessage =TwitchUserDataObjectMother.addColor("#FFBB86FC")
                                .addDisplayName("Room update")
                                .addUserType("Ban successful")
                                .addMessageType(MessageType.NOTICE)
                                .build()
                            listChats.add(unBanSuccessMessage)
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

    }

    //TODO: TWICH METHOD
    fun banUser() = viewModelScope.launch {
        val banUserNew = BanUser(
            data = BanUserData( //TODO:SHOULD BE PASSED IN
                user_id =_clickedUIState.value.clickedUserId,
                reason = _uiState.value.banReason,
                duration = _uiState.value.banDuration

            )
        )
        Log.d("deleteChatMessageException", "PbanUser.user_id---> ${banUserNew.data.user_id}")
        // Log.d("deleteChatMessageException", "clickedUserId ${clickedUserId}")
        val isMod:Boolean = _uiState.value.loggedInUserData?.mod ?: false
        withContext(ioDispatcher + CoroutineName("BanUser")) {
            if(isMod){
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
                                banResponseMessage = "Ban attempt unsuccessful"
                            )
                        }
                    }
                }
            }else{
                _uiState.value = _uiState.value.copy(
                    showStickyHeader = true,
                    undoBanResponse = false,
                    banUserError=true,
                    banResponseMessage = "Not a moderator"
                )
                delay(2000)
                _uiState.value = _uiState.value.copy(
                    banUserError=false,

                )
            }

        }
    }
    //TODO: DOES THIS EVEN WORK WITHOUT _idOfLatestBan
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

                        val unBanSuccessMessage =TwitchUserDataObjectMother.addColor("#FFBB86FC")
                            .addDisplayName("Room update")
                            .addUserType("Unban successful")
                            .addMessageType(MessageType.NOTICE)
                            .build()
                        listChats.add(unBanSuccessMessage)
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


    override fun onCleared() {
        super.onCleared()
        webSocket.close()
    }

}


