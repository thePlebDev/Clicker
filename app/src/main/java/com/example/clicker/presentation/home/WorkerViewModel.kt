package com.example.clicker.presentation.home

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.ViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkManager
import com.example.clicker.data.TokenValidationWorker
import com.example.clicker.data.workManager.OAuthTokeValidationWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WorkerViewModel @Inject constructor(
    tokenValidationWorker: TokenValidationWorker
): ViewModel() {

    val validationWorker = tokenValidationWorker.enqueuedWork

}