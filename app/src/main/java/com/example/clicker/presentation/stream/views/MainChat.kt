package com.example.clicker.presentation.stream.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R
import com.example.clicker.network.websockets.MessageType
import com.example.clicker.network.websockets.models.TwitchUserData
import com.example.clicker.presentation.stream.AnnouncementMessage
import com.example.clicker.presentation.stream.ErrorMessage
import com.example.clicker.presentation.stream.GiftSubMessage
import com.example.clicker.presentation.stream.JoinMessage
import com.example.clicker.presentation.stream.MysteryGiftSubMessage
import com.example.clicker.presentation.stream.NoticeMessage
import com.example.clicker.presentation.stream.ResubMessage
import com.example.clicker.presentation.stream.SubMessage
import com.example.clicker.presentation.stream.isScrolledToEnd
import kotlinx.coroutines.delay

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
                NoticeMessage(
                    color = Color.White,
                    displayName = twitchUser.displayName,
                    message = twitchUser.userType
                )
            }

            MessageType.USER -> {
                swipeContent()
            }

            MessageType.ANNOUNCEMENT -> {
                AnnouncementMessage(
                    displayName = twitchUser.displayName,
                    message = twitchUser.userType,
                    systemMessage = twitchUser.systemMessage
                )
            }
            MessageType.RESUB -> {
                ResubMessage(
                    message = twitchUser.userType,
                    systemMessage = twitchUser.systemMessage
                )
            }
            MessageType.SUB -> {
                SubMessage(
                    message = twitchUser.userType,
                    systemMessage = twitchUser.systemMessage
                )
            }
            // MYSTERYGIFTSUB,GIFTSUB
            MessageType.GIFTSUB -> {
                GiftSubMessage(
                    message = twitchUser.userType,
                    systemMessage = twitchUser.systemMessage
                )
            }
            MessageType.MYSTERYGIFTSUB -> {
                MysteryGiftSubMessage(
                    message = twitchUser.userType,
                    systemMessage = twitchUser.systemMessage
                )
            }
            MessageType.ERROR -> {
                ErrorMessage(
                    message = twitchUser.userType!!,
                    user = twitchUser.displayName!!,
                    restartWebSocket = { restartWebSocket() }
                )
            }
            MessageType.JOIN -> {
                JoinMessage(
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
}