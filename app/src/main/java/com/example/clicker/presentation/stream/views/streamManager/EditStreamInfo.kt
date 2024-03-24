package com.example.clicker.presentation.stream.views.streamManager

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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.clicker.R
import com.example.clicker.network.models.websockets.TwitchUserData
import com.example.clicker.presentation.home.disableClickAndRipple
import com.example.clicker.presentation.home.views.LiveChannelsLazyColumnScope
import com.example.clicker.presentation.sharedViews.SharedComponents
import com.example.clicker.presentation.stream.FilterType
import com.example.clicker.presentation.stream.views.AutoMod
import com.example.clicker.presentation.stream.views.dialogs.Dialogs
import com.example.clicker.presentation.stream.views.isScrolledToEnd
import com.example.clicker.presentation.stream.views.streamManager.util.Section
import com.example.clicker.presentation.stream.views.streamManager.util.changeSectionOneNThree
import com.example.clicker.presentation.stream.views.streamManager.util.changeSectionOneNTwo
import com.example.clicker.presentation.stream.views.streamManager.util.changeSectionTwoNThree
import com.example.clicker.util.Response
import com.example.clicker.util.objectMothers.TwitchUserDataObjectMother
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun ManageStreamInformation(
    closeStreamInfo:()->Unit,
    streamTitle:String,
    streamCategory:String,
    updateStreamTitle:(String)->Unit,
    showAutoModSettings:Boolean,
    changeSelectedIndex:(Int, FilterType)->Unit,

    swearingIndex:Int,
    sexBasedTermsIndex:Int,
    aggressionIndex:Int,
    bullyingIndex:Int,
    disabilityIndex:Int,
    sexualityIndex:Int,
    misogynyIndex:Int,
    raceIndex:Int,
    sliderPosition: Float,
    changSliderPosition:(Float)->Unit,
    filterText:String,
    isModerator: Response<Boolean>,
    updateAutoModSettings:()->Unit,
    updateAutoModSettingsStatus:Response<Boolean>?,
    updateAutoModSettingsStatusToNull:()->Unit,
    updateChannelInfo:()->Unit,
){
    if(showAutoModSettings){
        EditAutoModSettings(
            closeStreamInfo={closeStreamInfo()},
            changeSelectedIndex = {item,filterType ->changeSelectedIndex(item,filterType)},
            swearingIndex = swearingIndex,
            sexBasedTermsIndex = sexBasedTermsIndex,
            aggressionIndex = aggressionIndex,
            bullyingIndex = bullyingIndex,
            disabilityIndex = disabilityIndex,
            sexualityIndex = sexualityIndex,
            misogynyIndex = misogynyIndex,
            raceIndex = raceIndex,
            sliderPosition =sliderPosition,
            changSliderPosition = {float -> changSliderPosition(float)},
            filterText=filterText,
            isModerator =isModerator,
            updateAutoModSettings={updateAutoModSettings()},
            updateAutoModSettingsStatus=updateAutoModSettingsStatus,
            updateAutoModSettingsStatusToNull ={updateAutoModSettingsStatusToNull()}


        )
    }else{
        ModView(
            closeStreamInfo={closeStreamInfo()},
        )
//        EditStreamInfo(
//            closeStreamInfo ={closeStreamInfo()},
//            streamTitle = streamTitle,
//            updateStreamTitle = { newText -> updateStreamTitle(newText) },
//            streamCategory = streamCategory,
//            updateChannelInfo={updateChannelInfo()}
//
//        )
    }


}
@Composable
fun EditAutoModSettings(
    closeStreamInfo:()->Unit,
    changeSelectedIndex:(Int, FilterType)->Unit,
    swearingIndex:Int,
    sexBasedTermsIndex:Int,
    aggressionIndex:Int,
    bullyingIndex:Int,
    disabilityIndex:Int,
    sexualityIndex:Int,
    misogynyIndex:Int,
    raceIndex:Int,

    sliderPosition: Float,
    changSliderPosition:(Float)->Unit,
    filterText:String,
    isModerator: Response<Boolean>,
    updateAutoModSettings:()->Unit,
    updateAutoModSettingsStatus:Response<Boolean>?,
    updateAutoModSettingsStatusToNull:()->Unit
){
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            EditAutoModTitle(
                closeStreamInfo={closeStreamInfo()},
                title ="AutoMod Info",
                contentDescription = "close auto mod info",
                isModerator =isModerator,
                updateAutoModSettings ={updateAutoModSettings()}
            )
        }

    ) {contentPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)){
            Column(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.primary)

            ) {

                AutoMod.Settings(
                    sliderPosition =sliderPosition,
                    changSliderPosition = {float -> changSliderPosition(float)},
                    discriminationFilterList = listOf("No filtering", "Less filtering", "Some filtering", "More filtering", "Maximum filtering"),
                    changeSelectedIndex = {item,filterType ->changeSelectedIndex(item,filterType)},
                    updateAutoModSettings = {  },
                    swearingIndex = swearingIndex,
                    sexBasedTermsIndex = sexBasedTermsIndex,
                    aggressionIndex = aggressionIndex,
                    bullyingIndex = bullyingIndex,
                    disabilityIndex = disabilityIndex,
                    sexualityIndex = sexualityIndex,
                    misogynyIndex = misogynyIndex,
                    raceIndex = raceIndex,
                    isModerator = true,
                    filterText = filterText
                )
            } // end of column
            when(updateAutoModSettingsStatus){
                is Response.Loading ->{
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is Response.Success ->{
                    Text("Success",
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 5.dp)
                            .background(Color.Green.copy(0.7f))
                            .align(Alignment.Center)
                            .clickable {
                                scope.launch {
                                    updateAutoModSettingsStatusToNull()
                                }
                            },
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                }
                is Response.Failure ->{
                    Text("Failed",
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 5.dp)
                            .background(Color.Red.copy(0.7f))
                            .align(Alignment.Center)
                            .clickable {
                                scope.launch {
                                    updateAutoModSettingsStatusToNull()
                                }
                            },
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                else ->{

                }
            }

        }//end of box

    }

}
@Composable
fun EditAutoModTitle(
    closeStreamInfo:()->Unit,
    title:String,
    contentDescription:String,
    isModerator: Response<Boolean>,
    updateAutoModSettings:()->Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.DarkGray)
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Row(verticalAlignment = Alignment.CenterVertically){
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = contentDescription,
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        closeStreamInfo()
                    },
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Text(text =title,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,modifier = Modifier.padding(start=20.dp))
        }

        IsModeratorButton(
            isModerator = isModerator,
            updateAutoModSettings={updateAutoModSettings()}
        )


    }
}
@Composable
fun EditStreamInfo(
    closeStreamInfo:()->Unit,
    streamTitle:String,
    updateStreamTitle:(String)->Unit,
    updateChannelInfo:()->Unit,
    streamCategory:String,

){
    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
    ){
        InfoTitle(
            closeStreamInfo={closeStreamInfo()},
            title ="Stream Info",
            contentDescription = "close edit stream info",
            updateChannelInfo = {updateChannelInfo()}
        )
        ChangeStreamTitleTextField(
            streamTitle =streamTitle,
            updateStreamTitle={text ->updateStreamTitle(text)}
        )
//        ChangeStreamCategoryTextField(
//            streamTitle =streamCategory,
//            updateText={text ->updateText(text)}
//        )

    }
}

@Composable
fun ModView(
    closeStreamInfo:()->Unit,
){
    SharedComponents.NoDrawerScaffold(
        topBar = {
            IconTextTopBarRow(
                icon = {
                    BasicIcon(color = MaterialTheme.colorScheme.onPrimary,
                        imageVector = Icons.Default.Close,
                        contentDescription = "close this section of UI",
                        onClick = {
                            closeStreamInfo()
                        }
                    )
                },
                text="Mod View",
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                horizontalArrangement = Arrangement.SpaceBetween
            )
        },
        bottomBar = {}
    ) {contentPadding ->
        DraggableBackground(contentPadding)

    }


}


@Composable
fun DraggableBackground(
    contentPadding: PaddingValues
){


    var boxOneYOffset by remember { mutableStateOf(0f) }
    var boxOneZIndex by remember {mutableStateOf(1f)}

    var boxTwoYOffset by remember { mutableStateOf(692f) }
    var boxTwoZIndex by remember {mutableStateOf(0f)}

    var boxThreeYOffset by remember { mutableStateOf((692f*2)) }
    var boxThreeZIndex by remember {mutableStateOf(1f)}

    var totalItemHeight by remember { mutableStateOf(0) }


    //todo: I think we need totally separate colors for box boxes and then have a conditional to determine which colors shoul be used

    var sectionOneColor = remember { mutableStateOf(Color.Black) }
    var sectionTwoColor = remember { mutableStateOf(Color.Black) }
    var sectionThreeColor = remember { mutableStateOf(Color.Black) }

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
        Log.d("boxOneYOffset","boxOneYOffset -->{boxOneYOffset}")
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
    var boxSize by remember { mutableStateOf(100) }

    var boxOneDragging by remember { mutableStateOf(false) }

    var boxTwoDragging by remember { mutableStateOf(false) }





    Box(modifier = Modifier
        .fillMaxSize()
        .padding(contentPadding)

    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)) {

            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(sectionOneColor.value)
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        totalItemHeight = (it.size.height - 130)
                        Log.d("DragEnding", "area 1 row size -> ${it.size.height}")
                    }
                ,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                Text("AREA 1")
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(sectionTwoColor.value)
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        Log.d("DragEnding", "area 2 row size -> ${it.size.height}")

                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                Text("AREA 2")
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(sectionThreeColor.value)
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        boxSize = (it.size.height / 2.61).toInt()
                        Log.d("detectTapGesturesonLongPress", "onGloballyPositioned() --> called")
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                Text("AREA 3")
            }

        }

        Box(
            Modifier
                .offset { IntOffset(0, boxOneYOffset.roundToInt()) }
                .background(Color.Magenta)
                .height(boxSize.dp)
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
                            isDraggedDown = dragAmount.y < 0


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
            .height(boxSize.dp)
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

            ChatBox(
                boxTwoDragging,
                setDragging = {value -> boxTwoDragging = value},
                chatMessageList = listOf(fakeDataOne,fakeDataTwo,fakeDataThree)
            )

        }
        /***------------------BELOW IS THE THIRD BOX-----------------------------------------------*/

        Box(Modifier
            .offset { IntOffset(0, boxThreeYOffset.roundToInt()) }
            .background(Color.Green)
            .height(boxSize.dp)
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
                        isDraggedDown = dragAmount.y < 0
                    )
                    boxThreeYOffset += dragAmount.y
                }
            }
        ){
            AutoModQueueBox()
        }


    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatBox(
    dragging:Boolean,
    setDragging:(Boolean)->Unit,
    chatMessageList: List<TwitchUserData>,

    ){
    val opacity = if(dragging) 0.5f else 0f
    val listState = rememberLazyListState()

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primary)

    ){
//        ModView.DetectDoubleClickSpacer(opacity,setDragging={newValue ->setDragging(newValue)})
//        ModView.DetectDraggingOrNotAtBottomButton(
//            dragging = dragging,
//            modifier = Modifier.align(Alignment.BottomCenter),
//            listState = listState,
//            scrollToBottomOfList = {}
//        )
        Column(
            modifier =Modifier.fillMaxSize()
        ) {
            ModView.DropDownMenuHeaderBox(headerTitle ="CHAT")
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(vertical = 5.dp)
            ){
                items(chatMessageList){chatTwitchUserData ->
                    DragDetectionBox(
                        itemBeingDragged = {dragOffset ->
                            MessageCard(
                                dragOffset,
                                setDragging={newValue ->setDragging(newValue)},
                                chatMessageData =chatTwitchUserData
                            )
                        }
                    )
                }



            }

        }
        if(dragging){
            ModView.DetectDoubleClickSpacer(opacity,setDragging={newValue ->setDragging(newValue)})
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DragDetectionBox(
    itemBeingDragged:@Composable (dragOffset:Float) -> Unit,
){
    var iconPainterResource = painterResource(id =R.drawable.ban_24)
    var dragging by remember{ mutableStateOf(true) }
    var showTimeOutDialog by remember{ mutableStateOf(false) }
    var showBanDialog by remember{ mutableStateOf(false) }


    val state = rememberDraggableActions()

    var iconColor = MaterialTheme.colorScheme.primary

    if(dragging){
        if (state.offset.value >= (state.halfWidth)) {
            iconPainterResource =painterResource(id =R.drawable.delete_outline_24)
            iconColor = MaterialTheme.colorScheme.onPrimary

        }
        else if (state.offset.value <= -(state.halfWidth)){
            iconPainterResource =painterResource(id =R.drawable.delete_outline_24)
            iconColor = MaterialTheme.colorScheme.onPrimary
        }
        else if (state.offset.value <= -(state.quarterWidth)){
            iconPainterResource =painterResource(id =R.drawable.time_out_24)
            iconColor = MaterialTheme.colorScheme.onPrimary
        }
        else if (state.offset.value >= (state.quarterWidth)){
            iconPainterResource =painterResource(id =R.drawable.ban_24)
            iconColor = MaterialTheme.colorScheme.onPrimary
        }
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .draggable(
                orientation = Orientation.Horizontal,
                onDragStopped = {
                    checkDragThresholdCrossed(
                        halfWidth = state.halfWidth,
                        quarterWidth = state.quarterWidth,
                        state.offset.value,
                        deleteMessageSwipe = {
                            Log.d("DraggableStateWidth", "DELETE MESSAGE")
                        },
                        timeoutUserSwipe = {
                            showTimeOutDialog = true
                        },
                        banUserSwipe = {
                            showBanDialog = true
                        }
                    )
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
            .align(
                Alignment.CenterEnd
            )
            .padding(end = 10.dp)
        )

        Icon(painter = iconPainterResource, contentDescription = "",tint = iconColor,
            modifier = Modifier
                .align(
                    Alignment.CenterStart
                )
                .padding(start = 10.dp)
        )
        itemBeingDragged(state.offset.value)
        if(showTimeOutDialog){
            ModViewTimeoutDialog(
                closeDialog = {showTimeOutDialog =false}
            )
        }
        if(showBanDialog){
            ModViewBanDialog(
                closeDialog = {showBanDialog =false}
            )
        }


    }

}
fun checkDragThresholdCrossed(
    halfWidth:Int,
    quarterWidth:Int,
    offset:Float,
    deleteMessageSwipe:()->Unit,
    timeoutUserSwipe:() ->Unit,
    banUserSwipe:() ->Unit,
){

    when {
        offset >= halfWidth -> {
            deleteMessageSwipe()
        }
        offset <= -halfWidth -> {
            deleteMessageSwipe()
        }
        offset >= quarterWidth -> {
            banUserSwipe()
        }
        offset <= -quarterWidth -> {
            timeoutUserSwipe()
        }
    }
}

@Composable
fun ModViewTimeoutDialog(
    closeDialog:() ->Unit,
){
    Dialogs.TimeoutDialog(
        onDismissRequest = {
            closeDialog()
        },
        username = "clickedUsername",
        timeoutDuration = 3,
        timeoutReason = "timeoutReason",
        changeTimeoutDuration = { duration ->
           // changeTimeoutDuration(duration)
        },
        changeTimeoutReason = { reason ->
            //changeTimeoutReason(reason)
        },
        closeDialog = {
            closeDialog()

        },
        timeOutUser = {
          //  timeOutUser()
        }
    )
}

@Composable
fun ModViewBanDialog(
    closeDialog:() ->Unit,
){
    Dialogs.BanDialog(
        onDismissRequest={closeDialog()},
    username="thePlebDev",
    banReason="",
    changeBanReason={},
    banUser={},
    clickedUserId="",
    closeDialog={closeDialog()},

    )

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageCard(
    offset:Float,
    setDragging:(Boolean)->Unit,
    chatMessageData:TwitchUserData
) {


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .absoluteOffset { IntOffset(x = offset.roundToInt(), y = 0) }
            .combinedClickable(
                onDoubleClick = {
                    setDragging(true)
                },
                // onLongClick = {setDragging(true)},
                onClick = {
                }
            )
        ,
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
    ) {
            TextWithChatBadges(
                displayName = "${chatMessageData.displayName}",
                message = " ${chatMessageData.userType}",
                isMod = chatMessageData.mod == "1",
                isSub = chatMessageData.subscriber == true,
                isMonitored =chatMessageData.isMonitored,
                userNameColor =chatMessageData.color?: "#7F00FF"
            )
    }
}

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
                    tint = Color.Yellow,
                    modifier = Modifier.size(30.dp)
                )
            }
        )

    )
    Column(
        modifier =Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
    ) {
        Text(
            text,
            inlineContent = inlineContent
        )

    }

}


@Composable
fun rememberDraggableActions():ModViewDragState{
    return remember {ModViewDragState()}
}

@Stable
class ModViewDragState(){
    val offset: State<Float> get() = offsetX
    private var offsetX = mutableStateOf(0f)

    val width: State<Int> get() = swipeWidth
    private var swipeWidth = mutableStateOf(100)

     val halfWidth get() = (swipeWidth.value/2.5).toInt()
     val quarterWidth get() = swipeWidth.value/4

    fun setWidth(width: Int){
        swipeWidth.value = width
    }



    val draggableState = DraggableState { delta ->
       if(offsetX.value >= halfWidth){
           offsetX.value += delta/5
       }
        else if(offsetX.value <= -halfWidth){
           offsetX.value += delta/5
        }

        else if(offsetX.value <= -quarterWidth){
            offsetX.value += delta
            Log.d("draggindTheFourthWidth","TIMEOUT")
        }
        else if(offsetX.value >= quarterWidth){
            offsetX.value += delta
            Log.d("draggindTheFourthWidth","BAN")
        }
        else{
            offsetX.value += delta
        }

    }

    suspend fun resetOffset(){
        Log.d("resetOffset","offsetX --> ${offsetX.value}")
        draggableState.drag(MutatePriority.PreventUserInput) {
            Animatable(offsetX.value).animateTo(
                targetValue = 0f,
                tween(durationMillis = 300)
            ) {
                dragBy(value - offsetX.value)
            }
        }
    }



}

@Composable
fun AutoModQueueBox(){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primary)){
        Column(modifier =Modifier.fillMaxSize()) {
            ModView.SectionHeaderRow(title ="AutoMod Queue")
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModActions(
     dragging:Boolean,
     setDragging:(Boolean)->Unit,
     length:Int,

){

    val listState = rememberLazyListState()
    val opacity = if(dragging) 0.5f else 0f
    val scope = rememberCoroutineScope()
    Box(modifier = Modifier
        .fillMaxSize()
        .combinedClickable(
            onDoubleClick = {
                setDragging(true)
            },
            // onLongClick = {setDragging(true)},
            onClick = {
                Log.d("AnotherTapping", "CLICK")
            }
        )
    ) {
        Column(modifier =Modifier.fillMaxSize()) {
            ModView.SectionHeaderRow(title ="MOD ACTIONS: 44")

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 20.dp, vertical = 5.dp)

            ) {


                item{
                    FollowersOnlyChatMessage()

                }
                item{
                    TimedUserOutMessage()
                }

                items(length) {
                    DeletedMessage()
                }

            }
        }
        if(dragging){
            ModView.DetectDoubleClickSpacer(opacity,setDragging={newValue ->setDragging(newValue)})
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

@Composable
fun DualIconsButton(
    buttonAction: () -> Unit,
    iconImageVector: ImageVector,
    iconDescription:String,
    buttonText:String,
    modifier:Modifier
){
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
        shape = RoundedCornerShape(4.dp),
        onClick = { buttonAction() }
    ) {
        Icon(
            imageVector = iconImageVector,
            contentDescription = iconDescription,
            tint =  MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier
        )
        Text(buttonText,color =  MaterialTheme.colorScheme.onSecondary,)
        Icon(
            imageVector = iconImageVector,
            contentDescription = iconDescription,
            tint =  MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier
        )
    }
}

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

//todo: rememberDraggableActions() is what I am going to later use to model the complex state


@Composable
fun InfoTitle(
    closeStreamInfo:()->Unit,
    updateChannelInfo:()->Unit,
    title:String,
    contentDescription:String,
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.DarkGray)
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Row(verticalAlignment = Alignment.CenterVertically){
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = contentDescription,
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        closeStreamInfo()
                    },
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Text(text =title,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,modifier = Modifier.padding(start=20.dp))
        }

        Button(
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            onClick = {
                updateChannelInfo()
            },
            shape = RoundedCornerShape(5.dp)
        ) {
            Text(text ="Save",
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize)
        }


    }

}

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
fun ChangeStreamTitleTextField(
    streamTitle:String,
    updateStreamTitle:(String)->Unit
) {

    var textLengthLeft by remember(streamTitle) {
        mutableStateOf(141 - streamTitle.length)
    }


    Column(modifier = Modifier.fillMaxWidth()){
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp, top = 30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            Text("Title",fontSize=MaterialTheme.typography.headlineMedium.fontSize,color = MaterialTheme.colorScheme.onPrimary)
            Text(textLengthLeft.toString(),fontSize=MaterialTheme.typography.headlineMedium.fontSize,color = MaterialTheme.colorScheme.onPrimary.copy(.6f))
        }

        CustomTextField(
            streamTitle=streamTitle,
            updateText={
                    newText -> updateStreamTitle(newText)
            }
        )
    }


}
@Composable
fun ChangeStreamCategoryTextField(
    streamTitle:String,
    updateText:(String)->Unit
) {
    var text by remember { mutableStateOf(streamTitle) }



    Column(modifier = Modifier.fillMaxWidth()){
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp, top = 30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            Text("Category",fontSize=MaterialTheme.typography.headlineMedium.fontSize,color = MaterialTheme.colorScheme.onPrimary)

        }

        SimpleFilledTextFieldSampleTesting(
            streamTitle=text,
            updateText={newText -> text = newText}
        )
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

