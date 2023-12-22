package com.example.clicker.presentation.stream.views

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.PressGestureScope
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.clicker.R
import com.example.clicker.network.websockets.models.TwitchUserData


import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


/**Extension function used to determine if the use has scrolled to the end of the chat*/
fun LazyListState.isScrolledToEnd() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
/**
 * 1) delete this after) Rule: brief description followed by the number of implementations
 *
 * MainChat represents all the UI composables used to build the auto scrolling chat system, the text box used to enter chat messages and utility composables
 *  - MainChat contains 1 top level implementation:
 *  1) [AutoScrollChatWithTextBox]
 *
 *
 * */
object MainChat{

    /**
     * 1) delete this after) Rule: brief description followed by builders used, then parameter description
     *
     * AutoScrollChatWithTextBox is the implementation that contains all the individual elements that makes
     * our user chat experience
     * - AutoScrollChatWithTextBox implements the [ScrollableChat][ChatBuilder.ScrollableChat] builder
     * */
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun AutoScrollChatWithTextBox(
        undoBan: () -> Unit,
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
        drawerState: androidx.compose.material3.DrawerState,
        showUndoButton:Boolean,
        noChatMode:Boolean
    ){
        val lazyColumnListState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        var autoscroll by remember { mutableStateOf(true) }

        //this is a builder
        ChatBuilder.ScrollableChat(
            determineScrollState={
                MainChatParts.DetermineScrollState(
                    lazyColumnListState =lazyColumnListState,
                    setAutoScrollFalse={autoscroll = false},
                    setAutoScrollTrue = {autoscroll = true},
                    showStickyHeader =showStickyHeader,
                    closeStickyHeader ={closeStickyHeader()}
                )
            },
            autoScrollingChat={
                MainChatParts.AutoScrollingChat(
                    twitchUserChat = twitchUserChat,
                    lazyColumnListState = lazyColumnListState,
                    showStickyHeader = showStickyHeader,
                    banResponseMessage =banResponseMessage,
                    closeStickyHeader ={closeStickyHeader()},
                    autoscroll =autoscroll,
                    restartWebSocket ={restartWebSocket()},
                    bottomModalState =bottomModalState,
                    updateClickedUser ={username,userId,banned,isMod ->updateClickedUser(username,userId,banned,isMod)},
                    deleteMessage ={messageId -> deleteMessage(messageId)}
                )
            },
            enterChat ={boxModifier ->
                TextChat.EnterChat(
                    modifier = boxModifier,
                    filteredChatList = filteredChatList,
                    textFieldValue = textFieldValue,
                    clickedAutoCompleteText = { addedValue, currentValue ->
                        clickedAutoCompleteText(
                            addedValue,
                            currentValue
                        )
                    },
                    modStatus = modStatus,
                    filterMethod = { username, text -> filterMethod(username, text) },
                    sendMessageToWebSocket = {chatMessage -> sendMessageToWebSocket(chatMessage)},
                    showModal ={
                        coroutineScope.launch { drawerState.open() }
                    },
                )

            }, // end of enter chat
            scrollToBottom ={boxModifier ->
                MainChat.ScrollToBottom(
                    scrollingPaused = !autoscroll,
                    enableAutoScroll = { autoscroll = true },
                    modifier = boxModifier
                )
            },
            draggableButton = {
                    MainChatParts.DraggableUndoButton(
                        undoBan={undoBan()},
                        showUndoButton =showUndoButton

                    )

            },
            noChatMode = noChatMode
        )
    }

    /**IMPLEMENTATIONS BELOW*/

    //todo: this is a part

    @Composable
    fun StickyHeader(
        banResponseMessage:String,
        closeStickyHeader: () -> Unit,

        ){
        MainChatParts.AlertRowHeader(
            alertMessage =banResponseMessage,
            closeAlert ={closeStickyHeader()}
        )

    }

    //
    @Composable
    fun ScrollToBottom(
        scrollingPaused: Boolean,
        enableAutoScroll: () -> Unit,
        modifier:Modifier
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (scrollingPaused) {
                    MainChatParts.DualIconsButton(
                        buttonAction = {enableAutoScroll()},
                        iconImageVector=Icons.Default.ArrowDropDown,
                        iconDescription = stringResource(R.string.arrow_drop_down_description),
                        buttonText =stringResource(R.string.scroll_to_bottom)

                    )
                }
            }
    }

    /**
     * ChatBuilder is the most generic of all builders and
     * */
    private object ChatBuilder{

        /**
         *
         * ScrollableChat  is the basic layout for the user chat experience. A example of what the typical UI looks like
         * with this builder can be found [HERE](https://theplebdev.github.io/Modderz-style-guide/#ScrollableChat)
         * */
        @Composable
        fun ScrollableChat(
            noChatMode: Boolean,
            determineScrollState:@Composable () -> Unit,
            autoScrollingChat:@Composable () -> Unit,
            enterChat:@Composable (modifier:Modifier) -> Unit,
            scrollToBottom:@Composable (modifier:Modifier) -> Unit,
            draggableButton:@Composable () -> Unit,
        ){
            determineScrollState()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)
            ) {
                autoScrollingChat()
                enterChat(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                )
                scrollToBottom(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 77.dp)
                )
                draggableButton()
                if(noChatMode){
                    Text(
                        "You are in no chat mode",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }//end of the builder




    /**PARTS BELOW*/
    private object MainChatParts{
        /**
         * A [Row] meant to display a urgent message to the user. This message should indicate a failure of some sort.
         * This message should be a max of 3 words and will be placed between two icons
         *
         * @param alertMessage a String to be placed between two icons and display a urgent message to the user. Should be
         * no longer than 3 words and will be placed between 2 close icons
         * @param closeAlert a function used to close this header
         * */
        @Composable
        fun AlertRowHeader(
            alertMessage:String,
            closeAlert: () -> Unit,
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .clip(shape = RoundedCornerShape(10.dp))
                    .background(Color.Red.copy(alpha = 0.6f))
                    .clickable {
                        closeAlert()
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
                    text = alertMessage,
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

        /**
         * A [Button] meant to display a message surrounded by two icons.
         *
         * @param buttonAction a function that will run when this button is clicked
         * @param iconImageVector the image vector for the two icons surrounding the [buttonText]
         * @param iconDescription a String that will act as the contentDescription for the two icons created by the [iconImageVector]
         * @param buttonText a String that will be displayed on top of the Button. This String should be short and no longer than
         * 3 words
         * */
        @Composable
        fun DualIconsButton(
            buttonAction: () -> Unit,
            iconImageVector:ImageVector,
            iconDescription:String,
            buttonText:String,
        ){
            Button(
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
                onClick = { buttonAction() }
            ) {
                Icon(
                    imageVector = iconImageVector,
                    contentDescription = iconDescription,
                    tint =  MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                )
                Text(buttonText,color =  MaterialTheme.colorScheme.onSecondary,)
                Icon(
                    imageVector = iconImageVector,
                    contentDescription = iconDescription,
                    tint =  MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                )
            }
        }

        /**
         * A utility composable used to determine the current state of a [LazyListState] and act accordingly
         * There are 3 states that this composables detects:
         * 1) [DragInteraction.Start],
         * 2) [PressInteraction.Press]
         * 3) [isScrolledToEnd]
         *
         * @param lazyColumnListState the current state of a [LazyColumn]. This state us used to determine the 3 needed states
         * of this composable
         *
         * @param setAutoScrollFalse a function used to turn the auto-scrolling chat off
         * @param setAutoScrollTrue a function used to turn the auto-scrolling chat on
         * @param showStickyHeader used to determine if the sticky header is showing or not. If it is then wait 2 seconds and run [closeStickyHeader]
         * @param closeStickyHeader function called to hide the sticky header after 2 seconds
         * */
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

        /**This composable is used to create the auto scrolling chat functionality. Inside of a [LazyColumn] is combines
         * 2 composables, [StickyHeader] and [SystemChats.IndividualChatMessages] to create the desired chat features
         *
         * @param twitchUserChat a list of [TwitchUserData] that represents all of the user's current chat messages. It gets
         * passed to the [SystemChats.IndividualChatMessages] composable
         *
         * @param lazyColumnListState the state passed to the [LazyColumn] to enable or disable its auto scrolling state
         * @param showStickyHeader a boolean meant to determine if the [StickyHeader] should be shown or not
         * @param banResponseMessage a String representing the message that will be shown to the [StickyHeader]
         * @param closeStickyHeader a function passed to [StickyHeader] that will be used to hide the sticky header
         * @param autoscroll a boolean used to determine if the auto scrolling functionality should be enabled or not
         * @param restartWebSocket a function passed to [SystemChats.IndividualChatMessages] used to restart a websocket after a failure has occured
         * @param bottomModalState the state to determine if the bottom modal should pop up or not. This is passed to
         * [SystemChats.IndividualChatMessages] and gets triggered when a user's chat message is clicked
         * @param updateClickedUser a function passed to [SystemChats.IndividualChatMessages] and used to update the viewmodel
         * state containing the current clicked user
         * @param deleteMessage a function passed to [SystemChats.IndividualChatMessages] that will be triggered when the chat
         * message is swiped passed its threshold
         * */
        @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
        @Composable
        fun AutoScrollingChat(
            twitchUserChat: List<TwitchUserData>,
            lazyColumnListState: LazyListState,
            showStickyHeader: Boolean,
            banResponseMessage: String,
            closeStickyHeader: () -> Unit,
            autoscroll: Boolean,
            restartWebSocket: () -> Unit,
            bottomModalState: ModalBottomSheetState,
            updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
            deleteMessage: (String) -> Unit,
        ){
            val coroutineScope = rememberCoroutineScope()
            LazyColumn(
                state = lazyColumnListState,
                modifier = Modifier
                    .padding(bottom = 70.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .fillMaxSize()

            ) {
                stickyHeader {
                    if (showStickyHeader) {
                        MainChat.StickyHeader(
                            banResponseMessage =banResponseMessage,
                            closeStickyHeader ={closeStickyHeader()}
                        )

                    }
                }

                coroutineScope.launch {
                    if (autoscroll) {
                        lazyColumnListState.scrollToItem(twitchUserChat.size)
                    }
                }

                items(twitchUserChat) { twitchUser ->

                    val color = Color(android.graphics.Color.parseColor(twitchUser.color))

                    // TODO: THIS IS WHAT IS PROBABLY CAUSING MY DOUBLE MESSAGE BUG
                    if (twitchUserChat.isNotEmpty()) {
                        SystemChats.IndividualChatMessages(
                            twitchUser = twitchUser,
                            restartWebSocket = {restartWebSocket() },
                            bottomModalState = bottomModalState,
                            deleteMessage = {messageId -> deleteMessage(messageId)},
                            updateClickedUser = { username, userId, banned, isMod ->
                                updateClickedUser(
                                    username,
                                    userId,
                                    banned,
                                    isMod
                                )
                            }
                        )

                    }
                }
            }// END OF THE LAZY COLUMN
        }

        /**
         * A [Box] within a [Box] used to be able to detect drag events move the inner box accordingly. A demonstration
         * can be found on Google's official home page [HERE](https://developer.android.com/jetpack/compose/touch-input/pointer-input/drag-swipe-fling)
         * */
        @Composable
        fun DraggableUndoButton(
            undoBan: () -> Unit,
            showUndoButton: Boolean
        ){
            Box(modifier = Modifier.fillMaxSize()) {
                var offsetX by remember { mutableStateOf(0f) }
                var offsetY by remember { mutableStateOf(0f) }


                if(showUndoButton){
                    Box(
                        Modifier
                            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                            .clip(RoundedCornerShape(5.dp))
                            .background(MaterialTheme.colorScheme.secondary)
                            .size(50.dp)
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->

                                    change.consume()
                                    offsetX += dragAmount.x
                                    offsetY += dragAmount.y
                                }
                            }

                            .align(Alignment.Center)
                            .clickable {
                                undoBan()
                            }
                    ){
                        Icon(
                            modifier = Modifier
                                .size(50.dp)
                                .align(Alignment.Center),
                            tint=MaterialTheme.colorScheme.onSecondary,
                            imageVector = Icons.Default.Refresh,
                            contentDescription = ""

                        )
                    }
                }


            }
        }


    }


}




