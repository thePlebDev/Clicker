package com.example.clicker.presentation.stream.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DrawerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.clicker.R
import com.example.clicker.network.websockets.MessageType
import com.example.clicker.network.websockets.models.TwitchUserData
import com.example.clicker.presentation.stream.ScrollingChat
import com.example.clicker.util.Response

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun LazyListState.isScrolledToEnd() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
/**
 * MainChat is all the components used to construct the chat functionality when the user navigates to the Stream fragment
 *
 * - [DetermineScrollState] : Used to facilitate,modify and identify the current scroll state of the chat messages Lazy Column
 *
 * - [ChatMessages]: The combination of all the possible chat messages delivered from the [Twitch IRC websocket](https://dev.twitch.tv/docs/irc/capabilities/)
 *
 * - [StickyHeader] : Show at the top of the user's chat when a sticky header conditional is triggered
 *
 * */
object MainChat{
    @Composable
    fun DetermineScrollState(
        lazyColumnListState: LazyListState,
        setAutoScrollFalse:()->Unit,
        setAutoScrollTrue:()->Unit,
        showStickyHeader:Boolean,
        closeStickyHeader: () -> Unit,
    ){
        val interactionSource = lazyColumnListState.interactionSource
        val endOfListReached by remember {
            derivedStateOf {
                lazyColumnListState.isScrolledToEnd()
            }
        }

        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is DragInteraction.Start -> {
                        setAutoScrollFalse()
                    }
                    is PressInteraction.Press -> {
                        setAutoScrollFalse()
                    }
                }
            }
        }

        // observer when reached end of list
        LaunchedEffect(endOfListReached) {
            // do your stuff
            if (endOfListReached) {
                setAutoScrollTrue()
            }
        }

        if (showStickyHeader) {
            LaunchedEffect(key1 = Unit) {
                delay(2000)
                closeStickyHeader()
            }
        }
    }
    @Composable
    fun ChatMessages(
        twitchUser: TwitchUserData,
        restartWebSocket: () -> Unit,
        swipeContent:@Composable () -> Unit,

        ){
        when (twitchUser.messageType) {
            MessageType.NOTICE -> {
                SystemChats.NoticeMessage(
                    color = Color.White,
                    displayName = twitchUser.displayName,
                    message = twitchUser.userType
                )
            }

            MessageType.USER -> {
                swipeContent()
            }

            MessageType.ANNOUNCEMENT -> {
                SystemChats.AnnouncementMessage(
                    displayName = twitchUser.displayName,
                    message = twitchUser.userType,
                    systemMessage = twitchUser.systemMessage
                )
            }
            MessageType.RESUB -> {
                SystemChats.ResubMessage(
                    message = twitchUser.userType,
                    systemMessage = twitchUser.systemMessage
                )
            }
            MessageType.SUB -> {

                SystemChats.SubMessage(
                    message = twitchUser.userType,
                    systemMessage = twitchUser.systemMessage
                )
            }
            // MYSTERYGIFTSUB,GIFTSUB
            MessageType.GIFTSUB -> {
                SystemChats.GiftSubMessage(
                    message = twitchUser.userType,
                    systemMessage = twitchUser.systemMessage
                )
            }
            MessageType.MYSTERYGIFTSUB -> {
                SystemChats.MysteryGiftSubMessage(
                    message = twitchUser.userType,
                    systemMessage = twitchUser.systemMessage
                )
            }
            MessageType.ERROR -> {
                SystemChats.ErrorMessage(
                    message = twitchUser.userType!!,
                    alterMessage = twitchUser.displayName!!,
                    restartWebSocket = { restartWebSocket() }
                )
            }
            MessageType.JOIN -> {
                SystemChats.JoinMessage(
                    message = twitchUser.userType!!
                )
            }

            else -> {}
        } // end of the WHEN BLOCK

    }
    @Composable
    fun StickyHeader(
        banResponseMessage:String,
        closeStickyHeader: () -> Unit,

        ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .clip(shape = RoundedCornerShape(10.dp))
                .background(Color.Red.copy(alpha = 0.6f))
                .clickable {
                    closeStickyHeader()
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.close_icon_description),
                modifier = Modifier
                    .size(30.dp),
                tint = Color.White
            )
            Text(
                text = banResponseMessage,
                color = Color.White,
                fontSize = 20.sp
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.close_icon_description),
                modifier = Modifier
                    .size(30.dp),
                tint = Color.White
            )
        }
    }

    @Composable
    fun ScrollToBottom(
        scrollingPaused: Boolean,
        enableAutoScroll: () -> Unit,

        ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 77.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (scrollingPaused) {
                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
                        onClick = { enableAutoScroll() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = stringResource(R.string.arrow_drop_down_description),
                            tint =  MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier
                        )
                        Text(stringResource(R.string.scroll_to_bottom),color =  MaterialTheme.colorScheme.onSecondary,)
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = stringResource(R.string.arrow_drop_down_description),
                            tint =  MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier
                        )
                    }
                }
            }

        }
    }

    object TextChat{
        @Composable
        fun EnterChat(
            modifier: Modifier,
            chat: (String) -> Unit,
            modStatus: Boolean?,
            filteredChatList: List<String>,
            filterMethod: (String, String) -> Unit,
            clickedAutoCompleteText: (String, String) -> String,
            textFieldValue: MutableState<TextFieldValue>,
            channelName: String?,
            showModal: () -> Unit
        ) {
            // todo: I think we can move this to the viewModel

            Column(modifier = modifier.background(MaterialTheme.colorScheme.primary)) {
                FilteredMentionLazyRow(
                    filteredChatList =filteredChatList,
                    textFieldValue =textFieldValue,
                    clickedAutoCompleteText ={addedValue, currentValue ->clickedAutoCompleteText(addedValue,currentValue)}
                )

                TextFieldChat(
                    textFieldValue = textFieldValue,
                    modStatus = modStatus,
                    filterMethod = { username, text -> filterMethod(username, text) },
                    chat = { chatMessage -> chat(chatMessage) },
                    showModal = { showModal() }
                )
            }
        }
        @Composable
        fun FilteredMentionLazyRow(
            filteredChatList: List<String>,
            textFieldValue: MutableState<TextFieldValue>,
            clickedAutoCompleteText: (String, String) -> String,
        ){
            LazyRow(modifier = Modifier.padding(vertical = 10.dp)) {
                items(filteredChatList) {
                    Text(
                        it,
                        modifier = Modifier
                            .padding(5.dp)
                            .clickable {
                                textFieldValue.value = TextFieldValue(
                                    text = clickedAutoCompleteText(textFieldValue.value.text, it),
                                    selection = TextRange(
                                        (textFieldValue.value.text + "$it ").length
                                    )
                                )
                            },
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        @Composable
        fun TextFieldChat(
            textFieldValue: MutableState<TextFieldValue>,
            modStatus: Boolean?,
            filterMethod: (String, String) -> Unit,
            chat: (String) -> Unit,
            showModal: () -> Unit
        ) {
            val customTextSelectionColors = TextSelectionColors(
                handleColor = MaterialTheme.colorScheme.secondary,
                backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
            )

            Row(
                modifier = Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.primary),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (modStatus != null && modStatus == true) {
                    AsyncImage(
                        model = "https://static-cdn.jtvnw.net/badges/v1/3267646d-33f0-4b17-b3df-f923a41db1d0/3",
                        contentDescription = stringResource(R.string.moderator_badge_icon_description)
                    )
                }
                CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                    TextField(

                        modifier = Modifier
                            .weight(2f),
                        maxLines =1,
                        singleLine = true,
                        value = textFieldValue.value,
                        shape = RoundedCornerShape(8.dp),
                        onValueChange = { newText ->
                            filterMethod("username", newText.text)
                            textFieldValue.value = TextFieldValue(
                                text = newText.text,
                                selection = newText.selection
                            )
                            // text = newText
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.White,
                            backgroundColor = Color.DarkGray,
                            cursorColor = Color.White,
                            disabledLabelColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        placeholder = {
                            Text(stringResource(R.string.send_a_message), color = Color.White)
                        }
                    )
                }
                if (textFieldValue.value.text.length > 0) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = stringResource(R.string.send_chat),
                        modifier = Modifier
                            .size(35.dp)
                            .clickable { chat(textFieldValue.value.text) }
                            .padding(start = 5.dp),
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.more_vert_icon_description),
                        modifier = Modifier
                            .size(35.dp)
                            .clickable { showModal() }
                            .padding(start = 5.dp),
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }// end of Text Chat
}

object ChatBuilder{
    @Composable
    fun ScrollableChat(
        determineScrollState:@Composable () -> Unit,
        autoScrollingChat:@Composable () -> Unit,
        enterChat:@Composable (modifier:Modifier) -> Unit,
        scrollToBottom:@Composable () -> Unit,
    ){
        determineScrollState()
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            autoScrollingChat()
            enterChat(Modifier.align(Alignment.BottomCenter).fillMaxWidth())
            scrollToBottom()
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun ChatBuilderImpl(
        showStickyHeader: Boolean,
        closeStickyHeader: () -> Unit,
        twitchUserChat: List<TwitchUserData>,
        bottomModalState: ModalBottomSheetState,
        restartWebSocket: () -> Unit,
        banResponseMessage: String,
        updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
        deleteMessage: (String) -> Unit,
        sendMessageToWebSocket: (String) -> Unit,
        modStatus: Boolean?,
        filteredChatList: List<String>,
        filterMethod: (String, String) -> Unit,
        clickedAutoCompleteText: (String, String) -> String,
        textFieldValue: MutableState<TextFieldValue>,
        channelName: String?,
        drawerState: DrawerState,
    ){
        val lazyColumnListState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        var autoscroll by remember { mutableStateOf(true) }
        ScrollableChat(
            determineScrollState={
                MainChat.DetermineScrollState(
                    lazyColumnListState =lazyColumnListState,
                    setAutoScrollFalse={autoscroll = false},
                    setAutoScrollTrue = {autoscroll = true},
                    showStickyHeader =showStickyHeader,
                    closeStickyHeader ={closeStickyHeader()}
                )
            },
            autoScrollingChat={
                ScrollingChat(
                    twitchUserChat = twitchUserChat,
                    lazyColumnListState = lazyColumnListState,
                    showStickyHeader = showStickyHeader,
                    banResponseMessage =banResponseMessage,
                    closeStickyHeader ={closeStickyHeader()},
                    autoscroll =autoscroll,
                    restartWebSocket ={restartWebSocket},
                    bottomModalState =bottomModalState,
                    updateClickedUser ={username,userId,banned,isMod ->updateClickedUser(username,userId,banned,isMod)},
                    deleteMessage ={messageId -> deleteMessage(messageId)}
                )
            },
            enterChat ={boxModifier ->
                MainChat.TextChat.EnterChat(
                    modifier = boxModifier,
                    chat = { text -> sendMessageToWebSocket(text) },
                    modStatus = modStatus,
                    filteredChatList = filteredChatList,
                    filterMethod = { username, newText -> filterMethod(username, newText) },
                    clickedAutoCompleteText = { fullText, clickedText ->
                        clickedAutoCompleteText(
                            fullText,
                            clickedText
                        )
                    },
                    textFieldValue = textFieldValue,
                    channelName = channelName,
                    showModal = { coroutineScope.launch { drawerState.open() } }
                )
            }, // end of enter chat
            scrollToBottom ={
                MainChat.ScrollToBottom(
                    scrollingPaused = !autoscroll,
                    enableAutoScroll = { autoscroll = true },
                )
            }
        )
    }
}
