package com.example.clicker.presentation.stream.views

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.clicker.R
import com.example.clicker.presentation.stream.ForwardSlashCommands
import kotlinx.coroutines.launch

/**
 * TextChat contains all the composables responsible for creating the text chat experience for the users
 *
 * */
object TextChat{


    /**
     * This is the public facing API composable for the [TextChat] object. Placing EnterChat in the code base will
     * give the user full access to the active typing experience
     *
     * @param modifier a modifier used to determine where this Composable should be placed in a box. the typical is BottomCenter
     * @param filteredChatList a list of filtered usernames that is shown when the user types @
     * @param textFieldValue a value of what the user is currently typing
     * @param clickedAutoCompleteText a function to do autocomplete to the [textFieldValue] when run
     * @param modStatus a boolean that determines if the user is a moderator or not
     * @param sendMessageToWebSocket a function that is used to send the message to the websocket hooked up to the TwitchIRC server
     * @param showModal a function to run to show the bottom modal when a individual chat message is clicked
     * @param showOuterBottomModalState a function used to show the a bottom layout sheet
     * */
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun EnterChat(
        modifier: Modifier,
        filteredChatList: List<String>,
        textFieldValue: MutableState<TextFieldValue>,
        clickedAutoCompleteText: (String) -> Unit,
        modStatus: Boolean?,
        sendMessageToWebSocket: (String) -> Unit,
        showModal: () -> Unit,
        showOuterBottomModalState:() ->Unit,
        newFilterMethod:(TextFieldValue) ->Unit,
        forwardSlashCommands: List<ForwardSlashCommands>
    ){
        TextChatBuilders.EnterChat(
            modifier = modifier,
            filteredRow = {
                TextChatParts.FilteredMentionLazyRow(
                    filteredChatList = filteredChatList,
                    clickedAutoCompleteText = { username ->
                        clickedAutoCompleteText(
                            username
                        )
                    }
                )
            },
            showModStatus = {
                TextChatParts.ShowModStatus(
                    modStatus =modStatus,
                    showOuterBottomModalState={showOuterBottomModalState()}
                )
            },
            stylizedTextField ={boxModifier ->
                TextChatParts.StylizedTextField(
                    modifier = boxModifier,
                    textFieldValue = textFieldValue,
                    newFilterMethod = {newTextValue ->newFilterMethod(newTextValue)}

                )
            },
            showIconBasedOnTextLength ={
                TextChatParts.ShowIconBasedOnTextLength(
                    textFieldValue =textFieldValue,
                    chat = {item -> sendMessageToWebSocket(item)},
                    showModal ={showModal()}
                )
            },
            forwardSlashCommands = {
                TextChatParts.ForwardSlash(
                    commandList = forwardSlashCommands
                )
            }
        )
    }





    /**
     * TextChatBuilders is the most generic section of all the [TextChat] composables. It is meant to
     * act as a layout guide for how all [TextChat] implementations should look
     * */
    private object TextChatBuilders{

        /**
         * The basic layout of the text box users use to enter and share chat to the server. An example of what a typical composable
         * looks like can be seen, [HERE](https://theplebdev.github.io/Modderz-style-guide/#enterchat-)
         *
         * @param modifier this modifier is used to position the chat box inside of the [MainChat] builders. The modifier for
         * this builder is .align(Alignment.BottomCenter).fillMaxWidth()
         * @param filteredRow a Composable meant to represent the list of filtered usernames. [TextChatParts.FilteredMentionLazyRow] is used to
         * implement this functionality
         * @param showModStatus a Composable meant to determine if there should be a Mod icon shown to the user.
         * [TextChatParts.ShowModStatus] is the implementation of this functionality
         * @param stylizedTextField a composable that acts a simple text field that users can enter their chat messages to.
         * [TextChatParts.StylizedTextField] is the implementation of this functionality
         * @param showIconBasedOnTextLength a composable that determines which Icon to show depending on length of the characters inside of the [stylizedTextField]
         * [TextChatParts.ShowIconBasedOnTextLength] is the implementation of this functionality
         *
         * */
        @Composable
        fun EnterChat(
            modifier: Modifier,
            filteredRow:@Composable () -> Unit,
            forwardSlashCommands:@Composable () -> Unit,
            showModStatus:@Composable () -> Unit,
            stylizedTextField:@Composable (modifier:Modifier) -> Unit,
            showIconBasedOnTextLength:@Composable () -> Unit,
        ) {


            Column(modifier = modifier.background(MaterialTheme.colorScheme.primary)) {
                forwardSlashCommands()
                filteredRow()
                Row(modifier = Modifier.background(MaterialTheme.colorScheme.primary),
                    verticalAlignment = Alignment.CenterVertically){
                    showModStatus()
                    stylizedTextField(modifier = Modifier.weight(2f))
                    showIconBasedOnTextLength()
                }
            }
        }
    }



    private object TextChatParts{
        @Composable
        fun ForwardSlash(commandList:List<ForwardSlashCommands>){
            val primaryColor = MaterialTheme.colorScheme.primary
            val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

            if(commandList.isNotEmpty()){
                LazyColumn(modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.DarkGray)
                    .padding(10.dp)){
                    items(commandList){command ->
                        Text(command.title, fontSize = 18.sp, color = onPrimaryColor, fontWeight = FontWeight.ExtraBold)
                        Text(command.subtitle, fontSize = 16.sp, color = onPrimaryColor, modifier = Modifier.padding(start=5.dp,bottom=5.dp))
                    }
                }
            }




        }


        /**
         * This composable represents a clickable username shown to the user. When the [username] is clicked it will
         * automatically be added to the users text that they are typing
         *
         * @param clickedAutoCompleteText a function that will do the auto completing when this text is clicked
         * @param username the String shown to the user. This represents a username of a user in chat.
         * */
        @Composable
        fun ClickedAutoText(
            clickedAutoCompleteText: (String) -> Unit,
            username:String
        ){
            Box(
                Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 5.dp)){
                Text(
                    text =username,
                    Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.DarkGray)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .clickable {
                            clickedAutoCompleteText(username)
                        }
                    ,
                    color = Color.White,

                )
            }


        }

        /**
         * A [LazyRow] used to represent all the usernames of every chatter in chat. This will be triggered to be shown
         * when a user enters the ***@*** character. This composable is also made up of the [TextChatParts.ClickedAutoText]
         * composable
         *
         * @param filteredChatList a list of Strings meant to represent all of the users in chat
         * @param clickedAutoCompleteText a function passed to [TextChatParts.ClickedAutoText] that enables autocomplete on click
         * */
        @Composable
        fun FilteredMentionLazyRow(
            filteredChatList: List<String>,
            clickedAutoCompleteText: (String) -> Unit,
        ){
            LazyRow(modifier = Modifier.padding(vertical = 10.dp)) {
                items(filteredChatList) {

                        TextChatParts.ClickedAutoText(
                            clickedAutoCompleteText ={
                                    username ->clickedAutoCompleteText(username)},
                            username =it
                        )


                }
            }
        }



        /**
         * A Composable that will show an Icon based on the length of [textFieldValue]. If the length is greater than 0 then
         * the [ArrowForward] will be shown. If the length is less then or equal to 0 then the [MoreVert] will be shown
         *
         * @param textFieldValue the values used to determine which icon should be shown
         * @param chat a function that is used to send a message to the websocket and allows the user to communicate with other users
         * @param showModal a function that is used to open the side chat and show the chat settings
         * */
        @Composable
        fun ShowIconBasedOnTextLength(
            textFieldValue: MutableState<TextFieldValue>,
            chat: (String) -> Unit,
            showModal: () -> Unit
        ){
            if (textFieldValue.value.text.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = stringResource(R.string.send_chat),
                    modifier = Modifier
                        .size(35.dp)
                        .clickable { chat(textFieldValue.value.text) }
                        .padding(start = 5.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.more_vert_icon_description),
                    modifier = Modifier
                        .size(35.dp)
                        .clickable { showModal() }
                        .padding(start = 5.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }



        /**
         * A styled [TextField] to allow the user to enter chat messages
         *
         * @param modifier determines how much of the screen it takes up. should be given a value of .weight(2f)
         * @param textFieldValue The value that the user it currently typing in
         * @param newFilterMethod This method will trigger where to show the [TextChatParts.FilteredMentionLazyRow] or not
         *
         * */
        @Composable
        fun StylizedTextField(
            modifier: Modifier,
            textFieldValue: MutableState<TextFieldValue>,
            newFilterMethod:(TextFieldValue) ->Unit,
        ){
            val customTextSelectionColors = TextSelectionColors(
                handleColor = MaterialTheme.colorScheme.secondary,
                backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
            )
            CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                TextField(

                    modifier = modifier,
                    singleLine = false,
                    value = textFieldValue.value,
                    shape = RoundedCornerShape(8.dp),
                    onValueChange = { newText ->
                        newFilterMethod(newText)
                        textFieldValue.value = TextFieldValue(
                            text = newText.text,
                            selection = newText.selection
                        )

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
                        Text(stringResource(R.string.send_a_message), color = Color.White)
                    }
                )
            }

        }

        /**
         * A composable meant to show a moderator Icon based on the status of [modStatus]
         *
         * @param modStatus a boolean meant to determine if the user is a moderator or not.
         * @param showOuterBottomModalState a function used to show the a bottom layout sheet
         * */
        @Composable
        fun ShowModStatus(
            modStatus: Boolean?,
            showOuterBottomModalState: () ->Unit
        ){
            val scope = rememberCoroutineScope()
            if (modStatus != null && modStatus == true) {
                AsyncImage(
                    modifier = Modifier.clickable {
                        showOuterBottomModalState()
                    },
                    model = "https://static-cdn.jtvnw.net/badges/v1/3267646d-33f0-4b17-b3df-f923a41db1d0/3",
                    contentDescription = stringResource(R.string.moderator_badge_icon_description)
                )
            }

        }
    }// end of TextChatParts

}// end of Text Chat