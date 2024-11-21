package com.example.clicker.data.domains

import androidx.annotation.WorkerThread
import com.example.clicker.data.room.PinnedItem
import com.example.clicker.network.clients.TopGame
import kotlinx.coroutines.flow.Flow


/**
 * PinnedItemInter is the interface that acts as the API for all the methods needed to interact with internal storage system
 *
 * @property getAllPinnedItems a [Flow] containing a [MutableList] of [PinnedItem] objects. Meant to represent clicked
 * category
 *
 * @property insertPinnedItem a function, when call with a [PinnedItem] object, is meant to store said object in the internal system
 * @property deletePinnedItem a function, when call with a [PinnedItem] object, is meant to remove said object in the internal system
 * */
interface PinnedItemInter {

    val getAllPinnedItems: Flow<MutableList<PinnedItem>>

    suspend fun insertPinnedItem(pinnedItem: PinnedItem)

    fun deletePinnedItem( pinnedItem: PinnedItem)
}