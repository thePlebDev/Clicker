package com.example.clicker

import com.example.clicker.network.websockets.ParsingEngine
import com.example.clicker.util.Response
import org.junit.Assert
import org.junit.Test

class ParsingEngineTest {
    val underTest:ParsingEngine = ParsingEngine()

    @Test
    fun testing_clear_chat_parsing_clear_chat_command(){
        /* Given */
        val CLEAR_CHAT_TEXT = "@room-id=520593641;tmi-sent-ts=1696019043159 :tmi.twitch.tv CLEARCHAT #theplebdev"
        val STREAMER_NAME = "theplebdev"
        val EXPECTED_VALUE = STREAMER_NAME


        /* When */
        val result = underTest.clearChatTesting(text = CLEAR_CHAT_TEXT, streamerName = STREAMER_NAME)



        /* Then */
        Assert.assertEquals(EXPECTED_VALUE, result )

    }

    @Test
    fun testing_clear_chat_parsing_ban_user_command(){
        /* Given */
        val EXPECTED_USERNAME = "meanermeeny"
        val BAN_USER_TEXT = "@room-id=520593641;target-user-id=949335660;tmi-sent-ts=1696019132494 :tmi.twitch.tv CLEARCHAT #theplebdev :$EXPECTED_USERNAME"
        val STREAMER_NAME = "theplebdev"
        val EXPECTED_VALUE = EXPECTED_USERNAME


        /* When */
        val result = underTest.clearChatTesting(text = BAN_USER_TEXT, streamerName = STREAMER_NAME)



        /* Then */
        Assert.assertEquals(EXPECTED_VALUE, result )

    }
}