package com.example.clicker.util

class ChatUtils {
}
enum class TokenType {
    //a @username word
    MENTION,

    // everything that is NOT a @username word
    WORD,

    // just empty space characters
    EmptySpace
}

class Scanner{
    private final var source:String = ""
    private final val tokens = mutableListOf<Token>()
    private var start = 0
    private var current = 0
    private val line = 1

    fun setSource(source:String){
        this.source = source
    }

    private fun scanTokens(){
        val char = advance()
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
            ' ' ->{addToken(TokenType.EmptySpace)}
        }
    }
    private fun addToken(type:TokenType){
        val text = source.subSequence(start,current).toString()
        val token = Token(type,text,0)
        tokens.add(token)

    }
}

data class Token(
    val type: TokenType,
    val lexeme: String,
    val line:Int
    )