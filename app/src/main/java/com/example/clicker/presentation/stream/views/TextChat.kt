package com.example.clicker.presentation.stream.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.clicker.R
import kotlinx.coroutines.launch

/**
 * TextChat contains all the composables responsible for creating the text chat experience for the users
 *
 * */
object TextChat{
    // Anything at this level can be used anywhere in the code base
    // how to determine where the composable should go
    //1) if it uses the slot layout then it is a builder
    //2) if it does not use the slot layout and is only used inside of this declaration then it is a part
    //3) parts are allowed to be made up of multiple parts
    //4) if it does not contain a slot layout and is used elsewhere in the code base, then it goes at the top level and is considered an implementation
    @Composable
    fun EnterChat(
        modifier: Modifier,
        filteredChatList: List<String>,
        textFieldValue: MutableState<TextFieldValue>,
        clickedAutoCompleteText: (String, String) -> String,
        modStatus: Boolean?,
        filterMethod: (String, String) -> Unit,
        sendMessageToWebSocket: (String) -> Unit,
        showModal: () -> Unit
    ){
        ChatBuilders.EnterChat(
            modifier = modifier,
            filteredRow = {
                TextChatParts.FilteredMentionLazyRow(
                    filteredChatList = filteredChatList,
                    textFieldValue = textFieldValue,
                    clickedAutoCompleteText = { addedValue, currentValue ->
                        clickedAutoCompleteText(
                            addedValue,
                            currentValue
                        )
                    }
                )
            },
            enterChatBox = {
                TextChat.TextFieldChat(
                    textFieldValue = textFieldValue,
                    modStatus = modStatus,
                    filterMethod = { username, text -> filterMethod(username, text) },
                    chat = { chatMessage -> sendMessageToWebSocket(chatMessage) },
                    showModal = { showModal() }
                )
            }
        )
    }




    @Composable
    fun TextFieldChat(
        textFieldValue: MutableState<TextFieldValue>,
        modStatus: Boolean?,
        filterMethod: (String, String) -> Unit,
        chat: (String) -> Unit,
        showModal: () -> Unit
    ) {

        Row(
            modifier = Modifier.background(MaterialTheme.colorScheme.primary),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //This should be its own composable too
            if (modStatus != null && modStatus == true) {
                AsyncImage(
                    model = "https://static-cdn.jtvnw.net/badges/v1/3267646d-33f0-4b17-b3df-f923a41db1d0/3",
                    contentDescription = stringResource(R.string.moderator_badge_icon_description)
                )
            }
            TextChatParts.StylizedTextField(
                modifier = Modifier.weight(2f),
                textFieldValue = textFieldValue,
                filterMethod ={username, newText -> filterMethod(username,newText)}
            )

            TextChatParts.ShowIconBasedOnTextLength(
                textFieldValue =textFieldValue,
                chat = {item -> chat(item)},
                showModal ={showModal()}
            )
        }
    }

    // the modifier should be Modifier.weight(2f)



    private object ChatBuilders{
        @Composable
        fun EnterChat(
            modifier: Modifier,
            filteredRow:@Composable () -> Unit,
            enterChatBox:@Composable () -> Unit,
        ) {

            Column(modifier = modifier.background(MaterialTheme.colorScheme.primary)) {
                filteredRow()
                enterChatBox()
            }
        }
    }

    private object TextChatParts{
        /**
         * This composable represents a stylized text item that has the ability to auto complete the [textFieldValue]
         * when this text is clicked
         *
         * @param textFieldValue the value that will get auto completed when this text is clicked
         * @param clickedAutoCompleteText a function that will do the auto completing when this text is clicked
         * @param text the String shown to the user. This String will represent a user in chat and is clickable
         * */
        @Composable
        fun ClickedAutoText(
            textFieldValue: MutableState<TextFieldValue>,
            clickedAutoCompleteText: (String, String) -> String,
            text:String
        ){
            Text(
                text,
                modifier = Modifier
                    .padding(5.dp)
                    .clickable {
                        textFieldValue.value = TextFieldValue(
                            text = clickedAutoCompleteText(textFieldValue.value.text, text),
                            selection = TextRange(
                                (textFieldValue.value.text + "$text ").length
                            )
                        )
                    },
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        /**
         * A [LazyRow] used to represent all the usernames of every chatter in chat. This will be triggered to be shown
         * when a user enters the ***@*** character. This composable is also made up of the [TextChatParts.ClickedAutoText]
         * composable
         *
         * @param filteredChatList a list of Strings meant to represent all of the users in chat
         * @param textFieldValue passed to [TextChatParts.ClickedAutoText] and is the current text typed by the user
         * @param clickedAutoCompleteText a function passed to [TextChatParts.ClickedAutoText] that enables autocomplete on click
         * */
        @Composable
        fun FilteredMentionLazyRow(
            filteredChatList: List<String>,
            textFieldValue: MutableState<TextFieldValue>,
            clickedAutoCompleteText: (String, String) -> String,
        ){
            LazyRow(modifier = Modifier.padding(vertical = 10.dp)) {
                items(filteredChatList) {
                    TextChatParts.ClickedAutoText(
                        textFieldValue =textFieldValue,
                        clickedAutoCompleteText ={oldValue, currentValue ->clickedAutoCompleteText(oldValue, currentValue)},
                        text =it
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

        // need to document this
        @Composable
        fun StylizedTextField(
            modifier: Modifier,
            textFieldValue: MutableState<TextFieldValue>,
            filterMethod: (String, String) -> Unit,
        ){
            val customTextSelectionColors = TextSelectionColors(
                handleColor = MaterialTheme.colorScheme.secondary,
                backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
            )
            CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                TextField(

                    modifier = modifier,
                    maxLines =1,
                    singleLine = true,
                    value = textFieldValue.value,
                    shape = RoundedCornerShape(8.dp),
                    onValueChange = { newText ->
                        filterMethod("username", newText.text)
                        textFieldValue.value = TextFieldValue(
                            text = newText.text,
                            selection = newText.selection
                        )
                        // text = newText
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
    }// end of TextChatParts

}// end of Text Chat