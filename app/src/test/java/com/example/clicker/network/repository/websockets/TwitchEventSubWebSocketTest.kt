package com.example.clicker.network.repository.websockets

import com.example.clicker.network.websockets.notificationTypeIsNotification
import com.example.clicker.network.websockets.notificationTypeIsWelcome
import com.example.clicker.network.websockets.parseAutoModQueueMessage
import com.example.clicker.network.websockets.parseEventSubWelcomeMessage
import com.example.clicker.network.websockets.parseMessageId
import com.example.clicker.network.websockets.parseStatusType
import com.example.clicker.network.websockets.parseSubscriptionType
import org.junit.Assert
import org.junit.Test

class TwitchEventSubWebSocketTest {


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
        val autoQueueMessage = parseAutoModQueueMessage(stringToParse)
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
        val messageId =parseMessageId(stringToParse)


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





}


