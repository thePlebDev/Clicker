package com.example.clicker.presentation.home

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.clicker.data.TokenDataStore
import com.example.clicker.data.TokenValidationWorker
import com.example.clicker.data.workManager.OAuthTokeValidationWorker
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.AuthenticatedUser
import com.example.clicker.network.models.StreamData
import com.example.clicker.network.models.ValidatedUser
import com.example.clicker.util.Response
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkerUIState(

    val streamStatus:Response<List<StreamData>> = Response.Loading,
    val authStatus:String = "Checking if token is available",
    val testingState:Response<String> = Response.Loading,
    val loggingOut:Response<Boolean>? = null
)
@HiltViewModel
class WorkerViewModel @Inject constructor(
    private val tokenValidationWorker: TokenValidationWorker,
    private val tokenDataStore: TokenDataStore,
    private val twitchRepoImpl: TwitchRepo,
): ViewModel() {

    //TODO: MAKE THIS A DO NOTHING CLASS THAT WILL JUST KEEP RUNNING EVER HOUR. TO SATISFY THE TWITCH DOCS

    private var _uiState: MutableState<WorkerUIState> = mutableStateOf(WorkerUIState())
    val state: State<WorkerUIState> = _uiState


    private val _oAuthUserToken: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _AuthenticatedUser: MutableStateFlow<AuthenticatedUser?> = MutableStateFlow(null)
    var liveDataWork: LiveData<WorkInfo>? = null







    private fun runWorkManager(oAuthToken:String){

        liveDataWork= tokenValidationWorker.enqueueRequest(oAuthToken)

    }


}

data class MainStates(
    val oAuthToken:String? = null,
    val authUser:AuthenticatedUser? = null,
)