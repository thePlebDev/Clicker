package com.example.clicker.domain

import kotlinx.coroutines.flow.Flow

interface ChatSettingsDataStore {

    suspend fun setBadgeSize(badgeSize: Float)

    fun getBadgeSize(): Flow<Float>

}