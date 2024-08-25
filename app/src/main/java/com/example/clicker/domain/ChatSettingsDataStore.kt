package com.example.clicker.domain

import kotlinx.coroutines.flow.Flow

interface ChatSettingsDataStore {

    suspend fun setBadgeSize(badgeSize: Float)

    fun getBadgeSize(): Flow<Float>

    suspend fun setUsernameSize(badgeSize: Float)

    fun getUsernameSize(): Flow<Float>

    suspend fun setMessageSize(badgeSize: Float)

    fun getMessageSize(): Flow<Float>

    suspend fun setLineHeight(badgeSize: Float)

    fun getLineHeight(): Flow<Float>

    suspend fun setCustomUsernameColor(showCustomUsernameColor: Boolean)

    fun getCustomUsernameColor(): Flow<Boolean>







}