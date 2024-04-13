package com.example.clicker.network.domain

import com.example.clicker.network.clients.BlockedTerm
import com.example.clicker.network.clients.ManageAutoModMessage
import com.example.clicker.network.clients.ModViewChatSettings
import com.example.clicker.network.models.twitchStream.ChatSettings
import com.example.clicker.network.models.twitchStream.ChatSettingsData
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * TwitchEventSubscriptions is the interface that is used to get information related to the TwitchWebSocket.
 * It currently has 3 abstract methods:
 *
 * @property createEventSubSubscription
 * @property manageAutoModMessage
 * @property getBlockedTerms
 * */
interface TwitchEventSubscriptions {

    /**
     * createEventSubSubscription() is called to create a websocket subscription. More info on subscriptions can be found,
     * [HERE](https://dev.twitch.tv/docs/eventsub/eventsub-subscription-types/)
     *
     * @param oAuthToken a token representing all the authentication scopes the logged in user has access to
     * @param clientId represents the identity of the user authentication
     * @param broadcasterId represents the broadcaster who's stream is currently being watched
     * @param moderatorId represents the user that is watching the stream
     * @param sessionId represents the websocket session
     * @param type represents the type of websocket subscription that we want to subscriber to
     * */
    fun createEventSubSubscription(
        oAuthToken:String,
        clientId:String,
        broadcasterId:String,
        moderatorId:String,
        sessionId:String,
        type:String,
    ): Flow<Response<Boolean>>

    /**
     * manageAutoModMessage() is called when the logged in user trys to deny or allow a automod queue message
     *
     * @param oAuthToken a token representing all the authentication scopes the logged in user has access to
     * @param clientId represents the identity of the user authentication
     * @param manageAutoModMessageData a [ManageAutoModMessage] object that represents what the user wants to do to the
     * current message
     * */
    fun manageAutoModMessage(
        oAuthToken:String,
        clientId:String,
        manageAutoModMessageData: ManageAutoModMessage
    ) : Flow<Response<Boolean>>


    /**
     * getBlockedTerms() is called to get a list of all the stream's blocked terms. More info can be found,
     * [HERE](https://dev.twitch.tv/docs/api/reference/#get-blocked-terms)
     *
     * @param oAuthToken a token representing all the authentication scopes the logged in user has access to
     * @param clientId represents the identity of the user authentication
     * @param broadcasterId represents the broadcaster who's stream is currently being watched
     * @param moderatorId represents the user that is watching the stream
     *
     * @return a list of [BlockedTerm] objects
     * */
    fun getBlockedTerms(
        oAuthToken:String,
        clientId:String,
        broadcasterId:String,
        moderatorId:String,
    ):Flow<Response<List<BlockedTerm>>>

    /**
     * deleteBlockedTerm() is called to remove an individual term from the broadcaster's blocked terms. More info can be found,
     * [HERE](https://dev.twitch.tv/docs/api/reference/#remove-blocked-term)
     *
     * @param oAuthToken a token representing all the authentication scopes the logged in user has access to
     * @param clientId represents the identity of the user authentication
     * @param broadcasterId represents the broadcaster who's stream is currently being watched
     * @param moderatorId represents the user that is watching the stream
     * @param id represents the ID of the blocked term to remove from the broadcaster’s list of blocked terms.
     *
     * @return a list of [BlockedTerm] objects
     * */
    fun deleteBlockedTerm(
        oAuthToken:String,
        clientId:String,
        broadcasterId:String,
        moderatorId:String,
        id:String,
    ):Flow<Response<Boolean>>


    /**
     * - getChatSettings represents a GET method. A function meant to get the chat settings of the currently viewed stream
     *
     * @param oAuthToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param broadcasterId a String used to represent the unique identifier of the streamer being currently viewed
     * */
    suspend fun getChatSettings(oAuthToken: String, clientId: String, broadcasterId: String): Flow<Response<ChatSettings>>


    fun updateModViewChatSettings(
        authorizationToken: String,
        clientId: String,
        broadcasterId: String,
        moderatorId: String,
        body: ChatSettingsData
    ): Flow<Response<ModViewChatSettings>>
}