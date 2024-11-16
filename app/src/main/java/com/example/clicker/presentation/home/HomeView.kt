package com.example.clicker.presentation.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
//import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.clicker.R

import com.example.clicker.presentation.home.views.HomeViewImplementation
import com.example.clicker.presentation.authentication.logout.LogoutViewModel

import com.example.clicker.presentation.enhancedModView.viewModels.ModViewViewModel
import com.example.clicker.presentation.search.SearchViewModel


import com.example.clicker.presentation.stream.AutoModViewModel

import com.example.clicker.presentation.stream.StreamViewModel
import com.example.clicker.presentation.stream.views.chat.chatSettings.ChatSettingsViewModel
import com.example.clicker.presentation.streamInfo.StreamInfoViewModel
import com.example.clicker.util.Response




/**
 * - **ValidationView** is the external wrapper for all the compose code that will be shown inside of the [HomeFragment].
 * It will handle all the UI related interactions when the user is on the homepage. It also act as the main api between
 * ViewModels and the [HomeFragment] compose code
 *
 * @param homeViewModel a [HomeViewModel] object containing access to all the parameters and functions of the  [HomeViewModel]
 * @param streamViewModel a [StreamViewModel] object containing access to all the parameters and functions of the  [StreamViewModel]
 * @param streamInfoViewModel a [StreamInfoViewModel] object containing access to all the parameters and functions of the  [StreamInfoViewModel]
 * @param chatSettingsViewModel a [ChatSettingsViewModel] object containing access to all the parameters and functions of the  [ChatSettingsViewModel]
 * @param onNavigate a function, when called with an Integer, will navigate the user to the appropriate fragment
 * @param autoModViewModel a [AutoModViewModel] object containing access to all the parameters and functions of the  [AutoModViewModel]
 * @param updateModViewSettings a function, when called with an 4 String, will update information related to the current user
 * @param createNewTwitchEventWebSocket a function, when called, will create a websocket to connect to the desired Twitch chat
 * @param hapticFeedBackError a function, when called, will create a haptic feedback on the user's device. This function is
 * meant to be called when an error occurs
 * @param modViewViewModel a [ModViewViewModel] object containing access to all the parameters and functions of the  [ModViewViewModel]
 * @param searchViewModel a [SearchViewModel] object containing access to all the parameters and functions of the  [SearchViewModel]
 *
 * */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ValidationView(
    homeViewModel: HomeViewModel,
    streamViewModel: StreamViewModel,
    streamInfoViewModel: StreamInfoViewModel,
    chatSettingsViewModel:ChatSettingsViewModel,
    logoutViewModel: LogoutViewModel,
    onNavigate: (Int) -> Unit,
    autoModViewModel: AutoModViewModel,
    updateModViewSettings:(String,String,String,String,)->Unit,
    createNewTwitchEventWebSocket:()->Unit,
    hapticFeedBackError:() ->Unit,

    modViewViewModel: ModViewViewModel,
    searchViewModel: SearchViewModel,
) {
    val bottomModalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)



    val userIsAuthenticated = homeViewModel.validatedUser.collectAsState().value?.clientId != null
    val userId = homeViewModel.validatedUser.collectAsState().value?.userId
    val clientId = homeViewModel.validatedUser.collectAsState().value?.clientId
    val oAuthToken = homeViewModel.oAuthToken.collectAsState().value ?:""
    val lowPowerModeActive = streamViewModel.lowPowerModeActive.value
    val context = LocalContext.current
    var isBoxVisible by remember { mutableStateOf(false) }





    HomeViewImplementation(
        bottomModalState =bottomModalState,
        loginWithTwitch ={
            logoutViewModel.setLoggedOutStatus("TRUE")
            onNavigate(R.id.action_homeFragment_to_logoutFragment)
                         },
        onNavigate = {id -> onNavigate(id) },
        updateStreamerName = { streamerName, clientId, broadcasterId, userId ->

            if (!lowPowerModeActive) {
                homeViewModel.updateClickedStreamerName(streamerName)


                Log.d("LOWPOWERMODETESTING", "NON-ACTIVE")
                streamViewModel.updateChannelNameAndClientIdAndUserId(
                    streamerName,
                    clientId,
                    broadcasterId,
                    userId,
                    login = homeViewModel.validatedUser.value?.login ?: "",
                    oAuthToken= homeViewModel.oAuthToken.value ?:""
                )
                streamViewModel.getBetterTTVGlobalEmotes()
                autoModViewModel.updateAutoModCredentials(
                    oAuthToken = oAuthToken,
                    clientId = streamViewModel.state.value.clientId,
                    moderatorId = streamViewModel.state.value.userId,
                    broadcasterId = streamViewModel.state.value.broadcasterId,
                )
                updateModViewSettings(
                    oAuthToken,
                    streamViewModel.state.value.clientId,
                    streamViewModel.state.value.broadcasterId,
                    streamViewModel.state.value.userId,
                )
                createNewTwitchEventWebSocket()
                streamViewModel.getChannelEmotes(
                    oAuthToken,
                    streamViewModel.state.value.clientId,
                    streamViewModel.state.value.broadcasterId,
                )
                chatSettingsViewModel.getGlobalChatBadges(
                    oAuthToken = oAuthToken,
                    clientId = streamViewModel.state.value.clientId,
                )
                chatSettingsViewModel.getGlobalEmote(
                    oAuthToken = oAuthToken,
                    clientId = streamViewModel.state.value.clientId,
                )
                streamViewModel.getBetterTTVChannelEmotes(streamViewModel.state.value.broadcasterId)
                streamViewModel.clearAllChatters()
                streamInfoViewModel.getStreamInfo(
                    authorizationToken = oAuthToken,
                    clientId = streamViewModel.state.value.clientId,
                    broadcasterId = streamViewModel.state.value.broadcasterId,
                )
                //todo: this needs to be added to the
                modViewViewModel.getUnbanRequests(
                    oAuthToken =homeViewModel.oAuthToken.value ?:"",
                    clientId=clientId,
                    moderatorId=userId,
                    broadcasterId=broadcasterId
                )
            }
            streamViewModel.setImmersiveMode(false)

        },
        updateClickedStreamInfo={
            //todo: THIS IS WHAT I NEED TO UPDATE
                clickedStreamInfo ->streamViewModel.updateClickedStreamInfo(clickedStreamInfo)
                                },
        followedStreamerList = homeViewModel.state.value.streamersListLoading,
        clientId = clientId ?: "",
        userId = userId ?: "",
        height = homeViewModel.state.value.aspectHeight,
        width = homeViewModel.state.value.width,
        logout = {

            logoutViewModel.setNavigateHome(false)
//            logoutViewModel.setLoggedOutStatus("TRUE")
            logoutViewModel.logout(
                clientId = clientId?:"",
                oAuthToken = oAuthToken
            )
            homeViewModel.hideLogoutDialog()
            onNavigate(R.id.action_homeFragment_to_logoutFragment)

        },
        userIsAuthenticated =userIsAuthenticated,
        screenDensity = homeViewModel.state.value.screenDensity,
        homeRefreshing =homeViewModel.state.value.homeRefreshing,
        homeRefreshFunc = {homeViewModel.pullToRefreshHome()},
        networkMessageColor=Color.Red,
        networkMessage =homeViewModel.state.value.homeNetworkErrorMessage,
        showNetworkMessage = homeViewModel.state.value.networkConnectionState,
        logoutDialogIsOpen =homeViewModel.state.value.logoutDialogIsOpen,
        hideLogoutDialog ={homeViewModel.hideLogoutDialog()},
        showLogoutDialog ={homeViewModel.showLogoutDialog()},
        currentUsername = homeViewModel.validatedUser.collectAsState().value?.login ?: "Username not found",
        showNetworkRefreshError = homeViewModel.state.value.showNetworkRefreshError,
        hapticFeedBackError={hapticFeedBackError()},
        lowPowerModeActive=lowPowerModeActive,
        changeLowPowerMode={newValue ->streamViewModel.changeLowPowerModeActive(newValue)},
        getTopGames = {
            when(searchViewModel.topGames.value){

                is Response.Success ->{}
                else->{
                    searchViewModel.getTopGames(
                        oAuthToken = oAuthToken,
                        clientId = clientId?:""
                    )
                }
            }
        },
        getPinnedList={},
        permissionCheck={

        }

    )

}/******END OF THE VALIDATION VIEW********/


/**
 * - **disableClickAndRipple** is a custom modifier that makes a composable function un-clickable
 *
 * */
fun Modifier.disableClickAndRipple(): Modifier = composed {
    clickable(
        enabled = false,
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = { }
    )
}









