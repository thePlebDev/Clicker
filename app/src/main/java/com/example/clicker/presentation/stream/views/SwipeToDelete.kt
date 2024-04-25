package com.example.clicker.presentation.stream.views

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.clicker.R
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.presentation.modView.views.HorizontalDragDetectionBox
import com.example.clicker.presentation.stream.views.streamManager.ModViewChat
import com.example.clicker.util.rememberSwipeableActionsState
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt


/**
 * SwipeToDelete contains all of the composables that are used to create and react to the swipe to delete functionality
 * */



    /**
     * SwipeToDeleteChatMessages is the implementation that represents the entire feature chat feature of swiping to delete.
     *
     *
     * @param twitchUser a [TwitchUserData][com.example.clicker.network.websockets.models] object that represents the entire chat user state
     * @param bottomModalState the state of [ModalBottomSheetLayout][androidx.compose.material] and used for when a user's chat message is clicked
     * @param updateClickedUser a function used to update all the state related to the clicked user inside the ViewModel
     * @param deleteMessage a function that will be used to initiate the ban user functionality
     * */
    @OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
    @Composable
    fun SwipeToDeleteChatMessages(
        twitchUser: TwitchUserData,
        bottomModalState: ModalBottomSheetState,
        updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
        deleteMessage: (String) -> Unit,
        toggleTimeoutDialog:()->Unit,
        toggleBanDialog:()->Unit,

    ) {
        // no logic here, this should be a clean API wrapper
        var color by remember { mutableStateOf(Color(android.graphics.Color.parseColor(twitchUser.color))) }
        if(color == Color.Black){
            color = MaterialTheme.colorScheme.primary
        }
        var iconXOffset by remember { mutableFloatStateOf(0f) }

        HorizontalDragDetectionBox(
            itemBeingDragged = { dragOffset ->
//                ClickableCard(
//                    twitchUser =twitchUser,
//                  //  color = color,
//                    bottomModalState = bottomModalState,
//                    offset = if (twitchUser.mod != "1") dragOffset else 0f,
//                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
//                    updateClickedUser = {  username, userId,isBanned,isMod ->
//                        updateClickedUser(
//                            username,
//                            userId,
//                            isBanned,
//                            isMod
//                        )
//                    },
//                    iconXOffset =iconXOffset,
//                    updateIconXOffset = {
//                            value ->iconXOffset = value
//                        Log.d("doubleClickIcons","iconXOffset -->${iconXOffset}")
//                    }
//                )
            },
            quarterSwipeLeftAction = {
//                updateClickedUser(
//                    twitchUser.displayName?:"",
//                    twitchUser.userId?:"",
//                    twitchUser.banned,
//                    twitchUser.mod == "1"
//                )
//                toggleTimeoutDialog()
            },
            quarterSwipeRightAction = {

//                updateClickedUser(
//                    twitchUser.displayName?:"",
//                    twitchUser.userId?:"",
//                    twitchUser.banned,
//                    twitchUser.mod == "1"
//                )
//                toggleBanDialog()
            },
            halfSwipeAction = {
               // deleteMessage(twitchUser.id ?: "")

            },
            twoSwipeOnly = false,
            swipeEnabled = false

        )

    }



    // parts

    /**
     * SwipeToDeleteParts represents all the possible individual composables that can be used inside of a [SwipeToDelete]
     * */


        /**
         * ClickableCard is the composable that implements the functionality that allows the user to click on a chat message
         * and have the bottom modal pop up
         *
         * @param twitchUser a [TwitchUserData][com.example.clicker.network.websockets.models.TwitchUserData] object that represents the state of an individual user and their chat message
         * @param color  a Color that will eventually be passed to [ChatBadges] and represent the color of the text
         * @param offset a Float representing how far this composable will be moving on screen
         * @param bottomModalState the state of a [ModalBottomSheetState][androidx.compose.material]
         * @param fontSize the font size of the text inside the [ChatBadges] composable
         * @param updateClickedUser a function that will run once this composable is clicked and will update the ViewModel with information
         * about the clicked user
         * */
        @OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
        @Composable
        fun ClickableCard(
            twitchUser: TwitchUserData,
            color: Color,
//            offset: Float,
            bottomModalState: ModalBottomSheetState,
            fontSize: TextUnit,
            updateClickedUser: (String, String, Boolean, Boolean) -> Unit,



            ){
            Log.d("TestingIndivChatMessage",twitchUser.userType ?:"")
            val coroutineScope = rememberCoroutineScope()
          //  val iconXOffset by remember { mutableStateOf(0f) }
            val iconXOffset = remember { mutableStateOf(0f) }
            val showIcon = remember { mutableStateOf(false) }
            Log.d("DOUBLECLICKERS","RECOMP->${iconXOffset}")

            Column(
                modifier = Modifier.combinedClickable(
                    enabled = true,
                    onDoubleClick = {
                        showIcon.value = true
                    },
                    onClick = {
                        updateClickedUser(
                            twitchUser.displayName?:"",
                            twitchUser.userId?:"",
                            twitchUser.banned,
                            twitchUser.mod == "1"
                        )
//                        coroutineScope.launch {
//                            bottomModalState.show()
//                        }
                    }
                )

            ) {
                Spacer(modifier =Modifier.height(5.dp))
                Box(){
                        Column(modifier = Modifier
                            .fillMaxWidth()) {
                            CheckIfUserDeleted(twitchUser = twitchUser)
                            CheckIfUserIsBanned(twitchUser = twitchUser)
                            TextWithChatBadges(
                                twitchUser = twitchUser,
                                color = color,
                                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                            )
                        }
                    if(showIcon.value){
                        AysncImageTest()
                    }


                }



                Spacer(modifier =Modifier.height(5.dp))

            }

        }
@Composable
fun AysncImageTest(){

    val size = remember { Animatable(10F) }
    LaunchedEffect(true){
        size.animateTo(40f)
    }
    Box(modifier = Modifier.fillMaxWidth().padding(end=30.dp)){
        AsyncImage(
            model = "https://static-cdn.jtvnw.net/emoticons/v2/64138/static/light/1.0",
            contentDescription = stringResource(R.string.moderator_badge_icon_description),
            //alpha =iconOpacity,
            modifier = Modifier
                .size(size.value.dp)
                .align(Alignment.CenterEnd)
        )
    }

}


        /**
         * CheckIfUserDeleted is the composable that will be used to determine if there should be extra information shown
         * depending if the user's message has been deleted or not
         *
         * @param twitchUser a [TwitchUserData][com.example.clicker.network.websockets.models.TwitchUserData] object that represents the state of an individual user and their chat message
         * */
        @Composable
        fun CheckIfUserDeleted(twitchUser: TwitchUserData){
            if (twitchUser.deleted) {
                Text(
                    stringResource(R.string.moderator_deleted_comment),
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    modifier = Modifier.padding(start = 5.dp),
                    color = androidx.compose.material.MaterialTheme.colors.onPrimary
                )
            }
        }
        /**
         *
         * CheckIfUserIsBanned is the composable that will be used to determine if there should be extra information shown
         * depending if the user has been banned by a moderator
         *
         * @param twitchUser a [TwitchUserData][com.example.clicker.network.websockets.models.TwitchUserData] object that represents the state of an individual user and their chat message
         *
         * */
        @Composable
        fun CheckIfUserIsBanned(twitchUser: TwitchUserData){
            if (twitchUser.banned) {
                val duration = if (twitchUser.bannedDuration != null) "Banned for ${twitchUser.bannedDuration} seconds" else "Banned permanently"
                Text(duration, fontSize = MaterialTheme.typography.headlineMedium.fontSize, modifier = Modifier.padding(start = 5.dp))
            }
        }

        /**
         *
         * TextWithChatBadges is really just a wrapper class around [ChatBadges] to allow us to use it a little more cleanly
         * throughout our code
         *
         * @param twitchUser a [TwitchUserData][com.example.clicker.network.websockets.models.TwitchUserData] object that represents the state of an individual user and their chat message
         * @param color  a color passed to [ChatBadges]
         * @param fontSize a font size passed to [ChatBadges]
         * */
        @Composable
        fun TextWithChatBadges(
            twitchUser: TwitchUserData,
            color: Color,
            fontSize: TextUnit,

        ){
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                ChatBadges(
                    username = "${twitchUser.displayName} :",
                    message = " ${twitchUser.userType}",
                    isMod = twitchUser.mod == "1",
                    isSub = twitchUser.subscriber == true,
                    isMonitored =twitchUser.isMonitored,
                    color = color,
                    textSize = fontSize
                )

            } // end of the row
        }

        /**
         *
         * ChatBadges is the composable that is responsible for showing the chat badges(mod or sub) beside the users username
         *
         * @param username a String representing the user that is currently sending chats
         * @param message  a String representing the message sent by this user
         * @param isMod a boolean determining if the user is a moderator or not
         * @param isSub a boolean determining if the user is a subscriber or not
         * @param color the color of the text
         * @param textSize the size of the text
         * */
        @Composable
        fun ChatBadges(
            username: String,
            message: String,
            isMod: Boolean,
            isSub: Boolean,
            isMonitored:Boolean,
            color: Color,
            textSize: TextUnit
        ) {
            //for not these values can stay here hard coded. Until I implement more Icon
//            val color = MaterialTheme.colorScheme.secondary
//            val textSize = MaterialTheme.typography.headlineSmall.fontSize
            val modBadge = "https://static-cdn.jtvnw.net/badges/v1/3267646d-33f0-4b17-b3df-f923a41db1d0/1"
            val subBadge = "https://static-cdn.jtvnw.net/badges/v1/5d9f2208-5dd8-11e7-8513-2ff4adfae661/1"
            val modId = "modIcon"
            val subId = "subIcon"
            val monitorId ="monitorIcon"
            val text = buildAnnotatedString {
                // Append a placeholder string "[icon]" and attach an annotation "inlineContent" on it.
                if(isMonitored){
                    appendInlineContent(monitorId, "[monitorIcon]")
                }

                if (isMod) {
                    appendInlineContent(modId, "[icon]")
                }
                if (isSub) {
                    appendInlineContent(subId, "[subicon]")
                }
                withStyle(style = SpanStyle(color = color, fontSize = textSize)) {
                    append(username)
                }
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onPrimary)) {
                    append(message)
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
                            tint= Color.Yellow,
                            modifier = Modifier.size(35.dp)
                        )
                    }
                )

            )

            Text(
                text = text,
                inlineContent = inlineContent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                color = color,
                fontSize = textSize
            )
        }











