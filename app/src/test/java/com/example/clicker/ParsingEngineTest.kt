package com.example.clicker

import com.example.clicker.network.websockets.ParsingEngine
import com.example.clicker.util.objectMothers.LoggedInUserDataObjectMother
import com.example.clicker.util.objectMothers.TwitchUserDataObjectMother
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
    //TODO: TEST USERNAME PARSING WITH NON LATIN BASED ALPHABETS
    // 板橋小結巴 THIS IS A MANDARIN BASED USER NAME

    @Test
    fun ban_user_parsing_with_non_latin_alphabet(){
        /* Given */
        val EXPECTED_BANNEDUSERID = "949335660"
        val EXPECTED_USERNAME = "板橋小結巴"

        val BAN_USER_TEXT = "@room-id=520593641;target-user-id=949335660;tmi-sent-ts=1696019132494 :tmi.twitch.tv CLEARCHAT #theplebdev :板橋小結巴"
        val STREAMER_NAME = "theplebdev"

        /* When */
        val result = underTest.clearChatTesting(text = BAN_USER_TEXT, streamerName = STREAMER_NAME)


        /* Then */
        Assert.assertEquals(EXPECTED_USERNAME, result.displayName )

    }

    @Test
    fun user_state_parsing(){
        /* Given */
        val EXPECTED_COLOR = "#000000"
        val EXPECTED_DISPLAYNAME="bobber-micky54$"
        val EXPECTED_MOD = 0
        val EXPECTED_SUB = 0
        val givenString = "@badge-info=;badges=staff/1;color=$EXPECTED_COLOR;display-name=$EXPECTED_DISPLAYNAME;emote-sets=0,33,50,237,793,2126,3517,4578,5569,9400,10337,12239;mod=$EXPECTED_MOD;subscriber=$EXPECTED_SUB;turbo=1;user-type=staff :tmi.twitch.tv USERSTATE #dallas"


        /* When */
        val result = underTest.userStateParsing(givenString)

        /* Then */
        Assert.assertEquals(EXPECTED_COLOR, result.color )
        Assert.assertEquals(EXPECTED_DISPLAYNAME, result.displayName )
        Assert.assertEquals(false, result.mod )
        Assert.assertEquals(false, result.sub )



    }

    @Test
    fun clear_message_parsing(){
        /* Given */
        val EXPECTED_MSG_ID ="94e6c7ff-bf98-4faa-af5d-7ad633a158a9"
        val parsingString ="@login=foo;room-id=;target-msg-id=$EXPECTED_MSG_ID;tmi-sent-ts=1642720582342 :tmi.twitch.tv CLEARMSG #bar :what a great day"

        /* When */
        val result = underTest.clearMsgParsing(parsingString)


        /* Then */
        Assert.assertEquals(EXPECTED_MSG_ID, result )
    }
}