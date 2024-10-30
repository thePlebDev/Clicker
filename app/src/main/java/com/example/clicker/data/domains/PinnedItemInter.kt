package com.example.clicker.data.domains

import androidx.annotation.WorkerThread
import com.example.clicker.data.room.PinnedItem
import com.example.clicker.network.clients.TopGame
import kotlinx.coroutines.flow.Flow

interface PinnedItemInter {

    val getAllPinnedItems: Flow<MutableList<PinnedItem>>

    suspend fun insertPinnedItem(pinnedItem: PinnedItem)

    fun deletePinnedItem( pinnedItem: PinnedItem)
}