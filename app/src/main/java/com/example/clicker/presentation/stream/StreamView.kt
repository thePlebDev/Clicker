package com.example.clicker.presentation.stream

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch


@Composable
fun StreamView(
    streamViewModel: StreamViewModel
){

    val stringList = streamViewModel.listChats.toList()

    TextChat(
        stringList = stringList,
        addItem ={
            string ->streamViewModel.addItem(string)
        }
    )


}

@Composable
fun TextChat(
    stringList:List<String>,
    addItem: (String) -> Unit
){
    val lazyColumnListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    Box(modifier = Modifier.padding(top=200.dp)){
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
            items(stringList){string ->
                if(stringList.isNotEmpty()){
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                            .clickable { },
                        elevation = 10.dp
                    ){
                        Text(string, fontSize = 20.sp)

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

