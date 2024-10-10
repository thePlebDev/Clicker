package com.example.clicker.network.domain

import androidx.compose.runtime.State
import com.example.clicker.network.clients.UnbanRequestItem
import com.example.clicker.network.models.twitchStream.ChatSettingsData
import com.example.clicker.network.repository.util.AutoModMessageUpdate
import com.example.clicker.network.repository.util.AutoModQueueMessage
import com.example.clicker.network.websockets.ResolvedUnBanRequestStatusNId

import com.example.clicker.presentation.modView.ModActionData
import kotlinx.coroutines.flow.StateFlow


/**
 * TwitchEventSubscriptionWebSocket is the interface that acts as the API for all the methods needed to interact with Twitch's
 * EventSub Web Socket
 * - You can read more about it [HERE](https://dev.twitch.tv/docs/eventsub/manage-subscriptions/)
 *
 * @property parsedSessionId a [StateFlow] object containing a String that represents the id sent in the initial welcoming message from
 * the Twitch server. You can read more about the welcome message [HERE](https://dev.twitch.tv/docs/eventsub/handling-websocket-events/#welcome-message)
 * @property autoModMessageQueue a [StateFlow] object containing the most recent [AutoModQueueMessage] object sent from the Twitch server
 * @property messageIdForAutoModQueue a [StateFlow] object containing the most recent [AutoModMessageUpdate] object sent from the Twitch server
 * @property updatedChatSettingsData a [StateFlow] object containing the most recent [ChatSettingsData] object sent from the Twitch server
 * @property modActions a [StateFlow] object containing the most recent [ModActionData] object sent from the Twitch server
 *
 * @property newWebSocket()
 * @property closeWebSocket()
 *
 * */
interface TwitchEventSubscriptionWebSocket {

    val parsedSessionId: StateFlow<String?>
    val autoModMessageQueue: StateFlow<AutoModQueueMessage?>
    val messageIdForAutoModQueue: StateFlow<AutoModMessageUpdate?>
    val updatedChatSettingsData: StateFlow<ChatSettingsData?>
    val mostRecentResolvedUnbanRequest: StateFlow<ResolvedUnBanRequestStatusNId?>
    val mostRecentUnbanRequest: StateFlow<UnbanRequestItem?>

    val modActions: StateFlow<ModActionData?>


    /**
     * a function that when called will create a new Web Socket
     * */
    fun newWebSocket():Unit

    /**
     * a function that when called will close a existing Web Socket
     * */
    fun closeWebSocket():Unit
}