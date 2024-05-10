package com.example.clicker

import com.example.clicker.network.websockets.MessageScanner
import com.example.clicker.network.websockets.MessageType
import com.example.clicker.network.websockets.ParsingEngine
import com.example.clicker.network.websockets.findEmoteNames
import com.example.clicker.presentation.stream.util.Token
import com.example.clicker.presentation.stream.util.TokenType
import com.example.clicker.util.objectMothers.TwitchUserDataObjectMother
import org.junit.Assert
import org.junit.Test

class ParsingEngineTest {
    val underTest: ParsingEngine = ParsingEngine()
    val EXPECTED_NON_LATIN_BASED_USERNAME = "不橋小結"

    @Test
    fun testing_clear_chat_parsing_clear_chat_command() {
        /* Given */
        val EXPECTED_VALUE = "Chat cleared by moderator"
        TwitchUserDataObjectMother.addUserType(EXPECTED_VALUE).build()
        val CLEAR_CHAT_TEXT = "@room-id=520593641;tmi-sent-ts=1696019043159 :tmi.twitch.tv CLEARCHAT #theplebdev"
        val STREAMER_NAME = "theplebdev"

        /* When */
        val result = underTest.clearChatTesting(
            text = CLEAR_CHAT_TEXT,
            streamerName = STREAMER_NAME
        )

        /* Then */
        Assert.assertEquals(EXPECTED_VALUE, result.userType)
    }

    @Test
    fun testing_clear_chat_parsing_ban_user_command() {
        // todo: THIS NEEDS TO BE REDONE WITH THE PROPER
        /* Given */
        // BELOW IS WRONG, REDO TO TEST THE BAN USER FUNCTIONALITY
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
        Assert.assertEquals(EXPECTED_BANNEDUSERID, result.id)
        Assert.assertEquals(expectedTwitchUserData.userType, result.userType)
    }
    // TODO: TEST USERNAME PARSING WITH NON LATIN BASED ALPHABETS
    // 板橋小結巴 THIS IS A MANDARIN BASED USER NAME

    @Test
    fun ban_user_parsing_with_non_latin_alphabet() {
        /* Given */
        val EXPECTED_BANNEDUSERID = "949335660"
        val EXPECTED_USERNAME = "板橋小結巴"

        val BAN_USER_TEXT = "@room-id=520593641;target-user-id=949335660;tmi-sent-ts=1696019132494 :tmi.twitch.tv CLEARCHAT #theplebdev :板橋小結巴"
        val STREAMER_NAME = "theplebdev"

        /* When */
        val result = underTest.clearChatTesting(text = BAN_USER_TEXT, streamerName = STREAMER_NAME)

        /* Then */
        Assert.assertEquals(EXPECTED_USERNAME, result.displayName)
    }

    @Test
    fun user_state_parsing() {
        /* Given */
        val EXPECTED_COLOR = "#000000"
        val EXPECTED_DISPLAYNAME = "bobber-micky54$"
        val EXPECTED_MOD = 0
        val EXPECTED_SUB = 0
        val givenString = "@badge-info=;badges=staff/1;color=$EXPECTED_COLOR;display-name=$EXPECTED_DISPLAYNAME;emote-sets=0,33,50,237,793,2126,3517,4578,5569,9400,10337,12239;mod=$EXPECTED_MOD;subscriber=$EXPECTED_SUB;turbo=1;user-type=staff :tmi.twitch.tv USERSTATE #dallas"

        /* When */
        val result = underTest.userStateParsing(givenString)

        /* Then */
        Assert.assertEquals(EXPECTED_COLOR, result.color)
        Assert.assertEquals(EXPECTED_DISPLAYNAME, result.displayName)
        Assert.assertEquals(false, result.mod)
        Assert.assertEquals(false, result.sub)
    }

    @Test
    fun clear_message_parsing() {
        /* Given */
        val EXPECTED_MSG_ID = "94e6c7ff-bf98-4faa-af5d-7ad633a158a9"
        val parsingString = "@login=foo;room-id=;target-msg-id=$EXPECTED_MSG_ID;tmi-sent-ts=1642720582342 :tmi.twitch.tv CLEARMSG #bar :what a great day"

        /* When */
        val result = underTest.clearMsgParsing(parsingString)

        /* Then */
        Assert.assertEquals(EXPECTED_MSG_ID, result)
    }

    @Test
    fun user_notice_parsing_no_personal_message() {
        /* Given */
        val streamerName = "hasanabi"
        val EXPECTED_USERNAME = "Bob34t4"
        val EXPECTED_SYSTEM_MESSAGE = "CoalTheTroll subscribed with Prime. They've subscribed for 25 months!"
        val EXPECTED_MESSAGE_ID = "submysterygift"
        val parsingString = "@badge-info=subscriber/25;badges=subscriber/24,premium/1;color=#008000;display-name=$EXPECTED_USERNAME;emotes=;flags=;id=3ceab6bd-de3f-4d05-8038-5cebdb2af1c7;login=coalthetroll;mod=0;msg-id=$EXPECTED_MESSAGE_ID;msg-param-cumulative-months=25;msg-param-months=0;msg-param-multimonth-duration=0;msg-param-multimonth-tenure=0;msg-param-should-share-streak=0;msg-param-sub-plan-name=Woke\\sBeys\\s(hasanpiker):\\s\$4.99\\sSub;msg-param-sub-plan=Prime;msg-param-was-gifted=false;room-id=207813352;subscriber=1;system-msg=CoalTheTroll\\ssubscribed\\swith\\sPrime.\\sThey've\\ssubscribed\\sfor\\s25\\smonths!;tmi-sent-ts=1696621315536;user-id=73777153;user-type=;vip=0 :tmi.twitch.tv USERNOTICE #hasanabi"

        /* Given */
        val result = underTest.userNoticeParsing(parsingString, streamerName)

        /* Then */
        Assert.assertEquals(MessageType.MYSTERYGIFTSUB, result.messageType)
        Assert.assertEquals(EXPECTED_USERNAME, result.displayName)
        Assert.assertEquals(EXPECTED_SYSTEM_MESSAGE, result.userType)
    }

    @Test
    fun user_notice_parsing_with_personal_message() {
        /* Given */
        val streamerName = "hasanabi"
        val EXPECTED_MESSAGE = "yo <3"
        val EXPECTED_USERNAME = "Bob34t4"
        val EXPECTED_SYSTEM_MESSAGE = "CoalTheTroll subscribed with Prime. They've subscribed for 25 months!"
        val EXPECTED_MESSAGE_ID = "submysterygift"
        val parsingString = "@badge-info=subscriber/25;badges=subscriber/24,premium/1;color=#008000;display-name=$EXPECTED_USERNAME;emotes=;flags=;id=3ceab6bd-de3f-4d05-8038-5cebdb2af1c7;login=coalthetroll;mod=0;msg-id=$EXPECTED_MESSAGE_ID;msg-param-cumulative-months=25;msg-param-months=0;msg-param-multimonth-duration=0;msg-param-multimonth-tenure=0;msg-param-should-share-streak=0;msg-param-sub-plan-name=Woke\\sBeys\\s(hasanpiker):\\s\$4.99\\sSub;msg-param-sub-plan=Prime;msg-param-was-gifted=false;room-id=207813352;subscriber=1;system-msg=CoalTheTroll\\ssubscribed\\swith\\sPrime.\\sThey've\\ssubscribed\\sfor\\s25\\smonths!;tmi-sent-ts=1696621315536;user-id=73777153;user-type=;vip=0 :tmi.twitch.tv USERNOTICE #$streamerName :$EXPECTED_MESSAGE"

        /* Given */
        val result = underTest.userNoticeParsing(parsingString, streamerName)

        /* Then */
        Assert.assertEquals(MessageType.MYSTERYGIFTSUB, result.messageType)
        Assert.assertEquals(EXPECTED_USERNAME, result.displayName)
        Assert.assertEquals(EXPECTED_MESSAGE, result.userType)
        Assert.assertEquals(EXPECTED_SYSTEM_MESSAGE, result.systemMessage)
    }

    @Test
    fun private_message_parsing() {
        /* Given */

        val EXPECTED_MESSAGE = "GIGACHAD"
        val LONGER_EXPECTED_MESSAGE = "@NotTooBadAye same was just thinking the same thing"
        val channelName = "cohhcarnage"

        val parsingString = "@badge-info=;badges=premium/1;client-nonce=3630d7e4c765758191175beaf3929b33;color=;display-name=FemboyQtx;emotes=;first-msg=1;flags=;id=d2c1740e-aab5-4428-9b45-5ca049e7e669;mod=0;returning-chatter=0;room-id=26610234;subscriber=0;tmi-sent-ts=1696686131397;turbo=0;user-id=816961592;user-type= :femboyqtx!femboyqtx@femboyqtx.tmi.twitch.tv PRIVMSG #cohhcarnage :$EXPECTED_MESSAGE"
        val longerParsingString = "@badge-info=;badges=premium/1;client-nonce=3110c13339efbb8bf9e2c10c9951de6d;color=#8A2BE2;display-name=PixelWhip;emotes=;first-msg=0;flags=;id=1df2a3a7-7ab7-42a3-882c-836cea92f377;mod=0;reply-parent-display-name=NotTooBadAye;reply-parent-msg-body=this\\sgame\\sreminds\\sme\\sof\\sNeopets;reply-parent-msg-id=43eb8d7d-4584-4777-9b14-fc6479637b20;reply-parent-user-id=72807154;reply-parent-user-login=nottoobadaye;reply-thread-parent-msg-id=43eb8d7d-4584-4777-9b14-fc6479637b20;reply-thread-parent-user-login=nottoobadaye;returning-chatter=0;room-id=26610234;subscriber=0;tmi-sent-ts=1696686129437;turbo=0;user-id=66065436;user-type= :pixelwhip!pixelwhip@pixelwhip.tmi.twitch.tv PRIVMSG #cohhcarnage :$LONGER_EXPECTED_MESSAGE"

        /* When */
        val result = underTest.privateMessageParsing(longerParsingString,channelName)

        /* Then */
        Assert.assertEquals(LONGER_EXPECTED_MESSAGE, result.userType)
    }



    @Test
    fun getting_duration(){
        val text = "@followers-only=10;room-id=520593641 :tmi.twitch.tv ROOMSTATE #theplebdev"
        val actualResult = underTest.getDuration(text, "followers-only")


        Assert.assertEquals(10, actualResult)
    }
    @Test
    fun night_bot_PRIVMSG_test(){
        /* Given */
        val channelName = "tru3ta1ent"
        val expectedResult = "By subscribing to the channel here - https://secure.twitch.tv/products/tru3ta1ent/ticket/new you will get ZERO ads for how ever long you sub for! tru3Love "
        val text = "@badge-info=subscriber/82;badges=moderator/1,subscriber/3072,partner/1;color=#7C7CE1;display-name=Nightbot;emotes=140523:146-153;first-msg=0;flags=;id=40aebe34-5f5e-466a-b2b3-21fdc3e241a3;mod=1;returning-chatter=0;room-id=48286022;subscriber=1;tmi-sent-ts=1699455792281;turbo=0;user-id=19264788;user-type=mod :nightbot!nightbot@nightbot.tmi.twitch.tv PRIVMSG #tru3ta1ent :$expectedResult"

        /* When */
        // so we need to match everything after this patter, #tru3ta1ent :
        val actualResult = underTest.privateMessageParsing(text,channelName).userType

        /* THEN */
        Assert.assertEquals(expectedResult, actualResult)
    }
    @Test
    fun star_notice_testing(){
        /* Given */
        val expectedResult="Login authentication failed"
        val testText = ":tmi.twitch.tv NOTICE * :$expectedResult"

        /* When */
        // so we need to match everything after this patter, #tru3ta1ent :

        val actualResult = underTest.noticeParsing(testText,"channelName").userType


        /* THEN */
        Assert.assertEquals(expectedResult, actualResult)
    }

    @Test
    fun new_clear_message_parsing(){
        /* Given */
        val givenStringBanDuration = "@ban-duration=600;room-id=520593641;target-user-id=949335660;tmi-sent-ts=1700154214763 :tmi.twitch.tv CLEARCHAT #theplebdev :meanermeeny"
        val givenStringWithNoBanDuration = "room-id=520593641;target-user-id=949335660;tmi-sent-ts=1700154214763 :tmi.twitch.tv CLEARCHAT #theplebdev :meanermeeny"

        /* When */
        val pattern = "ban-duration=([^;]+)".toRegex()

        val banDurationFound = pattern.find(givenStringBanDuration)?.groupValues?.get(1)
        val banDurationNotFound = pattern.find(givenStringWithNoBanDuration)?.groupValues?.get(1)

        /* Then */
        Assert.assertEquals("600", banDurationFound)
        Assert.assertEquals(null, banDurationNotFound)

    }

    @Test
    fun message_id_parsing(){
        val messageIdExpected ="msg-id=emote_only_on"
        val testString ="@msg-id=emote_only_on :tmi.twitch.tv NOTICE #theplebdev :This room is now in emote-only mode."
        val pattern = "msg-id=emote_only_on".toRegex()

        val foundId = pattern.find(testString)?.value

        Assert.assertEquals(messageIdExpected, foundId)
    }

    @Test
    fun parsing_seems_good(){
        /**GIVEN*/
        val channelName ="theplebdev"
        val foundEmoteOne = "SeemsGood"
        val foundEmoteTwo = "GoldPLZ"
        val privateMsgPattern = "(#$channelName :)(.+)".toRegex()
        val input ="@badge-info=;badges=;client-nonce=3f38fcdc2df589ee2c07f582ddb1941b;color=;display-name=meanermeeny;emotes=64138:24-32;first-msg=0;flags=;id=ef438e2b-3117-49ee-b4f6-7086f078c8d8;mod=0;returning-chatter=0;room-id=520593641;subscriber=0;tmi-sent-ts=1715189385354;turbo=0;user-id=949335660;user-type= :meanermeeny!meanermeeny@meanermeeny.tmi.twitch.tv PRIVMSG #theplebdev :testing $foundEmoteOne $foundEmoteTwo  again"
        val privateMsgResult = privateMsgPattern.find(input)
        val privateMsg = privateMsgResult?.groupValues?.get(2) ?: ""
        val emoteNames = listOf("SeemsGood","ChewyYAY", "GoatEmotey", "GoldPLZ", "ForSigmar", "TwitchConHYPE", "PopNemo", "FlawlessVictory", "PikaRamen", "DinoDance", "NiceTry", "LionOfYara", "NewRecord", "Lechonk", "Getcamped", "SUBprise", "FallHalp", "FallCry", "FallWinning")

        /**WHEN*/
        val foundEmotes = findEmoteNames(privateMsg, emoteNames)

        /**THEN*/
        //Assert.assertEquals(foundEmotes[0].emoteKey, foundEmoteOne)

    }

    @Test
    fun incoming_message_tokenization(){
        /**GIVEN*/
        val text = "antoher one SeemsGood seemsgood"
        val scanner = MessageScanner(text)

        /**WHEN*/
        scanner.startScanningTokens()
        val tokenList = scanner.tokenList

        /**THEN*/
        Assert.assertEquals(tokenList.size, 4)

    }
}




