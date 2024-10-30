package com.example.clicker.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

interface MyInterface {
    fun bar()
    fun foo() {
        // optional body
    }
}

@Dao
interface UserDao {
    @Query("SELECT *")
    fun getAll(): List<String>

}