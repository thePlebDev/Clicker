package com.example.clicker.presentation.stream

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getSelectedText
import androidx.compose.ui.text.input.getTextAfterSelection
import androidx.compose.ui.text.input.getTextBeforeSelection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.example.clicker.R
import com.example.clicker.domain.TwitchDataStore
import com.example.clicker.network.clients.BanUser
import com.example.clicker.network.clients.BanUserData
import com.example.clicker.network.clients.IndivBetterTTVEmote
import com.example.clicker.network.clients.WarnData
import com.example.clicker.network.clients.WarnUserBody
import com.example.clicker.network.domain.TwitchEmoteRepo
import com.example.clicker.network.domain.TwitchStream
import com.example.clicker.network.models.twitchStream.ChatSettingsData
import com.example.clicker.network.models.twitchStream.UpdateChatSettings
import com.example.clicker.network.websockets.MessageType
import com.example.clicker.network.domain.TwitchSocket
import com.example.clicker.network.models.websockets.LoggedInUserData
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.network.repository.BetterTTVEmotesImpl
import com.example.clicker.network.repository.EmoteNameUrl
import com.example.clicker.network.repository.EmoteNameUrlList
import com.example.clicker.network.repository.EmoteNameUrlNumber
import com.example.clicker.network.repository.EmoteNameUrlNumberList
import com.example.clicker.network.repository.TwitchEmoteImpl
import com.example.clicker.network.websockets.MessageToken
import com.example.clicker.network.websockets.PrivateMessageType
import com.example.clicker.network.websockets.TwitchEventSubWebSocket


import com.example.clicker.presentation.stream.util.NetworkMonitoring
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChattingUser(
    val username: String,
    val message: String
)

data class EmoteBoardData(
    val height:Int,
    val showBoard:Boolean
)
data class ClickedUserNameChats(
    val dateSent:String,
    val message:String
)

@Immutable
data class TextFieldValueImmutable(
    val textFieldValue: TextFieldValue
)
@Immutable
data class ClickedUsernameChatsWithDateSentImmutable(
    val clickedChats:List<ClickedUserNameChats>
)



/**
 * ChatSettings holds all the data representing the current mod related chat settings
 * */
data class ModChatSettings(
    val showChatSettingAlert: Boolean = false,
    val showUndoButton:Boolean = false,
    val data: ChatSettingsData = ChatSettingsData(
        slowMode = false,slowModeWaitTime = null,
        followerMode = false, followerModeDuration =null ,
        subscriberMode = false,emoteMode=false
    ),

    val switchesEnabled:Boolean = true
)
/**
 * AdvancedChatSettings holds all the data representing the current advanced settings relating to the chat messages
 *
 * @param noChatMode a boolean determining if the user should be shown the chat messages or not
 * @param showSubs a boolean determining if the user should be shown subscription messages or not
 * @param showReSubs a boolean determining if the user should be shown re-subscription messages or not
 * @param showAnonSubs a boolean determining if the user should be shown anonymous subscription messages or not
 * @param showGiftSubs a boolean determining if the user should be shown gift subscription messages or not
 * */
data class AdvancedChatSettings(
    val noChatMode:Boolean = false,
    val showSubs:Boolean = true,
    val showReSubs:Boolean = true,
    val showAnonSubs:Boolean = true,
    val showGiftSubs:Boolean = true,
)
data class StreamUIState(
    val chatSettings: Response<ChatSettingsData> = Response.Loading, //websocket twitchImpl
    val loggedInUserData: LoggedInUserData? = null, //websocket

    val clientId: String = "", //twitchRepoImpl
    val broadcasterId: String = "", //twitchRepoImpl
    val userId: String = "", //twitchRepoImpl. This is also the moderatorId
    val login:String="",
    val oAuthToken: String = "", //twitchRepoImpl


    val oneClickActionsChecked:Boolean = true,
    val noChatMode:Boolean = false,
    val timeoutUserError:Boolean = false,
    val banUserError:Boolean = false,

    val banDuration: Int = 0, //twitchRepoImpl
    val banReason: String = "", //twitchRepoImpl
    val timeoutDuration: Int = 60, //twitchRepoImpl
    val timeoutReason: String = "", //twitchRepoImpl
    val banResponse: Response<Boolean> = Response.Success(false), //twitchRepoImpl
    val banResponseMessage: String = "", //twitchRepoImpl
    val undoBanResponse: Boolean = false, //twitchRepoImpl
    val showStickyHeader: Boolean = false, //twitchRepoImpl

    val chatSettingsFailedMessage: String = "",
    val networkStatus:Boolean? = null,
)
data class ClickedUIState(
    val clickedUsername:String ="", //websocket
    val clickedUserId: String ="",
    val clickedUsernameBanned: Boolean=false,
    val clickedUsernameIsMod:Boolean =false,
    val shouldMonitorUser:Boolean = false,
)

data class ClickedStreamInfo(
    val channelName: String ="",
    val streamTitle:String ="",
    val category:String="",
    val tags:List<String> = listOf(),
    val adjustedUrl:String=""
)


@HiltViewModel
class StreamViewModel @Inject constructor(
    private val webSocket: TwitchSocket,
    private val tokenDataStore: TwitchDataStore,
    private val twitchRepoImpl: TwitchStream,
    private val ioDispatcher: CoroutineDispatcher,
    private val autoCompleteChat: AutoCompleteChat,
    private val networkMonitoring: NetworkMonitoring,
    private val twitchEmoteImpl: TwitchEmoteRepo,
    private val betterTTVEmotesImpl: BetterTTVEmotesImpl,
    private val textParsing:TextParsing = TextParsing(),
    private val tokenMonitoring: TokenMonitoring= TokenMonitoring(),
    private val tokenCommand: TokenCommand =TokenCommand(),
) : ViewModel() {




    /**
     * The name of the channel that this chat is connecting to
     * */
    private val _channelName: MutableStateFlow<String?> = MutableStateFlow(null)
    val channelName: StateFlow<String?> = _channelName

    private val _clientId: MutableState<String?> = mutableStateOf(null)
    val clientId: State<String?> = _clientId

    private val _emoteBoardData: MutableState<EmoteBoardData> = mutableStateOf(EmoteBoardData(200,false))
    val emoteBoardData: State<EmoteBoardData> = _emoteBoardData


    /********THIS IS ALL THE EMOTE RELATED CALLS**************************************/
    val inlineTextContentTest = twitchEmoteImpl.emoteList
    val globalEmoteUrlList = twitchEmoteImpl.emoteBoardGlobalList
    val channelEmoteUrlList = twitchEmoteImpl.emoteBoardChannelList

    private val _globalBetterTTVEmotes: MutableState<Response<List<IndivBetterTTVEmote>>> = mutableStateOf(Response.Loading)




    val globalBetterTTVEmotes=twitchEmoteImpl.globalBetterTTVEmotes
    val channelBetterTTVEmote = twitchEmoteImpl.channelBetterTTVEmotes
    val sharedChannelBetterTTVEmote = twitchEmoteImpl.sharedBetterTTVEmotes


    /**
     * A list representing all the most recent clicked emotes
     * */
    //todo: mutableStateOf<EmoteNameUrlList>(EmoteNameUrlList())
    val mostFrequentEmoteList = mutableStateListOf<EmoteNameUrl>()
    val mostFrequentEmoteListTesting = mutableStateOf(EmoteNameUrlNumberList())


    fun updateMostFrequentEmoteList(clickedItem:EmoteNameUrl){
        mostFrequentEmoteList.add(clickedItem)
    }
    @SuppressLint("LongLogTag")
    fun updateMostFrequentEmoteListTesting(clickedItem:EmoteNameUrl){
        val oldList = mostFrequentEmoteListTesting.value.list.toMutableList()
        val newClickedItem = EmoteNameUrlNumber(clickedItem.name,clickedItem.url,1)
        if(oldList.size ==12){

        }else{
            val foundItem =oldList.find { it.name == clickedItem.name }
            if(foundItem!= null){
                val foundIndex = oldList.indexOf(foundItem)
                val timesClicked =foundItem.timesClicked +1
                oldList[foundIndex] =EmoteNameUrlNumber(clickedItem.name,clickedItem.url,timesClicked)
                mostFrequentEmoteListTesting.value = mostFrequentEmoteListTesting.value.copy(
                    list =oldList
                )
            }else{
                val newList = oldList + listOf(newClickedItem)
                mostFrequentEmoteListTesting.value = mostFrequentEmoteListTesting.value.copy(
                    list =newList
                )
            }

        }
        Log.d("updateMostFrequentEmoteListTestingUpdate","list->${mostFrequentEmoteListTesting.value.list}")
        Log.d("updateMostFrequentEmoteListTestingUpdate","newUpdate->${mostFrequentEmoteListTesting.value.list[0].timesClicked}")

    }
    /**
     * A list representing all the chats users have sent
     * */
    val listChats = mutableStateListOf<TwitchUserData>()

    /**
     * A list representing all the actions taken by moderators
     * */
    val modActionList= mutableStateListOf<TwitchUserData>()


    private var _uiState: MutableState<StreamUIState> = mutableStateOf(StreamUIState())
    val state: State<StreamUIState> = _uiState

    private val _clickedUIState = mutableStateOf(ClickedUIState())
    val clickedUIState = _clickedUIState

    //all the related chat settings code
    //todo: I think this can get deleted
    private val _modChatSettingsState = mutableStateOf(ModChatSettings())
    val modChatSettingsState = _modChatSettingsState

    private val _clickedStreamInfo = mutableStateOf(ClickedStreamInfo())
    val clickedStreamInfo = _clickedStreamInfo

    fun updateClickedStreamInfo(clickedStreamInfo:ClickedStreamInfo){
        //todo: need to do some adjusting for the thumbnail url
        _clickedStreamInfo.value =clickedStreamInfo
        _channelName.value = clickedStreamInfo.channelName
    }
    private val _showAutoModSettings = mutableStateOf(false)
    val showAutoModSettings = _showAutoModSettings

    fun setAutoModSettings(show:Boolean){
        _showAutoModSettings.value = show
    }
    fun updateStreamTitle(newStreamTitle:String){
        _clickedStreamInfo.value = _clickedStreamInfo.value.copy(
            streamTitle = newStreamTitle
        )
    }

    //todo: THIS IS THE CODE FOR THE etterTTVEmotesImpl.getGlobalEmotes()
    fun getBetterTTVGlobalEmotes(){
        viewModelScope.launch(Dispatchers.IO) {
            twitchEmoteImpl.getBetterTTVGlobalEmotes().collect{response ->

                when(response){
                    is Response.Loading ->{
                        _globalBetterTTVEmotes.value = Response.Loading
                    }
                    is Response.Success ->{
                        _globalBetterTTVEmotes.value = response
                    }
                    is Response.Failure ->{
                        _globalBetterTTVEmotes.value = Response.Failure(Exception("Failed"))
                    }
                }

            }
        }
    }
    fun getBetterTTVChannelEmotes(broadcasterId: String){
        Log.d("getBetterTTVChannelEmotes", "broadcasterId ->$broadcasterId")
        viewModelScope.launch(Dispatchers.IO) {
            twitchEmoteImpl.getBetterTTVChannelEmotes(broadcasterId).collect{response ->

            }
        }
    }



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



    val openWarningDialog =mutableStateOf(false)
    val warningTextIsEmpty =mutableStateOf(false)
    val warningText = mutableStateOf("")
    fun changeWarningText(newValue:String){
        warningText.value = newValue
    }
    fun changeOpenWarningDialog(newValue:Boolean){
        openWarningDialog.value = newValue
    }
    /***
     * the immutable TextFieldValue
     */
    val textFieldValueImmutable:MutableState<TextFieldValueImmutable> = mutableStateOf(TextFieldValueImmutable(textParsing.textFieldValue.value))



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
     * A list of Strings that represents the list of users that are being searched when the user enters the ***@***
     * into the text box
     * */
    var filteredChatList:SnapshotStateList<String> = textParsing.filteredChatList
    val filteredChatListImmutable = textParsing.filteredChatListImmutable

    val forwardSlashCommandImmutable = textParsing.forwardSlashCommandsState

    fun clearFilteredChatterList(){
        textParsing.clearFilteredChatterList()
    }




    val clickedUsernameChatsWithDateSent = mutableStateListOf<ClickedUserNameChats>()
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
     val shouldMonitorUser:State<Boolean>
        get() = mutableStateOf(monitoredUsers.contains(_clickedUIState.value.clickedUsername))







    /**
     * updateAdvancedChatSettings is used to update the [_advancedChatSettingsState] UI state
     *
     * @param advancedChatSettings the new state that will now represent the [_advancedChatSettingsState] UI state
     */
    fun updateAdvancedChatSettings(advancedChatSettings: AdvancedChatSettings){
        _advancedChatSettingsState.value =advancedChatSettings
    }
    fun updateShouldMonitorUser(){
        val clickedUsername = _clickedUIState.value.clickedUsername
        val alreadyMonitored =monitoredUsers.contains(clickedUsername)

        if(alreadyMonitored){
            monitoredUsers.remove(clickedUsername)
        }else{
            monitoredUsers.add(clickedUsername)
        }

    }
    fun callingThePrivateMethodFromView(a:Int,b:Int){
        privateMethod(a,b)
    }
    private fun privateMethod(a:Int,b:Int): Int{
        //pretend this is advanced logic
        val answer =(a/b) * 45 -9
        return answer
    }


    init{
//        getAutoModStatus()
    }
    val another ="Disconnected from chat. Check internet connection. Click button to attempt reconnect. If issue persists, your token may be expired and you have to logout to be issued a new one"

    val errorValue = TwitchUserDataObjectMother
        .addColor("#FF0000")
        .addDisplayName("Connection Error")
        .addMessageType(MessageType.ERROR)
        .addUserType(
            "Disconnected from chat."
        )
        .build()
    val noInternetErrorValue = TwitchUserDataObjectMother
        .addColor("#FF0000")
        .addDisplayName("Connection Error")
        .addMessageType(MessageType.ERROR)
        .addUserType(
            "Disconnected from chat. Please check network and try again"
        )
        .build()
    init{
        viewModelScope.launch {
            webSocket.hasWebSocketFailed.collect{nullableValue ->
                nullableValue?.also { value ->
                    if(value && _uiState.value.networkStatus == true){
                        //todo: CHECK IF THERE IS AN INTERNET CONNECTION
                        listChats.add(noInternetErrorValue)
                    }else{
                        listChats.add(errorValue)
                    }
                }

            }
        }
    }

    //todo:THIS IS THE MONITORING of the network status
    init{
        viewModelScope.launch {
            networkMonitoring.networkStatus.collect{nullableValue ->
                nullableValue?.also{nonNullableValue ->
                    _uiState.value = _uiState.value.copy(
                        networkStatus = nonNullableValue
                    )
                    if(nullableValue){
                        val username =_uiState.value.login
                        val channelName = _channelName.value?:""
                        webSocket.run(channelName, username)
                    }

                }

            }
        }
    }

    fun getGlobalChatBadges(
        oAuthToken: String,
        clientId: String,
    ){
        viewModelScope.launch(Dispatchers.IO) {
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
        Log.d("getGlobalEmotes","oAuthToken-->${oAuthToken}")
        Log.d("largeChatLoading","getGlobalEmotes")
        //todo: this needs to become a get channel specific emotes
        viewModelScope.launch {
            withContext(Dispatchers.IO){
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






    /**
     * showUndoButton() is function used by [SettingsSwitches][com.example.clicker.presentation.stream.views.ChatSettingsContainer.SettingsSwitches]
     * composable to hide or show the [DraggableUndoButton][com.example.clicker.presentation.stream.views.MainChat.MainChatParts.DraggableUndoButton]
     *
     * @param status a boolean representing the current state of the switch being clicked
     * */
    fun showUndoButton(status:Boolean){

        _modChatSettingsState.value = _modChatSettingsState.value.copy(
            showUndoButton = status
        )
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
        viewModelScope.launch {
            delay(200)
            listChats.clear()
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
    fun addChatter(username: String, message: String) {
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



       // clickedUsernameChatsWithDateSent.clear()
        clearClickedUsernameChatsDateSent()
        val messages = listChats.filter { it.displayName == clickedUsername }
            .map { "${it.dateSend} " +if (it.deleted)  it.userType!! + " (deleted by mod)" else it.userType!!   }


        val clickedUserChats = listChats.filter { it.displayName == clickedUsername }
        val clickedUserMessages = clickedUserChats.map {
            ClickedUserNameChats(
                message =it.userType?:"",
                dateSent = it.dateSend
            )
        }



        //clickedUsernameChatsWithDateSent.addAll(clickedUserMessages)
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
        textParsing.updateTextField(" $emoteText ")
    }
    fun deleteEmote(){
        Log.d("addToken","deleteEmote()")
        textParsing.deleteEmote(inlineTextContentTest.value.map)
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

    fun clearAllChatMessages(chatList: SnapshotStateList<TwitchUserData>){
        chatList.clear()
        val data =TwitchUserDataObjectMother
            .addMessageType(MessageType.JOIN)
            .addUserType("Chat cleared by moderator")
            .addColor("#000000")
            .build()

        chatList.add(data)
    }
    //this is the culprit
    fun notifyChatOfBanTimeoutEvent(chatList: SnapshotStateList<TwitchUserData>, message: String?){
        val data = TwitchUserDataObjectMother
            .addMessageType(MessageType.CLEARCHAT)
            .addUserType(message)
            .addColor("#000000")
            .build()
        chatList.add(data)
    }




    /**THis is the data for the new filter methods*/
    private val _idOfLatestBan = mutableStateOf("")

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
        //TODO: SOCKET METHOD
        monitorForChannelName()
    }
    init {
        //TODO: SOCKET METHOD

        monitorSocketRoomState()


    }
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
     * This is meant to monitor of the latest ban/timeout messages
     *
     * */

    private fun monitorForLatestBannedUserId(){
        viewModelScope.launch {
            webSocket.latestBannedUserId.collect{latestBannedId ->
                latestBannedId?.also{
                    Log.d("latestBannedId", "latestBannedId --> ${latestBannedId}")
                    _idOfLatestBan.value = latestBannedId
                }

            }
        }
    }


    //this function is used heavily to determine if the user is a moderator or not
    private fun monitorForLoggedInUserData(){
        viewModelScope.launch {
            webSocket.loggedInUserUiState.collect {nullableLoggedInData ->
                nullableLoggedInData?.let {LoggedInData ->
                    _uiState.value = _uiState.value.copy(
                        loggedInUserData = LoggedInData
                    )
                }
            }
        }
    }

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
        viewModelScope.launch {
            webSocket.state.collect { twitchUserMessage ->
                Log.d("loggedMessage", " tmiSentTs --> ${twitchUserMessage.tmiSentTs}")
                Log.d("twitchUserMessage", " messageType --> ${twitchUserMessage.messageType}")
                Log.d("twitchUserMessage", " twitchUserMessage --> ${twitchUserMessage}")
                Log.d("twitchUserMessage", "-----------------------------------------------------")
                Log.d("twitchUserMessageTesting", "displayName ->${twitchUserMessage.displayName}")
                Log.d("twitchUserMessageTesting", "clickedUsername ->${_clickedUIState.value.clickedUsername}")
                Log.d("twitchUserMessageTesting", "equal ->${twitchUserMessage.displayName == _clickedUIState.value.clickedUsername}")

                if (twitchUserMessage.displayName == _clickedUIState.value.clickedUsername) {


//                    clickedUsernameChatsWithDateSent.add(
//                        ClickedUserNameChats(
//                            message =twitchUserMessage.userType?:"",
//                            dateSent = twitchUserMessage.dateSend
//                        )
//                    )
                    addAllClickedUsernameChatsDateSent(
                        listOf(
                            ClickedUserNameChats(
                                message =twitchUserMessage.userType?:"",
                                dateSent = twitchUserMessage.dateSend
                            )
                        )
                    )

                }
                if(monitoredUsers.contains(twitchUserMessage.displayName)){
                    //twitchUserMessage.isMonitored = true
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
                        autoCompleteChat.addChatter(twitchUserMessage.displayName!!)
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


    fun restartWebSocketFromLongClickMenu(channelName: String){
        startWebSocket(channelName)
    }

    //TODO: SOCKET METHOD
    fun restartWebSocket() {
        val channelName = _channelName.value ?: ""
        Log.d("startWebSocket", "websocket is starting")
        startWebSocket(channelName)
    }

    //TODO: SOCKET METHOD
    /**
     * startWebSocket() is a private method meant to be called by methods inside of [StreamViewModel]
     * It is used to start and connect a Websocket using the [TwitchSocket]
     * */
    private fun startWebSocket(channelName: String) = viewModelScope.launch {
        Log.d("startWebSocket", "startWebSocket() is being called")

        if(_advancedChatSettingsState.value.noChatMode){
            //this is meant to be empty to represent doing nothing and the user being in no chat mode
            //no actions are to be commited in this conditional branch
        }else{

            val username = _uiState.value.login
            webSocket.run(channelName, username)
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

        // val messageResult = webSocket.sendMessage(chatMessage)
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
                addToMonitorUser={username -> monitoredUsers.add(username)},
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
        login:String
    ) {
        Log.d("updateChannelNameAndClientIdAndUserId","broadcasterId --->${broadcasterId}")
        _channelName.tryEmit(channelName)
        //startWebSocket(channelName)

        _uiState.value = _uiState.value.copy(
            clientId = clientId,
            broadcasterId = broadcasterId,
            userId = userId,
            login =login
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


    /**
     * getChatSettings() is a private function used by [updateChannelNameAndClientIdAndUserId] and [retryGettingChatSetting] to
     * get the chat settings of the current channel the viewer is viewing
     * */
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
                                _modChatSettingsState.value = _modChatSettingsState.value.copy(
                                    switchesEnabled = false
                                )
                            }
                            is Response.Success -> {

                                val chatSettingsData = response.data.data[0]
                                _modChatSettingsState.value = _modChatSettingsState.value.copy(
                                    data = chatSettingsData,
                                    switchesEnabled = true
                                )
                            }
                            is Response.Failure -> {

                                _modChatSettingsState.value = _modChatSettingsState.value.copy(
                                    switchesEnabled = true,
                                    showChatSettingAlert = true

                                )
                            }
                        }
                    }
                }
            }
        }

    }

    fun closeSettingsAlertHeader(){
        _modChatSettingsState.value = _modChatSettingsState.value.copy(
            showChatSettingAlert = false
        )
    }

    //below is what I am building to create the new generic chat settings switch function
    /**
     * toggleChatSettings() is a generic function that is called when a [Switch][androidx.compose.material.Switch] inside of [ChatSettingsContainer][com.example.clicker.presentation.stream.views.ChatSettingsContainer]
     * is clicked. Once triggered this function will send a request to the Twitch server and attempt to change the current channel's chat settings
     *
     *
     * @param checkedBoolean a boolean that represents the current [Switch's][androidx.compose.material.Switch] state. True for clicked and false for not clicked
     * @param switchType is a Enum of type [ChatSettingsContainer.SwitchTypes] and represents which switch got toggled
     * */
    fun toggleChatSettings(chatSettingsData: ChatSettingsData){
        _modChatSettingsState.value = _modChatSettingsState.value.copy(
            switchesEnabled = false,
            showChatSettingAlert = false
        )
        // then we make the request
        // if the request is a success update the chatSettingsData, set switchesEnabled = true
        // if the request fails, do nothing except, switchesEnabled = true,showChatSettingAlert=true
        updateDateChatSettings(chatSettingsData)

    }


    /**
     * updateDateChatSettings() is a private function called by [toggleChatSettings] and it is making the actual request
     * to the Twitch server to update the channels chat settings
     *
     * @param updatedChatSettings this object represents the updated settings it is sending to the Twitch servers. The object
     * will differ depending on SwitchType passed to the [toggleChatSettings] function
     *
     * */
    private fun updateDateChatSettings(
        chatSettingsData: ChatSettingsData,
    ) = viewModelScope.launch{


        withContext(ioDispatcher + CoroutineName("updateDateChatSettings")) {
            twitchRepoImpl.updateChatSettings(
                oAuthToken = _uiState.value.oAuthToken,
                clientId = _uiState.value.clientId,
                moderatorId = _uiState.value.userId,
                broadcasterId = _uiState.value.broadcasterId,
                body = UpdateChatSettings(
                    emote_mode = chatSettingsData.emoteMode,
                    follower_mode = chatSettingsData.followerMode,
                    slow_mode = chatSettingsData.slowMode,
                    subscriber_mode = chatSettingsData.subscriberMode
                )
            ).collect { response ->
                when (response) {
                    is Response.Loading -> {
                        Log.d("changeChatSettings", "LOADING")
                    }
                    is Response.Success -> {

                        _modChatSettingsState.value = _modChatSettingsState.value.copy(
                            data = chatSettingsData,
                            switchesEnabled = true,
                        )

                    }
                    is Response.Failure -> {
                        Log.d("changeChatSettings", "FAILED -> ${response.e.message}")
                        _modChatSettingsState.value = _modChatSettingsState.value.copy(
                            showChatSettingAlert = true,
                            switchesEnabled = true,
                        )

                    }
                }
            }
        }

    }
/******************************WARNING USER**********************************************************/
fun warnUser()=viewModelScope.launch(Dispatchers.IO){

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
    ) = viewModelScope.launch(Dispatchers.IO){
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
    fun setTimeoutUserError(timeoutUserError:Boolean){
        _uiState.value = _uiState.value.copy(
            timeoutUserError = timeoutUserError,
        )
    }
    fun setBanUserError(banUserError: Boolean){
        _uiState.value = _uiState.value.copy(
            banUserError = banUserError,
        )
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
    //TODO: TWICH METHOD
    fun unBanUser() = viewModelScope.launch {
        withContext(ioDispatcher + CoroutineName("UnBanUser")) {
            twitchRepoImpl.unBanUser(
                oAuthToken = _uiState.value.oAuthToken,
                clientId = _uiState.value.clientId,
                moderatorId = _uiState.value.userId,
                broadcasterId = _uiState.value.broadcasterId,
                userId = _idOfLatestBan.value //TODO:PASS IT IN

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


