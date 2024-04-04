package com.example.clicker.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
//import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.clicker.presentation.home.views.HomeViewImplementation
import com.example.clicker.presentation.modView.ModViewViewModel

import com.example.clicker.presentation.stream.AutoModViewModel
import com.example.clicker.presentation.stream.StreamViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ValidationView(
    homeViewModel: HomeViewModel,
    streamViewModel: StreamViewModel,
    loginWithTwitch: () -> Unit,
    onNavigate: (Int) -> Unit,
    addToLinks: () -> Unit,
    autoModViewModel: AutoModViewModel,
    modViewViewModel:ModViewViewModel
) {
    val bottomModalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val domainIsRegistered = homeViewModel.state.value.domainIsRegistered
    val scope = rememberCoroutineScope()


    val userIsAuthenticated = homeViewModel.validatedUser.collectAsState().value?.clientId != null
    val userId = homeViewModel.validatedUser.collectAsState().value?.userId
    val clientId = homeViewModel.validatedUser.collectAsState().value?.clientId
    val oAuthToken = homeViewModel.state.value.oAuthToken
    val isUserLoggedIn = homeViewModel.state.value.userIsLoggedIn
    val haptic = LocalHapticFeedback.current
    modViewViewModel.hapticFeedBack ={haptic.performHapticFeedback(HapticFeedbackType.LongPress)}



    HomeViewImplementation(
        bottomModalState =bottomModalState,
        loginWithTwitch ={loginWithTwitch()},
        domainIsRegistered =domainIsRegistered,
        addToLinks = { addToLinks() },
        onNavigate = {id -> onNavigate(id) },
        updateStreamerName = { streamerName, clientId,broadcasterId,userId->
            streamViewModel.updateChannelNameAndClientIdAndUserId(
                streamerName,
                clientId,
                broadcasterId,
                userId,
                login =homeViewModel.validatedUser.value?.login ?:""
            )
            autoModViewModel.updateAutoModCredentials(
                oAuthToken = homeViewModel.state.value.oAuthToken,
                clientId = streamViewModel.state.value.clientId,
                moderatorId = streamViewModel.state.value.userId,
                broadcasterId = streamViewModel.state.value.broadcasterId,
            )

        },
        updateClickedStreamInfo={clickedStreamInfo ->streamViewModel.updateClickedStreamInfo(clickedStreamInfo)  },
        followedStreamerList = homeViewModel.state.value.streamersListLoading,
        clientId = clientId ?: "",
        userId = userId ?: "",
        height = homeViewModel.state.value.aspectHeight,
        width = homeViewModel.state.value.width,
        logout = {
            homeViewModel.beginLogout(
                clientId = clientId?:"",
                oAuthToken = oAuthToken
            )
            //homeViewModel.logout()
            homeViewModel.hideLogoutDialog()

        },
        userIsAuthenticated =userIsAuthenticated,
        screenDensity = homeViewModel.state.value.screenDensity,
        homeRefreshing =homeViewModel.state.value.homeRefreshing,
        homeRefreshFunc = {homeViewModel.pullToRefreshGetLiveStreams()},
        networkMessageColor=Color.Red,
        networkMessage =homeViewModel.state.value.homeNetworkErrorMessage,
        showNetworkMessage = homeViewModel.state.value.networkConnectionState,
        logoutDialogIsOpen =homeViewModel.state.value.logoutDialogIsOpen,
        hideLogoutDialog ={homeViewModel.hideLogoutDialog()},
        showLogoutDialog ={homeViewModel.showLogoutDialog()},
        currentUsername = homeViewModel.validatedUser.collectAsState().value?.login ?: "Username not found",
        isUserLoggedIn=isUserLoggedIn,
        showFailedDialog = homeViewModel.state.value.showFailedDialog,
        hideDialog = {homeViewModel.hideDialog()}

    )


}

fun Modifier.disableClickAndRipple(): Modifier = composed {
    clickable(
        enabled = false,
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = { }
    )
}






