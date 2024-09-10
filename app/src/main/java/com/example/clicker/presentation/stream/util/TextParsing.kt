package com.example.clicker.presentation.stream.util

import android.util.Log
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getTextBeforeSelection
import com.example.clicker.presentation.stream.util.domain.TextFieldParsing

import javax.inject.Inject

/**
 * ForwardSlashCommands represents the possible commands a user can type out in the chat.
 *
 * @param title a String representing the title of the slash command
 * @param subtitle a String representing an explanation of what this command does
 * @param clickedValue a String representing the value that will be added to chat when clicked
 * */
data class ForwardSlashCommands(
    val title:String,
    val subtitle:String,
    val clickedValue:String,
)

/**
 * ForwardSlashCommandsImmutableCollection is a Wrapper object created specifically to handle the problem of the Compose compiler
 *  always marking the List as unstable.
 *  - You can read more about this Wrapper solution, [HERE](https://developer.android.com/develop/ui/compose/performance/stability/fix#annotated-classes)
 *
 * - ForwardSlashCommandsImmutableCollection is an immutable wrapper for a list of ForwardSlashCommands object. Where each object
 * represents a slash command typed out by the user
 * */
@Immutable
data class ForwardSlashCommandsImmutableCollection(
    val snacks: List<ForwardSlashCommands>
)

/**
 * FilteredChatListImmutableCollection is a Wrapper object created specifically to handle the problem of the Compose compiler
 *  always marking the List as unstable.
 *  - You can read more about this Wrapper solution, [HERE](https://developer.android.com/develop/ui/compose/performance/stability/fix#annotated-classes)
 *  - FilteredChatListImmutableCollection is an immutable wrapper for a list of filtered Twitch chatter  usernames
 *
 * */
@Immutable
data class FilteredChatterListImmutableCollection(
    val chatList: List<String>
)



class TextParsing @Inject constructor():TextFieldParsing {
    private val listOfCommands = listOf(
        ForwardSlashCommands(title="/ban [username] [reason] ", subtitle = "Permanently ban a user from chat",clickedValue="ban"),
        ForwardSlashCommands(title="/unban [username] ", subtitle = "Remove a timeout or a permanent ban on a user",clickedValue="unban"),
        ForwardSlashCommands(title="/warn [username] [reason]", subtitle = "issue a warning to a user that they must acknowledge before chatting again",clickedValue="warn")

    )


    override val textFieldValue = mutableStateOf(
        TextFieldValue(
            text = "",
            selection = TextRange(0)
        )
    )
    private var filteredChatterList = mutableStateListOf<String>()

    private val _forwardSlashCommands = mutableStateListOf<ForwardSlashCommands>()

    /**
     * private mutable version of [filteredChatterListImmutable]
     * */
    private var _filteredChatterListImmutableCollection by mutableStateOf(
        FilteredChatterListImmutableCollection(filteredChatterList)
    )
    override val filteredChatterListImmutable: State<FilteredChatterListImmutableCollection>
        get() = mutableStateOf(_filteredChatterListImmutableCollection)

    private fun addAllFilteredChattersList(commands:List<String>){
        filteredChatterList.addAll(commands)
        _filteredChatterListImmutableCollection = FilteredChatterListImmutableCollection(filteredChatterList)

    }
    private fun filterChatListUsername(
        usernameRegex: Regex
    ){
        filteredChatterList.removeIf{
            !it.contains(usernameRegex)
        }
        _filteredChatterListImmutableCollection = FilteredChatterListImmutableCollection(filteredChatterList)
    }

    private fun clearFilteredChatterListImmutable() {
        filteredChatterList.clear()
        _filteredChatterListImmutableCollection = FilteredChatterListImmutableCollection(listOf())
    }




    /**
     * private mutable version of [forwardSlashCommandsState]
     * */
    private var _forwardSlashCommandsImmutableCollection by mutableStateOf(
        ForwardSlashCommandsImmutableCollection(_forwardSlashCommands)
    )

    override val forwardSlashCommandsState: State<ForwardSlashCommandsImmutableCollection>
        get() = mutableStateOf(_forwardSlashCommandsImmutableCollection)


    private fun addAllForwardSlashCommand(commands:List<ForwardSlashCommands>){
        _forwardSlashCommands.addAll(commands)
        _forwardSlashCommandsImmutableCollection = ForwardSlashCommandsImmutableCollection(_forwardSlashCommands)

    }

    private fun clearForwardSlashCommand() {
        _forwardSlashCommands.clear()
        _forwardSlashCommandsImmutableCollection = ForwardSlashCommandsImmutableCollection(listOf())
    }


    /**********New forward slash command to make it immutable  above*************/

    var parsingIndex:Int =0
    var startParsing:Boolean = false

    var slashCommandState:Boolean = false
    var slashCommandIndex:Int =0


    override fun clickUsernameAutoTextChange(
        username:String,
    ){
        val currentCharacterIndex = textFieldValue.value.selection.end

        val replacedString =textFieldValue.value.text.replaceRange(parsingIndex,currentCharacterIndex,"$username ")
        textFieldValue.value = textFieldValue.value.copy(
            text = replacedString,
            selection = TextRange(replacedString.length)
        )

        clearFilteredChatterList()

    }

    private fun clearFilteredChatterList(){

        clearFilteredChatterListImmutable()
    }

    override fun updateTextFieldWithEmote(emoteText:String){
        val currentString = textFieldValue.value.text
        val cursorPosition = textFieldValue.value.selection.start

        val newText = StringBuilder(currentString).insert(cursorPosition, emoteText).toString()
        val newCursorPosition = cursorPosition + emoteText.length

        textFieldValue.value = textFieldValue.value.copy(
            text = newText,
            selection = TextRange(newCursorPosition, newCursorPosition)
        )
    }




    override fun clickSlashCommandTextAutoChange(
        command:String,
    ){
        val currentCharacterIndex = textFieldValue.value.selection.end

        val replacedString =textFieldValue.value.text.replaceRange(slashCommandIndex,currentCharacterIndex,"$command ")
        textFieldValue.value = textFieldValue.value.copy(
            text = replacedString,
            selection = TextRange(replacedString.length)
        )
        _forwardSlashCommands.clear()
        clearFilteredChatterList()
        slashCommandIndex =0
    }

    override fun parsingMethod(
        textFieldValue: TextFieldValue,
        allChatters:List<String>
    ){
        try{
            val currentCharacter = textFieldValue.getTextBeforeSelection(1)  // this is the current text

            Log.d("newParsingAgainThing","$currentCharacter")

            if(currentCharacter.toString()=="/"){
                Log.d("newParsingAgain",currentCharacter.toString())
                slashCommandState = true
                slashCommandIndex =textFieldValue.selection.start
                //_forwardSlashCommands.addAll(listOfCommands)
                addAllForwardSlashCommand(listOfCommands)
            }
            if(currentCharacter.toString() == " "){
                Log.d("newParsingAgainThing","currentCharacter.toString() == blank space")
               // _forwardSlashCommands.clear()
                clearForwardSlashCommand()
                slashCommandState = false
                slashCommandIndex =0
            }
            if(currentCharacter.toString() == "" && slashCommandState){
                Log.d("newParsingAgainThing","end currentCharacter and slashCommandState true")
               // _forwardSlashCommands.clear()
                clearForwardSlashCommand()
                slashCommandState = false
                slashCommandIndex =0
            }
            if(slashCommandState){
                //todo here
                parseNFilterCommandList(textFieldValue)
            }

            if(currentCharacter.toString()==""){
                Log.d("newParsingAgainThing","end currentCharacter")
                endParsingNClearFilteredChatList()
               // _forwardSlashCommands.clear()
                clearForwardSlashCommand()
                slashCommandState = false
                slashCommandIndex =0
            }


            if(textFieldValue.selection.start < parsingIndex && startParsing){
                Log.d("newParsingAgainThing","textFieldValue.selection.start < parsingIndex && startParsing")
                endParsingNClearFilteredChatList()
               // _forwardSlashCommands.clear()
                clearForwardSlashCommand()
                slashCommandState = false

            }

            if (currentCharacter.toString() == " " && startParsing){
                Log.d("newParsingAgainThing","currentCharacter.toString() == blankspace && startParsing")
                endParsingNClearFilteredChatList()
               // _forwardSlashCommands.clear()
                clearForwardSlashCommand()
                slashCommandState = false

            }
            /**---------set parsing to false should be above this line----------------*/
            if(startParsing){
                parseNFilterChatList(textFieldValue)
            }

            if(currentCharacter.toString() == "@"){
              //  _forwardSlashCommands.clear()
                clearForwardSlashCommand()
                showFilteredChatListNStartParsing(textFieldValue,allChatters)
            }


        }catch (e:Exception){
            endParsingNClearFilteredChatList()
            negateSlashCommandStateNClearForwardSlashCommands()
        }

    }

    /**
     * showFilteredChatListNStartParsing is a private function called when the current character the user is
     * typing is equal to ***@***. It sets [parsingIndex] to the current character index,[startParsing] to true
     * and adds all the current usernames in chat to [filteredChatList]
     *
     * @param textFieldValue a [TextFieldValue] that represents what the user is currently typing
     * */
    private fun showFilteredChatListNStartParsing(
        textFieldValue: TextFieldValue,
        allChatters:List<String>
    ){
        Log.d("newParsingAgain","-----------BEGIN PARSING----------")
      //  filteredChatList.clear()
        clearFilteredChatterList()
        addAllFilteredChattersList(allChatters)
       // filteredChatList.addAll(allChatters)
        parsingIndex =textFieldValue.selection.start
        startParsing = true
    }
    /**
     * parseNFilterChatList is a private function called when [startParsing] is set to true. Its main
     * goal is to parse out the ***username*** from the [textFieldValue]. Then take that ***username***
     * and filter everything out of [filteredChatList] that does not match the ***username***
     *
     * @param textFieldValue a [TextFieldValue] that represents what the user is currently typing
     * */
    private fun parseNFilterChatList(textFieldValue: TextFieldValue){
        val username =textFieldValue.text.subSequence(parsingIndex,textFieldValue.selection.end)

        val usernameRegex = Regex("^$username",RegexOption.IGNORE_CASE)
        filterChatListUsername(usernameRegex)
//        filteredChatList.removeIf{
//            !it.contains(usernameRegex)
//        }

    }
    /**BELOW IS THE COMMAND LIST!!!!!*/
    private fun parseNFilterCommandList(textFieldValue: TextFieldValue){
        val command ="/"+textFieldValue.text.subSequence(slashCommandIndex,textFieldValue.selection.end)
        Log.d("parseNFilterCommandList","command ----> $command")

        //val commandRegex = Regex("$command",RegexOption.IGNORE_CASE)
        val commandRegex = Regex("^$command",RegexOption.IGNORE_CASE)
        _forwardSlashCommands.removeIf{
            !it.title.contains(commandRegex)
        }

    }
    /**
     * endParsingNClearFilteredChatList is a private function meant to call ***.clear()*** on [filteredChatList] and
     * set [startParsing] to false
     * */
    private fun endParsingNClearFilteredChatList(){
       // filteredChatList.clear()
        clearFilteredChatterList()
        startParsing = false
    }
    /**
     * negateSlashCommandStateNClearForwardSlashCommands is a private function meant to call ***.clear()*** on [forwardSlashCommands] and
     * set [slashCommandState] to false
     * */
    private fun negateSlashCommandStateNClearForwardSlashCommands(){
        slashCommandState = false
    }

}
