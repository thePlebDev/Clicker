package com.example.clicker.presentation.stream

import android.annotation.SuppressLint
import android.content.res.Configuration
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
import com.example.clicker.network.websockets.TwitchUserData
import kotlinx.coroutines.launch
import android.graphics.Color.parseColor
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Switch
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import com.example.clicker.network.models.ChatSettingsData
import com.example.clicker.network.websockets.LoggedInUserData
import com.example.clicker.network.websockets.MessageType
import com.example.clicker.util.Response

@Composable
fun StreamView(
    streamViewModel: StreamViewModel
){

    val twitchUserChat = streamViewModel.listChats.toList()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val chatSettingData = streamViewModel.state.value.chatSettings
    val modStatus = streamViewModel.state.value.loggedInUserData?.mod
   // val modStatus = false
    Log.d("loggedInUserUiState","modStatus --->   $modStatus")


    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                chatSettingData,
                showChatSettingAlert = streamViewModel.state.value.showChatSettingAlert,
                slowModeToggle = {chatSettingsData -> streamViewModel.slowModeChatSettings(chatSettingsData) },
                followerModeToggle = {chatSettingsData -> streamViewModel.followerModeToggle(chatSettingsData) },
                subscriberModeToggle = {chatSettingsData -> streamViewModel.subscriberModeToggle(chatSettingsData) },
                emoteModeToggle = {chatSettingsData -> streamViewModel.emoteModeToggle(chatSettingsData) },
                enableSlowModeSwitch = streamViewModel.state.value.enableSlowMode,
                enableFollowerModeSwitch = streamViewModel.state.value.enableFollowerMode,
                enableSubscriberSwitch = streamViewModel.state.value.enableSubscriberMode,
                enableEmoteModeSwitch = streamViewModel.state.value.enableEmoteMode
            )
        }
    ){
        TextChat(
            twitchUserChat = twitchUserChat,
            sendMessageToWebSocket ={
                    string ->streamViewModel.sendMessage(string)
            },
            drawerState=drawerState,
            modStatus  =modStatus
        )
    }


    val testingString = ""
    val anotherThingy = testingString.indexOf("badge-info")
    var orientation by remember { mutableStateOf(Configuration.ORIENTATION_PORTRAIT) }
    val configuration = LocalConfiguration.current
//
//    LaunchedEffect(configuration) {
//        // Save any changes to the orientation value on the configuration object
//        snapshotFlow { configuration.orientation }
//            .collect { orientation = it }
//    }
//
//    when (orientation) {
//        Configuration.ORIENTATION_LANDSCAPE -> {
//
//            Column(){
//                Text("LANDSCAPE",fontSize=30.sp,color= Color.Red)
//                Text("LANDSCAPE",fontSize=30.sp,color= Color.Red)
//                Text("LANDSCAPE",fontSize=30.sp,color= Color.Red)
//                Text("LANDSCAPE",fontSize=30.sp,color= Color.Red)
//            }
//        }
//        else -> {
//            Column(){
//                Text("PORTRAIT",fontSize=30.sp,color= Color.Red)
//                Text("PORTRAIT",fontSize=30.sp,color= Color.Red)
//                Text("PORTRAIT",fontSize=30.sp,color= Color.Red)
//                Text("PORTRAIT",fontSize=30.sp,color= Color.Red)
//            }
//        }
//    }

}

@Composable
fun DrawerContent(
     chatSettingsData: Response<ChatSettingsData>,
     showChatSettingAlert:Boolean,

     slowModeToggle:(ChatSettingsData) -> Unit,
     followerModeToggle:(ChatSettingsData) -> Unit,
     subscriberModeToggle:(ChatSettingsData) -> Unit,
     emoteModeToggle:(ChatSettingsData) -> Unit,

     enableSlowModeSwitch:Boolean,
     enableFollowerModeSwitch:Boolean,
     enableSubscriberSwitch:Boolean,
     enableEmoteModeSwitch:Boolean,
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
                    enableEmoteModeSwitch = enableEmoteModeSwitch
                )
            }
            is Response.Failure ->{
                Text("FAILED TO FETCH CHAT SETTINGS")
            }
        }
    }


}
@Composable
fun ChatSettingsDataUI(
    chatSettingsData: ChatSettingsData,
    showChatSettingAlert:Boolean,
    slowModeToggle:(ChatSettingsData) -> Unit,
    followerModeToggle:(ChatSettingsData) -> Unit,
    subscriberModeToggle:(ChatSettingsData) -> Unit,
    emoteModeToggle:(ChatSettingsData) -> Unit,

    enableSlowModeSwitch:Boolean,
    enableFollowerModeSwitch:Boolean,
    enableSubscriberSwitch:Boolean,
    enableEmoteModeSwitch:Boolean,


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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(text = "Slow mode: ",fontSize = 25.sp)
            Switch(
                enabled = enableSlowModeSwitch,
                checked = slowMode,
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
                                  },
                modifier = Modifier.size(40.dp)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(text = "Follower mode: ",fontSize = 25.sp)

            Switch(
                enabled=enableFollowerModeSwitch,
                checked = followerMode,
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
                                  },
                modifier = Modifier.size(40.dp)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(text = "Subscriber mode: ",fontSize = 25.sp)

            Switch(
                enabled=enableSubscriberSwitch,
                checked = subscriberMode,
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
                },
                modifier = Modifier.size(40.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(text = "Emote mode: ",fontSize = 25.sp)

            Switch(
                enabled = enableEmoteModeSwitch,
                checked = emoteMode,
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
                },
                modifier = Modifier.size(40.dp)
            )
        }



        AnimatedVisibility(visible = showChatSettingAlert) {
            MessageAlertText()
        }



    }// end of the Column

}

//TODO: MAKE IT SO THE X CLICK REMOVES THE REQUEST MESSAGE
@Composable
fun MessageAlertText(){
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
                    .clickable { }
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


@SuppressLint("SuspiciousIndentation")
@Composable
fun TextChat(
    twitchUserChat:List<TwitchUserData>,
    sendMessageToWebSocket: (String) -> Unit,
    drawerState: DrawerState,
    modStatus:Boolean?
){

    val lazyColumnListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Box(){
        LazyColumn(modifier = Modifier
            .padding(bottom = 60.dp)
            .fillMaxSize()
            .background(Color.Red),
            state = lazyColumnListState

        ){
            coroutineScope.launch {
                if(twitchUserChat.size > 6){
                    lazyColumnListState.scrollToItem(twitchUserChat.size)
                }
            }
            items(twitchUserChat){twitchUser ->
                val color = Color(parseColor(twitchUser.color))
                    if(twitchUserChat.isNotEmpty()){
                        if(twitchUser.messageType == MessageType.USER){
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(15.dp)
                                    .clickable {
                                        coroutineScope.launch {
                                            drawerState.open()
                                        }
                                    },
                                elevation = 10.dp
                            ){
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ){

                                    Text(buildAnnotatedString {
                                        withStyle(style = SpanStyle(color = color, fontSize = 17.sp)) {
                                            append("${twitchUser.displayName} :")
                                        }
                                        append(" ${twitchUser.userType}")

                                    }
                                    )

                                }


                            }// end of the Card
                        }
                        if(twitchUser.messageType == MessageType.NOTICE){
                            Text(buildAnnotatedString {
                                withStyle(style = SpanStyle(color = color, fontSize = 17.sp)) {
                                    append("${twitchUser.displayName} :")
                                }
                                append(" ${twitchUser.userType}")

                            },
                                modifier = Modifier.fillMaxWidth().padding(15.dp)
                            )
                        }

                }


            }
        }

        EnterChat(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(5.dp)
                .fillMaxWidth(),
            chat = {text -> sendMessageToWebSocket(text)},
            modStatus = modStatus
        )
        SettingsTab(showModal = {coroutineScope.launch { drawerState.open() }})

    }// end of the Box scope
}

@Composable
fun SettingsTab(
    showModal:()->Unit,
){
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(275.dp)) {
        Card(
            modifier = Modifier.align(Alignment.BottomEnd),
            elevation = 10.dp
        ){
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Send chat",
                modifier = Modifier
                    .size(35.dp)
                    .clickable { showModal() },

            )
        }

    }

}

@Composable
fun EnterChat(
    modifier: Modifier,
    chat: (String) -> Unit,
    modStatus:Boolean?
){
        var text by remember { mutableStateOf("") }

    Row(modifier, verticalAlignment = Alignment.CenterVertically){

        if(modStatus != null && modStatus == true){
            AsyncImage(
                model = "https://static-cdn.jtvnw.net/badges/v1/3267646d-33f0-4b17-b3df-f923a41db1d0/3",
                contentDescription = "Moderator badge"
            )
        }
        TextField(
            modifier = Modifier.weight(2f),
            value = text,
            onValueChange = { newText ->
                text = newText
            }
        )

        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription ="Send chat",
            modifier = Modifier
                .size(35.dp)
                .clickable { chat(text) }
                .padding(start = 5.dp)
        )
    }
        
}

