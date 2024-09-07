package com.example.clicker.presentation.stream.views.chat

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.network.repository.models.EmoteListMap
import com.example.clicker.network.repository.models.EmoteNameUrl
import com.example.clicker.network.repository.models.EmoteNameUrlEmoteTypeList
import com.example.clicker.network.repository.models.EmoteNameUrlList
import com.example.clicker.network.repository.models.IndivBetterTTVEmoteList

import com.example.clicker.presentation.stream.util.FilteredChatterListImmutableCollection
import com.example.clicker.presentation.stream.util.ForwardSlashCommands
import com.example.clicker.presentation.stream.util.ForwardSlashCommandsImmutableCollection
import com.example.clicker.util.Response
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



@Composable
fun FullChatModView(
    twitchUserChat: List<TwitchUserData>,
    showBottomModal:()->Unit,
    updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
    showTimeoutDialog:()->Unit,
    showBanDialog:()->Unit,
    doubleClickMessage:(String)->Unit,
    //below is what is needed for the chat UI
    textFieldValue: MutableState<TextFieldValue>,
    clickedAutoCompleteText: (String) -> Unit,
    isMod: Boolean,
    sendMessageToWebSocket: (String) -> Unit,
    showModal: () -> Unit,
    showOuterBottomModalState:() ->Unit,
    newFilterMethod:(TextFieldValue) ->Unit,
    orientationIsVertical:Boolean,
    notificationAmount:Int,
    noChat:Boolean,
    deleteChatMessage:(String)->Unit,
    clickedCommandAutoCompleteText: (String) -> Unit,
    badgeListMap: EmoteListMap,
    hideSoftKeyboard:()-> Unit,
    emoteBoardGlobalList: EmoteNameUrlList,
    emoteBoardChannelList: EmoteNameUrlEmoteTypeList,
    emoteBoardMostFrequentList: EmoteNameUrlList,
    updateMostFrequentEmoteList:(EmoteNameUrl)->Unit,
    updateTextWithEmote:(String) ->Unit,
    deleteEmote:()->Unit,
    showModView:()->Unit,
    fullMode: Boolean,
    setDragging: () -> Unit,
    globalBetterTTVEmotes: IndivBetterTTVEmoteList,
    channelBetterTTVResponse: IndivBetterTTVEmoteList,
    sharedBetterTTVResponse: IndivBetterTTVEmoteList,
    userIsSub:Boolean,
    forwardSlashes: ForwardSlashCommandsImmutableCollection,
    filteredChatListImmutable: FilteredChatterListImmutableCollection,
    actualTextFieldValue:TextFieldValue,
    changeActualTextFieldValue:(String, TextRange)->Unit,
    usernameSize:Float,
    messageSize:Float,
    lineHeight:Float,
    useCustomUsernameColors:Boolean,
    globalTwitchEmoteContentMap:EmoteListMap,
    channelTwitchEmoteContentMap:EmoteListMap,
    globalBetterTTVEmoteContentMap: EmoteListMap,
    channelBetterTTVEmoteContentMap: EmoteListMap,
    sharedBetterTTVEmoteContentMap: EmoteListMap,
    lowPowerMode:Boolean,

    ){
    val lazyColumnListState = rememberLazyListState()
    var autoscroll by remember { mutableStateOf(true) }
    val emoteKeyBoardHeight = remember { mutableStateOf(0.dp) }
    var iconClicked by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    ChatUIBox(
        determineScrollState={
            DetermineScrollState(
                lazyColumnListState = lazyColumnListState,
                setAutoScrollFalse = { autoscroll = false },
                setAutoScrollTrue = { autoscroll = true },
            )
        },
        chatUI={modifier ->
            ChatUILazyColumn(
                lazyColumnListState=lazyColumnListState,
                twitchUserChat=twitchUserChat,
                autoscroll=autoscroll,
                showBottomModal={showBottomModal()},
                showTimeoutDialog={showTimeoutDialog()},
                showBanDialog={showBanDialog()},
                updateClickedUser = {  username, userId,isBanned,isMod ->
                    updateClickedUser(
                        username,
                        userId,
                        isBanned,
                        isMod
                    )
                },
                doubleClickMessage={username ->doubleClickMessage(username)},
                modifier=modifier,
                deleteChatMessage={messageId ->deleteChatMessage(messageId)},
                isMod = isMod,
                fullMode = fullMode,
                setDragging ={setDragging()},
                badgeListMap=badgeListMap,
                usernameSize=usernameSize,
                messageSize=messageSize,
                lineHeight=lineHeight,
                useCustomUsernameColors=useCustomUsernameColors,
                globalTwitchEmoteContentMap=globalTwitchEmoteContentMap,
                channelTwitchEmoteContentMap=channelTwitchEmoteContentMap,
                globalBetterTTVEmoteContentMap=globalBetterTTVEmoteContentMap,
                channelBetterTTVEmoteContentMap=channelBetterTTVEmoteContentMap,
                sharedBetterTTVEmoteContentMap=sharedBetterTTVEmoteContentMap,

            )
        },
        scrollToBottom ={modifier ->
            ScrollToBottom(
                scrollingPaused = !autoscroll,
                enableAutoScroll = { autoscroll = true },
                emoteKeyBoardHeight =emoteKeyBoardHeight.value,
                modifier = modifier
            )
        },
        enterChat = {modifier ->
            EnterChatColumn(
                modifier = modifier,
                filteredRow = {
                    FilteredMentionLazyRow(
                        clickedAutoCompleteText = { username ->
                            clickedAutoCompleteText(
                                username
                            )
                        },
                        filteredChatListImmutable=filteredChatListImmutable
                    )
                },
                showModStatus = {
                    ShowModStatus(
                        modStatus =isMod,
                        orientationIsVertical =orientationIsVertical,
                        notificationAmount=notificationAmount,
                        showModView={showModView()}
                    )
                },
                stylizedTextField ={boxModifier ->
                    StylizedTextField(
                        modifier = boxModifier,
                       // textFieldValue = textFieldValue,
                        newFilterMethod = {newTextValue ->newFilterMethod(newTextValue)},
                        showEmoteBoard = {
                            hideSoftKeyboard()
//                            scope.launch { //todo: this is what causes a recomposition
//                                delay(100)
                                emoteKeyBoardHeight.value = 350.dp
//                            }
                        },
                        showKeyBoard = {
                            emoteKeyBoardHeight.value = 0.dp
                        },
                        iconClicked=iconClicked,
                        setIconClicked = {newValue -> iconClicked = newValue},
                        actualTextFieldValue =actualTextFieldValue,
                        changeActualTextFieldValue={text,textRange ->changeActualTextFieldValue(text,textRange)},

                    )
                },
                showIconBasedOnTextLength ={
                    ShowIconBasedOnTextLength(
                        textFieldValue =textFieldValue,
                        chat = {item ->

                            hideSoftKeyboard()
                            emoteKeyBoardHeight.value = 0.dp// closes emote board
                            iconClicked = false // changes the icon
                            sendMessageToWebSocket(item)
                               },
                        showModal ={showModal()},
                    )
                },
            )
        },
        noChat=noChat,
        clickedCommandAutoCompleteText={clickedValue -> clickedCommandAutoCompleteText(clickedValue)},
        emoteKeyBoardHeight =emoteKeyBoardHeight.value,
        emoteBoardGlobalList =emoteBoardGlobalList,
        updateTextWithEmote={newValue ->updateTextWithEmote(newValue)},
        emoteBoardChannelList=emoteBoardChannelList,
        closeEmoteBoard = {
            emoteKeyBoardHeight.value = 0.dp
            iconClicked = false
        },
        deleteEmote={deleteEmote()},
        emoteBoardMostFrequentList= emoteBoardMostFrequentList,
        updateTempararyMostFrequentEmoteList ={value ->updateMostFrequentEmoteList(value)},
        globalBetterTTVEmotes= globalBetterTTVEmotes,
        channelBetterTTVResponse=channelBetterTTVResponse,
        sharedBetterTTVResponse=sharedBetterTTVResponse,
        userIsSub = userIsSub,
        forwardSlashes = forwardSlashes,
        lowPowerMode = lowPowerMode



    )
}