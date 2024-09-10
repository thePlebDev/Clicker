package com.example.clicker.presentation.stream.util

import android.util.Log
import com.example.clicker.presentation.stream.util.domain.TokenCommandParsing
import javax.inject.Inject

/**
 * TokenCommand is a class means to determine if there are an [TextCommands] the user has sent
 *
 * @property checkForSlashCommands
 * */
class TokenCommand @Inject constructor(): TokenCommandParsing {

    override fun checkForSlashCommands(tokenList: List<Token>):TextCommands{

        when{
            hasUnrecognizedTokenType(tokenList)->{
                val unrecognized =tokenList.first { it.tokenType == TokenType.UNRECOGNIZED }.lexeme

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

    /**
     * function used to determine if any of the [tokens] have a type of TokenType.BAN
     *
     * @param tokens a List of [Token] objects where each token represents a singular word
     * */
    private fun hasBanTokenType(tokens: List<Token>): Boolean {
        return tokens.any { it.tokenType == TokenType.BAN }
    }
    /**
     * function used to determine if any of the [tokens] have a type of TokenType.UNBAN
     *
     * @param tokens a List of [Token] objects where each token represents a singular word
     * */
    private fun hasUnbanTokenType(tokens: List<Token>): Boolean {
        return tokens.any { it.tokenType == TokenType.UNBAN }
    }
    /**
     * function used to determine if any of the [tokens] have a type of TokenType.UNRECOGNIZED
     *
     * @param tokens a List of [Token] objects where each token represents a singular word
     * */
    private fun hasUnrecognizedTokenType(tokens: List<Token>): Boolean {
        return tokens.any { it.tokenType == TokenType.UNRECOGNIZED }
    }
    /**
     * function used to determine if any of the [tokens] have a type of TokenType.WARN
     *
     * @param tokens a List of [Token] objects where each token represents a singular word
     * */
    private fun hasWarnTokenType(tokens: List<Token>): Boolean {
        return tokens.any { it.tokenType == TokenType.WARN }
    }
}