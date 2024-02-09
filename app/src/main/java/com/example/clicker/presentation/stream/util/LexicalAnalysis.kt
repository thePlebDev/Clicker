package com.example.clicker.presentation.stream.util

class LexicalAnalysis {
}

enum class TokenType {
    BAN, UNBAN, MONITOR, UNMONITOR,USERNAME,TEXT
}

data class Token(
    val tokenType: TokenType,
    val lexeme:String
)



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
                addToken()
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
    fun notEndNullOrEmptySpace(c:Char):Boolean{
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