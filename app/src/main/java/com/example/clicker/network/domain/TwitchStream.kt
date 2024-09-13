package com.example.clicker.network.domain

import com.example.clicker.network.clients.BanUser
import com.example.clicker.network.clients.ChannelInformation
import com.example.clicker.network.clients.TwitchClient
import com.example.clicker.network.clients.WarnUserBody
import com.example.clicker.network.models.twitchStream.AutoModSettings
import com.example.clicker.network.models.twitchStream.BanUserResponse
import com.example.clicker.network.models.twitchStream.ChatSettings
import com.example.clicker.network.models.twitchStream.IndividualAutoModSettings
import com.example.clicker.network.models.twitchStream.UpdateChatSettings
import com.example.clicker.util.Response
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Query


/**
 * TwitchStream is the interface that acts as the API for all the methods needed to interact with Twitch's Stream API
 *
 * @property getChatSettings a function meant to get the chat settings of the stream currently views
 * @property updateChatSettings a function meant to update the currently viewed chat settings
 * @property deleteChatMessage a function meant to delete a specific chat message
 * @property banUser a function meant to ban a specific user
 * @property unBanUser a function meant to unban a specific user
 * @property getAutoModSettings a function meant to get the AutoMod settings of the currently viewed stream
 * @property updateAutoModSettings a function meant to update the AutoMod settings of the currently viewed stream
 * */

interface TwitchStream {

    /**
     * - getChatSettings represents a GET method. A function meant to get the chat settings of the stream currently views
     *
     * @param oAuthToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param broadcasterId a String used to represent the unique identifier of the streamer being currently viewed
     * */
    suspend fun getChatSettings(oAuthToken: String, clientId: String, broadcasterId: String): Flow<Response<ChatSettings>>

    /**
     * - updateChatSettings represents a PATCH method. a function meant to update the currently viewed chat settings
     *
     * @param oAuthToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param broadcasterId a String used to represent the unique identifier of the streamer being currently viewed
     * @param moderatorId A String used to represent the unique identifier of the current user and their moderator abilities
     * @param body a [UpdateChatSettings] object that represents the new settings
     * */
    suspend fun updateChatSettings(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String,
        moderatorId: String,
        body: UpdateChatSettings
    ): Flow<Response<Boolean>>


    /**
     * - deleteChatMessage represents a DELETE method. a function meant to delete a specific chat message
     *
     * @param oAuthToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param broadcasterId a String used to represent the unique identifier of the streamer being currently viewed
     * @param moderatorId A String used to represent the unique identifier of the current user and their moderator abilities
     * @param messageId a String used to represent the unique identifier of the message to be deleted
     * */
    suspend fun deleteChatMessage(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String,
        moderatorId: String,
        messageId: String

    ): Flow<Response<Boolean>>


    /**
     * - banUser represents a POST method. a function meant to ban a specific user
     *
     * @param oAuthToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param broadcasterId a String used to represent the unique identifier of the streamer being currently viewed
     * @param moderatorId A String used to represent the unique identifier of the current user and their moderator abilities
     * @param body a [BanUser] object meant to represent details of the ban and the user to be banned
     * */
    suspend fun banUser(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String,
        moderatorId: String,
        body: BanUser
    ): Flow<Response<BanUserResponse>>

    /**
     * - unBanUser represents a POST method. a function meant to unban a specific user
     *
     * @param oAuthToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param broadcasterId a String used to represent the unique identifier of the streamer being currently viewed
     * @param moderatorId A String used to represent the unique identifier of the current user and their moderator abilities
     * @param userId a String representing the Id of the user to be unbanned with this method call
     * */
    suspend fun unBanUser(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String,
        moderatorId: String,
        userId: String

    ): Flow<Response<Boolean>>

    /**
     * - warnUser represents a POST method. a function meant to implement Twitch's new warn feature
     * - read more about the warn feature, [HERE](https://dev.twitch.tv/docs/api/reference/#warn-chat-user)
     *
     * @param authorizationToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param broadcasterId a String used to represent the unique identifier of the streamer being currently viewed
     * @param moderatorId A String used to represent the unique identifier of the current user and their moderator abilities
     * @param body a [WarnUserBody] object that is used to hold the user_id of the user being warned and the reason they are
     * being warned
     * */
    suspend fun warnUser(
        oAuthToken: String,
        clientId: String,
        moderatorId: String,
        broadcasterId: String,
        body: WarnUserBody

    ): Flow<Response<Boolean>>




    /**
     * - getAutoModSettings represents a GET method. a function meant to get the AutoMod settings of the currently viewed stream
     *
     * @param oAuthToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param broadcasterId a String used to represent the unique identifier of the streamer being currently viewed
     * @param moderatorId A String used to represent the unique identifier of the current user and their moderator abilities
     * */
    suspend fun getAutoModSettings(
        oAuthToken: String,
        clientId: String,
        broadcasterId: String,
        moderatorId: String,
    ):Flow<Response<AutoModSettings>>


    /**
     * - updateAutoModSettings represents a PUT method. a function meant to update the AutoMod settings of the currently viewed stream
     *
     * @param oAuthToken a String used to represent the OAuth token that uniquely identifies this user's granted abilities
     * @param clientId a String used to represent the clientId(unique identifier) of this application
     * @param autoModSettings A [AutoModSettings] object used to represent the new updated AutoMod settings for the channel
     * */
    suspend fun updateAutoModSettings(
        oAuthToken: String,
        clientId: String,
        autoModSettings: IndividualAutoModSettings
    ):Flow<Response<AutoModSettings>>




}