package com.example.clicker.presentation.modView.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.clicker.R


object SharedBottomModal{
    @Composable
    fun ClickedUserBottomModal(
        bottomModalHeaders:@Composable BottomModalHeaders.() -> Unit,
        bottomModalButtons:@Composable BottomModalContent.() -> Unit,
        bottomModalRecentMessages:@Composable BottomModalContent.() -> Unit,
    ){

        val headerScope = remember{ BottomModalHeaders() }
        val buttonScope = remember{ BottomModalContent() }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            with(headerScope){bottomModalHeaders()}
            with(buttonScope){
                bottomModalButtons()
                Text(stringResource(R.string.recent_messages),color = MaterialTheme.colorScheme.onPrimary,modifier = Modifier.padding(bottom=5.dp))
                bottomModalRecentMessages()
            }
        }


    }
}

@Stable
class BottomModalContent(){
    @Composable
    fun ContentBottom(
        banned: Boolean,
        loggedInUserIsMod: Boolean,
        clickedUserIsMod:Boolean,
        closeBottomModal: () -> Unit,
        unbanUser: () -> Unit,
        openTimeoutDialog:() -> Unit,
        openBanDialog:() -> Unit,
        shouldMonitorUser:Boolean,
        updateShouldMonitorUser:()->Unit
    ){

        Log.d("ClickedUserBottomModal","loggedInUserIsMod --> $loggedInUserIsMod")
        Log.d("ClickedUserBottomModal","clickedUserIsMod --> $clickedUserIsMod")
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End){
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (loggedInUserIsMod && !clickedUserIsMod ) {
                    Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.End) {
                        Button(
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
                            onClick = {
                                openTimeoutDialog()
                            },
                            modifier = Modifier.padding(end = 20.dp)
                        ) {
                            Text(stringResource(R.string.timeout),color = MaterialTheme.colorScheme.onSecondary)
                        }
                        BanUnBanButtons(
                            banned =banned,
                            closeBottomModal={closeBottomModal()},
                            unbanUser ={unbanUser()},
                            openBanDialog={openBanDialog()}

                        )
                    }
                }
            }
            Row(modifier=Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){

                Icon(
                    painter = painterResource(id = R.drawable.visibility_24),
                    "Moderation Icon",
                    tint= if(shouldMonitorUser) Color.Yellow else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(35.dp)
                )


                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
                    onClick = {
                        updateShouldMonitorUser()
                    }) {
                    Text(
                        if(shouldMonitorUser)"UnMonitor" else "Monitor",
                        color = MaterialTheme.colorScheme.onSecondary)
                }
            }


        }/**End of the column**/

    }

    @Composable
    fun BanUnBanButtons(
        banned:Boolean,
        closeBottomModal: () -> Unit,
        unbanUser: () -> Unit,
        openBanDialog: () -> Unit
    ){
        if (banned) {
            Button(
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
                onClick = {
                    closeBottomModal()
                    unbanUser()
                }) {
                Text(stringResource(R.string.unban),color = MaterialTheme.colorScheme.onSecondary)
            }
        } else {
            Button(
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
                onClick = {
                    openBanDialog()
                }) {
                Text(stringResource(R.string.ban),color = MaterialTheme.colorScheme.onSecondary)
            }
        }
    }

    @Composable
    fun ClickedUserMessages(
        clickedUsernameChats: List<String>
    ){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .height(100.dp)
                .background(MaterialTheme.colorScheme.primary)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            items(clickedUsernameChats) {message->

                Text(

                    buildAnnotatedString {
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary, fontSize = MaterialTheme.typography.headlineSmall.fontSize)) {
                            append("Message: ")
                        }


                        withStyle(style = SpanStyle(fontSize = MaterialTheme.typography.headlineSmall.fontSize, color = MaterialTheme.colorScheme.onPrimary)) {
                            append(message)
                        }

                    },
                    modifier = Modifier.fillMaxWidth().padding(5.dp)
                )


            }
        }
    }
}
@Stable
class BottomModalHeaders(){

    @Composable
    fun ContentHeaderRow(
        clickedUsername: String,
        textFieldValue: MutableState<TextFieldValue>,
        closeBottomModal: () -> Unit,
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(R.string.user_icon_description),
                    modifier = Modifier
                        .clickable { }
                        .size(35.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(clickedUsername, color = MaterialTheme.colorScheme.onPrimary, fontSize = MaterialTheme.typography.headlineMedium.fontSize)
            }

            Button(
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
                onClick = {
                    textFieldValue.value = TextFieldValue(
                        text = textFieldValue.value.text + "@$clickedUsername ",
                        selection = TextRange(textFieldValue.value.selection.start+"@$clickedUsername ".length)
                    )
                    closeBottomModal()
                }) {
                Text(stringResource(R.string.reply),color = MaterialTheme.colorScheme.onSecondary)
            }
        }
    }

}