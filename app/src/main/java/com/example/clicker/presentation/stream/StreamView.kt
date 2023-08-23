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
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.ModalDrawer
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import coil.compose.AsyncImage
import com.example.clicker.network.models.ChatSettingsData
import com.example.clicker.network.websockets.LoggedInUserData
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
        drawerContent = { DrawerContent(chatSettingData)}
    ){
        TextChat(
            twitchUserChat = twitchUserChat,
            addItem ={
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
     chatSettingsData: Response<ChatSettingsData>
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
                ChatSettingsDataUI(chatSettingsData.data)
            }
            is Response.Failure ->{
                Text("FAILED TO FETCH DATA")
            }
        }
    }


}
@Composable
fun ChatSettingsDataUI(
    chatSettingsData: ChatSettingsData
){
    Column(
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(text = "Slow mode: ",fontSize = 25.sp)
            Text(chatSettingsData.slowMode.toString(),fontSize = 25.sp)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(text = "Follower mode: ",fontSize = 25.sp)
            Text(chatSettingsData.followerMode.toString(),fontSize = 25.sp)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(text = "Subscriber mode: ",fontSize = 25.sp)
            Text(chatSettingsData.subscriberMode.toString(),fontSize = 25.sp)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(text = "Emote mode: ",fontSize = 25.sp)
            Text(chatSettingsData.emoteMode.toString(),fontSize = 25.sp)
        }

    }

}

@SuppressLint("SuspiciousIndentation")
@Composable
fun TextChat(
    twitchUserChat:List<TwitchUserData>,
    addItem: (String) -> Unit,
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


                    }

                }



            }
        }

        EnterChat(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(5.dp)
                .fillMaxWidth(),
            chat = {text -> addItem(text)},
            showModal = {coroutineScope.launch { drawerState.open() }},
            modStatus = modStatus
        )
    }
}

@Composable
fun EnterChat(
    modifier: Modifier,
    chat: (String) -> Unit,
    showModal:()->Unit,
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

