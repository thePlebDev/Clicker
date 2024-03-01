package com.example.clicker.presentation.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.domain.TwitchDataStore
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.presentation.AuthenticationEvent
import com.example.clicker.presentation.authentication.AuthenticationUIState
import com.example.clicker.services.NetworkMonitorService
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.NetworkResponse
import com.example.clicker.util.Response
import com.example.clicker.util.logCoroutineInfo
import com.example.clicker.util.mapWithRetry
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
data class AuthenticationUIState(

    val showLoginButton: Boolean = true,

    val logoutError: Boolean = false,

    val authenticationCode: String = "", //this is the oAuthToken
    val clientId: String = "",
    val userId: String = "",

    val authenticated: Boolean = false,

    val showErrorModal: Boolean = false,

    val showLoginModal: Boolean = false,
    val modalText: String = "Login to continue"

)
/**
 * StreamInfo is a data class that represents all the information that is shown to the user when their followed streams
 * are fetched
 *
 * */
data class StreamInfo(
    val streamerName: String,
    val streamTitle: String,
    val gameTitle: String,
    val views: Int,
    val url: String,
    val broadcasterId: String
)
data class HomeUIState(

    val hideModal: Boolean = false,
    val width: Int = 0,
    val aspectHeight: Int = 0,
    val screenDensity: Float = 0f,
    val streamersListLoading: NetworkResponse<Boolean> = NetworkResponse.Loading,
    val showLoginModal: Boolean = false,
    val domainIsRegistered: Boolean = false,
    val oAuthToken: String = "",

    val networkConnectionState:Boolean = true,

    val offlineModChannelList:List<String> =listOf(),
    val liveModChannelList:List<StreamData> = listOf(),
    val modChannelResponseState:Response<Boolean> = Response.Loading,
    val modRefreshing:Boolean = false,
    val modChannelShowBottomModal:Boolean = false,

    val homeRefreshing:Boolean = false,
    val homeNetworkErrorMessage:String ="Disconnected from network",
    val logoutDialogIsOpen:Boolean=false,




)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val twitchRepoImpl: TwitchRepo,
    private val ioDispatcher: CoroutineDispatcher,
    private val tokenDataStore: TwitchDataStore,
    private val authentication: TwitchAuthentication,
) : ViewModel() {

    private val _newUrlList = MutableStateFlow<List<StreamData>?>(null)
    val newUrlList: StateFlow<List<StreamData>?> = _newUrlList

    private var _uiState: MutableState<HomeUIState> = mutableStateOf(HomeUIState())
    val state: State<HomeUIState> = _uiState

    private var _authenticationUIState: MutableState<AuthenticationUIState> = mutableStateOf(
        AuthenticationUIState()
    )
    val authenticationUIState: State<AuthenticationUIState> = _authenticationUIState





    private val _validatedUser = MutableStateFlow<ValidatedUser?>(null)
    val validatedUser = _validatedUser
    private val _oAuthToken = MutableStateFlow<String?>(null)
    val oAuthToken:String? =  _oAuthToken.value
    /**BELOW IS THE NETWORK REQUEST BUILDER*/

    fun hideLogoutDialog(){
        _uiState.value = _uiState.value.copy(
            logoutDialogIsOpen = false
        )
    }
    fun showLogoutDialog(){
        _uiState.value = _uiState.value.copy(
            logoutDialogIsOpen = true
        )
    }
    //    init {
    //TESTING THE MACRO BENCH MARK TESTING
//        val list = StreamData(
//            "","","","",
//            "","","","",0,"",
//            "","", listOf(""),listOf(""),false
//        )
//        val listOfStreamData = mutableListOf<StreamData>()
//        for (i in 1..31) {
//            listOfStreamData.add(list.copy(userId = (list.userId.toInt() + 1).toString()))
//        }
//        _newUrlList.tryEmit(listOfStreamData)
//        _uiState.value = _uiState.value.copy(
//            streamersListLoading = NetworkResponse.Success(true)
//        )
//    }


    fun beginLogout(clientId: String,oAuthToken: String) = viewModelScope.launch {
//
        _authenticationUIState.value = _authenticationUIState.value.copy(
            showLoginModal = true,
            modalText = "Logging out..."

        )
        withContext(ioDispatcher + CoroutineName("BeginLogout")) {
            authentication.logout(
                clientId = clientId,
                token = oAuthToken
            )
                .collect { response ->
                    when (response) {
                        is NetworkAuthResponse.Loading -> {
                            _uiState.value = _uiState.value.copy(
                                modChannelResponseState = Response.Loading,
                                streamersListLoading = NetworkResponse.Loading
                            )
                        }
                        is NetworkAuthResponse.Success -> {
                            _uiState.value = _uiState.value.copy(
                                streamersListLoading = NetworkResponse.Failure(
                                    Exception("Success! Login with Twitch")
                                ),
                                showLoginModal = true,
                                homeRefreshing = false,

                                modChannelResponseState = Response.Failure(
                                    Exception("Success! Login with Twitch")
                                ),
                                modChannelShowBottomModal = true,
                                modRefreshing = false,
                            )
                            _validatedUser.value = null
                        }
                        is NetworkAuthResponse.Failure -> {
                            _uiState.value = _uiState.value.copy(
                                networkConnectionState = false,
                                homeNetworkErrorMessage = "Logout failed"
                            )
                            delay(2000)

                            _uiState.value = _uiState.value.copy(
                                networkConnectionState = true,
                            )
                        }
                        is NetworkAuthResponse.NetworkFailure->{
                            _uiState.value = _uiState.value.copy(
                                networkConnectionState = false,
                                homeNetworkErrorMessage = "Network Error"
                            )
                            delay(2000)

                            _uiState.value = _uiState.value.copy(
                                networkConnectionState = true,
                            )
                        }
                        is NetworkAuthResponse.Auth401Failure ->{
                            _uiState.value = _uiState.value.copy(
                                networkConnectionState = false,
                                homeNetworkErrorMessage = "Logout failed"
                            )
                            delay(2000)

                            _uiState.value = _uiState.value.copy(
                                networkConnectionState = true,
                            )
                        }


                    }
                }
        }
    }





    /**
     * refreshFromConnection is a private function that will get called when [monitorForNetworkConnection] detects a
     * reconnection to the network. First it will get the locally stored OAuth token, then if [validatedUser] is
     * not null [getLiveStreams] is called. If [validatedUser] is null then [validateOAuthToken] is run.
     * */
    private fun refreshFromConnection(){
        viewModelScope.launch {
            tokenDataStore.getOAuthToken().collect{oAuthToken ->
                if(oAuthToken.length > 2 ){
                    when(validatedUser){
                        null ->{
                            validateOAuthToken(oAuthToken)
                        }
                        else ->{
                            getLiveStreams(
                                clientId = _validatedUser.value?.clientId ?:"",
                                userId = _validatedUser.value?.clientId ?:"",
                                oAuthToken =oAuthToken
                            )
                        }
                    }
                }


            }
        }
    }


    fun registerDomian(isRegistered: Boolean) {
        _uiState.value = _uiState.value.copy(
            domainIsRegistered = isRegistered
        )
    }

    init{
        monitorForOAuthToken()
    }
    init {
        monitorForValidatedUser()
    }
    init{
        getOAuthToken()
    }
    init{
        monitorNewList()
    }

    fun pullToRefreshModChannels(){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                modRefreshing = true,
            )
            if(_uiState.value.modChannelShowBottomModal ){
                runFakeRequest()
            }
            else if(_validatedUser.value?.clientId == null){
                validateOAuthToken(_oAuthToken.value ?: "")
                Log.d("pullToRefreshModChannels","clientId is null")
            }

            else{
                Log.d("pullToRefreshModChannels","getLiveStreams()")
                getLiveStreams(
                    clientId = _validatedUser.value?.clientId ?:"",
                    userId = _validatedUser.value?.userId ?:"",
                    oAuthToken = _oAuthToken.value ?: "",
                )
            }



        }
    }

    private fun pullToRefreshHome(){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                homeRefreshing = true,
            )
            if(_uiState.value.showLoginModal ){
                runFakeRequest()
            }

            else if(_validatedUser.value?.clientId == null){
                validateOAuthToken(_oAuthToken.value ?: "")
            }
            else{
                getLiveStreams(
                    clientId = _validatedUser.value?.clientId ?:"",
                    userId = _validatedUser.value?.userId ?:"",
                    oAuthToken = _oAuthToken.value ?: "",
                )
            }





        }
    }

    /**
     * runFakeRequest() is a suspending function used to simulate a request to the servers. It is called from
     * [pullToRefreshModChannels]
     * - This function should only get called if _uiState.value.modChannelShowBottomModal is set to true, which means that
     * the user has not authenticated with Twitch yet.
     * */
    private suspend fun runFakeRequest(){
        _uiState.value = _uiState.value.copy(
            modChannelShowBottomModal = false,
            showLoginModal = false,
        )
        delay(1000)
        _uiState.value = _uiState.value.copy(
            modRefreshing = false,
            modChannelShowBottomModal = true,

            showLoginModal = true,
            homeRefreshing = false

        )
    }

    private fun monitorNewList() {
        viewModelScope.launch {
            _newUrlList.collect{streamList ->
                streamList?.let{nonNullableStreamList ->
                    getModeratedChannels(
                        oAuthToken = _oAuthToken.value ?: "",
                        clientId = _validatedUser.value?.clientId ?:"",
                        userId = _validatedUser.value?.userId ?:""
                    )

                }
            }
        }
    }
    private fun getModeratedChannels(
        oAuthToken: String,
        clientId:String,
        userId: String
    ){
        viewModelScope.launch {
            withContext(ioDispatcher){
//                delay(5000)
                twitchRepoImpl.getModeratedChannels(
                    authorizationToken = oAuthToken,
                    clientId = clientId,
                    userId = userId
                ).collect{response ->
                    Log.d("getModeratedChannels","RESPONSE -> $response")

                    when(response){
                        is NetworkAuthResponse.Loading ->{}
                        is NetworkAuthResponse.Success ->{

                            val offlineModList = mutableListOf<String>()
                            val onlineList = mutableListOf<StreamData>()
                            for(modChannel in response.data.data ){
                                offlineModList.add(modChannel.broadcasterName)
                                _newUrlList.value?.forEach {
                                    if(it.userName == modChannel.broadcasterName){
                                        onlineList.add(it)
                                        offlineModList.remove(modChannel.broadcasterName)
                                    }
                                }

                            }
                            _uiState.value = _uiState.value.copy(
                                offlineModChannelList = offlineModList,
                                liveModChannelList = onlineList,
                                modChannelResponseState = Response.Success(true),
                                modRefreshing = false
                            )
                        }
                        is NetworkAuthResponse.Failure ->{
                            Log.d("getModeratedChannels","RESPONSE -> FAILURE")
                            _uiState.value = _uiState.value.copy(
                                modChannelResponseState = Response.Failure(Exception("Error! Pull to refresh")),
                                modRefreshing = false
                            )
                        }
                        is NetworkAuthResponse.NetworkFailure ->{
                            _uiState.value = _uiState.value.copy(
                                homeNetworkErrorMessage="Network error",
                                networkConnectionState =false,
                                modRefreshing = false
                            )
                            delay(3000)
                            _uiState.value = _uiState.value.copy(
                                networkConnectionState =true
                            )

                        }
                        is NetworkAuthResponse.Auth401Failure ->{
                            _uiState.value = _uiState.value.copy(
                                modChannelResponseState = Response.Failure(Exception("Login with Twitch")),
                                modChannelShowBottomModal = true,
                                modRefreshing = false
                            )
                        }
                    }
                }
            }
        }
    }

/**
 * monitorForValidatedUser is a private function that upon the initialization of this viewModel is meant to monitor the [_validatedUser] hot flow for any non null values
 * to be emitted. Once a non null value is emitted to [_validatedUser] this function will then call [getLiveStreams]
 * */
    private fun monitorForValidatedUser(){
        viewModelScope.launch {
            _validatedUser.collect{nullableValidatedUser ->
                nullableValidatedUser?.also{nonNullValidatedUser ->
                    Log.d("nullableValidatedUser","RUNNING")
                    getLiveStreams(
                        clientId = nonNullValidatedUser.clientId,
                        userId = nonNullValidatedUser.userId,
                        oAuthToken = _uiState.value.oAuthToken
                    )
                }
            }
        }
    }

    /**
     * monitorForOAuthToken is a private function that upon the initialization of this viewModel is meant to monitor the [_oAuthToken] hot flow for any non null values. Once
     * a new non null value is emitted to [_oAuthToken], [validateOAuthToken] will be called
     * */
    private fun monitorForOAuthToken(){
        viewModelScope.launch {
            _oAuthToken.collect{nullableOAuthToken ->
                nullableOAuthToken?.also { nonNullOAuthToken ->
                    validateOAuthToken(nonNullOAuthToken)
                }

            }
        }
    }


    /**
     * getOAuthToken is a private function that upon the initialization of this viewModel is meant to try and retrieve the locally
     * stored oAuth token from [tokenDataStore]. If successful the oAuth token is emitted to [_oAuthToken]. If a oAuth token
     * is not found then the user is notified by telling them they need to sign in
     * */
    private fun getOAuthToken() = viewModelScope.launch {
        tokenDataStore.getOAuthToken().collect { storedOAuthToken ->
            Log.d("monitorForNetworkConnection","getOAuthToken  ---> TOKKEN:$storedOAuthToken")


            if (storedOAuthToken.length > 2) {
                //need to call the validateToken
                //this should emit a value to a HOT storedOAuthToken flow which then runs the validateOAuthToken
                _oAuthToken.tryEmit(storedOAuthToken)


            } else {
                _uiState.value = _uiState.value.copy(
                    streamersListLoading = NetworkResponse.Failure(
                        Exception("You're new! Please login with Twitch")
                    ),
                    showLoginModal = true
                )


            }


        }
    }
    /**
     * setOAuthToken is a function called to set the locally stored authentication token
     *
     * @param oAuthToken a string representing the authentication token that is to be stored locally
     */
    fun setOAuthToken(oAuthToken: String) = viewModelScope.launch {
        // need to make a call to exchange the authCode for a validationToken
        _uiState.value = _uiState.value.copy(
            showLoginModal = false,
        )
        Log.d("setOAuthToken", "token -> $oAuthToken")
        tokenDataStore.setOAuthToken(oAuthToken)
        _oAuthToken.tryEmit(oAuthToken)
        _uiState.value = _uiState.value.copy(
            modChannelShowBottomModal = false,
            showLoginModal = false,
            modChannelResponseState = Response.Loading

        )
    }

    /**
     * The second method to be called in the authentication flow.
     * This function is used to make a request to Twitch's API and validate the oAuthenticationToken
     * */
    private fun validateOAuthToken(
        oAuthenticationToken: String
    ) = viewModelScope.launch {
        withContext(ioDispatcher + CoroutineName("TokenValidator")) {
            authentication.validateToken("https://id.twitch.tv/oauth2/validate",oAuthenticationToken)
                .collect { response ->
                    Log.d("monitorForNetworkConnection","validateOAuthTokenResponse ->${response}")

                when (response) {
                    is NetworkAuthResponse.Loading -> {
                        // the loading state is to be left empty because its initial state is loading
                    }
                    is NetworkAuthResponse.Success -> {
                        logCoroutineInfo("CoroutineDebugging", "GOT ITEMS from remote")
                        Log.d("VALIDATINGTOKEN", "clientId --> ${response.data.clientId}")
                        Log.d("VALIDATINGTOKEN", "userId --> ${response.data.userId}")

                        _uiState.value = _uiState.value.copy(
                            oAuthToken = oAuthenticationToken,
                        )

                        _validatedUser.tryEmit(response.data)


                        // I think we need the below for the streamViewModel
                        //todo:THIS SHOULD GET REMOVED. TOO MUCH IS GOING ON INSIDE OF THIS FUNCTION
                        tokenDataStore.setUsername(response.data.login)
                    }
                    is NetworkAuthResponse.Failure -> {
                        Log.d("VALIDATINGTOKEN", "TOKEN ---> FAILED.....")

                        _uiState.value = _uiState.value.copy(
                            streamersListLoading = NetworkResponse.Failure(
                                Exception("Error! Pull refresh")
                            ),
                            homeRefreshing = false,
                            modRefreshing = false

                        )
                    }
                    is NetworkAuthResponse.NetworkFailure ->{
                        Log.d("VALIDATINGTOKEN", "TOKEN ---> NetworkFailure.....")
                        _uiState.value = _uiState.value.copy(
                            homeNetworkErrorMessage="Network error  ",
                            networkConnectionState =false,
                            homeRefreshing = false,
                            modRefreshing = false,
                            streamersListLoading = NetworkResponse.NetworkFailure(Exception("failed"))
                        )
                        delay(2000)
                        _uiState.value = _uiState.value.copy(
                            networkConnectionState =true
                        )
                    }
                    is NetworkAuthResponse.Auth401Failure ->{
                        Log.d("VALIDATINGTOKEN", "TOKEN ---> Auth401Failure.....")
                        _uiState.value = _uiState.value.copy(
                            streamersListLoading = NetworkResponse.Failure(
                                Exception("Error! Re-login with Twitch")
                            ),
                            showLoginModal = true,
                            homeRefreshing = false,

                            modChannelResponseState = Response.Failure(
                                Exception("Error! Re-login with Twitch")
                            ),
                            modChannelShowBottomModal = true,
                            modRefreshing = false
                        )
                    }
                }
            }
        }
    } // end validateOAuthToken

    // THIS IS THE END

    //todo: This should just call the getFollowedLiveStreams() method
    fun pullToRefreshGetLiveStreams() {
        viewModelScope.launch {
            withContext(ioDispatcher + CoroutineName("GetLiveStreamsPull")) {
                pullToRefreshHome()
            }
        }
    }

    suspend fun getLiveStreams(
        clientId: String,
        userId: String,
        oAuthToken: String
    ) {
        try {
            withContext(Dispatchers.IO + CoroutineName("GetLiveStreams")) {

                twitchRepoImpl.getFollowedLiveStreams(
                    authorizationToken = oAuthToken,
                    clientId = clientId,
                    userId = userId
                ).collect { response ->
                    when (response) {
                        is NetworkAuthResponse.Loading -> {
                        }
                        is NetworkAuthResponse.Success -> {
                            val liveStreamLists = response.data
                            Log.d(
                                "AuthenticationViewModelGetLiveStreams",
                                "size -> ${liveStreamLists.size}"
                            )

                            val replacedWidthHeightList = response.data.map {
                                it.changeUrlWidthHeight(
                                    _uiState.value.width,
                                    _uiState.value.aspectHeight
                                )
                            }

                            _uiState.value = _uiState.value.copy(
                                streamersListLoading = NetworkResponse.Success(true),
                                modRefreshing = false,
                                homeRefreshing = false
                            )
                            _newUrlList.tryEmit(replacedWidthHeightList)
                        }
                        // end
                        is NetworkAuthResponse.Failure -> {
                            _uiState.value = _uiState.value.copy(
                                modRefreshing = false,
                                homeRefreshing = false,
                                streamersListLoading = NetworkResponse.Failure(
                                    Exception("Error! Pull refresh")
                                )
                            )
                        }
                        is NetworkAuthResponse.NetworkFailure ->{
                            _uiState.value = _uiState.value.copy(
                                homeNetworkErrorMessage="Network error",
                                networkConnectionState =false,
                                modRefreshing = false,
                                homeRefreshing = false
                            )
                            delay(2000)
                            _uiState.value = _uiState.value.copy(
                                networkConnectionState =true
                            )
                        }
                        is NetworkAuthResponse.Auth401Failure->{
                            _uiState.value = _uiState.value.copy(
                                streamersListLoading = NetworkResponse.Failure(
                                    Exception("Error! Re-login with Twitch")
                                ),
                                homeRefreshing = false,
                                showLoginModal = true,

                                modChannelResponseState = Response.Failure(Exception("Error! Re-login with Twitch")),
                                modChannelShowBottomModal = true,
                                modRefreshing = false
                            )

                        }
                    }
                }
            }
        } catch (e: IOException) {
        }

    }

    fun updateAspectWidthHeight(width: Int, aspectHeight: Int,screenDensity:Float) {

        _uiState.value = _uiState.value.copy(
            aspectHeight = aspectHeight,
            width = width,
            screenDensity =screenDensity
        )
    }
    fun updateUrlWidthHeight(aspectWidth: Int, aspectHeight: Int){
        val replacedWidthHeightList = _newUrlList.value?.map {
            it.changeUrlWidthHeight(
                aspectWidth,
                aspectHeight
            )
        }

        _newUrlList.tryEmit(replacedWidthHeightList)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("onclearedCalled","DEATH TO US ALL")
    }


}

/***END OF VIEWMODEL**/

fun StreamData.changeUrlWidthHeight(aspectWidth: Int, aspectHeight: Int): StreamData {

    return copy(
        thumbNailUrl = thumbNailUrl.replace("{width}", "$aspectWidth")
            .replace("{height}", "$aspectHeight")
    )
}

data class MainBusState(
    val oAuthToken: String? = null,
    val authUser: ValidatedUser? = null
)
