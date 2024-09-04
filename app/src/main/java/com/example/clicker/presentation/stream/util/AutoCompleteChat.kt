package com.example.clicker.presentation.stream.util

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import javax.inject.Inject

class AutoCompleteChat @Inject constructor() {


    private val _filteredChatList = mutableStateListOf<String>()
    val filteredChatList:List<String> = _filteredChatList

    private val _allChatters = mutableStateListOf<String>()
    val allChatters:List<String> = _allChatters

    private val _showFilteredUsernames = mutableStateOf(false)
    val showFilteredUsernames:State<Boolean> = _showFilteredUsernames


    fun addChatter(username: String) {
        if (!allChatters.contains(username)) {
            _allChatters.add(username)
        }
        Log.d("NEWCHATTERlIST", "username --> $username")
    }
    fun clearFilteredChatList(){
        _filteredChatList.clear()
    }
    fun addAllToFilteredChatList(usernameList:List<String>){
        _filteredChatList.clear()
        _filteredChatList.addAll(usernameList)

    }
    fun setShowFilteredUsernames(updatedValue:Boolean){
        _showFilteredUsernames.value =updatedValue
    }

}