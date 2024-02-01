package com.example.clicker.services

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class NetworkMonitorViewModel (
    application: Application
): AndroidViewModel(application) {
    fun startService(){
        val context:Application = getApplication()
        context.startService(Intent(context, NetworkMonitorService::class.java))

    }
    override fun onCleared() {
        super.onCleared()
        val context:Application = getApplication()
        context.stopService(Intent(context, NetworkMonitorService::class.java))
        Log.d("NetworkMonitorViewModel","cleared")
    }
}