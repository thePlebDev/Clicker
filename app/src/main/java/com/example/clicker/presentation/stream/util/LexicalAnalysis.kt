package com.example.clicker.presentation.stream.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * TokenType represents types that are used inside the lexical analysis of the chat messages
 * */
enum class TokenType {
    BAN, UNBAN, MONITOR, UNMONITOR,USERNAME,TEXT, UNRECOGNIZED
}

/**
 * TokenCommandTypes are based on [TokenType]. TokenCommandTypes will be emitted to let the system know what kind
 * of command it should send to the twitch server
 *
 * */
enum class TokenCommandTypes{
    BAN, UNBAN, MONITOR, UNMONITOR,NOUSERNAME,UNRECOGNIZEDCOMMAND,INITIALVALUE,NORMALMESSAGE
}

data class Token(
    val tokenType: TokenType,
    val lexeme:String
)
 sealed class TextCommands(val username: String="",val reason: String =""){
     class Ban(username:String,reason:String):TextCommands(username,reason)
     class UnBan(username:String):TextCommands(username)
     class MONITOR(username:String):TextCommands(username)
     class UNMONITOR(username:String):TextCommands(username)
     class UNRECOGNIZEDCOMMAND(command:String):TextCommands(command)
     class NORMALMESSAGE(message:String) : TextCommands(message)
     object NOUSERNAME : TextCommands()
     object INITIALVALUE : TextCommands()


 }

class Scanner(private val source: String) {
    private val map = hashMapOf<String, Token>()
    init {
        map["/ban"] = Token(TokenType.BAN,"/ban")
        map["/unban"] = Token(TokenType.UNBAN,"/unban")
        map["/monitor"] = Token(TokenType.MONITOR,"/monitor")
        map["/unmonitor"] = Token(TokenType.UNMONITOR,"/unmonitor")
        map["@username"] = Token(TokenType.USERNAME,"/username")
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
class TokenCommand(){
    //we have a public hot flow that gets TokenCommandTypes emitted to it and then base actions on that
    // Backing property to avoid state updates from other classes
    private val _tokenCommand = MutableStateFlow<TextCommands>(TextCommands.INITIALVALUE)
    // The UI collects from this StateFlow to get its state updates
    val tokenCommand: StateFlow<TextCommands> = _tokenCommand
     fun checkForSlashCommands(tokenList: List<Token>){
        //create findusername() function to check for null
        when{
            hasUnrecognizedTokenType(tokenList)->{
                val unrecognized =tokenList.first { it.tokenType == TokenType.UNRECOGNIZED }.lexeme
                _tokenCommand.tryEmit(TextCommands.UNRECOGNIZEDCOMMAND(unrecognized))
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

                    _tokenCommand.tryEmit(TextCommands.Ban(username=username,reason=reason))
                }
                else{
                    //todo: tell user that there is no username
                    _tokenCommand.tryEmit(TextCommands.NOUSERNAME)
                }

            }
            hasUnbanTokenType(tokenList)->{
                //make unban request
                // get username
                val username =tokenList
                    .find{ it.tokenType == TokenType.USERNAME }?.lexeme
                if(username != null){
                    //todo: send unBan command

                    _tokenCommand.tryEmit(TextCommands.UnBan(username=username))
                }
                else{
                    //todo: tell user that there is no username
                    _tokenCommand.tryEmit(TextCommands.NOUSERNAME)
                }
            }
            hasMonitorTokenType(tokenList)->{
                //make monitor request
                // get username
                val username =tokenList
                    .find{ it.tokenType == TokenType.USERNAME }?.lexeme
                if(username != null){
                    //todo: send monitor command

                    _tokenCommand.tryEmit(TextCommands.MONITOR(username=username))
                }
                else{
                    //todo: tell user that there is no username
                    _tokenCommand.tryEmit(TextCommands.NOUSERNAME)
                }
            }
            hasUnMonitorTokenType(tokenList)->{
                //make unMonitor request
                // get username
                val username =tokenList
                    .find{ it.tokenType == TokenType.USERNAME }?.lexeme
                if(username != null){
                    //todo: send unMonitor command

                    _tokenCommand.tryEmit(TextCommands.UNMONITOR(username=username))
                }
                else{
                    //todo: tell user that there is no username
                    _tokenCommand.tryEmit(TextCommands.NOUSERNAME)
                }
            }

            else->{
                val message = tokenList.map { it.lexeme }.joinToString(separator = " ")
                _tokenCommand.tryEmit(TextCommands.NORMALMESSAGE(message))

            }
        }

    }

    private fun hasBanTokenType(tokens: List<Token>): Boolean {
        return tokens.any { it.tokenType == TokenType.BAN }
    }
    private fun hasUnbanTokenType(tokens: List<Token>): Boolean {
        return tokens.any { it.tokenType == TokenType.UNBAN }
    }
    private fun hasMonitorTokenType(tokens: List<Token>): Boolean {
        return tokens.any { it.tokenType == TokenType.MONITOR }
    }
    private fun hasUnMonitorTokenType(tokens: List<Token>): Boolean {
        return tokens.any { it.tokenType == TokenType.UNMONITOR }
    }
    private fun hasUnrecognizedTokenType(tokens: List<Token>): Boolean {
        return tokens.any { it.tokenType == TokenType.UNRECOGNIZED }
    }
}