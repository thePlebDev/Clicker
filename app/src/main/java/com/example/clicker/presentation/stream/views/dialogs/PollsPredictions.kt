package com.example.clicker.presentation.stream.views.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun CreateNewPollDialog(){
    var showDialog by rememberSaveable { mutableStateOf(true) }
    if(showDialog){
        MinimalDialog(
            onDismissRequest = {showDialog = false}
        )
    }


}

@Composable
fun MinimalDialog(onDismissRequest: () -> Unit) {
    var response2 by remember { mutableStateOf("") }
    var response3 by remember { mutableStateOf("") }
    var response4 by remember { mutableStateOf("") }
    var response5 by remember { mutableStateOf("") }
    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            backgroundColor= MaterialTheme.colorScheme.primary,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = "Create a New Poll",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 25.sp, modifier = Modifier.padding(start = 20.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close poll dialog",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                onDismissRequest()
                            },
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                PollQuestionTitle("Question",60)
                PollQuestionTitle("Responses (Minimum 2)",25)
                SimpleFilledTextFieldSample(
                    text =response2,
                    updateText = {newText -> response2 = newText}
                )
                SimpleFilledTextFieldSample(
                    text =response3,
                    updateText = {newText -> response3 = newText}
                )
                SimpleFilledTextFieldSample(
                    text =response4,
                    updateText = {newText -> response4 = newText}
                )
                SimpleFilledTextFieldSample(
                    text =response5,
                    updateText = {newText -> response5 = newText}
                )

                ChannelPointsEnabled()


                DropdownDemo()
                CancelStartButtonRow()
                //make duration sample with cancel and start
            }


        }
    }
}

@Composable
fun PollQuestionTitle(
     title:String,
     characterLimit:Int,
){
    var text by remember { mutableStateOf("") }
    var textLengthLeft by remember(text) {
        mutableStateOf(characterLimit - text.length)
    }
    Column(){

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp, top = 30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            Text(title,fontSize=20.sp,color = MaterialTheme.colorScheme.onPrimary)
            Text("$textLengthLeft",fontSize=20.sp,color = MaterialTheme.colorScheme.onPrimary.copy(.6f))
        }
        SimpleFilledTextFieldSampleTesting(
            text =text,
            updateText={newText ->text = newText},
            maxLength = characterLimit
        )
    }
}
@Composable
fun SimpleFilledTextFieldSampleTesting(
    text:String,
    updateText:(String)->Unit,
    maxLength:Int
) {
    val secondaryColor =Color(0xFF6650a4)

    val selectionColors = TextSelectionColors(
        handleColor = secondaryColor, // Set the color of the selection handles
        backgroundColor = secondaryColor // Set the background color of the selected text
    )

    Column(modifier = Modifier.fillMaxWidth()){
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            onValueChange = {
                if ((text.length +1)<= maxLength || (it.length+1) < (text.length+1)) {
                    updateText(it)
                }
                            },
            label = { },
            colors = TextFieldDefaults.colors(
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
    fun SimpleFilledTextFieldSample(
    text:String,
    updateText: (String) -> Unit
    ) {
      //  var text by remember { mutableStateOf("") }
    val secondaryColor =Color(0xFF6650a4)

    val selectionColors = TextSelectionColors(
        handleColor = secondaryColor, // Set the color of the selection handles
        backgroundColor = secondaryColor // Set the background color of the selected text
    )

    Column(modifier = Modifier.fillMaxWidth()){
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            onValueChange = {
                if ((text.length +1)<= 25 || (it.length+1) < (text.length+1)) {
                    updateText(it)
                }
            },
            label = { },
            colors = TextFieldDefaults.colors(
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
fun ChannelPointsEnabled(){
    val checkedState = remember { mutableStateOf(true) }
    var text by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxWidth()){
        Row(verticalAlignment = Alignment.CenterVertically){
            Checkbox(
                checked = checkedState.value,
                onCheckedChange = { checkedState.value = it },
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.secondary, uncheckedColor = MaterialTheme.colorScheme.onPrimary)
            )
            Text("Enable channel point voting",color=MaterialTheme.colorScheme.onPrimary)
        }
        Spacer(modifier =Modifier.height(5.dp))
        Row(modifier= Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
            TextField(
                value = text,
                onValueChange = { text = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Channel points per vote") },
                colors = TextFieldDefaults.colors(
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.secondary
                )

            )
        }
        Spacer(modifier =Modifier.height(5.dp))
    }

}

@Composable
fun DropdownDemo() {
    var expanded by remember { mutableStateOf(false) }
    val items = listOf("1m", "2m", "3m", "5m", "10m")
    var selectedIndex by remember { mutableStateOf(0) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Duration",fontSize=20.sp,color = MaterialTheme.colorScheme.onPrimary)
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(items[selectedIndex],modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { expanded = true })
                .background(
                    MaterialTheme.colorScheme.secondary
                ),
                color = MaterialTheme.colorScheme.onSecondary
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.secondary
                    )
            ) {
                items.forEachIndexed { index, text ->
                    DropdownMenuItem(
                        onClick = {
                            selectedIndex = index
                            expanded = false
                        },
                        text={
                            Column() {
                                Text(text =text,color=MaterialTheme.colorScheme.onSecondary)
                                Divider(color = MaterialTheme.colorScheme.onSecondary, thickness = 1.dp)
                            }
                        }
                    )
                }
            }
        }
    }

}

@Composable
fun CancelStartButtonRow(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.End
    ){
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Cancel")
        }
        Spacer(modifier = Modifier.width(5.dp))
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Start")
        }

    }
}