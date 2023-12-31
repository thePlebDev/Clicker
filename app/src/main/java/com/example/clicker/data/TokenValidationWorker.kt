package com.example.clicker.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.clicker.data.workManager.OAuthTokeValidationWorker
import com.example.clicker.domain.TwitchTokenValidationWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TokenValidationWorker @Inject constructor(
    private val context: Context
): TwitchTokenValidationWorker {

    private val uniqueId: String = "Validating"

    private val workManager = WorkManager.getInstance(context)

    override fun enqueueRequest(oAuthToken: String): LiveData<WorkInfo> {
        val workRequest = PeriodicWorkRequestBuilder<OAuthTokeValidationWorker>(
            1, // repeating interval
            TimeUnit.HOURS
        ) // todo:MAKE THIS PERIODIC
        val data = Data.Builder()
        data.putString("token", oAuthToken)
        workRequest.setInputData(data.build())

        val builtWorkRequest = workRequest.build()

        // workManager.enqueueUniqueWork(uniqueId, ExistingWorkPolicy.KEEP,builtWorkRequest)
        workManager.enqueueUniquePeriodicWork(
            uniqueId,
            ExistingPeriodicWorkPolicy.KEEP,
            builtWorkRequest
        )
        Log.d("ENQUEDID", "IT DO BE LIKE THAT SOMETIMES")
        return workManager.getWorkInfoByIdLiveData(builtWorkRequest.id)
    }
}