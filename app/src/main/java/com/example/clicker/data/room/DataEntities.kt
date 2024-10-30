package com.example.clicker.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PinnedItem(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "name") val firstName: String,
    @ColumnInfo(name = "box_art_url") val boxArtUrl: String
)