package com.example.clicker.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.clicker.network.clients.TopGame
/**
 * - **PinnedItem** is a data class that is used to be the base for a database [Entity] object. You
 * can read more about Entity classes, [HERE](https://developer.android.com/training/data-storage/room/defining-data)
 *
 * @param uid a Int that is used by the internal room database to uniquley identify the class
 * @param firstName A String used to represent the name of this pinned item
 * @param boxArtUrl a String used to hold the URL of the image that will be shown to the suer
 * @param igdbId a String used to identify this object inside of Twitch's system
 * @param clicked a Boolean used to determine if the user has double clicked this object or not
 *
 * */
@Entity(tableName = "pinned")
data class PinnedItem(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "name") val firstName: String,
    @ColumnInfo(name = "box_art_url") val boxArtUrl: String,
    @ColumnInfo(name = "igdb_id") val igdbId: String,
    @ColumnInfo(name = "clicked") val clicked: Boolean
)

/**
 * - **toTopGame** is an extension utility function used to turn [PinnedItem] objects into [TopGame] objects
 * */
fun PinnedItem.toTopGame(): TopGame {
    return TopGame(
        id = uid.toString(),
        name = firstName,
        box_art_url = boxArtUrl,
        igdb_id = igdbId,
        clicked = clicked
    )
}

/**
 * - **toTopGame** is an extension utility function used to turn [TopGame] objects into [PinnedItem] objects
 * */
fun TopGame.toPinnedItem(): PinnedItem {
    return PinnedItem(
        uid = id.toInt(),
        firstName = name,
        boxArtUrl = box_art_url,
        igdbId = igdb_id,
        clicked = clicked
    )
}

