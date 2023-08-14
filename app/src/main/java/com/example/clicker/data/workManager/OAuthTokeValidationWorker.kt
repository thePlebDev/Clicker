package com.example.clicker.data.workManager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.util.Response
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

@HiltWorker
class OAuthTokeValidationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @Assisted private val twitchRepoImpl: TwitchRepo
): CoroutineWorker( appContext,workerParams) {


    override suspend fun doWork(): Result {


        val response = twitchRepoImpl.validateToken("FAILED TOKEN")
          .drop(1) // skip the first emission of LOADING
          .firstOrNull() // will catch either SUCCESS OF FAILURE


//        return when(response){
//            is Response.Loading -> {
//                Log.d("observeForeversWorker","LOADING")
//                Result.success()
//            }
//            is Response.Success -> {
//                Log.d("observeForeversWorker","SUCCESS")
//                Result.success()
//            }
//            is Response.Failure -> {
//                Log.d("observeForeversWorker","FAILED")
//                Result.failure()
//            }
//            else -> Result.failure()
//        }
        return Result.success()
    }

}

class CustomWorker(
    appContext: Context,
    workerParams: WorkerParameters,
):Worker(appContext,workerParams){
    override fun doWork(): Result {
        Log.d("OAuthTokeValidationWorker","IT IS RUNNING")
        return Result.success()
    }

}