package com.example.clicker.utility

import com.example.clicker.network.clients.TopGame
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.presentation.selfStreaming.util.UrlParser
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

    @Test
    fun url_parser() {
        // occurs when an attempt is made to convert a string with an incorrect format to a numeric value
        //url of the stream like: protocol://ip:port/application/streamName
        val EXPECTED_HOST ="sfo.contribute.live-video.net"
        val url ="rtmp://sfo.contribute.live-video.net/app/live_user_123456789?bandwidthtest=true"
        val validSchemes = arrayOf("rtmp", "rtmps", "rtmpt", "rtmpts")
        val parser = UrlParser.parse(url, validSchemes)



        Assert.assertEquals(EXPECTED_HOST, parser.host)
    }




}



