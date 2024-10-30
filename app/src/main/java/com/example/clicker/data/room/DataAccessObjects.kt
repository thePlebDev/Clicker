package com.example.clicker.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.clicker.network.clients.TopGame
import kotlinx.coroutines.flow.Flow


@Dao
interface PinnedItemsDAO {
    @Query("SELECT * FROM pinned")
    fun getAllPinnedItems(): Flow<MutableList<PinnedItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPinnedItem( pinnedItem: PinnedItem)

    @Delete
    fun deletePinnedItem( pinnedItem: PinnedItem)

}