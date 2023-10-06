package com.example.clicker

import com.example.clicker.network.websockets.MessageType
import com.example.clicker.network.websockets.ParsingEngine
import com.example.clicker.util.objectMothers.LoggedInUserDataObjectMother
import com.example.clicker.util.objectMothers.TwitchUserDataObjectMother
import org.junit.Assert
import org.junit.Test

class ParsingEngineTest {
    val underTest:ParsingEngine = ParsingEngine()
    val EXPECTED_NON_LATIN_BASED_USERNAME = "不橋小結"

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

    @Test
    fun user_notice_parsing_no_personal_message(){
        /* Given */
        val streamerName ="hasanabi"
        val EXPECTED_USERNAME = "Bob34t4"
        val EXPECTED_SYSTEM_MESSAGE ="CoalTheTroll subscribed with Prime. They've subscribed for 25 months!"
        val EXPECTED_MESSAGE_ID = "submysterygift"
        val parsingString ="@badge-info=subscriber/25;badges=subscriber/24,premium/1;color=#008000;display-name=$EXPECTED_USERNAME;emotes=;flags=;id=3ceab6bd-de3f-4d05-8038-5cebdb2af1c7;login=coalthetroll;mod=0;msg-id=$EXPECTED_MESSAGE_ID;msg-param-cumulative-months=25;msg-param-months=0;msg-param-multimonth-duration=0;msg-param-multimonth-tenure=0;msg-param-should-share-streak=0;msg-param-sub-plan-name=Woke\\sBeys\\s(hasanpiker):\\s\$4.99\\sSub;msg-param-sub-plan=Prime;msg-param-was-gifted=false;room-id=207813352;subscriber=1;system-msg=CoalTheTroll\\ssubscribed\\swith\\sPrime.\\sThey've\\ssubscribed\\sfor\\s25\\smonths!;tmi-sent-ts=1696621315536;user-id=73777153;user-type=;vip=0 :tmi.twitch.tv USERNOTICE #hasanabi"

        /* Given */
        val result = underTest.userNoticeParsing(parsingString,streamerName)

        /* Then */
        Assert.assertEquals(MessageType.MYSTERYGIFTSUB, result.messageType )
        Assert.assertEquals(EXPECTED_USERNAME, result.displayName )
        Assert.assertEquals(EXPECTED_SYSTEM_MESSAGE, result.userType )


    }

    @Test
    fun user_notice_parsing_with_personal_message(){
        /* Given */
        val streamerName ="hasanabi"
        val EXPECTED_MESSAGE ="yo <3"
        val EXPECTED_USERNAME = "Bob34t4"
        val EXPECTED_SYSTEM_MESSAGE ="CoalTheTroll subscribed with Prime. They've subscribed for 25 months!"
        val EXPECTED_MESSAGE_ID = "submysterygift"
        val parsingString ="@badge-info=subscriber/25;badges=subscriber/24,premium/1;color=#008000;display-name=$EXPECTED_USERNAME;emotes=;flags=;id=3ceab6bd-de3f-4d05-8038-5cebdb2af1c7;login=coalthetroll;mod=0;msg-id=$EXPECTED_MESSAGE_ID;msg-param-cumulative-months=25;msg-param-months=0;msg-param-multimonth-duration=0;msg-param-multimonth-tenure=0;msg-param-should-share-streak=0;msg-param-sub-plan-name=Woke\\sBeys\\s(hasanpiker):\\s\$4.99\\sSub;msg-param-sub-plan=Prime;msg-param-was-gifted=false;room-id=207813352;subscriber=1;system-msg=CoalTheTroll\\ssubscribed\\swith\\sPrime.\\sThey've\\ssubscribed\\sfor\\s25\\smonths!;tmi-sent-ts=1696621315536;user-id=73777153;user-type=;vip=0 :tmi.twitch.tv USERNOTICE #$streamerName :$EXPECTED_MESSAGE"

        /* Given */
        val result = underTest.userNoticeParsing(parsingString,streamerName)

        /* Then */
        Assert.assertEquals(MessageType.MYSTERYGIFTSUB, result.messageType )
        Assert.assertEquals(EXPECTED_USERNAME, result.displayName )
        Assert.assertEquals(EXPECTED_MESSAGE, result.userType )
        Assert.assertEquals(EXPECTED_SYSTEM_MESSAGE, result.systemMessage )



    }
}