package com.example.clicker.domain

import androidx.lifecycle.LiveData
import androidx.work.WorkInfo

interface TwitchTokenValidationWorker {
    fun enqueueRequest(oAuthToken: String): LiveData<WorkInfo>
}