package com.example.clicker.presentation.modView.views

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.clicker.R
import com.example.clicker.network.clients.BlockedTerm
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.network.websockets.AutoModQueueMessage
import com.example.clicker.network.websockets.MessageType
import com.example.clicker.presentation.modView.ListTitleValue
import com.example.clicker.presentation.stream.views.isScrolledToEnd
import com.example.clicker.presentation.stream.views.streamManager.ModActionMessage
import com.example.clicker.presentation.stream.views.streamManager.ModView
import com.example.clicker.presentation.stream.views.streamManager.ModViewChat
import com.example.clicker.presentation.stream.ClickedUIState
import com.example.clicker.presentation.stream.views.streamManager.util.rememberDraggableActions

import com.example.clicker.util.Response
import com.example.clicker.util.objectMothers.TwitchUserDataObjectMother
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
/**
 * ModViewDragSection contains all the composables functions that are related to the dragging portion of the ModView Feature.
 * Here are the contained `Composables`:
 *
 *
 * @property DraggingBox
 * @property ChatBox
 * @property AutoModQueueBox
 * @property AutoModItemRow
 * @property ModActions
 * @property IsModeratorButton
 * @property HorizontalDragDetectionBox
 * @property IsModeratorButton
 * */
object ModViewDragSection {

    /**DraggableModViewBox is responsible for containing the entire ModView Feature and showing the user the 3 [DraggingBox]
     * composables
     * */
    @Composable
    fun DraggableModViewBox(
        boxOneOffsetY:Float,
        boxTwoOffsetY:Float,
        boxThreeOffsetY:Float,

        boxOneDragState: DraggableState,
        boxTwoDragState: DraggableState,
        boxThreeDragState: DraggableState,

        setBoxTwoOffset:(Float) ->Unit,
        setBoxOneOffset:(Float) ->Unit,
        setBoxThreeOffset:(Float) ->Unit,

        boxOneZIndex:Float,
        boxTwoZIndex:Float,
        boxThreeZIndex:Float,
        indivBoxSize:Dp,
        animateToOnDragStop: Float,
        sectionBreakPoint:Int,

        boxOneDragging:Boolean,
        setBoxOneDragging:(Boolean)->Unit,

        boxTwoDragging:Boolean,
        setBoxTwoDragging:(Boolean)->Unit,

        boxThreeDragging:Boolean,
        setBoxThreeDragging:(Boolean)->Unit,
        contentPaddingValues: PaddingValues,
        chatMessages:List<TwitchUserData>,
        triggerBottomModal: () -> Unit,
        updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
        clickedUserData: ClickedUIState,
        timeoutDuration:Int,
        changeTimeoutDuration: (Int) -> Unit,
        timeoutReason: String,
        changeTimeoutReason: (String) -> Unit,

        banDuration:Int,
        changeBanDuration:(Int)->Unit,
        banReason:String,
        changeBanReason: (String) -> Unit,

        timeoutUser: () -> Unit,
        showTimeoutErrorMessage:Boolean,
        setTimeoutShowErrorMessage:(Boolean)->Unit,

        showBanErrorMessage:Boolean,
        setBanShowErrorMessage:(Boolean)->Unit,
        banUser:()->Unit,
        modActionList: List<TwitchUserData>,
        autoModMessageList:List<AutoModQueueMessage>,
        manageAutoModMessage:(String,String,String)-> Unit,
        connectionError: Response<Boolean>,
        reconnect:()->Unit,
        blockedTerms:List<BlockedTerm>,
        deleteBlockedTerm:(String) ->Unit,
        emoteOnly:Boolean,
        setEmoteOnly:(Boolean) ->Unit,
        subscriberOnly:Boolean,
        setSubscriberOnly:(Boolean) ->Unit,
        chatSettingsEnabled:Boolean,
        switchEnabled:Boolean,
        followersOnlyList: List<ListTitleValue>,
        selectedFollowersModeItem: ListTitleValue,
        changeSelectedFollowersModeItem: (ListTitleValue) -> Unit,
        slowModeList: List<ListTitleValue>,
        selectedSlowModeItem: ListTitleValue,
        changeSelectedSlowModeItem: (ListTitleValue) -> Unit,
        deleteMessage:(String)->Unit,

        ) {


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPaddingValues)
        ){
            /**THIS IS THE FIRST BOX*/
            DraggingBox(
                boxOffsetY =boxOneOffsetY,
                boxDragState=boxOneDragState,
                boxZIndex =boxOneZIndex,
                setBoxOffset ={newValue->setBoxOneOffset(newValue)},
                height = indivBoxSize,
                boxColor =Color.Red,
                sectionBreakPoint =sectionBreakPoint,
                animateToOnDragStop=animateToOnDragStop,
                dragging = boxOneDragging,
                setDragging={newValue->setBoxOneDragging(newValue)},
                content={
                    ChatBox(
                        dragging = boxOneDragging,
                        chatMessageList = chatMessages,
                        setDragging = {newValue ->setBoxOneDragging(newValue)},
                        triggerBottomModal = { triggerBottomModal()},
                        updateClickedUser = {  username, userId,isBanned,isMod ->
                            updateClickedUser(
                                username,
                                userId,
                                isBanned,
                                isMod
                            )
                        },
                        clickedUserData = clickedUserData,
                        timeoutDuration=timeoutDuration,
                        changeTimeoutDuration={newValue->changeTimeoutDuration(newValue)},
                        timeoutReason = timeoutReason,
                        changeTimeoutReason ={newValue ->changeTimeoutReason(newValue)},

                        banDuration = banDuration,
                        changeBanDuration={newValue ->changeBanDuration(newValue)},
                        banReason= banReason,
                        changeBanReason = {newValue ->changeBanReason(newValue)},
                        timeoutUser = {timeoutUser()},
                        showTimeoutErrorMessage= showTimeoutErrorMessage,
                        setTimeoutShowErrorMessage ={newValue ->setTimeoutShowErrorMessage(newValue)},

                        showBanErrorMessage= showBanErrorMessage,
                        setBanShowErrorMessage ={newValue ->setBanShowErrorMessage(newValue)},
                        banUser = {banUser()},
                        blockedTerms =blockedTerms,
                        deleteBlockedTerm ={blockedTermId ->deleteBlockedTerm(blockedTermId)},
                        emoteOnly =emoteOnly,
                        setEmoteOnly={newValue -> setEmoteOnly(newValue)},
                        subscriberOnly =subscriberOnly,
                        setSubscriberOnly={newValue -> setSubscriberOnly(newValue)},

                        chatSettingsEnabled=chatSettingsEnabled,
                        switchEnabled =switchEnabled,
                        followersOnlyList=followersOnlyList,
                        selectedFollowersModeItem=selectedFollowersModeItem,
                        changeSelectedFollowersModeItem ={newValue -> changeSelectedFollowersModeItem(newValue)},
                        slowModeList=slowModeList,
                        selectedSlowModeItem=selectedSlowModeItem,
                        changeSelectedSlowModeItem ={newValue ->changeSelectedSlowModeItem(newValue)},
                        deleteMessage={messageId ->deleteMessage(messageId)}
                    )
                }

            )


            /*************START OF THE SECOND BOX***********************/
            DraggingBox(
                boxOffsetY =boxTwoOffsetY,
                boxDragState=boxTwoDragState,
                boxZIndex =boxTwoZIndex,
                setBoxOffset ={newValue->setBoxTwoOffset(newValue)},
                height = indivBoxSize,
                boxColor =Color.Cyan,
                sectionBreakPoint =sectionBreakPoint,
                animateToOnDragStop=animateToOnDragStop,
                dragging = boxTwoDragging,
                setDragging={newValue -> setBoxTwoDragging(newValue)},
                content={
                    AutoModQueueBox(
                        dragging =boxTwoDragging,
                        setDragging={newValue -> setBoxTwoDragging(newValue)},
                        autoModMessageList =autoModMessageList,
                        manageAutoModMessage ={messageId, userId,action ->manageAutoModMessage(messageId,userId,action)},
                        connectionError =connectionError,
                        reconnect = {reconnect()}
                    )
                }
            )

            /*************START OF THE THIRD BOX***********************/
            DraggingBox(
                boxOffsetY =boxThreeOffsetY,
                boxDragState=boxThreeDragState,
                boxZIndex =boxThreeZIndex,
                setBoxOffset ={newValue->setBoxThreeOffset(newValue)},
                height = indivBoxSize,
                boxColor =Color.Magenta,
                sectionBreakPoint =sectionBreakPoint,
                animateToOnDragStop=animateToOnDragStop,
                dragging = boxThreeDragging,
                setDragging={newValue -> setBoxThreeDragging(newValue)},
                content={
                    ModActions(
                        dragging =boxThreeDragging,
                        setDragging={newValue -> setBoxThreeDragging(newValue)},
                        modActionList =modActionList
                    )
                }
            )
        }// This is the end of the box


    }






    /**
     * DraggingBox a composable that is responsible for all the dragging movements inside of the modView feature section
     *
     * @param boxOffsetY a float used to determine if the position of the internal [Box]
     * @param boxDragState a [DraggableState] object that is used for the internal draggable() modifier
     * @param setBoxOffset a function used to set the value of [boxOffsetY]
     * @param boxZIndex a float used to determine the value of the internal box's z-index. The values should be either 0 or 1
     * @param height the height of the internal box
     * @param boxColor the color of the internal box
     * @param sectionBreakPoint a value used to determine the what actions should be taken when the draggin stops
     * @param animateToOnDragStop a value that will be used when
     * @param dragging a value used to determine if the item should be dragging or not
     * @param setDragging a function used to set the value of [dragging]
     * @param content a composable function that will fill up the internal Box
     *
     * */
    @Composable
    fun DraggingBox(
        boxOffsetY: Float,
        boxDragState: DraggableState,
        setBoxOffset:(Float)->Unit,
        boxZIndex:Float,
        height: Dp,
        boxColor:Color,
        sectionBreakPoint: Int,
        animateToOnDragStop:Float,
        dragging:Boolean,
        setDragging:(Boolean)->Unit,
        content:@Composable () -> Unit,

        ){

        val opacity = if(dragging) 0.5f else 0f
        val hapticFeedback = LocalHapticFeedback.current
        val offset = (54.857143 +boxOffsetY).roundToInt()
        Log.d("ChatBoxHeight","height -->$height")

        Box(
            modifier = Modifier
                .offset { IntOffset(0, boxOffsetY.roundToInt()) }
                .background(boxColor)
                .draggable(
                    orientation = Orientation.Vertical,
                    state = boxDragState,
                    onDragStopped = {
                        setDragging(false)
                        when {
                            boxOffsetY < sectionBreakPoint -> {
                                setBoxOffset(0f)
                            }

                            boxOffsetY > sectionBreakPoint && boxOffsetY < (sectionBreakPoint * 2) -> {
                                setBoxOffset(animateToOnDragStop)
                            }

                            boxOffsetY >= (sectionBreakPoint * 2) -> {
                                setBoxOffset(animateToOnDragStop * 2)
                            }
                        }

                    }
                )
                .zIndex(boxZIndex)
                .height(height)
                .fillMaxWidth()
        ) {
            content()
            if(dragging){
                ModView.DetectDoubleClickSpacer(
                    opacity,
                    setDragging={newValue ->setDragging(newValue)},
                    hapticFeedback ={hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)}
                )
            }


        }
    }


    /**
     * ChatBox is the composable function that is used inside of [DraggableBackground] to represent the chat messages shown to the user
     *
     * @param dragging a Boolean used to determine if the user is dragging this component
     * @param setDragging a function used to set the value of [dragging]
     * @param chatMessageList a list of [TwitchUserData] objects. Represents all of the current chat messages
     * @param triggerBottomModal a function used to determine if the the bottom modal should be shown to the user
     * */
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ChatBox(
        dragging:Boolean,
        setDragging:(Boolean)->Unit,
        chatMessageList: List<TwitchUserData>,
        triggerBottomModal:()->Unit,
        updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
        clickedUserData: ClickedUIState,
        timeoutDuration:Int,
        changeTimeoutDuration:(Int)->Unit,
        timeoutReason:String,
        changeTimeoutReason: (String) -> Unit,

        banDuration:Int,
        changeBanDuration:(Int)->Unit,
        banReason:String,
        changeBanReason: (String) -> Unit,
        timeoutUser:()->Unit,
        deleteMessage:(String)->Unit,

        showTimeoutErrorMessage:Boolean,
        setTimeoutShowErrorMessage:(Boolean)->Unit,
        showBanErrorMessage:Boolean,
        setBanShowErrorMessage:(Boolean)->Unit,
        banUser:()->Unit,
        blockedTerms:List<BlockedTerm>,
        deleteBlockedTerm:(String) ->Unit,
        emoteOnly:Boolean,
        setEmoteOnly:(Boolean) ->Unit,
        subscriberOnly:Boolean,
        setSubscriberOnly:(Boolean) ->Unit,
        chatSettingsEnabled:Boolean,
        switchEnabled: Boolean,

        followersOnlyList: List<ListTitleValue>,
        selectedFollowersModeItem: ListTitleValue,
        changeSelectedFollowersModeItem: (ListTitleValue) -> Unit,
        slowModeList: List<ListTitleValue>,
        selectedSlowModeItem: ListTitleValue,
        changeSelectedSlowModeItem: (ListTitleValue) -> Unit,

        ){
        Log.d("ChatBoxTesting","RECOMP!!!!!!")
        val opacity = if(dragging) 0.5f else 0f
        val listState = rememberLazyListState()
        val scope = rememberCoroutineScope()
        var showTimeOutDialog by remember{ mutableStateOf(false) }
        var showBanDialog by remember{ mutableStateOf(false) }
        val hapticFeedback = LocalHapticFeedback.current


        var autoscroll by remember { mutableStateOf(true) }
        val interactionSource = listState.interactionSource


        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is DragInteraction.Start -> {
                        autoscroll = false
                    }
                    is PressInteraction.Press -> {
                        autoscroll = false
                    }
                }
            }
        }

        val endOfListReached by remember {
            derivedStateOf {
                listState.isScrolledToEnd()
            }
        }
        // observer when reached end of list
        LaunchedEffect(endOfListReached) {
            // do your stuff
            if (endOfListReached) {
                autoscroll = true
            }
        }


        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)


        ){

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(vertical = 5.dp)
                ){
                    stickyHeader {
                        ModView.DropDownMenuHeaderBox(
                            headerTitle ="CHAT",
                            blockedTerms =blockedTerms,
                            deleteBlockedTerm ={blockedTermId ->deleteBlockedTerm(blockedTermId)},
                            emoteOnly =emoteOnly,
                            setEmoteOnly={newValue -> setEmoteOnly(newValue)},
                            subscriberOnly =subscriberOnly,
                            setSubscriberOnly ={newValue ->setSubscriberOnly(newValue)},
                            chatSettingsEnabled=chatSettingsEnabled,
                            switchEnabled=switchEnabled,
                            followersOnlyList=followersOnlyList,
                            selectedFollowersModeItem=selectedFollowersModeItem,
                            changeSelectedFollowersModeItem ={newValue -> changeSelectedFollowersModeItem(newValue)},
                            slowModeList=slowModeList,
                            selectedSlowModeItem=selectedSlowModeItem,
                            changeSelectedSlowModeItem ={newValue ->changeSelectedSlowModeItem(newValue)},
                        )
                    }
                    scope.launch {
                        if(autoscroll){
                            listState.scrollToItem(chatMessageList.size)
                        }
                    }

                    items(chatMessageList){chatTwitchUserData ->
                        when(chatTwitchUserData.messageType){
                            MessageType.NOTICE ->{
                                Log.d("MODACTIONS","NOTICE")


                            }
                            MessageType.CLEARCHAT ->{
                                Log.d("MODACTIONS","CLEARCHAT")
                            }
                            MessageType.ANNOUNCEMENT->{

                            }
                            MessageType.RESUB->{

                            }
                            MessageType.SUB->{

                            }
                            MessageType.MYSTERYGIFTSUB->{

                            }
                            MessageType.GIFTSUB->{

                            }
                            else->{
                                HorizontalDragDetectionBox(
                                    itemBeingDragged = {dragOffset ->
                                        ModViewChat.ChatMessageCard(
                                            offset = if(chatTwitchUserData.mod !="1") dragOffset else 0f,
                                            setDragging={newValue ->setDragging(newValue)},
                                            indivUserChatMessage =chatTwitchUserData,
                                            triggerBottomModal={triggerBottomModal()},
                                            updateClickedUser = {  username, userId,isBanned,isMod ->
                                                updateClickedUser(
                                                    username,
                                                    userId,
                                                    isBanned,
                                                    isMod
                                                )
                                            },
                                        )
                                    },
                                    quarterSwipeLeftAction={
                                        updateClickedUser(chatTwitchUserData.displayName?:"",chatTwitchUserData.userId?:"",chatTwitchUserData.banned,chatTwitchUserData.mod =="1")
                                        showTimeOutDialog = true
                                    },
                                    quarterSwipeRightAction = {
                                        updateClickedUser(chatTwitchUserData.displayName?:"",chatTwitchUserData.userId?:"",chatTwitchUserData.banned,chatTwitchUserData.mod =="1")
                                        showBanDialog =true
                                    },
                                    halfSwipeAction={
                                        deleteMessage(chatTwitchUserData.id?:"")

                                    },
                                    twoSwipeOnly = false,
                                    swipeEnabled = chatTwitchUserData.mod !="1"

                                )
                            }
                        }


                    }
                }
            if(showTimeoutErrorMessage){
                TimeoutErrorMessage(
                    modifier = Modifier.align(Alignment.Center),
                    setTimeoutShowErrorMessage ={newValue ->setTimeoutShowErrorMessage(newValue)}
                )

            }
            if(showBanErrorMessage){
                BanErrorMessage(
                    modifier = Modifier.align(Alignment.Center),
                    setBanShowErrorMessage ={newValue ->setBanShowErrorMessage(newValue)}
                )
            }



            if(dragging){
                ModView.DetectDoubleClickSpacer(
                    opacity,
                    setDragging={newValue ->setDragging(newValue)},
                    hapticFeedback ={hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)}
                )
            }
            ModView.DetectDraggingOrNotAtBottomButton(
                dragging = dragging,
                modifier = Modifier.align(Alignment.BottomCenter),
                listState = listState,
                scrollToBottomOfList = {
                    scope.launch {
                        listState.animateScrollToItem(chatMessageList.lastIndex)
                    }
                }
            )


        } //End of box
        if(showTimeOutDialog){
            ModViewDialogs.ModViewTimeoutDialog(
                closeDialog = {showTimeOutDialog = false},
                swipedMessageUsername = clickedUserData.clickedUsername,
                timeoutDuration =timeoutDuration,
                changeTimeoutDuration={newValue -> changeTimeoutDuration(newValue)},
                timeoutReason = timeoutReason,
                changeTimeoutReason ={newValue->changeTimeoutReason(newValue)},
                timeoutUser = {timeoutUser()}
            )
        }

        if(showBanDialog){
            ModViewDialogs.ModViewBanDialog(
                closeDialog = {showBanDialog =false},
                swipedMessageUsername =clickedUserData.clickedUsername,
                banDuration = banDuration,
                changeBanDuration={newValue ->changeBanDuration(newValue)},
                banReason= banReason,
                changeBanReason = {newValue ->changeBanReason(newValue)},
                banUser={banUser()}
            )
        }

    }

    @Composable
    fun TimeoutErrorMessage(
        modifier: Modifier,
        setTimeoutShowErrorMessage:(Boolean)->Unit,
    ){

        var offsetX by remember { mutableStateOf(0f) }
        var opacity by remember { mutableStateOf(0.9f) }

        val dragState = rememberDraggableState{delta ->
            if(opacity>0.1f){
                opacity -= 0.05f
            }
            offsetX += (delta/2)
        }

            Row(modifier = modifier
                .fillMaxWidth()
                , horizontalArrangement = Arrangement.Center){
                Text("You are not a moderator",modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), 0) }
                    .draggable(
                        state = dragState,
                        orientation = Orientation.Horizontal,
                        onDragStopped = {
                            setTimeoutShowErrorMessage(false)

                        }
                    )
                    .background(Color.Red.copy(alpha = opacity))
                    .padding(10.dp),
                    color = Color.White.copy(alpha = opacity))
            }
    }
    @Composable
    fun BanErrorMessage(
        modifier: Modifier,
        setBanShowErrorMessage:(Boolean)->Unit,
    ){

        var offsetX by remember { mutableStateOf(0f) }
        var opacity by remember { mutableStateOf(0.9f) }

        val dragState = rememberDraggableState{delta ->
            if(opacity>0.1f){
                opacity -= 0.05f
            }
            offsetX += (delta/2)
        }

        Row(modifier = modifier
            .fillMaxWidth()
            , horizontalArrangement = Arrangement.Center){
            Text("You are not a moderator",modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .draggable(
                    state = dragState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        setBanShowErrorMessage(false)
                    }
                )
                .background(Color.Red.copy(alpha = opacity))
                .padding(10.dp),
                color = Color.White.copy(alpha = opacity))
        }
    }




    /**
     * AutoModQueueBox is the composable function that is used inside of [DraggableBackground] to represent the AutoModQue messages
     * shown to the user
     *
     * @param dragging a Boolean used to determine if the user is dragging this component
     * @param setDragging a function used to set the value of [dragging]
     * */
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun AutoModQueueBox(
        setDragging: (Boolean) -> Unit,
        dragging:Boolean,
        autoModMessageList:List<AutoModQueueMessage>,
        manageAutoModMessage:(String,String,String)-> Unit,
        connectionError:Response<Boolean>,
        reconnect:()->Unit

        ){
        val hapticFeedback = LocalHapticFeedback.current

        val opacity = if(dragging) 0.5f else 0f
        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .combinedClickable(
                onDoubleClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    setDragging(true)
                },
                onClick = {

                }
            )
        ){
            LazyColumn(
                modifier =Modifier.fillMaxSize()
            ){
                stickyHeader {
                    Text(
                        "AutoMod Queue",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }


                items(autoModMessageList){autoModMessage->
                    AutoModBoxHorizontalDragBox(
                        autoModMessage=autoModMessage,
                        manageAutoModMessage={
                                messageId,userId,action->manageAutoModMessage(messageId,userId,action)
                        }
                    )


                }


            }

        }
        if(dragging){
            ModView.DetectDoubleClickSpacer(
                opacity,
                setDragging={newValue ->setDragging(newValue)},
                hapticFeedback ={hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)}
            )
        }
        ConnectionErrorResponse(
            connectionError,
            reconnect ={reconnect()}
        )


    }
    @Composable
    fun ConnectionErrorResponse(
        connectionError: Response<Boolean>,
        reconnect:()->Unit
    ){
        when(connectionError){
            is Response.Loading ->{
                SubscriptionConnectionLoading()
            }
            is Response.Success ->{}
            is Response.Failure ->{
                ConnectionError(
                    message = "AutoMod connection error",
                    reconnect ={reconnect()}
                )
            }
        }
    }
    @Composable
    fun SubscriptionConnectionLoading(){
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }


    @Composable
    fun ConnectionError(
        message:String,
        reconnect:()->Unit
    ){
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))){
            Column(
                modifier= Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(message,color = Color.Red, fontSize = 25.sp)
                Button(
                    onClick ={
                        reconnect()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor=MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text("Re-connect", color = MaterialTheme.colorScheme.onSecondary)
                }
            }


        }
    }
    @Composable
    fun AutoModBoxHorizontalDragBox(
        autoModMessage: AutoModQueueMessage,
        manageAutoModMessage:(String,String,String)-> Unit
    ){
        Log.d("AutoModBoxHorizontalDragBoxSwiped","swiped --->$autoModMessage")

        HorizontalDragDetectionBox(
            itemBeingDragged ={offset ->
                AutoModItemRow(
                    autoModMessage.username,
                    autoModMessage.fullText,
                    offset = offset,
                    approved =autoModMessage.approved,
                    messageCategory = autoModMessage.category
                )
            },
            quarterSwipeRightAction = {
                manageAutoModMessage(
                    autoModMessage.messageId,
                    autoModMessage.userId,
                    "DENY"
                )
                Log.d("AutoModQueueBoxDragDetectionBox","RIGHT")
            },
            quarterSwipeLeftAction = {
                Log.d("AutoModQueueBoxDragDetectionBox","LEFT")
                manageAutoModMessage(
                    autoModMessage.messageId,
                    autoModMessage.userId,
                    "ALLOW"
                )
            },
            twoSwipeOnly = true,
            quarterSwipeLeftIconResource = painterResource(id =R.drawable.baseline_check_24),
            quarterSwipeRightIconResource = painterResource(id =R.drawable.baseline_close_24),
            swipeEnabled = !autoModMessage.swiped,
        )
    }


    /**
     * AutoModItemRow is the composable function that is used inside of [AutoModQueueBox] to represent the individual AutoModQue messages
     * shown to the user
     *
     * @param username the username of the user
     * @param message the message that is under review
     * @param offset a float used to offset this composable and animate the dragging effect
     * */
    @Composable
    fun AutoModItemRow(
        username:String,
        message: String,
        offset: Float,
        messageCategory: String,
        approved:Boolean?,
    ){

        val annotatedMessageText = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.White)) {
                append("$username: ")
            }
            withStyle(style = SpanStyle(color = Color.White, background = Color.Red.copy(alpha = 0.6f))) {
                append(" $message ")
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .absoluteOffset { IntOffset(x = offset.roundToInt(), y = 0) }
                .background(MaterialTheme.colorScheme.primary)
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,

            ){
                AutoModItemRowTesting(messageCategory)
                AutoModItemPendingText(approved)

            }
            Text(annotatedMessageText)
            Spacer(modifier =Modifier.height(5.dp))
            Divider(color = Color.White.copy(alpha = 0.6f), thickness = 1.dp, modifier = Modifier.fillMaxWidth())
            Spacer(modifier =Modifier.height(5.dp))

        }
    }
    @Composable
    fun AutoModItemPendingText(
        approved:Boolean?,
    ){
        when(approved){
            null ->{
                Text("Pending approval", fontSize = MaterialTheme.typography.headlineSmall.fontSize)
            }
            true ->{
                Row(){
                    Icon(painter = painterResource(id =R.drawable.baseline_check_24), contentDescription = "",tint = Color.Green)
                    Text("Approved")
                }
            }
            false ->{
                Row(){
                    Text("Denied")
                    Icon(painter = painterResource(id =R.drawable.baseline_close_24), contentDescription = "",tint = Color.Red)
                }
            }
        }


    }
    @Composable
    fun AutoModItemRowTesting(
        category:String,
    ){
        Row(){
            Spacer(modifier =Modifier.height(5.dp))
            Icon(painter = painterResource(id =R.drawable.mod_view_24), contentDescription = "")
            Text(category)
            Spacer(modifier =Modifier.height(20.dp))
        }

    }


    /**
     * AutoModQueueBox is the composable function that is used inside of [DraggableBackground] to represent the actions that
     * have been taken by moderators in the chat
     *
     * @param dragging a Boolean used to determine if the user is dragging this component
     * @param setDragging a function used to set the value of [dragging]
     * */
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ModActions(
        dragging:Boolean,
        setDragging:(Boolean)->Unit,
        modActionList:List<TwitchUserData>
        ){
        val hapticFeedback = LocalHapticFeedback.current

        val listState = rememberLazyListState()
        val opacity = if(dragging) 0.5f else 0f

        val scope = rememberCoroutineScope()
        var autoscroll by remember { mutableStateOf(true) }
        val interactionSource = listState.interactionSource


        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is DragInteraction.Start -> {
                        autoscroll = false
                    }
                    is PressInteraction.Press -> {
                        autoscroll = false
                    }
                }
            }
        }

        val endOfListReached by remember {
            derivedStateOf {
                listState.isScrolledToEnd()
            }
        }
        // observer when reached end of list
        LaunchedEffect(endOfListReached) {
            // do your stuff
            if (endOfListReached) {
                autoscroll = true
            }
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .combinedClickable(
                onDoubleClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    setDragging(true)
                },
                // onLongClick = {setDragging(true)},
                onClick = {
                    Log.d("AnotherTapping", "CLICK")
                }
            )
        ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(vertical = 5.dp)

                ) {
                    stickyHeader {
                        Text(
                            "MOD ACTIONS: ${modActionList.size}",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                    scope.launch {
                        if(autoscroll){
                            listState.scrollToItem(modActionList.size)
                        }
                    }



                    items(modActionList) {messageItem ->
                        Log.d("modActionListTesting","bannedDuration -->${messageItem.bannedDuration}<--")
                        when(messageItem.messageType){
                            MessageType.CLEARCHAT ->{
                                if(messageItem.bannedDuration != null){
                                    ModActionMessage.TimeoutMessage(
                                        message= messageItem.userType ?:"No message"
                                    )
                                }else{
                                    ModActionMessage.DeletedMessage(
                                        message= messageItem.userType ?:"No message"
                                    )
                                }
                            }
                            MessageType.CLEARCHATALL ->{
                                ModActionMessage.ClearChatMessage(
                                    message= messageItem.userType ?:"No message"
                                )
                            }
                            MessageType.NOTICE ->{
                                ModActionMessage.NoticeMessage(
                                    message= messageItem.userType ?:"No message"
                                )
                            }
                            else ->{

                            }

                        }

                    }

                }

            if(dragging){
                ModView.DetectDoubleClickSpacer(
                    opacity,
                    setDragging={newValue ->setDragging(newValue)},
                    hapticFeedback ={hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)}
                )
            }


            ModView.DetectDraggingOrNotAtBottomButton(
                dragging = dragging,
                modifier = Modifier.align(Alignment.BottomCenter),
                listState = listState,
                scrollToBottomOfList = {
                    scope.launch {
                        listState.animateScrollToItem(modActionList.lastIndex)
                    }
                }
            )


        }
    }



//todo: rememberDraggableActions() is what I am going to later use to model the complex state


    @Composable
    fun IsModeratorButton(
        isModerator: Response<Boolean>,
        updateAutoModSettings:()->Unit,
    ){
        when(isModerator){
            is Response.Loading ->{
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    onClick = {},
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(text ="LOADING",
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = MaterialTheme.typography.headlineLarge.fontSize)
                }
            }
            is Response.Success ->{
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    onClick = {
                        updateAutoModSettings()
                    },
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(text ="Save",
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = MaterialTheme.typography.headlineLarge.fontSize)
                }
            }
            is Response.Failure ->{
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    onClick = {
                        updateAutoModSettings()
                    },
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(text ="Retry",
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = MaterialTheme.typography.headlineLarge.fontSize
                    )
                }
            }
        }


    }



    @Composable
    fun SimpleFilledTextFieldSampleTesting(
        streamTitle:String,
        updateText:(String)->Unit
    ) {
        val secondaryColor =Color(0xFF6650a4)

        val selectionColors = TextSelectionColors(
            handleColor = secondaryColor, // Set the color of the selection handles
            backgroundColor = secondaryColor // Set the background color of the selected text
        )

        Column(modifier = Modifier.fillMaxWidth()){
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = streamTitle,
                singleLine = true,
                onValueChange = {

                    updateText(it)

                },
                shape = RoundedCornerShape(8.dp),
                label = { },
                colors = androidx.compose.material3.TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.secondary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                    selectionColors = selectionColors
                )
            )
            Spacer(modifier =Modifier.height(5.dp))
        }

    }

    @Composable
    fun CustomTextField(
        streamTitle:String,
        updateText:(String)->Unit
    ){
        val customTextSelectionColors = TextSelectionColors(
            handleColor = MaterialTheme.colorScheme.secondary,
            backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
        )
        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {


            androidx.compose.material.TextField(
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                value = streamTitle,
                shape = RoundedCornerShape(8.dp),
                onValueChange = {
                    if (streamTitle.length <= 140|| it.length < streamTitle.length) {
                        updateText(it)
                    }
                },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White,
                    backgroundColor = Color.DarkGray,
                    cursorColor = Color.White,
                    disabledLabelColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = {
                    androidx.compose.material.Text(
                        "Enter stream title",
                        color = Color.White
                    )
                }
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
        var iconPainterResource: Painter = painterResource(id = R.drawable.ban_24)
        var dragging by remember{ mutableStateOf(true) }
        val state = rememberDraggableActions()
        val offset = if(swipeEnabled) state.offset.value else 0f
        var iconColor = hideIconColor

        if(dragging && !twoSwipeOnly){
            if (state.offset.value >= (state.halfWidth)) {
                iconPainterResource =halfSwipeIconResource
                iconColor = showIconColor
            }
            else if (state.offset.value <= -(state.halfWidth)){
                iconPainterResource =halfSwipeIconResource
                iconColor = showIconColor
            }
            else if (state.offset.value <= -(state.quarterWidth)){
                iconPainterResource =quarterSwipeLeftIconResource
                iconColor = showIconColor
            }
            else if (state.offset.value >= (state.quarterWidth)){
                iconPainterResource = quarterSwipeRightIconResource
                iconColor = showIconColor
            }
        }
        else if(dragging && twoSwipeOnly){
            if (state.offset.value <= -(state.quarterWidth)){
                iconPainterResource =quarterSwipeLeftIconResource
                iconColor = showIconColor
            }
            else if (state.offset.value >= (state.quarterWidth)){
                iconPainterResource = quarterSwipeRightIconResource
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

            Icon(painter = iconPainterResource, contentDescription = "",tint = iconColor, modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp)
            )
            Icon(painter = iconPainterResource, contentDescription = "",tint = iconColor,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 10.dp)
            )


            itemBeingDragged(offset)

        }


    }
}