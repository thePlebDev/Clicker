package com.example.clicker.presentation.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.domain.TwitchDataStore
import com.example.clicker.network.clients.GetModChannelsData
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.network.domain.TwitchEmoteRepo
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.presentation.AuthenticationEvent
import com.example.clicker.presentation.authentication.AuthenticationUIState
import com.example.clicker.presentation.stream.util.NetworkMonitoring
import com.example.clicker.services.NetworkMonitorService
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.NetworkNewUserResponse
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * UserTypes is used to in the [determineUserType()][com.example.clicker.presentation.home.HomeViewModel.determineUserType] method
 * to determine the type of user the current user is
 * */
enum class UserTypes {
    NEW, RETURNING, LOGGEDOUT,
}
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
data class ModChannelUIState(
    val offlineModChannelList:List<String> =listOf(),
    val liveModChannelList:List<StreamData> = listOf(),
    val modChannelResponseState:NetworkNewUserResponse<Boolean> = NetworkNewUserResponse.Loading,
    val modRefreshing:Boolean = false,
)
data class HomeUIState(

    val width: Int = 0,
    val aspectHeight: Int = 0,
    val screenDensity: Float = 0f,
    val streamersListLoading: NetworkNewUserResponse<List<StreamData>> = NetworkNewUserResponse.Loading,
    val domainIsRegistered: Boolean = false,
    val oAuthToken: String = "",

    val networkConnectionState:Boolean = true,

    val homeRefreshing:Boolean = false,
    val homeNetworkErrorMessage:String ="Disconnected from network",
    val logoutDialogIsOpen:Boolean=false,
    val horizontalLongHoldStreamList:NetworkNewUserResponse<List<StreamData>> = NetworkNewUserResponse.Loading,
    val userIsLoggedIn:NetworkAuthResponse<Boolean> = NetworkAuthResponse.Loading,
    val showFailedDialog:Boolean = false,
    val showNetworkRefreshError:Boolean = false,

    )


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val twitchRepoImpl: TwitchRepo,
    private val ioDispatcher: CoroutineDispatcher,
    private val tokenDataStore: TwitchDataStore,
    private val authentication: TwitchAuthentication,
    private val networkMonitoring:NetworkMonitoring,
    private val twitchEmoteImpl: TwitchEmoteRepo,
) : ViewModel() {

    private var _uiState: MutableState<HomeUIState> = mutableStateOf(HomeUIState())
    val state: State<HomeUIState> = _uiState

    private var _modChannelUIState: MutableState<ModChannelUIState> = mutableStateOf(ModChannelUIState())
    val modChannelUIState: State<ModChannelUIState> = _modChannelUIState

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
    fun hideDialog(){
        _uiState.value = _uiState.value.copy(
            showFailedDialog = false
        )
    }


    /**Initial state monitoring
     * - These functions will monitor Hot StateFlows upon the creation of the HomeViewModel
     * */
    init{
        monitorForOAuthToken()
    }
    init {
        monitorForValidatedUser()
    }

    init{
        getOAuthToken()
    }


    fun beginLogout(clientId: String,oAuthToken: String) = viewModelScope.launch {
//
        withContext(ioDispatcher + CoroutineName("BeginLogout")) {
            authentication.logout(
                clientId = clientId,
                token = oAuthToken
            )
                .collect { response ->
                    when (response) {
                        is NetworkAuthResponse.Loading -> {
                            _uiState.value = _uiState.value.copy(
                                streamersListLoading = NetworkNewUserResponse.Loading,
                                showFailedDialog = false
                            )
                            _modChannelUIState.value =_modChannelUIState.value.copy(
                                modChannelResponseState = NetworkNewUserResponse.Loading,
                            )
                        }
                        is NetworkAuthResponse.Success -> {
                            _uiState.value = _uiState.value.copy(
                                userIsLoggedIn = response,
                                homeRefreshing = false,

                            )
                            _modChannelUIState.value =_modChannelUIState.value.copy(
                                modChannelResponseState = NetworkNewUserResponse.Auth401Failure(
                                    Exception("Success! Login with Twitch")
                                ),
                                modRefreshing = false,
                            )
                            _validatedUser.value = null
                        }
                        is NetworkAuthResponse.Failure -> {
                            _uiState.value = _uiState.value.copy(
                                userIsLoggedIn = response,
                                showFailedDialog = true
                                )
                        }
                        is NetworkAuthResponse.NetworkFailure->{
                            _uiState.value = _uiState.value.copy(
                                userIsLoggedIn = response,
                                showFailedDialog = true
                            )
                        }
                        is NetworkAuthResponse.Auth401Failure ->{
                            _uiState.value = _uiState.value.copy(
                                userIsLoggedIn = response,
                                showFailedDialog = true
                            )
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



    fun pullToRefreshModChannels(){
        viewModelScope.launch {
            _modChannelUIState.value = _modChannelUIState.value.copy(
                modRefreshing = true,
            )
             if(_validatedUser.value?.clientId == null){
                validateOAuthToken(_oAuthToken.value ?: "")
                Log.d("pullToRefreshModChannels","clientId is null")
            } else{
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

            if(_validatedUser.value?.clientId == null){
                validateOAuthToken(_oAuthToken.value ?: "")
            }
            else{
                getLiveStreams(
                    clientId = _validatedUser.value?.clientId ?:"",
                    userId = _validatedUser.value?.userId ?:"",
                    oAuthToken = _oAuthToken.value ?: "",
                )
                getGlobalEmote(_oAuthToken.value ?: "",_validatedUser.value?.clientId ?:"")
            }





        }
    }

    /**
     * runFakeRequest() is a suspending function used to simulate a request to the servers. It is called from
     * [pullToRefreshModChannels]
     * - This function should only get called if _uiState.value.modChannelShowBottomModal is set to true, which means that
     * the user has not authenticated with Twitch yet.
     * */

    private fun getModeratedChannels(
        oAuthToken: String,
        clientId:String,
        userId: String,
        liveFollowedStreamers:List<StreamData>
    ){
        viewModelScope.launch {
            withContext(ioDispatcher){
                twitchRepoImpl.getModeratedChannels(
                    authorizationToken = oAuthToken,
                    clientId = clientId,
                    userId = userId
                ).collect{response ->
                    Log.d("getModeratedChannels","RESPONSE -> $response")

                    when(response){
                        is NetworkAuthResponse.Loading ->{}
                        is NetworkAuthResponse.Success ->{

                            val responseData =response.data.data
                            val offlineModList = mutableListOf<String>()
                            val onlineList = mutableListOf<StreamData>()

                            val listOfModName = responseData.map{it.broadcasterName}
                            val listOfStreamerName = liveFollowedStreamers.map { it.userName }

                            for (name in listOfModName){
                                if(listOfStreamerName.contains(name)){
                                    val item = liveFollowedStreamers.first { it.userName == name }
                                    onlineList.add(item)
                                }else{
                                    val offlineItem = responseData.first{it.broadcasterName ==name}
                                    offlineModList.add(offlineItem.broadcasterName)
                                }
                            }
                            _modChannelUIState.value = _modChannelUIState.value.copy(
                                offlineModChannelList = offlineModList,
                                liveModChannelList = onlineList,
                                modChannelResponseState = NetworkNewUserResponse.Success(true),
                                modRefreshing = false
                            )
                        }
                        is NetworkAuthResponse.Failure ->{
                            Log.d("getModeratedChannels","RESPONSE -> FAILURE")

                            _modChannelUIState.value = _modChannelUIState.value.copy(
                                modChannelResponseState = NetworkNewUserResponse.Failure(Exception("Error! Pull to refresh")),
                                modRefreshing = false
                            )
                        }
                        is NetworkAuthResponse.NetworkFailure ->{
                            _uiState.value = _uiState.value.copy(
                                homeNetworkErrorMessage="Network error",
                                networkConnectionState =false,
                            )
                            _modChannelUIState.value = _modChannelUIState.value.copy(
                                modRefreshing = false
                            )
                            delay(3000)
                            _uiState.value = _uiState.value.copy(
                                networkConnectionState =true
                            )

                        }
                        is NetworkAuthResponse.Auth401Failure ->{
                            _modChannelUIState.value = _modChannelUIState.value.copy(
                                modChannelResponseState = NetworkNewUserResponse.Auth401Failure(Exception("Login with Twitch")),
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
                    getGlobalEmote(_uiState.value.oAuthToken,nonNullValidatedUser.clientId)
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

    private fun getGlobalEmote(oAuthToken:String,clientId: String) {
        //_validatedUser.value?.clientId ?:""
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                twitchEmoteImpl.getGlobalEmotes(
                    oAuthToken, clientId
                ).mapWithRetry(
                    action={
                        // result is the result from getGlobalEmotes()
                            result -> result
                    },
                    predicate = { result, attempt ->
                        val repeatResult = result is Response.Failure && attempt < 3
                        repeatResult
                    }
                ).collect{}
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
                    streamersListLoading = NetworkNewUserResponse.NewUser(
                        "New user! Login with Twitch"
                    ),
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

        Log.d("setOAuthToken", "token -> $oAuthToken")
        tokenDataStore.setOAuthToken(oAuthToken)
        _oAuthToken.tryEmit(oAuthToken)
    }

    /**
     * determineUserType() is a thread blocking function that is used to determine if the user is a new user, returning user or a
     * logged out user
     * @return a [UserTypes] object used to determine the current user's type
     * */
    fun determineUserType(): UserTypes = runBlocking(Dispatchers.IO) {
        val token = tokenDataStore.getOAuthToken().first()
        val loggedOut = false
        when {
            token.length < 2 -> UserTypes.NEW
            loggedOut -> UserTypes.LOGGEDOUT
            else -> UserTypes.RETURNING
        }
    }



    /**
     * The second method to be called in the authentication flow.
     * This function is used to make a request to Twitch's API and validate the oAuthenticationToken
     * */
    private fun validateOAuthToken(
        oAuthenticationToken: String
    ) = viewModelScope.launch {
        withContext(ioDispatcher + CoroutineName("TokenValidator")) {
            authentication.validateToken(oAuthenticationToken)
                .collect { response ->
                    Log.d("monitorForNetworkConnection","validateOAuthTokenResponse ->${response}")

                when (response) {
                    is NetworkNewUserResponse.Loading -> {
                        // the loading state is to be left empty because its initial state is loading
                        _uiState.value = _uiState.value.copy(
                            userIsLoggedIn = NetworkAuthResponse.Loading
                        )
                    }
                    is NetworkNewUserResponse.Success -> {

                        _uiState.value = _uiState.value.copy(
                            oAuthToken = oAuthenticationToken,
                            userIsLoggedIn = NetworkAuthResponse.Success(true)
                        )

                        _validatedUser.tryEmit(response.data)
                        Log.d("monitorForNetworkConnection","Login ->${response.data.login}")

                    }
                    is NetworkNewUserResponse.Failure -> {
                        Log.d("VALIDATINGTOKEN", "TOKEN ---> FAILED.....")

                        _uiState.value = _uiState.value.copy(
                            streamersListLoading = response,
                            homeRefreshing = false,
                        )
                        _modChannelUIState.value = _modChannelUIState.value.copy(
                            modRefreshing = false
                        )
                    }
                    is NetworkNewUserResponse.NetworkFailure ->{
                        Log.d("VALIDATINGTOKEN", "TOKEN ---> NetworkFailure.....")
                        _uiState.value = _uiState.value.copy(

                            homeRefreshing = false,
                            streamersListLoading = response
                        )
                        _modChannelUIState.value = _modChannelUIState.value.copy(
                            modRefreshing = false
                        )
                    }
                    is NetworkNewUserResponse.Auth401Failure ->{
                        Log.d("VALIDATINGTOKEN", "TOKEN ---> Auth401Failure.....")
                        _uiState.value = _uiState.value.copy(
                            streamersListLoading = response,
                            homeRefreshing = false,
                        )
                        _modChannelUIState.value = _modChannelUIState.value.copy(
                            modChannelResponseState = NetworkNewUserResponse.Auth401Failure(
                                Exception("Error! Re-login with Twitch")
                            ),
                            modRefreshing = false
                        )
                    }
                    is NetworkNewUserResponse.NewUser ->{

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
        Log.d("getLiveStreams","OAuthToken --> ${oAuthToken}")
        try {
            withContext(Dispatchers.IO + CoroutineName("GetLiveStreams")) {

                twitchRepoImpl.getFollowedLiveStreams(
                    authorizationToken = oAuthToken,
                    clientId = clientId,
                    userId = userId
                ).collect { response ->
                    when (response) {
                        is NetworkNewUserResponse.Loading -> {
                        }
                        is NetworkNewUserResponse.Success -> {

                            val replacedWidthHeightList = response.data.map {
                                it.changeUrlWidthHeight(
                                    _uiState.value.width,
                                    _uiState.value.aspectHeight
                                )
                            }
                            val horizontalLongHoldStreamList =response.data.map {
                                it.changeUrlWidthHeight(
                                    (_uiState.value.width)/2,
                                    (_uiState.value.aspectHeight)/2
                                )
                            }

                            _uiState.value = _uiState.value.copy(
                                streamersListLoading = NetworkNewUserResponse.Success(replacedWidthHeightList),
                                homeRefreshing = false,
                                horizontalLongHoldStreamList =NetworkNewUserResponse.Success(horizontalLongHoldStreamList)
                            )
                            _modChannelUIState.value = _modChannelUIState.value.copy(
                                modRefreshing = false,
                            )
                            getModeratedChannels(
                                oAuthToken = _oAuthToken.value ?: "",
                                clientId = _validatedUser.value?.clientId ?:"",
                                userId = _validatedUser.value?.userId ?:"",
                                liveFollowedStreamers = replacedWidthHeightList
                    )
                        }
                        // end
                        is NetworkNewUserResponse.Failure -> {
                            _uiState.value = _uiState.value.copy(
                                homeRefreshing = false,
                                streamersListLoading = response,
                                horizontalLongHoldStreamList =response
                            )
                            _modChannelUIState.value = _modChannelUIState.value.copy(
                                modRefreshing = false,
                            )
                        }
                        is NetworkNewUserResponse.NetworkFailure ->{
                            _uiState.value = _uiState.value.copy(
                                homeRefreshing = false,
                                showNetworkRefreshError = true
                            )

                            _modChannelUIState.value = _modChannelUIState.value.copy(
                                modRefreshing = false,
                            )
                            delay(1000)
                            _uiState.value = _uiState.value.copy(
                                showNetworkRefreshError = false
                            )


                        }
                        is NetworkNewUserResponse.Auth401Failure->{
                            _uiState.value = _uiState.value.copy(
                                streamersListLoading = response,
                                homeRefreshing = false,
                                horizontalLongHoldStreamList =response,
                            )
                            _modChannelUIState.value = _modChannelUIState.value.copy(
                                modChannelResponseState = NetworkNewUserResponse.Auth401Failure(Exception("Error! Re-login with Twitch")),
                                modRefreshing = false
                            )

                        }
                        is NetworkNewUserResponse.NewUser ->{

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
