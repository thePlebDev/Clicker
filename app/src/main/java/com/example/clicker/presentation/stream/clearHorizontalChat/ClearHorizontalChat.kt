package com.example.clicker.presentation.stream.clearHorizontalChat

import android.content.res.Resources
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.clicker.R
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.network.repository.models.EmoteListMap
import com.example.clicker.network.websockets.models.MessageToken
import com.example.clicker.network.websockets.models.PrivateMessageType
import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.presentation.stream.views.chat.DualIconsButton
import com.example.clicker.presentation.stream.views.chat.chatSettings.ChatSettingsViewModel
import com.example.clicker.presentation.stream.views.chat.isScrolledToEnd
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun ClearHorizontalChatView(
    streamViewModel:StreamViewModel,
    chatSettingsViewModel:ChatSettingsViewModel
){
    val twitchUserChat = streamViewModel.listChats.toList()
    val doubleClickChat:(String)->Unit =remember(streamViewModel) { {
        streamViewModel.sendDoubleTapEmote(it)
    } }
    DraggableClearChat(
        (streamViewModel.fullImmersionWidth.value)*-1,
        twitchUserChat=twitchUserChat,
        usernameSize = chatSettingsViewModel.usernameSize.value,
        badgeListMap =chatSettingsViewModel.globalChatBadgesMap.value,
        messageSize = chatSettingsViewModel.messageSize.value,
        globalTwitchEmoteContentMap = chatSettingsViewModel.globalEmoteMap.value,
        channelTwitchEmoteContentMap= chatSettingsViewModel.inlineContentMapChannelEmoteList.value,
        globalBetterTTVEmoteContentMap =chatSettingsViewModel.betterTTVGlobalInlineContentMapChannelEmoteList.value,
        channelBetterTTVEmoteContentMap =chatSettingsViewModel.betterTTVChannelInlineContentMapChannelEmoteList.value,
        sharedBetterTTVEmoteContentMap =chatSettingsViewModel.betterTTVSharedInlineContentMapChannelEmoteList.value,
        useCustomUsernameColors = chatSettingsViewModel.customUsernameColor.value,
        doubleClickMessage={username ->doubleClickChat(username)}
    )

}

@Composable
fun DraggableClearChat(
    fullImmersionWidth:Int,
    twitchUserChat: List<TwitchUserData>,
    usernameSize:Float,
    badgeListMap: EmoteListMap,
    messageSize:Float,
    globalTwitchEmoteContentMap:EmoteListMap, //this is 0
    channelTwitchEmoteContentMap:EmoteListMap,
    globalBetterTTVEmoteContentMap:EmoteListMap,
    channelBetterTTVEmoteContentMap:EmoteListMap, // this is 0
    sharedBetterTTVEmoteContentMap:EmoteListMap,
    useCustomUsernameColors:Boolean,
    doubleClickMessage:(String)->Unit,
){
    var offsetX by remember { mutableStateOf(0f) }

    val maxWidthHalf = (Resources.getSystem().displayMetrics.widthPixels/2.5)*-1
    var clearChatWidth by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    val draggableState = DraggableState { delta ->
        offsetX += delta
    }

    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Box(modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .fillMaxHeight()
            .fillMaxWidth(.25f)
            .align(Alignment.CenterEnd)
            .draggable(
                orientation = Orientation.Horizontal,
                state = draggableState,
                onDragStopped = {

                    Log.d("TestingMaxWidthClear", "threshold crossed-->${offsetX <= maxWidthHalf}")
                    if (offsetX <= maxWidthHalf) {
                        draggableState.drag(MutatePriority.PreventUserInput) {
                            Animatable(offsetX).animateTo(
                                targetValue = (fullImmersionWidth + clearChatWidth).toFloat(),
                                tween(durationMillis = 300)
                            ) {
                                dragBy(value - offsetX)
                            }
                        }

                    } else {
                        draggableState.drag(MutatePriority.PreventUserInput) {
                            Animatable(offsetX).animateTo(
                                targetValue = 0f,
                                tween(durationMillis = 300)
                            ) {
                                dragBy(value - offsetX)
                            }
                        }
                    }


                }
            )
            .onGloballyPositioned {
                clearChatWidth = it.size.width
            }

        ){

            val coroutineScope = rememberCoroutineScope()


            // this is where the chat needs to go
            ClearChatLazyColumn(
                twitchUserChat=twitchUserChat,
                usernameSize=usernameSize,
                badgeListMap=badgeListMap,
                messageSize=messageSize,
                globalTwitchEmoteContentMap=globalTwitchEmoteContentMap,
                channelTwitchEmoteContentMap=channelTwitchEmoteContentMap,
                globalBetterTTVEmoteContentMap=globalBetterTTVEmoteContentMap,
                channelBetterTTVEmoteContentMap=channelBetterTTVEmoteContentMap,
                sharedBetterTTVEmoteContentMap=sharedBetterTTVEmoteContentMap,
                useCustomUsernameColors=useCustomUsernameColors,
                doubleClickMessage={username ->doubleClickMessage(username)}

            )


        }

    }
}
@Composable
fun ClearChatLazyColumn(
    twitchUserChat: List<TwitchUserData>,
    usernameSize:Float,
    badgeListMap: EmoteListMap,
    messageSize:Float,
    globalTwitchEmoteContentMap:EmoteListMap, //this is 0
    channelTwitchEmoteContentMap:EmoteListMap,
    globalBetterTTVEmoteContentMap:EmoteListMap,
    channelBetterTTVEmoteContentMap:EmoteListMap, // this is 0
    sharedBetterTTVEmoteContentMap:EmoteListMap,
    useCustomUsernameColors:Boolean,
    doubleClickMessage:(String)->Unit,
){

    var autoscroll by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val lazyColumnListState = rememberLazyListState()

    Box(
        modifier = Modifier.padding(10.dp)
    ){
        LazyColumn(
            modifier =Modifier.fillMaxSize(),
            state = lazyColumnListState
        ){
            coroutineScope.launch {
                if (autoscroll) {
                    lazyColumnListState.scrollToItem(twitchUserChat.size)
                }
            }
            items(twitchUserChat){twitchUser->
                ClearChatMessage(
                    twitchChatMessage=twitchUser,
                    usernameSize=usernameSize,
                    badgeListMap=badgeListMap,
                    badgeList=twitchUser.badges,
                    messageList = twitchUser.messageList,
                    messageSize=messageSize,
                    globalTwitchEmoteContentMap=globalTwitchEmoteContentMap,
                    channelTwitchEmoteContentMap=channelTwitchEmoteContentMap,
                    globalBetterTTVEmoteContentMap=globalBetterTTVEmoteContentMap,
                    channelBetterTTVEmoteContentMap=channelBetterTTVEmoteContentMap,
                    sharedBetterTTVEmoteContentMap=sharedBetterTTVEmoteContentMap,
                    useCustomUsernameColors=useCustomUsernameColors,
                    doubleClickMessage={username ->doubleClickMessage(username)}
                )
            }

        }
        ClearChatDetermineScrollState(
            lazyColumnListState = lazyColumnListState,
            setAutoScrollFalse = { autoscroll = false },
            setAutoScrollTrue = { autoscroll = true },
        )
        ClearChatScrollToBottom(
            scrollingPaused = !autoscroll,
            enableAutoScroll = { autoscroll = true },

            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }


}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClearChatMessage(
    twitchChatMessage: TwitchUserData,
    usernameSize:Float,
    badgeList:List<String>,
    badgeListMap: EmoteListMap,
    globalTwitchEmoteContentMap:EmoteListMap, //this is 0
    channelTwitchEmoteContentMap:EmoteListMap,
    globalBetterTTVEmoteContentMap:EmoteListMap,
    channelBetterTTVEmoteContentMap:EmoteListMap, // this is 0
    sharedBetterTTVEmoteContentMap:EmoteListMap,
    messageList:List<MessageToken>,
    messageSize:Float,
    useCustomUsernameColors:Boolean,
    doubleClickMessage:(String)->Unit,
){
    val showIcon = remember { mutableStateOf(false) }
    val newMap = globalTwitchEmoteContentMap.map +badgeListMap.map +channelTwitchEmoteContentMap.map +globalBetterTTVEmoteContentMap.map +channelBetterTTVEmoteContentMap.map+sharedBetterTTVEmoteContentMap.map
    val color = remember { mutableStateOf(Color(android.graphics.Color.parseColor(twitchChatMessage.color))) }
    if(color.value == Color.Black){
        color.value = MaterialTheme.colorScheme.secondary
    }
    val usernameColor = if(useCustomUsernameColors) color.value else MaterialTheme.colorScheme.onPrimary
    val text = buildAnnotatedString {
        for(item in badgeList){
            if(badgeListMap.map.containsKey(item)){
                withStyle(style = SpanStyle(fontSize = 15.sp)) {
                    appendInlineContent(item, item)
                }
            }
        }
        withStyle(style = SpanStyle(color = usernameColor, fontSize = usernameSize.sp)) {
            append("${twitchChatMessage.displayName} ")
        }
        for(messageToken in messageList){

            if(messageToken.messageType == PrivateMessageType.MESSAGE){
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onPrimary, fontSize = messageSize.sp)) {

                    appendInlineContent(messageToken.messageValue, "${messageToken.messageValue} ")
                }
            }else{
                appendInlineContent(messageToken.messageValue, "[${messageToken.messageValue}]")
            }
        }

    }
    Box(modifier = Modifier.combinedClickable(
        enabled = true,
        onDoubleClick = {
            showIcon.value = true
            doubleClickMessage(twitchChatMessage.displayName?:"")
        },
        onClick = {}
    )){
        Text(
            text = text,
            inlineContent = newMap,
        )
        if(showIcon.value){
            ClearChatDoubleClickSeemsGoodIcon()
        }
    }

}

@Composable
fun ClearChatDetermineScrollState(
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

@Composable
fun ClearChatScrollToBottom(
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
                Box(
                    modifier = Modifier
                         // Adjust the size as needed
                        .background(color = MaterialTheme.colorScheme.secondary, shape = CircleShape).clickable {
                            enableAutoScroll()
                        }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.keyboard_arrow_down_24),
                        contentDescription = "Down",
                        tint = Color.White,
                        modifier = Modifier.size(35.dp)
                    )
                }

            }
        }

}

@Composable
fun ClearChatDoubleClickSeemsGoodIcon(){

    val size = remember { Animatable(10F) }
    LaunchedEffect(true){
        size.animateTo(40f)
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(end = 30.dp)){
        AsyncImage(
            model = "https://static-cdn.jtvnw.net/emoticons/v2/64138/static/light/1.0",
            contentDescription = stringResource(R.string.moderator_badge_icon_description),
            modifier = Modifier
                .size(size.value.dp)
                .align(Alignment.CenterEnd)
        )
    }

}