package com.example.clicker.presentation.stream

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.substring
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R
import com.example.clicker.network.websockets.models.TwitchUserData
import com.example.clicker.util.Response
import com.example.clicker.util.SwipeableActionsState
import com.example.clicker.util.rememberSwipeableActionsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt


@Composable
fun HorizontalChat(
    streamViewModel: StreamViewModel
){
    val twitchUserChat = streamViewModel.listChats.toList()
    val lazyColumnListState = rememberLazyListState()
    var autoscroll by remember { mutableStateOf(true) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primary)
    ){
        ChatList(
            twitchUserChat,
            lazyColumnListState = lazyColumnListState,
            autoscroll = autoscroll,
            changeAutoScroll = {value -> autoscroll = value}
        )
        EnterChatBox(
            modifier =Modifier.align(Alignment.BottomCenter),
            textFieldValue = streamViewModel.textFieldValue,
            filterMethod = {text,character,index ->streamViewModel.filterMethodBetter(text,character,index)},
            filteredChatList = streamViewModel.filteredChatList,
            clickedAutoCompleteText= { text, username -> streamViewModel.autoTextChange(text,username) }
        )
        ScrollToBottomButton(
            scrollingPaused = !autoscroll,
            enableAutoScroll = { autoscroll = true },
            modifier =Modifier.align(Alignment.BottomCenter)
        )

    }


}

@Composable
fun EnterChatBox(
    modifier:Modifier = Modifier,
    textFieldValue: MutableState<TextFieldValue>,
    filterMethod:(String,Char,Int) -> Unit,
    filteredChatList:List<String>,
    clickedAutoCompleteText: (String, String) -> String,
){
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary),
    ){


        AutoCompleteUserNameRow(filteredChatList,textFieldValue,
            clickedAutoCompleteText={currentText,username -> clickedAutoCompleteText(currentText,username)})
       ChatTextField(
           textFieldValue,
           filterMethod = {text,character,index ->filterMethod(text,character,index)},
       )

    }
}
@Composable
fun AutoCompleteUserNameRow(
    listName:List<String>,
    textFieldValue: MutableState<TextFieldValue>,
    clickedAutoCompleteText: (String, String) -> String,
){
    LazyRow(modifier = Modifier.padding(vertical = 10.dp)){
        if(listName.isEmpty()){

        }else{
            items(listName){name ->
                Text(
                    name,
                    modifier = Modifier
                        .padding(5.dp)
                        .clickable {

                            textFieldValue.value = TextFieldValue(
                                text = clickedAutoCompleteText(textFieldValue.value.text, name),
                                selection = TextRange(
                                    (textFieldValue.value.text + "$name ").length
                                )
                            )
                        },

                    color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                )
            }
        }

    }

}


@Composable
fun ChatTextField(
    textFieldValue: MutableState<TextFieldValue>,
    filterMethod:(String,Char,Int) -> Unit,

){

    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.secondary,
        backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
    )

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        TextField(
            modifier = Modifier,
            value = textFieldValue.value,

            shape = RoundedCornerShape(8.dp),
            onValueChange = { newText ->
                val index = newText.selection
                if(newText.selection.collapsed && index.start != 0){
                    val currentIndex = (index.start -1)
                    val currentCharacter = newText.text[currentIndex]
                    filterMethod(newText.text,currentCharacter,currentIndex)
                }

                textFieldValue.value = TextFieldValue(
                    text = newText.text,
                    selection = newText.selection
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.White,
                backgroundColor = Color.DarkGray,
                cursorColor = MaterialTheme.colorScheme.secondary,
                disabledLabelColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            placeholder = {
                Text(stringResource(R.string.send_a_message), color = Color.White)
            }
        )

    }


}


@Composable
fun ChatList(
    chatList:List<TwitchUserData>,
    lazyColumnListState: LazyListState,
    autoscroll: Boolean,
    changeAutoScroll:(Boolean) ->Unit

){
    val coroutineScope = rememberCoroutineScope()

    var showStickyHeader by remember { mutableStateOf(true) }

    // Add a gesture listener to detect upward scroll


    AutoScrollUtil(
        interactionSource = lazyColumnListState.interactionSource,
        changeAutoScroll = {booleanValue ->changeAutoScroll(booleanValue)},
        lazyColumnListState = lazyColumnListState
    )


    LazyColumn(
        state = lazyColumnListState,
        modifier = Modifier
            .padding(bottom = 70.dp)
            .background(androidx.compose.material3.MaterialTheme.colorScheme.primary)
            .fillMaxSize()
    ){

        coroutineScope.launch {
            if (autoscroll) {
                lazyColumnListState.scrollToItem(chatList.size)
            }
        }
        items(chatList) { twitchUser ->
            ChatCard(twitchUser)
        }

    }
}

@Composable
fun AutoScrollUtil(
    interactionSource: InteractionSource,
    changeAutoScroll:(Boolean)->Unit,
    lazyColumnListState: LazyListState
){

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is DragInteraction.Start -> {
                    changeAutoScroll(false)
                }
                is PressInteraction.Press -> {
                    changeAutoScroll(false)
                }
            }
        }
    }
    // observer when reached end of list
    val endOfListReached by remember {
        derivedStateOf {
            lazyColumnListState.isScrolledToEnd()
        }
    }

    // act when end of list reached
    LaunchedEffect(endOfListReached) {
        // do your stuff
        if (endOfListReached) {
            changeAutoScroll(true)
        }
    }

}

@Composable
fun ScrollToBottomButton(
    scrollingPaused: Boolean,
    enableAutoScroll: () -> Unit,
    modifier:Modifier= Modifier,

) {
    Box(
        modifier = modifier
            .padding(bottom = 77.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (scrollingPaused) {
                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
                    onClick = { enableAutoScroll() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.arrow_drop_down_description),
                        tint =  MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier
                    )
                    Text(stringResource(R.string.scroll_to_bottom),color =  MaterialTheme.colorScheme.onSecondary,)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.arrow_drop_down_description),
                        tint =  MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
fun ChatCard(twitchUser:TwitchUserData){

    val state = rememberSwipeableActionsState()
    var dragState = state.draggableState
    val offset = state.offset.value

    val swipeThreshold = 130.dp
    val swipeThresholdPx = LocalDensity.current.run { swipeThreshold.toPx() }
    var backgroundColor by remember { mutableStateOf(Color.Black) }


    val thresholdCrossed = abs(offset) > swipeThresholdPx

    if (thresholdCrossed) {
        backgroundColor = Color.Red
    } else {
        backgroundColor = Color.Black
    }

    SwipeDetectionBox(
        backgroundColor = backgroundColor,
        dragState = dragState,
        thresholdCrossed = thresholdCrossed,
        state = state,

    ){
        SwipeableChatCard(
            twitchUser = twitchUser,
            offset = offset
        )

    }
}

@Composable
fun SwipeableChatCard(
    twitchUser:TwitchUserData,
    offset: Float,
){
    var fontSize = 17.sp
    var color by remember { mutableStateOf(Color(android.graphics.Color.parseColor(twitchUser.color))) }
    if(color == Color.Black){
        color = androidx.compose.material3.MaterialTheme.colorScheme.primary
    }
    Column(
        verticalArrangement = Arrangement.Center

    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .absoluteOffset { IntOffset(x = offset.roundToInt(), y = 0) }
            ,
            backgroundColor = MaterialTheme.colorScheme.primary,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)

        ) {
            Column() {


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
        } // end of the Card
    }
}

@Composable
fun SwipeDetectionBox(
    backgroundColor: Color,
    dragState: DraggableState,
    thresholdCrossed:Boolean,
    state: SwipeableActionsState,
    content: @Composable() (() -> Unit)
){
    val scope = rememberCoroutineScope()
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
                            // deleteMessage(twitchUser.id ?: "")
                        } else {
                            state.resetOffset()
                        }
                    }

                }

            )

    ) {
        content()
    }

}