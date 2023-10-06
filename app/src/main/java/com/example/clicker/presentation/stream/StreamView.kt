package com.example.clicker.presentation.stream

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import android.graphics.Color.parseColor
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.tween
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.Divider
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.ModalDrawer
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Switch
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.rememberDismissState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.swipeable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.clicker.network.models.ChatSettingsData

import com.example.clicker.network.websockets.MessageType
import com.example.clicker.presentation.home.HomeViewModel
import com.example.clicker.util.Response
import androidx.compose.material.DismissValue.Default
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.ThresholdConfig
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.rememberSwipeableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import com.example.clicker.network.BanUser
import com.example.clicker.network.BanUserData
import com.example.clicker.network.websockets.models.TwitchUserData
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StreamView(
    streamViewModel: StreamViewModel,
    homeViewModel: HomeViewModel
) {



    val twitchUserChat = streamViewModel.listChats.toList()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val chatSettingData = streamViewModel.state.value.chatSettings
    val modStatus = streamViewModel.state.value.loggedInUserData?.mod
    val filteredChat = streamViewModel.filteredChatList
    val clickedUsernameChats = streamViewModel.clickedUsernameChats
    val scope = rememberCoroutineScope()



            val bottomModalState = rememberModalBottomSheetState(
                initialValue = ModalBottomSheetValue.Hidden,
                skipHalfExpanded = true
            )






    val testingString = ""
    val anotherThingy = testingString.indexOf("badge-info")
    var orientation by remember { mutableStateOf(Configuration.ORIENTATION_PORTRAIT) }
    val configuration = LocalConfiguration.current
//
    LaunchedEffect(configuration) {
        // Save any changes to the orientation value on the configuration object
        snapshotFlow { configuration.orientation }
            .collect { orientation = it }
    }

    when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {


        }
        else -> {
            ModalBottomSheetLayout(
                sheetState = bottomModalState,
                sheetContent = {
                    BottomModalContent(
                        //TODO: this should 100% not be filteredChat. Need to create new variable
                        clickedUsernameChats = clickedUsernameChats,
                        clickedUsername = streamViewModel.clickedUsername.value,
                        bottomModalState = bottomModalState,
                        textFieldValue = streamViewModel.textFieldValue,

                        timeoutDuration = streamViewModel.state.value.timeoutDuration,
                        timeoutReason = streamViewModel.state.value.timeoutReason,
                        banDuration = streamViewModel.state.value.banDuration,
                        banReason = streamViewModel.state.value.banReason,
                        changeTimeoutReason = {reason -> streamViewModel.changeTimeoutReason(reason)},
                        changeTimeoutDuration = {duration -> streamViewModel.changeTimeoutDuration(duration)},
                        changeBanDuration = {duration -> streamViewModel.changeBanDuration(duration)},
                        changeBanReason = {reason -> streamViewModel.changeBanReason(reason)},
                        banUser = {banUser -> streamViewModel.banUser(banUser)},
                        clickedUserId = streamViewModel.clickedUserId.value,
                        closeBottomModal ={scope.launch { bottomModalState.hide() }},
                        timeOutUser = {streamViewModel.timeoutUser()},
                        banned = streamViewModel.clickedUsernameBanned.value,
                        unbanUser = {streamViewModel.unBanUser()},
                        isMod = streamViewModel.clickedUsernameIsMod.value
                    )

                }
            ) {

                ModalDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        DrawerContent(
                            chatSettingData,
                            showChatSettingAlert = streamViewModel.state.value.showChatSettingAlert,
                            slowModeToggle = { chatSettingsData ->
                                streamViewModel.slowModeChatSettings(
                                    chatSettingsData
                                )
                            },
                            followerModeToggle = { chatSettingsData ->
                                streamViewModel.followerModeToggle(
                                    chatSettingsData
                                )
                            },
                            subscriberModeToggle = { chatSettingsData ->
                                streamViewModel.subscriberModeToggle(
                                    chatSettingsData
                                )
                            },
                            emoteModeToggle = { chatSettingsData ->
                                streamViewModel.emoteModeToggle(
                                    chatSettingsData
                                )
                            },
                            enableSlowModeSwitch = streamViewModel.state.value.enableSlowMode,
                            enableFollowerModeSwitch = streamViewModel.state.value.enableFollowerMode,
                            enableSubscriberSwitch = streamViewModel.state.value.enableSubscriberMode,
                            enableEmoteModeSwitch = streamViewModel.state.value.enableEmoteMode,
                            chatSettingsFailedMessage = streamViewModel.state.value.chatSettingsFailedMessage,
                            fetchChatSettings = {streamViewModel.retryGettingChatSetting()},
                            closeChatSettingAlter = {streamViewModel.closeChatSettingAlert()}
                        )
                    }
                ) {
                    TextChat(
                        twitchUserChat = twitchUserChat,
                        sendMessageToWebSocket = { string ->
                            streamViewModel.sendMessage(string)
                        },
                        drawerState = drawerState,
                        modStatus = modStatus,
                        bottomModalState = bottomModalState,
                        filteredChatList = filteredChat,
                        filterMethod= {username,newText ->streamViewModel.filterChatters(username,newText)},
                        clickedAutoCompleteText={fullText,clickedText -> streamViewModel.autoTextChange(fullText,clickedText)},
                        addChatter = {username,message -> streamViewModel.addChatter(username,message)},
                        updateClickedUser = {username,userId,banned,isMod -> streamViewModel.updateClickedChat(username,userId,banned,isMod)},
                        textFieldValue = streamViewModel.textFieldValue,
                        channelName = streamViewModel.channelName.collectAsState().value,
                        deleteMessage = {messageId -> streamViewModel.deleteChatMessage(messageId)},

                        banResponse = streamViewModel.state.value.banResponse,
                        undoBan = {streamViewModel.unBanUser()},
                        undoBanResponse = streamViewModel.state.value.undoBanResponse,
                        showStickyHeader = streamViewModel.state.value.showStickyHeader,
                        closeStickyHeader = {streamViewModel.closeStickyHeader()},
                        banResponseMessage = streamViewModel.state.value.banResponseMessage,
                        removeUnBanButton = { streamViewModel.removeUnBanButton() },
                        restartWebSocket ={streamViewModel.restartWebSocket()}
                    )
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomModalContent(
    clickedUsernameChats:List<String>,
    clickedUsername:String,
    bottomModalState: ModalBottomSheetState,
    textFieldValue: MutableState<TextFieldValue>,

    timeoutDuration:Int,
    timeoutReason:String,
    banDuration:Int,
    banReason:String,

    changeTimeoutDuration: (Int) -> Unit,
    changeTimeoutReason: (String) -> Unit,

    changeBanDuration: (Int) -> Unit,
    changeBanReason: (String) -> Unit,

    banUser:(BanUser) ->Unit,
    banned:Boolean,
    isMod:Boolean,
    clickedUserId:String,
    closeBottomModal: () -> Unit,

    timeOutUser:()->Unit,
    unbanUser:()->Unit
){
    val scope = rememberCoroutineScope()
    val openTimeoutDialog = remember { mutableStateOf(false) }
    val openBanDialog = remember { mutableStateOf(false) }



    if(openTimeoutDialog.value){
        TimeoutDialog(
            onDismissRequest = {openTimeoutDialog.value = false},
            username = clickedUsername,
            timeoutDuration = timeoutDuration,
            timeoutReason = timeoutReason,
            changeTimeoutDuration = {duration -> changeTimeoutDuration(duration)},
            changeTimeoutReason = {reason -> changeTimeoutReason(reason)},
            closeDialog = {
                openTimeoutDialog.value = false
                closeBottomModal()
            },
            timeOutUser ={timeOutUser()}

        )
    }
    if(openBanDialog.value){
        BanDialog(
            onDismissRequest = {openBanDialog.value = false},
            username = clickedUsername,
            banDuration = banDuration,
            banReason = banReason,
            changeBanDuration ={duration -> changeBanDuration(duration)},
            changeBanReason ={reason -> changeBanReason(reason)},
            banUser = {bannedUser ->  banUser(bannedUser)},
            clickedUserId = clickedUserId,
            closeDialog = {openBanDialog.value = false},
            closeBottomModal = {closeBottomModal()},

        )
    }


    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)) {
        Row(modifier= Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Row(verticalAlignment = Alignment.CenterVertically){
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Send chat",
                    modifier = Modifier
                        .clickable { }
                        .padding(2.dp)
                        .size(25.dp),
                    tint = Color.Red
                )
                Text(clickedUsername)
            }

            Button(onClick = {

                scope.launch {
                     textFieldValue.value = TextFieldValue(
                        text = "@$clickedUsername ",
                        selection = TextRange("@$clickedUsername ".length)
                    )
                    bottomModalState.hide()
                }
            }) {
                Text("Reply")
            }

        }
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween){
            Text("Recent Messages")
            if(isMod){
                Row(){
                    Button(
                        onClick ={
                            openTimeoutDialog.value = true
                        },
                        modifier= Modifier.padding(end = 20.dp)) {
                        Text("Timeout",)
                    }
                    if(banned){
                        Button(onClick ={
                            closeBottomModal()
                            unbanUser()
                        }) {
                            Text("Unban")
                        }
                    }else{
                        Button(onClick ={
                            openBanDialog.value = true
                        }) {
                            Text("Ban")
                        }
                    }

                }
            }

        }

    }//END OF THE COLUMN


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .height(100.dp)
            .background(Color.DarkGray)
    ){
        items(clickedUsernameChats){
            Text(it,modifier= Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp),color =Color.White)
        }

    }



}

@Composable
fun DrawerContent(
     chatSettingsData: Response<ChatSettingsData>,
     showChatSettingAlert:Boolean,
     closeChatSettingAlter:()->Unit,
     slowModeToggle:(ChatSettingsData) -> Unit,
     followerModeToggle:(ChatSettingsData) -> Unit,
     subscriberModeToggle:(ChatSettingsData) -> Unit,
     emoteModeToggle:(ChatSettingsData) -> Unit,

     enableSlowModeSwitch:Boolean,
     enableFollowerModeSwitch:Boolean,
     enableSubscriberSwitch:Boolean,
     enableEmoteModeSwitch:Boolean,

     chatSettingsFailedMessage:String,
     fetchChatSettings:()-> Unit
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Chat settings", fontSize = 30.sp)
        when(chatSettingsData){
            is Response.Loading ->{
                CircularProgressIndicator()
            }
            is Response.Success ->{
                ChatSettingsDataUI(
                    chatSettingsData.data,
                    showChatSettingAlert = showChatSettingAlert,
                    slowModeToggle = {chatSettingsData -> slowModeToggle(chatSettingsData) },
                    followerModeToggle = {chatSettingsData -> followerModeToggle(chatSettingsData) },
                    subscriberModeToggle = {chatSettingsData -> subscriberModeToggle(chatSettingsData) },
                    emoteModeToggle = {chatSettingsData -> emoteModeToggle(chatSettingsData) },
                    enableSlowModeSwitch = enableSlowModeSwitch,
                    enableFollowerModeSwitch = enableFollowerModeSwitch,
                    enableSubscriberSwitch = enableSubscriberSwitch,
                    enableEmoteModeSwitch = enableEmoteModeSwitch,
                    chatSettingsFailedMessage = chatSettingsFailedMessage,
                    closeChatSettingAlter = {closeChatSettingAlter()}
                )
            }
            is Response.Failure ->{
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
                    Text("FAILED TO FETCH CHAT SETTINGS")
                    Button(onClick = {
                        fetchChatSettings()
                    }) {
                        Text("Get Chat settings")
                    }
                }

            }
        }
    }


}
@Composable
fun ChatSettingsDataUI(
    chatSettingsData: ChatSettingsData,
    showChatSettingAlert:Boolean,
    closeChatSettingAlter:()->Unit,
    slowModeToggle:(ChatSettingsData) -> Unit,
    followerModeToggle:(ChatSettingsData) -> Unit,
    subscriberModeToggle:(ChatSettingsData) -> Unit,
    emoteModeToggle:(ChatSettingsData) -> Unit,

    enableSlowModeSwitch:Boolean,
    enableFollowerModeSwitch:Boolean,
    enableSubscriberSwitch:Boolean,
    enableEmoteModeSwitch:Boolean,
    chatSettingsFailedMessage:String


){



    var tabIndex by remember { mutableIntStateOf(0) }
    val titles = listOf("Chat room Settings")
    Column {
        TabRow(selectedTabIndex = tabIndex) {
            titles.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index }
                )
            }
        }
        when (tabIndex) {
            0 -> {
                ChatSettings(
                    chatSettingsData =chatSettingsData,
                    showChatSettingAlert =showChatSettingAlert,
                    slowModeToggle = {chatSettingsInfo -> slowModeToggle(chatSettingsInfo)  },
                    followerModeToggle = {chatSettingsInfo -> followerModeToggle(chatSettingsInfo)  },
                    subscriberModeToggle = {chatSettingsInfo -> subscriberModeToggle(chatSettingsInfo)  },
                    emoteModeToggle = {chatSettingsInfo -> emoteModeToggle(chatSettingsInfo)  },

                    enableSlowModeSwitch =enableSlowModeSwitch,
                    enableFollowerModeSwitch =enableFollowerModeSwitch,
                    enableSubscriberSwitch =enableSubscriberSwitch,
                    enableEmoteModeSwitch =enableEmoteModeSwitch,
                    chatSettingsFailedMessage = chatSettingsFailedMessage,
                    closeChatSettingsAlert = {closeChatSettingAlter()}
                )
            }
            1 -> {
                Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "Bonker Settings",
                style = MaterialTheme.typography.body1
            )
            }

        }

    }



}

@Composable
fun ChatSettings(
    chatSettingsData: ChatSettingsData,
    showChatSettingAlert:Boolean,
    closeChatSettingsAlert:()->Unit,
    slowModeToggle:(ChatSettingsData) -> Unit,
    followerModeToggle:(ChatSettingsData) -> Unit,
    subscriberModeToggle:(ChatSettingsData) -> Unit,
    emoteModeToggle:(ChatSettingsData) -> Unit,

    enableSlowModeSwitch:Boolean,
    enableFollowerModeSwitch:Boolean,
    enableSubscriberSwitch:Boolean,
    enableEmoteModeSwitch:Boolean,
    chatSettingsFailedMessage:String,
){
    val slowMode = chatSettingsData.slowMode
    val followerMode = chatSettingsData.followerMode
    val subscriberMode = chatSettingsData.subscriberMode
    val emoteMode = chatSettingsData.emoteMode
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        SlowSwitchRow(
            switchLabel = "Slow mode: ",
            enableSwitch = enableSlowModeSwitch,
            switchCheck = slowMode,
            chatSettingsData = chatSettingsData,
            slowModeToggle = {chatSettingsData -> slowModeToggle(chatSettingsData)}
        )

        FollowerSwitchRow(
            switchLabel = "Follower mode: ",
            enableSwitch = enableFollowerModeSwitch,
            switchCheck = followerMode,
            chatSettingsData = chatSettingsData,
            followerModeToggle = {chatSettingsData -> followerModeToggle(chatSettingsData)}
        )

        SubscriberSwitchRow(
            switchLabel = "Subscriber mode: ",
            enableSwitch = enableSubscriberSwitch,
            switchCheck = subscriberMode,
            chatSettingsData = chatSettingsData,
            subscriberModeToggle = {chatSettingsData -> subscriberModeToggle(chatSettingsData)}
        )

        EmoteSwitchRow(
            switchLabel = "Emote mode: ",
            enableSwitch = enableEmoteModeSwitch,
            switchCheck = emoteMode,
            chatSettingsData = chatSettingsData,
            emoteModeToggle = {chatSettingsData -> emoteModeToggle(chatSettingsData) }

        )



        AnimatedVisibility(visible = showChatSettingAlert) {
            MessageAlertText(
                message = chatSettingsFailedMessage,
                closeChatSettingsAlert ={closeChatSettingsAlert()}
            )
        }



    }// end of the Column
}

@Composable
fun SlowSwitchRow(
    switchLabel:String,
    enableSwitch:Boolean,
    switchCheck:Boolean,
    chatSettingsData: ChatSettingsData,
    slowModeToggle:(ChatSettingsData) -> Unit
){
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween){
        Text(text = switchLabel,fontSize = 25.sp)
        Switch(
            checked = switchCheck,
            enabled = enableSwitch,
            modifier = Modifier.size(40.dp),
            onCheckedChange = {
                slowModeToggle(
                    ChatSettingsData(
                        broadcasterId = chatSettingsData.broadcasterId,
                        slowMode = it,
                        slowModeWaitTime = chatSettingsData.slowModeWaitTime,
                        followerMode = chatSettingsData.followerMode,
                        followerModeDuration = chatSettingsData.followerModeDuration,
                        subscriberMode = chatSettingsData.subscriberMode,
                        emoteMode = chatSettingsData.emoteMode,
                        uniqueChatMode = chatSettingsData.uniqueChatMode

                    )
                )
            }
        )
    }
}

@Composable
fun EmoteSwitchRow(
    switchLabel:String,
    enableSwitch:Boolean,
    switchCheck:Boolean,
    chatSettingsData: ChatSettingsData,
    emoteModeToggle:(ChatSettingsData) -> Unit
){
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween){
        Text(text = switchLabel,fontSize = 25.sp)
        Switch(
            checked = switchCheck,
            enabled = enableSwitch,
            modifier = Modifier.size(40.dp),
            onCheckedChange = {
                emoteModeToggle(
                    ChatSettingsData(
                        broadcasterId = chatSettingsData.broadcasterId,
                        slowMode = chatSettingsData.slowMode,
                        slowModeWaitTime = chatSettingsData.slowModeWaitTime,
                        followerMode = chatSettingsData.followerMode,
                        followerModeDuration = chatSettingsData.followerModeDuration,
                        subscriberMode = chatSettingsData.subscriberMode,
                        emoteMode = it,
                        uniqueChatMode = chatSettingsData.uniqueChatMode

                    )
                )
            }
        )
    }
}
@Composable
fun SubscriberSwitchRow(
    switchLabel:String,
    enableSwitch:Boolean,
    switchCheck:Boolean,
    chatSettingsData: ChatSettingsData,
    subscriberModeToggle:(ChatSettingsData) -> Unit
){
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween){
        Text(text = switchLabel,fontSize = 25.sp)
        Switch(
            checked = switchCheck,
            enabled = enableSwitch,
            modifier = Modifier.size(40.dp),
            onCheckedChange = {
                subscriberModeToggle(
                    ChatSettingsData(
                        broadcasterId = chatSettingsData.broadcasterId,
                        slowMode = chatSettingsData.slowMode,
                        slowModeWaitTime = chatSettingsData.slowModeWaitTime,
                        followerMode = chatSettingsData.followerMode,
                        followerModeDuration = chatSettingsData.followerModeDuration,
                        subscriberMode = it,
                        emoteMode = chatSettingsData.emoteMode,
                        uniqueChatMode = chatSettingsData.uniqueChatMode

                    )
                )
            }
        )
    }
}

@Composable
fun FollowerSwitchRow(
    switchLabel:String,
    enableSwitch:Boolean,
    switchCheck:Boolean,
    chatSettingsData: ChatSettingsData,
    followerModeToggle:(ChatSettingsData) -> Unit
){
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween){
        Text(text = switchLabel,fontSize = 25.sp)
        Switch(
            checked = switchCheck,
            enabled = enableSwitch,
            modifier = Modifier.size(40.dp),
            onCheckedChange = {
                followerModeToggle(
                    ChatSettingsData(
                        broadcasterId = chatSettingsData.broadcasterId,
                        slowMode = chatSettingsData.slowMode,
                        slowModeWaitTime = chatSettingsData.slowModeWaitTime,
                        followerMode = it,
                        followerModeDuration = chatSettingsData.followerModeDuration,
                        subscriberMode = chatSettingsData.subscriberMode,
                        emoteMode = chatSettingsData.emoteMode,
                        uniqueChatMode = chatSettingsData.uniqueChatMode

                    )
                )
            }
        )
    }
}

//TODO: MAKE IT SO THE X CLICK REMOVES THE REQUEST MESSAGE
@Composable
fun MessageAlertText(
    message: String,
    closeChatSettingsAlert: ()->Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clickable { },
        border = BorderStroke(2.dp,Color.Red),
        elevation = 10.dp
    ) {
        Box(){
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Send chat",
                modifier = Modifier
                    .clickable { closeChatSettingsAlert()}
                    .padding(2.dp)
                    .size(25.dp)
                    .align(Alignment.TopEnd),
                tint = Color.Red
                )
            Text(
                "Request failed",
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

    }
}

fun LazyListState.isScrolledToEnd() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun TextChat(
    twitchUserChat:List<TwitchUserData>,
    sendMessageToWebSocket: (String) -> Unit,
    drawerState: DrawerState,
    modStatus:Boolean?,
    bottomModalState: ModalBottomSheetState,
    filteredChatList:List<String>,
    filterMethod:(String,String) ->Unit,
    clickedAutoCompleteText:(String,String) -> String,
    addChatter:(String,String) -> Unit,
    updateClickedUser:(String,String,Boolean,Boolean) -> Unit,
    textFieldValue: MutableState<TextFieldValue>,
    channelName: String?,
    deleteMessage: (String) -> Unit,
    banResponse:Response<Boolean>,
    undoBanResponse: Boolean,
    undoBan:()->Unit,
    showStickyHeader:Boolean,
    closeStickyHeader:()->Unit,
    banResponseMessage:String,
    removeUnBanButton:()->Unit,
    restartWebSocket:() -> Unit

){

    val lazyColumnListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var autoscroll by remember { mutableStateOf(true) }

    // Add a gesture listener to detect upward scroll

    val testingStuff = lazyColumnListState.interactionSource
    LaunchedEffect(testingStuff){
        testingStuff.interactions.collect{interaction ->
            when(interaction){
                is DragInteraction.Start -> {
                    autoscroll = false
                }
                is PressInteraction.Press ->{
                    autoscroll = false
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
        if(endOfListReached){
            autoscroll = true
        }
    }
    if(showStickyHeader){
        LaunchedEffect(key1 = Unit){
            delay(2000)
            closeStickyHeader()
        }
    }



    Box(
        modifier = Modifier
    ){
        LazyColumn(
            state = lazyColumnListState,
            modifier = Modifier
                .padding(bottom = 70.dp)
                .background(Color.DarkGray)
                .fillMaxSize(),

        ){

            stickyHeader {
                if(showStickyHeader){
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                            .clip(shape = RoundedCornerShape(10.dp))
                            .background(Color.Red.copy(alpha = 0.6f))
                            .clickable {
                                closeStickyHeader()
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription ="Error has occured",
                            modifier = Modifier
                                .size(30.dp)
                            ,
                            tint = Color.White
                        )
                        Text(
                            text = banResponseMessage,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription ="Error has occured",
                            modifier = Modifier
                                .size(30.dp)
                            ,
                            tint = Color.White
                        )
                    }
                }else{

                }


            }



            coroutineScope.launch {
                if(autoscroll){
                    lazyColumnListState.scrollToItem(twitchUserChat.size)
                }
            }


            items(twitchUserChat){twitchUser ->





                val color = Color(parseColor(twitchUser.color))

                //TODO: THIS IS WHAT IS PROBABLY CAUSING MY DOUBLE MESSAGE BUG
                    if(twitchUserChat.isNotEmpty()){
                        when(twitchUser.messageType){

                            MessageType.NOTICE ->{
                                NoticeMessage(
                                    color = color,
                                    displayName = twitchUser.displayName,
                                    message = twitchUser.userType
                                )
                            }

                            MessageType.USER ->{
                                addChatter(twitchUser.displayName!!, twitchUser.userType!!)
                                SwipeToDeleteTextCard(
                                    twitchUser = twitchUser,
                                    bottomModalState = bottomModalState,
                                    updateClickedUser ={username,userId,banned,isMod -> updateClickedUser(username,userId,banned,isMod)},
                                    deleteMessage ={messageId -> deleteMessage(messageId)}
                                )

                            }

                            MessageType.ANNOUNCEMENT ->{
                                AnnouncementMessage(
                                    displayName = twitchUser.displayName,
                                    message = twitchUser.userType,
                                    systemMessage = twitchUser.systemMessage
                                )
                            }
                            MessageType.RESUB ->{
                                ResubMessage(
                                    message = twitchUser.userType,
                                    systemMessage = twitchUser.systemMessage
                                )

                            }
                            MessageType.SUB ->{
                                SubMessage(
                                    message = twitchUser.userType,
                                    systemMessage = twitchUser.systemMessage
                                )
                            }
                            //MYSTERYGIFTSUB,GIFTSUB
                            MessageType.GIFTSUB ->{
                                GiftSubMessage(
                                    message = twitchUser.userType,
                                    systemMessage = twitchUser.systemMessage
                                )
                            }
                            MessageType.MYSTERYGIFTSUB ->{
                                MysteryGiftSubMessage(
                                    message = twitchUser.userType,
                                    systemMessage = twitchUser.systemMessage
                                )

                            }
                            MessageType.ERROR ->{
                                ErrorMessage(
                                    message = twitchUser.userType!!,
                                    user = twitchUser.displayName!!,
                                    restartWebSocket ={restartWebSocket()}
                                )

                            }
                            MessageType.JOIN ->{

                                JoinMessage(
                                    message = twitchUser.userType!!
                                )
                            }


                            else -> {}
                        } // end of the WHEN BLOCK



                }


            }
        }



        EnterChat(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
            ,
            chat = {text -> sendMessageToWebSocket(text)},
            modStatus = modStatus,
            filteredChatList = filteredChatList,
            filterMethod ={username,newText -> filterMethod(username,newText)},
            clickedAutoCompleteText ={fullText,clickedText -> clickedAutoCompleteText(fullText,clickedText) },
            textFieldValue = textFieldValue,
            channelName = channelName,
            showModal = {coroutineScope.launch { drawerState.open() }}
        )
        ScrollToBottom(
            scrollingPaused = !autoscroll,
            enableAutoScroll = {autoscroll = true},
            banResponse = banResponse,
            undoBan = {undoBan()},
            undoBanResponse = undoBanResponse,
            removeUndoButton = {removeUnBanButton()}
        )



    }// end of the Box scope
}

@Composable
fun JoinMessage(message:String){
    Text(message, fontSize = 17.sp,color = Color.White,modifier = Modifier.padding(start =5.dp))
}

@Composable
fun ErrorMessage(
    message:String,
    user:String,
    restartWebSocket:() ->Unit
){
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(Color.Red.copy(alpha = 0.6f))){

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ){
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription ="Error has occured",
                    modifier = Modifier
                        .size(30.dp)
                    ,
                    tint = Color.White
                )
                Text(user,color = Color.White, fontSize = 20.sp)
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription ="Error has occured",
                    modifier = Modifier
                        .size(30.dp)
                    ,
                    tint = Color.White
                )
            }

            Text(buildAnnotatedString {
//
                withStyle(style = SpanStyle(color = Color.White, fontSize = 17.sp)) {
                    append(" ${message}")
                }
            }
            )
            Button(onClick = { restartWebSocket() }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)) {
                Text("Click to reconnect",color = Color.White)
            }
        }

    }
}
@Composable
fun MysteryGiftSubMessage(
    message:String?,
    systemMessage: String?
){
    val personalMessage = message ?: ""
    val twitchIRCMessage = systemMessage ?:""
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(Color.Black.copy(alpha = 0.6f))){

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ){
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription ="RANDOM GIFTED SUB",
                    modifier = Modifier
                        .size(30.dp)
                    ,
                    tint = Color.White
                )
                Text("RANDOM GIFTED SUB",color = Color.White, fontSize = 20.sp)
            }

            Text(buildAnnotatedString {
//
                withStyle(style = SpanStyle(color = Color.White, fontSize = 17.sp)) {
                    append(" ${twitchIRCMessage}")
                    append(" ${personalMessage}")
                }
            }
            )
        }

    }
}

@Composable
fun GiftSubMessage(
    message:String?,
    systemMessage: String?
){
    val personalMessage = message ?:""
    val twitchIRCMessage = systemMessage ?:""
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(Color.Black.copy(alpha = 0.6f))){

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ){
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription ="GIFTED SUB",
                    modifier = Modifier
                        .size(30.dp)
                    ,
                    tint = Color.White
                )
                Text("GIFTED SUB",color = Color.White, fontSize = 20.sp)
            }

            Text(buildAnnotatedString {
//
                withStyle(style = SpanStyle(color = Color.White, fontSize = 17.sp)) {
                    append(" ${twitchIRCMessage}")
                    append(" ${personalMessage}")
                }
            }
            )
        }

    }
}
@Composable
fun SubMessage(
    message:String?,
    systemMessage: String?
){
    val TwitchIRCMessage = systemMessage ?:""
    val personalMessage = message?:""
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(Color.Black.copy(alpha = 0.6f))){

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ){
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription ="Sub",
                    modifier = Modifier
                        .size(30.dp)
                    ,
                    tint = Color.White
                )
                Text("SUB",color = Color.White, fontSize = 20.sp)
            }

            Text(buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.White, fontSize = 17.sp)) {
                    append(" ${TwitchIRCMessage}")
                    append(" ${personalMessage}")
                }
            }
            )
        }
    }
}

@Composable
fun ResubMessage(
    message:String?,
    systemMessage:String?
){
    val personalMessage = message ?:""
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(Color.Black.copy(alpha = 0.6f))){

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ){
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription ="resub icon",
                    modifier = Modifier
                        .size(30.dp)
                    ,
                    tint = Color.White
                )
                Text("RESUB",color = Color.White, fontSize = 20.sp)
            }

            Text(buildAnnotatedString {

                withStyle(style = SpanStyle(color = Color.White, fontSize = 17.sp)) {
                    append(" $systemMessage")
                    append(" $personalMessage")
                }
            }
            )
        }

    }
}

@Composable
fun NoticeMessage(
    color: Color,
    displayName:String?,
    message:String?
){
    Text(buildAnnotatedString {
        withStyle(style = SpanStyle(color = color, fontSize = 17.sp)) {
            append("${displayName} :")
        }
        append(" ${message}")

    },
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
    )
}

@Composable
fun AnnouncementMessage(
    displayName:String?,
    message:String?,
    systemMessage: String?
){
    val personalMessage = message ?: ""
    val twitchIRCMessage = systemMessage ?: ""
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(Color.Black.copy(alpha = 0.6f))){

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ){
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription ="Send chat",
                    modifier = Modifier
                        .size(30.dp)
                    ,
                    tint = Color.White
                )
                Text("ANNOUNCEMENT",color = Color.White, fontSize = 20.sp)
            }

            Text(buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.White, fontSize = 17.sp)) {
                    append("${displayName} :")
                }
                withStyle(style = SpanStyle(color = Color.White, fontSize = 17.sp)) {
                    append(" ${twitchIRCMessage}")
                    append(" ${personalMessage}")
                }
            }
            )
        }

    }
}



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDeleteTextCard(
    twitchUser: TwitchUserData,
    bottomModalState: ModalBottomSheetState,
    updateClickedUser:(String,String,Boolean,Boolean) -> Unit,
    deleteMessage:(String)-> Unit

){
        ChatCard(
            twitchUser = twitchUser,
            bottomModalState = bottomModalState,
            updateClickedUser ={username,userId,banned,isMod -> updateClickedUser(username,userId,banned,isMod)},
            deleteMessage = {messageId -> deleteMessage(messageId)}
        )
}

@Composable
fun rememberSwipeableActionsState(): SwipeableActionsState {
    return remember { SwipeableActionsState() }
}
@Stable
class SwipeableActionsState internal constructor() {
    /**
     * The current position (in pixels) of a [SwipeableActionsBox].
     */
    val offset: State<Float> get() = offsetState
    private var offsetState = mutableStateOf(0f)
    private var canSwipeTowardsRight =false
    private var canSwipeTowardsLeft= true

    internal val draggableState = DraggableState { delta ->

        val targetOffset = offsetState.value + delta
        val isAllowed = isResettingOnRelease
                || targetOffset > 0f && canSwipeTowardsRight
                || targetOffset < 0f && canSwipeTowardsLeft
        // Add some resistance if needed
        offsetState.value += if (isAllowed) delta else delta / 10
    }
    /**
     * Whether [SwipeableActionsBox] is currently animating to reset its offset after it was swiped.
     */
    var isResettingOnRelease: Boolean by mutableStateOf(false)
        private set
    internal suspend fun resetOffset() {
        draggableState.drag(MutatePriority.PreventUserInput) {
            isResettingOnRelease = true
            try {
                Animatable(offsetState.value).animateTo(targetValue = 0f, tween(durationMillis = 300)) {
                    dragBy(value - offsetState.value)
                }
            } finally {
                isResettingOnRelease = false
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun ChatCard(
    twitchUser: TwitchUserData,
    bottomModalState: ModalBottomSheetState,
    updateClickedUser:(String,String,Boolean,Boolean) -> Unit,
    deleteMessage:(String)-> Unit
){
    val subBadge = "https://static-cdn.jtvnw.net/badges/v1/5d9f2208-5dd8-11e7-8513-2ff4adfae661/1"
    val modBadge = "https://static-cdn.jtvnw.net/badges/v1/3267646d-33f0-4b17-b3df-f923a41db1d0/1"
    val coroutineScope = rememberCoroutineScope()


    var color by remember { mutableStateOf(Color(parseColor(twitchUser.color))) }
    var displayName by remember { mutableStateOf(twitchUser.displayName) }
    var comment by remember { mutableStateOf(twitchUser.userType) }
    var showIcons by remember { mutableStateOf(true) }
    val state = rememberSwipeableActionsState()

    val offset = state.offset.value
    val swipeThreshold = 130.dp
    val swipeThresholdPx = LocalDensity.current.run { swipeThreshold.toPx() }

    val thresholdCrossed = abs(offset) > swipeThresholdPx

   // val backgroundColor = Color.Black
    var backgroundColor by remember { mutableStateOf(Color.Black) }
    var fontSize = 17.sp


    if(thresholdCrossed){
        backgroundColor = Color.Red
    }else{
        backgroundColor = Color.Black
    }


    val modDragState = DraggableState { delta ->

    }

    var dragState = state.draggableState
    if(twitchUser.mod == "1"){
        dragState = modDragState
    }
    if(twitchUser.deleted){
        dragState = modDragState
        backgroundColor = Color.Red
        fontSize = 14.sp
    }





    val cardWidth = Resources.getSystem().displayMetrics.widthPixels.dp //width of what will be moving
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
                            deleteMessage(twitchUser.id ?: "")
                        } else {
                            state.resetOffset()
                        }

                    }
                },

                )

    ){
        Column(
            verticalArrangement = Arrangement.Center,

        ){

            Card(
                modifier = Modifier
                    .fillMaxWidth()

                    .absoluteOffset { IntOffset(x = offset.roundToInt(), y = 0) }
                    .combinedClickable(
                        onClick = {

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
                    ),
                backgroundColor = Color.DarkGray,
                border = BorderStroke(2.dp,Color.White),

                ){
                Column() {
                    if(twitchUser.deleted){
                        Text("Moderator deleted Comment", fontSize = 20.sp,modifier = Modifier.padding(start = 5.dp))
                    }

                    if(twitchUser.banned){
                        val duration = if (twitchUser.bannedDuration != null) "Banned for ${twitchUser.bannedDuration} seconds" else "Banned permanently"
                        Text(duration, fontSize = 20.sp,modifier = Modifier.padding(start = 5.dp))
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        if (showIcons){
                            if(twitchUser.subscriber == true){
                                AsyncImage(
                                    model = subBadge,
                                    contentDescription = "Subscriber badge",
                                    modifier = Modifier.padding(5.dp)
                                )
                            }
                            if(twitchUser.mod == "1"){
                                AsyncImage(
                                    model = modBadge,
                                    contentDescription = "Moderator badge",
                                    modifier = Modifier.padding(5.dp)
                                )
                            }
                        }


                        Text(buildAnnotatedString {
                            withStyle(style = SpanStyle(color = color, fontSize = fontSize)) {
                                append("${twitchUser.displayName} :")
                            }
                            withStyle(style = SpanStyle(color = Color.White)) {
                                append(" ${twitchUser.userType}")
                            }


                        },
                            modifier = Modifier.padding(5.dp)
                        )

                    }// end of the row
                }


            }// end of the Card


        }


    }


}




@Composable
fun ScrollToBottom(
    scrollingPaused:Boolean,
    enableAutoScroll:() -> Unit,
    banResponse:Response<Boolean>,
    undoBanResponse: Boolean,
    undoBan:()->Unit,
    removeUndoButton:()->Unit


){
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 77.dp)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(horizontal = 10.dp),
            horizontalArrangement =Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            if(scrollingPaused){

                    Button(onClick = { enableAutoScroll() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Send chat",
                            modifier = Modifier
                        )
                        Text("Scroll to bottom")
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Send chat",
                            modifier = Modifier
                        )
                    }

            }

        }
        when(val response =banResponse){
            is Response.Loading ->{}
            is Response.Success ->{
                if(response.data && undoBanResponse){

                }
                if(response.data && !undoBanResponse){


                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription ="undo ban button",
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .size(50.dp)
                            .clickable { undoBan() } //todo: WHEN CLICKED WE NEED TO CLOSE ALL THE MODAL
                            .align(Alignment.BottomEnd)
                            .background(Color.White)
                        ,
                        tint = Color.Magenta
                    )
                    LaunchedEffect(key1 = Unit){
                        delay(10000)
                        //todo: reset the response.data && !undoBanResponse
                        removeUndoButton()

                    }
                }

            }
            is Response.Failure ->{}
        }





    }

}

@Composable
fun EnterChat(
    modifier: Modifier,
    chat: (String) -> Unit,
    modStatus:Boolean?,
    filteredChatList: List<String>,
    filterMethod:(String,String) ->Unit,
    clickedAutoCompleteText:(String,String) -> String,
    textFieldValue: MutableState<TextFieldValue>,
    channelName:String?,
    showModal: () -> Unit
){
        //todo: I think we can move this to the viewModel
    Log.d("currentStreamChannelName","NAME --> $channelName")

    Column(modifier = modifier.background(Color.DarkGray)){
            LazyRow(modifier = Modifier.padding(vertical = 10.dp)){

                items(filteredChatList){
                    Text(
                        it,
                        modifier= Modifier
                            .padding(5.dp)
                            .clickable {

                                textFieldValue.value = TextFieldValue(
                                    text = clickedAutoCompleteText(textFieldValue.value.text, it),
                                    selection = TextRange((textFieldValue.value.text + "$it ").length)
                                )

                            },
                        color = Color.White
                    )
                }
        }


        TextFieldChat(
            textFieldValue = textFieldValue,
            modStatus = modStatus,
            filterMethod = {username,text -> filterMethod(username,text)},
            chat ={chatMessage -> chat(chatMessage)},
            showModal = {showModal()}
        )

    }


}

@Composable
fun TextFieldChat(
    textFieldValue: MutableState<TextFieldValue>,
    modStatus:Boolean?,
    filterMethod:(String,String) ->Unit,
    chat: (String) -> Unit,
    showModal: () -> Unit
){
    Row( verticalAlignment = Alignment.CenterVertically){

        if(modStatus != null && modStatus == true){
            AsyncImage(
                model = "https://static-cdn.jtvnw.net/badges/v1/3267646d-33f0-4b17-b3df-f923a41db1d0/3",
                contentDescription = "Moderator badge"
            )
        }
        TextField(
            modifier = Modifier
                .weight(2f)

            ,
            value = textFieldValue.value,
            shape = RoundedCornerShape(8.dp),
            onValueChange = { newText ->
                filterMethod("username",newText.text)
                textFieldValue.value = TextFieldValue(
                    text = newText.text,
                    selection = TextRange(newText.text.length)
                )
                //text = newText
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.White,
                backgroundColor = Color.Black,
                cursorColor = Color.White,
                disabledLabelColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            placeholder = {
                Text("Send a message",color = Color.White)
            }
        )
        if(textFieldValue.value.text.length >0){
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription ="Send chat",
                modifier = Modifier
                    .size(35.dp)
                    .clickable { chat(textFieldValue.value.text) }
                    .padding(start = 5.dp),
                tint = Color.White
            )
        }else{
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription ="Show side modal",
                modifier = Modifier
                    .size(35.dp)
                    .clickable { showModal() }
                    .padding(start = 5.dp),
                tint = Color.White
            )
        }


    }
}


@Composable
fun TimeoutDialog(
    onDismissRequest: () -> Unit,
    username:String,

    timeoutDuration:Int,
    timeoutReason:String,
    changeTimeoutDuration:(Int) ->Unit,
    changeTimeoutReason:(String) ->Unit,
    closeDialog: () -> Unit,
    timeOutUser:()->Unit
) {


    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier
                .padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    Text("Timeout: ",fontSize = 22.sp)
                    Text(username,fontSize = 22.sp)
                }
                Divider(color = Color.Red, thickness = 1.dp,modifier = Modifier.fillMaxWidth())
                Text("Duration :")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly){
                    Column {
                        RadioButton(
                            selected = timeoutDuration == 10,
                            onClick = { changeTimeoutDuration(10) }
                        )
                        Text("10sec")
                    }
                    Column {
                        RadioButton(
                            selected = timeoutDuration == 60,
                            onClick = { changeTimeoutDuration(60)  }
                        )
                        Text("1min")
                    }
                    Column {
                        RadioButton(
                            selected = timeoutDuration == 600,
                            onClick = { changeTimeoutDuration(600) }
                        )
                        Text("10min")
                    }
                    Column {
                        RadioButton(
                            selected = timeoutDuration == 1800,
                            onClick = { changeTimeoutDuration(1800) }
                        )
                        Text("30min")
                    }
                }
                OutlinedTextField(
                    value = timeoutReason,
                    onValueChange = { changeTimeoutReason(it) },
                    label = { Text("Reason") }
                )
                Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                    Button(onClick = { onDismissRequest() }, modifier = Modifier.padding(10.dp)) {
                        Text("Cancel")
                    }
                    //todo: Implement the details of the timeout implementation
                    Button(onClick = {
                        closeDialog()
                        timeOutUser()
                    }, modifier = Modifier.padding(10.dp)) {
                        Text("Timeout")
                    }
                }


            }


        }
    }
}

@Composable
fun BanDialog(
    onDismissRequest: () -> Unit,
    username:String,
    banDuration:Int,
    banReason:String,
    changeBanDuration:(Int) ->Unit,
    changeBanReason:(String) ->Unit,
    banUser:(BanUser) -> Unit,
    clickedUserId: String,
    closeDialog:() ->Unit,
    closeBottomModal: ()->Unit,
) {


    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
            ,
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier
                .padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    Text("Ban: ",fontSize = 22.sp)
                    Text(username,fontSize = 22.sp)
                }
                Divider(color = Color.Red, thickness = 1.dp,modifier = Modifier.fillMaxWidth())
                Text("Duration :")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly){
                    Column {
                        RadioButton(
                            selected = banDuration == 604800,
                            onClick = { changeBanDuration(604800) }
                        )
                        Text("1 week")
                    }
                    Column {
                        RadioButton(
                            selected = banDuration == 1209600,
                            onClick = { changeBanDuration(1209600) }
                        )
                        Text("2 weeks")
                    }
                    Column {
                        RadioButton(
                            selected = banDuration == 0,
                            onClick = { changeBanDuration(0) }
                        )
                        Text("Permanently")
                    }
                }
                OutlinedTextField(
                    value = banReason,
                    onValueChange = { changeBanReason(it) },
                    label = { Text("Reason") }
                )
                Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(onClick = { onDismissRequest() }, modifier = Modifier.padding(10.dp)) {
                        Text("Cancel")
                    }


                    Button(
                            onClick = {
                                closeDialog()
                                closeBottomModal()
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
                            Text("Ban")
                        }
                }


            }


        }
    }
}

private fun getDismissDirection(from: DismissValue, to: DismissValue): DismissDirection? {
    return when {
        // settled at the default state
        from == to && from == Default -> null
        // has been dismissed to the end
        from == to && from == DismissValue.DismissedToEnd -> DismissDirection.StartToEnd
        // has been dismissed to the start
        from == to && from == DismissValue.DismissedToStart -> DismissDirection.EndToStart
        // is currently being dismissed to the end
        from == Default && to == DismissValue.DismissedToEnd -> DismissDirection.StartToEnd
        // is currently being dismissed to the start
        from == Default && to == DismissValue.DismissedToStart -> DismissDirection.EndToStart
        // has been dismissed to the end but is now animated back to default
        from == DismissValue.DismissedToEnd && to == Default -> DismissDirection.StartToEnd
        // has been dismissed to the start but is now animated back to default
        from == DismissValue.DismissedToStart && to == Default -> DismissDirection.EndToStart
        else -> null
    }
}