package com.example.clicker.utility

import com.example.clicker.util.Scanner
import com.example.clicker.util.objectMothers.TwitchUserDataObjectMother
import org.junit.Assert
import org.junit.Test

class ChatUtilTest {


    //UNDER TEST
    private val scannerUnderTest = Scanner()
    @Test
    fun testing_clear_chat_parsing_clear_chat_command() {
        /* Given */
        val sourceStringWithSevenSpaces = "/unban @Tester"
        val sourceStringWithTwoSpaces = "It do "

        /* When */
        scannerUnderTest.setSource(sourceStringWithSevenSpaces)
        val actualAmountOfTokens = scannerUnderTest.getTokenList().size
        for (token in scannerUnderTest.getTokenList()){
            println(token.toString())
        }



        /* Then */
        Assert.assertEquals(7,55)
    }
}