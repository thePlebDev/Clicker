package com.example.clicker.presentation.stream.util

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * TokenType represents types that are used inside the lexical analysis of the chat messages. TokenType has
 * 8 distinct types
 *
 * @property BAN triggered by a **`/ban`** command
 * @property UNBAN triggered by a **`/unban`** command
 * @property WARN triggered by a **`/warn`** command
 * @property USERNAME triggered by a **`@username`** being typed
 * @property TEXT represents normal text being typed
 * @property UNRECOGNIZED triggered by a **`/fdjsafds`** command or any other command that is not set up
 *
 *
 * */
enum class TokenType {
    BAN, UNBAN,USERNAME,TEXT, UNRECOGNIZED, WARN
}


data class Token(
    val tokenType: TokenType,
    val lexeme:String
)
/**
 * TextCommands represents a command that was sent in the chat message box
 *
 * @property Ban represents the /ban command
 * @property Warn represents the /warn command
 * @property UnBan represents the /unban command
 * @property UnrecognizedCommand represents the edge case where a / was typed but no command
 * @property NormalMessage represents a normal messag sent by the user
 * @property Monitor represents the /monitor command
 * @property UnMonitor represents the /unmonitor command
 * @property NoUsername represents the edge case where a command was typed but no username
 * @property InitialValue represents the edge case of the first message typed in chat
 * */
 sealed class TextCommands(val username: String="",val reason: String =""){
     class Ban(username:String,reason:String):TextCommands(username,reason)

    class Warn(username:String,reason:String):TextCommands(username,reason)
     class UnBan(username:String):TextCommands(username)
     class UnrecognizedCommand(command:String):TextCommands(command)
     class NormalMessage(message:String) : TextCommands(message)

     object NoUsername : TextCommands()
     object InitialValue : TextCommands()


 }

/**
 * - Read more about scanning, [HERE](https://craftinginterpreters.com/scanning.html)
 * */
class Scanner (private val source:String) {
    private val map = hashMapOf<String, Token>()


    /**
     * When there is a need for a new slash command, just add it below
     * */
    init {
        map["/ban"] = Token(TokenType.BAN,"/ban")
        map["/unban"] = Token(TokenType.UNBAN,"/unban")
        map["@username"] = Token(TokenType.USERNAME,"/username")
        map["/warn"] = Token(TokenType.WARN,"/warn")

    }
    private val tokens = mutableListOf<Token>()
    val tokenList:List<Token> = tokens
    private var start = 0
    private var current = 0

     private fun scanToken() {
        val c = advance()
        when (c) {
            '/' -> {
                while (isAlphaNumeric(peek())){
                    advance()
                }
                addBanToken()
            }
            '@' ->{
                while (notEndNullOrEmptySpace(peek())){
                    advance()
                }
                addUsernameToken()
            }
            ' '->{}
            else->{
                while (notEndNullOrEmptySpace(peek())){
                    advance()
                }
                addToken()
            }
        }
    }
    private fun notEndNullOrEmptySpace(c:Char):Boolean{
        return !isAtEnd() && c != '\u0000' && c != '\u0020'
    }

    fun scanTokens(){
        while(!isAtEnd()){
            start = current;
            scanToken()
        }
    }

    private fun isAlpha(c: Char): Boolean {
        return (c in 'a'..'z') ||
                (c in 'A'..'Z') ||
                c == '_'
    }
    private fun isDigit(c: Char): Boolean {
        return c in '0'..'9'
    }
    private fun isAlphaNumeric(c: Char): Boolean {
        return isAlpha(c) || isDigit(c)
    }

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

    private fun advance(): Char {
        return source[current++]
    }

    private fun addToken() {
        val text = source.substring(start, current)
        var type: TokenType = map[text]?.tokenType ?: TokenType.TEXT

        tokens.add(Token(type, text))
    }
    private fun addBanToken() {
        val text = source.substring(start, current)
        var type: TokenType = map[text]?.tokenType ?: TokenType.UNRECOGNIZED

        tokens.add(Token(type, text))
    }
    private fun addUsernameToken() {
        val text = source.substring(start, current)
        val type: TokenType = map["@username"]?.tokenType ?: TokenType.TEXT

        tokens.add(Token(type, text))
    }

    //'\u0000' is the null character
    private fun peek(): Char {
        if (isAtEnd()) return '\u0000'
        return source[current]
    }



}
class TokenCommand @Inject constructor(){

    /**
     * checkForSlashCommands is used to return a single [TextCommands] object. Which is used to determine if any commands should
     * be sent from the users messaging prompts. ie, /ban,/unban or /warn
     * */
     fun checkForSlashCommands(tokenList: List<Token>):TextCommands{
        //create findusername() function to check for null
         Log.d("checkForSlashCommands","tokenList size -->${tokenList.size}")
        val listOfTextCommands = mutableListOf<TextCommands>()

        when{
            hasUnrecognizedTokenType(tokenList)->{
                val unrecognized =tokenList.first { it.tokenType == TokenType.UNRECOGNIZED }.lexeme
              //  _tokenCommand.tryEmit(TextCommands.UNRECOGNIZEDCOMMAND(unrecognized))
                return TextCommands.UnrecognizedCommand(unrecognized)

            }
            hasBanTokenType(tokenList)->{
                //make ban request
                // get username and text
                val username =tokenList
                    .find{ it.tokenType == TokenType.USERNAME }?.lexeme
                if(username != null){
                    //todo: send Ban command
                    val reason = tokenList
                        .filter { it.tokenType == TokenType.TEXT }
                        .map{ it.lexeme }
                        .joinToString(separator = " ")

                  //  _tokenCommand.tryEmit(TextCommands.Ban(username=username.replace("@", ""),reason=reason))
                    return TextCommands.Ban(username=username.replace("@", ""),reason=reason)
                }
                else{
                    //todo: tell user that there is no username
                  //  _tokenCommand.tryEmit(TextCommands.NOUSERNAME)
                    return TextCommands.NoUsername
                }

            }
            hasUnbanTokenType(tokenList)->{
                //make unban request
                // get username
                val username =tokenList
                    .find{ it.tokenType == TokenType.USERNAME }?.lexeme
                if(username != null){
                    //todo: send unBan command

                 //   _tokenCommand.tryEmit(TextCommands.UnBan(username=username.replace("@", "")))
                    return TextCommands.UnBan(username=username.replace("@", ""))
                }
                else{
                    //todo: tell user that there is no username
                  //  _tokenCommand.tryEmit(TextCommands.NOUSERNAME)
                    return TextCommands.NoUsername
                }
            }

            hasWarnTokenType(tokenList)->{
                val username =tokenList
                    .find{ it.tokenType == TokenType.USERNAME }?.lexeme
                if(username != null){
                    val reason = tokenList
                        .filter { it.tokenType == TokenType.TEXT }
                        .map{ it.lexeme }
                        .joinToString(separator = " ")

                    return TextCommands.Warn(username=username.replace("@", ""),reason=reason)

                }else{
                    return TextCommands.NoUsername
                }

            }

            else->{
                val message = tokenList.map { it.lexeme }.joinToString(separator = " ")
                return TextCommands.NormalMessage(message)

            }
        }

    }

    private fun hasBanTokenType(tokens: List<Token>): Boolean {
        return tokens.any { it.tokenType == TokenType.BAN }
    }
    private fun hasUnbanTokenType(tokens: List<Token>): Boolean {
        return tokens.any { it.tokenType == TokenType.UNBAN }
    }
    private fun hasUnrecognizedTokenType(tokens: List<Token>): Boolean {
        return tokens.any { it.tokenType == TokenType.UNRECOGNIZED }
    }
    private fun hasWarnTokenType(tokens: List<Token>): Boolean {
        return tokens.any { it.tokenType == TokenType.WARN }
    }
}