package com.example.clicker.data.repos

import androidx.annotation.WorkerThread
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.clicker.data.domains.PinnedItemInter
import com.example.clicker.data.room.PinnedItem
import com.example.clicker.data.room.PinnedItemsDAO
import com.example.clicker.network.clients.TopGame
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PinnedItemRepo @Inject constructor(
    private val pinnedItemDao: PinnedItemsDAO
    ): PinnedItemInter {



    //Room executes all the queries on a separate thread
    // Observed Flow will notify the observer when the data has changed
    override val getAllPinnedItems: Flow<MutableList<PinnedItem>> = pinnedItemDao.getAllPinnedItems();


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    override suspend fun insertPinnedItem(pinnedItem: PinnedItem) {
        //suspend tells the compiler the this needs to be called from a coroutine or another suspending function
        pinnedItemDao.insertPinnedItem(pinnedItem)
    }


    @WorkerThread
    override fun deletePinnedItem(pinnedItem: PinnedItem){
        pinnedItemDao.deletePinnedItem(pinnedItem)
    }

}