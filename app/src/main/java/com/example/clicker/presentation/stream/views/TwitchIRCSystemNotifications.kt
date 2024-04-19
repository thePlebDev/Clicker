package com.example.clicker.presentation.stream.views

import android.util.Log

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetState

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

import com.example.clicker.network.websockets.MessageType
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.presentation.sharedViews.ChatScope
import com.example.clicker.presentation.sharedViews.ErrorScope

import com.example.clicker.presentation.sharedViews.ScaffoldTopBarScope


//parts builders implementations (PBI architecture for short)

/**
 * SystemChats represents all the UI composables for the chat messages sent by the [Twitch IRC server](https://dev.twitch.tv/docs/irc/commands/)
 * that contains the  [USERNOTICE](https://dev.twitch.tv/docs/irc/commands/#usernotice) command. It
 * is then picked up by our [ParsingEngine][com.example.clicker.network.websockets.ParsingEngine] which will run the [userNoticeParsing()][com.example.clicker.network.websockets.ParsingEngine.userNoticeParsing]
 * method to create a [TwitchUserData][com.example.clicker.network.websockets.models.TwitchUserData] object marked with the appropriate [MessageType][com.example.clicker.network.websockets.MessageType] and then given to the UI.
 *
 * */


    //slotting layout means that it is a builder
    // MainChatting is a builder

    /**
     *
     * IndividualChatMessages is the implementation used to represent all the individual messages
     * sent from the TwitchIRC server
     * - IndividualChatMessages implements the [MainChatting][Builders.MainChatting] builder
     * */
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun IndividualChatMessages(
        twitchUser: TwitchUserData,
        restartWebSocket: () -> Unit,
        bottomModalState: ModalBottomSheetState,
        deleteMessage: (String) -> Unit,
        updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
        toggleTimeoutDialog:()->Unit,
        toggleBanDialog:()->Unit,


    ){
        val fontSize = MaterialTheme.typography.headlineSmall.fontSize
        val errorScope = remember(){ ErrorScope(fontSize) }

        MainChatting(
            twitchUser =twitchUser,
            individualSwipableChatMessage = {
                SwipeToDeleteChatMessages(
                    twitchUser = twitchUser,
                    bottomModalState = bottomModalState,
                    updateClickedUser = { username, userId, banned, isMod ->
                        updateClickedUser(
                            username,
                            userId,
                            banned,
                            isMod
                        )
                    },
                    deleteMessage = { messageId -> deleteMessage(messageId) },
                    toggleTimeoutDialog={toggleTimeoutDialog()},
                    toggleBanDialog={toggleBanDialog()}
                )
            },
            noticeMessage = {
                NoticeMessages(
                    systemMessage="",
                    message =twitchUser.userType
                )
            },
            announcementMessage = {
                AnnouncementMessages(
                    message = "${twitchUser.displayName}: ${twitchUser.systemMessage}"
                )
            },
            resubMessage = {
                ReSubMessage(
                    systemMessage = twitchUser.systemMessage,
                    message = twitchUser.userType,
                )
            },
            subMessage = {
                SubMessages(
                    systemMessage = twitchUser.systemMessage,
                    message = twitchUser.userType,
                )
            },
            giftSubMessage = {
                GiftSubMessages(
                    message = twitchUser.userType,
                    systemMessage = twitchUser.systemMessage
                )
            },
            mysterySubMessage = {
                AnonGiftMessages(
                    message = twitchUser.userType,
                    systemMessage = twitchUser.systemMessage
                )
            },
            errorMessage = {
                with(errorScope){
                    ChatErrorMessage()
                }
            },
            joinMessage = {
                JoinMessage(
                    message = twitchUser.userType ?:""
                )
            },

        )

    }


        /**
         * - ScrollableChat is used inside of  [IndividualChatMessages].
         *
         * @param noChatMode a boolean to determine if a String saying, `You are in no chat mode`, should be shown
         * @param determineScrollState a composable function used to determine the current scrolling state of [AutoScrollChatWithTextBox]
         * @param autoScrollingChat a composable function that represents the auto scrolling chat functionality
         * @param enterChat a composable function that represents the entering chat function
         * @param scrollToBottom a composable function that represents a button to be pressed when autoscrolling is paused
         * @param draggableButton a composable function that represents a button that that should be draggable all throughout the chat feature
         * */
        @Composable
        fun MainChatting(
            twitchUser: TwitchUserData,
            individualSwipableChatMessage:@Composable () -> Unit,
            noticeMessage:@Composable ChatScope.() -> Unit,
            announcementMessage:@Composable ChatScope.() -> Unit,
            resubMessage:@Composable ChatScope.() -> Unit,
            subMessage:@Composable ChatScope.() -> Unit,
            giftSubMessage:@Composable ChatScope.() -> Unit,
            mysterySubMessage:@Composable ChatScope.() -> Unit,
            errorMessage:@Composable ChatScope.() -> Unit,
            joinMessage:@Composable ChatScope.() -> Unit,


            ){
            val titleFontSize = MaterialTheme.typography.headlineMedium.fontSize
            val messageFontSize = MaterialTheme.typography.headlineSmall.fontSize
            val chatScope = remember(){ ChatScope(titleFontSize,messageFontSize) }
            with(chatScope){
                when (twitchUser.messageType) {
                    MessageType.NOTICE -> { //added
                        noticeMessage()
                    }

                    MessageType.USER -> { //added
                        individualSwipableChatMessage()
                    }

                    MessageType.ANNOUNCEMENT -> { //added
                        announcementMessage()
                    }
                    MessageType.RESUB -> { //added
                        resubMessage()
                    }
                    MessageType.SUB -> { //added
                        subMessage()
                    }
                    // MYSTERYGIFTSUB,GIFTSUB
                    MessageType.GIFTSUB -> { //added
                        giftSubMessage()
                    }
                    MessageType.MYSTERYGIFTSUB -> { //
                        mysterySubMessage()
                    }
                    MessageType.ERROR -> {

                        errorMessage()
                    }
                    MessageType.JOIN -> {
                        joinMessage()
                    }
                    else -> {}
                } // end of the WHEN BLOCK
            }


        }






