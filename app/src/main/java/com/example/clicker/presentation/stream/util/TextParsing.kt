package com.example.clicker.presentation.stream.util

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getTextBeforeSelection

import javax.inject.Inject

data class ForwardSlashCommands(
    val title:String,
    val subtitle:String,
    val clickedValue:String,
)

class TextParsing @Inject constructor() {
    private val listOfCommands = listOf(
        ForwardSlashCommands(title="/ban [username] [reason] ", subtitle = "Permanently ban a user from chat",clickedValue="ban"),
        ForwardSlashCommands(title="/unban [username] ", subtitle = "Remove a timeout or a permanent ban on a user",clickedValue="unban"),
        ForwardSlashCommands(title="/monitor [username] ", subtitle = "Start monitoring a user's messages (only visible to you)",clickedValue="monitor"),
        ForwardSlashCommands(title="/unmonitor [username] ", subtitle = "Stop monitoring a user's messages",clickedValue="unmonitor")
    )

    //
    val textFieldValue = mutableStateOf(
        TextFieldValue(
            text = "",
            selection = TextRange(0)
        )
    )
    var filteredChatList = mutableStateListOf<String>()
    private val _forwardSlashCommands = mutableStateListOf<ForwardSlashCommands>()
    val forwardSlashCommands = _forwardSlashCommands

    var parsingIndex:Int =0
    var startParsing:Boolean = false

    var slashCommandState:Boolean = false
    var slashCommandIndex:Int =0

    /********BELOW ARE ALL THE methods*******/

    /**
     * clickUsernameAutoTextChange() is a function meant to be run when a user clicks on a username after typing the ***@*** symbol.
     * It will create a new text with the clicked username and replace the old text that the user was typing
     *
     * @param username a string meant to represent the username that the user just clicked on
     * @param parsingIndex a integer that represents where the parsing should begin
     * @param clearChat a function meant to represent any extra clean up that needs to take place.
     * */
    fun clickUsernameAutoTextChange(
        username:String,

    ){
        val currentCharacterIndex = textFieldValue.value.selection.end

        val replacedString =textFieldValue.value.text.replaceRange(parsingIndex,currentCharacterIndex,"$username ")
        textFieldValue.value = textFieldValue.value.copy(
            text = replacedString,
            selection = TextRange(replacedString.length)
        )
        filteredChatList.clear()

    }

    /**
     * clickUsernameAutoTextChange() is a function meant to be run when a user clicks on a slash command after typing the `\` symbol.
     * It will create a new text with the clicked slash command and replace the old text that the user was typing
     *
     * @param command a string meant to represent the slash command that the user clicked on
     * @param slashCommandIndex a integer that represents where the parsing should begin for the slash command
     * @param cleanUp a function meant to represent any extra clean up that needs to take place.
     * */
    fun clickSlashCommandTextAutoChange(
        command:String,
    ){
        val currentCharacterIndex = textFieldValue.value.selection.end

        val replacedString =textFieldValue.value.text.replaceRange(slashCommandIndex,currentCharacterIndex,"$command ")
        textFieldValue.value = textFieldValue.value.copy(
            text = replacedString,
            selection = TextRange(replacedString.length)
        )
        _forwardSlashCommands.clear()
        filteredChatList.clear()
        slashCommandIndex =0
    }

    fun parsingMethod(
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
                _forwardSlashCommands.addAll(listOfCommands)
            }
            if(currentCharacter.toString() == " "){
                Log.d("newParsingAgainThing","currentCharacter.toString() == blank space")
                _forwardSlashCommands.clear()
                slashCommandState = false
                slashCommandIndex =0
            }
            if(currentCharacter.toString() == "" && slashCommandState){
                Log.d("newParsingAgainThing","end currentCharacter and slashCommandState true")
                _forwardSlashCommands.clear()
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
                _forwardSlashCommands.clear()
                slashCommandState = false
                slashCommandIndex =0
            }


            if(textFieldValue.selection.start < parsingIndex && startParsing){
                Log.d("newParsingAgainThing","textFieldValue.selection.start < parsingIndex && startParsing")
                endParsingNClearFilteredChatList()
                _forwardSlashCommands.clear()
                slashCommandState = false

            }

            if (currentCharacter.toString() == " " && startParsing){
                Log.d("newParsingAgainThing","currentCharacter.toString() == blankspace && startParsing")
                endParsingNClearFilteredChatList()
                _forwardSlashCommands.clear()
                slashCommandState = false

            }
            /**---------set parsing to false should be above this line----------------*/
            if(startParsing){
                parseNFilterChatList(textFieldValue)
            }

            if(currentCharacter.toString() == "@"){
                _forwardSlashCommands.clear()
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
        filteredChatList.clear()
        filteredChatList.addAll(allChatters)
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
        filteredChatList.removeIf{
            !it.contains(usernameRegex)
        }

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
        filteredChatList.clear()
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