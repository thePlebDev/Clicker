package com.example.clicker.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.clicker.network.clients.TopGame

@Entity(tableName = "pinned")
data class PinnedItem(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "name") val firstName: String,
    @ColumnInfo(name = "box_art_url") val boxArtUrl: String,
    @ColumnInfo(name = "igdb_id") val igdbId: String,
    @ColumnInfo(name = "clicked") val clicked: Boolean
)

fun PinnedItem.toTopGame(): TopGame {
    return TopGame(
        id = uid.toString(),
        name = firstName,
        box_art_url = boxArtUrl,
        igdb_id = igdbId,
        clicked = clicked
    )
}

fun TopGame.toPinnedItem(): PinnedItem {
    return PinnedItem(
        uid = id.toInt(),
        firstName = name,
        boxArtUrl = box_art_url,
        igdbId = igdb_id,
        clicked = clicked
    )
}

