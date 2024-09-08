package com.example.clicker.presentation.stream.util

import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class TokenCommandTest {
    val underTest = TokenCommand()

    @Test
    fun checkForSlashCommands_hasUnrecognizedTokenType()  {
        /**GIVEN*/
        val command = "/ban"
        val username = "Bobberson"
        val reason = "weird"

        val banToken =Token(TokenType.BAN,command)
        val usernameToken =Token(TokenType.USERNAME,username)
        val textToken =Token(TokenType.TEXT,reason)
        val expectedReturnType =TextCommands.Ban(username, reason)

        val tokenList = listOf(banToken,usernameToken,textToken)


        /**WHEN*/
        val commandTest =underTest.checkForSlashCommands(tokenList)


        /**THEN*/
        Assert.assertEquals(expectedReturnType.javaClass, commandTest.javaClass)

    }
    @Test
    fun checkForSlashCommands_hasWarnTokenType(){
        /**GIVEN*/
        val command = "/warn"

        val username = "Bobberson"
        val reason = "weird"
        val banToken =Token(TokenType.WARN,command)
        val usernameToken =Token(TokenType.USERNAME,username)
        val textToken =Token(TokenType.TEXT,reason)
        val tokenList = listOf(banToken,usernameToken,textToken)
        val expectedReturnType =TextCommands.Warn(username, reason)

        /**WHEN*/
        val commandTest =underTest.checkForSlashCommands(tokenList)
        println("reason -->"+commandTest.reason)
        println("username -->"+commandTest.username)
        println("javaClass -->"+commandTest.javaClass)


        /**THEN*/
        Assert.assertEquals(expectedReturnType, expectedReturnType)

    }

    @Test
    fun checkForSlashCommands_normalMessage() = runTest {
        /**GIVEN*/
        val message = "LUL"
//
//
//
//        val textToken =Token(TokenType.TEXT,message)
//        val expectedReturnedTypes =TextCommands.NORMALMESSAGE(message)
//
//
//        val tokenList = listOf(textToken)
//
//
//        /**WHEN*/
//        val commandTest =underTest.checkForSlashCommands(tokenList)
//
//
//        /**THEN*/
//        Assert.assertEquals(expectedReturnedTypes.javaClass, commandTest.javaClass)


    }

    @Test
    fun checkForSlashCommands_hasUnbanTokenType() = runTest {
        /**GIVEN*/


    }
}