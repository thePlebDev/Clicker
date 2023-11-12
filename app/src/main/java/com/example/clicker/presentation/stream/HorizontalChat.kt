package com.example.clicker.presentation.stream

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.substring
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R


@Composable
fun HorizontalChat(
    streamViewModel: StreamViewModel
){

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primary)){
        ChatList()
        EnterChatBox(
            modifier =Modifier.align(Alignment.BottomCenter),
            textFieldValue = streamViewModel.textFieldValue,
            filterMethod = {text,character,index ->streamViewModel.filterMethodBetter(text,character,index)},
            filteredChatList = streamViewModel.filteredChatList,
            textRange = streamViewModel.chatTextRange.value,
            changeTextRange = {range -> streamViewModel.changeTextRange(range)}
        )

    }


}

@Composable
fun EnterChatBox(
    modifier:Modifier = Modifier,
    textFieldValue: MutableState<TextFieldValue>,
    filterMethod:(String,Char,Int) -> Unit,
    filteredChatList:List<String>,
    changeTextRange:(TextRange) -> Unit,
    textRange: TextRange
){
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary),
    ){


        AutoCompleteUserNameRow(filteredChatList,textFieldValue,textRange)
       ChatTextField(
           textFieldValue,
           filterMethod = {text,character,index ->filterMethod(text,character,index)},
           changeTextRange = {range -> changeTextRange(range) }
       )

    }
}
@Composable
fun AutoCompleteUserNameRow(
    listName:List<String>,
    textFieldValue: MutableState<TextFieldValue>,
    textRange: TextRange
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

                            //autoChangeText()
                            textFieldValue.value = TextFieldValue(
                                text = textFieldValue.value.text + name,
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
//THis needs to be implemented
fun autoChangeText(text:String,index:Int,clickedName:String){
    text
}

@Composable
fun ChatTextField(
    textFieldValue: MutableState<TextFieldValue>,
    filterMethod:(String,Char,Int) -> Unit,
    changeTextRange:(TextRange) -> Unit
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
                //filterMethod("username", newText.text)
                val index = newText.selection
                changeTextRange(index)
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
fun ChatList(){
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 80.dp)){

        items(35) { index ->
            Text(text = "Item: $index",fontSize = 30.sp,color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}