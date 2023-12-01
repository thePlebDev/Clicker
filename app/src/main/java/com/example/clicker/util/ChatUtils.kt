package com.example.clicker.util

class ChatUtils {
}
enum class TokenType {
    //a @username word
    MENTION,

    // everything that is NOT a @username word
    WORD,

    // just empty space characters
    EmptySpace,

    //Twitch mod command
    ModCommand
}

class Scanner{
    private  var source:String = ""
    private  val tokens = mutableListOf<Token>()
    private var start = 0
    private var current = 0
    private var mentionStart = 0
    var chatCommands : HashMap<String, TokenType> = HashMap()
    init{
        chatCommands["/ban"] = TokenType.ModCommand
        chatCommands["/mods"] = TokenType.ModCommand
        chatCommands["/block"] = TokenType.ModCommand
        chatCommands["/unban"] = TokenType.ModCommand
        chatCommands["/disconnect"] = TokenType.ModCommand
    }

    fun setSource(source:String){
        this.source = source
        scanTokens()
    }

    private fun scanTokens(){
        while(!isAtEnd()){
            start = current
            //start scanning tokens here
            scanToken()
        }
    }
    private fun isAtEnd():Boolean{
        return this.current >= source.length
    }
    private fun advance():Char{

        return this.source[current++]
    }
    private fun scanToken(){
        val char = advance()
        when(char){
            ' ' ->{addToken(TokenType.EmptySpace,start)}
            '@' ->{
                mentionStart = start
                while (!isAtEnd() && peek() != ' '){
                    advance()
                }
                addToken(TokenType.MENTION,mentionStart)
            }
            '/' ->{
                mentionStart = start
                while (!isAtEnd() && peek() != ' '){
                    advance()
                }
                if(isChatCommands(mentionStart)){
                    addToken(TokenType.ModCommand,mentionStart)
                }

            }
        }
    }
    private fun isChatCommands(startIndex: Int):Boolean{
        val chatCommandKey = source.subSequence(startIndex,current).toString()

        return chatCommands[chatCommandKey] !=null
    }
    private fun addToken(type:TokenType,startIndex: Int){
        val text = source.subSequence(startIndex,current).toString()
        val token = Token(type,text,startIndex,current)
        tokens.add(token)

    }
    fun getTokenList():List<Token>{
        return this.tokens
    }
    private fun peek():Char{

        return source[current]
    }
}

data class Token(
    val type: TokenType,
    val lexeme: String,
    val startIndex:Int,
    val endIndex:Int
    )