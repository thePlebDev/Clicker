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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.substring
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.clicker.R
import com.example.clicker.network.BanUser
import com.example.clicker.network.BanUserData
import com.example.clicker.network.websockets.MessageType
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
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val clickedUsername = streamViewModel.clickedUIState.value.clickedUsername
    val recentChatMessagesByClickedUsername = streamViewModel.clickedUsernameChats
    val textFieldValue = streamViewModel.textFieldValue



    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                ClickedUserModalDrawer(
                    clickedUsername,
                    recentChatMessagesByClickedUsername,
                    textFieldValue =textFieldValue,
                    drawerState = drawerState,
                    banDuration = streamViewModel.state.value.banDuration,
                    banReason = streamViewModel.state.value.banReason,
                    changeBanReason = { reason -> streamViewModel.changeBanReason(reason) },
                    changeBanDuration = { duration ->
                        streamViewModel.changeBanDuration(
                            duration
                        )
                    },
                    banUser = { banUser -> streamViewModel.banUser(banUser) },
                    clickedUserId = streamViewModel.clickedUIState.value.clickedUserId
                )
            }
        },
        gesturesEnabled = true
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
        ){
            ChatList(
                twitchUserChat,
                lazyColumnListState = lazyColumnListState,
                autoscroll = autoscroll,
                changeAutoScroll = {value -> autoscroll = value},
                drawerState,
                updateClickedUser={
                        username,userId,banned,isMod ->streamViewModel.updateClickedChat(username,userId,banned,isMod)
                },
                restartWebSocket = {streamViewModel.restartWebSocket()}
            )
            EnterChatBox(
                modifier =Modifier.align(Alignment.BottomCenter),
                textFieldValue = streamViewModel.textFieldValue,
                filterMethod = {text,character,index ->streamViewModel.filterMethodBetter(text,character,index)},
                filteredChatList = streamViewModel.filteredChatList,
                clickedAutoCompleteText= { text, username -> streamViewModel.autoTextChange(text,username) },
                chatMentionMethod = {text -> streamViewModel.filterTextMethodFinal(text)}
            )
            ScrollToBottomButton(
                scrollingPaused = !autoscroll,
                enableAutoScroll = { autoscroll = true },
                modifier =Modifier.align(Alignment.BottomCenter)
            )

        }
    }




}

@Composable
fun ClickedUserModalDrawer(
    clickedUsername:String,
    recentChatMessagesByClickedUsername:List<String>,
    textFieldValue: MutableState<TextFieldValue>,
    drawerState: DrawerState,
    changeBanDuration: (Int) -> Unit,
    changeBanReason: (String) -> Unit,
    banDuration: Int,
    banReason: String,
    banUser: (BanUser) -> Unit,
    clickedUserId: String
){
    val scope = rememberCoroutineScope()
    val openTimeoutDialog = remember { mutableStateOf(false) }
    val openBanDialog = remember { mutableStateOf(false) }
    if(openTimeoutDialog.value){
        HorizontalTimeoutDialog(
            onDismissRequest = {openTimeoutDialog.value = false},
            clickedUsername = clickedUsername
        )
    }
    if(openBanDialog.value){

        HorizontalBanDialog(
            onDismissRequest = {openBanDialog.value = false},
            clickedUsername = clickedUsername,
            changeBanDuration = { duration -> changeBanDuration(duration) },
            changeBanReason = { reason -> changeBanReason(reason) },
            banDuration =banDuration,
            banReason = banReason,
            banUser ={user ->banUser(user)},
            clickedUserId = clickedUserId
        )
    }

    val secondaryColor =androidx.compose.material3.MaterialTheme.colorScheme.secondary
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = stringResource(R.string.user_icon_description),
            modifier = Modifier
                .clickable { }
                .size(35.dp),
            tint = androidx.compose.material3.MaterialTheme.colorScheme.secondary
        )
        Text(clickedUsername, color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary, fontSize = 20.sp)
    }
    Divider(color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(vertical = 10.dp))
    Row(modifier = Modifier.padding(5.dp)){
        Column(
            modifier = Modifier.weight(1f)) {
            Text(text ="Recent Chats",
                color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                fontSize = 20.sp
            )
            Divider(color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(vertical = 10.dp))
            LazyColumn(){
                items(recentChatMessagesByClickedUsername){message ->
                    Text(
                        message,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(vertical = 5.dp)
                    )
                }
            }
        }

        Column(modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            ){
            Box(modifier = Modifier.fillMaxSize()){
                Column(modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(5.dp), horizontalAlignment = Alignment.End) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp), horizontalArrangement = Arrangement.SpaceBetween){
                        Button(
                            onClick ={
                                openBanDialog.value = true
                            },
                            colors = ButtonDefaults.buttonColors(secondaryColor)
                        ) {
                            Text(
                                "Ban",
                                color = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.padding(vertical = 5.dp)
                            )
                        }

                        Button(
                            onClick ={
                                openTimeoutDialog.value = true
                            },
                            colors = ButtonDefaults.buttonColors(secondaryColor)
                        ) {
                            Text(
                                "Timeout",
                                color = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.padding(vertical = 5.dp)
                            )
                        }

                    }
                   //here
                    Button(
                        onClick ={
                            textFieldValue.value = TextFieldValue(
                                text = textFieldValue.value.text + "@$clickedUsername ",
                                selection = TextRange(textFieldValue.value.selection.start+"@$clickedUsername ".length)
                            )

                            scope.launch {
                                drawerState.close()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(secondaryColor)
                    ) {
                        Text(
                            "Reply",
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.padding(vertical = 5.dp)
                        )
                    }
                }

            }
        }

    }


}

@Composable
fun EnterChatBox(
    modifier:Modifier = Modifier,
    textFieldValue: MutableState<TextFieldValue>,
    filterMethod:(String,Char,Int) -> Unit,
    filteredChatList:List<String>,
    clickedAutoCompleteText: (String, String) -> String,
    chatMentionMethod:(String) ->Unit
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
           chatMentionMethod = chatMentionMethod
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
    chatMentionMethod:(String) ->Unit

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
                chatMentionMethod(newText.text)
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
    changeAutoScroll:(Boolean) ->Unit,
    drawerState: DrawerState,
    updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
    restartWebSocket: () -> Unit

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
            /**THIS IS WHERE THE FILTER NEEDS TO GO**/
            FilterChatMessageTypes(
                twitchUser,
                restartWebSocket = {restartWebSocket()}
            ){
                ChatCard(
                    twitchUser,
                    drawerState,
                    updateClickedUser ={username, userId, banned, isMod ->updateClickedUser(username,userId,banned,isMod)}
                )
            }


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
fun ChatCard(
    twitchUser:TwitchUserData,
    drawerState: DrawerState,
    updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
){

    val state = rememberSwipeableActionsState()
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
        thresholdCrossed = thresholdCrossed,
        state = state,

    ){
        SwipeableChatCard(
            twitchUser = twitchUser,
            offset = offset,
            drawerState,
            updateClickedUser ={username, userId, banned, isMod ->updateClickedUser(username,userId,banned,isMod)}
        )

    }
}

@Composable
fun SwipeableChatCard(
    twitchUser:TwitchUserData,
    offset: Float,
    drawerState: DrawerState,
    updateClickedUser: (String, String, Boolean, Boolean) -> Unit,
){
    val coroutineScope = rememberCoroutineScope()
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
                .clickable {
                    updateClickedUser(
                        twitchUser.displayName.toString(),
                        twitchUser.userId.toString(),
                        twitchUser.banned,
                        twitchUser.mod != "1"
                    )
                    coroutineScope.launch {
                        drawerState.open()
                    }
                }
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
                state = state.draggableState,
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
@Composable
fun FilterChatMessageTypes(
    twitchUser: TwitchUserData,
    restartWebSocket:()->Unit,
    mainChatCard: @Composable() (() -> Unit)
){
    when (twitchUser.messageType) {
        MessageType.NOTICE -> {
            NoticeMessage(
                color = Color.White,
                displayName = twitchUser.displayName,
                message = twitchUser.userType
            )
        }

        MessageType.USER -> {
            mainChatCard()
        }

        MessageType.ANNOUNCEMENT -> {
            AnnouncementMessage(
                displayName = twitchUser.displayName,
                message = twitchUser.userType,
                systemMessage = twitchUser.systemMessage
            )
        }
        MessageType.RESUB -> {
            ResubMessage(
                message = twitchUser.userType,
                systemMessage = twitchUser.systemMessage
            )
        }
        MessageType.SUB -> {
            SubMessage(
                message = twitchUser.userType,
                systemMessage = twitchUser.systemMessage
            )
        }
        // MYSTERYGIFTSUB,GIFTSUB
        MessageType.GIFTSUB -> {
            GiftSubMessage(
                message = twitchUser.userType,
                systemMessage = twitchUser.systemMessage
            )
        }
        MessageType.MYSTERYGIFTSUB -> {
            MysteryGiftSubMessage(
                message = twitchUser.userType,
                systemMessage = twitchUser.systemMessage
            )
        }
        MessageType.ERROR -> {
            ErrorMessage(
                message = twitchUser.userType!!,
                user = twitchUser.displayName!!,
                restartWebSocket = { restartWebSocket() }
            )
        }
        MessageType.JOIN -> {
            JoinMessage(
                message = twitchUser.userType!!
            )
        }

        else -> {}
    }

}

@Composable
fun HorizontalTimeoutDialog(
    onDismissRequest: () -> Unit,
    clickedUsername: String
){
    val secondary = androidx.compose.material3.MaterialTheme.colorScheme.secondary
    val primary = androidx.compose.material3.MaterialTheme.colorScheme.primary
    val onPrimary = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
    val onSecondary = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            backgroundColor = primary,
            border = BorderStroke(2.dp,secondary)
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .background(primary)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(stringResource(R.string.timeout_text), fontSize = 22.sp,color = onPrimary)
                    Text(clickedUsername, fontSize = 22.sp,color = onPrimary)
                }
                androidx.compose.material.Divider(
                    color = secondary,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(stringResource(R.string.duration_text),color = onPrimary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column {
                        RadioButton(
                            colors =  RadioButtonDefaults.colors( selectedColor=secondary, unselectedColor = onPrimary),
                            selected = "timeoutDuration" == "10",
                            onClick = {
                               // changeTimeoutDuration(10)
                            }
                        )
                        Text(stringResource(R.string.ten_seconds),color = onPrimary)
                    }
                    Column {
                        RadioButton(
                            colors =  RadioButtonDefaults.colors( selectedColor=secondary, unselectedColor = onPrimary),
                            selected = "timeoutDuration" == "60",
                            onClick = {
                                //changeTimeoutDuration(60)
                            }
                        )
                        Text(stringResource(R.string.one_minute),color = onPrimary)
                    }
                    Column {
                        RadioButton(
                            colors =  RadioButtonDefaults.colors( selectedColor=secondary, unselectedColor = onPrimary),
                            selected = "timeoutDuration" == "600",
                            onClick = {
                               // changeTimeoutDuration(600)
                            }
                        )
                        Text(stringResource(R.string.ten_minutes),color = onPrimary)
                    }
                    Column {
                        RadioButton(
                            colors =  RadioButtonDefaults.colors( selectedColor=secondary, unselectedColor = onPrimary),
                            selected = "timeoutDuration" == "1800",
                            onClick = {
                              //  changeTimeoutDuration(1800)
                            }
                        )
                        Text(stringResource(R.string.thirty_minutes),color = onPrimary)
                    }
                }
                OutlinedTextField(
                    colors= TextFieldDefaults.textFieldColors(
                        textColor = onPrimary, focusedLabelColor = onPrimary,
                        focusedIndicatorColor = onPrimary, unfocusedIndicatorColor = onPrimary, unfocusedLabelColor = onPrimary),
                    value = "timeoutReason",
                    onValueChange = {
                        //changeTimeoutReason(it)
                                    },
                    label = { Text(stringResource(R.string.reason)) }
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(10.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = secondary)
                    ) {
                        Text(stringResource(R.string.cancel),color = onSecondary)
                    }
                    // todo: Implement the details of the timeout implementation
                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = secondary),
                        onClick = {
//                            closeDialog()
//                            timeOutUser()
                        }, modifier = Modifier.padding(10.dp)) {
                        Text(stringResource(R.string.timeout_confirm),color = onSecondary)
                    }
                }
            }
        }
    }

}

@Composable
fun HorizontalBanDialog(
    onDismissRequest: () -> Unit,
    clickedUsername: String,
    banDuration: Int,
    banReason: String,
    changeBanDuration: (Int) -> Unit,
    changeBanReason: (String) -> Unit,
    banUser: (BanUser) -> Unit,
    clickedUserId:String
){
    val secondary = androidx.compose.material3.MaterialTheme.colorScheme.secondary
    val primary = androidx.compose.material3.MaterialTheme.colorScheme.primary
    val onPrimary = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
    val onSecondary = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
    Dialog(
        onDismissRequest = { onDismissRequest() },
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            backgroundColor = primary,
            border = BorderStroke(2.dp,secondary)
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .background(primary)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(stringResource(R.string.ban), fontSize = 22.sp,color = onPrimary)
                    Text(clickedUsername, fontSize = 22.sp,color = onPrimary)
                }
                androidx.compose.material.Divider(
                    color = MaterialTheme.colorScheme.secondary,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(stringResource(R.string.duration_text),color = onPrimary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Column() {
                        RadioButton(
                            colors =  RadioButtonDefaults.colors( selectedColor=secondary, unselectedColor = onPrimary),
                            selected = banDuration == 0,
                            onClick = {
                                changeBanDuration(0)
                            }
                        )
                        Text(stringResource(R.string.permanently),color = onPrimary)
                    }
                }
                OutlinedTextField(
                    colors= TextFieldDefaults.textFieldColors(
                        textColor = onPrimary, focusedLabelColor = onPrimary,
                        focusedIndicatorColor = onPrimary, unfocusedIndicatorColor = onPrimary, unfocusedLabelColor = onPrimary),
                    value = banReason,
                    onValueChange = {
                        changeBanReason(it)
                                    },
                    label = { Text(stringResource(R.string.reason),color = onPrimary) }
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = secondary),
                        onClick = { onDismissRequest() }, modifier = Modifier.padding(10.dp)
                    ) {
                        Text(stringResource(R.string.cancel),color =onSecondary)
                    }

                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = secondary),
                        onClick = {
                                  /**This is the actual modding action takes place*/
                            onDismissRequest()

                            banUser(
                                BanUser(
                                    data = BanUserData(
                                        user_id = clickedUserId,
                                        reason = banReason
                                    )
                                )
                            )
                        },
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(stringResource(R.string.ban),color =onSecondary)
                    }
                }
            }
        }
    }
}