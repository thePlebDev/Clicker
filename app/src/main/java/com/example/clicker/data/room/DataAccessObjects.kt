package com.example.clicker.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.clicker.network.clients.TopGame
import kotlinx.coroutines.flow.Flow


/**
 * - **PinnedItemsDAO** is the interface that acts as the main Data Access Object for the underlying room database
 * - you can read more about data access objects, [HERE](https://developer.android.com/training/data-storage/room/accessing-data)
 *
 * @property getAllPinnedItems a [Flow] containing a [MutableList] of [PinnedItem] objects. Meant to represent clicked
 * category
 *
 * @property insertPinnedItem a function, when call with a [PinnedItem] object, is meant to store said object in the internal system
 * @property deletePinnedItem a function, when call with a [PinnedItem] object, is meant to remove said object in the internal system
 * */
@Dao
interface PinnedItemsDAO {
    @Query("SELECT * FROM pinned")
    fun getAllPinnedItems(): Flow<MutableList<PinnedItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPinnedItem( pinnedItem: PinnedItem)

    @Delete
    fun deletePinnedItem( pinnedItem: PinnedItem)

}