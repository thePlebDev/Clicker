package com.example.clicker.workManager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class OAuthTokeValidationWorker(
    appContext: Context,
    workerParams: WorkerParameters
): Worker( appContext,workerParams) {


    override fun doWork(): Result {
        Log.d("OAuthTokeValidationWorker","IT IS RUNNING")
        return Result.success()
    }
}