package com.example.clicker.presentation.stream.views

import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
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
import coil.compose.AsyncImage
import com.example.clicker.R
import com.example.clicker.network.websockets.models.TwitchUserData
import com.example.clicker.util.SwipeableActionsState
import com.example.clicker.util.rememberSwipeableActionsState
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt


/**
 * SwipeToDelete contains all of the composables that are used to create and react to the swipe to delete functionality
 * */
object SwipeToDelete{


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
    ) {
        // no logic here, this should be a clean API wrapper


        SwipeToDeleteBuilder.DetectSwipeBox(
            twitchUser = twitchUser,
            deleteMessage ={messageId ->deleteMessage(messageId)},
            twitchUserId = twitchUser.id,
            cardThatMoves={ color, offset,fontSize ->
                SwipeToDeleteParts.ClickableCard(
                    twitchUser =twitchUser,
                    color = color,
                    bottomModalState = bottomModalState,
                    offset = offset,
                    fontSize = fontSize,
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
        )
    }

    /**
     * SwipeToDeleteBuilder is the most generic section of all the [SwipeToDelete] composables. It is meant to
     * act as a layout guide for how all [SwipeToDeleteChatMessages] implementations should look
     * */
    //builders
    private object SwipeToDeleteBuilder{

        /**
         * The basic layout and functionality for the swipe to delete functionality. A UI demonstration can be seen
         * [HERE](https://theplebdev.github.io/Modderz-style-guide/#DetectSwipeBox)
         *
         * @param twitchUser a [TwitchUserData][com.example.clicker.network.websockets.models] object that represents the entire chat user state
         * @param deleteMessage a function that will be used to initiate the ban user functionality
         * @param twitchUserId a string representing the id of the user whos chat message is being swiped
         * @param cardThatMoves a composable that represents the composable that is actually moving from the swipe to delete
         * functionality. This composable also gets sent the color,offset and the fontSize
         * */
        @Composable
        fun DetectSwipeBox(
            twitchUser: TwitchUserData,
            deleteMessage: (String) -> Unit,
            twitchUserId:String?,
            cardThatMoves:@Composable (color:Color,offset: Float,fontSize: TextUnit,) -> Unit,

            ){
            val subBadge = "https://static-cdn.jtvnw.net/badges/v1/5d9f2208-5dd8-11e7-8513-2ff4adfae661/1"
            val modBadge = "https://static-cdn.jtvnw.net/badges/v1/3267646d-33f0-4b17-b3df-f923a41db1d0/1"
            val scope = rememberCoroutineScope()
            var color by remember { mutableStateOf(Color(android.graphics.Color.parseColor(twitchUser.color))) }
            if(color == Color.Black){
                color = androidx.compose.material3.MaterialTheme.colorScheme.primary
            }

            val state = rememberSwipeableActionsState()

            var offset = state.offset.value

            val swipeThreshold = 130.dp
            val swipeThresholdPx = LocalDensity.current.run { swipeThreshold.toPx() }

            val thresholdCrossed = abs(offset) > swipeThresholdPx

            var backgroundColor by remember { mutableStateOf(Color.Black) }
            var fontSize = 17.sp

            if (thresholdCrossed) {
                backgroundColor = Color.Red

            } else {
                backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
            }


            // makes it so mods can not be swiped on
            val modDragState = DraggableState { delta ->
            }

            var dragState = state.draggableState
            if (twitchUser.mod == "1") {
                dragState = modDragState
            }
            if (twitchUser.deleted) {
                dragState = modDragState
                backgroundColor = Color.Red
                fontSize = 14.sp
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .background(backgroundColor)
                    .draggable(
                        orientation = Orientation.Horizontal,
                        enabled = true,
                        state = dragState,
                        onDragStopped = {
                            scope.launch {

                                if (thresholdCrossed) {
                                    state.resetOffset()
                                    deleteMessage(twitchUserId ?: "")
                                } else {
                                    state.resetOffset()
                                }
                            }
                        },
                        onDragStarted = {

                        }

                    )

            ) {
                cardThatMoves(
                    color, offset, fontSize
                )
            }
        }// end of DetectSwipeBox
    }//end of builder
    // parts

    /**
     * SwipeToDeleteParts represents all the possible individual composables that can be used inside of a [SwipeToDelete]
     * */
    private object SwipeToDeleteParts{


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
        @OptIn(ExperimentalMaterialApi::class)
        @Composable
        fun ClickableCard(
            twitchUser: TwitchUserData,
            color: Color,
            offset: Float,
            bottomModalState: ModalBottomSheetState,
            fontSize: TextUnit,
            updateClickedUser: (String, String, Boolean, Boolean) -> Unit,

            ){
            val coroutineScope = rememberCoroutineScope()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .absoluteOffset { IntOffset(x = offset.roundToInt(), y = 0) }
                    .clickable {
                        updateClickedUser(
                            twitchUser.displayName.toString(),
                            twitchUser.userId.toString(),
                            twitchUser.banned,
                            twitchUser.mod != "1"
                        )
                        coroutineScope.launch {
                            bottomModalState.show()
                        }
                    },
                backgroundColor = MaterialTheme.colorScheme.primary,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)

            ) {
                Column() {
                    CheckIfUserDeleted(twitchUser = twitchUser)
                    CheckIfUserIsBanned(twitchUser = twitchUser)
                    TextWithChatBadges(
                        twitchUser = twitchUser,
                        color = color,
                        fontSize = fontSize
                    )
                }
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
                    fontSize = 20.sp,
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
                Text(duration, fontSize = 20.sp, modifier = Modifier.padding(start = 5.dp))
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
            fontSize: TextUnit

        ){
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                ChatBadges(
                    username = "${twitchUser.displayName} :",
                    message = " ${twitchUser.userType}",
                    isMod = twitchUser.mod == "1",
                    isSub = twitchUser.subscriber == true,
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
            color: Color,
            textSize: TextUnit
        ) {
            //for not these values can stay here hard coded. Until I implement more Icon
            val modBadge = "https://static-cdn.jtvnw.net/badges/v1/3267646d-33f0-4b17-b3df-f923a41db1d0/1"
            val subBadge = "https://static-cdn.jtvnw.net/badges/v1/5d9f2208-5dd8-11e7-8513-2ff4adfae661/1"
            val modId = "modIcon"
            val subId = "subIcon"
            val text = buildAnnotatedString {
                // Append a placeholder string "[icon]" and attach an annotation "inlineContent" on it.
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
                            width = 20.sp,
                            height = 20.sp,
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
                            width = 20.sp,
                            height = 20.sp,
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
    }// end of parts
}









