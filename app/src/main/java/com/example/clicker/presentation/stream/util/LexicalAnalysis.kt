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
 * @property USERNAME triggered by a **`@username`** being typed
 * @property TEXT represents normal text being typed
 * @property UNRECOGNIZED triggered by a **`/fdjsafds`** command or any other command that is not set up
 *
 * @property MONITOR triggered by a **`/monitor`** command
 *
 * @property UNMONITOR triggered by a **`/unmonitor`** command
 *
 * */
enum class TokenType {
    BAN, UNBAN,USERNAME,TEXT, UNRECOGNIZED,MONITOR,UNMONITOR
}


data class Token(
    val tokenType: TokenType,
    val lexeme:String
)
/**THIS IS TERRIBLE AND CONFUSING WITH THE USERNAME/REASON/COMMAND/MESSAGE AND IT NEEDS TO BE REFACTORED**/
 sealed class TextCommands(val username: String="",val reason: String =""){
     class Ban(username:String,reason:String):TextCommands(username,reason)
     class UnBan(username:String):TextCommands(username)
     class UNRECOGNIZEDCOMMAND(command:String):TextCommands(command)
     class NORMALMESSAGE(message:String) : TextCommands(message)

     class MONITOR(username: String):TextCommands(username)
     class UnMONITOR(username: String):TextCommands(username)
     object NOUSERNAME : TextCommands()
     object INITIALVALUE : TextCommands()


 }

class Scanner (private val source:String) {
    private val map = hashMapOf<String, Token>()

    init {
        map["/ban"] = Token(TokenType.BAN,"/ban")
        map["/unban"] = Token(TokenType.UNBAN,"/unban")
        map["@username"] = Token(TokenType.USERNAME,"/username")

        map["/monitor"] = Token(TokenType.MONITOR,"/monitor")
        map["/unmonitor"] = Token(TokenType.UNMONITOR,"/unmonitor")
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
    //we have a public hot flow that gets TokenCommandTypes emitted to it and then base actions on that
    // Backing property to avoid state updates from other classes
//    private val _tokenCommand = MutableStateFlow<TextCommands>(TextCommands.INITIALVALUE)
//    // The UI collects from this StateFlow to get its state updates
//    val tokenCommand: StateFlow<TextCommands> = _tokenCommand
     fun checkForSlashCommands(tokenList: List<Token>):TextCommands{
        //create findusername() function to check for null
         Log.d("checkForSlashCommands","tokenList size -->${tokenList.size}")
        val listOfTextCommands = mutableListOf<TextCommands>()

        when{
            hasUnrecognizedTokenType(tokenList)->{
                val unrecognized =tokenList.first { it.tokenType == TokenType.UNRECOGNIZED }.lexeme
              //  _tokenCommand.tryEmit(TextCommands.UNRECOGNIZEDCOMMAND(unrecognized))
                return TextCommands.UNRECOGNIZEDCOMMAND(unrecognized)

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
                    return TextCommands.NOUSERNAME
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
                    return TextCommands.NOUSERNAME
                }
            }
            hasMonitorTokenType(tokenList) ->{
                val username =tokenList
                    .find{ it.tokenType == TokenType.USERNAME }?.lexeme
                if(username != null){
                    //todo: send unBan command

                   // _tokenCommand.tryEmit(TextCommands.MONITOR(username=username.replace("@", "")))
                    return TextCommands.MONITOR(username=username.replace("@", ""))
                }
                else{
                    //todo: tell user that there is no username
                 //   _tokenCommand.tryEmit(TextCommands.NOUSERNAME)
                    return TextCommands.NOUSERNAME
                }
            }
            hasUnMonitorTokenType(tokenList) ->{
                val username =tokenList
                    .find{ it.tokenType == TokenType.USERNAME }?.lexeme
                if(username != null){
                    //todo: send unBan command


                    return TextCommands.UnMONITOR(username=username.replace("@", ""))
                }
                else{
                    //todo: tell user that there is no username

                    return TextCommands.NOUSERNAME
                }
            }

            else->{
                val message = tokenList.map { it.lexeme }.joinToString(separator = " ")
                return TextCommands.NORMALMESSAGE(message)

            }
        }

    }

    private fun hasMonitorTokenType(tokens: List<Token>): Boolean {
        return tokens.any { it.tokenType == TokenType.MONITOR }
    }
    private fun hasUnMonitorTokenType(tokens: List<Token>): Boolean {
        return tokens.any { it.tokenType == TokenType.UNMONITOR }
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
}