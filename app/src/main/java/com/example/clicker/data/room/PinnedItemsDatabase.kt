package com.example.clicker.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [PinnedItem::class], version = 1, exportSchema = false)

public abstract class PinnedItemRoomDatabase : RoomDatabase(){

    //For each DAO class that is associated with the database,
    // the database class must define an abstract method that has zero arguments and returns an instance of the DAO class.
    abstract fun pinnedItemsDAO(): PinnedItemsDAO

    companion object{
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: PinnedItemRoomDatabase? = null //companion property

        fun getDatabase(context: Context): PinnedItemRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this){

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PinnedItemRoomDatabase::class.java,
                    "pinned_item_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }
}