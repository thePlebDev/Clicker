package com.example.clicker.presentation.stream.views

import android.util.Log
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.clicker.R
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.network.websockets.MessageType
import com.example.clicker.presentation.modView.views.HorizontalDragDetectionBox
import com.example.clicker.presentation.sharedViews.ChatScope
import com.example.clicker.presentation.sharedViews.ErrorScope
import com.example.clicker.presentation.sharedViews.ScaffoldTopBarScope
import kotlinx.coroutines.launch




@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatUI(
    twitchUserChat: List<TwitchUserData>,
    showBottomModal:()->Unit,
    updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
){
    val lazyColumnListState = rememberLazyListState()
    var autoscroll by remember { mutableStateOf(true) }

    ChatUIBox(
        determineScrollState={
            DetermineScrollState(
                lazyColumnListState = lazyColumnListState,
                setAutoScrollFalse = { autoscroll = false },
                setAutoScrollTrue = { autoscroll = true },
            )
        },
        chatUI={
            ChatUILazyColumn(
                lazyColumnListState=lazyColumnListState,
                twitchUserChat=twitchUserChat,
                autoscroll=autoscroll,
                showBottomModal={showBottomModal()},
                updateClickedUser = {  username, userId,isBanned,isMod ->
                    updateClickedUser(
                        username,
                        userId,
                        isBanned,
                        isMod
                    )
                }

            )
        },
        scrollToBottom ={modifier ->
            ScrollToBottom(
                scrollingPaused = !autoscroll,
                enableAutoScroll = { autoscroll = true },
                modifier = modifier
            )
        },

        )
}

@Composable
 private fun ChatUIBox(
    determineScrollState: @Composable ImprovedChatUI.() -> Unit,
    chatUI: @Composable ImprovedChatUI.() -> Unit,
    scrollToBottom: @Composable ImprovedChatUI.(modifier: Modifier) -> Unit,
){
    val chatUIScope = remember(){ ImprovedChatUI() }
    with(chatUIScope){
        Box(modifier = Modifier.fillMaxSize()){
            determineScrollState()
            chatUI()
            scrollToBottom(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 30.dp))
        }
    }


}



@Stable
private class ImprovedChatUI(){
    @Composable
    fun DetermineScrollState(
        lazyColumnListState: LazyListState,
        setAutoScrollFalse:()->Unit,
        setAutoScrollTrue:()->Unit,
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

    }



    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun ChatUILazyColumn(
        lazyColumnListState: LazyListState,
        twitchUserChat: List<TwitchUserData>,
        autoscroll:Boolean,
        showBottomModal:()->Unit,
        updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
    ){
        val coroutineScope = rememberCoroutineScope()
        LazyColumn(
            state = lazyColumnListState
        ){
            coroutineScope.launch {
                if (autoscroll) {
                    lazyColumnListState.scrollToItem(twitchUserChat.size)
                }
            }
            items(
                twitchUserChat,
                key = { item -> item.id ?:"" }
            ) {indivChatMessage ->
                ChatMessages(
                    indivChatMessage,
                    showBottomModal={showBottomModal()},
                    updateClickedUser = {  username, userId,isBanned,isMod ->
                        updateClickedUser(
                            username,
                            userId,
                            isBanned,
                            isMod
                        )
                    }

                )

            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun ChatMessages(
        twitchChatMessage: TwitchUserData,
        showBottomModal:()->Unit,
        updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
    ){
        val titleFontSize = MaterialTheme.typography.headlineMedium.fontSize
        val messageFontSize = MaterialTheme.typography.headlineSmall.fontSize
        val chatScope = remember(){ ChatScope(titleFontSize,messageFontSize) }
        val errorScope = remember(){ ErrorScope(messageFontSize) }
        val color = remember { mutableStateOf(Color(android.graphics.Color.parseColor(twitchChatMessage.color))) }
        if(color.value == Color.Black){
            color.value = MaterialTheme.colorScheme.primary
        }
        with(chatScope) {

            when (twitchChatMessage.messageType) {
                MessageType.NOTICE -> { //added
                    NoticeMessages(
                        systemMessage = "",
                        message = twitchChatMessage.userType
                    )
                }

                MessageType.USER -> { //added
                    // individualSwipableChatMessage()
                  //  TestingIndivChatMessage(twitchChatMessage)
                    HorizontalDragDetectionBox(
                        itemBeingDragged = {

                        },
                        quarterSwipeLeftAction={},
                        quarterSwipeRightAction={},
                        swipeEnabled = true,
                        twoSwipeOnly= false
                    )
                    ClickableCard(
                        twitchUser =twitchChatMessage,
                        color = color.value,
                        fontSize = messageFontSize,
                        showBottomModal={showBottomModal()},
                        updateClickedUser = {  username, userId,isBanned,isMod ->
                            updateClickedUser(
                                username,
                                userId,
                                isBanned,
                                isMod
                            )
                        }
                    )
                }

                MessageType.ANNOUNCEMENT -> { //added
                    AnnouncementMessages(
                        message = "${twitchChatMessage.displayName}: ${twitchChatMessage.systemMessage}"
                    )
                }

                MessageType.RESUB -> { //added
                    ReSubMessage(
                        systemMessage = twitchChatMessage.systemMessage,
                        message = twitchChatMessage.userType,
                    )
                }

                MessageType.SUB -> { //added
                    SubMessages(
                        systemMessage = twitchChatMessage.systemMessage,
                        message = twitchChatMessage.userType,
                    )
                }
                // MYSTERYGIFTSUB,GIFTSUB
                MessageType.GIFTSUB -> { //added
                    GiftSubMessages(
                        message = twitchChatMessage.userType,
                        systemMessage = twitchChatMessage.systemMessage
                    )
                }

                MessageType.MYSTERYGIFTSUB -> { //
                    AnonGiftMessages(
                        message = twitchChatMessage.userType,
                        systemMessage = twitchChatMessage.systemMessage
                    )
                }

                MessageType.ERROR -> {
                    with(errorScope){
                        ChatErrorMessage(twitchChatMessage.userType ?:"")
                    }
                }

                MessageType.JOIN -> {
                    JoinMessage(
                        message = twitchChatMessage.userType ?:""
                    )
                }

                else -> {

                }

            }
        }
    }

    @Composable
    fun TestingIndivChatMessage(twitchChatMessage: TwitchUserData){
        Log.d("TestingIndivChatMessage",twitchChatMessage.userType?:"")
        Text(twitchChatMessage.userType?:"", color = MaterialTheme.colorScheme.secondary)
    }
    @Composable
    fun ScrollToBottom(
        scrollingPaused: Boolean,
        enableAutoScroll: () -> Unit,
        modifier: Modifier
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (scrollingPaused) {
                DualIconsButton(
                    buttonAction = { enableAutoScroll() },
                    iconImageVector = Icons.Default.ArrowDropDown,
                    iconDescription = stringResource(R.string.arrow_drop_down_description),
                    buttonText = stringResource(R.string.scroll_to_bottom)

                )
            }
        }
    }
}
