package com.example.clicker.network.repository.util

import androidx.compose.foundation.text.InlineTextContent
import com.example.clicker.network.repository.EmoteNameUrl
import org.junit.Assert
import org.junit.Test

class EmoteParsingTest {
    val underTest = EmoteParsing()


    @Test
    fun `emote parsing adds emote value to map`(){
        /**GIVEN*/
        val nameUrl = EmoteNameUrl("testing","testingURL")
        val innerInlineContentMap: MutableMap<String, InlineTextContent> = mutableMapOf()

        /**WHEN*/
        underTest.createMapValueForComposeChat(nameUrl,innerInlineContentMap)

        /**THEN*/

        Assert.assertEquals(1, innerInlineContentMap.size)
    }
}