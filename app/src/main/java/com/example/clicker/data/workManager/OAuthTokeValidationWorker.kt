package com.example.clicker.data.workManager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.util.Response
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class OAuthTokeValidationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @Assisted private val twitchRepoImpl: TwitchAuthentication
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val token = inputData.getString("token")
        Log.d("doWorkToken", " doWorkToken --> $token")
        val response = twitchRepoImpl.validateToken(token!!)
            .drop(1) // skip the first emission of LOADING
            .firstOrNull() // will catch either SUCCESS OF FAILURE

        return when (response) {
            is Response.Loading -> {
                Log.d("observeForeversWorker", "LOADING")

                Result.success()
            }
            is Response.Success -> {
                Log.d("observeForeversWorker", "SUCCESS")
                Log.d("observeForeversWorker", response.data.toString())

                val serializedValue = Gson().toJson(response.data)
                val outputData = Data.Builder()
                    .putString("result_key", serializedValue)
                    .build()

                Result.success(outputData)
            }
            is Response.Failure -> {
                Log.d("observeForeversWorker", "FAILED")
                Result.failure()
            }
            else -> Result.failure()
        }
    }
}

class CustomWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        Log.d("OAuthTokeValidationWorker", "IT IS RUNNING")
        return Result.success()
    }
}