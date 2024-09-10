package com.example.clicker.presentation.stream.util.domain

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.text.input.TextFieldValue
import com.example.clicker.presentation.stream.util.FilteredChatterListImmutableCollection
import com.example.clicker.presentation.stream.util.ForwardSlashCommandsImmutableCollection

interface TextFieldParsing {

    /**
     * a [TextFieldValue] object that represents what the user is typing inside of the TextField
     * */
    val textFieldValue: MutableState<TextFieldValue>


    /**
     * a [FilteredChatterListImmutableCollection] containing a list of all the filtered chatters. Used by the UI when a
     * user types a ***@***
     *
     * */
    val filteredChatterListImmutable: State<FilteredChatterListImmutableCollection>

    /**
     * - a state object containing a [ForwardSlashCommandsImmutableCollection] object
     * */
    val forwardSlashCommandsState: State<ForwardSlashCommandsImmutableCollection>


    /**
     * parsingMethod is a function that gets called each time the user types in the chat box. Also, its main job is to
     * monitor for if the user types in ***@*** or slash command
     *
     * @param textFieldValue a [TextFieldValue] that represents what the user is currently typing in chat
     * @param allChatters a List of Strings representing all of the individual chatters in this chat
     * */
    fun parsingMethod(
        textFieldValue: TextFieldValue,
        allChatters: List<String>
    )


    /**
     * clickUsernameAutoTextChange() is a function meant to be run when a user clicks on a username after typing the ***@*** symbol.
     * It will create a new text with the clicked username and replace the old text that the user was typing
     *
     * @param username a string meant to represent the username that the user just clicked on
     * */
    fun clickUsernameAutoTextChange(
        username:String,
    )


    /**
     * updateTextField is a function used to update [textFieldValue] with a text that represents the Emote a user just clicked
     *
     * @param emoteText a String representing the emote a user just clicked on
     * */
    fun updateTextFieldWithEmote(
        emoteText: String
    )

    /**
     * clickUsernameAutoTextChange() is a function meant to be run when a user clicks on a slash command after typing the `\` symbol.
     * It will create a new text with the clicked slash command and replace the old text that the user was typing
     *
     * @param command a string meant to represent the slash command that the user clicked on
     * */
    fun clickSlashCommandTextAutoChange(
    command:String,
    )

}