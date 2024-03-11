package com.example.clicker.presentation.stream.util


import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class TextParsingTest {
    val underTest:TextParsing =TextParsing()

    @Test
    fun clickSlashCommandTextAutoChange_success() = runTest {
        /**GIVEN*/
        val clickedOnCommand ="/ban"
        val expectedResult = "/ban "


        /**WHEN*/
        underTest.parsingMethod( // represents the user typing in the text field
            textFieldValue = TextFieldValue("/"),
            allChatters = listOf("")
        )
        underTest.clickSlashCommandTextAutoChange(clickedOnCommand)

        /**THEN*/
        val actualValue = underTest.textFieldValue.value.text
        val expectedValue = expectedResult



        Assert.assertEquals(expectedValue, actualValue)

    }
    @Test
    fun clickUsernameAutoTextChange_success() = runTest {
        /**GIVEN*/
        val clickedOnUsername ="Bob3324"
        val expectedResult = "Bob3324 "


        /**WHEN*/
        underTest.parsingMethod( // represents the user typing in the text field
            textFieldValue = TextFieldValue("@"),
            allChatters = listOf("Bob3324","MoreFakeTest444","fakeTestName55t")
        )
        underTest.clickUsernameAutoTextChange(clickedOnUsername)

        /**THEN*/
        val actualValue = underTest.textFieldValue.value.text
        val expectedValue = expectedResult



        Assert.assertEquals(expectedValue, actualValue)

    }
}