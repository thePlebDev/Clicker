package com.example.clicker.presentation.stream.util

import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class TokenCommandTest {
    val underTest = TokenCommand()

    @Test
    fun checkForSlashCommands_hasUnrecognizedTokenType() = runTest {
        /**GIVEN*/
        val unrecognizedCommand = "/dksjld88"
        val tokens =Token(TokenType.UNRECOGNIZED,unrecognizedCommand)
        val returnedTextCommand =TextCommands.UNRECOGNIZEDCOMMAND(unrecognizedCommand)

        val tokenList = listOf<Token>(tokens)

        /**WHEN*/
        underTest.checkForSlashCommands(tokenList)

        /**THEN*/
        val actualValue = underTest.tokenCommand.first()
        val expectedValue =returnedTextCommand

        Assert.assertEquals(expectedValue.username, actualValue.username)

    }

    @Test
    fun checkForSlashCommands_hasBanTokenType() = runTest {
        /**GIVEN*/
        val command = "/ban"
        val username = "Bobberson"
        val reason = "weird"

        val banToken =Token(TokenType.BAN,command)
        val usernameToken =Token(TokenType.USERNAME,username)
        val textToken =Token(TokenType.TEXT,reason)
        val returnedTextCommand =TextCommands.Ban(username, reason)

        val tokenList = listOf(banToken,usernameToken,textToken)

        /**WHEN*/
        underTest.checkForSlashCommands(tokenList)

        /**THEN*/
        val actualValue = underTest.tokenCommand.first()
        val expectedValue =returnedTextCommand

        Assert.assertEquals(expectedValue.username, actualValue.username)

    }

    @Test
    fun checkForSlashCommands_hasUnbanTokenType() = runTest {
        /**GIVEN*/
        val command = "/unban"
        val username = "Bobberson"


        val unbanToken =Token(TokenType.UNBAN,command)
        val usernameToken =Token(TokenType.USERNAME,username)

        val returnedTextCommand =TextCommands.UnBan(username)

        val tokenList = listOf(unbanToken,usernameToken)

        /**WHEN*/
        underTest.checkForSlashCommands(tokenList)

        /**THEN*/
        val actualValue = underTest.tokenCommand.first()
        val expectedValue =returnedTextCommand

        Assert.assertEquals(expectedValue.username, actualValue.username)

    }
}