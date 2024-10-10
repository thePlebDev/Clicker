package com.example.clicker.utility

import com.example.clicker.util.replaceChannelName
import org.junit.Assert
import org.junit.Test

class UtilityTests {

    @Test
    fun testing_replace_channelName_on_url() {
        // occurs when an attempt is made to convert a string with an incorrect format to a numeric value
        val url = "https://player.twitch.tv/?channel=channelName&controls=false&muted=false&parent=modderz"
        val channelNameReplacement = "Bob443sal"
        val expectedUrl = "https://player.twitch.tv/?channel=$channelNameReplacement&controls=false&muted=false&parent=modderz"
        val actualUrl = replaceChannelName(url,channelNameReplacement)


        Assert.assertEquals(expectedUrl, actualUrl)
    }


}


