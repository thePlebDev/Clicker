package com.example.clicker.network.repository.websockets

import com.example.clicker.network.clients.UnbanRequestItem
import com.example.clicker.network.models.twitchStream.ChatSettingsData
import com.example.clicker.network.repository.util.AutoModMessageParsing
import com.example.clicker.network.repository.util.ChatSettingsParsing
import com.example.clicker.network.repository.util.ModActionParsing
import com.example.clicker.network.websockets.notificationTypeIsNotification
import com.example.clicker.network.websockets.notificationTypeIsWelcome

import com.example.clicker.network.websockets.parseEventSubWelcomeMessage

import com.example.clicker.network.websockets.parseStatusType
import com.example.clicker.network.websockets.parseSubscriptionType
import org.junit.Assert
import org.junit.Test
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class TwitchEventSubWebSocketTest {

    private val modActionParsing = ModActionParsing()
    private val chatSettingsParsing = ChatSettingsParsing()
    private val autoModMessageParsing: AutoModMessageParsing = AutoModMessageParsing()


    @Test
    fun parsing_welcome_session_id(){
        /**GIVEN*/
        val expectedMessageId ="AgoQYw-xoivNRc-gGlydLD3vABIGY2VsbC1i"
        val stringToParse="{\"metadata\":{\"message_id\":\"8bfad1fb-8af7-4e9c-a028-c15c05575a7c\",\"message_type\":\"session_welcome\",\"message_timestamp\":\"2024-04-05T22:16:46.828736991Z\"},\"payload\":{\"session\":{\"id\":\"AgoQYw-xoivNRc-gGlydLD3vABIGY2VsbC1i\",\"status\":\"connected\",\"connected_at\":\"2024-04-05T22:16:46.818068229Z\",\"keepalive_timeout_seconds\":10,\"reconnect_url\":null,\"recovery_url\":null}}}"

        /**WHEN*/
        val parsedMessageId = parseEventSubWelcomeMessage(stringToParse)

        /**THEN*/
        Assert.assertEquals(expectedMessageId, parsedMessageId)

    }

    @Test
    fun parsing_notification_message_type(){
        /**GIVEN*/
        val expectedUsername = "meanermeeny"
        val expectedText = "fucking dude wtf testing"
        val expectedCategory = "swearing"
        val expectedUserId ="949335660"
        val expectedMessageId="307352ba-ed6c-4a1c-8f15-110e56a6595a"

        val stringToParse ="{\"metadata\":{\"message_id\":\"Ydtu-bdsaX3N4mpOFq_4t_NopStBaZvOVEdJFLF32gk=\",\"message_type\":\"notification\",\"message_timestamp\":\"2024-04-07T17:31:05.637055709Z\",\"subscription_type\":\"automod.message.hold\",\"subscription_version\":\"1\"},\"payload\":{\"subscription\":{\"id\":\"188aac9d-eeae-4f34-b2bf-e26ec6f2de3a\",\"status\":\"enabled\",\"type\":\"automod.message.hold\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"520593641\",\"moderator_user_id\":\"946933663\"},\"transport\":{\"method\":\"websocket\",\"session_id\":\"AgoQokeExKb9Sbi1Wd6DgL0pWRIGY2VsbC1i\"},\"created_at\":\"2024-04-07T17:28:09.657846521Z\",\"cost\":0},\"event\":{\"broadcaster_user_id\":\"520593641\",\"broadcaster_user_login\":\"theplebdev\",\"broadcaster_user_name\":\"theplebdev\",\"user_id\":\"949335660\",\"user_login\":\"meanermeeny\",\"user_name\":\"meanermeeny\",\"message_id\":\"307352ba-ed6c-4a1c-8f15-110e56a6595a\",\"message\":{\"text\":\"fucking dude wtf testing\",\"fragments\":[{\"type\":\"text\",\"text\":\"fucking\",\"cheermote\":null,\"emote\":null},{\"type\":\"text\",\"text\":\" dude \",\"cheermote\":null,\"emote\":null},{\"type\":\"text\",\"text\":\"wtf\",\"cheermote\":null,\"emote\":null},{\"type\":\"text\",\"text\":\" testing\",\"cheermote\":null,\"emote\":null}]},\"category\":\"swearing\",\"level\":4,\"held_at\":\"2024-04-07T17:31:04.841129605Z\"}}}"

        val messageIdRegex = "\"message_id\":([^=]+)".toRegex()
        val allFoundMessageIdList = messageIdRegex.findAll(stringToParse).toList()
        val messageIdNoQuotes=allFoundMessageIdList[1].groupValues[1].replace("\"","")

        val newMessageRegex = "[^,]+".toRegex()
        val desiredMessageId = newMessageRegex.find(messageIdNoQuotes)?.value


        /**WHEN*/
        val autoQueueMessage = autoModMessageParsing.parseAutoModQueueMessage(stringToParse)
        println("messageIdSize --> $desiredMessageId")

        /**THEN*/
        Assert.assertEquals(expectedMessageId, autoQueueMessage.messageId)
        Assert.assertEquals(expectedUserId, autoQueueMessage.userId)
        Assert.assertEquals(expectedUsername, autoQueueMessage.username)
        Assert.assertEquals(expectedText, autoQueueMessage.fullText)
        Assert.assertEquals(expectedCategory, autoQueueMessage.category)
        Assert.assertEquals(true, notificationTypeIsNotification(stringToParse))
    }
    @Test
    fun parsing_welcome_message_type(){
        /**GIVEN*/
        val stringToParse ="{\"metadata\":{\"message_id\":\"9e1f6210-71ef-477f-bfc3-9bdc2e726202\",\"message_type\":\"session_welcome\",\"message_timestamp\":\"2024-04-10T17:56:10.206700638Z\"},\"payload\":{\"session\":{\"id\":\"AgoQDXbKk8FsQBSNCtRW0FjYhxIGY2VsbC1i\",\"status\":\"connected\",\"connected_at\":\"2024-04-10T17:56:10.197705509Z\",\"keepalive_timeout_seconds\":10,\"reconnect_url\":null,\"recovery_url\":null}}}"

        /**WHEN*/
        val actualMessage = notificationTypeIsWelcome(stringToParse)


        /**THEN*/
        Assert.assertEquals(true, actualMessage)
    }
    @Test
    fun parsing_autoMod_message_update(){
        /**GIVEN*/
        val stringToParse ="{\"metadata\":{\"message_id\":\"trwb-ee6WBXSZDdemn75iaGC8r01keqycRu2U3EWoDA=\",\"message_type\":\"notification\",\"message_timestamp\":\"2024-04-11T15:40:11.138443076Z\",\"subscription_type\":\"automod.message.update\",\"subscription_version\":\"1\"},\"payload\":{\"subscription\":{\"id\":\"4bc7826f-1f11-481a-8c58-d758fcda5fb8\",\"status\":\"enabled\",\"type\":\"automod.message.update\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"520593641\",\"moderator_user_id\":\"946933663\"},\"transport\":{\"method\":\"websocket\",\"session_id\":\"AgoQGQKjr3lXTpGzHUlC6WPNQBIGY2VsbC1i\"},\"created_at\":\"2024-04-11T15:39:34.564728572Z\",\"cost\":0},\"event\":{\"broadcaster_user_id\":\"520593641\",\"broadcaster_user_login\":\"theplebdev\",\"broadcaster_user_name\":\"theplebdev\",\"user_id\":\"949335660\",\"user_login\":\"meanermeeny\",\"user_name\":\"meanermeeny\",\"moderator_user_id\":\"520593641\",\"moderator_user_login\":\"theplebdev\",\"moderator_user_name\":\"theplebdev\",\"message_id\":\"6dfe9f99-7525-4dfd-acd2-32500427761f\",\"message\":{\"text\":\"fucking this dude wtf\",\"fragments\":[{\"type\":\"text\",\"text\":\"fucking\",\"cheermote\":null,\"emote\":null},{\"type\":\"text\",\"text\":\" this dude \",\"cheermote\":null,\"emote\":null},{\"type\":\"text\",\"text\":\"wtf\",\"cheermote\":null,\"emote\":null}]},\"category\":\"swearing\",\"level\":4,\"status\":\"denied\",\"held_at\":\"2024-04-11T15:39:55.553668661Z\"}}}"

        val expectedSubscriptionType ="automod.message.update"
        val expectedMessageId ="6dfe9f99-7525-4dfd-acd2-32500427761f"

        /**WHEN*/
        val subscriptionType = parseSubscriptionType(stringToParse)
        val messageId =autoModMessageParsing.parseMessageId(stringToParse)


        /**THEN*/
        Assert.assertEquals(expectedSubscriptionType, subscriptionType)
        Assert.assertEquals(expectedMessageId, messageId)
    }

    @Test
    fun parsing_autoMod_message_update_status(){
        /**GIVEN*/
        val stringToParse ="{\"metadata\":{\"message_id\":\"trwb-ee6WBXSZDdemn75iaGC8r01keqycRu2U3EWoDA=\",\"message_type\":\"notification\",\"message_timestamp\":\"2024-04-11T15:40:11.138443076Z\",\"subscription_type\":\"automod.message.update\",\"subscription_version\":\"1\"},\"payload\":{\"subscription\":{\"id\":\"4bc7826f-1f11-481a-8c58-d758fcda5fb8\",\"status\":\"enabled\",\"type\":\"automod.message.update\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"520593641\",\"moderator_user_id\":\"946933663\"},\"transport\":{\"method\":\"websocket\",\"session_id\":\"AgoQGQKjr3lXTpGzHUlC6WPNQBIGY2VsbC1i\"},\"created_at\":\"2024-04-11T15:39:34.564728572Z\",\"cost\":0},\"event\":{\"broadcaster_user_id\":\"520593641\",\"broadcaster_user_login\":\"theplebdev\",\"broadcaster_user_name\":\"theplebdev\",\"user_id\":\"949335660\",\"user_login\":\"meanermeeny\",\"user_name\":\"meanermeeny\",\"moderator_user_id\":\"520593641\",\"moderator_user_login\":\"theplebdev\",\"moderator_user_name\":\"theplebdev\",\"message_id\":\"6dfe9f99-7525-4dfd-acd2-32500427761f\",\"message\":{\"text\":\"fucking this dude wtf\",\"fragments\":[{\"type\":\"text\",\"text\":\"fucking\",\"cheermote\":null,\"emote\":null},{\"type\":\"text\",\"text\":\" this dude \",\"cheermote\":null,\"emote\":null},{\"type\":\"text\",\"text\":\"wtf\",\"cheermote\":null,\"emote\":null}]},\"category\":\"swearing\",\"level\":4,\"status\":\"denied\",\"held_at\":\"2024-04-11T15:39:55.553668661Z\"}}}"
        val expectedStatus ="denied"

        /**WHEN*/
        val status=  parseStatusType(stringToParse)


        /**THEN*/
        Assert.assertEquals(expectedStatus, status)


    }

    @Test
    fun parsing_autoMod_message_hold(){
        /**GIVEN*/
        val stringToParse ="{\"metadata\":{\"message_id\":\"Ydtu-bdsaX3N4mpOFq_4t_NopStBaZvOVEdJFLF32gk=\",\"message_type\":\"notification\",\"message_timestamp\":\"2024-04-07T17:31:05.637055709Z\",\"subscription_type\":\"automod.message.hold\",\"subscription_version\":\"1\"},\"payload\":{\"subscription\":{\"id\":\"188aac9d-eeae-4f34-b2bf-e26ec6f2de3a\",\"status\":\"enabled\",\"type\":\"automod.message.hold\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"520593641\",\"moderator_user_id\":\"946933663\"},\"transport\":{\"method\":\"websocket\",\"session_id\":\"AgoQokeExKb9Sbi1Wd6DgL0pWRIGY2VsbC1i\"},\"created_at\":\"2024-04-07T17:28:09.657846521Z\",\"cost\":0},\"event\":{\"broadcaster_user_id\":\"520593641\",\"broadcaster_user_login\":\"theplebdev\",\"broadcaster_user_name\":\"theplebdev\",\"user_id\":\"949335660\",\"user_login\":\"meanermeeny\",\"user_name\":\"meanermeeny\",\"message_id\":\"307352ba-ed6c-4a1c-8f15-110e56a6595a\",\"message\":{\"text\":\"fucking dude wtf testing\",\"fragments\":[{\"type\":\"text\",\"text\":\"fucking\",\"cheermote\":null,\"emote\":null},{\"type\":\"text\",\"text\":\" dude \",\"cheermote\":null,\"emote\":null},{\"type\":\"text\",\"text\":\"wtf\",\"cheermote\":null,\"emote\":null},{\"type\":\"text\",\"text\":\" testing\",\"cheermote\":null,\"emote\":null}]},\"category\":\"swearing\",\"level\":4,\"held_at\":\"2024-04-07T17:31:04.841129605Z\"}}}"
        val expectedSubscriptionType ="automod.message.hold"

        /**WHEN*/
        val subscriptionType = parseSubscriptionType(stringToParse)



        /**THEN*/
        Assert.assertEquals(expectedSubscriptionType, subscriptionType)

    }

    @Test
    fun parsing_chat_settings_update(){
        /**GIVEN*/

        val expectedEmoteMode = true
        val expectedSubscriberMode = true

        val expectedFollowerMode = true
        val expectedFollowerModeDuration = 33
        val expectedSlowMode = true
        val expectedSlowModeDuration = 44


        val anotherStringToParse ="{\"metadata\":{\"message_id\":\"WnfTJmChxvcgPWHgfottTLgmyoGlM1EfzfAV3_QB3fw=\",\"message_type\":\"notification\",\"message_timestamp\":\"2024-06-17T02:07:01.406636207Z\",\"subscription_type\":\"channel.chat_settings.update\",\"subscription_version\":\"1\"},\"payload\":{\"subscription\":{\"id\":\"b64b7a8c-cb46-475d-9077-edfec2f27079\",\"status\":\"enabled\",\"type\":\"channel.chat_settings.update\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"520593641\",\"user_id\":\"946933663\"},\"transport\":{\"method\":\"websocket\",\"session_id\":\"AgoQIBY8fv56ShabsomD9WssjhIGY2VsbC1i\"},\"created_at\":\"2024-06-17T02:06:51.693257523Z\",\"cost\":0},\"event\":{\"broadcaster_user_id\":\"520593641\",\"broadcaster_user_login\":\"theplebdev\",\"broadcaster_user_name\":\"theplebdev\",\"emote_mode\":true,\"follower_mode\":false,\"follower_mode_duration_minutes\":null,\"slow_mode\":false,\"slow_mode_wait_time_seconds\":null,\"subscriber_mode\":true,\"unique_chat_mode\":false}}}"
        val stringToParse ="{\"metadata\":{\"message_id\":\"rZ-HByBPf2Mc7NfYSgR5mlsf0jHsNdZ9VKKb313Lbjw=\",\"message_type\":\"notification\",\"message_timestamp\":\"2024-04-14T23:53:59.928981523Z\",\"subscription_type\":\"channel.chat_settings.update\",\"subscription_version\":\"1\"},\"payload\":{\"subscription\":{\"id\":\"46440d9c-e754-47be-840a-943c1250a222\",\"status\":\"enabled\",\"type\":\"channel.chat_settings.update\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"520593641\",\"user_id\":\"946933663\"},\"transport\":{\"method\":\"websocket\",\"session_id\":\"AgoQvN_TbZDMRr6yuimMnf6ZHRIGY2VsbC1i\"},\"created_at\":\"2024-04-14T23:52:23.445062985Z\",\"cost\":0},\"event\":{\"broadcaster_user_id\":\"520593641\",\"broadcaster_user_login\":\"theplebdev\",\"broadcaster_user_name\":\"theplebdev\",\"emote_mode\":$expectedEmoteMode,\"follower_mode\":$expectedFollowerMode,\"follower_mode_duration_minutes\":$expectedFollowerModeDuration,\"slow_mode\":$expectedSlowMode,\"slow_mode_wait_time_seconds\":$expectedSlowModeDuration,\"subscriber_mode\":$expectedSubscriberMode,\"unique_chat_mode\":false}}}"
        val expectedSubscriptionType ="channel.chat_settings.update"

        val parsedChatSettingsData =chatSettingsParsing.parseChatSettingsData(stringToParse)


        /**WHEN*/
        val actualSubscriptionType =parseSubscriptionType(stringToParse)

        /**THEN*/
        Assert.assertEquals(expectedEmoteMode, parsedChatSettingsData.emoteMode)
        Assert.assertEquals(expectedSubscriberMode, parsedChatSettingsData.subscriberMode)
        Assert.assertEquals(expectedFollowerMode, parsedChatSettingsData.followerMode)
        Assert.assertEquals(expectedFollowerModeDuration, parsedChatSettingsData.followerModeDuration)
        Assert.assertEquals(expectedSlowMode, parsedChatSettingsData.slowMode)
        Assert.assertEquals(expectedSlowModeDuration, parsedChatSettingsData.slowModeWaitTime)
        Assert.assertEquals(actualSubscriptionType, expectedSubscriptionType)

    }

    @Test
    fun `testing failing thinger`(){
        // I need to test
        val testingString = "{\"metadata\":{\"message_id\":\"pjsWbBkLJwRarWHDkTpCB7YV6LNc2fZt1Q8VPfRWOSg=\",\"message_type\":\"notification\",\"message_timestamp\":\"2024-06-15T22:17:46.833518239Z\",\"subscription_type\":\"channel.moderate\",\"subscription_version\":\"1\"},\"payload\":{\"subscription\":{\"id\":\"d40b2695-ecbd-4b25-ae13-1b54c5d1c0c1\",\"status\":\"enabled\",\"type\":\"channel.moderate\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"520593641\",\"moderator_user_id\":\"946933663\"},\"transport\":{\"method\":\"websocket\",\"session_id\":\"AgoQxz8Sci1BSTOkdudModlGhhIGY2VsbC1i\"},\"created_at\":\"2024-06-15T22:17:36.135821546Z\",\"cost\":0},\"event\":{\"broadcaster_user_id\":\"520593641\",\"broadcaster_user_login\":\"theplebdev\",\"broadcaster_user_name\":\"theplebdev\",\"moderator_user_id\":\"520593641\",\"moderator_user_login\":\"theplebdev\",\"moderator_user_name\":\"theplebdev\",\"action\":\"timeout\",\"followers\":null,\"slow\":null,\"vip\":null,\"unvip\":null,\"mod\":null,\"unmod\":null,\"ban\":null,\"unban\":null,\"timeout\":{\"user_id\":\"949335660\",\"user_login\":\"meanermeeny\",\"user_name\":\"meanermeeny\",\"reason\":\"\",\"expires_at\":\"2024-06-15T22:27:46.81829331Z\"},\"untimeout\":null,\"raid\":null,\"unraid\":null,\"delete\":null,\"automod_terms\":null,\"unban_request\":null}}}"

        println("expires in"+getExpiresAtTest(testingString))
        Assert.assertEquals(1, 2)


    }
    private fun getExpiresAtTest(stringToParse: String):String{

        val messageTypeRegex = "\"expires_at\":\"([^\"]*)".toRegex()

        val foundString =messageTypeRegex.find(stringToParse)?.groupValues?.get(1)

        if(foundString != null){
            return convertToReadableDateTest(foundString) ?:""
        }
        else return ""
    }

    fun convertToReadableDateTest(timestamp: String): String {
        // Define the date format expected for the timestamp
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        // Parse the timestamp to a Date object
        val date: Date
        try {
            date = dateFormat.parse(timestamp)
        } catch (e: Exception) {
            return ""
        }

        // Get the current date and time
        val currentDate = Calendar.getInstance().time

        // Calculate the difference in seconds
        val bannedSeconds = (date.time - currentDate.time) / 10000

        return bannedSeconds.toString()
    }

    @Test
    fun `parsing moderation action`(){
        val stringToParse ="{\"metadata\":{\"message_id\":\"a2bfJHk2z_vTexJKgMVqKT0Fz68r7nFTeERPwEgHEUI=\",\"message_type\":\"notification\",\"message_timestamp\":\"2024-06-14T01:57:06.371464897Z\",\"subscription_type\":\"channel.moderate\",\"subscription_version\":\"1\"},\"payload\":{\"subscription\":{\"id\":\"7caf1631-1c0e-408c-985d-adf5b7b5101a\",\"status\":\"enabled\",\"type\":\"channel.moderate\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"520593641\",\"moderator_user_id\":\"946933663\"},\"transport\":{\"method\":\"websocket\",\"session_id\":\"AgoQTbl6eU93QxqK0eBXgjgo8RIGY2VsbC1i\"},\"created_at\":\"2024-06-14T01:55:43.164335608Z\",\"cost\":0},\"event\":{\"broadcaster_user_id\":\"520593641\",\"broadcaster_user_login\":\"theplebdev\",\"broadcaster_user_name\":\"theplebdev\",\"moderator_user_id\":\"520593641\",\"moderator_user_login\":\"theplebdev\",\"moderator_user_name\":\"theplebdev\",\"action\":\"untimeout\",\"followers\":null,\"slow\":null,\"vip\":null,\"unvip\":null,\"mod\":null,\"unmod\":null,\"ban\":null,\"unban\":null,\"timeout\":null,\"untimeout\":{\"user_id\":\"949335660\",\"user_login\":\"meanermeeny\",\"user_name\":\"meanermeeny\"},\"raid\":null,\"unraid\":null,\"delete\":null,\"automod_terms\":null,\"unban_request\":null}}}"

        val subscribersOnlyOffString ="{\"metadata\":{\"message_id\":\"bDvc1KOhpHgOCHAUNWVmllDLDWD4SY2BizmhLmbZxK0=\",\"message_type\":\"notification\",\"message_timestamp\":\"2024-06-16T17:00:09.35426471Z\",\"subscription_type\":\"channel.moderate\",\"subscription_version\":\"1\"},\"payload\":{\"subscription\":{\"id\":\"07a6ce98-5afd-4148-8636-372ce9a4e30c\",\"status\":\"enabled\",\"type\":\"channel.moderate\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"520593641\",\"moderator_user_id\":\"946933663\"},\"transport\":{\"method\":\"websocket\",\"session_id\":\"AgoQLAd-3Yi_SVmoLHdSRCXFCBIGY2VsbC1i\"},\"created_at\":\"2024-06-16T17:00:00.922161673Z\",\"cost\":0},\"event\":{\"broadcaster_user_id\":\"520593641\",\"broadcaster_user_login\":\"theplebdev\",\"broadcaster_user_name\":\"theplebdev\",\"moderator_user_id\":\"520593641\",\"moderator_user_login\":\"theplebdev\",\"moderator_user_name\":\"theplebdev\",\"action\":\"subscribersoff\",\"followers\":null,\"slow\":null,\"vip\":null,\"unvip\":null,\"mod\":null,\"unmod\":null,\"ban\":null,\"unban\":null,\"timeout\":null,\"untimeout\":null,\"raid\":null,\"unraid\":null,\"delete\":null,\"automod_terms\":null,\"unban_request\":null}}}"
        val emoteOnlyOff ="{\"metadata\":{\"message_id\":\"te_Ier_0QeMRNnzMB3LzCJ2vLqpFchCeUly4g1O_CLU=\",\"message_type\":\"notification\",\"message_timestamp\":\"2024-06-15T02:10:09.222309637Z\",\"subscription_type\":\"channel.moderate\",\"subscription_version\":\"1\"},\"payload\":{\"subscription\":{\"id\":\"09482491-882d-479a-ad54-b45b8cb7c14c\",\"status\":\"enabled\",\"type\":\"channel.moderate\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"520593641\",\"moderator_user_id\":\"946933663\"},\"transport\":{\"method\":\"websocket\",\"session_id\":\"AgoQc73Y5j05SzKCGeDI79dngRIGY2VsbC1i\"},\"created_at\":\"2024-06-15T02:09:51.382733083Z\",\"cost\":0},\"event\":{\"broadcaster_user_id\":\"520593641\",\"broadcaster_user_login\":\"theplebdev\",\"broadcaster_user_name\":\"theplebdev\",\"moderator_user_id\":\"520593641\",\"moderator_user_login\":\"theplebdev\",\"moderator_user_name\":\"theplebdev\",\"action\":\"emoteonlyoff\",\"followers\":null,\"slow\":null,\"vip\":null,\"unvip\":null,\"mod\":null,\"unmod\":null,\"ban\":null,\"unban\":null,\"timeout\":null,\"untimeout\":null,\"raid\":null,\"unraid\":null,\"delete\":null,\"automod_terms\":null,\"unban_request\":null}}}"
        val followersOffString ="{\"metadata\":{\"message_id\":\"mq_pOx72Z2EMf3WfaNucb2hxAZ0_fnc9195uqclG-lY=\",\"message_type\":\"notification\",\"message_timestamp\":\"2024-06-15T02:10:08.176556348Z\",\"subscription_type\":\"channel.moderate\",\"subscription_version\":\"1\"},\"payload\":{\"subscription\":{\"id\":\"09482491-882d-479a-ad54-b45b8cb7c14c\",\"status\":\"enabled\",\"type\":\"channel.moderate\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"520593641\",\"moderator_user_id\":\"946933663\"},\"transport\":{\"method\":\"websocket\",\"session_id\":\"AgoQc73Y5j05SzKCGeDI79dngRIGY2VsbC1i\"},\"created_at\":\"2024-06-15T02:09:51.382733083Z\",\"cost\":0},\"event\":{\"broadcaster_user_id\":\"520593641\",\"broadcaster_user_login\":\"theplebdev\",\"broadcaster_user_name\":\"theplebdev\",\"moderator_user_id\":\"520593641\",\"moderator_user_login\":\"theplebdev\",\"moderator_user_name\":\"theplebdev\",\"action\":\"followersoff\",\"followers\":null,\"slow\":null,\"vip\":null,\"unvip\":null,\"mod\":null,\"unmod\":null,\"ban\":null,\"unban\":null,\"timeout\":null,\"untimeout\":null,\"raid\":null,\"unraid\":null,\"delete\":null,\"automod_terms\":null,\"unban_request\":null}}}"
        val slowModeOffString ="{\"metadata\":{\"message_id\":\"vqLFkigIcmKay6lSaUvxg6EPSl3Io1YG3tVFlGx1GM4=\",\"message_type\":\"notification\",\"message_timestamp\":\"2024-06-15T02:10:04.259505497Z\",\"subscription_type\":\"channel.moderate\",\"subscription_version\":\"1\"},\"payload\":{\"subscription\":{\"id\":\"09482491-882d-479a-ad54-b45b8cb7c14c\",\"status\":\"enabled\",\"type\":\"channel.moderate\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"520593641\",\"moderator_user_id\":\"946933663\"},\"transport\":{\"method\":\"websocket\",\"session_id\":\"AgoQc73Y5j05SzKCGeDI79dngRIGY2VsbC1i\"},\"created_at\":\"2024-06-15T02:09:51.382733083Z\",\"cost\":0},\"event\":{\"broadcaster_user_id\":\"520593641\",\"broadcaster_user_login\":\"theplebdev\",\"broadcaster_user_name\":\"theplebdev\",\"moderator_user_id\":\"520593641\",\"moderator_user_login\":\"theplebdev\",\"moderator_user_name\":\"theplebdev\",\"action\":\"slowoff\",\"followers\":null,\"slow\":null,\"vip\":null,\"unvip\":null,\"mod\":null,\"unmod\":null,\"ban\":null,\"unban\":null,\"timeout\":null,\"untimeout\":null,\"raid\":null,\"unraid\":null,\"delete\":null,\"automod_terms\":null,\"unban_request\":null}}}"

        val subscribersOnlyString ="{\"metadata\":{\"message_id\":\"bDvc1KOhpHgOCHAUNWVmllDLDWD4SY2BizmhLmbZxK0=\",\"message_type\":\"notification\",\"message_timestamp\":\"2024-06-16T17:00:09.35426471Z\",\"subscription_type\":\"channel.moderate\",\"subscription_version\":\"1\"},\"payload\":{\"subscription\":{\"id\":\"07a6ce98-5afd-4148-8636-372ce9a4e30c\",\"status\":\"enabled\",\"type\":\"channel.moderate\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"520593641\",\"moderator_user_id\":\"946933663\"},\"transport\":{\"method\":\"websocket\",\"session_id\":\"AgoQLAd-3Yi_SVmoLHdSRCXFCBIGY2VsbC1i\"},\"created_at\":\"2024-06-16T17:00:00.922161673Z\",\"cost\":0},\"event\":{\"broadcaster_user_id\":\"520593641\",\"broadcaster_user_login\":\"theplebdev\",\"broadcaster_user_name\":\"theplebdev\",\"moderator_user_id\":\"520593641\",\"moderator_user_login\":\"theplebdev\",\"moderator_user_name\":\"theplebdev\",\"action\":\"subscribers\",\"followers\":null,\"slow\":null,\"vip\":null,\"unvip\":null,\"mod\":null,\"unmod\":null,\"ban\":null,\"unban\":null,\"timeout\":null,\"untimeout\":null,\"raid\":null,\"unraid\":null,\"delete\":null,\"automod_terms\":null,\"unban_request\":null}}}"
        val slowModeString ="{\"metadata\":{\"message_id\":\"U5cBONBn1cfheY4OwQ50eZj42YaIMABNvD27uMM82xg=\",\"message_type\":\"notification\",\"message_timestamp\":\"2024-06-15T02:10:01.447485974Z\",\"subscription_type\":\"channel.moderate\",\"subscription_version\":\"1\"},\"payload\":{\"subscription\":{\"id\":\"09482491-882d-479a-ad54-b45b8cb7c14c\",\"status\":\"enabled\",\"type\":\"channel.moderate\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"520593641\",\"moderator_user_id\":\"946933663\"},\"transport\":{\"method\":\"websocket\",\"session_id\":\"AgoQc73Y5j05SzKCGeDI79dngRIGY2VsbC1i\"},\"created_at\":\"2024-06-15T02:09:51.382733083Z\",\"cost\":0},\"event\":{\"broadcaster_user_id\":\"520593641\",\"broadcaster_user_login\":\"theplebdev\",\"broadcaster_user_name\":\"theplebdev\",\"moderator_user_id\":\"520593641\",\"moderator_user_login\":\"theplebdev\",\"moderator_user_name\":\"theplebdev\",\"action\":\"slow\",\"followers\":null,\"slow\":{\"wait_time_seconds\":20},\"vip\":null,\"unvip\":null,\"mod\":null,\"unmod\":null,\"ban\":null,\"unban\":null,\"timeout\":null,\"untimeout\":null,\"raid\":null,\"unraid\":null,\"delete\":null,\"automod_terms\":null,\"unban_request\":null}}}"
        val followerModeString = "{\"metadata\":{\"message_id\":\"y881L-dGe73Mh8y-HZxinAsPGmRmuPM316Dj8fM-qEU=\",\"message_type\":\"notification\",\"message_timestamp\":\"2024-06-15T02:09:58.8733187Z\",\"subscription_type\":\"channel.moderate\",\"subscription_version\":\"1\"},\"payload\":{\"subscription\":{\"id\":\"09482491-882d-479a-ad54-b45b8cb7c14c\",\"status\":\"enabled\",\"type\":\"channel.moderate\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"520593641\",\"moderator_user_id\":\"946933663\"},\"transport\":{\"method\":\"websocket\",\"session_id\":\"AgoQc73Y5j05SzKCGeDI79dngRIGY2VsbC1i\"},\"created_at\":\"2024-06-15T02:09:51.382733083Z\",\"cost\":0},\"event\":{\"broadcaster_user_id\":\"520593641\",\"broadcaster_user_login\":\"theplebdev\",\"broadcaster_user_name\":\"theplebdev\",\"moderator_user_id\":\"520593641\",\"moderator_user_login\":\"theplebdev\",\"moderator_user_name\":\"theplebdev\",\"action\":\"followers\",\"followers\":{\"follow_duration_minutes\":10},\"slow\":null,\"vip\":null,\"unvip\":null,\"mod\":null,\"unmod\":null,\"ban\":null,\"unban\":null,\"timeout\":null,\"untimeout\":null,\"raid\":null,\"unraid\":null,\"delete\":null,\"automod_terms\":null,\"unban_request\":null}}}"
        val emoteOnlyString = "{\"metadata\":{\"message_id\":\"b1sMNwsPjyrmJg408vyvkyn7u1fgnMTAODS-LyMM4cg=\",\"message_type\":\"notification\",\"message_timestamp\":\"2024-06-15T02:09:55.631015594Z\",\"subscription_type\":\"channel.moderate\",\"subscription_version\":\"1\"},\"payload\":{\"subscription\":{\"id\":\"09482491-882d-479a-ad54-b45b8cb7c14c\",\"status\":\"enabled\",\"type\":\"channel.moderate\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"520593641\",\"moderator_user_id\":\"946933663\"},\"transport\":{\"method\":\"websocket\",\"session_id\":\"AgoQc73Y5j05SzKCGeDI79dngRIGY2VsbC1i\"},\"created_at\":\"2024-06-15T02:09:51.382733083Z\",\"cost\":0},\"event\":{\"broadcaster_user_id\":\"520593641\",\"broadcaster_user_login\":\"theplebdev\",\"broadcaster_user_name\":\"theplebdev\",\"moderator_user_id\":\"520593641\",\"moderator_user_login\":\"theplebdev\",\"moderator_user_name\":\"theplebdev\",\"action\":\"emoteonly\",\"followers\":null,\"slow\":null,\"vip\":null,\"unvip\":null,\"mod\":null,\"unmod\":null,\"ban\":null,\"unban\":null,\"timeout\":null,\"untimeout\":null,\"raid\":null,\"unraid\":null,\"delete\":null,\"automod_terms\":null,\"unban_request\":null}}}"

        val removedBlockedTermString ="{\"metadata\":{\"message_id\":\"IPg11ah82OVpS3OEsXnMK5pY5CmRqC76iqaUt8x5XAU=\",\"message_type\":\"notification\",\"message_timestamp\":\"2024-06-15T01:32:58.203468323Z\",\"subscription_type\":\"channel.moderate\",\"subscription_version\":\"1\"},\"payload\":{\"subscription\":{\"id\":\"f8a9a4b9-fe76-4c7b-96c1-ea01154a3a59\",\"status\":\"enabled\",\"type\":\"channel.moderate\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"520593641\",\"moderator_user_id\":\"946933663\"},\"transport\":{\"method\":\"websocket\",\"session_id\":\"AgoQVuAqrkV9TdG3g9-nAdB9iRIGY2VsbC1i\"},\"created_at\":\"2024-06-15T01:32:52.275225334Z\",\"cost\":0},\"event\":{\"broadcaster_user_id\":\"520593641\",\"broadcaster_user_login\":\"theplebdev\",\"broadcaster_user_name\":\"theplebdev\",\"moderator_user_id\":\"520593641\",\"moderator_user_login\":\"theplebdev\",\"moderator_user_name\":\"theplebdev\",\"action\":\"remove_blocked_term\",\"followers\":null,\"slow\":null,\"vip\":null,\"unvip\":null,\"mod\":null,\"unmod\":null,\"ban\":null,\"unban\":null,\"timeout\":null,\"untimeout\":null,\"raid\":null,\"unraid\":null,\"delete\":null,\"automod_terms\":{\"action\":\"remove\",\"list\":\"blocked\",\"terms\":[\"poop\"],\"from_automod\":false},\"unban_request\":null}}}"
        val addedBlockedTermString="{\"metadata\":{\"message_id\":\"2tHN-8G41kDWK64Oo9lYq-F0CkZ4HMwdWi7fhqm37Bk=\",\"message_type\":\"notification\",\"message_timestamp\":\"2024-06-15T01:33:02.81749915Z\",\"subscription_type\":\"channel.moderate\",\"subscription_version\":\"1\"},\"payload\":{\"subscription\":{\"id\":\"f8a9a4b9-fe76-4c7b-96c1-ea01154a3a59\",\"status\":\"enabled\",\"type\":\"channel.moderate\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"520593641\",\"moderator_user_id\":\"946933663\"},\"transport\":{\"method\":\"websocket\",\"session_id\":\"AgoQVuAqrkV9TdG3g9-nAdB9iRIGY2VsbC1i\"},\"created_at\":\"2024-06-15T01:32:52.275225334Z\",\"cost\":0},\"event\":{\"broadcaster_user_id\":\"520593641\",\"broadcaster_user_login\":\"theplebdev\",\"broadcaster_user_name\":\"theplebdev\",\"moderator_user_id\":\"520593641\",\"moderator_user_login\":\"theplebdev\",\"moderator_user_name\":\"theplebdev\",\"action\":\"add_blocked_term\",\"followers\":null,\"slow\":null,\"vip\":null,\"unvip\":null,\"mod\":null,\"unmod\":null,\"ban\":null,\"unban\":null,\"timeout\":null,\"untimeout\":null,\"raid\":null,\"unraid\":null,\"delete\":null,\"automod_terms\":{\"action\":\"add\",\"list\":\"blocked\",\"terms\":[\"fuckering\"],\"from_automod\":false},\"unban_request\":null}}}"

        val deleteMessageString ="{\"metadata\":{\"message_id\":\"b9spiGqjUwkPJN8g3IzzLPcIdv23oQS0QJEAekxoVjs=\",\"message_type\":\"notification\",\"message_timestamp\":\"2024-06-15T01:24:25.495312842Z\",\"subscription_type\":\"channel.moderate\",\"subscription_version\":\"1\"},\"payload\":{\"subscription\":{\"id\":\"59de02b6-f5ce-475b-add6-80140f0d8dd3\",\"status\":\"enabled\",\"type\":\"channel.moderate\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"520593641\",\"moderator_user_id\":\"946933663\"},\"transport\":{\"method\":\"websocket\",\"session_id\":\"AgoQYXhyszZeR1eAJJhZJ32PqxIGY2VsbC1i\"},\"created_at\":\"2024-06-15T01:24:03.49526361Z\",\"cost\":0},\"event\":{\"broadcaster_user_id\":\"520593641\",\"broadcaster_user_login\":\"theplebdev\",\"broadcaster_user_name\":\"theplebdev\",\"moderator_user_id\":\"946933663\",\"moderator_user_login\":\"themodymoder\",\"moderator_user_name\":\"themodymoder\",\"action\":\"delete\",\"followers\":null,\"slow\":null,\"vip\":null,\"unvip\":null,\"mod\":null,\"unmod\":null,\"ban\":null,\"unban\":null,\"timeout\":null,\"untimeout\":null,\"raid\":null,\"unraid\":null,\"delete\":{\"user_id\":\"949335660\",\"user_login\":\"meanermeeny\",\"user_name\":\"meanermeeny\",\"message_id\":\"0df8b1ce-66e1-4a38-9f52-80069b8ad15d\",\"message_body\":\"this is another things\"},\"automod_terms\":null,\"unban_request\":null}}}"
        val unbanString ="{\"metadata\":{\"message_id\":\"x6RpLLyj1dtneG4mCbKTtEkl1QT6MJ8tKvwlNCHEcsM=\",\"message_type\":\"notification\",\"message_timestamp\":\"2024-06-15T00:46:26.101384517Z\",\"subscription_type\":\"channel.moderate\",\"subscription_version\":\"1\"},\"payload\":{\"subscription\":{\"id\":\"134bd9b2-6378-45f2-9468-0c4cd74c512c\",\"status\":\"enabled\",\"type\":\"channel.moderate\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"520593641\",\"moderator_user_id\":\"946933663\"},\"transport\":{\"method\":\"websocket\",\"session_id\":\"AgoQMhZeSyr6TqO1ssTFs8-xFhIGY2VsbC1i\"},\"created_at\":\"2024-06-15T00:46:03.008965225Z\",\"cost\":0},\"event\":{\"broadcaster_user_id\":\"520593641\",\"broadcaster_user_login\":\"theplebdev\",\"broadcaster_user_name\":\"theplebdev\",\"moderator_user_id\":\"520593641\",\"moderator_user_login\":\"theplebdev\",\"moderator_user_name\":\"theplebdev\",\"action\":\"unban\",\"followers\":null,\"slow\":null,\"vip\":null,\"unvip\":null,\"mod\":null,\"unmod\":null,\"ban\":null,\"unban\":{\"user_id\":\"949335660\",\"user_login\":\"meanermeeny\",\"user_name\":\"meanermeeny\"},\"timeout\":null,\"untimeout\":null,\"raid\":null,\"unraid\":null,\"delete\":null,\"automod_terms\":null,\"unban_request\":null}}}"
        val banString = "{\"metadata\":{\"message_id\":\"YDdg85oBqVnoeuet2rQyJgVszWzecmCz-HTEf7TlLnA=\",\"message_type\":\"notification\",\"message_timestamp\":\"2024-06-15T00:46:15.668477176Z\",\"subscription_type\":\"channel.moderate\",\"subscription_version\":\"1\"},\"payload\":{\"subscription\":{\"id\":\"134bd9b2-6378-45f2-9468-0c4cd74c512c\",\"status\":\"enabled\",\"type\":\"channel.moderate\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"520593641\",\"moderator_user_id\":\"946933663\"},\"transport\":{\"method\":\"websocket\",\"session_id\":\"AgoQMhZeSyr6TqO1ssTFs8-xFhIGY2VsbC1i\"},\"created_at\":\"2024-06-15T00:46:03.008965225Z\",\"cost\":0},\"event\":{\"broadcaster_user_id\":\"520593641\",\"broadcaster_user_login\":\"theplebdev\",\"broadcaster_user_name\":\"theplebdev\",\"moderator_user_id\":\"520593641\",\"moderator_user_login\":\"theplebdev\",\"moderator_user_name\":\"theplebdev\",\"action\":\"ban\",\"followers\":null,\"slow\":null,\"vip\":null,\"unvip\":null,\"mod\":null,\"unmod\":null,\"ban\":{\"user_id\":\"949335660\",\"user_login\":\"meanermeeny\",\"user_name\":\"meanermeeny\",\"reason\":\"stinks\"},\"unban\":null,\"timeout\":null,\"untimeout\":null,\"raid\":null,\"unraid\":null,\"delete\":null,\"automod_terms\":null,\"unban_request\":null}}}"
        val timeoutString ="{\"metadata\":{\"message_id\":\"Cth4OcXFrhfVCHk3o0Yr_FxBsW-TzEmXY_8KoMKDyww=\",\"message_type\":\"notification\",\"message_timestamp\":\"2024-06-14T16:14:21.056265756Z\",\"subscription_type\":\"channel.moderate\",\"subscription_version\":\"1\"},\"payload\":{\"subscription\":{\"id\":\"92bdf45e-8427-4c07-98bb-97362a2e5a0c\",\"status\":\"enabled\",\"type\":\"channel.moderate\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"520593641\",\"moderator_user_id\":\"946933663\"},\"transport\":{\"method\":\"websocket\",\"session_id\":\"AgoQ-1V029d3Q-Owp90na8K3HhIGY2VsbC1i\"},\"created_at\":\"2024-06-14T16:14:04.804257892Z\",\"cost\":0},\"event\":{\"broadcaster_user_id\":\"520593641\",\"broadcaster_user_login\":\"theplebdev\",\"broadcaster_user_name\":\"theplebdev\",\"moderator_user_id\":\"520593641\",\"moderator_user_login\":\"theplebdev\",\"moderator_user_name\":\"theplebdev\",\"action\":\"timeout\",\"followers\":null,\"slow\":null,\"vip\":null,\"unvip\":null,\"mod\":null,\"unmod\":null,\"ban\":null,\"unban\":null,\"timeout\":{\"user_id\":\"949335660\",\"user_login\":\"meanermeeny\",\"user_name\":\"meanermeeny\",\"reason\":\"\",\"expires_at\":\"2024-06-14T19:39:37.444980148Z\"},\"untimeout\":null,\"raid\":null,\"unraid\":null,\"delete\":null,\"automod_terms\":null,\"unban_request\":null}}}"

        whenAction(
            modActionParsing.parseActionFromString(subscribersOnlyOffString),
            subscribersOnlyOffString
        )

        Assert.assertEquals(1, 2)
    }




    fun whenAction(action:String?,stringToParse: String){
        when(action){
            "untimeout" ->{
                //moderator name, user id, username
                modActionParsing.getModeratorUsername(stringToParse)
                modActionParsing.getUserId(stringToParse)
                modActionParsing.getUserName(stringToParse)
            }
            "timeout" ->{
                println("TIMEOUT ACTION")
                modActionParsing.getModeratorUsername(stringToParse)
                modActionParsing.getUserId(stringToParse)
                modActionParsing.getUserName(stringToParse)
                modActionParsing.getReason(stringToParse)
                modActionParsing.getExpiresAt(stringToParse)

            }
            "ban"->{
                println("BAN ACTION")
                modActionParsing.getModeratorUsername(stringToParse)
                modActionParsing.getUserId(stringToParse)
                modActionParsing.getUserName(stringToParse)
                modActionParsing.getReason(stringToParse)
            }
            "unban" ->{
                println("UNBAN ACTION")
                modActionParsing.getModeratorUsername(stringToParse)
                modActionParsing.getUserName(stringToParse)

            }
            "delete"->{
                println("DELETE ACTION")
                modActionParsing.getModeratorUsername(stringToParse)
                modActionParsing.getUserName(stringToParse)
                modActionParsing.getMessageBody(stringToParse)
            }

            "remove_blocked_term"->{
                println("REMOVED BLOCKED TERM ACTION")
                modActionParsing.getBlockedTerms(stringToParse)
                modActionParsing.getModeratorUsername(stringToParse)
            }

            "add_blocked_term"->{
                println("ADDED BLOCKED TERM ACTION")
                modActionParsing.getBlockedTerms(stringToParse)
                modActionParsing.getModeratorUsername(stringToParse)

            }
            "subscribers"->{
                println("subscribers")

            }
            "subscribersoff"->{
                println("subscribersoff")

            }
            "emoteonly"->{
                modActionParsing.getModeratorUsername(stringToParse)

            }
            "followers"->{
                modActionParsing.getModeratorUsername(stringToParse)
                modActionParsing.getFollowerTime(stringToParse)

            }
            "slow" ->{
                modActionParsing.getModeratorUsername(stringToParse)
                modActionParsing.getSlowModeTime(stringToParse)

            }
            "slowoff"->{
                modActionParsing.getModeratorUsername(stringToParse)

            }
            "followersoff"->{
                modActionParsing.getModeratorUsername(stringToParse)

            }
            "emoteonlyoff"->{
                modActionParsing.getModeratorUsername(stringToParse)

            }
            else ->{
                println("ACTION NULL")
            }

        }
    }

    @Test
    fun `parsing out the id and status`(){
        val EXPECTED_ID ="c6aa6c42-c8a6-479f-a14f-f5b01f5ab110"
        val EXPECTED_STATUS ="approved"
        val stringToParse ="{\"metadata\":{\"message_id\":\"9K4wz7BQu9apkuukQZ907WBx28k6US-83YMsPfu857U=\",\"message_type\":\"notification\",\"message_timestamp\":\"2024-10-10T00:06:43.226760688Z\",\"subscription_type\":\"channel.unban_request.resolve\",\"subscription_version\":\"1\"},\"payload\":{\"subscription\":{\"id\":\"e427191d-c26c-4689-86f7-8fc6046e7fbe\",\"status\":\"enabled\",\"type\":\"channel.unban_request.resolve\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"520593641\",\"moderator_user_id\":\"946933663\"},\"transport\":{\"method\":\"websocket\",\"session_id\":\"AgoQmj4Q67aDST-OjDGxAO1WshIGY2VsbC1i\"},\"created_at\":\"2024-10-10T00:05:35.362652503Z\",\"cost\":0},\"event\":{\"id\":\"c6aa6c42-c8a6-479f-a14f-f5b01f5ab110\",\"broadcaster_user_id\":\"520593641\",\"broadcaster_user_login\":\"theplebdev\",\"broadcaster_user_name\":\"theplebdev\",\"moderator_user_id\":\"946933663\",\"moderator_user_login\":\"themodymoder\",\"moderator_user_name\":\"themodymoder\",\"user_id\":\"949335660\",\"user_login\":\"meanermeeny\",\"user_name\":\"meanermeeny\",\"resolution_text\":\"ight you can come in\",\"status\":\"approved\"}}}"

        val result = transportParsing(stringToParse) ?: ""

        val id = parseResolveUnbanRequestId(result)
        val status = parseResolveUnbanRequestStatus(result)

        Assert.assertEquals(EXPECTED_STATUS, status)
        Assert.assertEquals(EXPECTED_ID, id)
    }

    fun parseResolveUnbanRequestId(stringToParse:String):String?{
        val pattern = "event:\\{id:([^,]+)".toRegex()
        val messageId = pattern.find(stringToParse)?.groupValues?.get(1)
        return messageId
    }
    fun parseResolveUnbanRequestStatus(stringToParse:String):String?{
        val pattern = "status:([^}]+)".toRegex()
        val messageId = pattern.find(stringToParse)?.groupValues?.get(1)
        return messageId
    }
    fun transportParsing(stringToParse:String):String?{
        val pattern =""""transport"\s*:\s*\{(.+)""".toRegex()
        val transportData = pattern.find(stringToParse)?.groupValues?.get(1)
        val parsedMessageId = transportData?.replace("\"","")

        return parsedMessageId
    }


    @Test
    fun `parsing data from create unban request event`(){
        val stringToParse ="{\"metadata\":{\"message_id\":\"Gm9U4sI-jrnh4GTrusz472ce7FoVXdHP_jg8RMy7oZw=\",\"message_type\":\"notification\",\"message_timestamp\":\"2024-10-10T21:55:51.614276009Z\",\"subscription_type\":\"channel.unban_request.create\",\"subscription_version\":\"1\"},\"payload\":{\"subscription\":{\"id\":\"976f1f12-7fc2-428e-9921-85adfae6c54c\",\"status\":\"enabled\",\"type\":\"channel.unban_request.create\",\"version\":\"1\",\"condition\":{\"broadcaster_user_id\":\"520593641\",\"moderator_user_id\":\"946933663\"},\"transport\":{\"method\":\"websocket\",\"session_id\":\"AgoQnBzFNAvRSluMoMMaQEZtpBIGY2VsbC1i\"},\"created_at\":\"2024-10-10T21:55:17.309037491Z\",\"cost\":0},\"event\":{\"id\":\"bf21f90f-3026-4877-8cff-76f66273f785\",\"broadcaster_user_id\":\"520593641\",\"broadcaster_user_login\":\"theplebdev\",\"broadcaster_user_name\":\"theplebdev\",\"user_id\":\"949335660\",\"user_login\":\"meanermeeny\",\"user_name\":\"meanermeeny\",\"text\":\"please man. just let me back in. I won't be doing it again\",\"created_at\":\"2024-10-10T21:55:51.614276009Z\"}}}"

        //please oh please let me in!!!!!
        val parsedEventData =parseEventData(stringToParse)?:""
        println("data ---> $parsedEventData")
//        println("name ---> ${parseBroadcasterNameData(parsedEventData)}")
        val item = UnbanRequestItem(
            id = parseIdData(parsedEventData),
            broadcaster_name= parseBroadcasterNameData(parsedEventData),
            broadcaster_login="",
            broadcaster_id="",
            moderator_id=null,
            moderator_login=null,
            moderator_name=null,
            user_id = parseUserIdData(parsedEventData),
            user_login = parseUserNameData(parsedEventData),
            user_name =parseUserNameData(parsedEventData),
            text=parseTextData(parsedEventData),
            status = "pending",
            created_at = parseCreatedAtData(parsedEventData).split("T")[0],
            resolved_at = null,
            resolution_text = null
        )
        println("user_id   ---> ${item.id}")
        println("user_login ---> ${item.user_login}")
        println("user_name ---> ${item.user_name}")
        println("text      ---> ${item.text}")
        println("status    ---> ${item.status}")
        println("createdAt ---> ${item.created_at}")


        Assert.assertEquals(1, 2)
    }

    fun parseEventData(stringToParse:String):String?{
        val pattern =""""event"\s*:\s*\{([^}]+)""".toRegex()
        val messageId = pattern.find(stringToParse)?.groupValues?.get(1)
        return messageId?.replace("\"","")
    }
    fun parseIdData(stringToParse:String):String{
        val pattern = "id:([^,]+)".toRegex()
        val messageId = pattern.find(stringToParse)?.groupValues?.get(1)?.replace(" ","")
        return messageId?:""
    }
    fun parseBroadcasterNameData(stringToParse:String):String{
        val pattern = "broadcaster_user_name:([^,]+)".toRegex()
        val messageId = pattern.find(stringToParse)?.groupValues?.get(1)
        return messageId?:""
    }
    fun parseUserIdData(stringToParse:String):String{
        val pattern = "user_id:([^,]+)".toRegex()
        val messageId = pattern.find(stringToParse)?.groupValues?.get(1)
        return messageId?:""
    }
    fun parseUserNameData(stringToParse:String):String{
        val pattern = "\\buser_name:([^,]+)".toRegex()
        val messageId = pattern.find(stringToParse)?.groupValues?.get(1)
        return messageId?:""
    }
    fun parseTextData(stringToParse:String):String{
       // val pattern = "text:([^,]+)".toRegex()
      //  val pattern = """text:([^,]+)""".toRegex()
        val pattern = "text:(.*?)(?:,created_at|$)".toRegex()
        val messageId = pattern.find(stringToParse)?.groupValues?.get(1)
        return messageId?:""
    }

    fun parseCreatedAtData(stringToParse:String):String{
        val pattern = "created_at:([^,]+)".toRegex()
        val messageId = pattern.find(stringToParse)?.groupValues?.get(1)
        return messageId?:""
    }







}


