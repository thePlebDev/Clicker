package com.example.clicker.presentation.stream.views.streamManager.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import android.animation.ObjectAnimator
import android.content.res.Resources
import android.icu.text.ListFormatter.Width
import android.util.Log
import android.view.View
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.clicker.R
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.presentation.home.disableClickAndRipple
import com.example.clicker.presentation.home.views.LiveChannelsLazyColumnScope
import com.example.clicker.presentation.sharedViews.IconScope
import com.example.clicker.presentation.sharedViews.SharedComponents
import com.example.clicker.presentation.stream.FilterType
import com.example.clicker.presentation.stream.views.AutoMod
import com.example.clicker.presentation.stream.views.BottomModal
import com.example.clicker.presentation.stream.views.SharedBottomModal
import com.example.clicker.presentation.stream.views.dialogs.Dialogs
import com.example.clicker.presentation.stream.views.dialogs.TimeListData
import com.example.clicker.presentation.stream.views.isScrolledToEnd
import com.example.clicker.presentation.stream.views.streamManager.ModActionMessage
import com.example.clicker.presentation.stream.views.streamManager.ModView
import com.example.clicker.presentation.stream.views.streamManager.ModViewChat
import com.example.clicker.presentation.stream.views.streamManager.ModViewDialogs
import com.example.clicker.presentation.stream.views.streamManager.util.DragDetectionBox
import com.example.clicker.presentation.stream.views.streamManager.util.Section
import com.example.clicker.presentation.stream.views.streamManager.util.changeSectionOneNThree
import com.example.clicker.presentation.stream.views.streamManager.util.changeSectionOneNTwo
import com.example.clicker.presentation.stream.views.streamManager.util.changeSectionTwoNThree
import com.example.clicker.presentation.stream.views.streamManager.util.rememberDraggableActions
import com.example.clicker.util.Response
import com.example.clicker.util.objectMothers.TwitchUserDataObjectMother
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

object ModViewDragSection {

    /**
     * DragSectionHeightColumn is a [Column] that will produce 3 [Row] composables that will be used to evenly divide up the screen's
     * height into 3 sections.
     *
     * @param updateTotalItemHeight a function that is used to create a value used to determine the height of the Rows minus an
     * offset of 130.
     * @param updateBoxSize a function used to determine the height of the divided boxes
     * @param updateStartingOffsets a function used to determine the starting offset of the 3 boxes used while dragging
     * */
    @Composable
    fun DragSectionHeightColumn(
        updateTotalItemHeight:(Int)->Unit,
        updateBoxSize:(Int)->Unit,
    ){
        val endOffset = 130
        val itemHeightRatio = 2.61
        Column(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)) {

            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.primary)
                    .fillMaxWidth()
                    .onGloballyPositioned {

                        Log.d("onGloballyPositionedHeight", "height --> ${it.size.height} ")
                        updateTotalItemHeight((it.size.height - endOffset))
                        updateBoxSize((it.size.height / itemHeightRatio).toInt())

                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.primary)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.primary)
                    .fillMaxWidth()
                    ,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
            }

        }
    }

    /**********BELOW IS THE MODVIEW ACTION***************/

    /**
     * The beating heart of the ModView component. This composable is responsible for all the items
     * that are being dragged and updating their state accordingly
     * */
    @Composable
    fun DraggableBackground(
        contentPadding: PaddingValues,
        triggerBottomModal:(Boolean)->Unit,
    ){


        var boxOneYOffset by remember { mutableStateOf(0f) }
        var boxOneZIndex by remember {mutableStateOf(1f)}

        var boxTwoYOffset by remember { mutableStateOf(691f) }
        var boxTwoZIndex by remember {mutableStateOf(0f)}

        var boxThreeYOffset by remember { mutableStateOf((691f*2)) }
        var boxThreeZIndex by remember {mutableStateOf(1f)}

        var totalItemHeight by remember { mutableStateOf(0) }


        //derived state of for each item I could do a derived state of for colors
        // that will be the next thing I do


        val boxOneSection by remember(boxOneYOffset) {
            Log.d("boxOneYOffset","boxOneYOffset -->{boxOneYOffset}")
            derivedStateOf {
                when {
                    boxOneYOffset < totalItemHeight -> Section.ONE
                    boxOneYOffset > totalItemHeight && boxOneYOffset < totalItemHeight * 2 -> Section.TWO
                    boxOneYOffset > totalItemHeight*2 ->Section.THREE
                    else -> Section.OTHER
                }
            }
        }
        val boxTwoSection by remember(boxTwoYOffset) {
            derivedStateOf {
                when {
                    boxTwoYOffset < totalItemHeight -> {
                        Log.d("boxTwoSection","section one")
                        Section.ONE
                    }
                    boxTwoYOffset > totalItemHeight && boxTwoYOffset < totalItemHeight * 2 -> {
                        Log.d("boxTwoSection","section two")
                        Section.TWO
                    }
                    boxTwoYOffset > totalItemHeight*2 -> {
                        Log.d("boxTwoSection","section three")
                        Section.THREE
                    }
                    else -> Section.OTHER
                }
            }
        }
        val boxThreeSection by remember(boxThreeYOffset) {

            derivedStateOf {
                when {
                    boxThreeYOffset < totalItemHeight -> Section.ONE
                    boxThreeYOffset > totalItemHeight && boxThreeYOffset < totalItemHeight * 2 -> Section.TWO
                    boxThreeYOffset > totalItemHeight*2 ->Section.THREE
                    else -> Section.OTHER
                }
            }
        }




        val scope = rememberCoroutineScope()
        var boxHeight by remember { mutableStateOf(100) }

        var boxOneDragging by remember { mutableStateOf(false) }

        var boxTwoDragging by remember { mutableStateOf(false) }

        var boxThreeDragging by remember { mutableStateOf(false) }
        val hapticFeedback = LocalHapticFeedback.current
        Log.d("boxOneDraggingTesting","boxOneYOffset -->$boxOneYOffset")





        Box(modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)

        ) {
            //TODO:THIS IS WHERE THE COLUMN OF ROWS GOES
            DragSectionHeightColumn(
                updateTotalItemHeight = {
                    totalItemHeight = it
                },
                updateBoxSize = {
                    boxHeight = it
                },
            )


            Box(
                Modifier
                    .offset { IntOffset(0, boxOneYOffset.roundToInt()) }
                    .background(Color.Magenta)
                    .height(boxHeight.dp)
                    .fillMaxWidth()
                    .zIndex(boxOneZIndex)
                    .pointerInput(Unit) {

                        detectDragGestures(
                            onDragEnd = {
                                when (boxOneSection) {
                                    Section.ONE -> {
                                        boxOneYOffset = 0f
                                    }

                                    Section.TWO -> {
                                        boxOneYOffset = totalItemHeight + 130f
                                    }

                                    Section.THREE -> {
                                        boxOneYOffset = (totalItemHeight + 130f) * 2
                                    }

                                    Section.OTHER -> {}
                                }
                                boxOneDragging = false
                                // offsetY = 0f
                            },
                            onDragStart = {
                                boxOneZIndex = 1f
                                boxThreeZIndex = 0f
                                boxTwoZIndex = 0f
                            }
                        ) { change, dragAmount ->
                            change.consume()
                            changeSectionTwoNThree(
                                boxOneSection = boxOneSection,
                                boxTwoSection = boxTwoSection,
                                boxThreeSection = boxThreeSection,
                                changeBoxTwoToSectionOne = {
                                    boxTwoYOffset = 0f
                                },
                                changeBoxTwoToSectionTwo = {
                                    boxTwoYOffset = totalItemHeight + 130f
                                },
                                changeBoxTwoToSectionThree = {
                                    boxTwoYOffset = (totalItemHeight + 130f) * 2

                                },
                                changeBoxThreeToSectionOne = {
                                    boxThreeYOffset = 0f
                                },
                                changeBoxThreeToSectionTwo = {
                                    boxThreeYOffset = totalItemHeight + 130f
                                },
                                changeBoxThreeToSectionThree = {
                                    boxThreeYOffset = (totalItemHeight + 130f) * 2
                                },
                                isDraggedDown = dragAmount.y < 0,
                                performHapticFeedbackType = {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                }


                            )

                            if (boxOneDragging) {
                                boxOneYOffset += dragAmount.y
                            }
                        }
                    }

            ){
                ModActions(
                    boxOneDragging,
                    setDragging = {value -> boxOneDragging = value},
                    length=20
                )
            }
            /***------------------BELOW IS THE SECOND BOX-----------------------------------------------*/

            Box(Modifier
                .offset { IntOffset(0, boxTwoYOffset.roundToInt()) }
                .background(Color.Red)
                .height(boxHeight.dp)
                .fillMaxWidth()
                .zIndex(boxTwoZIndex)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            when (boxTwoSection) {
                                Section.ONE -> {
                                    boxTwoYOffset = 0f
                                }

                                Section.TWO -> {
                                    boxTwoYOffset = totalItemHeight + 130f
                                }

                                Section.THREE -> {
                                    boxTwoYOffset = (totalItemHeight + 130f) * 2
                                }

                                Section.OTHER -> {}
                            }
                            boxTwoDragging = false
                            // offsetY = 0f
                        },
                        onDragStart = {
                            boxOneZIndex = 0f
                            boxThreeZIndex = 0f
                            boxTwoZIndex = 1f
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        //change

                        //This should have a parameter for chnging the offest of box one and three
                        changeSectionOneNThree(
                            boxOneSection = boxOneSection,
                            boxTwoSection = boxTwoSection,
                            boxThreeSection = boxThreeSection,
                            isDraggedDown = dragAmount.y < 0,
                            changeBoxOneToSectionOne = {
                                boxOneYOffset = 0f
                            },
                            changeBoxOneToSectionTwo = {
                                boxOneYOffset = totalItemHeight + 130f
                            },
                            changeBoxOneToSectionThree = {
                                boxOneYOffset = (totalItemHeight + 130f) * 2
                            },
                            changeBoxThreeToSectionOne = {
                                boxThreeYOffset = 0f
                            },
                            changeBoxThreeToSectionTwo = {
                                boxThreeYOffset = totalItemHeight + 130f
                            },
                            changeBoxThreeToSectionThree = {
                                boxThreeYOffset = (totalItemHeight + 130f) * 2
                            },
                            performHapticFeedbackType = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        )
                        Log.d("Consumingthedrag", "dragAmount.x ${dragAmount.x}")
                        if (boxTwoDragging) {
                            boxTwoYOffset += dragAmount.y
                        }

                    }
                }
            ){
                val fakeDataOne = TwitchUserDataObjectMother.addDisplayName("thePlebDev")
                    .addUserType("LUL get rekt kid").addColor("#BF40BF")
                    .addMod("1").addSubscriber(false).addMonitored(false)
                    .build()

                val fakeDataTwo = TwitchUserDataObjectMother.addDisplayName("Meatball")
                    .addUserType("ok but what the heck was that").addColor("#FF0000")
                    .addMod("0").addSubscriber(true).addMonitored(false)
                    .build()

                val fakeDataThree = TwitchUserDataObjectMother.addDisplayName("Osaka456")
                    .addUserType("There do be another one. So don't worry").addColor("#0000FF")
                    .addMod("1").addSubscriber(true).addMonitored(true)
                    .build()
                val fakeDataFour = TwitchUserDataObjectMother.addDisplayName("Osaka456")
                    .addUserType("There do be another one. So don't worry").addColor("#0000FF")
                    .addMod("1").addSubscriber(true).addMonitored(true)
                    .build()
                val fakeDataFive = TwitchUserDataObjectMother.addDisplayName("Osaka456")
                    .addUserType("There do be another one. So don't worry").addColor("#0000FF")
                    .addMod("1").addSubscriber(true).addMonitored(false)
                    .build()
                val fakeDataSix = TwitchUserDataObjectMother.addDisplayName("Osaka456")
                    .addUserType("There do be another one. So don't worry").addColor("#0000FF")
                    .addMod("1").addSubscriber(true).addMonitored(false)
                    .build()


                Log.d("ChatBoxOffset","BoxTwoOffset---> $boxTwoYOffset")
                ChatBox(
                    boxTwoDragging,
                    setDragging = {value -> boxTwoDragging = value},
                    chatMessageList = listOf(fakeDataOne,fakeDataTwo,
                        fakeDataThree,fakeDataFour,fakeDataFive,fakeDataSix,fakeDataSix,
                        fakeDataThree,fakeDataFour,fakeDataFive,fakeDataSix,fakeDataSix
                    ),
                    triggerBottomModal={newValue -> triggerBottomModal(newValue)}
                )

            }
            /***------------------BELOW IS THE THIRD BOX-----------------------------------------------*/

            Box(Modifier
                .offset { IntOffset(0, boxThreeYOffset.roundToInt()) }
                .background(Color.Green)
                .height(boxHeight.dp)
                .fillMaxWidth()
                .zIndex(boxThreeZIndex)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            when (boxThreeSection) {
                                Section.ONE -> {
                                    boxThreeYOffset = 0f
                                }

                                Section.TWO -> {
                                    boxThreeYOffset = totalItemHeight + 130f
                                }

                                Section.THREE -> {
                                    boxThreeYOffset = (totalItemHeight + 130f) * 2
                                }

                                Section.OTHER -> {}
                            }
                            // offsetY = 0f
                            boxThreeDragging = false
                        },
                        onDragStart = {
                            boxThreeZIndex = 1f
                            boxOneZIndex = 0f
                            boxTwoZIndex = 0f
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        changeSectionOneNTwo(
                            boxOneSection = boxOneSection,
                            boxTwoSection = boxTwoSection,
                            boxThreeSection = boxThreeSection,
                            changeBoxOneToSectionOne = {
                                boxOneYOffset = 0f
                            },
                            changeBoxOneToSectionTwo = {
                                boxOneYOffset = totalItemHeight + 130f
                            },
                            changeBoxOneToSectionThree = {
                                boxOneYOffset = (totalItemHeight + 130f) * 2
                            },
                            changeBoxTwoToSectionOne = {
                                boxTwoYOffset = 0f
                            },
                            changeBoxTwoToSectionTwo = {
                                boxTwoYOffset = totalItemHeight + 130f
                            },
                            changeBoxTwoToSectionThree = {
                                boxTwoYOffset = (totalItemHeight + 130f) * 2

                            },
                            isDraggedDown = dragAmount.y < 0,
                            performHapticFeedbackType = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        )
                        if (boxThreeDragging) {
                            boxThreeYOffset += dragAmount.y
                        }

                    }
                }
            ){
                AutoModQueueBox(
                    setDragging = {newValue -> boxThreeDragging =newValue  },
                    dragging = boxThreeDragging
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
        triggerBottomModal:(Boolean)->Unit,

        ){
        val opacity = if(dragging) 0.5f else 0f
        val listState = rememberLazyListState()
        val scope = rememberCoroutineScope()
        var showTimeOutDialog by remember{ mutableStateOf(false) }
        var showBanDialog by remember{ mutableStateOf(false) }
        val hapticFeedback = LocalHapticFeedback.current

        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)

        ){

            Column(
                modifier =Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(vertical = 5.dp)
                ){
                    stickyHeader {
                        ModView.DropDownMenuHeaderBox(headerTitle ="CHAT")
                    }
                    items(chatMessageList){chatTwitchUserData ->
                        DragDetectionBox(
                            itemBeingDragged = {dragOffset ->
                                ModViewChat.ChatMessageCard(
                                    dragOffset,
                                    setDragging={newValue ->setDragging(newValue)},
                                    chatMessageData =chatTwitchUserData,
                                    triggerBottomModal={newValue->triggerBottomModal(newValue)}
                                )
                            },
                            quarterSwipeLeftAction={showTimeOutDialog = true},
                            quarterSwipeRightAction = {showBanDialog =true},
                            halfSwipeAction={},
                            twoSwipeOnly = false

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
        }
        if(showTimeOutDialog){
            ModViewDialogs.ModViewTimeoutDialog(
                closeDialog = {showTimeOutDialog =false}
            )
        }
        if(showBanDialog){
            ModViewDialogs.ModViewBanDialog(
                closeDialog = {showBanDialog =false}
            )
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
                    ModView.SectionHeaderRow(title ="AutoMod Queue")
                }


                items(10){
                    DragDetectionBox(
                        itemBeingDragged ={offset ->
                            AutoModItemRow(
                                "thePlebDev",
                                "fuck, it do be like that sometimes",
                                offset = offset
                            )
                        },
                        quarterSwipeRightAction = {
                            Log.d("AutoModQueueBoxDragDetectionBox","RIGHT")
                        },
                        quarterSwipeLeftAction = {
                            Log.d("AutoModQueueBoxDragDetectionBox","LEFT")
                        },
                        twoSwipeOnly = true,
                        quarterSwipeLeftIconResource = painterResource(id =R.drawable.baseline_check_24),
                        quarterSwipeRightIconResource = painterResource(id =R.drawable.baseline_close_24)
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
            Row(){
                Spacer(modifier =Modifier.height(5.dp))
                Icon(painter = painterResource(id =R.drawable.mod_view_24), contentDescription = "")
                Text("Swearing")
                Spacer(modifier =Modifier.height(20.dp))
            }
            Text(annotatedMessageText)
            Spacer(modifier =Modifier.height(5.dp))
            Divider(color = Color.White.copy(alpha = 0.6f), thickness = 1.dp, modifier = Modifier.fillMaxWidth())
            Spacer(modifier =Modifier.height(5.dp))

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
        length:Int,
        ){
        val hapticFeedback = LocalHapticFeedback.current

        val listState = rememberLazyListState()
        val opacity = if(dragging) 0.5f else 0f
        val scope = rememberCoroutineScope()
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
            Column(modifier =Modifier.fillMaxSize()) {

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding( vertical = 5.dp)

                ) {
                    stickyHeader {
                        ModView.SectionHeaderRow(title ="MOD ACTIONS: 44")
                    }

                    item{
                        ModActionMessage.TimedUserOutMessage()
                    }

                    items(length) {
                        ModActionMessage.DeletedMessage()
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
                        listState.animateScrollToItem(length)
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
}