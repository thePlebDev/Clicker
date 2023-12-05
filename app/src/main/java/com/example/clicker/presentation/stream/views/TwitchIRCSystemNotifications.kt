package com.example.clicker.presentation.stream.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
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

object TwitchIRCSystemNotificationsBuilder{
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
}
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
                            iconImageVector =Icons.Default.Star
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
                    iconImageVector =Icons.Default.Star
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
}

object ChatMessagesParts{
    @Composable
    fun MessageHeader(
        contentDescription:String,
        iconImageVector: ImageVector
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
            Text(stringResource(R.string.sub), color = Color.White, fontSize = 20.sp)
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
}