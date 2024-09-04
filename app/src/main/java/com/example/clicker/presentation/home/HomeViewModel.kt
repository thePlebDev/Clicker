package com.example.clicker.presentation.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clicker.domain.TwitchDataStore
import com.example.clicker.network.models.twitchClient.GetModChannelsData
import com.example.clicker.network.domain.TwitchAuthentication
import com.example.clicker.network.domain.TwitchRepo
import com.example.clicker.network.models.twitchAuthentication.ValidatedUser
import com.example.clicker.network.models.twitchRepo.StreamData
import com.example.clicker.network.models.twitchRepo.changeUrlWidthHeight
import com.example.clicker.presentation.home.models.HomeUIState
import com.example.clicker.presentation.home.models.ModChannelUIState
import com.example.clicker.presentation.home.models.UserTypes
import com.example.clicker.presentation.home.util.createOfflineAndOnlineLists
import com.example.clicker.util.NetworkAuthResponse
import com.example.clicker.util.NetworkNewUserResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext



@HiltViewModel
class HomeViewModel @Inject constructor(
    private val twitchRepoImpl: TwitchRepo,
    private val ioDispatcher: CoroutineDispatcher,
    private val tokenDataStore: TwitchDataStore,
    private val authentication: TwitchAuthentication,
) : ViewModel() {

    private var _uiState: MutableState<HomeUIState> = mutableStateOf(HomeUIState())
    val state: State<HomeUIState> = _uiState

    private var _modChannelUIState: MutableState<ModChannelUIState> = mutableStateOf(ModChannelUIState())
    val modChannelUIState: State<ModChannelUIState> = _modChannelUIState

    /**
     * _validatedUser private mutable version of [validatedUser]
     * */
    private val _validatedUser = MutableStateFlow<ValidatedUser?>(null)
    /**
     * - validatedUser is a [MutableStateFlow] containing a nullable [ValidatedUser] object. The non-null value represents a
     * logged in user
     * */
    val validatedUser = _validatedUser

    private val _oAuthToken = MutableStateFlow<String?>(null)
    val oAuthToken:StateFlow<String?> =  _oAuthToken.asStateFlow()

    private var _clickedStreamerName: MutableState<String> = mutableStateOf("")
    val clickedStreamerName: State<String> = _clickedStreamerName

    fun updateClickedStreamerName(clickedUsername:String){
        _clickedStreamerName.value = clickedUsername
    }



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


    /**Initial state monitoring
     * - These functions will monitor Hot StateFlows upon the creation of the HomeViewModel
     * */
    init{
        monitorForOAuthToken()
    }
    init {
        monitorForValidatedUser()
    }

    /**
     * - tries to retrieve a locally stored OAuth Token
     * */
    init{
        getOAuthToken()
    }




    /**
     * - pullToRefreshModChannels() is a function that gets called when the user pulls down to refresh the mod channels page
     * - if the [validatedUser] is not null, then it will call [getLiveStreams] to refresh the page
     * */
    fun pullToRefreshModChannels(){
        viewModelScope.launch(ioDispatcher) {
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


    /**
     * - pullToRefreshHome() is called when the user pulls down to refresh the home page.
     * It will first check if their is a non-null [validatedUser] object. If there is a non-null value, then
     * [getLiveStreams] will be called
     * */
     fun pullToRefreshHome(){
        viewModelScope.launch(ioDispatcher) {
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
            }

        }
    }


    /**
     * getModeratedChannels() is used to make a request to the Twitch servers and get a response of all the channels a user moderates
     * for.
     *
     * @param oAuthToken representing the users logged in session
     * @param clientId representing this app
     * @param userId represents the logged in user
     * @param liveFollowedStreamers represents all the live streams
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
                            val parsedData =createOfflineAndOnlineLists(responseData,liveFollowedStreamers)

                            _modChannelUIState.value = _modChannelUIState.value.copy(
                                offlineModChannelList = parsedData.offlineModList,
                                liveModChannelList = parsedData.onlineList,
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
        viewModelScope.launch(ioDispatcher) {
            _validatedUser.collect{nullableValidatedUser ->
                nullableValidatedUser?.also{nonNullValidatedUser ->
                    Log.d("nullableValidatedUser","RUNNING")
                    getLiveStreams(
                        clientId = nonNullValidatedUser.clientId,
                        userId = nonNullValidatedUser.userId,
                        oAuthToken = _oAuthToken.value ?:""
                    )
                   // getGlobalEmote(_uiState.value.oAuthToken,nonNullValidatedUser.clientId)
                }
            }
        }
    }

    /**
     * monitorForOAuthToken is a private function that upon the initialization of this viewModel is meant to monitor the [_oAuthToken] hot flow for any non null values. Once
     * a new non null value is emitted to [_oAuthToken], [validateOAuthToken] will be called
     * */
    private fun monitorForOAuthToken(){
        viewModelScope.launch(ioDispatcher) {
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
    private fun getOAuthToken() = viewModelScope.launch(ioDispatcher) {
        tokenDataStore.getOAuthToken().collect { storedOAuthToken ->
            if (storedOAuthToken.length > 2) {

                _oAuthToken.tryEmit(storedOAuthToken)
            }
        }
    }

    //todo: this needs to get moved




    /**
     * setOAuthToken is a function called to set the locally stored authentication token
     *
     * @param oAuthToken a string representing the authentication token that is to be stored locally
     */
    fun setOAuthToken(oAuthToken: String) = viewModelScope.launch(ioDispatcher) {
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
    fun determineUserType(): UserTypes = runBlocking {
        val token = tokenDataStore.getOAuthToken().first()
        val loggedOut = tokenDataStore.getLoggedOutStatus().first()
        Log.d("UserTypes ", "Token --> $token")
        Log.d("UserTypes ", "loggedOut --> $loggedOut")
        when {
            token.length < 2 -> UserTypes.NEW
            loggedOut=="TRUE" -> UserTypes.LOGGEDOUT
            loggedOut=="WAITING" -> UserTypes.LOGGEDOUT
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
                            userIsLoggedIn = NetworkAuthResponse.Success(true)
                        )


                        //todo: set the logout and login idea

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

                }
            }
        }
    } // end validateOAuthToken

    // THIS IS THE END


    /**
     * - getLiveStreams() is used to make a request to the Twitch servers and get a response of all the live channel a user follows
     * for.
     * - You can read the full documentation on getting live streams,[HERE](https://dev.twitch.tv/docs/api/reference/#get-streams)
     *
     * @param oAuthToken representing the users logged in session
     * @param clientId representing this app
     * @param userId represents the logged in user
     * */

    private suspend fun getLiveStreams(
        clientId: String,
        userId: String,
        oAuthToken: String
    ) {

        Log.d("getLiveStreams","OAuthToken --> ${oAuthToken}")
        try {
            withContext(ioDispatcher + CoroutineName("GetLiveStreams")) {

                twitchRepoImpl.getFollowedLiveStreams(
                    authorizationToken = oAuthToken,
                    clientId = clientId,
                    userId = userId
                ).collect { response ->

                    println("RESPONSE --> $response")
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
                            println("getLiveStreams() FAILED ")
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

/**************************MODELS BELOW**************************/
