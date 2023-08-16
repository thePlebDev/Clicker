package com.example.clicker.data

import android.content.Context
import android.util.Log
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkInfo
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




    fun enqueueRequest(oAuthToken:String): LiveData<WorkInfo> {
        val workRequest = OneTimeWorkRequestBuilder<OAuthTokeValidationWorker>()
        val data = Data.Builder()
        data.putString("token",oAuthToken)
        workRequest.setInputData(data.build())

        val builtWorkRequest =workRequest.build()

        workManager.enqueueUniqueWork(uniqueId, ExistingWorkPolicy.KEEP,builtWorkRequest)
        val enqueuedWork = workManager.getWorkInfoByIdLiveData(builtWorkRequest.id)
        return enqueuedWork
    }

}