package com.example.clicker.presentation.stream.views.chat

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.clicker.R
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.network.websockets.MessageType
import com.example.clicker.presentation.sharedViews.ErrorScope
import com.example.clicker.presentation.stream.views.ChatBadges
import com.example.clicker.presentation.stream.views.CheckIfUserDeleted
import com.example.clicker.presentation.stream.views.CheckIfUserIsBanned
import com.example.clicker.presentation.stream.views.DualIconsButton
import com.example.clicker.presentation.stream.views.FilteredMentionLazyRow
import com.example.clicker.presentation.stream.views.ShowIconBasedOnTextLength
import com.example.clicker.presentation.stream.views.ShowModStatus
import com.example.clicker.presentation.stream.views.StylizedTextField
import com.example.clicker.presentation.stream.views.TextWithChatBadges

import com.example.clicker.presentation.stream.views.isScrolledToEnd
import com.example.clicker.presentation.stream.views.streamManager.util.rememberDraggableActions
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun ChatUI(
    twitchUserChat: List<TwitchUserData>,
    showBottomModal:()->Unit,
    updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
    showTimeoutDialog:()->Unit,
    showBanDialog:()->Unit,
    doubleClickMessage:(String)->Unit,
    //below is what is needed for the chat UI
    filteredChatList: List<String>,
    textFieldValue: MutableState<TextFieldValue>,
    clickedAutoCompleteText: (String) -> Unit,
    modStatus: Boolean?,
    sendMessageToWebSocket: (String) -> Unit,
    showModal: () -> Unit,
    showOuterBottomModalState:() ->Unit,
    newFilterMethod:(TextFieldValue) ->Unit,
    orientationIsVertical:Boolean,
    notificationAmount:Int
){
    val lazyColumnListState = rememberLazyListState()
    var autoscroll by remember { mutableStateOf(true) }

    ChatUIBox(
        determineScrollState={
            DetermineScrollState(
                lazyColumnListState = lazyColumnListState,
                setAutoScrollFalse = { autoscroll = false },
                setAutoScrollTrue = { autoscroll = true },
            )
        },
        chatUI={modifier ->
            ChatUILazyColumn(
                lazyColumnListState=lazyColumnListState,
                twitchUserChat=twitchUserChat,
                autoscroll=autoscroll,
                showBottomModal={showBottomModal()},
                showTimeoutDialog={showTimeoutDialog()},
                showBanDialog={showBanDialog()},
                updateClickedUser = {  username, userId,isBanned,isMod ->
                    updateClickedUser(
                        username,
                        userId,
                        isBanned,
                        isMod
                    )
                },
                doubleClickMessage={username ->doubleClickMessage(username)},
                modifier=modifier

            )
        },
        scrollToBottom ={modifier ->
            ScrollToBottom(
                scrollingPaused = !autoscroll,
                enableAutoScroll = { autoscroll = true },
                modifier = modifier
            )
        },
        enterChat = {modifier ->
            EnterChatColumn(
                modifier = modifier,
                filteredRow = {
                    FilteredMentionLazyRow(
                        filteredChatList = filteredChatList,
                        clickedAutoCompleteText = { username ->
                            clickedAutoCompleteText(
                                username
                            )
                        }
                    )
                },
                showModStatus = {
                    ShowModStatus(
                        modStatus =modStatus,
                        showOuterBottomModalState={showOuterBottomModalState()},
                        orientationIsVertical =orientationIsVertical,
                        notificationAmount=notificationAmount
                    )
                },
                stylizedTextField ={boxModifier ->
                    StylizedTextField(
                        modifier = boxModifier,
                        textFieldValue = textFieldValue,
                        newFilterMethod = {newTextValue ->newFilterMethod(newTextValue)},

                        )
                },
                showIconBasedOnTextLength ={
                    ShowIconBasedOnTextLength(
                        textFieldValue =textFieldValue,
                        chat = {item -> sendMessageToWebSocket(item)},
                        showModal ={showModal()}
                    )
                },
            )

        }

        )
}

@Composable
 private fun ChatUIBox(
    determineScrollState: @Composable ImprovedChatUI.() -> Unit,
    chatUI: @Composable ImprovedChatUI.(modifier: Modifier) -> Unit,
    scrollToBottom: @Composable ImprovedChatUI.(modifier: Modifier) -> Unit,
    enterChat: @Composable ImprovedChatUI.(modifier: Modifier) -> Unit,
){
    val chatUIScope = remember(){ ImprovedChatUI() }
    with(chatUIScope){
        Box(modifier = Modifier.fillMaxSize()){
            Column(Modifier.fillMaxSize()) {

                chatUI(modifier =Modifier.weight(1f))
                enterChat(
                    Modifier
                        .fillMaxWidth(),
                )
            }
            determineScrollState()

            scrollToBottom(modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp))
        }
    }


}



@Stable
private class ImprovedChatUI(){
    @Composable
    fun DetermineScrollState(
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
    fun ChatUILazyColumn(
        lazyColumnListState: LazyListState,
        twitchUserChat: List<TwitchUserData>,
        autoscroll:Boolean,
        showBottomModal:()->Unit,
        showTimeoutDialog:()->Unit,
        showBanDialog:()->Unit,
        updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
        doubleClickMessage:(String)->Unit,
        modifier: Modifier
    ){
        val coroutineScope = rememberCoroutineScope()
        LazyColumn(
            modifier =modifier,
            state = lazyColumnListState
        ){
            coroutineScope.launch {
                if (autoscroll) {
                    lazyColumnListState.scrollToItem(twitchUserChat.size)
                }
            }
            items(
                twitchUserChat,
                key = { item -> item.id ?:"" }
            ) {indivChatMessage ->
                ChatMessages(
                    indivChatMessage,
                    showBottomModal={showBottomModal()},
                    updateClickedUser = {  username, userId,isBanned,isMod ->
                        updateClickedUser(
                            username,
                            userId,
                            isBanned,
                            isMod
                        )
                    },
                    showTimeoutDialog ={showTimeoutDialog()},
                    showBanDialog={showBanDialog()},
                    doubleClickMessage={username ->doubleClickMessage(username)}

                )

            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun ChatMessages(
        twitchChatMessage: TwitchUserData,
        showBottomModal:()->Unit,
        updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
        showTimeoutDialog:()->Unit,
        showBanDialog:()->Unit,
        doubleClickMessage:(String)->Unit,
    ){
        val titleFontSize = MaterialTheme.typography.headlineMedium.fontSize
        val messageFontSize = MaterialTheme.typography.headlineSmall.fontSize
        val chatScope = remember(){ ChatScope(titleFontSize,messageFontSize) }
        val errorScope = remember(){ ErrorScope(messageFontSize) }
        val color = remember { mutableStateOf(Color(android.graphics.Color.parseColor(twitchChatMessage.color))) }
        if(color.value == Color.Black){
            color.value = MaterialTheme.colorScheme.primary
        }

        with(chatScope) {

            when (twitchChatMessage.messageType) {
                MessageType.NOTICE -> { //added
                    NoticeMessages(
                        systemMessage = "",
                        message = twitchChatMessage.userType
                    )
                }

                MessageType.USER -> { //added
                    HorizontalDragDetectionBox(
                        itemBeingDragged = {dragOffset ->
                            ClickableCard(
                                twitchUser =twitchChatMessage,
                                color = color.value,
                                fontSize = messageFontSize,
                                showBottomModal={showBottomModal()},
                                updateClickedUser = {  username, userId,isBanned,isMod ->
                                    updateClickedUser(
                                        username,
                                        userId,
                                        isBanned,
                                        isMod
                                    )
                                },
                                offset = if (twitchChatMessage.mod != "1") dragOffset else 0f,
                                doubleClickMessage ={username ->doubleClickMessage(username)}
                            )
                        },
                        quarterSwipeLeftAction={
                            Log.d("quarterSwipeLeftAction","Cclicked")
                            if(twitchChatMessage.mod != "1"){
                                updateClickedUser(
                                    twitchChatMessage.displayName?:"",
                                    twitchChatMessage.userId?:"",
                                    twitchChatMessage.banned,
                                    twitchChatMessage.mod == "1"
                                )
                                showTimeoutDialog()
                            }

                        },
                        quarterSwipeRightAction={
                            Log.d("quarterSwipeLeftAction","Cclicked")
                            if(twitchChatMessage.mod != "1"){
                                updateClickedUser(
                                    twitchChatMessage.displayName?:"",
                                    twitchChatMessage.userId?:"",
                                    twitchChatMessage.banned,
                                    twitchChatMessage.mod == "1"
                                )
                                showBanDialog()
                            }

                        },
                        swipeEnabled = true,
                        twoSwipeOnly= false
                    )

                }

                MessageType.ANNOUNCEMENT -> { //added
                    AnnouncementMessages(
                        message = "${twitchChatMessage.displayName}: ${twitchChatMessage.systemMessage}"
                    )
                }

                MessageType.RESUB -> { //added
                    ReSubMessage(
                        systemMessage = twitchChatMessage.systemMessage,
                        message = twitchChatMessage.userType,
                    )
                }

                MessageType.SUB -> { //added
                    SubMessages(
                        systemMessage = twitchChatMessage.systemMessage,
                        message = twitchChatMessage.userType,
                    )
                }
                // MYSTERYGIFTSUB,GIFTSUB
                MessageType.GIFTSUB -> { //added
                    GiftSubMessages(
                        message = twitchChatMessage.userType,
                        systemMessage = twitchChatMessage.systemMessage
                    )
                }

                MessageType.MYSTERYGIFTSUB -> { //
                    AnonGiftMessages(
                        message = twitchChatMessage.userType,
                        systemMessage = twitchChatMessage.systemMessage
                    )
                }

                MessageType.ERROR -> {
                    with(errorScope){
                        ChatErrorMessage(twitchChatMessage.userType ?:"")
                    }
                }

                MessageType.JOIN -> {
                    JoinMessage(
                        message = twitchChatMessage.userType ?:""
                    )
                }

                else -> {

                }

            }
        }



    }


    @Composable
    fun ScrollToBottom(
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
                DualIconsButton(
                    buttonAction = { enableAutoScroll() },
                    iconImageVector = Icons.Default.ArrowDropDown,
                    iconDescription = stringResource(R.string.arrow_drop_down_description),
                    buttonText = stringResource(R.string.scroll_to_bottom)

                )
            }
        }
    }
}


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
    offset: Float,
    showBottomModal:()->Unit,
    fontSize: TextUnit,
    updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
    doubleClickMessage:(String)->Unit,


    ){
    Log.d("TestingIndivChatMessage",twitchUser.userType ?:"")
    val showIcon = remember { mutableStateOf(false) }


    Column(
        modifier = Modifier.combinedClickable(
            enabled = true,
            onDoubleClick = {
                showIcon.value = true
                doubleClickMessage(twitchUser.displayName?:"")
            },
            onClick = {
                updateClickedUser(
                    twitchUser.displayName?:"",
                    twitchUser.userId?:"",
                    twitchUser.banned,
                    twitchUser.mod == "1"
                )
                showBottomModal()
            }
        )


    ) {
        Spacer(modifier =Modifier.height(5.dp))
        Box(
            modifier = Modifier
                .absoluteOffset { IntOffset(x = offset.roundToInt(), y = 0) }
        ){
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
            ) {
                CheckIfUserDeleted(twitchUser = twitchUser)
                CheckIfUserIsBanned(twitchUser = twitchUser)
                TextWithChatBadges(
                    twitchUser = twitchUser,
                    color = color,
                    fontSize = fontSize,
                )
            }
            if(showIcon.value){
                DoubleClickSeemsGoodIcon()
            }


        }

        Spacer(modifier =Modifier.height(5.dp))

    }

}
@Composable
fun DoubleClickSeemsGoodIcon(){

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

/**
 * HorizontalDragDetectionBox is a [Box] that will detect the user's drag movement and will move [itemBeingDragged] accordingly. Also, depending
 * of if the thresholds are dragged across functions such as [quarterSwipeRightAction], [quarterSwipeLeftAction] and [halfSwipeAction]
 * once the drag stopped. Icons such as [halfSwipeIconResource], [quarterSwipeLeftIconResource] and [quarterSwipeRightIconResource] will
 * also be shown when the user crosses those thresholds
 *
 * @param itemBeingDragged a composable function that will be dragged when the drags it accross the screen.
 * @param twoSwipeOnly a boolean that is used to determine of there are functions for quarter swipes and half swipes or just quarter swipes.
 * A true value indicates that [quarterSwipeRightAction] and [quarterSwipeLeftAction] will get triggered. A false value means that
 * [quarterSwipeRightAction], [quarterSwipeLeftAction] and [halfSwipeAction] will get triggered
 * @param quarterSwipeRightAction is a function that will be called if a user swipes and passes the threshold of 0.25 of [itemBeingDragged] width
 * @param quarterSwipeLeftAction is a function that will be called if a user swipes and passes the threshold of -1*(0.25) of [itemBeingDragged] width
 * @param halfSwipeAction a optional function that will be called if [twoSwipeOnly] is set to false and the user's drag passes
 * the threshold of +/- 0.5 of [itemBeingDragged] width
 * @param halfSwipeIconResource is a [Painter] that will be shown to the user if the half swipe threshold is crossed and [twoSwipeOnly] is false
 * @param quarterSwipeLeftIconResource is a [Painter] that will be shown to the user if the -1 *(quarter) swipe threshold is crossed
 * @param quarterSwipeRightIconResource is a [Painter] that will be shown to the user if the quarter swipe threshold is crossed
 * @param hideIconColor: a [Color] that the icons will be set to hide them from the user
 * @param showIconColor: a [Color] that the icons will be set to reveal them to the user
 * */
@Composable
fun HorizontalDragDetectionBox(
    itemBeingDragged:@Composable (dragOffset:Float) -> Unit,
    twoSwipeOnly:Boolean,
    quarterSwipeRightAction:()->Unit,
    quarterSwipeLeftAction:()->Unit,
    halfSwipeAction:()->Unit={},
    halfSwipeIconResource: Painter = painterResource(id = R.drawable.delete_outline_24),
    quarterSwipeLeftIconResource: Painter = painterResource(id = R.drawable.time_out_24),
    quarterSwipeRightIconResource: Painter = painterResource(id = R.drawable.ban_24),
    hideIconColor: Color = MaterialTheme.colorScheme.primary,
    showIconColor: Color = MaterialTheme.colorScheme.onPrimary,
    swipeEnabled:Boolean
){
    var iconShownToUser: Painter = painterResource(id = R.drawable.ban_24)
    var dragging by remember{ mutableStateOf(true) }
    val state = rememberDraggableActions()
    val offset = if(swipeEnabled) state.offset.value else 0f
    var iconColor = hideIconColor

    //todo: this could probably use derivedstateof
    if(dragging && !twoSwipeOnly){
        if (state.offset.value >= (state.halfWidth)) {
            iconShownToUser =halfSwipeIconResource
            iconColor = showIconColor
        }
        else if (state.offset.value <= -(state.halfWidth)){
            iconShownToUser =halfSwipeIconResource
            iconColor = showIconColor
        }
        else if (state.offset.value <= -(state.quarterWidth)){
            iconShownToUser =quarterSwipeLeftIconResource
            iconColor = showIconColor
        }
        else if (state.offset.value >= (state.quarterWidth)){
            iconShownToUser = quarterSwipeRightIconResource
            iconColor = showIconColor
        }
    }
    else if(dragging && twoSwipeOnly){
        if (state.offset.value <= -(state.quarterWidth)){
            iconShownToUser =quarterSwipeLeftIconResource
            iconColor = showIconColor
        }
        else if (state.offset.value >= (state.quarterWidth)){
            iconShownToUser = quarterSwipeRightIconResource
            iconColor = showIconColor
        }
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .draggable(
                orientation = Orientation.Horizontal,
                onDragStopped = {
                    if (twoSwipeOnly && swipeEnabled) {
                        state.checkQuarterSwipeThresholds(
                            leftSwipeAction = {
                                quarterSwipeLeftAction()
                            },
                            rightSwipeAction = {
                                quarterSwipeRightAction()
                            }
                        )
                    } else if (swipeEnabled) {
                        state.checkDragThresholdCrossed(
                            deleteMessageSwipe = {
                                halfSwipeAction()
                            },
                            timeoutUserSwipe = {
                                quarterSwipeLeftAction()
                            },
                            banUserSwipe = {
                                quarterSwipeRightAction()
                            }
                        )
                    }

                    dragging = false
                    state.resetOffset()
                },
                onDragStarted = {
                    dragging = true
                },


                enabled = true,
                state = state.draggableState
            )
            .onGloballyPositioned { layoutCoordinates ->
                state.setWidth(layoutCoordinates.size.width)
            }
    ){

        Icon(painter = iconShownToUser, contentDescription = "",tint = iconColor, modifier = Modifier
            .align(Alignment.CenterEnd)
            .padding(end = 10.dp)
        )
        Icon(painter = iconShownToUser, contentDescription = "",tint = iconColor,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 10.dp)
        )


        itemBeingDragged(offset)

    }


}

/**
 * This is the entire chat textfield with the filtered row above it
 * */
@Composable
fun EnterChatColumn(
    modifier: Modifier,
    filteredRow:@Composable () -> Unit,
    showModStatus:@Composable () -> Unit,
    stylizedTextField:@Composable (modifier:Modifier) -> Unit,
    showIconBasedOnTextLength:@Composable () -> Unit,
) {

    Column(
        modifier = modifier.background(MaterialTheme.colorScheme.primary)

    ) {
        filteredRow()
        Row(modifier = Modifier.background(MaterialTheme.colorScheme.primary),
            verticalAlignment = Alignment.CenterVertically){
            showModStatus()
            stylizedTextField(modifier = Modifier.weight(2f))
            showIconBasedOnTextLength()
        }
    }
}
