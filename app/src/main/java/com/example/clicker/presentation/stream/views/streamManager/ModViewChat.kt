package com.example.clicker.presentation.stream.views.streamManager

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.clicker.R
import com.example.clicker.network.models.websockets.TwitchUserData
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

object  ModViewChat {

    /**
     * ChatMessageCard is the [Card] composable that is used to display the user message data
     *
     * @param offset is a float that is used to animate the dragging of this card
     * @param setDragging a function used to determine if the user is dragging the card or not
     * @param indivUserChatMessage a [TwitchUserData] object that carries all the related information related to an individual chat message
     * @param triggerBottomModal a function used to show or hide the bottom modal
     * */
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ChatMessageCard(
        offset:Float,
        setDragging:(Boolean)->Unit,
        indivUserChatMessage: TwitchUserData,
        triggerBottomModal:(Boolean)->Unit,
        updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
    ) {
        val scope = rememberCoroutineScope()

        val hapticFeedback = LocalHapticFeedback.current

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .absoluteOffset { IntOffset(x = offset.roundToInt(), y = 0) }
                .combinedClickable(
                    onDoubleClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        setDragging(true)
                    },

                    onClick = {
                        updateClickedUser(
                            indivUserChatMessage.displayName ?:"",
                            indivUserChatMessage.userId ?:"",
                            indivUserChatMessage.banned,
                            indivUserChatMessage.mod =="1"
                        )
                        Log.d("ChatMessageCardChatMessage","displayName -> ${indivUserChatMessage.displayName}")
                        Log.d("ChatMessageCardChatMessage","userType -> ${indivUserChatMessage.userType}")
                        Log.d("ChatMessageCardChatMessage","banned -> ${indivUserChatMessage.banned}")
                        Log.d("ChatMessageCardChatMessage","mod -> ${indivUserChatMessage.mod}")
                        scope.launch {
                            triggerBottomModal(true)
                        }
                    }
                )
            ,
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            shape = RectangleShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
        ) {
            TextWithChatBadges(
                displayName = "${indivUserChatMessage.displayName}",
                message = " ${indivUserChatMessage.userType}",
                isMod = indivUserChatMessage.mod == "1",
                isSub = indivUserChatMessage.subscriber == true,
                isMonitored =indivUserChatMessage.isMonitored,
                userNameColor =indivUserChatMessage.color?: "#7F00FF"
            )
        }
    }

    /**
     * TextWithChatBadges is a composable function that is responsible for displaying the chat message and for determining
     * which icon is shown before the user's [displayName]
     *  @param isMod a boolean used to determine if the user should have a Moderator icon displayed before their [displayName]
     *  @param isSub a boolean used to determine if the user should have a Sub icon displayed before their [displayName]
     *  @param isMonitored a boolean used to determine if the user should have a Monitored icon displayed before their [displayName]
     *  @param displayName a string representing the name that is shown in chat and identifies the user
     *  @param message a string representing the message a user has sent in the chat messages field
     *  @param userNameColor a string representing the hexcode of the color of the user's [displayName]
     **/
    @Composable
    fun TextWithChatBadges(
        isMod:Boolean,
        isSub:Boolean,
        isMonitored:Boolean,
        displayName:String,
        message:String,
        userNameColor:String,
    ){
        var color = Color(android.graphics.Color.parseColor(userNameColor))
        val modBadge = "https://static-cdn.jtvnw.net/badges/v1/3267646d-33f0-4b17-b3df-f923a41db1d0/1"
        val subBadge = "https://static-cdn.jtvnw.net/badges/v1/5d9f2208-5dd8-11e7-8513-2ff4adfae661/1"
        val modId = "modIcon"
        val subId = "subIcon"
        val monitorId ="monitorIcon"

        val text = buildAnnotatedString {
            if(isMonitored){
                appendInlineContent(monitorId, "[monitorIcon]")
            }
            if (isMod) {
                appendInlineContent(modId, "[icon]")
            }
            if (isSub) {
                appendInlineContent(subId, "[subicon]")
            }
            withStyle(style = SpanStyle(color = color, fontSize = MaterialTheme.typography.headlineSmall.fontSize)) {
                append("${displayName} :")
            }
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineSmall.fontSize)) {
                append(" ${message}")
            }
        }
        val inlineContent = mapOf(
            Pair(

                modId,
                InlineTextContent(

                    Placeholder(
                        width = MaterialTheme.typography.headlineMedium.fontSize,
                        height = MaterialTheme.typography.headlineMedium.fontSize,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                    )
                ) {
                    AsyncImage(
                        model = modBadge,
                        contentDescription = stringResource(R.string.moderator_badge_icon_description),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                    )
                }
            ),
            Pair(

                subId,
                InlineTextContent(

                    Placeholder(
                        width = MaterialTheme.typography.headlineMedium.fontSize,
                        height = MaterialTheme.typography.headlineMedium.fontSize,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                    )
                ) {
                    AsyncImage(
                        model = subBadge,
                        contentDescription = stringResource(R.string.sub_badge_icon_description),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                    )
                }
            ),
            Pair(

                monitorId,
                InlineTextContent(

                    Placeholder(
                        width = MaterialTheme.typography.headlineMedium.fontSize,
                        height = MaterialTheme.typography.headlineMedium.fontSize,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.visibility_24),
                        "contentDescription",
                        tint = androidx.compose.ui.graphics.Color.Yellow,
                        modifier = Modifier.size(30.dp)
                    )
                }
            )

        )
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
        ) {
            Text(
                text,
                inlineContent = inlineContent
            )

        }

    }


}

object ModActionMessage{
    @Composable
    fun DeletedMessage(){
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(){
                Icon(painter = painterResource(id =R.drawable.delete_outline_24), modifier = Modifier.size(30.dp), contentDescription = "message deleted")
                Text(text ="meanermeeny", color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineLarge.fontSize, fontWeight = FontWeight.Bold)

            }
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp)) {
                Text(text ="Message deleted:", color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineMedium.fontSize,modifier = Modifier.padding(bottom=5.dp))
                Text(text ="That was a dumb thing to do... and you look like you smell", color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineSmall.fontSize,)
                Spacer(modifier = Modifier.height(10.dp))

            }
            Spacer(modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
                .background(Color.White.copy(0.6f)))

        }
    }

    @Composable
    fun TimedUserOutMessage(){
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(){
                Icon(painter = painterResource(id =R.drawable.time_out_24), modifier = Modifier.size(30.dp), contentDescription = "message deleted")
                Text(text ="meanermeeny", color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineLarge.fontSize, fontWeight = FontWeight.Bold)

            }
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp)) {
                Text(text ="Timed out:", color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineMedium.fontSize,modifier = Modifier.padding(bottom=5.dp))
                Text(text ="meanermeeny was timed out for 600 seconds", color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineSmall.fontSize,)
                Spacer(modifier = Modifier.height(10.dp))

            }
            Spacer(modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
                .background(Color.White.copy(0.6f)))

        }
    }
    @Composable
    fun ClearChatMessage(){
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(){
                Icon(painter = painterResource(id =R.drawable.clear_chat_alt_24), modifier = Modifier.size(30.dp), contentDescription = "message deleted")
                Text(text ="Clear Chat", color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineLarge.fontSize, fontWeight = FontWeight.Bold)

            }
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp)) {
                Text(text ="Chat was cleared for non-Moderators viewing this room. Messages are preserved for Moderator review", color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineSmall.fontSize,)
                Spacer(modifier = Modifier.height(10.dp))

            }
            Spacer(modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
                .background(Color.White.copy(0.6f)))

        }
    }

    @Composable
    fun FollowersOnlyChatMessage(){
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(){
                Icon(painter = painterResource(id =R.drawable.person_outline_24), modifier = Modifier.size(30.dp), contentDescription = "message deleted")
                Text(text ="Followers-Only Chat", color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineLarge.fontSize, fontWeight = FontWeight.Bold)

            }
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp)) {
                Text(text ="Enabled with 0 minutes min following age", color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineSmall.fontSize,)
                Spacer(modifier = Modifier.height(10.dp))

            }
            Spacer(modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
                .background(Color.White.copy(0.6f)))

        }
    }

    @Composable
    fun BannedUserMessage(){
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(){
                Icon(painter = painterResource(id =R.drawable.ban_24), modifier = Modifier.size(30.dp), contentDescription = "user banned")
                Text(text ="meanermeeny", color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineLarge.fontSize, fontWeight = FontWeight.Bold)

            }
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp)) {
                Text(text ="Banned", color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineMedium.fontSize,modifier = Modifier.padding(bottom=5.dp))
                Spacer(modifier = Modifier.height(10.dp))

            }
            Spacer(modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
                .background(Color.White.copy(0.6f)))

        }
    }
}