package com.example.clicker.presentation.stream.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R
import com.example.clicker.network.websockets.MessageType
import com.example.clicker.network.websockets.models.TwitchUserAnnouncement
import com.example.clicker.network.websockets.models.TwitchUserData
import com.example.clicker.presentation.stream.views.MainChat.AutoScrollChatWithTextBox
import com.example.clicker.presentation.stream.views.SwipeToDelete.SwipeToDeleteChatMessages

import com.example.clicker.presentation.stream.views.SystemChats.TwitchIRCSystemNotificationsBuilder.SystemChat

//parts builders implementations (PBI architecture for short)

/**
 * SystemChats represents all the UI composables for the chat messages sent by the [Twitch IRC server](https://dev.twitch.tv/docs/irc/commands/)
 * that contains the  [USERNOTICE](https://dev.twitch.tv/docs/irc/commands/#usernotice) command. It
 * is then picked up by our [ParsingEngine][com.example.clicker.network.websockets.ParsingEngine] which will run the [userNoticeParsing()][com.example.clicker.network.websockets.ParsingEngine.userNoticeParsing]
 * method to create a [TwitchUserData][com.example.clicker.network.websockets.models.TwitchUserData] object marked with the appropriate [MessageType][com.example.clicker.network.websockets.MessageType] and then given to the UI.
 * The UI then uses the [MessageType][com.example.clicker.network.websockets.MessageType] to determine which component to show
 * - SystemChats contains private 8 components:
 *
 * - [ResubMessage] : Shows a message in the chat when the [ParsingEngine][com.example.clicker.network.websockets.ParsingEngine] detects a ***msg-id*** of ***resub***
 *
 * - [SubMessage]: Shows a message in the chat when the [ParsingEngine][com.example.clicker.network.websockets.ParsingEngine] detects a ***msg-id*** of ***sub***
 *
 * - [AnnouncementMessage] : Shows a message in the chat when the [ParsingEngine][com.example.clicker.network.websockets.ParsingEngine] detects a ***msg-id*** of ***announcement***
 *
 * - [MysteryGiftSubMessage] : Shows a message in the chat when the [ParsingEngine][com.example.clicker.network.websockets.ParsingEngine] detects a ***msg-id*** of ***submysterygift***
 *
 * - [GiftSubMessage] : Shows a message in the chat when the [ParsingEngine][com.example.clicker.network.websockets.ParsingEngine] detects a ***msg-id*** of ***subgift***
 *
 * - [JoinMessage] : Shows a message in chat when the Twitch IRC server replies with the JOIN, indicating we have successfully joined the chat,
 *
 * - [NoticeMessage] : Shows a message in chat when the [ParsingEngine][com.example.clicker.network.websockets.ParsingEngine] detects a ***NOTICE*** message
 *
 * - [ErrorMessage] : Shows a message in chat when the [TwitchWebSocket][com.example.clicker.network.websockets.TwitchWebSocket] detects a failure an runs its [onFailure()][com.example.clicker.network.websockets.TwitchWebSocket.onFailure] method
 *
 * - SystemChats contains 1 top level implementation:
 *  1) [IndividualChatMessages]
 *
 * */
object SystemChats {


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
    ){
        Builders.MainChatting(
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
                )
            },
            noticeMessage = {
                SystemChats.NoticeMessage(
                    color = MaterialTheme.colorScheme.onPrimary,
                    displayName = twitchUser.displayName,
                    message = twitchUser.userType
                )
            },
            announcementMessage = {
                SystemChats.AnnouncementMessage(
                    displayName = twitchUser.displayName,
                    message = twitchUser.userType,
                    systemMessage = twitchUser.systemMessage
                )
            },
            resubMessage = {
                SystemChats.ResubMessage(
                    message = twitchUser.userType,
                    systemMessage = twitchUser.systemMessage
                )
            },
            subMessage = {
                SystemChats.SubMessage(
                    message = twitchUser.userType,
                    systemMessage = twitchUser.systemMessage
                )
            },
            giftSubMessage = {
                SystemChats.GiftSubMessage(
                    message = twitchUser.userType,
                    systemMessage = twitchUser.systemMessage
                )
            },
            mysterySubMessage = {
                SystemChats.MysteryGiftSubMessage(
                    message = twitchUser.userType,
                    systemMessage = twitchUser.systemMessage
                )
            },
            errorMessage = {
                SystemChats.ErrorMessage(
                    message = twitchUser.userType!!,
                    alterMessage = twitchUser.displayName!!,
                    restartWebSocket = { restartWebSocket() }
                )
            },
            joinMessage = {
                SystemChats.JoinMessage(
                    message = twitchUser.userType!!
                )
            }
        )

    }


    /**
     * Composable function meant to display a message to the user indicating a reSub event has occurred
     *
     * @param message String personal message sent from the user. Meant to be displayed to the rest of chat
     * @param systemMessage String system message. This message is sent by the Twitch servers indicating how long the user has been subscribed for
     * */
    @Composable
    private fun ResubMessage(
        message: String?,
        systemMessage: String?
    ) {
        TwitchIRCSystemNotificationsBuilder.SystemChat(
                    messageHeader = {
                        ChatMessagesParts.MessageHeader(
                            contentDescription = stringResource(R.string.re_sub),
                            iconImageVector =Icons.Default.Star,
                            message =stringResource(R.string.re_sub)
                        )
                    },
                    messageText = {
                        ChatMessagesParts.MessageText(
                            message =message,
                            systemMessage =systemMessage
                        )
                    }
                )
    }

    /**
     * Composable function meant to display a message to the user once a [USERNOTICE tags](https://dev.twitch.tv/docs/irc/tags/#usernotice-tags) of ***sub***
     *
     * @param message String personal message sent from the user. Meant to be displayed to the rest of chat
     * @param systemMessage String system message. This message is sent by the Twitch servers indicating how long the user has been subscribed for
     * */
    @Composable
   private fun SubMessage(
        message: String?,
        systemMessage: String?
    ){
        TwitchIRCSystemNotificationsBuilder.SystemChat(
            messageHeader = {
                ChatMessagesParts.MessageHeader(
                    contentDescription = stringResource(R.string.sub),
                    iconImageVector =Icons.Default.Star,
                    message =stringResource(R.string.sub)
                )
            },
            messageText = {
                ChatMessagesParts.MessageText(
                    message =message,
                    systemMessage =systemMessage
                )
            }
        )
    }

    /**
     * Composable function meant to display a message to the user once a [USERNOTICE tags](https://dev.twitch.tv/docs/irc/tags/#usernotice-tags) of ***announcement***
     *
     * @param displayName String of the username that sent this chat
     * @param message String personal message sent from the user. Meant to be displayed to the rest of chat
     * @param systemMessage String system message. This message is sent by the Twitch servers indicating how long the user has been subscribed for
     * */
    @Composable
    private fun AnnouncementMessage(
        displayName: String?,
        message: String?,
        systemMessage: String?
    ){
        TwitchIRCSystemNotificationsBuilder.SystemChat(
            messageHeader = {
                ChatMessagesParts.MessageHeader(
                    contentDescription = stringResource(R.string.re_sub),
                    iconImageVector =Icons.Default.Star,
                    message =stringResource(R.string.announcement)
                )
            },
            messageText = {
                ChatMessagesParts.NamedMessageText(
                    displayName =displayName,
                    message =message,
                    systemMessage =systemMessage
                )
            }
        )
    }

    /**
     * Composable function meant to display a message indicating that we have successfully joined the chat room. This composable
     * is shown after the [Joining a Chat Room](https://dev.twitch.tv/docs/irc/join-chat-room/) process
     *
     * @param message String personal message sent from the user. Meant to be displayed to the rest of chat
     * */
    @Composable
    private fun JoinMessage(message: String) {
        ChatMessagesParts.SimpleText(message = message)
    }


    /**
     * [TwitchWebSocket][com.example.clicker.network.websockets.TwitchWebSocket] fails and triggers the
     * [onFailure()][com.example.clicker.network.websockets.TwitchWebSocket.onFailure] method. When this is triggered
     * the user is no longer connected to chat
     *
     * @param message String main message shown to the user
     * @param alterMessage String indicating what type of user has occured
     * @param restartWebSocket function which when triggered should attempt to reconnect the web socket
     * */
    @Composable
    private fun ErrorMessage(
        message: String,
        alterMessage: String,
        restartWebSocket: () -> Unit
    ){
        TwitchIRCSystemNotificationsBuilder.SystemChatAlert(
            messageHeader ={
                ChatMessagesParts.AlertHeader(alertMessage = alterMessage, alertIcon =Icons.Default.Warning )
            },
            messageText = { ChatMessagesParts.SimpleText(message)},
            messageButton = {
                ChatMessagesParts.ButtonWithText(
                    buttonAction = {restartWebSocket()},
                    buttonText = stringResource(R.string.click_to_connect)
                )
            }
        )
    }

    /**
     * Composable function meant to display a message to the user once a [USERNOTICE tags](https://dev.twitch.tv/docs/irc/tags/#usernotice-tags) of ***submysterygift***
     *
     * @param message String personal message sent from the user. Meant to be displayed to the rest of chat
     * @param systemMessage String system message. This message is sent by the Twitch servers indicating how long the user has been subscribed for
     * */
    @Composable
    private fun MysteryGiftSubMessage(
        message: String?,
        systemMessage: String?
    ) {

        TwitchIRCSystemNotificationsBuilder.SystemChat(
            messageHeader ={
                ChatMessagesParts.MessageHeader(
                    contentDescription = stringResource(R.string.random_gift_sub),
                    iconImageVector =Icons.Default.ShoppingCart,
                    message =stringResource(R.string.random_gift_sub)
                )
            },
            messageText ={
                ChatMessagesParts.MessageText(
                    message =message,
                    systemMessage =systemMessage
                )
            }
        )
    }


    /**
     * Composable function meant to display a message to the user once a [USERNOTICE tags](https://dev.twitch.tv/docs/irc/tags/#usernotice-tags) of ***subgift***
     *
     * @param message String personal message sent from the user. Meant to be displayed to the rest of chat
     * @param systemMessage String system message. This message is sent by the Twitch servers indicating how long the user has been subscribed for
     * */
    @Composable
    private fun GiftSubMessage(
        message: String?,
        systemMessage: String?
    ) {
        TwitchIRCSystemNotificationsBuilder.SystemChat(
            messageHeader ={
                ChatMessagesParts.MessageHeader(
                    contentDescription = stringResource(R.string.gift_sub),
                    iconImageVector =Icons.Default.Favorite,
                    message =stringResource(R.string.gift_sub)
                )
            },
            messageText ={
                ChatMessagesParts.MessageText(
                    message =message,
                    systemMessage =systemMessage
                )
            }
        )

    }

    /**
     * Composable function meant to display a system level event sent from the Twitch IRC servers. Read more about the
     * [NOTICE](https://dev.twitch.tv/docs/irc/commands/#notice) process
     *
     * @param color Color of the Text to be displayed
     * @param displayName String of the entity sending the message, will be `Room status`
     * @param message String of the message meant to be displayed
     * */
    @Composable
    private fun NoticeMessage(
        color: Color,
        displayName: String?,
        message: String?
    ) {
        ChatMessagesParts.UserSpecificText(
            color = color,
            displayName = displayName,
            message = message
        )

    }

    /**
     * Builders represents the most generic parts of [SystemChats] and should be thought of as UI layout guides used
     * by the implementations above
     * */
    private object Builders{

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
            noticeMessage:@Composable () -> Unit,
            announcementMessage:@Composable () -> Unit,
            resubMessage:@Composable () -> Unit,
            subMessage:@Composable () -> Unit,
            giftSubMessage:@Composable () -> Unit,
            mysterySubMessage:@Composable () -> Unit,
            errorMessage:@Composable () -> Unit,
            joinMessage:@Composable () -> Unit,

            ){
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


    /**
     * TwitchIRCSystemNotificationsBuilder is the most generic section of all the [SystemChat] composables. It is meant to
     * act as a layout guide for how all [SystemChat] implementations should look
     * */
    private object TwitchIRCSystemNotificationsBuilder{

        /**
         * The basic layout of the chat messages sent from the Twitch IRC server. An example of what a typical composable
         * looks like can be seen, [HERE](https://theplebdev.github.io/Modderz-style-guide/#SystemChat)
         *
         * @param messageHeader a Composable part meant to represent the header of this composable. Should contain important
         * information that the user needs
         *
         * @param messageText a Composable part that represents information that represents the main information meant to be shown to the user
         * */
        @Composable
        fun SystemChat(
            messageHeader:@Composable () -> Unit,
            messageText:@Composable () -> Unit,
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    messageHeader()
                    messageText()
                }
            }
        }

        /**
         * The basic layout of the chat messages sent when an error has occurred.
         * This is usually shown when the [TwitchWebSocket][com.example.clicker.network.websockets.TwitchWebSocket] disconnects
         * typical UI demonstration can be shown, [HERE](https://theplebdev.github.io/Modderz-style-guide/#SystemChatAlert)
         *
         * @param messageHeader a Composable part meant to represent the header of this composable. Should contain important
         * information that the user needs
         * @param messageText a Composable part that represents information that represents the main information meant to be shown to the user
         * @param messageButton a Composable composing of a button, prompting the user to take an action of some kind
         * */
        @Composable
        fun SystemChatAlert(
            messageHeader:@Composable () -> Unit,
            messageText:@Composable () -> Unit,
            messageButton:@Composable () -> Unit,
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    messageHeader()
                    messageText()
                    Row(modifier = Modifier
                        .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ){
                        messageButton()
                    }

                }
            }

        }
    } // END OF TwitchIRCSystemNotificationsBuilder


    /**
     * ChatMessagesParts represents all the possible individual Composables that can be used inside of a [TwitchIRCSystemNotificationsBuilder]
     * */
    private object ChatMessagesParts{


        /**
         * A [Row] containing a Icon and Text Composable, this should be used to quickly convey short and important information to the user
         *
         * @param contentDescription a String used to describe the image vector being used as the Icon
         * @param iconImageVector a image vector used to convery what category the text is in
         * @param message a String used to represent the information shown to the user. This should only be one word or two
         * */
        @Composable
        fun MessageHeader(
            contentDescription:String,
            iconImageVector: ImageVector,
            message:String
        ){
            Icons.Default.Lock
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = iconImageVector,
                    contentDescription =contentDescription,
                    modifier = Modifier
                        .size(30.dp),
                    tint = Color.White
                )
                Text(message, color = Color.White, fontSize = 20.sp)
            }
        }
        /**
         * A stylized Text meant to convery a large amount of information to the user
         *
         * @param message a String representing any message sent by the user
         * @param systemMessage a String representing any message sent by the Twitch IRC server
         * */
        @Composable
        fun MessageText(
            message: String?,
            systemMessage: String?
        ){
            val TwitchIRCMessage = systemMessage ?: ""
            val personalMessage = message ?: ""
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White, fontSize = 17.sp)) {
                        append(" $TwitchIRCMessage")
                        append(" $personalMessage")
                    }
                }
            )
        }

        /**
         * A stylized Text meant to convery a large amount of information to a specific user
         *
         * @param displayName a String representing the username of a specific user
         * @param message a String representing any message targeted at a user
         * @param systemMessage a String representing any message sent by the Twitch IRC server
         * */
        @Composable
        fun NamedMessageText(
            displayName: String?,
            message: String?,
            systemMessage: String?
        ){
            val personalMessage = message ?: ""
            val twitchIRCMessage = systemMessage ?: ""
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White, fontSize = 17.sp)) {
                        append("$displayName :")
                    }
                    withStyle(style = SpanStyle(color = Color.White, fontSize = 17.sp)) {
                        append(" $twitchIRCMessage")
                        append(" $personalMessage")
                    }
                }
            )

        }


        /**
         * A Simple text styled with the onPrimary color theme
         *
         * @param message a String representing a large amount of information
         * */
        @Composable
        fun SimpleText(message: String){
            Text(message,
                fontSize = 17.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 5.dp)
            )
        }

        /**
         * A [Row] meant to display text surrounded by two icons. This Composable is meant to represent the type of
         * error that has occured
         *
         * @param alertMessage a String representing the error. Should only be one word or two
         * @param alertIcon a image vector meant to represent the two icons that will surround the [alertMessage]
         * */
        @Composable
        fun AlertHeader(
            alertMessage:String,
            alertIcon:ImageVector
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = alertIcon,
                    contentDescription = stringResource(R.string.warning_icon_description),
                    modifier = Modifier
                        .size(30.dp),
                    tint = Color.Red
                )
                Text(alertMessage, color = Color.Red, fontSize = 20.sp, modifier = Modifier.padding(horizontal = 10.dp))
                Icon(
                    imageVector = alertIcon,
                    contentDescription = stringResource(R.string.warning_icon_description),
                    modifier = Modifier
                        .size(30.dp),
                    tint = Color.Red
                )
            }
        }

        /**
         * A [Button] meant to be used to do some sort of action
         *
         * @param buttonAction a function that will run when the button is clicked
         * @param buttonText a String that will be displayed on the Button
         * */
        @Composable
        fun ButtonWithText(
            buttonAction: () -> Unit,
            buttonText:String,
        ){
            Button(
                onClick = { buttonAction() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
            ) {
                Text(buttonText, color = Color.White)
            }
        }


        /**
         * A stylized text that will be used to conver a message to a specific user
         *
         * @param color a Color what will be used on the [displayName] text
         * @param displayName a String that will represent a specific user in chat
         * @param message a String that will represent the information meant to be conveyed to the [displayName] user
         * */
        @Composable
        fun UserSpecificText(
            color: Color,
            displayName: String?,
            message: String?
        ){
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = color, fontSize = 17.sp)) {
                        append("$displayName :")
                    }
                    append(" $message")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

    }
}

