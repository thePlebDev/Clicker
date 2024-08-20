package com.example.clicker.presentation.stream.views.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.clicker.R
import com.example.clicker.presentation.sharedViews.fadingEdge


/**
 * ChatScope contains all the System level(sent from the Twitch servers) chat messages used throughout the application. ChatScope
 * contains 8 properties
 *
 * @param titleFontSize a [TextUnit] that is used to determine the the size of the messageType parameter used in all the composables
 * @param messageFontSize a [TextUnit] that is used to determine the the size of the message parameter used in all the composables
 *
 * @property NoticeMessages
 * @property AnnouncementMessages
 * @property ReSubMessage
 * @property SubMessages
 * @property GiftSubMessages
 * @property AnonGiftMessages
 * @property JoinMessage
 * @property ChatErrorMessage
 * */
@Stable
class ChatScope(
    private val titleFontSize: TextUnit,
    private val messageFontSize:TextUnit
) {
    /**
     * NoticeMessages is a wrapper composable around the [SystemMessageBox]
     * - Composable function meant to display a system level event sent from the Twitch IRC servers. Read more about the
     * [NOTICE](https://dev.twitch.tv/docs/irc/commands/#notice) process
     * - UI demonstration of NoticeMessages is [HERE](https://github.com/thePlebDev/Clicker/wiki/System-Level-Chat-messages#NoticeMessages)
     * */
    @Composable
    fun NoticeMessages(
        systemMessage: String?,
        message: String?
    ){
        SystemMessageBox(
            systemMessage =systemMessage?:"",
            message=message?:"",
            painter=painterResource(id = R.drawable.baseline_location_on_24),
            color= Color.Yellow,
            messageType = "Alert!"
        )
    }

    /**
     * AnnouncementMessages is a wrapper composable around the [SystemMessageBox]
     * - Composable function meant to display a message to the user once a
     * [USERNOTICE tags](https://dev.twitch.tv/docs/irc/tags/#usernotice-tags) of ***announcement***
     *
     * - UI demonstration of AnnouncementMessages is [HERE](https://github.com/thePlebDev/Clicker/wiki/System-Level-Chat-messages#announcemessages)
     * */
    @Composable
    fun AnnouncementMessages(
        message: String?
    ){
        SystemMessageBox(
            systemMessage ="",
            message=message?:"",
            painter=painterResource(id = R.drawable.baseline_announcement_24),
            color= Color.Blue,
            messageType = "Announcement"
        )
    }

    /**
     * ReSubMessage is a wrapper composable around the [SystemMessageBox]
     * - Composable function meant to display a message to the user indicating a reSub event has occurred
     * - UI demonstration of AnnouncementMessages is [HERE](https://github.com/thePlebDev/Clicker/wiki/System-Level-Chat-messages#resubmessage-)
     * */
    @Composable
    fun ReSubMessage(
        systemMessage: String?,
        message: String?
    ){
        SystemMessageBox(
            systemMessage =systemMessage?:"",
            message=message?:"",
            painter=painterResource(id = R.drawable.baseline_new_releases_24),
            color= MaterialTheme.colorScheme.secondary,
            messageType = "Re-Sub"
        )
    }

    /**
     * SubMessages is a wrapper composable around the [SystemMessageBox]
     * - Composable function meant to display a message to the user once a [USERNOTICE tags](https://dev.twitch.tv/docs/irc/tags/#usernotice-tags) of ***sub***
     * - UI demonstration of SubMessages is [HERE](https://github.com/thePlebDev/Clicker/wiki/System-Level-Chat-messages#submessages)
     * */
    @Composable
    fun SubMessages(
        systemMessage: String?,
        message: String?
    ){
        SystemMessageBox(
            systemMessage =systemMessage?:"",
            message=message?:"",
            painter=painterResource(id = R.drawable.baseline_star_outline),
            color= MaterialTheme.colorScheme.secondary,
            messageType = "Sub"
        )
    }

    /**
     * GiftSubMessages is a wrapper composable around the [SystemMessageBox]
     * -  Composable function meant to display a message to the user once a [USERNOTICE tags](https://dev.twitch.tv/docs/irc/tags/#usernotice-tags) of ***subgift***
     * - UI demonstration of GiftSubMessages is [HERE](https://github.com/thePlebDev/Clicker/wiki/System-Level-Chat-messages#giftmessages)
     * */
    @Composable
    fun GiftSubMessages(
        systemMessage: String?,
        message: String?
    ){
        SystemMessageBox(
            systemMessage =systemMessage?:"",
            message=message?:"",
            painter=painterResource(id = R.drawable.gift),
            color= MaterialTheme.colorScheme.secondary,
            messageType = "Gift Sub"
        )
    }


    /**
     * AnonGiftMessages is a wrapper composable around the [SystemMessageBox]
     * - Composable function meant to display a message to the user once a [USERNOTICE tags](https://dev.twitch.tv/docs/irc/tags/#usernotice-tags) of ***submysterygift***
     * - UI demonstration of AnonGiftMessages is [HERE](https://github.com/thePlebDev/Clicker/wiki/System-Level-Chat-messages#anongiftmessages)
     * */
    @Composable
    fun AnonGiftMessages(
        systemMessage: String?,
        message: String?
    ){
        SystemMessageBox(
            systemMessage =systemMessage?:"",
            message=message?:"",
            painter=painterResource(id = R.drawable.baseline_question_mark_24),
            color= MaterialTheme.colorScheme.secondary,
            messageType = "Anonymous gift sub"
        )
    }

    /**
     * Composable function meant to display a message indicating that we have successfully joined the chat room. This composable
     * is shown after the [Joining a Chat Room](https://dev.twitch.tv/docs/irc/join-chat-room/) process
     *
     * - UI demonstration of AnonGiftMessages is [HERE](https://github.com/thePlebDev/Clicker/wiki/System-Level-Chat-messages#joinmessage)
     *
     * @param message String personal message sent from the user. Meant to be displayed to the rest of chat
     * */
    @Composable
    fun JoinMessage(message: String) {
        SimpleText(message = message)
    }

    /**
     * SystemMessageBox is a private [Box] composable that is used internally to give the System level chat messages their gradient styling
     *
     * @param systemMessage a String representing the message that is sent from the Twitch IRC servers
     * @param message a String representing the message sent from the individual user
     * @param messageType A String representing the type of system level chat being sent
     * @param painter the [Painter] value shown in an icon to the right of [messageType]
     * @param color the color of the Gradient
     * */
    @Composable
   private fun SystemMessageBox(
        systemMessage:String,
        message:String,
        messageType: String,
        painter: Painter,
        color: Color,
    ){

        var height by remember { mutableStateOf(80.dp) }
        val localDensity = LocalDensity.current

        val sideFade = Brush.horizontalGradient(
            listOf(
                color, color.copy(alpha = 0.8f), color.copy(alpha = 0.6f),
                color.copy(alpha = 0.4f), color.copy(alpha = 0.2f), color.copy(alpha = 0.0f)
            ),
            startX = 0.0f,
            endX = 130.0f
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
        ){
            Spacer(modifier = Modifier
                .align(Alignment.CenterStart)
                .width(130.dp)
                .fadingEdge(sideFade)
                .clip(RoundedCornerShape(4.dp))
                .background(color)
                .height(height)

            )
            Row(){
                Spacer(modifier = Modifier
                    .height(height)

                )
                Spacer(modifier = Modifier
                    .width(17.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically){
                        //todo: this should be message type
                        Text("$messageType", color = MaterialTheme.colorScheme.onPrimary,fontSize=titleFontSize)
                        Spacer(modifier = Modifier
                            .width(6.dp)
                        )
                        Icon(
                            painter = painter,
                            contentDescription = messageType,
                            tint = color,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                    Text(
                        //todo: this should be message type
                        "$systemMessage $message",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize=messageFontSize,
                        modifier = Modifier.onGloballyPositioned {
                            height = with(localDensity) { it.size.height.toDp() + 30.dp }
                        }
                    )
                }
            }

        }

    }

    /**
     * ChatErrorMessage is a composable meant to signify to the user that an error has occurred relating to chat, ie, the intended
     * action has not taken place
     *
     * - UI demonstration of ChatErrorMessage is [HERE](https://github.com/thePlebDev/Clicker/wiki/System-Level-Chat-messages#ChatErrorMessage)
     *
     * @param message a String meant to display the error information to the user
     * */
    @Composable
    fun ChatErrorMessage(
        message:String
    ){

        val sideFade = Brush.horizontalGradient(
            listOf(
                Color.Red, Color.Red.copy(alpha = 0.8f), Color.Red.copy(alpha = 0.6f),
                Color.Red.copy(alpha = 0.4f), Color.Red.copy(alpha = 0.2f), Color.Red.copy(alpha = 0.0f)
            ),
            startX = 0.0f,
            endX = 130.0f
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
        ){
            Spacer(modifier = Modifier
                .align(Alignment.CenterStart)
                .width(130.dp)
                .fadingEdge(sideFade)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Red)
                .height(80.dp)

            )
            Row(){
                Spacer(modifier = Modifier
                    .height(80.dp)

                )
                Spacer(modifier = Modifier
                    .width(17.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically){
                        Text("Error", color = MaterialTheme.colorScheme.onPrimary,fontSize=titleFontSize)
                        Spacer(modifier = Modifier
                            .width(6.dp)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.error_outline_24),
                            contentDescription = "Chat error",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(message, color = MaterialTheme.colorScheme.onPrimary,fontSize=messageFontSize)
                }
            }

        }


    }// end or chat error message



    /**
     * SimpleText is a simple composable used to give the styling of [JoinMessage]
     *
     * @param message a String representing a large amount of information
     * */
    @Composable
    private fun SimpleText(message: String){
        Text(
            message,
            fontSize = titleFontSize,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(start = 5.dp)
        )
    }

}



//Tesitng out the UI for first time chatters:
@Composable
fun FirstTimeChatter(){
    val color = MaterialTheme.colorScheme.secondary

    var height by remember { mutableStateOf(80.dp) }
    val localDensity = LocalDensity.current
    val titleFontSize = MaterialTheme.typography.headlineMedium.fontSize
    val messageFontSize = MaterialTheme.typography.headlineSmall.fontSize

    val sideFade = Brush.horizontalGradient(
        listOf(
            color, color.copy(alpha = 0.8f), color.copy(alpha = 0.6f),
            color.copy(alpha = 0.4f), color.copy(alpha = 0.2f), color.copy(alpha = 0.0f)
        ),
        startX = 0.0f,
        endX = 130.0f
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
    ){
        Spacer(modifier = Modifier
            .align(Alignment.CenterStart)
            .width(130.dp)
            .fadingEdge(sideFade)
            .clip(RoundedCornerShape(4.dp))
            .background(color)
            .height(height)

        )
        Row(){
            Spacer(modifier = Modifier
                .height(height)

            )
            Spacer(modifier = Modifier
                .width(17.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically){
                    //todo: this should be message type
                    Text("First Time Chatter", color = MaterialTheme.colorScheme.onPrimary,fontSize=titleFontSize)
                    Spacer(modifier = Modifier
                        .width(6.dp)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.sparkle_awesome_24),
                        contentDescription = "First time chatter",
                        tint = color,
                        modifier = Modifier.size(25.dp)
                    )
                }
                Text(
                    //todo: this should be message type
                    "This is a message to determine what we are typing and when",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize=messageFontSize,
                    modifier = Modifier.onGloballyPositioned {
                        height = with(localDensity) { it.size.height.toDp() + 30.dp }
                    }
                )
            }
        }

    }

}


