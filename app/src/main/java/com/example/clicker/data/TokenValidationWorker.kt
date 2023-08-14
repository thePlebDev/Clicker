package com.example.clicker.data

import android.content.Context
import android.util.Log
import androidx.compose.runtime.livedata.observeAsState
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkManager
import com.example.clicker.data.workManager.OAuthTokeValidationWorker
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class TokenValidationWorker @Inject constructor(
    private val context: Context
) {
    private val uniqueId:String  ="Validating"

    private val workManager = WorkManager.getInstance(context)
    private val workRequest = OneTimeWorkRequestBuilder<OAuthTokeValidationWorker>().build()
    private val tokenValidationResult =workManager.enqueueUniqueWork(uniqueId, ExistingWorkPolicy.KEEP,workRequest)
    val enqueuedWork = workManager.getWorkInfoByIdLiveData(workRequest.id)

}