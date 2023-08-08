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
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.ModalDrawer

@Composable
fun StreamView(
    streamViewModel: StreamViewModel
){

    val stringList = streamViewModel.listChats.toList()
    val drawerState = rememberDrawerState(DrawerValue.Open)


    ModalDrawer(
        drawerState = drawerState,
        drawerContent = { Text(text ="THE DRAWER", fontSize = 30.sp)}
    ){
        TextChat(
            stringList = stringList,
            addItem ={
                    string ->streamViewModel.sendMessage(string)
            },
            drawerState=drawerState
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

@SuppressLint("SuspiciousIndentation")
@Composable
fun TextChat(
    stringList:List<TwitchUserData>,
    addItem: (String) -> Unit,
    drawerState: DrawerState
){
   Log.d("textUIstoof",stringList.size.toString())
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
                if(stringList.size > 6){
                    lazyColumnListState.scrollToItem(stringList.size)
                }
            }
            items(stringList){twitchUser ->
                val color = Color(parseColor(twitchUser.color))
                    if(stringList.isNotEmpty()){

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
                                    withStyle(style = SpanStyle(color = color)) {
                                        append("${twitchUser.displayName}")
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
            chat = {text -> addItem(text)}
        )
    }
}

@Composable
fun EnterChat(
    modifier: Modifier,
    chat: (String) -> Unit
){
        var text by remember { mutableStateOf("") }
    Row(modifier){
        TextField(
            modifier = Modifier.weight(2f),
            value = text,
            onValueChange = { newText ->
                text = newText
            }
        )
        Button(
            modifier = Modifier.weight(1f),
            onClick = { chat(text) }
        ) {
            Text(text = "Chat")
        }
    }
        
}

