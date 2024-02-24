package com.example.clicker.presentation.stream.views.streamManager

import android.animation.ObjectAnimator
import android.content.res.Resources
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R


@Composable
fun EditStreamInfo(
    closeEditStreamInfo:()->Unit,
    streamTitle:String,
    updateText:(String)->Unit
){
    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
    ){
        EditStreamInfoTitle(closeEditStreamInfo={closeEditStreamInfo()})
        ChangeStreamTitleTextField(
            streamTitle =streamTitle,
            updateText={text ->updateText(text)}
        )

    }

}

@Composable
fun EditStreamInfoTitle(
    closeEditStreamInfo:()->Unit
){
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.DarkGray).padding( 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Row(verticalAlignment = Alignment.CenterVertically){
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close edit stream info",
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        closeEditStreamInfo()
                    },
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Text(text ="Stream Info",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 25.sp,modifier = Modifier.padding(start=20.dp))
        }

        Text(text ="Save",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 25.sp,modifier = Modifier.padding(start=20.dp))


    }
}
@Composable
fun ChangeStreamTitleTextField(
    streamTitle:String,
    updateText:(String)->Unit
) {
    var text by remember { mutableStateOf(streamTitle) }
    var textLengthLeft by remember(text) {
        mutableStateOf(141 - text.length)
    }


    Column(modifier = Modifier.fillMaxWidth()){
        Row(modifier = Modifier.fillMaxWidth().padding(bottom=5.dp, top = 30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            Text("Title",fontSize=20.sp,color = MaterialTheme.colorScheme.onPrimary)
            Text(textLengthLeft.toString(),fontSize=20.sp,color = MaterialTheme.colorScheme.onPrimary.copy(.6f))
        }

        CustomTextField(
            streamTitle=text,
            updateText={newText -> text = newText}
        )
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
            singleLine = true,
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

