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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.clicker.R
import com.example.clicker.network.clients.BlockedTerm
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.network.repository.EmoteListMap
import com.example.clicker.network.repository.EmoteNameUrlList
import com.example.clicker.network.websockets.AutoModQueueMessage
import com.example.clicker.network.websockets.MessageType
import com.example.clicker.presentation.modView.ListTitleValue


import com.example.clicker.presentation.stream.ClickedUIState
import com.example.clicker.presentation.stream.views.chat.ChatUIBox
import com.example.clicker.presentation.stream.views.chat.DualIconsButton
import com.example.clicker.presentation.stream.views.chat.EmoteBoard
import com.example.clicker.presentation.stream.views.chat.EnterChatColumn
import com.example.clicker.presentation.stream.views.chat.FilteredMentionLazyRow
import com.example.clicker.presentation.stream.views.chat.HorizontalDragDetectionBox
import com.example.clicker.presentation.stream.views.chat.ImprovedChatUI
import com.example.clicker.presentation.stream.views.chat.ShowIconBasedOnTextLength
import com.example.clicker.presentation.stream.views.chat.ShowModStatus
import com.example.clicker.presentation.stream.views.chat.StylizedTextField
import com.example.clicker.presentation.stream.views.chat.isScrolledToEnd
import com.example.clicker.presentation.stream.views.streamManager.DetectDoubleClickSpacer
import com.example.clicker.presentation.stream.views.streamManager.DetectDraggingOrNotAtBottomButton
import com.example.clicker.presentation.stream.views.streamManager.DropDownMenuHeaderBox
import com.example.clicker.presentation.stream.views.streamManager.util.rememberDraggableActions

import com.example.clicker.util.Response
import com.example.clicker.util.objectMothers.TwitchUserDataObjectMother
import kotlinx.coroutines.delay
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


    /**DraggableModViewBox is responsible for containing the entire ModView Feature and showing the user the 3 [DraggingBox]
     * composables
     * */
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun DraggableModViewBox(
        boxOneOffsetY:Float,
        boxTwoOffsetY:Float,
        boxThreeOffsetY:Float,

        deleteOffsetY:Float,

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

        boxOneIndex:Int,
        boxTwoIndex:Int,
        boxThreeIndex:Int,
        setBoxIndex:(String,Int) ->Unit,

        boxOneHeight:Dp,
        boxTwoHeight:Dp,
        boxThreeHeight:Dp,

        fullModeActive:Boolean,
        fullChat: @Composable ( setDraggingTrue: () -> Unit)-> Unit,
        smallChat: @Composable ( setDraggingTrue: () -> Unit)-> Unit
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
                height = boxOneHeight,
                boxColor =Color.Red,
                sectionBreakPoint =sectionBreakPoint,
                animateToOnDragStop=animateToOnDragStop,
                dragging = boxOneDragging,
                setDragging={newValue->setBoxOneDragging(newValue)},
                changeBackgroundColor={
                    if(boxOneOffsetY>deleteOffsetY){
                        setBoxIndex("ONE",0)
                    }
                },
                content={

                    ChangingBoxTypes(
                        boxOneIndex,
                        setDraggingTrue = {setBoxOneDragging(true)},
                        setBoxDragging={value -> setBoxOneDragging(value)},
                        boxTwoDragging =boxTwoDragging,
                        boxThreeDragging = boxThreeDragging,
                        fullModeActive=fullModeActive,
                        fullChat={ setDraggingFunc ->
                            fullChat(
                                setDraggingTrue={setDraggingFunc()}
                            )
                        },
                        smallChat={ setDraggingFunc ->
                            smallChat(
                                setDraggingTrue={setDraggingFunc()}
                            )
                        }
                    )


                }

            )


            /*************START OF THE SECOND BOX***********************/
            DraggingBox(
                boxOffsetY =boxTwoOffsetY,
                boxDragState=boxTwoDragState,
                boxZIndex =boxTwoZIndex,
                setBoxOffset ={newValue->setBoxTwoOffset(newValue)},
                height = boxTwoHeight,
                boxColor =Color.Cyan,
                sectionBreakPoint =sectionBreakPoint,
                animateToOnDragStop=animateToOnDragStop,
                dragging = boxTwoDragging,
                setDragging={newValue -> setBoxTwoDragging(newValue)},
                changeBackgroundColor={
                    if(boxTwoOffsetY>deleteOffsetY){

                        setBoxIndex("TWO",0)
                    }
                },
                content={

                    ChangingBoxTypes(
                        boxTwoIndex,
                        setDraggingTrue = {setBoxTwoDragging(true)},
                        setBoxDragging={value -> setBoxTwoDragging(value)},
                        boxTwoDragging =boxTwoDragging,
                        boxThreeDragging = boxThreeDragging,
                        fullModeActive=fullModeActive,
                        fullChat={ setDraggingFunc ->
                            fullChat(
                                setDraggingTrue={setDraggingFunc()}
                            )
                        },
                        smallChat={ setDraggingFunc ->
                            smallChat(
                                setDraggingTrue={setDraggingFunc()}
                            )
                        }
                    )

                }
            )

            /*************START OF THE THIRD BOX***********************/
            DraggingBox(
                boxOffsetY =boxThreeOffsetY,
                boxDragState=boxThreeDragState,
                boxZIndex =boxThreeZIndex,
                setBoxOffset ={newValue->setBoxThreeOffset(newValue)},
                height = boxThreeHeight,
                boxColor =Color.Magenta,
                sectionBreakPoint =sectionBreakPoint,
                animateToOnDragStop=animateToOnDragStop,
                dragging = boxThreeDragging,
                setDragging={ newValue -> setBoxThreeDragging(newValue) },
                changeBackgroundColor={
                    if(boxThreeOffsetY>deleteOffsetY){
                        setBoxIndex("THREE",0)
                    }
                },
                content={
                    ChangingBoxTypes(
                        boxThreeIndex,
                        setDraggingTrue = { setBoxThreeDragging(true) },
                        setBoxDragging={ value -> setBoxThreeDragging(value) },
                        boxTwoDragging =boxTwoDragging,
                        boxThreeDragging = boxThreeDragging,
                        fullModeActive=fullModeActive,
                        fullChat={ setDraggingFunc ->
                            fullChat(
                                setDraggingTrue={setDraggingFunc()}
                            )
                        },
                        smallChat={ setDraggingFunc ->
                            smallChat(
                                setDraggingTrue={setDraggingFunc()}
                            )
                        }
                    )

                }
            )

            BoxDeleteSection(
                boxThreeDragging,boxTwoDragging,boxOneDragging,
                deleteOffsetY,
                boxThreeOffsetY,boxTwoOffsetY,boxOneOffsetY,
                Modifier.align(Alignment.BottomCenter)
            )

        }// This is the end of the box


    }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChangingBoxTypes(
    boxIndex:Int,
    setDraggingTrue: () -> Unit,
    setBoxDragging:(value:Boolean) -> Unit,
    boxTwoDragging: Boolean,
    boxThreeDragging:Boolean,
    fullModeActive:Boolean,
    fullChat: @Composable ( setDraggingTrue: () -> Unit)-> Unit,
    smallChat: @Composable ( setDraggingTrue: () -> Unit)-> Unit
){
    when(boxIndex){
        0 ->{
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ){


            }
        }
        1->{
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary)
            ){
                if(fullModeActive){
                    fullChat(setDraggingTrue={setDraggingTrue()})
                }else{
                    smallChat(setDraggingTrue={setDraggingTrue()})


                }


            }
        }
        2->{
            Box(
                modifier = Modifier
                    .fillMaxSize()

            ){

                AutoModQueueBox(
                    dragging =boxTwoDragging,
                    setDragging={newValue -> setBoxDragging(newValue)},
                    autoModMessageList = listOf(),
                    manageAutoModMessage ={messageId, userId,action ->},
                    connectionError =Response.Success(true),
                    reconnect = {}
                )

            }
        }
        3->{
            Box(
                modifier = Modifier
                    .fillMaxSize()

            ){

                ModActions(
                    dragging =boxThreeDragging,
                    setDragging={newValue -> setBoxDragging(newValue)},
                    modActionList = listOf()
                )

            }

        }
        4->{
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Yellow)
                    .combinedClickable(
                        onDoubleClick = { setDraggingTrue() },
                        onClick = {}
                    )
            ){
                Text(
                    text = "Unban requests ",
                    color = Color.Red,
                    modifier = Modifier
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp
                )

            }

        }
        5->{
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
                    .combinedClickable(
                        onDoubleClick = { setDraggingTrue() },
                        onClick = {}
                    )
            ){
                Text(
                    text = "Discord chat ",
                    color = Color.Red,
                    modifier = Modifier
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp
                )

            }
        }
        6->{
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Cyan)
                    .combinedClickable(
                        onDoubleClick = { setDraggingTrue() },
                        onClick = {}
                    )
            ){
                Text(
                    text = "Moderators ",
                    color = Color.Red,
                    modifier = Modifier
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp
                )

            }
        }
        7 ->{
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ){


            }
        }
    }

}

@Composable
fun BoxDeleteSection(
    boxThreeDragging:Boolean,
    boxTwoDragging:Boolean,
    boxOneDragging:Boolean,
    deleteOffsetY: Float,
    boxThreeOffsetY: Float,
    boxTwoOffsetY: Float,
    boxOneOffsetY: Float,
    modifier: Modifier

){
    val colorStops = arrayOf(
        0.0f to Color.Red.copy(0.0f),
        0.2f to Color.Red.copy(0.2f),
        0.4f to Color.Red.copy(0.4f),
        0.6f to Color.Red.copy(0.6f),
        1f to Color.Red
    )

    if(boxThreeDragging  || boxTwoDragging || boxOneDragging){
        if(boxThreeOffsetY > deleteOffsetY|| boxTwoOffsetY> deleteOffsetY || boxOneOffsetY> deleteOffsetY){
            Row(modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Brush.verticalGradient(colorStops = colorStops))
                .zIndex(10f)
            ){

            }
        }else{
            Row(modifier = modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(Brush.verticalGradient(colorStops = colorStops))
                .zIndex(10f)
            ){

            }
        }

    }
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
        changeBackgroundColor:()->Unit,
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
                        changeBackgroundColor()
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
                DetectDoubleClickSpacer(
                    opacity,
                    setDragging={newValue ->setDragging(newValue)},
                    hapticFeedback ={hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)}
                )
            }


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
                            .background(MaterialTheme.colorScheme.secondary) //todo: this is what I want to change
                            .combinedClickable(
                                onDoubleClick = {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    setDragging(true)
                                },
                                onClick = {}
                            )
                            .padding(horizontal = 10.dp)
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
            DetectDoubleClickSpacer(
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
                            "MOD ACTIONS: ${modActionList.size} ",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.secondary) //todo: this is what I want to change
                                .combinedClickable(
                                    onDoubleClick = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        setDragging(true)
                                    },
                                    onClick = {}
                                )
                                .padding(horizontal = 10.dp)
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
            ErrorMessage403(
                hapticFeedback =hapticFeedback,
                setDragging={value -> setDragging(value)},
                modActionListSize = modActionList.size
            )


        }

    }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ErrorMessage403(
    hapticFeedback: HapticFeedback,
    setDragging: (Boolean) -> Unit,
    modActionListSize: Int
){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primary)){
        Text(
            "MOD ACTIONS: $modActionListSize ",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary) //todo: this is what I want to change
                .combinedClickable(
                    onDoubleClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        setDragging(true)
                    },
                    onClick = {}
                )
                .padding(horizontal = 10.dp)
                .align(Alignment.TopCenter)
        )
        ElevatedCardError(
            modifier = Modifier.align(Alignment.Center)
        )

    }
}

@Composable
fun IconTextRow(
    modifier:Modifier
) {

            Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                Icon(painter = painterResource(id =R.drawable.error_outline_24), contentDescription = "error",tint=Color.Red)
                Text(
                    text = "Token error! Please login again to be issued a new token from Twitch",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }


}

object ModActionMessage{
    @Composable
    fun DeletedMessage(
        message:String,
    ){
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(10.dp))

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(painter = painterResource(id =R.drawable.mod_view_24), modifier = Modifier.size(25.dp), contentDescription = "message deleted",tint=MaterialTheme.colorScheme.onPrimary)
                    Text(text ="  Moderator Action", color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineMedium.fontSize,modifier = Modifier.padding(bottom=5.dp))
                }
                Text(text =message, color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineSmall.fontSize,)
                Spacer(modifier = Modifier.height(10.dp))

            }
            Spacer(modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
                .background(Color.White.copy(0.6f)))

        }
    }

    @Composable
    fun TimeoutMessage(
        message:String,
    ){
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(10.dp))

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(painter = painterResource(id =R.drawable.time_out_24), modifier = Modifier.size(25.dp), contentDescription = "message deleted",tint=MaterialTheme.colorScheme.onPrimary)
                    Text(text ="  Moderator Action", color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineMedium.fontSize,modifier = Modifier.padding(bottom=5.dp))
                }
                Text(text =message, color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineSmall.fontSize,)
                Spacer(modifier = Modifier.height(10.dp))

            }
            Spacer(modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
                .background(Color.White.copy(0.6f)))

        }
    }

    @Composable
    fun ClearChatMessage(
        message:String,
    ){
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(10.dp))

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(painter = painterResource(id =R.drawable.clear_chat_alt_24), modifier = Modifier.size(25.dp), contentDescription = "message deleted",tint=MaterialTheme.colorScheme.onPrimary)
                    Text(text ="  Moderator Action", color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineMedium.fontSize,modifier = Modifier.padding(bottom=5.dp))
                }
                Text(text =message, color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineSmall.fontSize,)
                Spacer(modifier = Modifier.height(10.dp))

            }
            Spacer(modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
                .background(Color.White.copy(0.6f)))

        }
    }

    @Composable
    fun NoticeMessage(
        message:String,
    ){
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(10.dp))

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(painter = painterResource(id =R.drawable.person_outline_24), modifier = Modifier.size(25.dp), contentDescription = "message deleted",tint=MaterialTheme.colorScheme.onPrimary)
                    Text(text ="  Moderator Action", color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineMedium.fontSize,modifier = Modifier.padding(bottom=5.dp))
                }
                Text(text =message, color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineSmall.fontSize,)
                Spacer(modifier = Modifier.height(10.dp))

            }
            Spacer(modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
                .background(Color.White.copy(0.6f)))

        }
    }


}


@Composable
fun SmallChat(
    twitchUserChat: List<TwitchUserData>,
    showBottomModal:()->Unit,
    updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
    showTimeoutDialog:()->Unit,
    showBanDialog:()->Unit,
    doubleClickMessage:(String)->Unit,
    deleteChatMessage:(String)->Unit,
    isMod: Boolean,
    inlineContentMap: EmoteListMap,
    setDragging: (Boolean) -> Unit,

){
    val lazyColumnListState = rememberLazyListState()
    var autoscroll by remember { mutableStateOf(true) }
    SmallChatUIBox(
        chatUI = { modifier ->
            SmallChatUILazyColumn(
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
                modifier=modifier,
                deleteChatMessage={messageId ->deleteChatMessage(messageId)},
                isMod = isMod,
                inlineContentMap=inlineContentMap,
                setDragging = {value -> setDragging(value)},

            )
        },
        scrollToBottom = { modifier ->
            SmallChatScrollToBottom(
                scrollingPaused =!autoscroll,
                enableAutoScroll = { autoscroll = true },
                modifier = modifier
            )
        },
        determineScrollState = {
            SmallChatDetermineScrollState(
                lazyColumnListState = lazyColumnListState,
                setAutoScrollFalse = { autoscroll = false },
                setAutoScrollTrue = { autoscroll = true },
            )
        }
    )
}



@Composable
fun SmallChatScrollToBottom(
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

@Composable
fun SmallChatUIBox(
    chatUI: @Composable ImprovedChatUI.(modifier: Modifier) -> Unit,
    determineScrollState: @Composable () -> Unit,
    scrollToBottom: @Composable (modifier:Modifier) -> Unit,
){
    val chatUIScope = remember(){ ImprovedChatUI() }
    Box(modifier = Modifier.fillMaxSize()){
        determineScrollState()
        with(chatUIScope){
            chatUI(modifier = Modifier.fillMaxSize())
        }
        scrollToBottom(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
                .zIndex(5f)
        )


    }

}

@Composable
fun SmallChatDetermineScrollState(
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SmallChatUILazyColumn(
    lazyColumnListState: LazyListState,
    twitchUserChat: List<TwitchUserData>,
    autoscroll:Boolean,
    showBottomModal:()->Unit,
    showTimeoutDialog:()->Unit,
    showBanDialog:()->Unit,
    updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
    doubleClickMessage:(String)->Unit,
    deleteChatMessage:(String)->Unit,
    modifier: Modifier,
    isMod: Boolean,
    inlineContentMap: EmoteListMap,
    setDragging: (Boolean) -> Unit,
){
    val coroutineScope = rememberCoroutineScope()
    val chatUIScope = remember(){ ImprovedChatUI() }
    val hapticFeedback = LocalHapticFeedback.current
    LazyColumn(
        modifier =modifier,
        state = lazyColumnListState
    ){
        coroutineScope.launch {
            if (autoscroll) {
                lazyColumnListState.scrollToItem(twitchUserChat.size)
            }
        }
        stickyHeader {
            Text(
                "Chat",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondary) //todo: this is what I want to change
                    .combinedClickable(
                        onDoubleClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            setDragging(true)
                        },
                        onClick = {}
                    )
                    .padding(horizontal = 10.dp)
            )
        }
        with(chatUIScope){
            items(
                twitchUserChat,
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
                    doubleClickMessage={username ->doubleClickMessage(username)},
                    deleteChatMessage={messageId->deleteChatMessage(messageId)},
                    isMod = isMod,
                    inlineContentMap=inlineContentMap

                )

            }
        }

    }
}
