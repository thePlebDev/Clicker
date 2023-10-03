package com.example.clicker

import com.example.clicker.network.websockets.ParsingEngine
import com.example.clicker.util.TwitchUserDataObjectMother
import org.junit.Assert
import org.junit.Test

class ParsingEngineTest {
    val underTest:ParsingEngine = ParsingEngine()

    @Test
    fun testing_clear_chat_parsing_clear_chat_command(){
        /* Given */
        val EXPECTED_VALUE = "Chat cleared by moderator"
        TwitchUserDataObjectMother.addUserType(EXPECTED_VALUE).build()
        val CLEAR_CHAT_TEXT = "@room-id=520593641;tmi-sent-ts=1696019043159 :tmi.twitch.tv CLEARCHAT #theplebdev"
        val STREAMER_NAME = "theplebdev"


        /* When */
        val result = underTest.clearChatTesting(text = CLEAR_CHAT_TEXT, streamerName = STREAMER_NAME)


        /* Then */
        Assert.assertEquals(EXPECTED_VALUE, result.userType )

    }

    @Test
    fun testing_clear_chat_parsing_ban_user_command(){
        //todo: THIS NEEDS TO BE REDONE WITH THE PROPER
        /* Given */
        //BELOW IS WRONG, REDO TO TEST THE BAN USER FUNCTIONALITY
        val EXPECTED_BANNEDUSERID = "949335660"
        val EXPECTED_USERNAME = "meanermeeny"
        val expectedTwitchUserData = TwitchUserDataObjectMother
           .addUserType("$EXPECTED_USERNAME banned by moderator")
           .addUserId(EXPECTED_BANNEDUSERID)
           .build()

        val BAN_USER_TEXT = "@room-id=520593641;target-user-id=$EXPECTED_BANNEDUSERID;tmi-sent-ts=1696019132494 :tmi.twitch.tv CLEARCHAT #theplebdev :$EXPECTED_USERNAME"
        val STREAMER_NAME = "theplebdev"


        /* When */
        val result = underTest.clearChatTesting(text = BAN_USER_TEXT, streamerName = STREAMER_NAME)



        /* Then */
        Assert.assertEquals(EXPECTED_BANNEDUSERID, result.id )
        Assert.assertEquals(expectedTwitchUserData.userType, result.userType )


    }
}