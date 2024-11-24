package com.example.clicker.domain

import kotlinx.coroutines.flow.Flow

/**
 * ChatSettingsDataStore is the interface that acts as the API for all the methods needed to interact with the internaly stored values
 * related to the display of the chat UI
 *
 * @property setBadgeSize a function, when called with a Float, will determine how the Twitch badges will look to the user
 * @property getBadgeSize a function, when called, will return the size of the Badges to be shown to the user
 * @property setUsernameSize a function, when called with a Float, will determine how the usernames will look in chat
 * @property getUsernameSize a function, when called, will return the size of the Twitch usernames
 * @property setMessageSize a function, when called with a Float, will determine how the Twitch messages will look in chat
 * @property getMessageSize a function, when called, will return the size of all the messages in the chat
 * @property setEmoteSize a function, when called with a Float, will determine how the Emotes will look in the chat
 * @property getEmoteSize a function, when called, will return the size of all the Emotes in the chat
 * @property setLineHeight a function, when called with a Float, will determine the line height of the chat messages
 * @property getLineHeight a function, when called, will return the line height of all the messages inside of chat
 * @property setCustomUsernameColor a function, when called with a Boolean, will determine if the usernames should be their own
 * custom colors of the main color application
 * @property getCustomUsernameColor a function, when called, will return the conditional used to determine the color of the
 * usernames inside of the chat
 *
 * */
interface ChatSettingsDataStore {

    /**
     * - **setBadgeSize** is a function, when called with a Float,
     * will determine how the Twitch badges will look to the user
     * @param badgeSize a Float that represents the badge size
     * */
    suspend fun setBadgeSize(badgeSize: Float)

    /**
     * - **getBadgeSize** a function, when called, will return the size of the Badges to be shown to the user
     *
     * @return a [Flow] containing a Float that represents how the badge size will look to the user
     * */
    fun getBadgeSize(): Flow<Float>

    /**
     * - **setUsernameSize** a function, when called with a Float, will determine how the usernames will look in chat
     *
     * @param badgeSize a Float used to determine how the usernames will look in chat
     * */
    suspend fun setUsernameSize(badgeSize: Float)

    /**
     * - **getUsernameSize** a function, when called, will return the size of the Twitch usernames.
     *
     * @return a [Flow] containing a Float that represents how the username size will look to the user
     * */
    fun getUsernameSize(): Flow<Float>

    /**
     * - **setMessageSize** a function, when called with a Float, will determine how the Twitch messages will look in chat
     *
     * @param badgeSize a Float used to determine the size of the messages inside of the chat
     * */
    suspend fun setMessageSize(badgeSize: Float)

    /**
     * - **getMessageSize** a function, when called, will return the size of all the messages in the chat
     *
     * @return a [Flow] containing a Float that represents how the messages will look inside of the chat
     * */
    fun getMessageSize(): Flow<Float>

    /**
     * - **setEmoteSize** a function, when called with a Float, will determine how the Emotes will look in the chat
     *
     * @param emoteSize a Float used to determine the size of the Emotes inside of the chat
     * */
    suspend fun setEmoteSize(emoteSize: Float)

    /**
     * - **getEmoteSize** a function, when called, will return the size of all the Emotes in the chat
     *
     * @return a [Flow] containing a Float that represents how the Emotes will look in chat
     * */
    fun getEmoteSize(): Flow<Float>

    /**
     * - **setLineHeight** a function, when called with a Float, will determine the line height of the chat messages
     *
     * @param lineHeight a Float used to determine the height of the messages line height inside of the chat
     * */
    suspend fun setLineHeight(lineHeight: Float)

    /**
     * - **getLineHeight** a function, when called, will return the line height of all the messages inside of chat
     *
     * @return a [Flow] containing a Float that represent the line height of each message in the chat
     * */
    fun getLineHeight(): Flow<Float>

    /**
     * - **setCustomUsernameColor** a function, when called with a Boolean, will determine if the usernames should be their own
     * custom colors of the main color application
     *
     * @param showCustomUsernameColor a Boolean used to determine if the usernames should be a custom color or a uniform color
     * */
    suspend fun setCustomUsernameColor(showCustomUsernameColor: Boolean)


    /**
     * - **getCustomUsernameColor** a function, when called, will return the conditional used to determine the color of the
     * usernames inside of the chat
     *
     * @return a [Flow] containing a Boolean used to determine if the usernames should be a custom color or a uniform color
     *
     * */
    fun getCustomUsernameColor(): Flow<Boolean>







}