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
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
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


object SystemChats {

    @Composable
    fun ResubMessage(
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
    @Composable
    fun SubMessage(
        message: String?,
        systemMessage: String?
    ){
        TwitchIRCSystemNotificationsBuilder.SystemChat(
            messageHeader = {
                ChatMessagesParts.MessageHeader(
                    contentDescription = stringResource(R.string.re_sub),
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
    @Composable
    fun AnnouncementMessage(
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
    @Composable
    fun JoinMessage(message: String) {
        ChatMessagesParts.SimpleText(message = message)
    }

    @Composable
    fun ErrorMessage(
        message: String,
        user: String,
        restartWebSocket: () -> Unit
    ){
        TwitchIRCSystemNotificationsBuilder.SystemChatAlert(
            messageHeader ={
                ChatMessagesParts.AlertHeader(alertMessage = user, alertIcon =Icons.Default.Warning )
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

    @Composable
    fun MysteryGiftSubMessage(
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

    @Composable
    fun GiftSubMessage(
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

    @Composable
    fun NoticeMessage(
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
    private object TwitchIRCSystemNotificationsBuilder{
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
    }

    private object ChatMessagesParts{
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

        @Composable
        fun SimpleText(message: String){
            Text(message,
                fontSize = 17.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 5.dp)
            )
        }
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
                color = Color.White
            )
        }

    }
}

