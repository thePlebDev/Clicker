package com.example.clicker.presentation.stream.views.chat.chatSettings

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ChatSettingsViewModel @Inject constructor(): ViewModel() {




    private val _badgeSize = mutableStateOf(20f)  // Initial value
    val badgeSize: State<Float> = _badgeSize

    fun changeBadgeSize(newValue:Float){
        Log.d("changeBadgeSizeViewMode","newValue ->${newValue}")
        _badgeSize.value = newValue
    }

}