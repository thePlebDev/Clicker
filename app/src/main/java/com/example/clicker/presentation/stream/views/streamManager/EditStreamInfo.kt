package com.example.clicker.presentation.stream.views.streamManager

import android.animation.ObjectAnimator
import android.content.res.Resources
import android.view.View
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R
import com.example.clicker.presentation.home.disableClickAndRipple
import com.example.clicker.presentation.stream.FilterType
import com.example.clicker.presentation.stream.views.AutoMod
import com.example.clicker.util.Response
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun ManageStreamInformation(
    closeStreamInfo:()->Unit,
    streamTitle:String,
    streamCategory:String,
    updateStreamTitle:(String)->Unit,
    showAutoModSettings:Boolean,
    changeSelectedIndex:(Int, FilterType)->Unit,

    swearingIndex:Int,
    sexBasedTermsIndex:Int,
    aggressionIndex:Int,
    bullyingIndex:Int,
    disabilityIndex:Int,
    sexualityIndex:Int,
    misogynyIndex:Int,
    raceIndex:Int,
    sliderPosition: Float,
    changSliderPosition:(Float)->Unit,
    filterText:String,
    isModerator: Response<Boolean>,
    updateAutoModSettings:()->Unit,
    updateAutoModSettingsStatus:Response<Boolean>?,
    updateAutoModSettingsStatusToNull:()->Unit,
    updateChannelInfo:()->Unit,
){
    if(showAutoModSettings){
        EditAutoModSettings(
            closeStreamInfo={closeStreamInfo()},
            changeSelectedIndex = {item,filterType ->changeSelectedIndex(item,filterType)},
            swearingIndex = swearingIndex,
            sexBasedTermsIndex = sexBasedTermsIndex,
            aggressionIndex = aggressionIndex,
            bullyingIndex = bullyingIndex,
            disabilityIndex = disabilityIndex,
            sexualityIndex = sexualityIndex,
            misogynyIndex = misogynyIndex,
            raceIndex = raceIndex,
            sliderPosition =sliderPosition,
            changSliderPosition = {float -> changSliderPosition(float)},
            filterText=filterText,
            isModerator =isModerator,
            updateAutoModSettings={updateAutoModSettings()},
            updateAutoModSettingsStatus=updateAutoModSettingsStatus,
            updateAutoModSettingsStatusToNull ={updateAutoModSettingsStatusToNull()}


        )
    }else{
        EditStreamInfo(
            closeStreamInfo ={closeStreamInfo()},
            streamTitle = streamTitle,
            updateStreamTitle = { newText -> updateStreamTitle(newText) },
            streamCategory = streamCategory,
            updateChannelInfo={updateChannelInfo()}

        )
    }


}
@Composable
fun EditAutoModSettings(
    closeStreamInfo:()->Unit,
    changeSelectedIndex:(Int, FilterType)->Unit,
    swearingIndex:Int,
    sexBasedTermsIndex:Int,
    aggressionIndex:Int,
    bullyingIndex:Int,
    disabilityIndex:Int,
    sexualityIndex:Int,
    misogynyIndex:Int,
    raceIndex:Int,

    sliderPosition: Float,
    changSliderPosition:(Float)->Unit,
    filterText:String,
    isModerator: Response<Boolean>,
    updateAutoModSettings:()->Unit,
    updateAutoModSettingsStatus:Response<Boolean>?,
    updateAutoModSettingsStatusToNull:()->Unit
){
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            EditAutoModTitle(
                closeStreamInfo={closeStreamInfo()},
                title ="AutoMod Info",
                contentDescription = "close auto mod info",
                isModerator =isModerator,
                updateAutoModSettings ={updateAutoModSettings()}
            )
        }

    ) {contentPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)){
            Column(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.primary)

            ) {

                AutoMod.Settings(
                    sliderPosition =sliderPosition,
                    changSliderPosition = {float -> changSliderPosition(float)},
                    discriminationFilterList = listOf("No filtering", "Less filtering", "Some filtering", "More filtering", "Maximum filtering"),
                    changeSelectedIndex = {item,filterType ->changeSelectedIndex(item,filterType)},
                    updateAutoModSettings = {  },
                    swearingIndex = swearingIndex,
                    sexBasedTermsIndex = sexBasedTermsIndex,
                    aggressionIndex = aggressionIndex,
                    bullyingIndex = bullyingIndex,
                    disabilityIndex = disabilityIndex,
                    sexualityIndex = sexualityIndex,
                    misogynyIndex = misogynyIndex,
                    raceIndex = raceIndex,
                    isModerator = true,
                    filterText = filterText
                )
            } // end of column
            when(updateAutoModSettingsStatus){
                is Response.Loading ->{
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is Response.Success ->{
                    Text("Success",
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 5.dp)
                            .background(Color.Green.copy(0.7f))
                            .align(Alignment.Center)
                            .clickable {
                                scope.launch {
                                    updateAutoModSettingsStatusToNull()
                                }
                            },
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                }
                is Response.Failure ->{
                    Text("Failed",
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 5.dp)
                            .background(Color.Red.copy(0.7f))
                            .align(Alignment.Center)
                            .clickable {
                                scope.launch {
                                    updateAutoModSettingsStatusToNull()
                                }
                            },
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                else ->{

                }
            }

        }//end of box

    }

}
@Composable
fun EditAutoModTitle(
    closeStreamInfo:()->Unit,
    title:String,
    contentDescription:String,
    isModerator: Response<Boolean>,
    updateAutoModSettings:()->Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.DarkGray)
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Row(verticalAlignment = Alignment.CenterVertically){
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = contentDescription,
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        closeStreamInfo()
                    },
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Text(text =title,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 25.sp,modifier = Modifier.padding(start=20.dp))
        }

        IsModeratorButton(
            isModerator = isModerator,
            updateAutoModSettings={updateAutoModSettings()}
        )


    }
}
@Composable
fun EditStreamInfo(
    closeStreamInfo:()->Unit,
    streamTitle:String,
    updateStreamTitle:(String)->Unit,
    updateChannelInfo:()->Unit,
    streamCategory:String,

){
    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
    ){
        InfoTitle(
            closeStreamInfo={closeStreamInfo()},
            title ="Stream Info",
            contentDescription = "close edit stream info",
            updateChannelInfo = {updateChannelInfo()}
        )
        ChangeStreamTitleTextField(
            streamTitle =streamTitle,
            updateStreamTitle={text ->updateStreamTitle(text)}
        )
//        ChangeStreamCategoryTextField(
//            streamTitle =streamCategory,
//            updateText={text ->updateText(text)}
//        )

    }
}

@Composable
fun InfoTitle(
    closeStreamInfo:()->Unit,
    updateChannelInfo:()->Unit,
    title:String,
    contentDescription:String,
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.DarkGray)
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Row(verticalAlignment = Alignment.CenterVertically){
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = contentDescription,
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        closeStreamInfo()
                    },
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Text(text =title,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 25.sp,modifier = Modifier.padding(start=20.dp))
        }

        Button(
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            onClick = {
                updateChannelInfo()
            },
            shape = RoundedCornerShape(5.dp)
        ) {
            Text(text ="Save",
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = 25.sp)
        }


    }

}

@Composable
fun IsModeratorButton(
    isModerator: Response<Boolean>,
    updateAutoModSettings:()->Unit,
){
    when(isModerator){
        is Response.Loading ->{
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                onClick = {},
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(text ="LOADING",
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 25.sp)
            }
        }
        is Response.Success ->{
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                onClick = {
                    updateAutoModSettings()
                },
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(text ="Save",
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 25.sp)
            }
        }
        is Response.Failure ->{
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                onClick = {
                    updateAutoModSettings()
                },
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(text ="Retry",
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 25.sp)
            }
        }
    }


}
@Composable
fun ChangeStreamTitleTextField(
    streamTitle:String,
    updateStreamTitle:(String)->Unit
) {

    var textLengthLeft by remember(streamTitle) {
        mutableStateOf(141 - streamTitle.length)
    }


    Column(modifier = Modifier.fillMaxWidth()){
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp, top = 30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            Text("Title",fontSize=20.sp,color = MaterialTheme.colorScheme.onPrimary)
            Text(textLengthLeft.toString(),fontSize=20.sp,color = MaterialTheme.colorScheme.onPrimary.copy(.6f))
        }

        CustomTextField(
            streamTitle=streamTitle,
            updateText={
                    newText -> updateStreamTitle(newText)
            }
        )
    }


}
@Composable
fun ChangeStreamCategoryTextField(
    streamTitle:String,
    updateText:(String)->Unit
) {
    var text by remember { mutableStateOf(streamTitle) }



    Column(modifier = Modifier.fillMaxWidth()){
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp, top = 30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            Text("Category",fontSize=20.sp,color = MaterialTheme.colorScheme.onPrimary)

        }

        SimpleFilledTextFieldSampleTesting(
            streamTitle=text,
            updateText={newText -> text = newText}
        )
    }


}

@Composable
fun SimpleFilledTextFieldSampleTesting(
    streamTitle:String,
    updateText:(String)->Unit
) {
    val secondaryColor =Color(0xFF6650a4)

    val selectionColors = TextSelectionColors(
        handleColor = secondaryColor, // Set the color of the selection handles
        backgroundColor = secondaryColor // Set the background color of the selected text
    )

    Column(modifier = Modifier.fillMaxWidth()){
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = streamTitle,
            singleLine = true,
            onValueChange = {

                    updateText(it)

            },
            shape = RoundedCornerShape(8.dp),
            label = { },
            colors = androidx.compose.material3.TextFieldDefaults.colors(
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
            singleLine = false,
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

