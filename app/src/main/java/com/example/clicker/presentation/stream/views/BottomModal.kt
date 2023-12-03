package com.example.clicker.presentation.stream.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clicker.R
import kotlinx.coroutines.launch

/**
 * BottomModal pops up when the user clicks on an individual chat message during stream chat.
 * It contains 3 components:
 *
 * - [ContentBanner] : shown at the top of the [ModalBottomSheetLayout]. Displays clicked username and reply button
 *
 * - [ContentBottom]: shown below [ContentBanner], contains ban, unban and timeout buttons
 *
 * - [ClickedUserMessages] : Shown below [ContentBottom] and displays a list of clickedUsername's chat
 *
 * */
object BottomModal{

    /**
     * Composable function meant to display a scrollable list of a users recent chats in a [LazyColumn]
     *
     * @param clickedUsernameChats String list of a users most recent chat messages
     * */
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
            items(clickedUsernameChats) {
                Text(
                    it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 15.sp

                )
            }
        }
    }

    /**
     * Will display a [Row] containing a username and a reply button
     *
     *
     * @param clickedUsername String meant to represent the username of a individual user
     * @param bottomModalState the state of a [ModalBottomSheetLayout] and used to close the modal when the reply button is pressed
     * @param textFieldValue the state of what the user is typing. When the user clicks the reply button the [clickedUsername] will be added to it
     * */
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun ContentBanner(
        clickedUsername: String,
        bottomModalState: ModalBottomSheetState,
        textFieldValue: MutableState<TextFieldValue>,
    ){
        val scope = rememberCoroutineScope()
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
                Text(clickedUsername, color = MaterialTheme.colorScheme.onPrimary, fontSize = 20.sp)
            }

            Button(
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
                onClick = {
                    scope.launch {
                        textFieldValue.value = TextFieldValue(
                            text = textFieldValue.value.text + "@$clickedUsername ",
                            selection = TextRange(textFieldValue.value.selection.start+"@$clickedUsername ".length)
                        )
                        bottomModalState.hide()
                    }
                }) {
                Text(stringResource(R.string.reply),color = MaterialTheme.colorScheme.onSecondary)
            }
        }
    }

    /**
     * Will display a [Row] containing the ban, unban, timeout buttons
     *
     *
     * @param banned Boolean to determine if the chatter is already banned or not
     * @param isMod Boolean to determine if the chatter is a mod or not
     * @param closeBottomModal function used to close the [ModalBottomSheetLayout] which the composable should be in
     * @param unbanUser function used to unban user if [banned] is true
     * @param openTimeoutDialog function used to open the TimeoutDialog
     * @param openBanDialog function used to open the BanDialog
     * */
    @Composable
    fun ContentBottom(
        banned: Boolean,
        isMod: Boolean,
        closeBottomModal: () -> Unit,
        unbanUser: () -> Unit,
        openTimeoutDialog:() -> Unit,
        openBanDialog:() -> Unit,
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.recent_messages),color = MaterialTheme.colorScheme.onPrimary)
            if (isMod) {
                Row() {
                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
                        onClick = {
                            openTimeoutDialog()
                        },
                        modifier = Modifier.padding(end = 20.dp)
                    ) {
                        Text(stringResource(R.string.timeout),color = MaterialTheme.colorScheme.onSecondary)
                    }
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
            }
        }
    }
}