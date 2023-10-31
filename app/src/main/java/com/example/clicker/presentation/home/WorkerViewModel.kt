package com.example.clicker.presentation.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.work.WorkInfo
import com.example.clicker.data.TokenDataStore
import com.example.clicker.data.TokenValidationWorker
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.AuthenticatedUser
import com.example.clicker.network.models.StreamData
import com.example.clicker.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow

data class WorkerUIState(

    val streamStatus: Response<List<StreamData>> = Response.Loading,
    val authStatus: String = "Checking if token is available",
    val testingState: Response<String> = Response.Loading,
    val loggingOut: Response<Boolean>? = null
)

@HiltViewModel
class WorkerViewModel @Inject constructor(
    private val tokenValidationWorker: TokenValidationWorker,
    private val tokenDataStore: TokenDataStore,
    private val twitchRepoImpl: TwitchRepo
) : ViewModel() {

    // TODO: MAKE THIS A DO NOTHING CLASS THAT WILL JUST KEEP RUNNING EVER HOUR. TO SATISFY THE TWITCH DOCS

    private var _uiState: MutableState<WorkerUIState> = mutableStateOf(WorkerUIState())
    val state: State<WorkerUIState> = _uiState

    private val _oAuthUserToken: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _AuthenticatedUser: MutableStateFlow<AuthenticatedUser?> = MutableStateFlow(null)
    var liveDataWork: LiveData<WorkInfo>? = null

    private fun runWorkManager(oAuthToken: String) {
        liveDataWork = tokenValidationWorker.enqueueRequest(oAuthToken)
    }
}

data class MainStates(
    val oAuthToken: String? = null,
    val authUser: AuthenticatedUser? = null
)